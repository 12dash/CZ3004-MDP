package Robot;

import Communication.Communication;
import Communication.CommunicationConstants;
import Environment.Grid;
import Simulator.Map;
import Values.Orientation;
import java.util.ArrayList;
import Utility.MapDescriptor;


/**
 * *
 * The robot is represented by a 3 x 3 cell space as below:
 *
 *           ^   ^   ^
 *          SR  SR  SR
 *      <SR [X] [X] [X]
 *      <LR [X] [X] [X] SR>
 *          [X] [X] [X]
 *
 * SR = Short Range Sensor, LR = Long Range Sensor
 *
 */

import Robot.RobotConstants.MOVEMENT;
import Exploration.Sensor;
import javafx.scene.shape.MoveTo;

public class RobotReal extends Robot{

    protected ArrayList<Grid> path = new ArrayList<>();  //Stores the fastest path solution
    private Orientation cur_or;     // For calculating the commands in Fastest Path

    // posRow & posCol are for exploration, in PC simulator robot's cur pos defined by Grid cur in Robot class
    // These are according to the ALGO MAP REPRESENTATION i.e. the top-right cell is (0, 0)
    private int posRow;
    private int posCol;
    private boolean calibMode = false;
    private boolean clickingRandomPicture = false;

    // Sensors
    private final Sensor FrontLeftSR;       // front-facing left SR
    private final Sensor FrontCenterSR;     // front-facing center SR
    private final Sensor FrontRightSR;      // front-facing right SR
    private final Sensor LeftRightSR;       // left-facing left SR
    private final Sensor LeftCenterLR;      // left-facing center LR // LONG RANGE
    private final Sensor RightRightSR;     // right-facing left SR

    private boolean touchedGoal;

    public RobotReal(Grid g) {
        super(g);

        this.posRow = g.getY();
        this.posCol = g.getX();
        this.orientation = RobotConstants.START_DIR; // EAST

        FrontLeftSR = new Sensor(RobotConstants.SENSOR_FRONT_SHORT_RANGE_L, RobotConstants.SENSOR_FRONT_SHORT_RANGE_H, this.posRow - 1, this.posCol + 1, this.orientation, "FL");
        FrontCenterSR = new Sensor(RobotConstants.SENSOR_FRONT_SHORT_RANGE_L, RobotConstants.SENSOR_FRONT_SHORT_RANGE_H, this.posRow, this.posCol+1, this.orientation, "FC");
        FrontRightSR = new Sensor(RobotConstants.SENSOR_FRONT_SHORT_RANGE_L, RobotConstants.SENSOR_FRONT_SHORT_RANGE_H, this.posRow + 1, this.posCol + 1, this.orientation, "FR");
        LeftRightSR = new Sensor(RobotConstants.SENSOR_LEFT_RIGHT_SHORT_RANGE_L, RobotConstants.SENSOR_LEFT_RIGHT_SHORT_RANGE_H, this.posRow - 1, this.posCol + 1, findNewDirection(MOVEMENT.LEFT_TURN), "LR");
        LeftCenterLR = new Sensor(RobotConstants.SENSOR_LONG_RANGE_L, RobotConstants.SENSOR_LONG_RANGE_H, this.posRow - 1 , this.posCol, findNewDirection(MOVEMENT.LEFT_TURN), "LC");
        RightRightSR = new Sensor(RobotConstants.SENSOR_FRONT_SHORT_RANGE_L, RobotConstants.SENSOR_FRONT_SHORT_RANGE_H, this.posRow + 1, this.posCol+1, findNewDirection(MOVEMENT.RIGHT_TURN), "RR");
    }



    //########################################
    // METHODS FOR FASTEST PATH
    //########################################

