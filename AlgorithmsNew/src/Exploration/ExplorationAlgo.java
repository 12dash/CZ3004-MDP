package Exploration;

import Algo.AStar;
import Algo.Exploration;
import Algo.ExplorationUtility;
import Algo.InnerExploration;
import Environment.Arena;
import Environment.ArenaConstants;
import Environment.Grid;
import Robot.Robot;
import Robot.RobotConstants;
import Values.Orientation;
import Simulator.Map;
import Communication.Communication;
import Communication.CommunicationConstants;
import Robot.RobotConstants.MOVEMENT;
import Robot.RobotReal;
import Exploration.IslandExploration;

import javax.naming.CommunicationException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeUnit;


public class ExplorationAlgo {
    private final Map exploredMap;
    private final Map realMap;
    private final boolean simulate;

    private final int timeLimit;
    private final int coverageLimit;
    private int areaExplored;
    private long startTime;
    private long endTime;
    private int lastCalibrate;
    private boolean calibrationMode = false;
    private Communication comm;

    public ExplorationAlgo(Map exploredMap, int timeLimit, int coverageLimit, Communication comm) {
        this.exploredMap = exploredMap;
        this.timeLimit = timeLimit;
        this.coverageLimit = coverageLimit;
        this.comm = comm;
        this.simulate = false;
        this.realMap = null;
    }

    public ExplorationAlgo(Map exploredMap, Map realMap, int timeLimit, int coverageLimit){
        this.exploredMap = exploredMap;
        this.timeLimit = timeLimit;
        this.coverageLimit = coverageLimit;
        this.realMap = realMap;
        this.simulate = true;
    }

    /**
     * Main method that is called to start the exploration.
     */

    public void initialCalibration(Map exploredMap){
        System.out.println("Starting calibration...");
        //comm.getCommunication().recvMsg(); //TODO: DOES ARDUINO SEND A MESSAGE BEFORE STARTING TO ASK FOR COMMANDS FOR CALIBRATION
        // INITIAL CALIBRATION: Right_Turn -> Calibrate -> Right_Turn -> Calibrate -> Left_Turn -> Calibrate -> Lef_Turn

//        comm.sendMsg(CommunicationConstants.ARDUINO, CommunicationConstants.INITIAL_CALIBRATION);

//        exploredMap.robotReal.move(MOVEMENT.RIGHT_TURN, false);
//        Communication.getCommunication().recvMsg(); // Receive Acknowledgement
//        exploredMap.robotReal.move(MOVEMENT.CALIBRATE, false);
//        Communication.getCommunication().recvMsg(); // Receive Acknowledgement
//        exploredMap.robotReal.move(MOVEMENT.RIGHT_TURN, false);
//        Communication.getCommunication().recvMsg(); // Receive Acknowledgement
//        exploredMap.robotReal.move(MOVEMENT.CALIBRATE, false);
//        Communication.getCommunication().recvMsg(); // Receive Acknowledgement
//        exploredMap.robotReal.move(MOVEMENT.LEFT_TURN, false);
//        Communication.getCommunication().recvMsg(); // Receive Acknowledgement
//        exploredMap.robotReal.move(MOVEMENT.CALIBRATE, false);
//        Communication.getCommunication().recvMsg(); // Receive Acknowledgement
//        exploredMap.robotReal.move(MOVEMENT.LEFT_TURN, false);

    }


    public void runExploration() throws InterruptedException {

        // ##########################################
        //         START EXPLORATION
        // ###########################################

        System.out.println("Starting exploration...");

        startTime = System.currentTimeMillis();
        endTime = startTime + (timeLimit * 1000);

        if(!simulate) {
            Communication.getCommunication().sendMsg(CommunicationConstants.ARDUINO, CommunicationConstants.START_EPLORATION);
        }
        senseAndRepaint(this.simulate);
        areaExplored = calculateAreaExplored();
        System.out.println("Explored Area: " + areaExplored);

//        explorationLoop(exploredMap.robotReal.getRobotPosRow(), exploredMap.robotReal.getRobotPosCol());
    }

