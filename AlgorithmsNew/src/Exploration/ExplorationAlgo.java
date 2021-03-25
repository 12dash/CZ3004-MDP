package Exploration;

import Algo.AStar;
import Algo.ExplorationUtility;
import Environment.Arena;
import Environment.ArenaConstants;
import Environment.Grid;
import Robot.RobotConstants;
import Values.Orientation;
import Simulator.Map;
import Communication.Communication;
import Communication.CommunicationConstants;
import Robot.RobotConstants.MOVEMENT;
import Robot.RobotReal;
import javafx.scene.shape.MoveTo;

import javax.annotation.processing.SupportedSourceVersion;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
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
    private int numMoves = 0;
    private boolean exploreLoop = true;


    private ArrayList<Grid> lastTenGrids = new ArrayList<>();
    private HashMap<Grid, Integer> counter = new HashMap<>();


// SIMULATE FALSE
    public ExplorationAlgo(Map exploredMap, int timeLimit, int coverageLimit, Communication comm) {
        this.exploredMap = exploredMap;
        this.timeLimit = timeLimit;
        this.coverageLimit = coverageLimit;
        this.comm = comm;
        this.simulate = false;
        this.realMap = null;
    }

// SIMULATE TRUE
    public ExplorationAlgo(Map exploredMap, Map realMap, int timeLimit, int coverageLimit) {
        this.exploredMap = exploredMap;
        this.timeLimit = timeLimit;
        this.coverageLimit = coverageLimit;
        this.realMap = realMap;
        this.simulate = true;
        this.comm = Communication.getCommunication();
    }

    /**
     * Main method that is called to start the exploration.
     */

    public void initialCalibration() throws InterruptedException {
        System.out.println("Calibrated at (1, 1)");
        comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_RIGHT);
    }


    public void runExploration() throws InterruptedException {

        // ##########################################
        //         START EXPLORATION
        // ###########################################

        System.out.println("\nStarting exploration...");

        startTime = System.currentTimeMillis();
        endTime = startTime + (timeLimit * 1000);
        System.out.println("----------------------------------------");
        System.out.println("Time left to Go Home: " + (endTime -startTime)/1000 + " secs");
        System.out.println("----------------------------------------");

        // Sends command to android to start exploration: E
        if (!simulate) Communication.getCommunication().sendMsg(CommunicationConstants.ARDUINO, CommunicationConstants.START_EPLORATION);
        senseAndRepaint(this.simulate);
        explore();

    }

    public void explore() throws InterruptedException {

        explorationLoop(exploredMap.robotReal.getRobotPosRow(), exploredMap.robotReal.getRobotPosCol());
            /**
            * Looks for a cell nearby to an island, tries to go to it
            * If reaches successfully, sets exploreLoop to true
            * else will just run again in next loop (cause exploreLoop is false)
            * returns -1  if no more islands left
            */

        if(calculateAreaExplored() < coverageLimit  && System.currentTimeMillis() < endTime) {
            exploreIslands();
        }

        endTime = endTime + ((ArenaConstants.MAX_TIME_LIMIT - ArenaConstants.GO_HOME_TIME_LIMIT)  * 1000);

        long curTime = System.currentTimeMillis();
        System.out.println("----------------------------------------");
        System.out.println("Time left to stop: " + (endTime -curTime)/1000 + " secs");
        System.out.println("----------------------------------------");

        goHome();
        endCalibrations();

        if(System.currentTimeMillis() >= endTime){
            System.out.println("=======================");
            System.out.println("=======================");
            System.out.println("      TIMES UP");
            System.out.println("=======================");
            System.out.println("=======================");
        }

        System.out.println();
        System.out.println("=======================");
        System.out.println("=======================");
        System.out.println(" FINISHED EXPLORATION! ");
        System.out.println("=======================");
        System.out.println("=======================");

    }

    /**
     * Loops through robot movements until one (or more) of the following conditions is met:
     * 1. Robot is back at (r, c)
     * 2. areaExplored > coverageLimit
     * 3. System.currentTimeMillis() > endTime
     */
    private void explorationLoop(int r, int c) throws InterruptedException {
        do {
            long curTime = System.currentTimeMillis();
            System.out.println("----------------------------------------");
            System.out.println("Time left to Go Home: " + (endTime - curTime)/1000 + " secs");
            System.out.println("----------------------------------------");
            nextMove();

            if (exploredMap.robotReal.getRobotPosRow() == r && exploredMap.robotReal.getRobotPosCol() == c) {
                break;
            }
        } while (System.currentTimeMillis() < endTime && !comm.isTaskFinish());

        System.out.println("-------------------------");
        System.out.println("FINISHED RIGHT WALL LOOP");
        System.out.println("-------------------------");
        exploredMap.repaint();


    }

    /**
     * Determines the next move for the robot and executes it accordingly.
     */
    private void nextMove() throws InterruptedException {
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
     * Moves the bot, repaints the map and calls senseAndRepaint().
     */
    private void moveBot(MOVEMENT m) throws InterruptedException {
        String moveString = "";
        String cali = calibrateRobot();
        if(!cali.equals("NIL")){
            moveString+=cali;
        }

        moveString+=MOVEMENT.print(m);

        if (simulate) {
            TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
            exploredMap.robotReal.moveSimulate(m);

        } else {
            exploredMap.robotReal.move(m);
            exploredMap.robotReal.sendMovement(moveString);
        }

        exploredMap.repaint(); // Repaints after each movement
        senseAndRepaint(simulate); // Waits for sense and then repaints immediately

        if(System.currentTimeMillis() <= endTime) getOutOfLoop();
        if(System.currentTimeMillis() <= endTime) check_stuck_at_one_place();
    }

    /**
     * Sets the bot's sensors, processes the sensor data and repaints the map.
     */
    public void senseAndRepaint(boolean simulate) {
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
    private boolean canCalibrate(Orientation botDir) {
        int row = exploredMap.robotReal.getRobotPosRow();
        int col = exploredMap.robotReal.getRobotPosCol();

        int  calibrationBlockInFront = 0;
        switch (botDir) {
            case North:
                if(exploredMap.arena.getIsObstacleOrWall(row - 2, col - 1)) calibrationBlockInFront++ ;
                if(exploredMap.arena.getIsObstacleOrWall(row - 2, col)) calibrationBlockInFront++ ;
                if(exploredMap.arena.getIsObstacleOrWall(row - 2, col + 1)) calibrationBlockInFront++ ;
                break;
            case East:
                if (exploredMap.arena.getIsObstacleOrWall(row + 1, col + 2)) calibrationBlockInFront++;
                if (exploredMap.arena.getIsObstacleOrWall(row, col + 2)) calibrationBlockInFront++;
                if (exploredMap.arena.getIsObstacleOrWall(row - 1, col + 2)) calibrationBlockInFront++;
                break;
            case South:
                if (exploredMap.arena.getIsObstacleOrWall(row + 2, col - 1)) calibrationBlockInFront++;
                if (exploredMap.arena.getIsObstacleOrWall(row + 2, col)) calibrationBlockInFront++;
                if (exploredMap.arena.getIsObstacleOrWall(row + 2, col + 1)) calibrationBlockInFront++;
                break;
            case West:
                if (exploredMap.arena.getIsObstacleOrWall(row + 1, col - 2)) calibrationBlockInFront++;
                if (exploredMap.arena.getIsObstacleOrWall(row, col - 2)) calibrationBlockInFront++;
                if (exploredMap.arena.getIsObstacleOrWall(row - 1, col - 2)) calibrationBlockInFront++;
                break;
        }

        return calibrationBlockInFront >= 3;
    }

    /**
     * Turns the robot to the required direction.
     */
    private MOVEMENT turnBotDirection(Orientation targetDir) throws InterruptedException {
        int numOfTurn = Math.abs(Orientation.ORIENTATION_MAPPINGS.get(exploredMap.robotReal.getOrientation()) - Orientation.ORIENTATION_MAPPINGS.get(targetDir));
        if (numOfTurn > 2) numOfTurn = numOfTurn % 2;

        if (numOfTurn == 1) {
            if (Orientation.getNextOrientation(exploredMap.robotReal.getOrientation()) == targetDir) {

                return MOVEMENT.RIGHT_TURN;
            } else {
                return MOVEMENT.LEFT_TURN;
            }
        } else if (numOfTurn == 2) {
            return MOVEMENT.TURN_AROUND;
        }
        return MOVEMENT.FORWARD;
    }


    private void exploreIslands() throws InterruptedException {
        System.out.println("-------------------------");
        System.out.println(" STARTED ISLAND EXPLORATION ");
        System.out.println("-------------------------");

        while (calculateAreaExplored() < coverageLimit &&  System.currentTimeMillis() < endTime) {
            long curTime = System.currentTimeMillis();
            System.out.println("----------------------------------------");
            System.out.println("Time left to Go Home: " + (endTime - curTime)/1000 + " secs");
            System.out.println("----------------------------------------");

            Grid curGrid = exploredMap.arena.getGrid(exploredMap.robotReal.getRobotPosRow(), exploredMap.robotReal.getRobotPosCol());
            RobotReal tempRobot = new RobotReal(curGrid);
            IslandExploration islandExploration = new IslandExploration(exploredMap.arena, tempRobot);
            ArrayList<Grid> path = islandExploration.getPathtoNearestUnexplored();

            if(path.size() > 1) {
                boolean straight = true;
                int cur = 1;
                while (straight && cur < path.size() && calculateAreaExplored() < coverageLimit &&  System.currentTimeMillis() < endTime) {
                    MOVEMENT m = getNextMove(path.get(cur));
                    if(!m.equals(MOVEMENT.FORWARD)){
                        straight = false;
                    }
                    moveBot(m);
                    cur++;

                    curTime = System.currentTimeMillis();
                    System.out.println("----------------------------------------");
                    System.out.println("Time left to Go Home: " + (endTime - curTime)/1000 + " secs");
                    System.out.println("----------------------------------------");
                }
            }
        }

    }

    private String calibrateRobot() throws InterruptedException {

        exploredMap.robotReal.setCalibMode(true);

        boolean caliFront = canCalibrate(exploredMap.robotReal.getOrientation());
        boolean caliRight = canCalibrate(Orientation.getNextOrientation(exploredMap.robotReal.getOrientation()));
        boolean caliLeft = canCalibrate(Orientation.getPreviousOrientation(exploredMap.robotReal.getOrientation()));

        String cali = "NIL";

        // FOR EVERY STEP

        if(caliFront && caliRight){
            cali = CommunicationConstants.CALI_RIGHT_FRONT;
            if (simulate) simulateCalibration(CommunicationConstants.CALI_RIGHT_FRONT);

        }
        else if(caliFront && caliLeft){
            cali = CommunicationConstants.CALI_LEFT_FRONT;
            if (simulate) simulateCalibration(CommunicationConstants.CALI_LEFT_FRONT);
        }


        else if(caliFront) {
            cali = CommunicationConstants.CALI_FRONT;
            if (simulate) simulateCalibration(CommunicationConstants.CALI_FRONT);
        }


        // IF NOT THEN AFTER EVER NUM_MOVES_AFTER_CALIBRATE
        else if(lastCalibrate >= RobotConstants.NUM_MOVES_AFTER_CALIBRATE){

            if(caliRight) {
                cali = CommunicationConstants.CALI_RIGHT;
                if (simulate) simulateCalibration(CommunicationConstants.CALI_RIGHT);
            }

            else if(caliLeft){
                cali = CommunicationConstants.CALI_LEFT;
                if (simulate) simulateCalibration(CommunicationConstants.CALI_LEFT);
            }

        }

        if(cali.equals("NIL")){
            lastCalibrate ++;
        }
        else
        {
            lastCalibrate = 0;
            // FOR LOGGING
            int botX = exploredMap.robotReal.getRobotPosCol();
            int botY = ArenaConstants.ARENA_ROWS - exploredMap.robotReal.getRobotPosRow() -1;
            System.out.println(cali + " at (" + botX + "," + botY + ")" );
        }
        exploredMap.robotReal.setCalibMode(false);
        return cali;
    }

    public void simulateCalibration(String calib) throws InterruptedException {
        switch(calib){
            case CommunicationConstants.CALI_FRONT:
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
                break;

            case CommunicationConstants.CALI_RIGHT:
                exploredMap.robotReal.moveSimulate(MOVEMENT.RIGHT_TURN);
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
                exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
                break;
            case CommunicationConstants.CALI_LEFT:
                exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
                exploredMap.robotReal.moveSimulate(MOVEMENT.RIGHT_TURN);
                break;
            case CommunicationConstants.CALI_RIGHT_FRONT:
                exploredMap.robotReal.moveSimulate(MOVEMENT.RIGHT_TURN);
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
                exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
                break;

            case CommunicationConstants.CALI_LEFT_FRONT:
                exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
                exploredMap.robotReal.moveSimulate(MOVEMENT.RIGHT_TURN);
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
                break;

            default:
                break;
        }
    }

    public void getOutOfLoop() throws InterruptedException {
        int botRow = exploredMap.robotReal.getRobotPosRow();
        int botCol = exploredMap.robotReal.getRobotPosCol();

        Grid botGrid = exploredMap.arena.getGrid(botRow, botCol);

        if(counter.containsKey(botGrid)){
            counter.replace(botGrid, counter.get(botGrid)+1);
            if(counter.get(botGrid) > 3){
                System.out.println("=======================");
                System.out.println("DETECTED LOOP: TRYING TO GET OUT");
                System.out.println("=======================");

                findRightWall();

                for(Grid g : counter.keySet()){
                    counter.replace(g, 0);
                }
            }
        }

        else {
            if (lastTenGrids.size() < 10) {
                lastTenGrids.add(botGrid);
                counter.put(botGrid, 0);
            } else {
                counter.remove(lastTenGrids.get(0));
                lastTenGrids.remove(0);
                lastTenGrids.add(botGrid);
                counter.put(botGrid, 0);
            }
        }
    }

    public void findRightWall() throws InterruptedException {

        MOVEMENT m = turnBotDirection(getClosestWall());

        exploredMap.robotReal.move(m);
        if(!simulate) exploredMap.robotReal.sendMovement(MOVEMENT.print(m));
        exploredMap.repaint();
        senseAndRepaint(simulate);

        while(lookForward() && System.currentTimeMillis() <= endTime && !comm.isTaskFinish()){
            exploredMap.robotReal.move(MOVEMENT.FORWARD);
            if(!simulate) exploredMap.robotReal.sendMovement(MOVEMENT.print(MOVEMENT.FORWARD));
            exploredMap.repaint();
            senseAndRepaint(simulate);
        }

        exploredMap.robotReal.move(MOVEMENT.LEFT_TURN);
        if(!simulate) exploredMap.robotReal.sendMovement(MOVEMENT.print(MOVEMENT.LEFT_TURN));
        exploredMap.repaint();
        senseAndRepaint(simulate);

    }

    public Orientation getClosestWall(){
        int botX = exploredMap.robotReal.getRobotPosCol();
        int botY = exploredMap.robotReal.getRobotPosRow();

        int eastWall  = ArenaConstants.ARENA_COLS - botX;
        int westWall  = botX;
        int northWall = botY;
        int southWall = ArenaConstants.ARENA_ROWS - botY;

        int closestWall = Math.min(Math.min(Math.min(eastWall, westWall), northWall), southWall);

        if(closestWall == eastWall) return Orientation.East;
        if(closestWall == southWall) return Orientation.South;
        if(closestWall == westWall) return Orientation.West;
        return Orientation.North;
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
     * Returns true for cells that are explored and not obstacles.
     */
    private boolean isExploredNotObstacle(int r, int c) {
        if (exploredMap.arena.areValidCoordinates(r, c)) {
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
            return (b.isExplored() && b.getAcc() && !b.isObstacle());
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
                if (exploredMap.arena.getGrid(r, c).isExplored()) {
                    result++;
                }
            }
        }
        return result;
    }


//    /**
//     * Returns a possible direction for robot calibration or null, otherwise.
//     */
//    private Orientation getCalibrationDirection() {
//        Orientation origDir = exploredMap.robotReal.getOrientation();
//        Orientation dirToCheck;
//
//        dirToCheck = Orientation.getNextOrientation(origDir);                    // right turn
//        if (canCalibrate(dirToCheck)) return dirToCheck;
//
//        dirToCheck = Orientation.getPreviousOrientation(origDir);                // left turn
//        if (canCalibrate(dirToCheck)) return dirToCheck;
//
//        dirToCheck = Orientation.getPreviousOrientation(dirToCheck);             // u turn
//        if (canCalibrate(dirToCheck)) return dirToCheck;
//
//        return null;
//    }

    /**
     * Returns the robot to START after exploration and points the bot northwards.
     */

    // TODO: Right code for go to home; Tell arduino to have a flag for go home, after this will give the whole string, and just go to home
    // Will append the path after that Eg-> GH:0LR2R
    private void goHome() throws InterruptedException {

        System.out.println("-------------------------");
        System.out.println("        GOING HOME");
        System.out.println("-------------------------");

        int bot_X = exploredMap.robotReal.getRobotPosCol();
        int bot_Y = exploredMap.robotReal.getRobotPosRow();

        while (System.currentTimeMillis() < endTime && (bot_X != 1 || bot_Y != 18)) {

            long curTime = System.currentTimeMillis();
            System.out.println("----------------------------------------");
            System.out.println("Time left to Stop: " + (endTime -curTime)/1000 + " secs");
            System.out.println("----------------------------------------");

            AStar astar = new AStar();
            astar.startSearch(exploredMap.arena, exploredMap.arena.getGrid(bot_Y, bot_X), exploredMap.arena.getGrid(18, 1), false);
            ArrayList<Grid> path = new ArrayList<>(astar.solution);

            if(path.size() > 1) {
                boolean straight = true;
                int cur = 1;
                while (straight && cur < path.size() &&  System.currentTimeMillis() < endTime) {
                    MOVEMENT m = getNextMove(path.get(cur));
                    if(!m.equals(MOVEMENT.FORWARD)){
                        straight = false;
                    }
                    moveBot(m);
                    cur++;
                }
                curTime = System.currentTimeMillis();
                System.out.println("----------------------------------------");
                System.out.println("Time left to Stop: " + (endTime - curTime)/1000 + " secs");
                System.out.println("----------------------------------------");
            }
            else{
               goSouth();
               goWest();
            }

            bot_X = exploredMap.robotReal.getRobotPosCol();
            bot_Y = exploredMap.robotReal.getRobotPosRow();
        }
    }


    public MOVEMENT getNextMove(Grid nextGrid) throws InterruptedException {
        int botX = exploredMap.robotReal.getRobotPosCol();
        int botY = exploredMap.robotReal.getRobotPosRow();
        Orientation relativeOrientaion = exploredMap.arena.getRelativeOrientation(botX, botY, nextGrid.getX(), nextGrid.getY());

        if(relativeOrientaion.equals(exploredMap.robotReal.getOrientation())){
            return MOVEMENT.FORWARD;
        }
        return turnBotDirection(relativeOrientaion);
    }


    public void debugAcc(){
        for(int i = 0; i< ArenaConstants.ARENA_ROWS ; i++){
            for(int j = 0; j<ArenaConstants.ARENA_COLS; j++){
                if(exploredMap.arena.getGrid(i, j).getAcc())
                    System.out.print("T ");
                else System.out.print("F ");
            }
            System.out.println();
        }
    }

    public void debugObst(){
        for(int i = 0; i< ArenaConstants.ARENA_ROWS ; i++){
            for(int j = 0; j<ArenaConstants.ARENA_COLS; j++){
                if(exploredMap.arena.getGrid(i, j).isObstacle())
                    System.out.print("T ");
                else System.out.print("F ");
            }
            System.out.println();
        }

    }

    public void debugExplored() {
        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                if (exploredMap.arena.getGrid(i, j).isExplored())
                    System.out.print("O ");
                else System.out.print(". ");
            }
            System.out.println();
        }
    }

    public void check_stuck_at_one_place() throws InterruptedException {

        if(!northFree() && !eastFree() && !southFree() && !westFree()){

            System.out.println("=======================");
            System.out.println("STUCK IN ONE PLACE: TRYING TO GET OUT");
            System.out.println("=======================");

            if(System.currentTimeMillis() <= endTime) return;

            exploredMap.robotReal.move(MOVEMENT.RIGHT_TURN);
            if(!simulate) exploredMap.robotReal.sendMovement(MOVEMENT.print(MOVEMENT.RIGHT_TURN));
            exploredMap.repaint();
            senseAndRepaint(simulate);


            if(System.currentTimeMillis() <= endTime) return;

            exploredMap.robotReal.move(MOVEMENT.RIGHT_TURN);
            if(!simulate) exploredMap.robotReal.sendMovement(MOVEMENT.print(MOVEMENT.RIGHT_TURN));
            exploredMap.repaint();
            senseAndRepaint(simulate);


            if(System.currentTimeMillis() <= endTime) return;

            exploredMap.robotReal.move(MOVEMENT.RIGHT_TURN);
            if(!simulate) exploredMap.robotReal.sendMovement(MOVEMENT.print(MOVEMENT.RIGHT_TURN));
            exploredMap.repaint();
            senseAndRepaint(simulate);


            if(System.currentTimeMillis() <= endTime) return;

            exploredMap.robotReal.move(MOVEMENT.RIGHT_TURN);
            if(!simulate) exploredMap.robotReal.sendMovement(MOVEMENT.print(MOVEMENT.RIGHT_TURN));
            exploredMap.repaint();
            senseAndRepaint(simulate);

            if(System.currentTimeMillis() <= endTime) return;

            findRightWall();
        }

    }

    public void endCalibrations() throws InterruptedException  {

        if (System.currentTimeMillis() <= endTime) {
            MOVEMENT turn = turnBotDirection(Orientation.South);
            if (!turn.equals(MOVEMENT.FORWARD)) {
                exploredMap.robotReal.move(turn);
                exploredMap.repaint();
                if (!simulate) {
                    exploredMap.robotReal.sendMovement(MOVEMENT.print(turn));
                    comm.recvMsg();
                }
            }
        }

        if(!simulate) {
            exploredMap.robotReal.sendMovement(CommunicationConstants.CALI_FRONT + "E");
            comm.recvMsg();
        }


        if (System.currentTimeMillis() <= endTime) {
            MOVEMENT turn = turnBotDirection(Orientation.West);
            if (!turn.equals(MOVEMENT.FORWARD)) {
                exploredMap.robotReal.move(turn);
                exploredMap.repaint();
                if (!simulate) {
                    exploredMap.robotReal.sendMovement(MOVEMENT.print(turn));
                    comm.recvMsg();
                }
            }
        }

        if(!simulate) {
            exploredMap.robotReal.sendMovement(CommunicationConstants.CALI_FRONT + "E");
            comm.recvMsg();
        }

    }


    public void goSouth() throws InterruptedException {

        MOVEMENT m = turnBotDirection(Orientation.South);

        exploredMap.robotReal.move(m);
        if(!simulate) exploredMap.robotReal.sendMovement(MOVEMENT.print(m));
        exploredMap.repaint();
        senseAndRepaint(simulate);

        while(lookForward() && System.currentTimeMillis() <= endTime && !comm.isTaskFinish()){
            exploredMap.robotReal.move(MOVEMENT.FORWARD);
            if(!simulate) exploredMap.robotReal.sendMovement(MOVEMENT.print(MOVEMENT.FORWARD));
            exploredMap.repaint();
            senseAndRepaint(simulate);
        }

        exploredMap.robotReal.move(MOVEMENT.RIGHT_TURN);
        if(!simulate) exploredMap.robotReal.sendMovement(MOVEMENT.print(MOVEMENT.LEFT_TURN));
        exploredMap.repaint();
        senseAndRepaint(simulate);

    }

    public void goWest() throws InterruptedException {

        MOVEMENT m = turnBotDirection(Orientation.West);

        exploredMap.robotReal.move(m);
        if(!simulate) exploredMap.robotReal.sendMovement(MOVEMENT.print(m));
        exploredMap.repaint();
        senseAndRepaint(simulate);

        while(lookForward() && System.currentTimeMillis() <= endTime && !comm.isTaskFinish()){
            exploredMap.robotReal.move(MOVEMENT.FORWARD);
            if(!simulate) exploredMap.robotReal.sendMovement(MOVEMENT.print(MOVEMENT.FORWARD));
            exploredMap.repaint();
            senseAndRepaint(simulate);
        }

        exploredMap.robotReal.move(MOVEMENT.LEFT_TURN);
        if(!simulate) exploredMap.robotReal.sendMovement(MOVEMENT.print(MOVEMENT.LEFT_TURN));
        exploredMap.repaint();
        senseAndRepaint(simulate);

    }


}




