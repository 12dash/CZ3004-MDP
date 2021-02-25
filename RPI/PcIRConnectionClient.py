import socket
import threading
import json
from config import *

class PcConnectionClient:
  def __init__(self):
    self.client = None
    self.server_ip = SERVER_IP
    self.connected = False
    print("Client initialized")

  
  def start_connection(self):
    self.client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self.client.connect((self.server_ip, 8081))
    self.connected = True
    print("Client connected")
  
  def send_to_server(self):
    while self.connected:
      msg = input("Send message:")
      msg = msg.encode(FORMAT)
      self.client.send(msg)

      if msg == DISCONNECT_MESSAGE:
        self.stop_connection()

  def send_to_server_ir_data(self, msg):
    msg = msg.encode(FORMAT)
    self.client.send(msg)

  def read_from_server(self):
    while self.connected:
      msg = self.client.recv(PC_BUFFER_SIZE).decode(FORMAT)
      print(f"[SERVER] {msg}")

      json_incoming = json.loads(msg)

      arr = json_incoming["imageArr"]
      coords = json_incoming["coords"]

      print("Input Arr: " + arr)
      print("Coords: " + coords)

      # Sabrina: Predict Image ID here
      print("Predicted Image IDs Here")
      predicted_img = "Some Image ID to be predicted here"

      #TODO: Save the raw image captured with bounding box here -- need to display as output as end of run

      #Then send to android
      json_outgoing = json.dumps({"image": [coords[0], coords[1], predicted_img]})
      self.send_to_server_ir_data(f"an|{json_outgoing}")
      
      if msg == DISCONNECT_MESSAGE or len(msg) == 0:
        print(len(msg))
        self.stop_connection()
          
  def stop_connection(self):
    print(f"[CONNECTION CLOSE] Algorithm at {self.server_ip}")
    self.client.close()
    self.connected = False
    self.client = None



if __name__ == '__main__':
  client = PcConnectionClient()
  client.start_connection()

  client_read_thread = threading.Thread(target = client.send_to_server, args = () )
  client_write_thread = threading.Thread(target = client.read_from_server, args = () )

  client_read_thread.start()
  client_write_thread.start()

  # Sending to server
  # while client.connected:
  #   msg = input("Send message:")
  #   client.send_to_server(msg)
  #   if msg == DISCONNECT_MESSAGE:
  #     client.connected = False


  # Reading from server
  # client.read_from_server()
  