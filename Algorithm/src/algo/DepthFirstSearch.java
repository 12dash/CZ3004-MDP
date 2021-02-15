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

    public void check_add_neighbors(Arena a, int[] pos, String move) {
        if (a.check_acc(this.visited, pos)) {
            if (!this.stack.contains(a.arena[pos[1]][pos[0]])) {
                this.stack.push(a.arena[pos[1]][pos[0]]);
                this.movement.push(move);
                this.mapping.add_pair(this.robot.cur, a.arena[pos[1]][pos[0]]);
            }
        }
    }

    public void get_neighbors(Arena a, Grid b) {

        ArrayList<int[]> next_positions = this.robot.get_next_positions(b);

        int[] pos_S = next_positions.get(0);
        int[] pos_R = next_positions.get(1);
        int[] pos_L = next_positions.get(2);

        check_add_neighbors(a, pos_R, "R");
        check_add_neighbors(a, pos_L, "L");
        check_add_neighbors(a, pos_S, "S");
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
        get_neighbors(a, s);

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
            get_neighbors(a, next_grid);
        }
    }
}
