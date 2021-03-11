package Environment;

import Values.*;


/**
 * Class which encapsulates the Arena data Structure.
 * Consists of a class variable called Grids which stores individual grid.
 */

public class Arena {

    public Grid[][] grids;

    public Arena(boolean setExplored) {
        /*
        Constructor for making an Arena and initializing it
         */
        grids = new Grid[ArenaConstants.ARENA_ROWS][ArenaConstants.ARENA_COLS];
        initializeArena(setExplored);//Initialize Arena to the default Values
    }

    public void initializeArena(boolean setExplored) {
        /*
        Initialize the arena setting the grids as :
            1. Type = Free
            2. Accessible = True
            3. Explored = False
         */
        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                //Grids(Type type, boolean acc, int x , int y)
                this.grids[i][j] = new Grid(Type.FREE, true, j, i, setExplored);
            }
        }

        //Add the border to the padding.
        addBorderPadding();
        markStartNGoalZoneAsExplored();
    }

    public void checkCellModifyAcc(int i, int j) {
        /*
        Method to check if the position is a valid position in the arena and then change the acc to false
         */
        if (((i >= 0) && (i < ArenaConstants.ARENA_COLS)) && ((j >= 0) && (j < ArenaConstants.ARENA_ROWS))) {
            if (this.grids[j][i].getAcc()) this.grids[j][i].setAcc(false);
        }
    }

    public void markStartNGoalZoneAsExplored(){

        //Start Zone
        for (int r = ArenaConstants.ARENA_ROWS-1; r >= ArenaConstants.ARENA_ROWS - 3; r-- ) { // from rows 17 to 19
            for (int c  = 0; c <=2; c ++){
                this.grids[r][c].isExplored();
            }
        }

        //Goal Zone
        for (int r = 0; r <=2; r ++){
            for (int c = ArenaConstants.ARENA_COLS-3; c <=ArenaConstants.ARENA_COLS-1; c++){
                this.grids[r][c].isExplored();
            }
        }


    }


    public void addNeighbourPadding(int i, int j) {
        /*
        Used to provide the padding around the border.
         */

        //i is row
        //j is column

        int x_1 = j - 1;//Left
        int x_2 = j + 1;//Right

        int y_1 = i - 1;//Up
        int y_2 = i + 1;//Down

        checkCellModifyAcc(j, y_1);//Left
        checkCellModifyAcc(j, y_2);//Right

        checkCellModifyAcc(x_1, i);//Up
        checkCellModifyAcc(x_2, i);//Down

        checkCellModifyAcc(x_1, y_1);//Up - Left
        checkCellModifyAcc(x_2, y_1);//Up - Right

        checkCellModifyAcc(x_1, y_2);//Down - Left
        checkCellModifyAcc(x_2, y_2);//Down - Right
    }

    public void addBorderPadding() {
        /*
        Method for padding the borders of the arena.
         */
        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                if ((i == 0) || (i == ArenaConstants.ARENA_ROWS - 1) || (j == 0) || (j == ArenaConstants.ARENA_COLS - 1)) {
                    this.grids[i][j].setAcc(false);
                }
            }
        }
    }

    public void addPadding() {
        /*
        General method which initiates the padding around all the obstacles.
         */
        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                if (this.grids[i][j].getType() == Type.OBSTACLE) {
                    this.grids[i][j].setAcc(false);
                    addNeighbourPadding(i, j);
                }
            }
        }
    }

    public void make_arena(int[][] temp) {
        /*
        Sets the obstacle that is read from the P2 String after conversion from Map Descriptor.
         */
        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                if (temp[i][j] == 1) {
                    this.grids[i][j].setType(Type.OBSTACLE);
                    this.grids[i][j].setAcc(false);
                }
            }
        }
        addPadding();
    }

    public void setUnexplored(){
        /*
        Sets all the grids into unexplored
         */
        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                    this.grids[i][j].setExplored(false);
            }
        }
    }

    public void setExplored(){
        /*
        Sets all the grids into explored
         */
        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                this.grids[i][j].setExplored(true);
            }
        }
    }

    /**
     * Returns true if the row and column values are valid.
     */
    public boolean areValidCoordinates(int row, int col) {
        return row >= 0 && col >= 0 && row < ArenaConstants.ARENA_ROWS && col < ArenaConstants.ARENA_COLS;
    }

    public Grid getGrid(int row, int col){
        return (this.grids[row][col]);
    }

    public void setGridType(int row, int col, Type type){
        this.grids[row][col].setType(type);
    }

    public void setGridExplored(int row, int col, boolean explored){
        this.grids[row][col].setExplored(explored);
    }


    private boolean inStartZone(int row, int col) {
        return (row >= ArenaConstants.START_ROW - 1 && row <= ArenaConstants.START_ROW + 1 && col >= ArenaConstants.START_COL - 1 && col <= ArenaConstants.START_COL + 1);
    }

    private boolean inGoalZone(int row, int col) {
        return (row >= ArenaConstants.GOAL_ROW - 1 && row <= ArenaConstants.GOAL_ROW + 1 && col >= ArenaConstants.GOAL_COL - 1 && col <= ArenaConstants.GOAL_COL + 1);
    }

    // Removes padding around the obstacles apart from the padding near border of the arena
    private void removePaddingFromNonObstacles(){
        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                if (this.grids[i][j].isExplored() && !this.grids[i][j].isObstacle()){
                    this.grids[i][j].setAcc(true);
                }
            }
        }
        addBorderPadding();
    }


    public void addPaddingToExploredCells() {
        /*
        General method which initiates the padding around all the obstacles.
         */
        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                if (this.grids[i][j].isExplored() && this.grids[i][j].getType() == Type.OBSTACLE) {
                    this.grids[i][j].setAcc(false);
                    addNeighbourPadding(i, j);
                }
            }
        }
    }


    /**
     * Sets a cell as an obstacle and the surrounding cells as virtual walls or resets the cell and surrounding
     * virtual walls.
     */
    public void setObstacleCell(int row, int col, boolean obstacle) {

        if ((inStartZone(row, col) || inGoalZone(row, col)))
            return;

        if (obstacle) {
            this.grids[row][col].setType(Type.OBSTACLE);
            removePaddingFromNonObstacles();
            addPaddingToExploredCells();
        }
        else{
            this.grids[row][col].setType(Type.FREE);
            removePaddingFromNonObstacles();
            addPaddingToExploredCells();
        }
    }

    /**
     * Returns true if the given cell is out of bounds or an obstacle.
     */
    public boolean getIsObstacleOrWall(int row, int col) {
        return !areValidCoordinates(row, col) || this.grids[row][col].isObstacle();
    }


}
