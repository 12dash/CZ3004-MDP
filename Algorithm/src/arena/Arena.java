/*
  The arena size is 20 rows and 15 columns.
  m = 20 --> Rows
  n = 15 --> Columns
 */

package arena;

import values.*;

public class Arena {

    public int m; //number of rows
    public int n; //number of columns
    public Grid[][] arena;

    public Arena(int m, int n) {
        this.m = m;
        this.n = n;
        this.arena = new Grid[this.m][this.n];
    }

    public void make_arena(int[][] temp) {
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                int y = i;
                int x = j;
                if (temp[i][j] == 1) {
                    this.arena[i][j] = new Grid(Types.OBSTACLE, x, y);
                } else {
                    this.arena[i][j] = new Grid(Types.FREE, x, y);
                }
            }
        }
    }

    public void get_view() {
        for (int i = 0; i < this.arena.length; i++) {
            for (int j = 0; j < this.arena[0].length; j++) {
                if (Types.OBSTACLE == this.arena[i][j].getType())
                    System.out.print(1);
                else {
                    System.out.print(0);
                }
            }
            System.out.println();
        }
    }

    public void get_acc_view() {
        for (int i = 0; i < this.arena.length; i++) {
            for (int j = 0; j < this.arena[0].length; j++) {
                if (Acc.FALSE == this.arena[i][j].getAcc())
                    System.out.print(1);
                else {
                    System.out.print(0);
                }
            }
            System.out.println();
        }
    }

    public void check_cell_modify_acc(int i, int j) {
        if (((i >= 0) && (i < this.m)) && ((j >= 0) && (j < this.n))) {
            if (this.arena[i][j].getAcc() == Acc.TRUE) {
                this.arena[i][j].setAcc(Acc.FALSE);
            }
        }
    }

    public void add_neighbour_padding(int i, int j) {
        int x = j;
        int x_1 = j - 1;
        int x_2 = j + 1;
        int y = i;
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

}
