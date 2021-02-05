/*
  The arena size is 20 rows and 15 columns.
  m = 20 --> Rows
  n = 15 --> Columns
 */

package arena;

import values.*;

public class Arena {

    int m; //number of rows
    int n; //number of columns
    Grid[][] arena;

    public Arena(int m, int n) {
        this.m = m;
        this.n = n;
        this.arena = new Grid[this.m][this.n];
    }

    public void make_arena(int[][] temp) {
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                int x = 19-i;
                int y = j;
                if (temp[i][j] == 1) {
                    this.arena[i][j] = new Grid(Types.OBSTACLE, x,y);
                } else {
                    this.arena[i][j] = new Grid(Types.FREE, x,y);
                }
            }
        }
    }

    public void get_view() {
        for (int i = 0; i < this.arena.length; i++) {
            for (int j = 0; j < this.arena[0].length; j++) {
                if (Types.OBSTACLE == this.arena[i][j].getType())
                    System.out.print(1);
                else{
                    System.out.print(0);
                }
            }
            System.out.println();
        }
    }

}
