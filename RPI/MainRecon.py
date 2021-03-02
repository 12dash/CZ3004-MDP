import threading
import queue
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

    # init queues
    self.pc_queue = queue.Queue()
    self.android_queue = queue.Queue()
    self.arduino_queue = queue.Queue()
  
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
      else:
        print("Invalid recipient from PC")

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

  def start_multi_threads(self):
    self.start_pc_threads()
    self.start_android_threads()
    self.start_arduino_threads()
    

if __name__ == "__main__":  
  print("Start main program")
  main = Main()
  main.start_multi_threads()
  print(f"Running threads: {threading.active_count()}")

  # Listen for disconnect and reconnect when that occurs
  while True:
    if not main.pc_read_thread.is_alive() or not main.pc_write_thread.is_alive():
      print(f"Running threads: {threading.active_count()}")
      print("Please reconnect PC")
      main.pc_connection = PcConnectionServer()
      main.pc_connection.start_connection()
      main.pc_queue = queue.Queue()

      main.start_pc_threads()
      print(f"Running threads: {threading.active_count()}")
    
    if not main.android_read_thread.is_alive() or not main.android_write_thread.is_alive():
      print(f"Running threads: {threading.active_count()}")
      print("Please reconnect Android")
      main.android_connection = AndroidBluetoothServer()
      main.android_connection.start_connection()
      main.android_queue = queue.Queue()

      main.start_android_threads()
      print(f"Running threads: {threading.active_count()}")

    if not main.arduino_read_thread.is_alive() or not main.arduino_write_thread.is_alive():
      print(f"Running threads: {threading.active_count()}")
      print("Please reconnect Arduino")
      main.arduino_connection = ArduinoConnectionServer()
      main.arduino_connection.start_connection()
      main.arduino_queue = queue.Queue()

      main.start_arduino_threads()
      print(f"Running threads: {threading.active_count()}")
  


