import Communication.Communication;
import Communication.CommunicationConstants;
import Environment.Arena;
import Simulator.Map;
import Utility.FileManager;
import Utility.MapDescriptor;
import Values.Orientation;


public class Main {

    private static Orientation START_ORIENTATION = Orientation.East;

    private static final Communication comm = Communication.getCommunication();
    private static Map map;
    private static int wayP_x = 1;
    private static int wayP_y = 3;
    private static String[] p_string;


    //#############################################
    //SET THE INITIAL ORIENTATION OF THE ROBOT HERE
    //#############################################
    private static Orientation start_orientation = Orientation.East;


    public static void main(String[] args) {
        map = new Map(new Arena(), false);

        String map_file = "example_1.txt";
        String[] p_string = FileManager.readFile(map_file);
        int[][] obs = MapDescriptor.getMap(p_string[0], p_string[1]);

        map.arena.make_arena(obs);
        map.arena.setExplored();

//      comm.openConnection();

        fastestPath();
    }

//################################
// SEND P1, P2 STRINGS TO ANDROID
// ################################
    public static void sendP1P2(){
        String p1 = p_string[0];
        String p2 = p_string[1];
    }


//################################
//    FASTEST PATH
//################################
    public static void fastestPath() {

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

        int[] pos = new int[]{wayP_x, wayP_y}; //x,y
        map.robotReal.setPath(Algo.FastestPath.findPath(map.arena, pos));
        String commandString = map.robotReal.generateMovementCommands(START_ORIENTATION);

        System.out.println("Sending Command String to Arduino: " + commandString);
//        comm.sendMsg(CommunicationConstants.ANRDUINO, commandString);

    }
}

