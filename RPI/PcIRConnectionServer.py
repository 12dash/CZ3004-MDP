import socket
from config import *
from struct import pack

"""
Connection between RPI and IR PC via IP Socket. 
"""

class PcIRConnectionServer:
    def __init__(self,  port = PORT):
        self.server_ip = SERVER_IP
        self.port = port
        self.server = None
        self.client_conn = None
        self.client_addr = None
        self.connected = False

    def start_connection(self):
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.server.bind((self.server_ip, self.port))
        self.server.listen()
        print(f"[Listening] Server is listening on {self.server}")

        self.client_conn, self.client_addr = self.server.accept()
        print(f"[NEW CONNECTION] Algorithm at {self.client_addr} connected.")
        self.connected = True

    def read_from_client(self):
        print("Reading message from PC: ")
        msg = self.client_conn.recv(PC_BUFFER_SIZE).decode(FORMAT)
        print(f"[PC] {msg}")
        return msg
          
    # Modify this for IR runs -- Sending an image array in batches      
    def send_to_client(self, image_data):
        try:
            ## Old
            # msg = msg.encode(FORMAT)
            # self.client_conn.send(msg)

            image_data = image_data.encode(FORMAT)
            # use struct to make sure we have a consistent endianness on the length
            print(f"Length of img data: {len(image_data)}")
            length = pack('>Q', len(image_data))
            # sendall to make sure it blocks if there's back-pressure on the socket
            self.client_conn.sendall(length)
            self.client_conn.sendall(image_data)

            if image_data == DISCONNECT_MESSAGE:
                self.stop_connection()
        except Exception as error:
            print("[ERROR] Message can't be send to Algorithm")
            print("Error message (Algorithm): " + str(error))
            raise error
        

    def stop_connection(self):
        try:
            print(f"[CONNECTION CLOSE] Algorithm at {self.client_addr}")
            self.client_conn.close()
            self.connected = False
            self.client_conn = None
        except Exception as error:
            print("[Error] Algorithm disconect failed:" + str(error))

if __name__ == '__main__':
    server = PcConnectionServer()
    server.start_connection()
