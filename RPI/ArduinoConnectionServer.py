import serial
from config import *

"""
Connection between RPI and Arduino via USB
"""
class ArduinoConnectionServer:
    def __init__(self):
        self.serial_port = SERIAL_PORT
        self.baud_rate = BAUD
        self.ser = None
        self.connected = False

    def start_connection(self):
        try:
            self.ser = serial.Serial(self.serial_port, self.baud_rate)
            self.connected = True
            print("[NEW CONNECTION] Serial link with Arduino connected")

        except Exception as error:
            print("[ERROR] Connection to arduino failed. Please retry")

    def stop_connection(self):
        if self.ser:
            print("[CONNECTION CLOSE] Arduino close")
            self.ser.close()

    def read_from_client(self):
        try:
            print("Reading message from arduino: ")
            msg = self.ser.readline().decode(FORMAT).rstrip()
            print(f"[ARDUINO] {msg}")
            return msg

        except Exception as error:
            print("[ERROR] Message from Arduino fail to print: " + str(error))
            raise error
            #reconnect
            #self.stop_connection()
            #self.start_connection()

    def send_to_client(self, msg):
         try:
             self.ser.write(msg.encode(FORMAT))
             if msg == DISCONNECT_MESSAGE:
                self.stop_connection()

         except Exception as error:
            print("[ERROR] Message can't be send to Arduino: " + str(error))
            raise error
            #reconnect
            #self.stop_connection()
            #self.start_connection()

if __name__ == '__main__':
    ser = ArduinoConnectionServer()
    ser.start_connection()

    while True:
        msg = "Hi AR from RPI"
        ser.send_to_client(msg)
        ser.read_from_client()

    ser.stop_connection()
