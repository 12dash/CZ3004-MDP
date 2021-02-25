package Environment;

import Values.*;

/**
 * Class which encapsulates the Arena data Structure.
 * Consists of a class variable called Grids which stores individual grid.
 */

public class Arena {

    public Grid[][] grids;

    public Arena() {
        /*
        Constructor for making an Arena and initializing it
         */
        grids = new Grid[ArenaConstants.ARENA_ROWS][ArenaConstants.ARENA_COLS];
        initializeArena();//Initialize Arena to the default Values
    }

    public void initializeArena() {
        /*
        Initialize the arena setting the grids as :
            1. Type = Free
            2. Accessible = True
            3. Explored = False
         */
        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                //Grids(Type type, boolean acc, int x , int y)
                this.grids[i][j] = new Grid(Type.FREE, true, j, i);
            }
        }

        //Add the border to the padding.
        getBorderPadding();
    }

    public void checkCellModifyAcc(int i, int j) {
        /*
        Method to check if the position is a valid position in the arena and then change the acc to false
         */
        if (((i >= 0) && (i < ArenaConstants.ARENA_COLS)) && ((j >= 0) && (j < ArenaConstants.ARENA_ROWS))) {
            if (this.grids[j][i].getAcc()) this.grids[j][i].setAcc(false);
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

    public void getBorderPadding() {
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
        General method which initiates the padding around the obstacles.
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

}
