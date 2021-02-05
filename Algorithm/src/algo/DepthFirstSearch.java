package algo;

import arena.*;
import values.*;
import robot.Robot;

import java.util.ArrayList;
import java.util.Stack;

public class DepthFirstSearch {

    Robot robot = null;
    ArrayList<Grid> visited = null;
    Stack<Grid> stack = null;

    ArrayList<Grid> path = null;

    public boolean check_acc(Arena a, int x, int y) {

        if ((x >= 0) && (x < a.n) && (y >= 0) && (y < a.m)) {
            Grid temp = a.arena[y][x];
            if (temp.getAcc() == Acc.TRUE) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    public void get_neighbors(Arena a, Grid b) {
        Orientation or = this.robot.or;

        int x = b.x;
        int y = b.y;

        int[] pos_S = null;
        int[] pos_L = null;
        int[] pos_R = null;

        switch (or) {

            case North: {
                pos_S = new int[]{x, y + 1};
                pos_L = new int[]{x - 1, y};
                pos_R = new int[]{x + 1, y};
                break;
            }
            case South: {
                pos_S = new int[]{x, y - 1};
                pos_L = new int[]{x + 1, y};
                pos_R = new int[]{x - 1, y};
                break;
            }
            case East: {
                pos_S =new int[]{x+1,y};
                pos_L =new int[]{x,y+1};
                pos_R = new int[]{x,y-1} ;
                break;
            }
            case West: {
                pos_S =new int[]{x-1,y};
                pos_L =new int[]{x,y-1};
                pos_R = new int[]{x,y+1};
                break;
            }
        }

        int x_1 = x - 1;
        int x_2 = x + 1;
        int y_1 = y - 1;
        int y_2 = y + 1;

        boolean temp;
        if (check_acc(a, x + 1, y)) {
            stack.push(a.arena[y][x + 1]);
        }
        if (check_acc(a, x - 1, y)) {
            stack.push(a.arena[y][x + 1]);
        }
        if (check_acc(a, x, y + 1)) {
            stack.push(a.arena[y][x + 1]);
        }

    }

    public void start_search(Arena a, Grid s, Grid e, Robot r) {

        this.robot = r;

        Grid cur = null;
        stack.add(s);


        while (!stack.isEmpty()) {
            cur = stack.pop();

        }

    }
}
