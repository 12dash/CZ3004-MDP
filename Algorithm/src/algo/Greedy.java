package algo;

import arena.*;
import values.*;
import robot.Robot;

import java.util.ArrayList;
import java.util.Stack;

public class Greedy {

    Robot robot = null;
    ArrayList<Grid> visited = new ArrayList<>();
    Stack<Grid> stack = new Stack<>();
    Stack<String> movement = new Stack<>();
    Mapping mapping = new Mapping();

    public void check_add_neighbors(Arena a, int[] pos, String move) {
        if (a.check_acc(this.visited, pos)) {
            if (!this.stack.contains(a.arena[pos[1]][pos[0]])) {
                this.stack.push(a.arena[pos[1]][pos[0]]);
                this.movement.push(move);
                this.mapping.add_pair(this.robot.cur, a.arena[pos[1]][pos[0]]);
            }
        }
    }

    public double calculate_cost(Grid next, Grid end, String s) {
        double cost = -1*(Math.abs((next.x - end.x)) + Math.abs((next.y - end.y)));
        return cost;
    }

    public void get_neighbors(Arena a, Grid b, Grid end) {

        ArrayList<int[]> next_positions = this.robot.get_next_positions(a, b);

        int[] pos_S = next_positions.get(0);
        int[] pos_R = next_positions.get(1);
        int[] pos_L = next_positions.get(2);

        double cost_S = calculate_cost(a.arena[pos_S[1]][pos_S[0]], end, "S");
        double cost_L = calculate_cost(a.arena[pos_L[1]][pos_L[0]], end, "L");
        double cost_R = calculate_cost(a.arena[pos_R[1]][pos_R[0]], end, "R");

        ArrayList<int[]> pos = new ArrayList<>();
        ArrayList<String> move = new ArrayList<>();

        if (cost_S < cost_L) {
            if (cost_S < cost_R) {
                pos.add(pos_S);
                move.add("S");
                if (cost_L < cost_R) {
                    pos.add(pos_L);
                    move.add("L");
                    pos.add(pos_R);
                    move.add("R");
                } else {
                    pos.add(pos_R);
                    move.add("R");
                    pos.add(pos_L);
                    move.add("L");
                }
            } else {
                pos.add(pos_R);
                move.add("R");
                pos.add(pos_S);
                move.add("S");
                pos.add(pos_L);
                move.add("L");
            }
        } else {
            if (cost_L < cost_R) {
                pos.add(pos_L);
                move.add("R");
                if (cost_S < cost_R) {
                    pos.add(pos_S);
                    move.add("S");
                    pos.add(pos_R);
                    move.add("R");

                } else {
                    pos.add(pos_R);
                    move.add("R");
                    pos.add(pos_S);
                    move.add("S");
                }
            } else {
                pos.add(pos_R);
                move.add("R");
                pos.add(pos_L);
                move.add("L");
                pos.add(pos_S);
                move.add("S");
            }
        }
        check_add_neighbors(a, pos.get(0), move.get(0));
        check_add_neighbors(a, pos.get(1), move.get(1));
        check_add_neighbors(a, pos.get(2), move.get(2));
    }



    public void start_search(Arena a, Grid s, Grid e, Robot r, boolean Goal_state) {

        //Initializing the Robot
        this.robot = r;
        this.robot.cur = r.cur;
        this.robot.or = r.or;

        Grid next_grid;
        String next_move;
        Orientation next_or;

        visited.add(s);
        get_neighbors(a, s, e);

        while (!stack.isEmpty()) {
            do {
                next_grid = stack.pop();
                next_move = movement.pop();

            } while (visited.contains(next_grid));
            next_or = this.robot.new_orientation(next_move);

            this.robot.cur = next_grid;
            this.robot.or = next_or;
            this.robot.add_node();

            if (this.robot.path.size() > 1) {
                this.robot.fix_path(next_grid, this.mapping);
            }
            this.visited.add(next_grid);

            if (Goal_state) {
                int x = next_grid.x;
                int y = next_grid.y;
                if ((x >= 12) && (y <= 2)) {
                    System.out.println("Reached the Goal State");
                    return;
                }
            }
            if (next_grid.equals(e)) {
                System.out.println("Reached the Goal State");
                return;
            }
            get_neighbors(a, next_grid, e);
        }
    }
}
