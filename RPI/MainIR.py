import threading
import queue
import json
import numpy as np
from config import *
from PcConnectionServer import PcConnectionServer
from AndroidBluetoothServer import AndroidBluetoothServer
from ArduinoConnectionServer import ArduinoConnectionServer
from PcIRConnectionServer import PcIRConnectionServer
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
    self.ir_pc_connection = PcIRConnectionServer(8081) # use another port for IR
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

          # after picture is taken, send to IR PC -> Image Array and Coords
          json_msg = {"imageArr" : image_arr, "coords": coords }
          self.ir_pc_queue.put_nowait(json.dumps(json_msg, cls=NumpyEncoder)) 
          print("Sent image and coords to IR PC")

          # after picture is taken, tell Algo to resume movement
          resume_msg = {"imageCaptured" : "true" }
          self.pc_queue.put_nowait(json.dumps(resume_msg))
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
    
  def start_pc_threads(self):
    # PC Write and Read Multi-threading
    self.pc_read_thread = threading.Thread(target = self.read_from_pc, args = (self.android_queue, self.arduino_queue) )
    self.pc_write_thread = threading.Thread(target = self.send_to_pc, args = (self.pc_queue,) )

    # Start threads
    self.pc_read_thread.start()
    self.pc_write_thread.start()

  def start_android_threads(self):
    # ANDROID Write and Read Multi-threading
    self.android_read_thread = threading.Thread(target = self.read_from_android, args = (self.pc_queue, self.arduino_queue) )
    self.android_write_thread = threading.Thread(target = self.send_to_android, args = (self.android_queue,) )

    # Start threads
    self.android_read_thread.start()
    self.android_write_thread.start()

  def start_arduino_threads(self):
    # ARDUINO Write and Read Multi-threadading
    self.arduino_read_thread = threading.Thread(target = self.read_from_arduino, args=(self.pc_queue, self.android_queue) )
    self.arduino_write_thread = threading.Thread(target = self.send_to_arduino, args=(self.arduino_queue,) )

    # Start threads
    self.arduino_read_thread.start()
    self.arduino_write_thread.start()

  def start_ir_pc_threads(self):
    # IR PC Write and Read Multi-therading
    self.ir_pc_read_thread = threading.Thread(target = self.read_from_ir_pc, args=(self.android_queue, ) )
    self.ir_pc_write_thread = threading.Thread(target = self.send_to_ir_pc, args=(self.ir_pc_queue,) )

    # Start threads
    self.ir_pc_read_thread.start()
    self.ir_pc_write_thread.start()

  def start_multi_threads(self):
    self.start_pc_threads()
    self.start_android_threads()
    self.start_arduino_threads()
    self.start_ir_pc_threads()



# For passing numpy arrays into json
class NumpyEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return json.JSONEncoder.default(self, obj)


if __name__ == "__main__":
  print("Start main program")
  main = Main()

  main.start_multi_threads()
  print(f"Running threads: {threading.active_count()}")

  # Listen for disconnect and reconnect when that occurs
  while True:
    if not main.pc_read_thread.is_alive() or not main.pc_write_thread.is_alive():
      print(f"Running threads: {threading.active_count()}")
      print("Please reconnect Algo PC")
      main.pc_connection = PcConnectionServer()
      main.pc_connection.start_connection()
      main.pc_queue.queue.clear()

      main.start_pc_threads()
      print(f"Running threads: {threading.active_count()}")

    if not main.android_read_thread.is_alive() or not main.android_write_thread.is_alive():
      print(f"Running threads: {threading.active_count()}")
      print("Please reconnect Android")
      main.android_connection = AndroidBluetoothServer()
      main.android_connection.start_connection()
      main.android_queue.queue.clear()

      main.start_android_threads()
      print(f"Running threads: {threading.active_count()}")

    if not main.arduino_read_thread.is_alive() or not main.arduino_write_thread.is_alive():
      print(f"Running threads: {threading.active_count()}")
      print("Please reconnect Arduino")
      main.arduino_connection = ArduinoConnectionServer()
      main.arduino_connection.start_connection()
      main.arduino_queue.queue.clear()

      main.start_arduino_threads()
      print(f"Running threads: {threading.active_count()}")

    if not main.ir_pc_read_thread.is_alive() or not main.ir_pc_write_thread.is_alive():
      print(f"Running threads: {threading.active_count()}")
      print("Please reconnect IR PC")
      main.ir_pc_connection = PcIRConnectionServer(8081)
      main.ir_pc_connection.start_connection()
      main.ir_pc_queue.queue.clear()

      main.start_ir_pc_threads()
      print(f"Running threads: {threading.active_count()}")


