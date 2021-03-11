package Communication;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;


public class Communication{

    private static Communication Communication = null;
    private static Socket conn = null;

    private BufferedWriter writer;
    private BufferedReader reader;

    private Communication() {
    }

    public static Communication getCommunication() {
        if (Communication == null) {
            Communication = new Communication();
        }
        return Communication;
    }


    public void openConnection() {
        System.out.println("Opening connection...");

        try {
            String HOST = "192.168.8.8";
            int PORT = 8080;
            conn = new Socket(HOST, PORT);

            writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(conn.getOutputStream())));
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            System.out.println("openConnection() --> " + "Connection established successfully!");

            return;
        } catch (UnknownHostException e) {
            System.out.println("openConnection() --> UnknownHostException");
        } catch (IOException e) {
            System.out.println("openConnection() --> IOException");
        } catch (Exception e) {
            System.out.println("openConnection() --> Exception");
            System.out.println(e.toString());
        }

        System.out.println("Failed to establish connection!");
    }


    public String recvMsg() {

        System.out.println();
        System.out.println("Receiving a message...");

        try {
            String input = reader.readLine();
            System.out.println("Message Received: " + input);
            return input;
        } catch (IOException e) {
            System.out.println("recvMsg() --> IOException");
        } catch (Exception e) {
            System.out.println("recvMsg() --> Exception");
            System.out.println(e.toString());
        }

        return null;
    }

    public void closeConnection() {
        System.out.println("Closing connection...");

        try {
            reader.close();

            if (conn != null) {
                conn.close();
                conn = null;
            }
            System.out.println("Connection closed!");
        } catch (IOException e) {
            System.out.println("closeConnection() --> IOException");
        } catch (NullPointerException e) {
            System.out.println("closeConnection() --> NullPointerException");
        } catch (Exception e) {
            System.out.println("closeConnection() --> Exception");
            System.out.println(e.toString());
        }
    }

    public void sendMsg(String recipient, String message) {
        try {
            String outputMsg;
            outputMsg = recipient + "|" + message;
            System.out.println("Sending out message: " + outputMsg);
            writer.write(outputMsg);
            writer.flush();
        } catch (IOException e) {
            System.out.println("sendMsg() --> IOException");
        } catch (Exception e) {
            System.out.println("sendMsg() --> Exception");
            System.out.println(e.toString());
        }
    }

    public boolean isConnected() {
        return conn.isConnected();
    }
}
