import socket
import threading
import json
import queue
import numpy as np
from config import *
from struct import unpack
import os

#from predict import makePrediction
from detect import detect

from PIL import Image
from ipython_genutils.py3compat import xrange

img_info = {}

prev = False

obs = []
for i in range(20):
  for j in range(15):
    obs.append([])

img_grid = []

class PcConnectionClient:
  def __init__(self):
    self.client = None
    self.server_ip = SERVER_IP
    self.connected = False
    self.processing_queue = queue.Queue()
    print("Client initialized")

  def start_connection(self):
    self.client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self.client.connect((self.server_ip, 8081))
    self.connected = True
    print("Client connected")
  
  def send_to_server_ir_data(self, msg):
    msg = msg.encode(FORMAT)
    self.client.send(msg)

  def get_nearby_images_count(self):
    a = 0
    for i in img_info:
      # If nearby, consider this as sucessful
      if(img_info[i][-1] == "True"):
        a+=1
    return a
  
  def output_nearby_imgs(self):
  # When NEARBY images count >=5, display images and send to PC done.
    # Display images
    print("Output all Nearby images and send to android results")
    img_path = os.listdir("./output")
    new_im = Image.new('RGB', (256*5,256))
    x_offset = 0
    coords_ids_lst = []
    for i in img_info:
      if img_info[i][-1] == "True":
        img_temp = Image.open(f"output/{img_info[i][1]}")    
        new_im.paste(img_temp,(x_offset,0))
        x_offset += img_temp.size[0]

        # Collate all results into an array of arrays (x,y,id)
        img_coords = img_info[i][0]
        coords_ids_lst.append([img_coords[0], img_coords[1], i])

        # Break when image count == 5
        if len(coords_ids_lst) == 5:
          break

    new_im.save("final.jpg")

    # Send to Android image results
    json_outgoing = json.dumps({"image": coords_ids_lst})
    self.send_to_server_ir_data(f"an|{json_outgoing}") 
    # Finally tell Algo we are done
    self.send_to_server_ir_data("pc|done")
  
  def output_nearby_and_far_images(self):
  # When NEARBY images count >=5, display images and send to PC done.
    # Display images
    print("Output Nearby and Far images and send to android results")
    img_path = os.listdir("./output")
    new_im = Image.new('RGB', (256*5,256))
    x_offset = 0
    coords_ids_lst = []
    for i in img_info:
      # Get all the nearby first
      if img_info[i][-1] == "True":
        img_temp = Image.open(f"output/{img_info[i][1]}")    
        new_im.paste(img_temp,(x_offset,0))
        x_offset += img_temp.size[0]

        # Collate all results into an array of arrays (x,y,id)
        img_coords = img_info[i][0]
        coords_ids_lst.append([img_coords[0], img_coords[1], i])

        # Break when image count == 5
        if len(coords_ids_lst) == 5:
          break
    
    # Finally, get the remaining Far ones (TODO: Currently randomly picking Far ones)
    for i in img_info:
      # Get all the nearby first
      if img_info[i][-1] != "True":
        img_temp = Image.open(f"output/{img_info[i][1]}")    
        new_im.paste(img_temp,(x_offset,0))
        x_offset += img_temp.size[0]

        # Collate all results into an array of arrays (x,y,id)
        img_coords = img_info[i][0]
        coords_ids_lst.append([img_coords[0], img_coords[1], i])

        # Break when image count == 5
        if len(coords_ids_lst) == 5:
          break

    new_im.save("final.jpg")

    # Send to Android image results
    json_outgoing = json.dumps({"image": coords_ids_lst})
    self.send_to_server_ir_data(f"an|{json_outgoing}") 
    # Finally tell Algo we are done
    self.send_to_server_ir_data("pc|done")

  def process_image(self, processing_queue):
    while self.connected:
      if not processing_queue.empty():
        obj = processing_queue.get_nowait()        

        arr = np.asarray(obj["imageArr"]).astype(np.uint8) #convert to numpy arr        
        coords = obj["coords"]
        nearby = obj["nearby"]

        img = Image.fromarray(arr)
        path = f"{obj['coords']}.jpg"
        img.save(f"raw/{path}")

        # Do processing here
        predicted_img = detect(path)

        # If no bounding box
        if predicted_img == [-1]:
          print("Removed file, no image detected")
          #os.remove(f"output/{path}")
        
        # Else if image found successfully
        else:
          for i in predicted_img:
            # If image has not been detected previously, store it
            if (i not in img_info.keys()):
              print(f"New image seen id={i} coords={coords}")
              img_info[i] = [coords, path, nearby]
            # If image has been deteted previously and NEARBY, update this.
            else:
              if(nearby == "True"):
                print(f"Old image seen, update img_info id={i} coords={coords}")
                img_info[i] = [coords, path, nearby]
        print(img_info)

        # Output all nearby images
        if self.get_nearby_images_count() >= 5:
          self.output_nearby_imgs() 
          return 

  # Modified version of reading -- reading image from RPI
  def read_from_server(self):
    try:
      while self.connected:
        print("Listening for images captured: ")

        bs = self.client.recv(15)

        # When exploration is done or 6 mins is up
        if bs.decode(FORMAT) == "complete":
          self.output_nearby_and_far_images()
          return
        else:
          (length,) = unpack('>Q', bs)
          msg = b''

          while len(msg) < length:
            # doing it in batches
            to_read = length - len(msg)
            msg += self.client.recv(PC_BUFFER_SIZE if to_read > PC_BUFFER_SIZE else to_read)

          if msg:
            # print(f"[IMAGE] {msg}")
            print(f"Image Received -- Length of image received: {len(msg)}")
            json_incoming = json.loads(msg)
          
          # Disconnect message
          if msg == DISCONNECT_MESSAGE or len(msg) == 0:
            self.stop_connection()
          
          #Add these to processing queue so that it can continuously receive new images
          self.processing_queue.put_nowait(json_incoming)
        
    except Exception:
      self.stop_connection()
      print(f"[CONNECTION CLOSE] IR at {self.server_ip}")
          
  def stop_connection(self):
    print(f"[CONNECTION CLOSE] IR at {self.server_ip}")
    self.client.close()
    self.connected = False
    self.client = None
  
  def start_multi_threads(self):
    #create a thread for read and processing each
    ir_read_thread = threading.Thread(target = self.read_from_server, args = () )
    ir_process_thread = threading.Thread(target = self.process_image, args = (self.processing_queue,) )

    ir_read_thread.start()
    ir_process_thread.start()


if __name__ == '__main__':
  client = PcConnectionClient()
  client.start_connection()

  client.start_multi_threads()

  # Sending to server
  # while client.connected:
  #   msg = input("Send message:")
  #   client.send_to_server(msg)
  #   if msg == DISCONNECT_MESSAGE:
  #     client.connected = False


  # Reading from server
  # client.read_from_server()
  