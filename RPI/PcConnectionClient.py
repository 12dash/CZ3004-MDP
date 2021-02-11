import socket
from PcConnectionServer import SERVER_IP, PORT, HEADER, FORMAT, DISCONNECT_MESSAGE

class PcConnectionClient:
  def __init__(self):
    self.client = None
    # self.server_ip = SERVER_IP
    self.server_ip = "192.168.8.8"
    self.connected = False
    print("Client initialized")

  
  def start_connection(self):
    self.client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self.client.connect((self.server_ip, PORT))
    self.connected = True
    print("Client connected")
  
  def send_to_server(self, msg):
    msg = msg.encode(FORMAT)
    self.client.send(msg)

  def read_from_server(self):
    while self.connected:
      msg = self.client.recv(HEADER).decode(FORMAT)
      print(f"[SERVER] {msg}")
      if msg == DISCONNECT_MESSAGE:
          self.connected = False
      
    print(f"[CONNECTION CLOSE] Algorithm at {self.server_ip}")
    self.client.close()



if __name__ == '__main__':
  client = PcConnectionClient()
  client.start_connection()

  # Sending to server
  # while client.connected:
  #   msg = input("Send message:")
  #   client.send_to_server(msg)
  #   if msg == DISCONNECT_MESSAGE:
  #     client.connected = False


  # Reading from server
  client.read_from_server()
  