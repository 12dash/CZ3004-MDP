package Actual_Run;

import Algo.Exploration;
import Communication.*;
import Communication.CommunicationConstants;
import Environment.Arena;
import Environment.ArenaConstants;
import Robot.RobotConstants;
import Simulator.Map;
import Simulator.SimulatorConstants;
import Utility.FileManager;
import Utility.MapDescriptor;
import javafx.scene.shape.MoveTo;
import Exploration.ExplorationAlgo;
import Robot.RobotConstants.MOVEMENT;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class testsComm {

    public static Map map;
    private static int timeLimit = ArenaConstants.MAX_TIME_LIMIT;
    private static int coverage = ArenaConstants.MAX_COVERAGE;

    private static JFrame _appFrame = null;         // application JFrame
    private static JPanel _mapCards = null;         // JPanel for map views

    public static void main(String[] args){

//        testRobotMovements();
//        testMapDescriptor();
//        testSendComm();
//        goToIslands();
//        testExploration();
        testIR();
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

    public static void testExploration(){
        map = new Map(new Arena(false), false, true);
        displayAll();
        int[] result = new int[6];
        Communication comm = Communication.getCommunication();
        comm.openConnection();

        Scanner sc = new Scanner(System.in);


        ExplorationAlgo e = new ExplorationAlgo(map, timeLimit, coverage, comm);

        while(true) {
            String inp = sc.nextLine();
            MOVEMENT m = null;
            switch (inp) {
                case "E":
                    comm.sendMsg(CommunicationConstants.ARDUINO, CommunicationConstants.START_EPLORATION);
                    break;
                case "0":
                    m = MOVEMENT.FORWARD;
                    break;
                case "L":
                    m = MOVEMENT.LEFT_TURN;
                    break;
                case "R":
                    m = MOVEMENT.RIGHT_TURN;
                    break;
                case "I":
                    m = MOVEMENT.TURN_AROUND;
                    break;
                default:
                    m = MOVEMENT.ERROR;
            }
            if (!inp.equals("E") && !m.equals(MOVEMENT.ERROR))
                map.robotReal.move(m);

//            comm.sendMsg(CommunicationConstants.ARDUINO, inp);

            e.senseAndRepaint(false);
//
        }
    }


    public static void goToIslands(){
        int i = ArenaConstants.ARENA_ROWS;
        int j = -1;

        while (i != 0 || j != ArenaConstants.ARENA_COLS - 1){
            if (i > 0) {
                i--;
            }
            if (j < ArenaConstants.ARENA_COLS - 1) {
                j++;
            }

            for (int c = 0; c <= j; c++) {
                System.out.println(i + "," + c);
            }
            for (int r = i+1; r <= ArenaConstants.ARENA_ROWS - 1; r++) {
                System.out.println(r + "," + j);
            }

        }
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


//    private static void loadMap(Map map){
//        String[] p_string = FileManager.readFile(INPUT_MAP_FILE);
//        int[][] obs = MapDescriptor.getMap(p_string[0], p_string[1]);
//        map.arena.make_arena(obs);
//        map.arena.setExplored();
//        map.repaint();
//    }

    public static void testIR(){
        Communication comm = Communication.getCommunication();
        comm.openConnection();
        while (true){
            Scanner sc = new Scanner(System.in);
            String a =sc.nextLine();
            comm.sendMsg(CommunicationConstants.IR, "{\"coords\":["+ a +",9],\"nearby\":\"True\"}");
            String b  = comm.recvMsg();
            System.out.println(b);
        }
    }
}
