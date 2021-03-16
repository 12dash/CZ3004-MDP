package Exploration;

import Algo.AStar;
import Algo.ExplorationUtility;
import Environment.ArenaConstants;
import Environment.Grid;
import Robot.RobotConstants;
import Values.Orientation;
import Simulator.Map;
import Communication.Communication;
import Communication.CommunicationConstants;
import Robot.RobotConstants.MOVEMENT;
import Robot.RobotReal;

import java.util.ArrayList;
import java.util.Arrays;
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


    public ExplorationAlgo(Map exploredMap, int timeLimit, int coverageLimit, Communication comm) {
        this.exploredMap = exploredMap;
        this.timeLimit = timeLimit;
        this.coverageLimit = coverageLimit;
        this.comm = comm;
        this.simulate = false;
        this.realMap = null;
    }

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

    //TODO: REMEMBER TO HARD STOP 6 MINUTES
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

        // Sends command to android to start exploration: E
        if (!simulate) Communication.getCommunication().sendMsg(CommunicationConstants.ARDUINO, CommunicationConstants.START_EPLORATION);

        senseAndRepaint(this.simulate);
        areaExplored = calculateAreaExplored();

        explorationLoop(exploredMap.robotReal.getRobotPosRow(), exploredMap.robotReal.getRobotPosCol());
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
            if (exploredMap.robotReal.getRobotPosRow() == r && exploredMap.robotReal.getRobotPosCol() == c) {
                break; // TODO: Breaks after reaching home
//                if (areaExplored >= coverageLimit) {
//                    break;
//                }
            }
        } while (areaExplored <= coverageLimit && System.currentTimeMillis() <= endTime && !comm.isTaskFinish());
        System.out.println("FINISHED TASK");
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
        if (simulate) {
            TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
            exploredMap.robotReal.moveSimulate(m);
        } else {
            exploredMap.robotReal.move(m);
        }

        exploredMap.repaint(); // Repaints after each movement
        senseAndRepaint(simulate); // Waits for sense and then repaints immediately

        calibrateRobot();
        clickPicture();

        if(numMoves > RobotConstants.NUM_MOVES_AFTER_CLICK_PICTURE){
            if(!simulate) turnAroundAndClickPicture();
            else simulateTurnAroundAndClickPictures();

            numMoves = 0;
            System.out.println("Clicked Random Pictures at: (" + exploredMap.robotReal.getRobotPosCol() + "," + (ArenaConstants.ARENA_ROWS - exploredMap.robotReal.getRobotPosRow() -1) + ")");
        }

        else numMoves++ ;

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

        return calibrationBlockInFront >= 2;
    }

    /**
     * Turns the robot to the required direction.
     */
    private void turnBotDirection(Orientation targetDir) throws InterruptedException {
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


    private void exploreIslands() throws InterruptedException {

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


    // Returns the coods of right block if can click picture, else [-1,-1]
    public int[] shouldClickPicture(){
        int botRow = exploredMap.robotReal.getRobotPosRow();
        int botCol = exploredMap.robotReal.getRobotPosCol();

        int[] cood = new int[]{-1, -1};

        switch (exploredMap.robotReal.getOrientation()) {
            case North:
                if(exploredMap.arena.areValidCoordinates(botRow + 1, botCol + 1) && exploredMap.arena.getGrid(botRow + 1, botCol + 1).isObstacle()){
                    cood = new int[]{botRow+1 , botCol+1 };
                }
                else if (exploredMap.arena.areValidCoordinates(botRow, botCol + 1) && exploredMap.arena.getGrid(botRow, botCol + 1).isObstacle()){
                    cood = new int[]{botRow, botCol+1};
                }
                break;

            case East:
                if(exploredMap.arena.areValidCoordinates(botRow + 1, botCol - 1) && exploredMap.arena.getGrid(botRow + 1, botCol - 1).isObstacle()){
                    cood = new int[]{botRow+1 ,botCol-1};
                }
                else if (exploredMap.arena.areValidCoordinates(botRow + 1, botCol) && exploredMap.arena.getGrid(botRow + 1, botCol).isObstacle()){
                    cood = new int[]{botRow+1 ,botCol};
                }
                break;

            case South:
                if(exploredMap.arena.areValidCoordinates(botRow - 1, botCol - 1) && exploredMap.arena.getGrid(botRow - 1, botCol -1).isObstacle()){
                    cood = new int[]{botRow-1 , botCol-1 };
                }
                else if(exploredMap.arena.areValidCoordinates(botRow, botCol - 1) && exploredMap.arena.getGrid(botRow, botCol - 1).isObstacle()){
                    cood = new int[]{botRow, botCol-1 };
                }
                break;

            case West:
                if (exploredMap.arena.areValidCoordinates(botRow - 1, botCol + 1) && exploredMap.arena.getGrid(botRow - 1, botCol + 1).isObstacle()) {
                    cood = new int[]{botRow-1, botCol+1 };
                }

                else if (exploredMap.arena.areValidCoordinates(botRow - 1, botCol) && exploredMap.arena.getGrid(botRow - 1, botCol).isObstacle()){
                     cood = new int[]{botRow-1, botCol};
                 }
                break;
        }

        return cood;
    }



    private void clickPicture(){
        int[] blockCood =  shouldClickPicture();

        if(blockCood[0] != -1 && blockCood[1] != -1){
            exploredMap.arena.grids[blockCood[0]][blockCood[1]].setPictureClicked(true);
            blockCood[0] = ArenaConstants.ARENA_ROWS - blockCood[0] - 1;  // Convert to physical coordinate system format
            if(!simulate) {
                comm.clickPictureAndWaitforAcknowledge(blockCood[0], blockCood[1], true);
            }
            System.out.println("Clicked Nearby Picture at: (" + blockCood[0] + "," + blockCood[1] + ")");
        }
    }


    // TODO: Finalise protocol for just clicking picture
    public void turnAroundAndClickPicture() throws InterruptedException {
        exploredMap.robotReal.setClickingRandomPicture(true);

        // 1st TURN
        //-----------

        exploredMap.robotReal.move(MOVEMENT.LEFT_TURN);
        if (canCalibrate(exploredMap.robotReal.getOrientation())){
            comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_FRONT);
            senseAndRepaint(simulate);
        }
        int[] randomCood = getRandomCoordinate();
        comm.clickPictureAndWaitforAcknowledge(randomCood[0], randomCood[1], false);


        // 2nd TURN
        //-----------
        exploredMap.robotReal.move(MOVEMENT.LEFT_TURN);
        if(!simulate && canCalibrate(exploredMap.robotReal.getOrientation())){
            comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_FRONT);
        }
        senseAndRepaint(simulate);

        randomCood = getRandomCoordinate();
        comm.clickPictureAndWaitforAcknowledge(randomCood[0], randomCood[1], false);

        // 3rd TURN
        //-----------

        exploredMap.robotReal.move(MOVEMENT.LEFT_TURN);
        if(!simulate && canCalibrate(exploredMap.robotReal.getOrientation())){
            comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_FRONT);
        }
        senseAndRepaint(simulate);

        randomCood = getRandomCoordinate();
        comm.clickPictureAndWaitforAcknowledge(randomCood[0], randomCood[1], false);

        // 4th TURN
        //----------
        exploredMap.robotReal.move(MOVEMENT.LEFT_TURN);
        senseAndRepaint(simulate);

        exploredMap.robotReal.setClickingRandomPicture(false);

    }

    // return location of the explored free cell near an unclicked obstacle, else return 0;

    public int[] getIslandCell(){

        int i = ArenaConstants.ARENA_ROWS;
        int j = -1;

        while (i != 0 || j != ArenaConstants.ARENA_COLS - 1) {
            if (i > 0) {
                i--;
            }
            if (j < ArenaConstants.ARENA_COLS - 1) {
                j++;
            }

            int[] noNeighbour = new int[]{-1,- 1};

            for (int c = 0; c <= j; c++) {
                if (exploredMap.arena.getGrid(i, c).isObstacle() && !exploredMap.arena.getGrid(i, c).getPictureClicked()) {
                    int[] nearByCell = exploredMap.arena.checkSurroundingExploredAndFree(i,c);
                    if(!Arrays.equals(nearByCell, noNeighbour)){
                        return new int[]{i, c};
                    }
                }
            }

            for (int r = i + 1; r <= ArenaConstants.ARENA_ROWS - 1; r++) {
                if (!exploredMap.arena.getGrid(r, j).getPictureClicked()) {
                    int[] nearByCell = exploredMap.arena.checkSurroundingExploredAndFree(r,j);
                    if(!Arrays.equals(nearByCell, noNeighbour)){
                        return new int[]{r, j};
                    }
                }

            }
        }
        return new int[]{-1, -1};
    }

    public void goToIslandLoop(int[] islandCell){

        // The nearby cell to which the robot can go
        int[] nearByCell = exploredMap.arena.checkSurroundingExploredAndFree(islandCell[0], islandCell[1]);
        Orientation relativeOrientation = exploredMap.arena.getRelativeOrientation(nearByCell[0], nearByCell[1], islandCell[0], islandCell[1]);
        //This realativeOrientaion should be to the right of the bot, so we get the counter-clockwise orientation
        Orientation botFinalOrientaion = Orientation.getPreviousOrientation(relativeOrientation);

        int botX = exploredMap.robotReal.getRobotPosCol();
        int botY = exploredMap.robotReal.getRobotPosRow();
                while(botX != nearByCell[0] && botX != nearByCell[1]){

            AStar astar = new AStar();
            astar.startSearch(exploredMap.arena, exploredMap.arena.getGrid(botX, botY), exploredMap.arena.getGrid(islandCell[0], islandCell[1]), false);
            ArrayList<Grid> path = new ArrayList<>(astar.solution);

            botX = exploredMap.robotReal.getRobotPosCol();
            botY = exploredMap.robotReal.getRobotPosRow();

        }
    }

    private int[] getRandomCoordinate(){
        return new int[]{1, 1};
    }


    private void calibrateRobot() throws InterruptedException {

        exploredMap.robotReal.setCalibMode(true);

        boolean caliFront = canCalibrate(exploredMap.robotReal.getOrientation());
        boolean caliRight = canCalibrate(Orientation.getNextOrientation(exploredMap.robotReal.getOrientation()));
        boolean caliLeft = canCalibrate(Orientation.getPreviousOrientation(exploredMap.robotReal.getOrientation()));

        String cali = "NIL";

        // FOR EVERY STEP

        if(caliFront) {
            cali = CommunicationConstants.CALI_FRONT;
            if (simulate) simulateCalibration(CommunicationConstants.CALI_FRONT);
            else comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_FRONT);

            if (caliRight && lastCalibrate > 1) {
                cali = CommunicationConstants.CALI_RIGHT_FRONT;
                if (simulate) simulateCalibration(CommunicationConstants.CALI_RIGHT_FRONT);
                else comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_RIGHT_FRONT);

            } else if (caliLeft && lastCalibrate > 1) { // So that doesn't calibrate immediately after a previous one
                cali = CommunicationConstants.CALI_LEFT_FRONT;
                if (simulate) simulateCalibration(CommunicationConstants.CALI_LEFT_FRONT);
                else comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_LEFT_FRONT);

            }
        }


        // IF NOT THEN AFTER EVER NUM_MOVES_AFTER_CALIBRATE
        else if(lastCalibrate >= RobotConstants.NUM_MOVES_AFTER_CALIBRATE){

            if(caliRight) {
                cali = CommunicationConstants.CALI_RIGHT;
                if (simulate) simulateCalibration(CommunicationConstants.CALI_RIGHT);
                else comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_RIGHT);
            }

            else if(caliLeft){
                cali = CommunicationConstants.CALI_LEFT;
                if (simulate) simulateCalibration(CommunicationConstants.CALI_LEFT);
                else comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_LEFT);
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

