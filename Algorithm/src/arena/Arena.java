/*
  The arena size is 20 rows and 15 columns.
  m = 20 --> Rows
  n = 15 --> Columns
 */

package arena;

import values.*;
import robot.Robot;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Arena extends JPanel {

    public int m; //number of rows
    public int n; //number of columns
    public Grid[][] arena;
    public Robot bot;

    public Arena(int m, int n, Robot bot) {
        this.m = m;
        this.n = n;
        this.arena = new Grid[this.m][this.n];
        this.bot = bot;
    }

    public void make_arena() {
        for (int i = 0; i < arena.length; i++) {
            for (int j = 0; j < arena[0].length; j++) {
                this.arena[i][j] = new Grid(Types.FREE, i, j);
            }
        }
    }

    public void update_arena(int[][] temp) {
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                if (temp[i][j] == 1) {
                    this.arena[i][j].setType(Types.OBSTACLE);
                } else {
                    this.arena[i][j].setType(Types.FREE);
                }
            }
        }
    }

    public void get_view() {
        for (int i = 0; i < this.arena.length; i++) {
            System.out.print("|");
            for (int j = 0; j < this.arena[0].length; j++) {
                if (Types.OBSTACLE == this.arena[i][j].getType())
                    System.out.print("X|");
                else {
                    System.out.print(" |");
                }
            }
            System.out.println();
        }
    }

    public void get_acc_view() {
        for (int i = 0; i < this.arena.length; i++) {
            System.out.print("|");
            for (int j = 0; j < this.arena[0].length; j++) {
                if (Acc.FALSE == this.arena[i][j].getAcc())
                    System.out.print("X|");
                else {
                    System.out.print(" |");
                }
            }
            System.out.println();
        }
    }

    public boolean check_acc(ArrayList<Grid> visited, int[] pos) {
        int x = pos[0];
        int y = pos[1];

        if ((x >= 0) && (x < this.n) && (y >= 0) && (y < this.m) && (!visited.contains(this.arena[y][x]))) {
            Grid temp = this.arena[y][x];
            return temp.getAcc() == Acc.TRUE;
        }
        return false;
    }

    public void check_cell_modify_acc(int i, int j) {
        if (((i >= 0) && (i < this.n)) && ((j >= 0) && (j < this.m))) {
            if (this.arena[j][i].getAcc() == Acc.TRUE) {
                this.arena[j][i].setAcc(Acc.FALSE);
            }
        }
    }

    public void add_neighbour_padding(int i, int j) {
        int x = j;
        int y = i;

        int x_1 = j - 1;
        int x_2 = j + 1;

        int y_1 = i - 1;
        int y_2 = i + 1;

        check_cell_modify_acc(x, y_1);
        check_cell_modify_acc(x, y_2);
        check_cell_modify_acc(x_1, y);
        check_cell_modify_acc(x_2, y);
        check_cell_modify_acc(x_1, y_1);
        check_cell_modify_acc(x_2, y_1);
        check_cell_modify_acc(x_1, y_2);
        check_cell_modify_acc(x_2, y_2);

    }

    public void add_padding() {
        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.n; j++) {
                if (this.arena[i][j].getType() == Types.OBSTACLE) {
                    add_neighbour_padding(i, j);
                }
            }
        }
        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.n; j++) {
                if ((i == 0) || (i == this.m - 1)) {
                    this.arena[i][j].setAcc(Acc.FALSE);
                }
                if ((j == 0) || (j == this.n - 1)) {
                    this.arena[i][j].setAcc(Acc.FALSE);
                }
            }
        }
    }

    /**
     * Sets all cells in the grid to an explored state.
     */
    public void setAllExplored() {
        for (int row = 0; row < arena.length; row++) {
            for (int col = 0; col < arena[0].length; col++) {
                arena[row][col].setExplored(true);
            }
        }
    }

    /**
     * Sets all cells in the grid to an unexplored state except for the START & GOAL zone.
     */
    public void setAllUnexplored() {
        for (int row = 0; row < arena.length; row++) {
            for (int col = 0; col < arena[0].length; col++) {
                if (inStartZone(row, col) || inGoalZone(row, col)) {
                    arena[row][col].setExplored(true);
                } else {
                    arena[row][col].setExplored(false);
                }
            }
        }
    }

    private boolean inStartZone(int row, int col) {
        return row >= 0 && row <= 2 && col >= 0 && col <= 2;
    }

    private boolean inGoalZone(int row, int col) {
        return (row <= ArenaConstants.GOAL_ROW + 1 && row >= ArenaConstants.GOAL_ROW - 1 && col <= ArenaConstants.GOAL_COL + 1 && col >= ArenaConstants.GOAL_COL - 1);
    }



    public void display_solution(ArrayList<Grid> path) {

        String[][] solution = new String[this.m][this.n];

        for (int i = 0; i < this.m; i++) {

            for (int j = 0; j < this.n; j++) {

                if (this.arena[i][j].getType() == Types.OBSTACLE)
                    solution[i][j] = "1";
                else {
                    solution[i][j] = " ";
                }
            }
        }

        for (int i = 0; i < path.size(); i++) {
            Grid temp = path.get(i);
            solution[temp.y][temp.x] = "O";
        }
        System.out.println("\nSolution");
        System.out.print("\t");
        for (int j = 0; j < 15; j++) {
            System.out.print(j + "\t|");

        }
        System.out.println();
        for (int i = 0; i < this.m; i++) {
            System.out.print("\t|");
            for (int j = 0; j < this.n; j++) {
                System.out.print(solution[i][j] + "\t|");
            }
            System.out.println(" " + i);
        }
    }


    /**
     * Overrides JComponent's paintComponent() method. It creates a two-dimensional array of _DisplayCell objects
     * to store the current map state. Then, it paints square cells for the grid with the appropriate colors as
     * well as the robot on-screen.
     */
    public void paintComponent(Graphics g) {
        // Create a two-dimensional array of _DisplayCell objects for rendering.
        _DisplayGrid[][] _gridCells = new _DisplayGrid[ArenaConstants.ARENA_ROWS][ArenaConstants.ARENA_COLS];
        for (int row = 0; row < ArenaConstants.ARENA_ROWS; row++) {
            for (int col = 0; col < ArenaConstants.ARENA_COLS; col++) {
                _gridCells[row][col] = new Arena._DisplayGrid(col * ArenaConstants.CELL_SIZE, (ArenaConstants.ARENA_ROWS - row) * ArenaConstants.CELL_SIZE, ArenaConstants.CELL_SIZE);
            }
        }

        // Paint the cells with the appropriate colors.
        for (int row = 0; row < ArenaConstants.ARENA_ROWS; row++) {
            for (int col = 0; col < ArenaConstants.ARENA_COLS; col++) {
                Color cellColor;

                if (inStartZone(row, col))
                    cellColor = ArenaConstants.C_START;
                else if (inGoalZone(row, col))
                    cellColor = ArenaConstants.C_GOAL;
                else {
                    if (!arena[row][col].isExplored())
                        cellColor = ArenaConstants.C_UNEXPLORED;
                    else if (arena[row][col].getType() == Types.OBSTACLE)
                        cellColor = ArenaConstants.C_OBSTACLE;
                    else
                        cellColor = ArenaConstants.C_FREE;
                }

                g.setColor(cellColor);
                g.fillRect(_gridCells[row][col].cellX + ArenaConstants.MAP_X_OFFSET, _gridCells[row][col].cellY, _gridCells[row][col].cellSize, _gridCells[row][col].cellSize);

            }
        }

        // Paint the robot on-screen.
        g.setColor(ArenaConstants.C_ROBOT);
        int r = ArenaConstants.ARENA_ROWS - bot.cur.x;
        int c = bot.cur.y;

        g.fillOval((c-1) * ArenaConstants.CELL_SIZE + ArenaConstants.ROBOT_X_OFFSET + ArenaConstants.MAP_X_OFFSET, ArenaConstants.MAP_H - (r * ArenaConstants.CELL_SIZE + ArenaConstants.ROBOT_Y_OFFSET), ArenaConstants.ROBOT_W, ArenaConstants.ROBOT_H);

        // Paint the robot's direction indicator on-screen.
        g.setColor(ArenaConstants.C_ROBOT_DIR);
        Orientation o = bot.or;
        switch (o) {
            case North:
                g.fillOval(c * ArenaConstants.CELL_SIZE + 10 + ArenaConstants.MAP_X_OFFSET, ArenaConstants.MAP_H - r * ArenaConstants.CELL_SIZE - 15, ArenaConstants.ROBOT_DIR_W, ArenaConstants.ROBOT_DIR_H);
                break;
            case East:
                g.fillOval(c * ArenaConstants.CELL_SIZE + 35 + ArenaConstants.MAP_X_OFFSET, ArenaConstants.MAP_H - r * ArenaConstants.CELL_SIZE + 10, ArenaConstants.ROBOT_DIR_W, ArenaConstants.ROBOT_DIR_H);
                break;
            case South:
                g.fillOval(c * ArenaConstants.CELL_SIZE + 10 + ArenaConstants.MAP_X_OFFSET, ArenaConstants.MAP_H - r * ArenaConstants.CELL_SIZE + 35, ArenaConstants.ROBOT_DIR_W, ArenaConstants.ROBOT_DIR_H);
                break;
            case West:
                g.fillOval(c * ArenaConstants.CELL_SIZE - 15 + ArenaConstants.MAP_X_OFFSET, ArenaConstants.MAP_H - r * ArenaConstants.CELL_SIZE + 10, ArenaConstants.ROBOT_DIR_W, ArenaConstants.ROBOT_DIR_H);
                break;
        }
    }

    private class _DisplayGrid {
        public final int cellX;
        public final int cellY;
        public final int cellSize;

        public _DisplayGrid(int borderX, int borderY, int borderSize) {
            this.cellX = borderX + ArenaConstants.CELL_LINE_WEIGHT;
            this.cellY = ArenaConstants.MAP_H - (borderY - ArenaConstants.CELL_LINE_WEIGHT);
            this.cellSize = borderSize - (ArenaConstants.CELL_LINE_WEIGHT * 2);
        }
    }
}