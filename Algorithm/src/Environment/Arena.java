package Environment;

import Values.*;


public class Arena {

    public Grid[][] grids;

    public Arena() {
        /*
        Constructor for making an Arena and initializing it
         */
        grids = new Grid[Constants.ROWS][Constants.COLUMNS];
        initializeArena();
    }

    public void initializeArena() {
        /*
        Initialize the arena setting the grids as :
            1. Type = Free
            2. Accessible = True
            3. Explored = False
         */
        for (int i = 0; i < Constants.ROWS; i++) {
            for (int j = 0; j < Constants.COLUMNS; j++) {
                //Grids(Type type, boolean acc, int x , int y)
                this.grids[i][j] = new Grid(Type.FREE, true, j, i);
            }
        }
    }

    public void checkCellModifyAcc(int i, int j) {
        /*
        Method to check if the position is a valid position in the arena and then change the acc to false
         */
        if (((i >= 0) && (i < Constants.COLUMNS)) && ((j >= 0) && (j < Constants.ROWS))) {
            if (this.grids[j][i].getAcc()) this.grids[j][i].setAcc(false);
        }
    }

    public void addNeighbourPadding(int i, int j) {
        int x = j;
        int y = i;

        int x_1 = j - 1;
        int x_2 = j + 1;

        int y_1 = i - 1;
        int y_2 = i + 1;

        checkCellModifyAcc(x, y_1);
        checkCellModifyAcc(x, y_2);
        checkCellModifyAcc(x_1, y);
        checkCellModifyAcc(x_2, y);
        checkCellModifyAcc(x_1, y_1);
        checkCellModifyAcc(x_2, y_1);
        checkCellModifyAcc(x_1, y_2);
        checkCellModifyAcc(x_2, y_2);
    }

    public void getBorderPadding() {
        for (int i = 0; i < Constants.ROWS; i++) {
            for (int j = 0; j < Constants.COLUMNS; j++) {
                if ((i == 0) || (i == Constants.ROWS - 1)) {
                    this.grids[i][j].setAcc(false);
                }
                if ((j == 0) || (j == Constants.COLUMNS - 1)) {
                    this.grids[i][j].setAcc(false);
                }
            }
        }
    }

    public void addPadding() {
        for (int i = 0; i < Constants.ROWS; i++) {
            for (int j = 0; j < Constants.COLUMNS; j++) {
                if (this.grids[i][j].getType() == Type.OBSTACLE) {
                    this.grids[i][j].setAcc(false);
                    addNeighbourPadding(i, j);
                }
            }
        }
        //Build the padding around the edges of the arena
        getBorderPadding();
    }

    public void make_arena(int[][] temp) {
        for (int i = 0; i < Constants.ROWS; i++) {
            for (int j = 0; j < Constants.COLUMNS; j++) {
                if (temp[i][j] == 1) {
                    this.grids[i][j].setType(Type.OBSTACLE);
                    this.grids[i][j].setAcc(false);
                }
            }
        }
        addPadding();
    }
}
