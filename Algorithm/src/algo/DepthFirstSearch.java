package algo;

import arena.*;
import values.*;
import robot.Robot;

import java.util.ArrayList;
import java.util.Stack;

public class DepthFirstSearch {

    Robot robot = null;
    ArrayList<Grid> visited = new ArrayList<>();
    Stack<Grid> stack = new Stack<>();
    Stack<String> movement = new Stack<>();
    Mapping mapping = new Mapping();

    public boolean check_acc(Arena a, int[] pos) {

        int x = pos[0];
        int y = pos[1];

         if ((x >= 0) && (x < a.n) && (y >= 0) && (y < a.m) && (!this.visited.contains(a.arena[y][x]))) {
            Grid temp = a.arena[y][x];
            return temp.getAcc() == Acc.TRUE;
        }
        return false;
    }

    public Orientation new_orientation(String next_move) {

        Orientation next_or;

        if (next_move.equals("S")) {
            next_or = this.robot.or;
            return next_or;
        }

        switch (this.robot.or) {
            case North:
                if (next_move.equals("L")) {
                    next_or = Orientation.West;
                } else {
                    next_or = Orientation.East;
                }
                return next_or;
            case East:
                if (next_move.equals("L")) {
                    next_or = Orientation.North;
                } else {
                    next_or = Orientation.South;
                }
                return next_or;
            case West:
                if (next_move.equals("L")) {
                    next_or = Orientation.South;
                } else {
                    next_or = Orientation.North;
                }
                return next_or;
            case South:
                if (next_move.equals("L")) {
                    next_or = Orientation.East;
                } else {
                    next_or = Orientation.West;
                }
                return next_or;
        }

        return null;
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
                pos_S = new int[]{x, y - 1};
                pos_L = new int[]{x - 1, y};
                pos_R = new int[]{x + 1, y};
                break;
            }
            case South: {
                pos_S = new int[]{x, y + 1};
                pos_L = new int[]{x + 1, y};
                pos_R = new int[]{x - 1, y};
                break;
            }
            case East: {
                pos_S = new int[]{x + 1, y};
                pos_L = new int[]{x, y - 1};
                pos_R = new int[]{x, y + 1};
                break;
            }
            case West: {
                pos_S = new int[]{x - 1, y};
                pos_L = new int[]{x, y + 1};
                pos_R = new int[]{x, y - 1};
                break;
            }
        }

        if (check_acc(a, pos_R)) {
            this.stack.push(a.arena[pos_R[1]][pos_R[0]]);
            this.movement.push("R");
            this.mapping.add_pair(this.robot.cur,a.arena[pos_R[1]][pos_R[0]] );

        }
        if (check_acc(a, pos_L)) {
            this.stack.push(a.arena[pos_L[1]][pos_L[0]]);
            this.movement.push("L");
            this.mapping.add_pair(this.robot.cur,a.arena[pos_L[1]][pos_L[0]]);
        }
        if (check_acc(a, pos_S)) {
            this.stack.push(a.arena[pos_S[1]][pos_S[0]]);
            this.movement.push("S");
            this.mapping.add_pair(this.robot.cur,a.arena[pos_S[1]][pos_S[0]]);
        }
    }

    public void fix_path(Grid a) {
        Grid parent = this.mapping.find_parent(a);
        Grid temp = this.robot.path.get(this.robot.path.size()-2);

        if (parent.equals(temp)) {
            return;
        }

        while (!temp.equals(parent)) {
            this.robot.path.remove(this.robot.path.size()-2);
            temp = this.robot.path.get(this.robot.path.size()-2);
        }
    }

    public void start_search(Arena a, Grid s, Grid e, Robot r, boolean Goal_state) {

        //Initializing the Robot
        this.robot = r;
        this.robot.cur = s;
        this.robot.or = Orientation.North;

        Grid next_grid;
        String next_move;
        Orientation next_or;

        visited.add(s);
        get_neighbors(a, s);

        while (!stack.isEmpty()) {
            do {
                next_grid = stack.pop();
                next_move = movement.pop();

            } while (visited.contains(next_grid));
            next_or = new_orientation(next_move);

            this.robot.cur = next_grid;
            this.robot.or = next_or;
            this.robot.add_node();

            if (this.robot.path.size() > 1) {
                fix_path(next_grid);
            }
            this.visited.add(next_grid);


            if (Goal_state){
                int x = next_grid.x;
                int y = next_grid.y;
                if ((x>=12)&&(y<=2)){
                    System.out.println("Reached the Goal State");
                    return;
                }

            }
            if (next_grid.equals(e)) {
                System.out.println("Reached the Goal State");
                return;
            }
            get_neighbors(a, next_grid);
        }
    }
}
