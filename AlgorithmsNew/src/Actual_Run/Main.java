package Actual_Run;

import Environment.Arena;
import Simulator.Map;
import Utility.FileManager;
import Utility.MapDescriptor;
import Values.Orientation;


public class Main {

    private static Map map;
    private static String[] p_string;


    //#############################################
    //          SET THESE VALUES
    //#############################################
    private static int wayP_x = 1;
    private static int wayP_y = 3;
    private static Orientation START_ORIENTATION = Orientation.East;
    private static String INPUT_MAP_FILE = "example_1.txt";


    public static void main(String[] args) {
        map = new Map(new Arena(), false);

        String map_file = INPUT_MAP_FILE;
        String[] p_string = FileManager.readFile(map_file);
        int[][] obs = MapDescriptor.getMap(p_string[0], p_string[1]);
        map.arena.make_arena(obs);
        map.arena.setExplored();

        fastestPath();
    }


//################################
//    FASTEST PATH
//################################
    public static void fastestPath() {

        map.arena.setExplored();
        int[] pos = new int[]{wayP_x, wayP_y}; //x,y
        map.robotReal.setPath(Algo.FastestPath.findPath(map.arena, pos));

        // To get the initial orientation
//        if(map.robotReal.getPath().get(1) == )

        String commandString = map.robotReal.generateMovementCommands(START_ORIENTATION);

        System.out.println("Sending Command String to Arduino: " + commandString);

    }
}

