package Robot;

import Values.Orientation;


public class RobotConstants {
    public static final int GOAL_ROW = 18;                          // row no. of goal cell
    public static final int GOAL_COL = 13;                          // col no. of goal cell
    public static final int START_ROW = 1;                          // row no. of start cell
    public static final int START_COL = 1;                          // col no. of start cell
    public static final int MOVE_COST = 10;                         // cost of FORWARD, BACKWARD movement
    public static final int TURN_COST = 20;                         // cost of RIGHT, LEFT movement
    public static final int SPEED = 100;                            // delay between movements (ms)
    public static final int SENSOR_SHORT_RANGE_L = 1;               // range of short range sensor (cells)
    public static final int SENSOR_SHORT_RANGE_H = 2;               // range of short range sensor (cells)
    public static final int SENSOR_LONG_RANGE_L = 3;                // range of long range sensor (cells)
    public static final int SENSOR_LONG_RANGE_H = 4;                // range of long range sensor (cells)
    public static final Orientation START_DIR = Orientation.North;  // start direction

    public static final int INFINITE_COST = 9999;

    public static final int ROBOT_START_X = 1;
    public static final int ROBOT_START_Y = 18;
}
