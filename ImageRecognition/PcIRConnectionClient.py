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
  

  def process_image(self, processing_queue):
    def get_number_img():
      a = 0
      for i in img_info:
        if(img_info[i][-1]):
          a+=1
      return a
    while self.connected:
      if not processing_queue.empty():
        obj = processing_queue.get_nowait()        

        arr = np.asarray(obj["imageArr"]).astype(np.uint8) #convert to numpy arr        
        coords = obj["coords"]
        nearby = True

        img = Image.fromarray(arr)
        path = f"{obj['coords']}.jpg"
        img.save(f"raw/{path}")

        # Do processing here
        # Sabrina: Predict Image ID here
        predicted_img = detect(path)

        if predicted_img == -1:
          print("Removed file")
          os.remove(f"output/{path}")
          #Then send to android

          json_outgoing = json.dumps({"image": [coords[0], coords[1], predicted_img[0]]})
          self.send_to_server_ir_data(f"an|{json_outgoing}")
        else:
          for i in predicted_img:
            if (i not in img_info.keys()):
              img_info[i] = [coords, path, nearby]     
            else:
              if(img_info[i][-1] == False):
                img_info[i] = [coords, path, nearby]

        if(get_number_img() >= 5):
          img_path = os.listdir("./output")
          new_im = Image.new('RGB', (256*5,256))
          x_offset = 0
          for i in img_info:
              img_temp = Image.open(f"output/{img_info[i][1]}")    
              new_im.paste(img_temp,(x_offset,0))
              x_offset += img_temp.size[0]
          new_im.save("final.jpg")
          self.send_to_server_ir_data("pc|done")
          print(img_info)
          return 

  # Modified version of reading -- reading image from RPI
  def read_from_server(self):
    try:
      while self.connected:
        print("Listening for images captured: ")

        bs = self.client.recv(15)
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
  