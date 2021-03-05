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


    // TODO: Finalise protocol for these commands: most importantly backward, calibrate
    // FOR EXPLORATION
    public enum MOVEMENT {
        FORWARD, BACKWARD, RIGHT_TURN, LEFT_TURN, TURN_AROUND, CALIBRATE, ERROR;

        public static char print(MOVEMENT m) {
            switch (m) {
                case FORWARD:
                    return '0';
                case BACKWARD:
                    return 'B';
                case RIGHT_TURN:
                    return 'R';
                case LEFT_TURN:
                    return 'L';
                case TURN_AROUND:
                    return 'I';
                case CALIBRATE:
                    return 'C';
                case ERROR:
                default:
                    return 'E';
            }
        }
    }

    //TODO: Ask for these ranges from Arduino

    public static final int SENSOR_SHORT_RANGE_L = 1;               // range of short range sensor (cells)
    public static final int SENSOR_SHORT_RANGE_H = 2;               // range of short range sensor (cells)
    public static final int SENSOR_LONG_RANGE_L = 3;                // range of long range sensor (cells)
    public static final int SENSOR_LONG_RANGE_H = 4;                // range of long range sensor (cells)

}