//        Grid robotCurGrid = exploredMap.arena.getGrid(exploredMap.robotReal.getRobotPosRow(), exploredMap.robotReal.getRobotPosCol());
//        Grid homeGrid = exploredMap.arena.getGrid(ArenaConstants.START_ROW, ArenaConstants.START_COL);
//
//        // TODO: Implement got to goal state if haven't
////        if (!exploredMap.robotReal.getTouchedGoal() && coverageLimit == ArenaConstants.MAX_COVERAGE && timeLimit == ArenaConstants.MAX_TIME_LIMIT) {
////            FastestPathAlgo goToGoal = new FastestPathAlgo(exploredMap, bot, realMap);
////            goToGoal.runFastestPath(RobotConstants.GOAL_ROW, RobotConstants.GOAL_COL);
////        }
//
//        AStar astar = new AStar();
//        astar.startSearch(exploredMap.arena, robotCurGrid, homeGrid, false);
//        exploredMap.robotReal.setPath(astar.solution);
//        String commandString = exploredMap.robotReal.generateMovementCommands(exploredMap.robotReal.getOrientation());
//        commandString = CommunicationConstants.GO_HOME + ":" + commandString;      // TODO: Finalise on this protocol
//        comm.sendMsg(CommunicationConstants.ARDUINO, commandString);
//
//        //TODO: Do we need to calibrate after reaching home?
//        turnBotDirection(Orientation.East);
//        comm.sendMsg(CommunicationConstants.ARDUINO, CommunicationConstants.INITIAL_CALIBRATION);
//
////        System.out.println("Exploration complete!");
////        areaExplored = calculateAreaExplored();
////        System.out.printf("%.2f%% Coverage", (areaExplored / 300.0) * 100.0);
////        System.out.println(", " + areaExplored + " Cells");
////        System.out.println((System.currentTimeMillis() - startTime) / 1000 + " Seconds");

    }

