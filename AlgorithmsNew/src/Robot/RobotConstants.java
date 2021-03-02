package Robot;
import static java.util.Map.entry;


import Values.Orientation;
import java.util.HashMap;
import java.util.Map;

public class RobotConstants {
    public static final int SPEED = 500;                             // delay between movements (ms)
    public static final Orientation START_DIR = Orientation.East;  // start direction
    public static final int START_ROW = 18;                          // row no. of start cell
    public static final int START_COL = 1;                          // col no. of start cell
    public static final int ROBOT_START_X = 1;
    public static final int ROBOT_START_Y = 18;

    public static final String LEFT = "L";
    public static final String RIGHT = "R";
    public static final String STRAIGHT = "S";
    public static final String BACK = "I";
    public static final String MOVEMENT_ERROR = "X";

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
}
