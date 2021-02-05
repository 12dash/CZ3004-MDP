from Algorithm import *
#from Arduino import *

import threading

class MainThread(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)

        #initialise the class
        self.algo = Algorithm()
        #self.arduino = Arduino()
    
    #init calls run() when thread starts
    def run(self):
        print("[STARTING ALL CONNECTIONS]")
        #initialise the connections
        self.algo.start_connection()    
        #self.arduino.start_connection()

"""
Here is the initialise of threads
"""


if __name__ == "__main__":
    #create thread
    mainThread = MainThread()
    #this is temporary till proper threading is done
    mainThread.start()
    #waiting the thread to finish executing b4 terminating the program
    mainThread.join()
    print("Done main thread")

