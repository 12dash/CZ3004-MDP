import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Communication manager to communicate with the different parts of the system via the RasPi.
 *
 * @author SuyashLakhotia
 */

public class communication {

    public static final String ANDROID = "an";
    public static final String ANRDUINO = "ar";
    public static final String EX_START = "EX_START";       // Android --> PC
    public static final String FP_START = "FP_START";       // Android --> PC
    public static final String MAP_STRINGS = "MAP";         // PC --> Android
    public static final String BOT_POS = "BOT_POS";         // PC --> Android
    public static final String BOT_START = "BOT_START";     // PC --> Arduino
    public static final String INSTRUCTIONS = "INSTR";      // PC --> Arduino
    public static final String SENSOR_DATA = "SDATA";       // Arduino --> PC

    private static communication communication = null;
    private static Socket conn = null;

    private BufferedWriter writer;
    private BufferedReader reader;

    private communication() {
    }


    public static void main(String[] args) {
        communication comm = new communication();
        comm.openConnection();

        // TODO: Implement multi-threading
        while (comm.isConnected()) {
            comm.recvMsg();
        }
        while (true) {

            Scanner inmMsg = new Scanner(System.in);
            System.out.print("Recipient: ");
            String rec = inmMsg.nextLine();
            System.out.print("Message: ");
            String msg = inmMsg.nextLine();
            comm.sendMsg(rec, msg);
        }
        //comm.closeConnection();
    }

    public static communication getcommunication() {
        if (communication == null) {
            communication = new communication();
        }
        return communication;
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
        System.out.println("Sending a message...");

        try {
            String outputMsg;
            outputMsg = recipient + "|" + message;
//            if (msg == null) {
//                outputMsg = receipient + "|";
//            } else if (msgType.equals(MAP_STRINGS) || msgType.equals(BOT_POS)) {
//                outputMsg = msgType + " " + msg + "\n";
//            } else {
//                outputMsg = msgType + "\n" + msg + "\n";
//            }

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

    public String recvMsg() {
        System.out.println("Receiving a message...");

        try {
            StringBuilder sb = new StringBuilder();
            String input = reader.readLine();

            if (input != null && input.length() > 0) {
                sb.append(input);
                System.out.println(sb.toString());
                return sb.toString();
            }
        } catch (IOException e) {
            System.out.println("recvMsg() --> IOException");
        } catch (Exception e) {
            System.out.println("recvMsg() --> Exception");
            System.out.println(e.toString());
        }

        return null;
    }

    public boolean isConnected() {
        return conn.isConnected();
    }
}