    private String nextMove(Grid cur, Grid next) {
        Orientation move_dir;

        int cur_X = cur.getX();
        int cur_Y = cur.getY();

        int next_X = next.getX();
        int next_Y = next.getY();

        if (cur_X == next_X) {
           move_dir = (cur_Y - next_Y) > 0 ? Orientation.North : Orientation.South;
        }
        else{
           move_dir = (cur_X - next_X) > 0 ? Orientation.West : Orientation.East;
        }

        String move="";

        switch(this.cur_or) {
            case North:
                switch (move_dir){
                    case North:
                        move = RobotConstants.STRAIGHT;
                        break;
                    case East:
                        move = RobotConstants.RIGHT + RobotConstants.STRAIGHT;
                        break;
                    case South:
                        move = RobotConstants.BACK + RobotConstants.STRAIGHT;
                        break;
                    case West:
                        move = RobotConstants.LEFT + RobotConstants.STRAIGHT;
                        break;
                }
            break;

            case East:
                switch (move_dir){
                    case North:
                        move = RobotConstants.LEFT + RobotConstants.STRAIGHT;
                        break;
                    case East:
                        move = RobotConstants.STRAIGHT;
                        break;
                    case South:
                        move = RobotConstants.RIGHT + RobotConstants.STRAIGHT;
                        break;
                    case West:
                        move = RobotConstants.BACK + RobotConstants.STRAIGHT;
                        break;
                }
            break;

            case South:
                switch (move_dir){
                    case North:
                        move = RobotConstants.BACK + RobotConstants.STRAIGHT;
                        break;
                    case East:
                        move = RobotConstants.LEFT + RobotConstants.STRAIGHT;
                        break;
                    case South:
                        move = RobotConstants.STRAIGHT;
                        break;
                    case West:
                        move = RobotConstants.RIGHT + RobotConstants.STRAIGHT;
                        break;
                }
            break;

            case West:
                switch (move_dir){
                    case North:
                        move = RobotConstants.RIGHT + RobotConstants.STRAIGHT;
                        break;
                    case East:
                        move = RobotConstants.BACK+ RobotConstants.STRAIGHT;
                        break;
                    case South:
                        move = RobotConstants.LEFT + RobotConstants.STRAIGHT;
                        break;
                    case West:
                        move = RobotConstants.STRAIGHT;
                        break;
                }
            break;
        }

        this.cur_or = move_dir;
        return move;
    }


    public String getCommandString(StringBuilder commands){  // Convert all the 'S' to numbers

        StringBuilder cmds = new StringBuilder();
        String temp = commands.toString();
        System.out.format("Commands: %s", temp);

        char[] commandsArray = temp.toCharArray();
        System.out.println();
        int i = 0;
        while(i < commandsArray.length){
            if (commandsArray[i] != 'S'){
                cmds.append(commandsArray[i]);
                i++;
            }
            else{
                int countS = -1;        // 0 => 1 step forward
                while(i < commandsArray.length && commandsArray[i] == 'S'){
                    countS++;
                    i++;
                }

                if (countS >= 10){      // For number of forward steps greater than 10
                    cmds.append(RobotConstants.NUMBER_MAPPINGS.get(countS+1));
                }
                else {
                    cmds.append(countS);
                }
            }
        }
        return cmds.toString();
    }

    public String generateMovementCommands(Orientation initial_or) {
        this.cur_or = initial_or;

        StringBuilder commands = new StringBuilder(); // Stores the commands to send to Arduino
        commands = new StringBuilder();

        for (int i = 0; i < this.path.size()-1; i++) {
                commands.append(nextMove(path.get(i), path.get(i + 1)));
        }
        return getCommandString(commands);
    }


    public ArrayList<Grid> getPath() {
        return this.path;
    }

    public void setPath(ArrayList<Grid> path) {
        this.path = path;

    }


    //########################################
    // METHODS FOR EXPLORATION
    //########################################

    /**
     * Takes in a MOVEMENT and moves the robot accordingly by changing its position and direction. Sends the movement
     * if this.realBot is set.
     */
    public void move(MOVEMENT m) {

        switch (m) {
            case FORWARD:
                switch (this.orientation) {
                    case North:
                        posRow--; // The rows in grid start from top left as 0
                        break;
                    case East:
                        posCol++;
                        break;
                    case South:
                        posRow++;
                        break;
                    case West:
                        posCol--;
                        break;
                }
                break;
            case RIGHT_TURN:
            case LEFT_TURN:
            case TURN_AROUND:
                this.orientation = findNewDirection(m);
                break;
            default:
                System.out.println("Error in Robot.move()!");
                break;
        }


        updateTouchedGoal();
    }

    // Override for array of movements
    public void move(MOVEMENT[] m) {

        for(MOVEMENT move:m) {
            switch (move) {
                case FORWARD:
                    switch (this.orientation) {
                        case North:
                            posRow--; // The rows in grid start from top left as 0
                            break;
                        case East:
                            posCol++;
                            break;
                        case South:
                            posRow++;
                            break;
                        case West:
                            posCol--;
                            break;
                    }
                    break;
                case RIGHT_TURN:
                case LEFT_TURN:
                case TURN_AROUND:
                    this.orientation = findNewDirection(move);
                    break;
                default:
                    System.out.println("Error in Robot.move()!");
                    break;
            }
        }

        updateTouchedGoal();
    }

