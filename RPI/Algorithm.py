import socket

"""
Connection between RPI and Algorithm via IP Socket. 
"""
class Algorithm:
    #Testing using own wifi instead of RPI remember to change to correct IP address
    #WIFI_IP = '192.168.1.175'
    WIFI_IP = '192.168.8.8'
    WIFI_PORT = 8080
    HEADER = 512
    FORMAT = 'UTF-8'
    DISCONNECT_MESSAGE = "!DISCONNECT!"

    def __init__(self, host = WIFI_IP, port = WIFI_PORT):
        #create a new host and port 
        self.host = host
        self.port = port

        self.server = None
        self.client_conn = None
        self.client_addr = None
        self.connected = False

        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        
        #SO_REUSEADDR flag tells the kernel to reuse local socket
        #to avoid error if run several times with too small delay between excutions
        self.server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        ADDR = (self.host, self.port)
        self.server.bind(ADDR)

    def start_connection(self):
        self.server.listen(3)
        print(f"[Listening] Server is listening on {self.server}")

        while True:
            self.client_conn, self.client_addr = self.server.accept()
            print(f"[NEW CONNECTION] Algorithm at {self.client_addr} connected.")
            # codes below are for testing. make sure to comment block or delete the codes in the future
            read_from_client()
            msg = "Hello Algo from RPI"
            send_to_client(msg)

    def stop_connection(self):
        try:
            if self.client_conn:
                print(f"[CONNECTION CLOSE] Algorithm at {self.client_addr}")
                self.client_conn.close()
                self.connected = False
                self.client_conn = None
        except Exception as error:
            print("[Error] Algorithm disconect failed:" + str(error))

    def read_from_client(self):
        self.connected = True

        while self.connected:
            msg_length = self.client_conn.recv(HEADER).decode(FORMAT)
            #if msg_length have content
            if msg_length:
                msg_length = int(msg_length)
                msg = self.client_conn.recv(msg_length).decode(FORMAT)
                print(f"[ALGORITHM] {msg}")
                if msg == DISCONNECT_MESSAGE:
                    self.connected = False

        print(f"[CONNECTION CLOSE] Algorithm at {self.client_addr}")
        self.client_conn.close()
    
    def send_to_client(self, msg):
        #all statements are executed until an exception is encountered
        try:
            print("To Algorithm: ")
            print(msg)
            message = msg.encode(FORMAT)
            self.client_conn.send(msg)
        except Exception as error:
            print("[ERROR] Message can't be send to Algorithm")
            print("Error message (Algorithm): " + str(error))
            raise error

"""
This is testing connection between algorithm and rpi to check if the coding works.
Remember to comment block the code once testing is successful 
"""
if __name__ == '__main__':
    ser = Algorithm()
    ser.start_connection()
#this flush function will flush any input output buffer. 
#to avoid receiving or sending weird or incomplete data at the start of the communication
    ser.flush()

    while true:
        msg = "Hi Algorithm from RPI"
        ser.send_to_client()
        if ser.in_waiting > 0:
            print("Reading from Algorithm:")
            ser.read_from_client()

        ser.stop_connection()