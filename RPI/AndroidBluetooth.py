from bluetooth import *
"""
Connection between RPI and Android via rfcomm
"""

"""
Configuration for Andriod
"""
#generic uuid
UUID = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

class Android:
    def __init__(self):
        self.server_sock = None
        self.client_sock = None
        self.bluetooth_is_connected = False

    def start_connection(self):
        try:
            self.server_sock = BluetoothSocket(RFCOMM)
            #port must indicate what port RPI is in.
            self.server_sock.bind(("",PORT_ANY))
            self.server_sock.listen(3)
            port = server_sock.getsockname()[1]

            advertise_service( self.server_sock, "MDP-Server",
             service_id = UUID,
             service_classes = [ UUID, SERIAL_PORT_CLASS ],
             profiles = [ SERIAL_PORT_PROFILE ],
             # protocols = [ OBEX_UUID ]
             )

            print("Waiting for connection on RFCOMM channel %d" % port)
            self.client_sock, client_info = server_sock.accept()
            print("[NEW CONNECTION] Bluetooth connection with Android connected to ", client_info)
            self.bluetooth_is_connected = True

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

        self.bluetooth_is_connected = False

    def bluetooth_is_connect(self):
        return self.bluetooth_is_connected

    def read_from_android(self):
        try:
            msg = self.client_sock.recv(2048)
            print(f"[ANDROID] {msg}")
        except Exception as error:
             print("[ERROR] Message from Andorid fail to print: " + str(error))
             raise error
             #reconnect bluetooth
             #self.close_connection()
             #self.start_connection()

    def send_to_android(self, msg):
        try:
            print("To Android: ")
            print(msg)
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
    
    while True:
        msg = input()
        test.send_to_android(msg)

        print("Reading message from android: ")
        test.read_from_android()

    test.close_connection()
