package Actual_Run;

import Communication.*;
import Communication.CommunicationConstants;
import Environment.Arena;
import Robot.RobotConstants;
import Simulator.Map;
import Utility.MapDescriptor;
import javafx.scene.shape.MoveTo;

import java.util.Scanner;

public class testsComm {

    public static void main(String[] args){

//        testRobotMovements();
//        testMapDescriptor();
        testSendComm();
    }


    public static void testMapDescriptor(){
        Communication comm = Communication.getCommunication();
        String[] p1p2 = new String[]{"FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF0000","000000000000010042038400000000000000030C000000000000021F8400080000000000040"};
        String anMssg = "{p1:" + p1p2[0] + ",p2:" + p1p2[1] +"}";
        comm.openConnection();
        Scanner sc= new Scanner(System.in);
        while(true) {
            sc.nextLine();
            comm.sendMsg(CommunicationConstants.ANDROID, anMssg);
        }
    }

    public static void testRobotMovements(){
        Communication comm = Communication.getCommunication();
        comm.openConnection();
        Scanner sc= new Scanner(System.in);
        while(true) {
            String inp = sc.nextLine();
            String androidMsg = "{move:" + (inp) + "}";
            comm.sendMsg(CommunicationConstants.ANDROID, androidMsg);
        }
    }

    public static void testSendComm(){
        Scanner sc= new Scanner(System.in);
        Communication comm = Communication.getCommunication();

        String[] p1p2 = new String[]{"FF007E00FC0180030004000800100020004000800000000000000000000000000007000E001F", "000004000000"};  // This ret
        String anMssg = "{p1:\"" + p1p2[0] + "\",p2:\"" + p1p2[1] + "\"}";


        comm.openConnection();
        while(true) {
            sc.nextLine();
            comm.sendMsg(CommunicationConstants.ANDROID, anMssg);
        }
    }

    public void testExploration(){

        int[] result = new int[6];
        Communication comm = Communication.getCommunication();
        comm.openConnection();

        Scanner sc = new Scanner(System.in);

        while(true){
            String inp = sc.nextLine();
            comm.sendMsg(CommunicationConstants.ARDUINO, inp);
            String msg = comm.recvMsg();
            String[] msgArr = msg.split(";");

            result[0] = (int)Double.parseDouble(msgArr[0]);  //FL
            result[1] = (int)Double.parseDouble(msgArr[1]);  //FC
            result[2] = (int)Double.parseDouble(msgArr[2]);  //FR
            result[3] = (int)Double.parseDouble(msgArr[3]);  //LC  This is  because of the order of the sensor reading sent
            result[4] = (int)Double.parseDouble(msgArr[4]);  //LL
            result[5] = (int)Double.parseDouble(msgArr[5]);  //RR

            // LOGGING
            System.out.println("Received Sensor Readings: ");
            System.out.print("Front Left:" + result[0]);
            System.out.print("; Front Center:" + result[1]);
            System.out.print("; Front Right:" + result[2]);
            System.out.print("; Left Center:" + result[3]);
            System.out.print("; Left Left:" + result[4]);
            System.out.print("; Right Right:" + result[5]);
            System.out.println();
        }
    }
}
