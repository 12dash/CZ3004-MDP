package Actual_Run;

import Communication.Communication;
import Environment.Arena;
import Simulator.Map;

import Simulator.SimulatorConstants;
import Utility.FileManager;
import Utility.MapDescriptor;
import Values.Orientation;

import javax.swing.*;
import java.awt.*;

public class ActualSimulator {

    private static JFrame _appFrame = null;         // application JFrame
    private static JPanel _mapCards = null;         // JPanel for map views


    public static Map map;   // real arena
    private static final Communication comm = Communication.getCommunication();

    //#############################################
    //          SET THESE VALUES
    //#############################################

    private static String INPUT_MAP_FILE = "example_4.txt";
    private static Orientation START_ORIENTATION = Orientation.North;

    //#############################################
    //          SET THESE VALUES
    //#############################################

    public static void main(String[] args) {

        map = new Map(new Arena(), false);
        loadMap();
        displayAll();
        fastestPath();
    }

    private static void loadMap(){
        String[] p_string = FileManager.readFile(INPUT_MAP_FILE);
        int[][] obs = MapDescriptor.getMap(p_string[0], p_string[1]);
        map.arena.make_arena(obs);
        map.arena.setExplored();
        map.repaint();
    }

    public static void displayAll() {

        _appFrame = new JFrame();
        _appFrame.setTitle("Simulator");
        _appFrame.setSize(new Dimension(SimulatorConstants.DEFAULT_WIDTH, SimulatorConstants.DEFAULT_HEIGHT));
        _mapCards = new JPanel(new CardLayout());

        initialiseMap();

        Container contentPane = _appFrame.getContentPane();
        contentPane.add(_mapCards, BorderLayout.CENTER);

        _appFrame.setVisible(true);
        _appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    private static void initialiseMap() {
        _mapCards.add(map, "MAP");
        CardLayout cl = ((CardLayout) _mapCards.getLayout());
        cl.show(_mapCards, "MAP");
    }


    public static void fastestPath(){

        int wayP_x = 12;
        int wayP_y = 12;

//
//        while (true) {
//            String msg = comm.recvMsg();
//            String[] msgArr = msg.split(":");
//            System.out.println(msgArr);
//            if (msgArr[0].equals(CommunicationConstants.START)) {
//                if (msgArr[1].equals(CommunicationConstants.FP)) {
//                    System.out.println("Received command to start fastest path...");
//                    break;
//                }
//            } else if (msgArr[0].equals(CommunicationConstants.WAYPOINT)) {
//                wayP_x = Integer.parseInt(msgArr[1]);
//                wayP_y = Integer.parseInt(msgArr[2]);
//                System.out.println("Received waypoint: (" + wayP_x + "," + wayP_y + ")");
//                wayP_y = ArenaConstants.ARENA_ROWS - wayP_y;
//            }
//        }

        map.setWaypoint(wayP_x, wayP_y);
        int[] pos = new int[]{wayP_x, wayP_y}; //x,y
        map.robotReal.setOrientation(START_ORIENTATION);
        map.robotReal.setPath(Algo.FastestPath.findPath(map.arena, pos));
        String commandString = map.robotReal.generateMovementCommands(START_ORIENTATION);
        map.repaint();
        System.out.println("Sending Command String to Arduino: " + commandString);
//        comm.sendMsg(CommunicationConstants.ANRDUINO, commandString);
    }



}