//    /**
//     * Turns the bot in the needed direction and sends the CALIBRATE movement. Once calibrated, the bot is turned back
//     * to its original direction.
//     */
//    private void calibrateBot(Orientation targetDir) throws InterruptedException {
//        Orientation origDir = exploredMap.robotReal.getOrientation();
//
//        turnBotDirection(targetDir);
////        moveBot(MOVEMENT.CALIBRATE);
//        turnBotDirection(origDir);
//    }


    public void simulateTurnAroundAndClickPictures() throws InterruptedException {

        exploredMap.robotReal.setClickingRandomPicture(true);

        // 1st TURN
        //-----------

        exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
        senseAndRepaint(simulate);
        TimeUnit.MILLISECONDS.sleep(2 * RobotConstants.SPEED);

        // 2nd TURN
        //-----------
        exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
        senseAndRepaint(simulate);
        TimeUnit.MILLISECONDS.sleep(2 * RobotConstants.SPEED);

        // 3rd TURN
        //-----------
        exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
        senseAndRepaint(simulate);
        TimeUnit.MILLISECONDS.sleep(2 * RobotConstants.SPEED);

        // 4th TURN
        //-----------
        exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
        senseAndRepaint(simulate);
        TimeUnit.MILLISECONDS.sleep(2 * RobotConstants.SPEED);

        exploredMap.robotReal.setClickingRandomPicture(false);
    }

}



