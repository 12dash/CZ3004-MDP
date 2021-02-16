package communicationManager;

import java.net.*;
import java.io.*;

public class Client {

    public static Socket s = null;

    public static void socket_connect() {
        try {
            s = new Socket(commConstants.IP_ADDRESS, commConstants.PORT);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void close_connection() {
        if (s != null) {
            try {
                s.close();
            } catch (Exception e) {
                System.out.println("Error in closing the connection");
            }
        }
    }

    public static void send_message(String msg) {
        try {
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(msg);
            dout.flush();
            dout.close();
        } catch (Exception e) {
            System.out.println("Error in sending the message : " + e);
        }
    }

    public static void main(String[] args) {
        socket_connect();
        if (s != null) {
            send_message("Hello Server");
        } else {
            System.out.println("Socket did not connect");
        }
    }
}