    /**
     * Loops through robot movements until one (or more) of the following conditions is met:
     * 1. Robot is back at (r, c)
     * 2. areaExplored > coverageLimit
     * 3. System.currentTimeMillis() > endTime
     */
    private void explorationLoop(int r, int c) throws InterruptedException {
        do {
            nextMove();
            areaExplored = calculateAreaExplored();
            System.out.println("Area explored: " + areaExplored);

            if (exploredMap.robotReal.getRobotPosRow() == r && exploredMap.robotReal.getRobotPosCol() == c) {
                break; // TODO: Breaks after reaching home
//                if (areaExplored >= coverageLimit) {
//                    break;
//                }
            }
            if(this.simulate) {
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
            }
        } while (areaExplored <= coverageLimit && System.currentTimeMillis() <= endTime);



        System.out.println("Completed loop");
        if (exploredMap.robotReal.getRobotPosRow() != r || exploredMap.robotReal.getRobotPosCol() != c) {
            goHome();
        }
    }

    /**
     * Determines the next move for the robot and executes it accordingly.
     */
    private void nextMove() {
        if (lookRight()) {
            moveBot(MOVEMENT.RIGHT_TURN);
            if (lookForward()) moveBot(MOVEMENT.FORWARD);
        } else if (lookForward()) {
            moveBot(MOVEMENT.FORWARD);
        } else if (lookLeft()) {
            moveBot(MOVEMENT.LEFT_TURN);
            if (lookForward()) moveBot(MOVEMENT.FORWARD);
        } else {

            moveBot(MOVEMENT.TURN_AROUND);
        }
    }

    /**
     * Returns true if the right side of the robot is free to move into.
     */
    private boolean lookRight() {
        switch (exploredMap.robotReal.getOrientation()) {
            case North:
                return eastFree();
            case East:
                return southFree();
            case South:
                return westFree();
            case West:
                return northFree();
        }
        return false;
    }

    /**
     * Returns true if the robot is free to move forward.
     */
    private boolean lookForward() {
        switch (exploredMap.robotReal.getOrientation()) {
            case North:
                return northFree();
            case East:
                return eastFree();
            case South:
                return southFree();
            case West:
                return westFree();
        }
        return false;
    }

    /**
     * * Returns true if the left side of the robot is free to move into.
     */
    private boolean lookLeft() {
        switch (exploredMap.robotReal.getOrientation()) {
            case North:
                return westFree();
            case East:
                return northFree();
            case South:
                return eastFree();
            case West:
                return southFree();
        }
        return false;
    }

    /**
     * Returns true if the robot can move to the north cell.
     */
    private boolean northFree() {
        int botRow = exploredMap.robotReal.getRobotPosRow();
        int botCol = exploredMap.robotReal.getRobotPosCol();
        return (isExploredNotObstacle(botRow - 1, botCol - 1) && isExploredAndFree(botRow - 1, botCol) && isExploredNotObstacle(botRow - 1, botCol + 1));
    }

    /**
     * Returns true if the robot can move to the east cell.
     */
    private boolean eastFree() {
        int botRow = exploredMap.robotReal.getRobotPosRow();
        int botCol = exploredMap.robotReal.getRobotPosCol();
        return (isExploredNotObstacle(botRow - 1, botCol + 1) && isExploredAndFree(botRow, botCol + 1) && isExploredNotObstacle(botRow + 1, botCol + 1));
    }

    /**
     * Returns true if the robot can move to the south cell.
     */
    private boolean southFree() {
        int botRow = exploredMap.robotReal.getRobotPosRow();
        int botCol = exploredMap.robotReal.getRobotPosCol();
        return (isExploredNotObstacle(botRow + 1, botCol - 1) && isExploredAndFree(botRow + 1, botCol) && isExploredNotObstacle(botRow + 1, botCol + 1));
    }

    /**
     * Returns true if the robot can move to the west cell.
     */
    private boolean westFree() {
        int botRow = exploredMap.robotReal.getRobotPosRow();
        int botCol = exploredMap.robotReal.getRobotPosCol();
        return (isExploredNotObstacle(botRow - 1, botCol - 1) && isExploredAndFree(botRow, botCol - 1) && isExploredNotObstacle(botRow + 1, botCol - 1));
    }

    /**
     * Returns the robot to START after exploration and points the bot northwards.
     */

    // TODO: Right code for go to home; Tell arduino to have a flag for go home, after this will give the whole string, and just go to home
    // Will append the path after that Eg-> GH:0LR2R

