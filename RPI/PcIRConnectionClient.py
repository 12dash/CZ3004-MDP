import socket
import threading
import json
import queue
from config import *

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

        arr = obj["imageArr"]
        coords = obj["coords"]
        print("Input Arr: " + arr)
        print("Coords: " + coords)
        
        # Do processing here
        # Sabrina: Predict Image ID here
        print("Predicted Image IDs Here")
        predicted_img = "Some Image ID to be predicted here"

        #TODO: Save the raw image captured with bounding box here -- need to display as output as end of run

        #Then send to android
        json_outgoing = json.dumps({"image": [coords[0], coords[1], predicted_img]})
        self.send_to_server_ir_data(f"an|{json_outgoing}")

  def read_from_server(self):
    while self.connected:
      print("Listening for images captured: ")
      msg = self.client.recv(PC_BUFFER_SIZE).decode(FORMAT)
      print(f"[IMAGE] {msg}")

      json_incoming = json.loads(msg)

      #Add these to processing queue so that it can continuously receive new images
      self.processing_queue.put_nowait(json_incoming)
      
      if msg == DISCONNECT_MESSAGE or len(msg) == 0:
        print(len(msg))
        self.stop_connection()
          
  def stop_connection(self):
    print(f"[CONNECTION CLOSE] Algorithm at {self.server_ip}")
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
  