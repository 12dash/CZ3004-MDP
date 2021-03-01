import Algo.Exploration;
import Communication.Communication;
import Communication.CommunicationConstants;
import Environment.Arena;
import Environment.ArenaConstants;
import Robot.Robot;
import Robot.RobotConstants;
import Robot.RobotSimulator;
import Simulator.Map;
import Utility.FileManager;
import Utility.MapDescriptor;
import javafx.scene.control.skin.SliderSkin;
import org.json.*;


import javax.swing.*;
import java.sql.ClientInfoStatus;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    private static final Communication comm = Communication.getCommunication();
    private static Map map;
    private static int wayP_x;
    private static int wayP_y;
    private static String[] p_string;


    public static void main(String[] args) {
        map = new Map(new Arena(), false);
        wayP_x = 1;
        wayP_y = 1;

        String map_file = "example_1.txt";

        String[] p_string = FileManager.readFile(map_file);
        int[][] obs = MapDescriptor.getMap(p_string[0], p_string[1]);
        map.arena.make_arena(obs);
        map.arena.setExplored();

        comm.openConnection();


        while (true) {
//            Scanner sc = new Scanner(System.in);
//            System.out.print(""+
//                    "1. Send P1, P2 Strings to Android \n" +
//                    "2. Fastest Path\n");
//
//            String in = sc.next();
//
//            if (in.equals("1")){
//                sendP1P2();
//            }
//            else if(in.equals("2")){
            fastestPath();
//            }

        }
    }
//################################
// SEND P1, P2 STRINGS TO ANDROID
// ################################
    public static void sendP1P2(){
        String p1 = p_string[0];
        String p2;

    }


//################################
//    FASTEST PATH
//################################
    public static void fastestPath() {

        while (true) {
            String msg = comm.recvMsg();
            String[] msgArr = msg.split(":");
            System.out.println(msgArr);
            if (msgArr[0].equals(CommunicationConstants.START)) {
                if (msgArr[1].equals(CommunicationConstants.FP)) {
                    System.out.println("Received command to start fastest path...");
                    break;
                }
            } else if (msgArr[0].equals(CommunicationConstants.WAYPOINT)) {
                wayP_x = Integer.parseInt(msgArr[1]);
                wayP_y = Integer.parseInt(msgArr[2]);
                System.out.println("Received waypoint: (" + wayP_x + "," + wayP_y + ")");
                wayP_y = ArenaConstants.ARENA_ROWS - wayP_y;
            }
        }

        int[] pos = new int[]{wayP_x, wayP_y}; //x,y

        map.robotReal.setPath(Algo.FastestPath.findPath(map.arena, pos));
        map.robotReal.getPathOrientation();
        map.robotReal.calculateMovementCommands();

        String commandString = map.robotReal.generateCommandsString();

        System.out.println("Sending Command String to Arduino: " + commandString);
        comm.sendMsg(CommunicationConstants.ANRDUINO, commandString);

    }
}