    private void goHome() {

        Grid robotCurGrid = exploredMap.arena.getGrid(exploredMap.robotReal.getRobotPosRow(), exploredMap.robotReal.getRobotPosCol());
        Grid homeGrid = exploredMap.arena.getGrid(ArenaConstants.START_ROW, ArenaConstants.START_COL);

        // TODO: Implement got to goal state if haven't
//        if (!exploredMap.robotReal.getTouchedGoal() && coverageLimit == ArenaConstants.MAX_COVERAGE && timeLimit == ArenaConstants.MAX_TIME_LIMIT) {
//            FastestPathAlgo goToGoal = new FastestPathAlgo(exploredMap, bot, realMap);
//            goToGoal.runFastestPath(RobotConstants.GOAL_ROW, RobotConstants.GOAL_COL);
//        }

        AStar astar = new AStar();
        astar.startSearch(exploredMap.arena, robotCurGrid, homeGrid, false);
        exploredMap.robotReal.setPath(astar.solution);
        String commandString = exploredMap.robotReal.generateMovementCommands(exploredMap.robotReal.getOrientation());
        commandString = CommunicationConstants.GO_HOME + ":" + commandString;      // TODO: Finalise on this protocol
        comm.sendMsg(CommunicationConstants.ARDUINO, commandString);

        //TODO: Do we need to calibrate after reaching home?
        turnBotDirection(Orientation.East);
        comm.sendMsg(CommunicationConstants.ARDUINO, CommunicationConstants.INITIAL_CALIBRATION);

//        System.out.println("Exploration complete!");
//        areaExplored = calculateAreaExplored();
//        System.out.printf("%.2f%% Coverage", (areaExplored / 300.0) * 100.0);
//        System.out.println(", " + areaExplored + " Cells");
//        System.out.println((System.currentTimeMillis() - startTime) / 1000 + " Seconds");

    }

    /**
     * Returns true for cells that are explored and not obstacles.
     */
    private boolean isExploredNotObstacle(int r, int c) {
        if (exploredMap.arena.areValidCoordinates(r, c)){
            Grid tmp = exploredMap.arena.getGrid(r, c);
            return (tmp.isExplored() && !tmp.isObstacle());
        }
        return false;
    }

    /**
     * Returns true for cells that are explored, not virtual walls and not obstacles.
     */
    private boolean isExploredAndFree(int r, int c) {
        if (exploredMap.arena.areValidCoordinates(r, c)) {
            Grid b = exploredMap.arena.getGrid(r, c);
            return (b.isExplored() && b.getAcc()  && !b.isObstacle());
        }
        return false;
    }

