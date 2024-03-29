import socket
import threading
import json
import queue
import numpy as np
from config import *
from struct import unpack
from predict import makePrediction

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
    while self.connected:
      if not processing_queue.empty():
        obj = processing_queue.get_nowait()

        arr = np.asarray(obj["imageArr"]) #convert to numpy arr
        coords = obj["coords"]

        print(arr.shape)
        
        # Do processing here
        # Sabrina: Predict Image ID here
        print("Predicted Image IDs Here")
        predicted_img = makePrediction(arr)

        #TODO: Save the raw image captured with bounding box here -- need to display as output as end of run

        #Then send to android
        json_outgoing = json.dumps({"image": [coords[0], coords[1], predicted_img]})
        self.send_to_server_ir_data(f"an|{json_outgoing}")

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
  