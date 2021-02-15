import threading
from PcConnectionServer import PcConnectionServer

class Main(threading.Thread):
  def __init__(self):
    threading.Thread.__init__(self)

    # init connections
    self.pc_connection = PcConnectionServer()
    self.pc_connection.start_connection()


  
  def start_multi_threads(self):
    # PC Write and Read Multi-threading
    pc_read_thread = threading.Thread(target = self.pc_connection.send_to_client, args = () )
    pc_write_thread = threading.Thread(target = self.pc_connection.read_from_client, args = () )

    # Start threads
    pc_read_thread.start()
    pc_write_thread.start()


if __name__ == "__main__":
  print("Start main program")
  main = Main()

  main.start_multi_threads()
  