    /**
     * Overloaded method that calls this.move(MOVEMENT m, boolean sendMoveToAndroid = true).
     */

    public void moveSimulate(MOVEMENT[] m){

        for(MOVEMENT move: m) {
            switch (move) {
                case FORWARD:
                    switch (this.orientation) {
                        case North:
                            posRow--; // The rows in grid start from top left as 0
                            break;
                        case East:
                            posCol++;
                            break;
                        case South:
                            posRow++;
                            break;
                        case West:
                            posCol--;
                            break;
                    }
                    break;
                case RIGHT_TURN:
                case LEFT_TURN:
                case TURN_AROUND:
                    this.orientation = findNewDirection(move);
                    break;
                default:
                    System.out.println("Error in Robot.move()!");
                    break;
            }
        }
        updateTouchedGoal();
    }

// Override for array of movements

    public void moveSimulate(MOVEMENT m){
        switch (m) {
            case FORWARD:
                switch (this.orientation) {
                    case North:
                        posRow--; // The rows in grid start from top left as 0
                        break;
                    case East:
                        posCol++;
                        break;
                    case South:
                        posRow++;
                        break;
                    case West:
                        posCol--;
                        break;
                }
                break;
            case RIGHT_TURN:
            case LEFT_TURN:
            case TURN_AROUND:
                this.orientation = findNewDirection(m);
                break;
            default:
                System.out.println("Error in Robot.move()!");
                break;
        }
//DUBUGGING
//        System.out.println("------------");
//        System.out.println(MOVEMENT.print(m));
//        System.out.println(getOrientation());
//        System.out.println(getRobotPosCol() + ", "+ getRobotPosRow());
//        System.out.println("------------");
//        updateTouchedGoal();
    }

    /**
     * Uses the Communication to send the next movement to the robot.
     */
    public void sendMovement(String m) {
        Communication comm = Communication.getCommunication();
        String androidMsg = "{move:" +  m + "}";
//        comm.sendMsg(CommunicationConstants.ANDROID, androidMsg);
        comm.sendMsg(CommunicationConstants.ARDUINO, m); // For back to back message to arduino and android
    }

    private Orientation findNewDirection(MOVEMENT m) {
        if (m == MOVEMENT.RIGHT_TURN) {
            return Orientation.getNextOrientation(this.orientation);
        } else if (m==MOVEMENT.LEFT_TURN) {
            return Orientation.getPreviousOrientation(this.orientation);
        }
        else{
            return Orientation.getPreviousOrientation(Orientation.getPreviousOrientation(this.orientation));
        }
    }

    public int getRobotPosRow() {
        return posRow;
    }

    public int getRobotPosCol() {
        return posCol;
    }

    private void updateTouchedGoal() {
        if (Map.inGoalZone(this.getRobotPosRow(),this.getRobotPosCol())){
            this.touchedGoal = true;
        }
    }

    public boolean getTouchedGoal(){
        return this.touchedGoal;
    }


    public void setSensors() {
        switch (this.orientation) {
            case North:
                FrontLeftSR.setSensor(this.posRow - 1, this.posCol - 1, this.orientation);
                FrontCenterSR.setSensor(this.posRow - 1, this.posCol, this.orientation);
                FrontRightSR.setSensor(this.posRow - 1, this.posCol + 1, this.orientation);
                LeftRightSR.setSensor(this.posRow - 1, this.posCol - 1, findNewDirection(MOVEMENT.LEFT_TURN));
                LeftCenterLR.setSensor(this.posRow, this.posCol-1, findNewDirection(MOVEMENT.LEFT_TURN));
                RightRightSR.setSensor(this.posRow-1, this.posCol + 1, findNewDirection(MOVEMENT.RIGHT_TURN));
                break;
            case East:
                FrontLeftSR.setSensor(this.posRow - 1, this.posCol + 1, this.orientation);
                FrontCenterSR.setSensor(this.posRow, this.posCol+1, this.orientation);
                FrontRightSR.setSensor(this.posRow + 1, this.posCol + 1, this.orientation);
                LeftRightSR.setSensor(this.posRow - 1, this.posCol + 1, findNewDirection(MOVEMENT.LEFT_TURN));
                LeftCenterLR.setSensor(this.posRow - 1 , this.posCol, findNewDirection(MOVEMENT.LEFT_TURN));
                RightRightSR.setSensor(this.posRow + 1, this.posCol+1, findNewDirection(MOVEMENT.RIGHT_TURN));
                break;
            case South:
                FrontLeftSR.setSensor(this.posRow + 1, this.posCol + 1, this.orientation);
                FrontCenterSR.setSensor(this.posRow + 1, this.posCol, this.orientation);
                FrontRightSR.setSensor(this.posRow + 1, this.posCol - 1, this.orientation);
                LeftRightSR.setSensor(this.posRow + 1, this.posCol + 1, findNewDirection(MOVEMENT.LEFT_TURN));
                LeftCenterLR.setSensor(this.posRow, this.posCol + 1, findNewDirection(MOVEMENT.LEFT_TURN));
                RightRightSR.setSensor(this.posRow+1, this.posCol - 1, findNewDirection(MOVEMENT.RIGHT_TURN));
                break;
            case West:
                FrontLeftSR.setSensor(this.posRow + 1, this.posCol - 1, this.orientation);
                FrontCenterSR.setSensor(this.posRow, this.posCol - 1, this.orientation);
                FrontRightSR.setSensor(this.posRow - 1, this.posCol - 1, this.orientation);
                LeftRightSR.setSensor(this.posRow + 1, this.posCol - 1, findNewDirection(MOVEMENT.LEFT_TURN));
                LeftCenterLR.setSensor(this.posRow + 1, this.posCol, findNewDirection(MOVEMENT.LEFT_TURN));
                RightRightSR.setSensor(this.posRow - 1, this.posCol-1, findNewDirection(MOVEMENT.RIGHT_TURN));
                break;
        }
    }


