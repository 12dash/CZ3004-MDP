import serial

"""
Connection between RPI and Arduino via USB
"""

class Arduino:
    #serial port name can be changed
    SERIAL_PORT = '/dev/ttyACM0'
    #Baud Rate 57600/ 115200 for no future problems
    BAUD = 9600
    FORMAT = 'UTF-8'

    def __init__(self, serial_port = SERIAL_PORT, baud_rate = BAUD):
        self.serial_port = serial_port
        self.baud_rate = baud_rate
        self.ser = None

    def start_connection(self):
        try:
            self.ser = serial.Serial(self.serial_port, self.baud_rate, timeout = 1)
            print("[NEW CONNECTION] Serial link with Arduino connected")

        except Exception as error:
            print("[ERROR] Connection to arduino failed. Please retry")

    def stop_connection(self):
        if self.ser:
            print("[CONNECTION CLOSE] Arduino close")
            self.ser.close()

    def read_from_arduino(self):
        try:
            msg = self.ser.readline().decode(FORMAT).rstrip()
            print(f"[ARDUINO] {msg}")

        except Exception as error:
            print("[ERROR] Message from Arduino fail to print")

    def write_to_arduino(self, msg):
         try:
             print("To Arduino: ")
             print(msg)
             self.ser.write(msg).encode(FORMAT)

         except Exception as error:
            print("[ERROR] Message can't be send to Arduino")
            print("Error message (Arduino): " + str(error))
            raise error
"""
This is testing connection between arduino and rpi to check the coding works.
Remember to comment block the code once testing is successful 
"""
"""
This is testing connection between arduino and rpi to check the coding works.
Remember to comment block the code once testing is successful 
"""
if __name__ == '__main__':
    ser = Arduino()
    ser.start_connection()
#this flush function will flush any input output buffer. 
#to avoid receiving or sending weird or incomplete data at the start of the communication
    ser.flush()

    while true:
        msg = "Hi Arduino from RPI"
        ser.write_to_arduino(msg)
        if ser.in_waiting > 0:
            ser.read_from_arduino()

    ser.stop_connection()
