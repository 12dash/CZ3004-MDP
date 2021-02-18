import threading
from config import *
from PcConnectionServer import PcConnectionServer
from AndroidBluetoothServer import AndroidBluetoothServer
from ArduinoConnectionServer import ArduinoConnectionServer


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

    #ARDUINO
    self.arduino_connection = ArduinoConnectionServer()
    self.arduino_connection.start_connection()

  
  def send_to_pc(self, msg):
    if msg:
    	self.pc_connection.send_to_client(msg)

  def read_from_pc(self):
    while self.pc_connection.connected:
      msg = self.pc_connection.read_from_client()
      msg_lst = msg.split("|")
      header = msg_lst[0]

      if header == "an":
        self.send_to_android(msg_lst[1])
      elif header == "ar":
        self.send_to_arduino(msg_lst[1])
      else:
        print("Invalid recipient from PC")

  def send_to_android(self, msg):
    if msg:
      self.android_connection.send_to_client(msg)

  def read_from_android(self):
    while self.android_connection.connected:
      msg = self.android_connection.read_from_client()
      msg_lst = msg.split("|")
      header = msg_lst[0]

      if header == "pc":
        self.send_to_pc(msg_lst[1])
      elif header == "ar":
        self.send_to_arduino(msg_lst[1])
      else:
        print("Invalid recipient from Android")

  def send_to_arduino(self, msg):
    if msg:
      self.arduino_connection.send_to_client(msg)

  def read_from_arduino(self):
    while self.arduino_connection.connected:
      msg = self.arduino_connection.read_from_client()
      msg_lst = msg.split("|")
      header = msg_lst[0]

      if header == "pc":
        self.send_to_pc(msg_lst[1])
      elif header == "an":
        self.send_to_android(msg_lst[1])
      else:
        print("Invalid recipient from Arduino")
    

  
  def start_multi_threads(self):
    # PC Write and Read Multi-threading
    pc_read_thread = threading.Thread(target = self.read_from_pc, args = () )
    pc_write_thread = threading.Thread(target = self.send_to_pc, args = ("",) )

     # ANDROID Write and Read Multi-threading
    android_read_thread = threading.Thread(target = self.read_from_android, args = () )
    android_write_thread = threading.Thread(target = self.send_to_android, args = ("",) )

    # ARDUINO Write and Read Multi-therading
    arduino_read_thread = threading.Thread(target = self.read_from_arduino, args=() )
    arduino_write_thread = threading.Thread(target = self.send_to_arduino, args=("",) )

    # Start threads
    pc_read_thread.start()
    pc_write_thread.start()

    android_read_thread.start()
    android_write_thread.start()

    arduino_read_thread.start()
    arduino_write_thread.start()



if __name__ == "__main__":
  print("Start main program")
  main = Main()

  main.start_multi_threads()
  