    /**
     * Calls the .sense() method of all the attached sensors and stores the received values in an integer array.
     *
     * @return [FrontLeftSR, FrontCenterSR, FrontRightSR, LefLeftSR, LeftCenterLR, RightRightSR]
     */
    public int[] senseMap(Map explorationMap) {
        int[] result = new int[6];
            System.out.println("Waiting for sensor readings..");
            Communication comm = Communication.getCommunication();
            String msg = comm.recvMsg();
            while(msg.indexOf(';') == -1){
                System.out.println("INVALID SENSOR READINGS : DOESN'T HAVE ; (SEMICOLON)");
                System.out.println();
                msg = comm.recvMsg();
            }
            String[] msgArr = msg.split(";");

                result[0] = (int)Double.parseDouble(msgArr[0]);  //FL
                result[1] = (int)Double.parseDouble(msgArr[1]);  //FC
                result[2] = (int)Double.parseDouble(msgArr[2]);  //FR
                result[3] = (int)Double.parseDouble(msgArr[3]);  //LC
                result[4] = (int)Double.parseDouble(msgArr[4]);  //LR
                result[5] = (int)Double.parseDouble(msgArr[5]);  //RR


            // LOGGING
            System.out.print("Front Left:" + result[0]);
            System.out.print("; Front Center:" + result[1]);
            System.out.print("; Front Right:" + result[2]);
            System.out.print("; Left Center:" + result[3]);
            System.out.print("; Left Right:" + result[4]);
            System.out.print("; Right Right:" + result[5]);
            System.out.println();

            FrontLeftSR.senseReal(explorationMap, result[0]+1);
            FrontCenterSR.senseReal(explorationMap, result[1]+1);
            FrontRightSR.senseReal(explorationMap, result[2]+1);
            LeftCenterLR.senseReal(explorationMap, result[3]+1);
            LeftRightSR.senseReal(explorationMap, result[4]+1);
            RightRightSR.senseReal(explorationMap, result[5]+1);

            String[] p1p2 =  MapDescriptor.generateMapDescriptor(explorationMap.arena);  // This ret
            String anMssg = "{p1:\"" + p1p2[0] + "\",p2:\"" + p1p2[1] + "\"}";
            comm.sendMsg(CommunicationConstants.ANDROID, anMssg);

        return result;
    }


    public int[] senseSimulate(Map explorationMap, Map realMap) {
        int[] result = new int[6];

        result[0] = FrontRightSR.sense(explorationMap, realMap);
        result[1] = FrontCenterSR.sense(explorationMap, realMap);
        result[2] = FrontLeftSR.sense(explorationMap, realMap);
        result[3] = LeftCenterLR.sense(explorationMap, realMap);
        result[4] = LeftRightSR.sense(explorationMap, realMap);
        result[5] = RightRightSR.sense(explorationMap, realMap);

        return result;
    }

    public boolean getCalibMode(){
        return calibMode;
    }

    public void setCalibMode(boolean calib){
        this.calibMode = calib;
    }

    public void setClickingRandomPicture(boolean val){
        clickingRandomPicture = val;
    }

    public boolean getIfClickingRandomPicture(){
        return clickingRandomPicture;
    }


}


