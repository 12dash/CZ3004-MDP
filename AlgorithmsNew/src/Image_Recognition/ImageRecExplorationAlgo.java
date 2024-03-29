//package Image_Recognition;
//
//import Algo.AStar;
//import Communication.Communication;
//import Communication.CommunicationConstants;
//import Environment.ArenaConstants;
//import Environment.Grid;
//import Robot.RobotConstants;
//import Robot.RobotConstants.MOVEMENT;
//import Simulator.Map;
//import Values.Orientation;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Random;
//import java.util.concurrent.TimeUnit;
//
//
//public class ImageRecExplorationAlgo {
//    private final Map exploredMap;
//    private final Map realMap;
//    private final boolean simulate;
//
//    private final int timeLimit;
//    private final int coverageLimit;
//    private int areaExplored;
//    private long startTime;
//    private long endTime;
//    private int lastCalibrate;
//    private boolean calibrationMode = false;
//    private Communication comm;
//    private int numMoves = 0;
//    private boolean exploreLoop = true;
//
//    private boolean bottomLeft = false;
//    private boolean bottomRight = false;
//    private boolean middleRight = false;
//    private boolean topRight= false;
//    private boolean topLeft = false;
//    private boolean middleLeft = false;
//
//    private int leftC = 5;
//    private int rightC = 11;
//    private int middleC = 10;
//
//    int loopNo = 0;
//
//    private ArrayList<Grid> lastTenGrids = new ArrayList<>();
//    private HashMap<Grid, Integer> counter = new HashMap<>();
//
//
//
//    public ImageRecExplorationAlgo(Map exploredMap, int timeLimit, int coverageLimit, Communication comm) {
//        this.exploredMap = exploredMap;
//        this.timeLimit = timeLimit;
//        this.coverageLimit = coverageLimit;
//        this.comm = comm;
//        this.simulate = false;
//        this.realMap = null;
//    }
//
//    public ImageRecExplorationAlgo(Map exploredMap, Map realMap, int timeLimit, int coverageLimit) {
//        this.exploredMap = exploredMap;
//        this.timeLimit = timeLimit;
//        this.coverageLimit = coverageLimit;
//        this.realMap = realMap;
//        this.simulate = true;
//        this.comm = Communication.getCommunication();
//    }
//
//    /**
//     * Main method that is called to start the exploration.
//     */
//
//    //TODO: REMEMBER TO HARD STOP 6 MINUTES
//    public void initialCalibration() throws InterruptedException {
//        System.out.println("Calibrated at (1, 1)");
//        comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_RIGHT);
//    }
//
//
//    public void runExploration() throws InterruptedException {
//
//        // ##########################################
//        //         START EXPLORATION
//        // ###########################################
//
//        System.out.println("\nStarting exploration...");
//
//        startTime = System.currentTimeMillis();
//        endTime = startTime + (timeLimit * 1000);
//
//        // Sends command to android to start exploration: E
//        if (!simulate) Communication.getCommunication().sendMsg(CommunicationConstants.ARDUINO, CommunicationConstants.START_EPLORATION);
//        senseAndRepaint(this.simulate);
//
//        explore();
//
//    }
//
//    public void explore() throws InterruptedException {
//
//        while(System.currentTimeMillis() <= endTime && !comm.isTaskFinish()){
//            if(exploreLoop){
//                explorationLoop(exploredMap.robotReal.getRobotPosRow(), exploredMap.robotReal.getRobotPosCol());
//                exploreLoop = false;
//                loopNo ++;
//            }
//            else{
//                /**
//                * Looks for a cell nearby to an island, tries to go to it
//                * If reaches successfully, sets exploreLoop to true
//                * else will just run again in next loop (cause exploreLoop is false)
//                * returns -1  if no more islands left
//                */
//
//                 if (goToIslandLoop() == -1){
//                     System.out.println("EXPLORATION COMPLETE: NO MORE ISLANDS LEFT");
//                     break;
//                 }
//            }
//        }
//
//        if(System.currentTimeMillis() >= endTime){
//            System.out.println("FINISHED TASK: TIMES UP");
//        }
//        else if(comm.isTaskFinish()){
//            System.out.println("ALL 5 IMAGES TAKEN");
//        }
//
//        if(!simulate) comm.sendMsg(CommunicationConstants.IR, CommunicationConstants.IR_TASK_COMPLETE);
//
//    }
//
//    /**
//     * Loops through robot movements until one (or more) of the following conditions is met:
//     * 1. Robot is back at (r, c)
//     * 2. areaExplored > coverageLimit
//     * 3. System.currentTimeMillis() > endTime
//     */
//    private void explorationLoop(int r, int c) throws InterruptedException {
//        clickPicture();
//        do {
//            nextMove();
//            getOutOfLoop();
//            if (exploredMap.robotReal.getRobotPosRow() == r && exploredMap.robotReal.getRobotPosCol() == c) {
//                break;
//            }
//        } while (System.currentTimeMillis() <= endTime && !comm.isTaskFinish());
//
//        System.out.println("FINISHED LOOP");
//        exploredMap.repaint();
//    }
//
//    /**
//     * Determines the next move for the robot and executes it accordingly.
//     */
//    private void nextMove() throws InterruptedException {
//        if (lookRight()) {
//            moveBot(MOVEMENT.RIGHT_TURN);
//            if (lookForward()) moveBot(MOVEMENT.FORWARD);
//        } else if (lookForward()) {
//            moveBot(MOVEMENT.FORWARD);
//        } else if (lookLeft()) {
//            moveBot(MOVEMENT.LEFT_TURN);
//            if (lookForward()) moveBot(MOVEMENT.FORWARD);
//        } else {
//            moveBot(MOVEMENT.TURN_AROUND);
//        }
//    }
//
//    /**
//     * Moves the bot, repaints the map and calls senseAndRepaint().
//     */
//    private void moveBot(MOVEMENT m) throws InterruptedException {
//        if (simulate) {
//            TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
//            exploredMap.robotReal.moveSimulate(m);
//        } else {
//            exploredMap.robotReal.move(m);
//        }
//
//        exploredMap.repaint(); // Repaints after each movement
//        senseAndRepaint(simulate); // Waits for sense and then repaints immediately
//
//        calibrateRobot();
//        takePicture();
//
//        if(loopNo > 0) {
//            clickPicture();
//        }
//
////        if(numMoves > RobotConstants.NUM_MOVES_AFTER_CLICK_PICTURE){
////            if(!simulate) turnAroundAndClickPicture();
////            else simulateTurnAroundAndClickPictures();
////
////            numMoves = 0;
////            System.out.println("Clicked Random Pictures at: (" + exploredMap.robotReal.getRobotPosCol() + "," + (ArenaConstants.ARENA_ROWS - exploredMap.robotReal.getRobotPosRow() -1) + ")");
////        }
////
////        else numMoves++ ;
//
//
//    }
//
//    /**
//     * Sets the bot's sensors, processes the sensor data and repaints the map.
//     */
//    public void senseAndRepaint(boolean simulate) {
//        exploredMap.robotReal.setSensors();
//        if(simulate){
//            exploredMap.robotReal.senseSimulate(exploredMap, realMap);
//        }
//        else {
//            exploredMap.robotReal.senseMap(exploredMap);
//        }
//        exploredMap.repaint();
//    }
//
//    /**
//     * Checks if the robot can calibrate at its current position given a direction.
//     */
//    private boolean canCalibrate(Orientation botDir) {
//        int row = exploredMap.robotReal.getRobotPosRow();
//        int col = exploredMap.robotReal.getRobotPosCol();
//
//        int  calibrationBlockInFront = 0;
//        switch (botDir) {
//            case North:
//                if(exploredMap.arena.getIsObstacleOrWall(row - 2, col - 1)) calibrationBlockInFront++ ;
//                if(exploredMap.arena.getIsObstacleOrWall(row - 2, col)) calibrationBlockInFront++ ;
//                if(exploredMap.arena.getIsObstacleOrWall(row - 2, col + 1)) calibrationBlockInFront++ ;
//                break;
//            case East:
//                if (exploredMap.arena.getIsObstacleOrWall(row + 1, col + 2)) calibrationBlockInFront++;
//                if (exploredMap.arena.getIsObstacleOrWall(row, col + 2)) calibrationBlockInFront++;
//                if (exploredMap.arena.getIsObstacleOrWall(row - 1, col + 2)) calibrationBlockInFront++;
//                break;
//            case South:
//                if (exploredMap.arena.getIsObstacleOrWall(row + 2, col - 1)) calibrationBlockInFront++;
//                if (exploredMap.arena.getIsObstacleOrWall(row + 2, col)) calibrationBlockInFront++;
//                if (exploredMap.arena.getIsObstacleOrWall(row + 2, col + 1)) calibrationBlockInFront++;
//                break;
//            case West:
//                if (exploredMap.arena.getIsObstacleOrWall(row + 1, col - 2)) calibrationBlockInFront++;
//                if (exploredMap.arena.getIsObstacleOrWall(row, col - 2)) calibrationBlockInFront++;
//                if (exploredMap.arena.getIsObstacleOrWall(row - 1, col - 2)) calibrationBlockInFront++;
//                break;
//        }
//
//        return calibrationBlockInFront >= 3;
//    }
//
//    /**
//     * Turns the robot to the required direction.
//     */
//    private MOVEMENT turnBotDirection(Orientation targetDir) {
//        int numOfTurn = Math.abs(Orientation.ORIENTATION_MAPPINGS.get(exploredMap.robotReal.getOrientation()) - Orientation.ORIENTATION_MAPPINGS.get(targetDir));
//        if (numOfTurn > 2) numOfTurn = numOfTurn % 2;
//
//        if (numOfTurn == 1) {
//            if (Orientation.getNextOrientation(exploredMap.robotReal.getOrientation()) == targetDir) {
//
//                return MOVEMENT.RIGHT_TURN;
//            } else {
//                return MOVEMENT.LEFT_TURN;
//            }
//        } else if (numOfTurn == 2) {
//            return MOVEMENT.TURN_AROUND;
//        }
//        return MOVEMENT.FORWARD;
//    }
//
//
////    private void exploreIslands() throws InterruptedException {
////
////        Grid curGrid = exploredMap.arena.getGrid(exploredMap.robotReal.getRobotPosRow(), exploredMap.robotReal.getRobotPosCol());
////        RobotReal tempRobot = new RobotReal(curGrid);
////
////        IslandExploration islandExploration = new IslandExploration(exploredMap.arena, tempRobot);
////        while ((islandExploration.percentExplored < ArenaConstants.MAX_COVERAGE_PERCENTAGE) && (System.currentTimeMillis() < endTime)) {
////            exploredMap.robotReal.setPath(islandExploration.getPathtoNearestUnexplored());
////            islandExploration.percentExplored = ExplorationUtility.percentExplore(exploredMap.arena);
////            String commandString = exploredMap.robotReal.generateMovementCommands(exploredMap.robotReal.getOrientation());
////            if (exploredMap.robotReal.getRobotPosRow() != ArenaConstants.START_ROW || exploredMap.robotReal.getRobotPosCol() != ArenaConstants.START_COL) {
////                break;
////            }
////        }
////        goHome();
////    }
//
//
//    // Returns the coods of right block if can click picture, else [-1,-1]
//    public int[] shouldClickPicture(){
//        int botRow = exploredMap.robotReal.getRobotPosRow();
//        int botCol = exploredMap.robotReal.getRobotPosCol();
//
//        int[] cood = new int[]{-1, -1};
//
//        switch (exploredMap.robotReal.getOrientation()) {
//            case North:
//                if(exploredMap.arena.areValidCoordinates(botRow + 1, botCol + 2) && exploredMap.arena.getGrid(botRow + 1, botCol + 2).isObstacle()){
//                    cood = new int[]{botRow+1 , botCol+2 };
//                }
//                else if (exploredMap.arena.areValidCoordinates(botRow, botCol + 2) && exploredMap.arena.getGrid(botRow, botCol + 2).isObstacle()){
//                    cood = new int[]{botRow, botCol+2};
//                }
//                break;
//
//            case East:
//                if(exploredMap.arena.areValidCoordinates(botRow + 2, botCol - 1) && exploredMap.arena.getGrid(botRow + 2, botCol - 1).isObstacle()){
//                    cood = new int[]{botRow+2 ,botCol-1};
//                }
//                else if (exploredMap.arena.areValidCoordinates(botRow + 2, botCol) && exploredMap.arena.getGrid(botRow + 2, botCol).isObstacle()){
//                    cood = new int[]{botRow+2 ,botCol};
//                }
//                break;
//
//            case South:
//                if(exploredMap.arena.areValidCoordinates(botRow - 1, botCol - 2) && exploredMap.arena.getGrid(botRow - 1, botCol -2).isObstacle()){
//                    cood = new int[]{botRow-1 , botCol-2 };
//                }
//                else if(exploredMap.arena.areValidCoordinates(botRow, botCol - 2) && exploredMap.arena.getGrid(botRow, botCol - 2).isObstacle()){
//                    cood = new int[]{botRow, botCol-2 };
//                }
//                break;
//
//            case West:
//                if (exploredMap.arena.areValidCoordinates(botRow - 2, botCol + 1) && exploredMap.arena.getGrid(botRow - 2, botCol + 1).isObstacle()) {
//                    cood = new int[]{botRow-2, botCol+1 };
//                }
//
//                else if (exploredMap.arena.areValidCoordinates(botRow - 2, botCol) && exploredMap.arena.getGrid(botRow - 2, botCol).isObstacle()){
//                     cood = new int[]{botRow-2, botCol};
//                 }
//                break;
//        }
//
//        return cood;
//    }
//
//
//    private void clickPicture(){
//        int[] blockCood =  shouldClickPicture();
//
//        if(blockCood[0] != -1 && blockCood[1] != -1){
//            exploredMap.arena.grids[blockCood[0]][blockCood[1]].setPictureClicked(true);
//            blockCood[0] = ArenaConstants.ARENA_ROWS - blockCood[0] - 1;  // Convert to physical coordinate system format
//            if(!simulate) {
//                comm.clickPictureAndWaitforAcknowledge(blockCood[1],  blockCood[0], true);
//            }
//            System.out.println("Clicked Nearby Picture of: (" + blockCood[1] + "," + blockCood[0] + ")");
//        }
//    }
//
//
//    public void turnAroundAndClickPicture() throws InterruptedException {
//        exploredMap.robotReal.setClickingRandomPicture(true);
//
//        // 1st TURN
//        //-----------
//
//        exploredMap.robotReal.move(MOVEMENT.LEFT_TURN);
//        senseAndRepaint(simulate);
//        if (canCalibrate(exploredMap.robotReal.getOrientation())){
//            comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_FRONT);
//            lastCalibrate =0;
//        }
//        int[] randomCood = getRandomCoordinate();
//        comm.clickPictureAndWaitforAcknowledge(randomCood[0], randomCood[1], false);
//
//        if(System.currentTimeMillis() > endTime || comm.isTaskFinish()) {
//            return;
//        }
//
//        // 2nd TURN
//        //-----------
//        exploredMap.robotReal.move(MOVEMENT.LEFT_TURN);
//        lastCalibrate++;
//        senseAndRepaint(simulate);
//        if(!simulate && canCalibrate(exploredMap.robotReal.getOrientation())){
//            comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_FRONT);
//            lastCalibrate = 0;
//        }
//
//        if(System.currentTimeMillis() > endTime || comm.isTaskFinish()) {
//            return;
//        }
//
//        randomCood = getRandomCoordinate();
//        comm.clickPictureAndWaitforAcknowledge(randomCood[0], randomCood[1], false);
//
//        // 3rd TURN
//        //-----------
//
//        exploredMap.robotReal.move(MOVEMENT.LEFT_TURN);
//        senseAndRepaint(simulate);
//        if(!simulate && canCalibrate(exploredMap.robotReal.getOrientation())){
//            comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_FRONT);
//        }
//
//        if(System.currentTimeMillis() > endTime || comm.isTaskFinish()) {
//            return;
//        }
//
//        randomCood = getRandomCoordinate();
//        comm.clickPictureAndWaitforAcknowledge(randomCood[0], randomCood[1], false);
//
//        // 4th TURN
//        //----------
//        exploredMap.robotReal.move(MOVEMENT.LEFT_TURN);
//        lastCalibrate++;
//        senseAndRepaint(simulate);
//
//        exploredMap.robotReal.setClickingRandomPicture(false);
//
//    }
//
//    // return location of the explored free cell near an unclicked obstacle, else return 0;
//
//    public int[][] getIslandCellAndNearbyCell(){
//
//        int i = ArenaConstants.ARENA_ROWS;
//        int j = -1;
//        int[] noNeighbour = new int[]{-1,- 1};
//        while (i != 0 || j != ArenaConstants.ARENA_COLS - 1) {
//            if (i > 0) {
//                i--;
//            }
//            if (j < ArenaConstants.ARENA_COLS - 1) {
//                j++;
//            }
//
//            for (int c = 0; c <= j; c++) {
//                if (exploredMap.arena.getGrid(i, c).isObstacle() && !exploredMap.arena.getGrid(i, c).getPictureClicked()) {
//                    int[] nearByCell = exploredMap.arena.checkSurroundingExploredAndFree(i,c);
//                    if(!Arrays.equals(nearByCell, noNeighbour)){
//                        return new int[][]{new int[]{i, c}, nearByCell};
//                    }
//                }
//            }
//
//            for (int r = i + 1; r <= ArenaConstants.ARENA_ROWS - 1; r++) {
//                if (exploredMap.arena.getGrid(r, j).isObstacle() && !exploredMap.arena.getGrid(r, j).getPictureClicked()) {
//                    int[] nearByCell = exploredMap.arena.checkSurroundingExploredAndFree(r,j);
//                    if(!Arrays.equals(nearByCell, noNeighbour)){
//                        return new int[][]{new int[] {r, j}, nearByCell};
//                    }
//                }
//
//            }
//        }
//        return new int[][]{noNeighbour, noNeighbour};
//    }
//
//    public int goToIslandLoop() throws InterruptedException {
//
//        // The nearby cell to which the robot can go
//        int[][] cells = getIslandCellAndNearbyCell();
//        int[] islandCell = cells[0];
//        int[] nearByCell = cells[1];
//
//        if (islandCell[0] != -1 && islandCell[1] != -1 && nearByCell[0] != -1 && nearByCell[1] != -1) {
//            Orientation relativeOrientation = exploredMap.arena.getRelativeOrientation(nearByCell[1], nearByCell[0], islandCell[1], islandCell[0]);
//            //This realativeOrientaion should be to the right of the bot, so we get the counter-clockwise orientation
//            Orientation botFinalOrientaion = Orientation.getPreviousOrientation(relativeOrientation);
////            System.out.println(botFinalOrientaion);
//            int botX = exploredMap.robotReal.getRobotPosCol();
//            int botY = exploredMap.robotReal.getRobotPosRow();
//
////            System.out.println(botX + ", " + botY);
////            System.out.println(nearByCell[1] + ", " + nearByCell[0]);
//
//            // The map might change while the robot is moving
////            System.out.println(exploredMap.arena.getGrid(nearByCell[0], nearByCell[1]).getAcc());
////            System.out.println(exploredMap.arena.getGrid(islandCell[0], islandCell[1]).isObstacle());
////            System.out.println(botY != nearByCell[0]);
////            System.out.println(botX != nearByCell[1]);
////            System.out.println(System.currentTimeMillis() <= endTime);
////            System.out.println(!comm.isTaskFinish());
//            while (exploredMap.arena.getGrid(nearByCell[0], nearByCell[1]).getAcc() && exploredMap.arena.getGrid(islandCell[0], islandCell[1]).isObstacle() && (botY != nearByCell[0] || botX != nearByCell[1]) && System.currentTimeMillis() <= endTime && !comm.isTaskFinish()) {
//
//                AStar astar = new AStar();
//
//                astar.startSearch(exploredMap.arena, exploredMap.arena.getGrid(botY, botX), exploredMap.arena.getGrid(nearByCell[0], nearByCell[1]), false);
//                ArrayList<Grid> path = new ArrayList<>(astar.solution);
//
//                System.out.println(path.get(1).getX() + ", " + path.get(1).getY() );
//
//                if (simulate) {
//                    TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
//                    exploredMap.robotReal.moveSimulate(getNextMove(path.get(1)));
//                } else {
//                    exploredMap.robotReal.move(getNextMove(path.get(1)));
//                }
//
//                senseAndRepaint(simulate);
//                botX = exploredMap.robotReal.getRobotPosCol();
//                botY = exploredMap.robotReal.getRobotPosRow();
//                calibrateRobot();
//            }
//
//            if (exploredMap.robotReal.getRobotPosRow() == nearByCell[0] && exploredMap.robotReal.getRobotPosCol() == nearByCell[1]) {
//                if (!exploredMap.robotReal.getOrientation().equals(botFinalOrientaion)) {
//                    MOVEMENT m = turnBotDirection(botFinalOrientaion);
//
//                    if (simulate) {
//                        TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
//                        exploredMap.robotReal.moveSimulate(m);
//                    } else {
//                        exploredMap.robotReal.move(m);
//                    }
//
//                    senseAndRepaint(simulate);
//                }
//                exploreLoop = true;
//            }
//            return 0;
//        }
//
//        else{
//            return -1;
//        }
//
//    }
//
//    private int[] getRandomCoordinate(){
//        Random r = new Random();
//        int x = r.nextInt(15);
//        int y = r.nextInt(20);
//
//        return new int[]{x, y};
//
////        switch (exploredMap.robotReal.getOrientation()){
////            case North:
////                int min_left = Math.max(0, exploredMap.robotReal.getRobotPosCol()-2);
////                int max_left = Math.min(ArenaConstants.ARENA_COLS-1, exploredMap.robotReal.getRobotPosCol()+2);
////                int bottom = exploredMap.robotReal.getRobotPosRow();
////                int top = 0;
////
////                for(int r = bottom; r >= top; r --){
////                    for(int c = min_left; c <= max_left; c++){
////                        if(exploredMap.arena.getGrid(r, c).isObstacle()){
////                            return new int[]{}
////                        }
////                    })
////                }
////        }
////////        int[] cood = new int[]{1, 1};
//////
////        int front_close = 0;
////        int front_far = 0;
////        int side_left = 0;
////        int side_right = 0;
////        switch (exploredMap.robotReal.getOrientation()) {
////            case North:
////                front_close = exploredMap.robotReal.getRobotPosRow() - 2;
////                front_far = 0;
////                side_left = 0;
////                side_right = ArenaConstants.ARENA_COLS -1;
////
//////                int y = front_close;
//////                int x_left = 0;
//////                int x_right = 0;
//////                int botX  = exploredMap.robotReal.getRobotPosCol();
//////
////                for (int i = front_close; i >= front_far; i--){
////                    for(int j = side_left; j < side_t; j++){
////                        exploredMap.arena.getGrid(i, j).isObstacle(){
////                            return new int[]{j, Arei}
////                        }
////                    }
////                }
////
////                while(y <= front_far){
////
////                    if (exploredMap.arena.getGrid(botX - x_left, y).isObstacle()){
////                        return new int[]{botX - x_left, y};
////                    }
////                    if (exploredMap.arena.getGrid(botX+x_right, y).isObstacle()){
////                        return new int[]{botX+x_right, y};
////                    }
////                    y ++;
////                    if ( botX - x_left > side_left){
////                        x_left++;
////                    }
////                    if(botX + x_left > side_left){
////                        x
////                    }
////                }
////
////
////                break;
////
////            case East:
////                front_close = exploredMap.robotReal.getRobotPosCol() + 2;
////                front_far = ArenaConstants.ARENA_COLS - 1;
////                side_left = 0;
////                side_right = ArenaConstants.ARENA_ROWS -1;
////                break;
////
////            case South:
////                front_close = exploredMap.robotReal.getRobotPosRow() + 2;
////                front_far = ArenaConstants.ARENA_ROWS - 1;
////                side_left = ArenaConstants.ARENA_COLS - 1;
////                side_right = 0;
////                break;
////
////            case West:
////                front_close = exploredMap.robotReal.getRobotPosCol() - 2;
////                front_far = 0;
////                side_left = ArenaConstants.ARENA_ROWS - 1;
////                side_right = 0;
////                break;
////
////        }
////
////        switch(exploredMap.robotReal.getOrientation()){
////            case North:
////                int
////        }
////
////        if(exploredMap.arena.getGrid())
////
////        int y = front_close;
////        int x_left = 1;
////        int x_right = 1;
////
////        while(y <= front_far){
////
////            exploredMap.arena.getGrid()
////        }
//
//
//    }
//
//
//    private void calibrateRobot() throws InterruptedException {
//
//        exploredMap.robotReal.setCalibMode(true);
//
//        boolean caliFront = canCalibrate(exploredMap.robotReal.getOrientation());
//        boolean caliRight = canCalibrate(Orientation.getNextOrientation(exploredMap.robotReal.getOrientation()));
//        boolean caliLeft = canCalibrate(Orientation.getPreviousOrientation(exploredMap.robotReal.getOrientation()));
//
//        String cali = "NIL";
//
//        // FOR EVERY STEP
//
//        if(caliFront && caliRight){
//            cali = CommunicationConstants.CALI_RIGHT_FRONT;
//            if (simulate) simulateCalibration(CommunicationConstants.CALI_RIGHT_FRONT);
//            else comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_RIGHT_FRONT);
//
//        }
//        else if(caliFront && caliLeft){
//            cali = CommunicationConstants.CALI_LEFT_FRONT;
//            if (simulate) simulateCalibration(CommunicationConstants.CALI_LEFT_FRONT);
//            else comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_LEFT_FRONT);
//        }
//
//
//        else if(caliFront) {
//            cali = CommunicationConstants.CALI_FRONT;
//            if (simulate) simulateCalibration(CommunicationConstants.CALI_FRONT);
//            else comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_FRONT);
//        }
//
//
//        // IF NOT THEN AFTER EVER NUM_MOVES_AFTER_CALIBRATE
//        else if(lastCalibrate >= RobotConstants.NUM_MOVES_AFTER_CALIBRATE){
//
//            if(caliRight) {
//                cali = CommunicationConstants.CALI_RIGHT;
//                if (simulate) simulateCalibration(CommunicationConstants.CALI_RIGHT);
//                else comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_RIGHT);
//            }
//
//            else if(caliLeft){
//                cali = CommunicationConstants.CALI_LEFT;
//                if (simulate) simulateCalibration(CommunicationConstants.CALI_LEFT);
//                else comm.sendCalibrationAndWaitForAcknowledge(CommunicationConstants.CALI_LEFT);
//            }
//
//        }
//
//        if(cali.equals("NIL")){
//            lastCalibrate ++;
//        }
//        else
//        {
//            lastCalibrate = 0;
//            // FOR LOGGING
//            int botX = exploredMap.robotReal.getRobotPosCol();
//            int botY = ArenaConstants.ARENA_ROWS - exploredMap.robotReal.getRobotPosRow() -1;
//            System.out.println(cali + " at (" + botX + "," + botY + ")" );
//        }
//
//        exploredMap.robotReal.setCalibMode(false);
//    }
//
//    public void simulateCalibration(String calib) throws InterruptedException {
//        switch(calib){
//            case CommunicationConstants.CALI_FRONT:
//                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
//                break;
//
//            case CommunicationConstants.CALI_RIGHT:
//                exploredMap.robotReal.moveSimulate(MOVEMENT.RIGHT_TURN);
//                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
//                exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
//                break;
//            case CommunicationConstants.CALI_LEFT:
//                exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
//                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
//                exploredMap.robotReal.moveSimulate(MOVEMENT.RIGHT_TURN);
//                break;
//            case CommunicationConstants.CALI_RIGHT_FRONT:
//                exploredMap.robotReal.moveSimulate(MOVEMENT.RIGHT_TURN);
//                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
//                exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
//                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
//                break;
//
//            case CommunicationConstants.CALI_LEFT_FRONT:
//                exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
//                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
//                exploredMap.robotReal.moveSimulate(MOVEMENT.RIGHT_TURN);
//                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
//                break;
//
//            default:
//                break;
//        }
//    }
//
//
//    /**
//     * Returns true if the right side of the robot is free to move into.
//     */
//    private boolean lookRight() {
//        switch (exploredMap.robotReal.getOrientation()) {
//            case North:
//                return eastFree();
//            case East:
//                return southFree();
//            case South:
//                return westFree();
//            case West:
//                return northFree();
//        }
//        return false;
//    }
//
//    /**
//     * Returns true if the robot is free to move forward.
//     */
//    private boolean lookForward() {
//        switch (exploredMap.robotReal.getOrientation()) {
//            case North:
//                return northFree();
//            case East:
//                return eastFree();
//            case South:
//                return southFree();
//            case West:
//                return westFree();
//        }
//        return false;
//    }
//
//    /**
//     * * Returns true if the left side of the robot is free to move into.
//     */
//    private boolean lookLeft() {
//        switch (exploredMap.robotReal.getOrientation()) {
//            case North:
//                return westFree();
//            case East:
//                return northFree();
//            case South:
//                return eastFree();
//            case West:
//                return southFree();
//        }
//        return false;
//    }
//
//    /**
//     * Returns true if the robot can move to the north cell.
//     */
//    private boolean northFree() {
//        int botRow = exploredMap.robotReal.getRobotPosRow();
//        int botCol = exploredMap.robotReal.getRobotPosCol();
//        return (isExploredNotObstacle(botRow - 1, botCol - 1) && isExploredAndFree(botRow - 1, botCol) && isExploredNotObstacle(botRow - 1, botCol + 1));
//    }
//
//    /**
//     * Returns true if the robot can move to the east cell.
//     */
//    private boolean eastFree() {
//        int botRow = exploredMap.robotReal.getRobotPosRow();
//        int botCol = exploredMap.robotReal.getRobotPosCol();
//        return (isExploredNotObstacle(botRow - 1, botCol + 1) && isExploredAndFree(botRow, botCol + 1) && isExploredNotObstacle(botRow + 1, botCol + 1));
//    }
//
//    /**
//     * Returns true if the robot can move to the south cell.
//     */
//    private boolean southFree() {
//        int botRow = exploredMap.robotReal.getRobotPosRow();
//        int botCol = exploredMap.robotReal.getRobotPosCol();
//        return (isExploredNotObstacle(botRow + 1, botCol - 1) && isExploredAndFree(botRow + 1, botCol) && isExploredNotObstacle(botRow + 1, botCol + 1));
//    }
//
//    /**
//     * Returns true if the robot can move to the west cell.
//     */
//    private boolean westFree() {
//        int botRow = exploredMap.robotReal.getRobotPosRow();
//        int botCol = exploredMap.robotReal.getRobotPosCol();
//        return (isExploredNotObstacle(botRow - 1, botCol - 1) && isExploredAndFree(botRow, botCol - 1) && isExploredNotObstacle(botRow + 1, botCol - 1));
//    }
//
//
//    /**
//     * Returns true for cells that are explored and not obstacles.
//     */
//    private boolean isExploredNotObstacle(int r, int c) {
//        if (exploredMap.arena.areValidCoordinates(r, c)) {
//            Grid tmp = exploredMap.arena.getGrid(r, c);
//            return (tmp.isExplored() && !tmp.isObstacle());
//        }
//        return false;
//    }
//
//    /**
//     * Returns true for cells that are explored, not virtual walls and not obstacles.
//     */
//    private boolean isExploredAndFree(int r, int c) {
//        if (exploredMap.arena.areValidCoordinates(r, c)) {
//            Grid b = exploredMap.arena.getGrid(r, c);
//            return (b.isExplored() && b.getAcc() && !b.isObstacle());
//        }
//        return false;
//    }
//
//    /**
//     * Returns the number of cells explored in the grid.
//     */
//    private int calculateAreaExplored() {
//        int result = 0;
//        for (int r = 0; r < ArenaConstants.ARENA_ROWS; r++) {
//            for (int c = 0; c < ArenaConstants.ARENA_COLS; c++) {
//                if (exploredMap.arena.getGrid(r, c).isExplored()) {
//                    result++;
//                }
//            }
//        }
//        return result;
//    }
//
//
////    /**
////     * Returns a possible direction for robot calibration or null, otherwise.
////     */
////    private Orientation getCalibrationDirection() {
////        Orientation origDir = exploredMap.robotReal.getOrientation();
////        Orientation dirToCheck;
////
////        dirToCheck = Orientation.getNextOrientation(origDir);                    // right turn
////        if (canCalibrate(dirToCheck)) return dirToCheck;
////
////        dirToCheck = Orientation.getPreviousOrientation(origDir);                // left turn
////        if (canCalibrate(dirToCheck)) return dirToCheck;
////
////        dirToCheck = Orientation.getPreviousOrientation(dirToCheck);             // u turn
////        if (canCalibrate(dirToCheck)) return dirToCheck;
////
////        return null;
////    }
//
//    /**
//     * Returns the robot to START after exploration and points the bot northwards.
//     */
//
//    // TODO: Right code for go to home; Tell arduino to have a flag for go home, after this will give the whole string, and just go to home
//    // Will append the path after that Eg-> GH:0LR2R
//    private void goHome() throws InterruptedException {
//
////        Grid robotCurGrid = exploredMap.arena.getGrid(exploredMap.robotReal.getRobotPosRow(), exploredMap.robotReal.getRobotPosCol());
////        Grid homeGrid = exploredMap.arena.getGrid(ArenaConstants.START_ROW, ArenaConstants.START_COL);
////
////        // TODO: Implement got to goal state if haven't
//////        if (!exploredMap.robotReal.getTouchedGoal() && coverageLimit == ArenaConstants.MAX_COVERAGE && timeLimit == ArenaConstants.MAX_TIME_LIMIT) {
//////            FastestPathAlgo goToGoal = new FastestPathAlgo(exploredMap, bot, realMap);
//////            goToGoal.runFastestPath(RobotConstants.GOAL_ROW, RobotConstants.GOAL_COL);
//////        }
////
////        AStar astar = new AStar();
////        astar.startSearch(exploredMap.arena, robotCurGrid, homeGrid, false);
////        exploredMap.robotReal.setPath(astar.solution);
////        String commandString = exploredMap.robotReal.generateMovementCommands(exploredMap.robotReal.getOrientation());
////        commandString = CommunicationConstants.GO_HOME + ":" + commandString;
////        comm.sendMsg(CommunicationConstants.ARDUINO, commandString);
////
////
////        turnBotDirection(Orientation.East);
////        comm.sendMsg(CommunicationConstants.ARDUINO, CommunicationConstants.INITIAL_CALIBRATION);
////
//////        System.out.println("Exploration complete!");
//////        areaExplored = calculateAreaExplored();
//////        System.out.printf("%.2f%% Coverage", (areaExplored / 300.0) * 100.0);
//////        System.out.println(", " + areaExplored + " Cells");
//////        System.out.println((System.currentTimeMillis() - startTime) / 1000 + " Seconds");
//
//    }
//
////    /**
////     * Turns the bot in the needed direction and sends the CALIBRATE movement. Once calibrated, the bot is turned back
////     * to its original direction.
////     */
////    private void calibrateBot(Orientation targetDir) throws InterruptedException {
////        Orientation origDir = exploredMap.robotReal.getOrientation();
////
////        turnBotDirection(targetDir);
//////        moveBot(MOVEMENT.CALIBRATE);
////        turnBotDirection(origDir);
////    }
//
//
//    public void simulateTurnAroundAndClickPictures() throws InterruptedException {
//
//        exploredMap.robotReal.setClickingRandomPicture(true);
//
//        // 1st TURN
//        //-----------
//
//        exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
//        senseAndRepaint(simulate);
//        TimeUnit.MILLISECONDS.sleep(2 * RobotConstants.SPEED);
//
//        // 2nd TURN
//        //-----------
//        exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
//        senseAndRepaint(simulate);
//        TimeUnit.MILLISECONDS.sleep(2 * RobotConstants.SPEED);
//
//        // 3rd TURN
//        //-----------
//        exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
//        senseAndRepaint(simulate);
//        TimeUnit.MILLISECONDS.sleep(2 * RobotConstants.SPEED);
//
//        // 4th TURN
//        //-----------
//        exploredMap.robotReal.moveSimulate(MOVEMENT.LEFT_TURN);
//        senseAndRepaint(simulate);
//        TimeUnit.MILLISECONDS.sleep(2 * RobotConstants.SPEED);
//
//        exploredMap.robotReal.setClickingRandomPicture(false);
//    }
//
//    public MOVEMENT getNextMove(Grid nextGrid) throws InterruptedException {
//        int botX = exploredMap.robotReal.getRobotPosCol();
//        int botY = exploredMap.robotReal.getRobotPosRow();
//        Orientation relativeOrientaion = exploredMap.arena.getRelativeOrientation(botX, botY, nextGrid.getX(), nextGrid.getY());
//        return turnBotDirection(relativeOrientaion);
//    }
//
//    public void debugAcc(){
//        for(int i = 0; i< ArenaConstants.ARENA_ROWS ; i++){
//            for(int j = 0; j<ArenaConstants.ARENA_COLS; j++){
//                if(exploredMap.arena.getGrid(i, j).getAcc())
//                    System.out.print("T ");
//                else System.out.print("F ");
//            }
//            System.out.println();
//        }
//    }
//
//    public void debugObst(){
//        for(int i = 0; i< ArenaConstants.ARENA_ROWS ; i++){
//            for(int j = 0; j<ArenaConstants.ARENA_COLS; j++){
//                if(exploredMap.arena.getGrid(i, j).isObstacle())
//                    System.out.print("T ");
//                else System.out.print("F ");
//            }
//            System.out.println();
//        }
//
//    }
//
//    public void debugExplored() {
//        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
//            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
//                if (exploredMap.arena.getGrid(i, j).isExplored())
//                    System.out.print("O ");
//                else System.out.print(". ");
//            }
//            System.out.println();
//        }
//    }
//
//    public void getOutOfLoop() {
//        int botRow = exploredMap.robotReal.getRobotPosRow();
//        int botCol = exploredMap.robotReal.getRobotPosCol();
//
//        Grid botGrid = exploredMap.arena.getGrid(botRow, botCol);
//
//        if(counter.containsKey(botGrid)){
//            counter.replace(botGrid, counter.get(botGrid)+1);
//            if(counter.get(botGrid) > 2){
//                findRightWall();
//            }
//        }
//
//        else {
//            if (lastTenGrids.size() < 10) {
//                lastTenGrids.add(botGrid);
//                counter.put(botGrid, 0);
//            } else {
//                counter.remove(lastTenGrids.get(0));
//                lastTenGrids.remove(0);
//                lastTenGrids.add(botGrid);
//                counter.put(botGrid, 0);
//                }
//            }
//    }
//
//    public void findRightWall() {
//
//            exploredMap.robotReal.move(turnBotDirection(getClosestWall()));
//            while(lookForward() && System.currentTimeMillis() <= endTime && !comm.isTaskFinish()){
//                exploredMap.robotReal.move(MOVEMENT.FORWARD);
//                exploredMap.robotReal.sendMovement(MOVEMENT.print(MOVEMENT.FORWARD));
//                senseAndRepaint(simulate);
//            }
//            exploredMap.robotReal.move(MOVEMENT.LEFT_TURN);
//            exploredMap.robotReal.sendMovement(MOVEMENT.print(MOVEMENT.LEFT_TURN));
//            senseAndRepaint(simulate);
//    }
//
//    public Orientation getClosestWall(){
//        int botX = exploredMap.robotReal.getRobotPosCol();
//        int botY = exploredMap.robotReal.getRobotPosRow();
//
//        int eastWall  = ArenaConstants.ARENA_COLS - botX;
//        int westWall  = botX;
//        int northWall = botY;
//        int southWall = ArenaConstants.ARENA_ROWS - botY;
//
//        int closestWall = Math.min(Math.min(Math.min(eastWall, westWall), northWall), southWall);
//
//        if(closestWall == eastWall) return Orientation.East;
//        if(closestWall == southWall) return Orientation.South;
//        if(closestWall == westWall) return Orientation.West;
//        return Orientation.North;
//    }
//
//
//    //Quadrants:
//    // 1 2
//    // 3 4
//    public int getQuadrant(){
//
//        int botX = exploredMap.robotReal.getRobotPosCol();
//        int botY = exploredMap.robotReal.getRobotPosRow();
//
//        if (botY < 10){
//            if(botX < 7){
//                return 1;
//            }
//            else{
//                return 2;
//            }
//        }
//
//        else{
//            if(botX > 7){
//                return 3;
//            }
//            else{
//                return 4;
//            }
//        }
//    }
//
//    public void takePicture() throws InterruptedException {
//        int botX = exploredMap.robotReal.getRobotPosCol();
//        int botY = exploredMap.robotReal.getRobotPosRow();
//
//        if(!bottomLeft && botX == leftC && botY > middleC){
//            if(!simulate) turnAroundAndClickPicture();
//            else simulateTurnAroundAndClickPictures();
//            bottomLeft = true;
//        }
//
//        else if(!bottomRight && botX == rightC && botY > middleC){
//            if(!simulate) turnAroundAndClickPicture();
//            else simulateTurnAroundAndClickPictures();
//            bottomRight = true;
//        }
//        else if(!middleRight && botY == middleC && (botX > (leftC+rightC/2))){
//            if(!simulate) turnAroundAndClickPicture();
//            else simulateTurnAroundAndClickPictures();
//            middleRight = true;
//        }
//        else if(!topRight && botX == rightC && botY < 3){
//            if(!simulate) turnAroundAndClickPicture();
//            else simulateTurnAroundAndClickPictures();
//            topRight = true;
//        }
//
//        else if(!topLeft && botX == leftC && botY < middleC){
//            if(!simulate) turnAroundAndClickPicture();
//            else simulateTurnAroundAndClickPictures();
//            topLeft = true;
//        }
//
//        else if(!middleLeft && botY == middleC && (botX < (leftC+rightC/2))){
//            if(!simulate) turnAroundAndClickPicture();
//            else simulateTurnAroundAndClickPictures();
//            middleLeft = true;
//        }
//    }
//
//}
//
//
//
//
