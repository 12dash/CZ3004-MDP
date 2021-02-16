import threading
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
    

  
  def start_multi_threads(self):
    # PC Write and Read Multi-threading
    pc_read_thread = threading.Thread(target = self.pc_connection.send_to_client, args = () )
    pc_write_thread = threading.Thread(target = self.pc_connection.read_from_client, args = () )

     # ANDROID Write and Read Multi-threading
    android_read_thread = threading.Thread(target = self.android_connection.send_to_client, args = () )
    android_write_thread = threading.Thread(target = self.android_connection.read_from_client, args = () )

    # Start threads
    pc_read_thread.start()
    pc_write_thread.start()

    android_read_thread.start()
    android_write_thread.start()


if __name__ == "__main__":
  print("Start main program")
  main = Main()

  main.start_multi_threads()
  
