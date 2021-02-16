import threading
from config import *
from PcConnectionServer import PcConnectionServer
from AndroidBluetoothServer import AndroidBluetoothServer


class Main(threading.Thread):
  def __init__(self):
    threading.Thread.__init__(self)

    ## init connections
    #PC
    self.pc_connection = PcConnectionServer()
    self.pc_connection.start_connection()

    #ANDROID
    self.android_connection = AndroidBluetoothServer()
    self.android_connection.start_connection()

  
  def send_to_pc(self, msg):
    self.pc_connection.send_to_client(msg)

  def read_from_pc(self):
    while self.pc_connection.connected:
            msg = self.pc_connection.read_from_client()

            if msg != DISCONNECT_MESSAGE:
              header, body = msg.split("|")
              
              if header == "an":
                self.send_to_android(body)
              elif header == "ar":
                self.send_to_arduino(body)

            else:
              self.pc_connection.stop_connection()

  def send_to_android(self, msg):
    self.android_connection.send_to_client(msg)

  def read_from_android(self):
    while self.android_connection.connected:
      msg = self.android_connection.read_from_client()

      if msg != DISCONNECT_MESSAGE:
        header, body = msg.split("|")

        if header == "pc":
          self.send_to_pc(body)
        elif header == "an":
          self.send_to_arduino(body)
      
      else:
        self.android_connection.stop_connection()
        
  def send_to_arduino(self, msg):
    pass

  def read_from_arduino(self):
    pass

    

  
  def start_multi_threads(self):
    # PC Write and Read Multi-threading
    pc_read_thread = threading.Thread(target = self.read_from_pc, args = () )
    pc_write_thread = threading.Thread(target = self.send_to_pc, args = () )

     # ANDROID Write and Read Multi-threading
    android_read_thread = threading.Thread(target = self.read_from_android, args = () )
    android_write_thread = threading.Thread(target = self.send_to_android, args = () )

    # Start threads
    pc_read_thread.start()
    pc_write_thread.start()

    android_read_thread.start()
    android_write_thread.start()


if __name__ == "__main__":
  print("Start main program")
  main = Main()

  main.start_multi_threads()
  
