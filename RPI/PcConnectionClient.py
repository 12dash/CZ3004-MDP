import socket
import threading
from PcConnectionServer import SERVER_IP, PORT, HEADER, FORMAT, DISCONNECT_MESSAGE

class PcConnectionClient:
  def __init__(self):
    self.client = None
    self.server_ip = SERVER_IP
    # self.server_ip = "192.168.8.8"
    self.connected = False
    print("Client initialized")

  
  def start_connection(self):
    self.client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self.client.connect((self.server_ip, PORT))
    self.connected = True
    print("Client connected")
  
  def send_to_server(self):
    while self.connected:
      msg = input("Send message:")
      msg = msg.encode(FORMAT)
      self.client.send(msg)

      if msg == DISCONNECT_MESSAGE:
        self.stop_connection()

  def read_from_server(self):
    while self.connected:
      msg = self.client.recv(HEADER).decode(FORMAT)
      print(f"[SERVER] {msg}")
      if msg == DISCONNECT_MESSAGE:
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
  