    /**
     * Returns the number of cells explored in the grid.
     */
    private int calculateAreaExplored() {
        int result = 0;
        for (int r = 0; r < ArenaConstants.ARENA_ROWS; r++) {
            for (int c = 0; c < ArenaConstants.ARENA_COLS; c++) {
                if (exploredMap.arena.getGrid(r, c).isExplored ()) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * Moves the bot, repaints the map and calls senseAndRepaint().
     */
    private void moveBot(MOVEMENT m) {
        if(m.equals(MOVEMENT.CALIBRATE) && !simulate){
            exploredMap.robotReal.move(m, false);
            comm.recvMsg();
            exploredMap.repaint();
        }
        else{
            if(!simulate) {
                exploredMap.robotReal.move(m);
            }
            else{
                exploredMap.robotReal.moveSimulate(m);
            }
            exploredMap.repaint();
            senseAndRepaint(simulate);
        }

        if (!calibrationMode) {
            calibrationMode = true;

            if (canCalibrateOnTheSpot(exploredMap.robotReal.getOrientation())){
                lastCalibrate = 0;
                moveBot(MOVEMENT.CALIBRATE);
            } else {
                lastCalibrate++;
                if (lastCalibrate >= 5) {
                    Orientation targetDir = getCalibrationDirection();
                    if (targetDir != null) {
                        lastCalibrate = 0;
                        calibrateBot(targetDir);
                    }
                }
            }

            calibrationMode = false;
        }
    }

    /**
     * Sets the bot's sensors, processes the sensor data and repaints the map.
     */
    private void senseAndRepaint(boolean simulate) {
        exploredMap.robotReal.setSensors();
        if(simulate){
            exploredMap.robotReal.senseSimulate(exploredMap, realMap);
        }
        else {
            exploredMap.robotReal.senseMap(exploredMap);
        }
        exploredMap.repaint();
    }

    /**
     * Checks if the robot can calibrate at its current position given a direction.
     */
    private boolean canCalibrateOnTheSpot(Orientation botDir) {
        int row = exploredMap.robotReal.getRobotPosRow();
        int col = exploredMap.robotReal.getRobotPosCol();

        switch (botDir) {
            case North:
                return exploredMap.arena.getIsObstacleOrWall(row - 2, col - 1) && exploredMap.arena.getIsObstacleOrWall(row - 2, col) && exploredMap.arena.getIsObstacleOrWall(row - 2, col + 1);
            case East:
                return exploredMap.arena.getIsObstacleOrWall(row + 1, col + 2) && exploredMap.arena.getIsObstacleOrWall(row, col + 2) && exploredMap.arena.getIsObstacleOrWall(row - 1, col + 2);
            case South:
                return exploredMap.arena.getIsObstacleOrWall(row + 2, col - 1) && exploredMap.arena.getIsObstacleOrWall(row + 2, col) && exploredMap.arena.getIsObstacleOrWall(row + 2, col + 1);
            case West:
                return exploredMap.arena.getIsObstacleOrWall(row + 1, col - 2) && exploredMap.arena.getIsObstacleOrWall(row, col - 2) && exploredMap.arena.getIsObstacleOrWall(row - 1, col - 2);
        }

        return false;
    }

    /**
     * Returns a possible direction for robot calibration or null, otherwise.
     */
    private Orientation getCalibrationDirection() {
        Orientation origDir = exploredMap.robotReal.getOrientation();
        Orientation dirToCheck;

        dirToCheck = Orientation.getNextOrientation(origDir);                    // right turn
        if (canCalibrateOnTheSpot(dirToCheck)) return dirToCheck;

        dirToCheck = Orientation.getPreviousOrientation(origDir);                // left turn
        if (canCalibrateOnTheSpot(dirToCheck)) return dirToCheck;

        dirToCheck = Orientation.getPreviousOrientation(dirToCheck);             // u turn
        if (canCalibrateOnTheSpot(dirToCheck)) return dirToCheck;

        return null;
    }

    /**
     * Turns the bot in the needed direction and sends the CALIBRATE movement. Once calibrated, the bot is turned back
     * to its original direction.
     */
    private void calibrateBot(Orientation targetDir) {
        Orientation origDir = exploredMap.robotReal.getOrientation();

        turnBotDirection(targetDir);
        moveBot(MOVEMENT.CALIBRATE);
        turnBotDirection(origDir);
    }

    /**
     * Turns the robot to the required direction.
     */
    private void turnBotDirection(Orientation targetDir) {
        int numOfTurn = Math.abs(Orientation.ORIENTATION_MAPPINGS.get(exploredMap.robotReal.getOrientation()) - Orientation.ORIENTATION_MAPPINGS.get(targetDir));
        if (numOfTurn > 2) numOfTurn = numOfTurn % 2;

        if (numOfTurn == 1) {
            if (Orientation.getNextOrientation(exploredMap.robotReal.getOrientation()) == targetDir) {
                moveBot(MOVEMENT.RIGHT_TURN);
            } else {
                moveBot(MOVEMENT.LEFT_TURN);
            }
        } else if (numOfTurn == 2) {
            moveBot(MOVEMENT.TURN_AROUND);
        }
    }


    private void exploreIslands(){

        Grid curGrid = exploredMap.arena.getGrid(exploredMap.robotReal.getRobotPosRow(), exploredMap.robotReal.getRobotPosCol());
        RobotReal tempRobot = new RobotReal(curGrid);

        IslandExploration islandExploration = new IslandExploration(exploredMap.arena, tempRobot);
        while ((islandExploration.percentExplored < ArenaConstants.MAX_COVERAGE_PERCENTAGE) && (System.currentTimeMillis() < endTime)) {
            exploredMap.robotReal.setPath(islandExploration.getPathtoNearestUnexplored());
            islandExploration.percentExplored = ExplorationUtility.percentExplore(exploredMap.arena);
            String commandString = exploredMap.robotReal.generateMovementCommands(exploredMap.robotReal.getOrientation());
            if (exploredMap.robotReal.getRobotPosRow() != ArenaConstants.START_ROW || exploredMap.robotReal.getRobotPosCol() != ArenaConstants.START_COL) {
                break;
            }
        }
        goHome();
    }

}
