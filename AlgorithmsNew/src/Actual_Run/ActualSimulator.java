package Actual_Run;

import Communication.*;
import Environment.*;
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
    private static String commandString = ""; // To store the command string

    //#############################################
    //          SET THESE VALUES
    //#############################################

    private static String INPUT_MAP_FILE = "example_1.txt";
    private static Orientation START_ORIENTATION = Orientation.East;
    private static int wayP_x = 12;
    private static int wayP_y = 12;


    //#############################################
    //
    //#############################################

    public static void main(String[] args) {

        map = new Map(new Arena(), false);
        loadMap();
        displayAll();
        comm.openConnection();
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

        while (true) {

            String msg = comm.recvMsg();
            String[] msgArr = msg.split(":");
            if (msgArr[0].equals(CommunicationConstants.START)) {

                // ##########################################
                //              SEND FASTEST PATH STRING
                // ###########################################

                if (msgArr[1].equals(CommunicationConstants.FP)) {
                    comm.sendMsg(CommunicationConstants.ANRDUINO, commandString);
                    System.out.println("Starting fastest path!");
                    map.repaint();
                }
            }
                // #############################################################
                //   SET WAY POINT,  GET THE FASTEST PATH & INITIAL ORIENTATION
                // #############################################################

            else if (msgArr[0].equals(CommunicationConstants.WAYPOINT)) {
                wayP_x = Integer.parseInt(msgArr[1]);
                wayP_y = Integer.parseInt(msgArr[2]);
                System.out.println("Waypoint Set: (" + wayP_x + "," + wayP_y + ")");

                wayP_y = ArenaConstants.ARENA_ROWS - wayP_y + 1;  // The algo grid counts y in reverse
                map.setWaypoint(wayP_x, wayP_y);
                int[] pos = new int[]{wayP_x, wayP_y}; //x,y

                map.robotReal.setPath(Algo.FastestPath.findPath(map.arena, pos));

                if(map.robotReal.getPath().get(1).getX() == 2){
                    System.out.println("Set the robot facing EAST");
                    map.robotReal.setOrientation(Orientation.East);
                }
                else{
                    System.out.println("Set the robot facing NORTH");
                    map.robotReal.setOrientation(Orientation.North);
                }

                commandString = map.robotReal.generateMovementCommands(map.robotReal.getOrientation());
                map.repaint();
            }
        }
    }



}
