package Environment;

import Values.*;


public class Arena {

    public Grid[][] grids;

    public Arena() {
        grids = new Grid[Constants.ROWS][Constants.COLUMNS];
    }

    public void initializeArena() {
        for (int i = 0; i < Constants.ROWS; i++) {
            for (int j = 0; j < Constants.COLUMNS; j++) {
                this.grids[i][j] = new Grid(Type.FREE, true, j, i); //Grids(Type type, boolean acc, int x , int y)
            }
        }
    }

    public void checkCellModifyAcc(int i, int j) {
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
                    checkCellModifyAcc(i, j);
                    addNeighbourPadding(i, j);
                }
            }
        }
        getBorderPadding();
    }

    public void make_arena(int[][] temp) {
        initializeArena();
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
