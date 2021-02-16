from bluetooth import *
from config import * 

"""
Connection between RPI and Android via rfcomm
"""

class AndroidBluetoothServer:
    def __init__(self):
        self.server_sock = None
        self.client_sock = None
        self.connected = False

    def start_connection(self):
        try:
            self.server_sock = BluetoothSocket(RFCOMM)
            #port must indicate what port RPI is in.
            self.server_sock.bind(("",RFCOMM_CHNL))
            self.server_sock.listen(3)
            port = self.server_sock.getsockname()[1]

            advertise_service( self.server_sock, "RPI Bluetooth Server",
             service_id = UUID,
             service_classes = [ UUID, SERIAL_PORT_CLASS ],
             profiles = [ SERIAL_PORT_PROFILE ],
              )

            print("Waiting for connection on RFCOMM channel %d" % port)
            self.client_sock, client_info = self.server_sock.accept()
            print("[NEW CONNECTION] Bluetooth connection with Android connected to ", client_info)
            self.connected = True

        except Exception as error:
            print("[ERROR] Connection to Andorid failed: " + str(error))
            raise error

    def close_connection(self):
        if self.client_sock:
            self.client_sock.close()
            print("[CONNECTION CLOSE] Bluetooth on Tablet close")

        if self.server_sock:
            self.server_sock.close()
            print("[CONNECTION CLOSE] Bluetooth connection on RPI close")

        self.connected = False

    def bluetooth_is_connect(self):
        return self.connected

    def read_from_client(self):
        try:
            while self.connected:
                print("Reading message from android: ")
                msg = self.client_sock.recv(ANDROID_BUFFER_SIZE).decode(FORMAT)
                print(f"[ANDROID] {msg}")
        except Exception as error:
             print("[ERROR] Message from Andorid fail to print: " + str(error))
             raise error
             #reconnect bluetooth
             #self.close_connection()
             #self.start_connection()

    def send_to_client(self):
        try:
            while self.connected:
                print("To Android: ")
                msg = input()
                self.client_sock.send(msg)

        except Exception as error:
            print("[ERROR] Message from RPI to Android fail to send: " + str(error))
            raise error
            #reconnect bluetooth
            #self.close_connection()
            #self.start_connection()

if __name__ == '__main__':
    test = Android()
    test.start_connection()
    
    # test.close_connection()
