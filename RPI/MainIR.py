import threading
import queue
import json
from config import *
from PcConnectionServer import PcConnectionServer
from AndroidBluetoothServer import AndroidBluetoothServer
from ArduinoConnectionServer import ArduinoConnectionServer
from RPICamera import RPICamera

"""
Driver file to be run for Image Recognition Runs
"""

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

    #IR PC ( Create another instance of socket connection w PC for image recognition processing )
    self.ir_pc_connection = PcConnectionServer(8081) # use another port for IR
    self.ir_pc_connection.start_connection()
    
    #RPI Camera
    self.rpi_camera = RPICamera()
    

    # init queues
    self.pc_queue = queue.Queue()
    self.android_queue = queue.Queue()
    self.arduino_queue = queue.Queue()
    self.ir_pc_queue = queue.Queue()
  
  def send_to_pc(self, pc_queue):
    while self.pc_connection.connected:
      if not pc_queue.empty():
        msg = pc_queue.get_nowait()
        self.pc_connection.send_to_client(msg)

  def read_from_pc(self, android_queue, arduino_queue):
    while self.pc_connection.connected:
      msg = self.pc_connection.read_from_client()
      msg_lst = msg.split("|")
      header = msg_lst[0]

      if header == "an":
        android_queue.put_nowait(msg_lst[1])
      elif header == "ar":
        arduino_queue.put_nowait(msg_lst[1])
      elif header == "ir":
        json_incoming = json.loads(msg_lst[1])

        if "coords" in json_incoming:
          coords = json_incoming["coords"]

          # RPI to take picture on command from ALGO PC
          image_arr = self.rpi_camera.capture_image()

          print("Sending image and coords to IR PC")
          # after picture is taken, send to IR PC -> Image Array and Coords
          json_msg = {"imageArr" : image_arr, "coords": coords }
          self.ir_pc_queue.put_nowait(json.dumps(json_msg)) 
          print("Sent image and coords to IR PC")
      else:
        print("Invalid recipient from PC")

  def send_to_ir_pc(self, ir_pc_queue):
    # After image is captured, send to IR PC for IR processing
    while self.ir_pc_connection.connected:
      if not ir_pc_queue.empty():
        msg = ir_pc_queue.get_nowait()
        self.ir_pc_connection.send_to_client(msg)
    
  def read_from_ir_pc(self, android_queue):
    # Receive predicted Image ID and Coords and send to android
    while self.ir_pc_connection.connected:
      msg = self.ir_pc_connection.read_from_client()
      msg_lst = msg.split("|")
      header = msg_lst[0]

      if header == "an":
        android_queue.put_nowait(msg_lst[1])
      else:
        print("Invalid recipient from Android")
    pass

  def send_to_android(self, android_queue):
    while self.android_connection.connected:
      if not android_queue.empty():
        msg = android_queue.get_nowait()
        self.android_connection.send_to_client(msg)

  def read_from_android(self, pc_queue, arduino_queue):
    while self.android_connection.connected:
      msg = self.android_connection.read_from_client()
      msg_lst = msg.split("|")
      header = msg_lst[0]

      if header == "pc":
        pc_queue.put_nowait(msg_lst[1])
      elif header == "ar":
        arduino_queue.put_nowait(msg_lst[1])
      else:
        print("Invalid recipient from Android")

  def send_to_arduino(self, arduino_queue):
    while self.arduino_connection.connected:
      if not arduino_queue.empty():
        msg = arduino_queue.get_nowait()
        self.arduino_connection.send_to_client(msg)

  def read_from_arduino(self, pc_queue, android_queue):
    while self.arduino_connection.connected:
      msg = self.arduino_connection.read_from_client()
      msg_lst = msg.split("|")
      header = msg_lst[0]

      if header == "pc":
        pc_queue.put_nowait(msg_lst[1])
      elif header == "an":
        android_queue.put_nowait(msg_lst[1])
      else:
        print("Invalid recipient from Arduino")
    

  
  def start_multi_threads(self):
    # PC Write and Read Multi-threading
    pc_read_thread = threading.Thread(target = self.read_from_pc, args = (self.android_queue, self.arduino_queue) )
    pc_write_thread = threading.Thread(target = self.send_to_pc, args = (self.pc_queue,) )

     # ANDROID Write and Read Multi-threading
    android_read_thread = threading.Thread(target = self.read_from_android, args = (self.pc_queue, self.arduino_queue) )
    android_write_thread = threading.Thread(target = self.send_to_android, args = (self.android_queue,) )

    # ARDUINO Write and Read Multi-therading
    arduino_read_thread = threading.Thread(target = self.read_from_arduino, args=(self.pc_queue, self.android_queue) )
    arduino_write_thread = threading.Thread(target = self.send_to_arduino, args=(self.arduino_queue,) )

    # IR PC Write and Read Multi-therading
    ir_pc_read_thread = threading.Thread(target = self.read_from_ir_pc, args=(self.android_queue, ) )
    ir_pc_write_thread = threading.Thread(target = self.send_to_ir_pc, args=(self.ir_pc_queue,) )

    # Start threads
    pc_read_thread.start()
    pc_write_thread.start()

    android_read_thread.start()
    android_write_thread.start()

    arduino_read_thread.start()
    arduino_write_thread.start()

    ir_pc_read_thread.start()
    ir_pc_write_thread.start()



if __name__ == "__main__":
  print("Start main program")
  main = Main()

  main.start_multi_threads()
