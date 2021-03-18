package Robot;


import Values.Orientation;
import java.util.HashMap;

public class RobotConstants {
    public static final int SPEED = 500;                             // delay between movements (ms)
    public static final Orientation START_DIR = Orientation.East;  // start direction
    public static final int START_ROW = 18;                          // row no. of start cell
    public static final int START_COL = 1;                          // col no. of start cell
    public static final int ROBOT_START_X = 1;
    public static final int ROBOT_START_Y = 18;

    //THESE ARE USED FOR CALCULATING STRING OF COMMANDS IN FASTEST PATH
    public static final String LEFT = "L";
    public static final String RIGHT = "R";
    public static final String STRAIGHT = "S";
    public static final String BACK = "I";


    // FOR MAPPING FORWARD STEPS WHICH ARE GREATER THAN 10
    public static final HashMap<Integer, Character> NUMBER_MAPPINGS;
    static {
        NUMBER_MAPPINGS = new HashMap<>();
        NUMBER_MAPPINGS.put(11, '!');
        NUMBER_MAPPINGS.put(12, '@');
        NUMBER_MAPPINGS.put(13, '#');
        NUMBER_MAPPINGS.put(14, '$');
        NUMBER_MAPPINGS.put(15, '%');
        NUMBER_MAPPINGS.put(16, '^');
        NUMBER_MAPPINGS.put(17, '&');
        NUMBER_MAPPINGS.put(18, '*');
        NUMBER_MAPPINGS.put(19, '(');
    }


    // FOR EXPLORATION
    public enum MOVEMENT {
        FORWARD, RIGHT_TURN, LEFT_TURN, TURN_AROUND, ERROR;

        public static char print(MOVEMENT m) {
            switch (m) {
                case FORWARD:
                    return '0';
                case RIGHT_TURN:
                    return 'R';
                case LEFT_TURN:
                    return 'L';
                case TURN_AROUND:
                    return 'I';
                case ERROR:
                default:
                    return 'X';
            }
        }
    }


    public static final int SENSOR_FRONT_SHORT_RANGE_L = 1;               // range of short range sensor (cells)
    public static final int SENSOR_FRONT_SHORT_RANGE_H = 1;               // range of short range sensor (cells)
    public static final int SENSOR_LEFT_RIGHT_SHORT_RANGE_L = 1;               // range of short range sensor (cells)
    public static final int SENSOR_LEFT_RIGHT_SHORT_RANGE_H = 3;               // range of short range sensor (cells)
    public static final int SENSOR_LONG_RANGE_L = 4;                // range of long range sensor (cells)
    public static final int SENSOR_LONG_RANGE_H = 7;                // range of long range sensor (cells)

    public static final int NUM_MOVES_AFTER_CLICK_PICTURE = 12;
    public static final int NUM_MOVES_AFTER_CALIBRATE = 4;

}
