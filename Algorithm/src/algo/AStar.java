package algo;

import arena.Arena;
import arena.Grid;
import robot.Robot;
import values.*;

import java.util.ArrayList;

public class AStar {

    ArrayList<Node> visited = new ArrayList<Node>();
    ArrayList<Node> candidate = new ArrayList<Node>();

    ArrayList<Grid> candidate_grid = new ArrayList<Grid>();
    ArrayList<Grid> visited_grid = new ArrayList<Grid>();
    Grid end;
    Robot robot;

    private int heuristic_cost(Grid cur, String move) {
        int cost = (-1 * (Math.abs((cur.x - this.end.x)) + Math.abs((cur.y - this.end.y))));
        return cost;
    }

    private boolean check_neighbors(Arena a, Node n) {

        int x = n.cur_grid.x;
        int y = n.cur_grid.y;

        if ((x >= 0) && (x < a.n) && (y >= 0) && (y < a.m) && (!visited_grid.contains(n.cur_grid)) && (!candidate_grid.contains(n.cur_grid))) {
            Grid temp = a.arena[y][x];
            return temp.getAcc() == Acc.TRUE;
        } else {
            return false;
        }
    }

    private void get_neighbours(Node cur, Arena arena) {

        Node s;
        Node r;
        Node l;

        ArrayList<int[]> next_positions = this.robot.get_next_positions(arena, cur.cur_grid);

        int[] pos_S = next_positions.get(0);
        int[] pos_R = next_positions.get(1);
        int[] pos_L = next_positions.get(2);

        int cost_S = heuristic_cost(arena.arena[pos_S[1]][pos_S[0]], "S");
        int cost_L = heuristic_cost(arena.arena[pos_L[1]][pos_L[0]], "L");
        int cost_R = heuristic_cost(arena.arena[pos_R[1]][pos_R[0]], "R");

        Orientation or_S = this.robot.new_orientation("S");
        Orientation or_L = this.robot.new_orientation("L");
        Orientation or_R = this.robot.new_orientation("R");

        s = new Node(arena.arena[pos_S[1]][pos_S[0]], cur, cost_S, or_S);
        l = new Node(arena.arena[pos_L[1]][pos_L[0]], cur, cost_L, or_L);
        r = new Node(arena.arena[pos_R[1]][pos_R[0]], cur, cost_R, or_R);

        if (check_neighbors(arena, s)) {
            candidate.add(s);
            candidate_grid.add(s.cur_grid);
        }
        if (check_neighbors(arena, l)) {
            candidate.add(l);
            candidate_grid.add(l.cur_grid);
        }
        if (check_neighbors(arena, r)) {
            candidate.add(r);
            candidate_grid.add(r.cur_grid);
        }
    }

    private int min_cost() {

        Node node_min = this.candidate.get(0);
        int pos = 0;

        for (int i = 1; i < this.candidate.size(); i++) {
            Node can = candidate.get(i);
            if (can.cur_grid.equals(this.end)) {
                node_min = can;
                pos = i;
                break;
            }
            if (node_min.total_cost > can.total_cost) {
                node_min = can;
                pos = i;
            }
        }
        return pos;
    }

    private void get_path(){
        ArrayList<Grid> path = new ArrayList<>();
        ArrayList<Orientation> orientation = new ArrayList<Orientation>();
        Node temp = this.visited.get(this.visited.size()-1);
        path.add(temp.cur_grid);
        while(true){
            temp = temp.parent_grid;
            if (temp == null){
                break;
            }
            path.add(0,temp.cur_grid);
            orientation.add(0,temp.or);

        }
        this.robot.path = path;
        this.robot.orientations = orientation;
    }

    public void start_search(Arena arena, Grid start, Grid end, Robot robot, boolean Goal_State) {

        this.robot = robot;
        this.end = end;

        int s_h_cost = heuristic_cost(start, "-1");
        Node start_node = new Node(start, null, s_h_cost, robot.or);
        get_neighbours(start_node, arena);
        this.visited.add(start_node);
        this.visited_grid.add(start_node.cur_grid);

        while (!candidate.isEmpty()) {
            int min_pos = min_cost();
            Node can = candidate.get(min_pos);
            robot.update_position(can.cur_grid,can.or);
            this.candidate.remove(min_pos);
            this.visited.add(can);
            this.visited_grid.add(can.cur_grid);
            if (Goal_State) {
                int x = can.cur_grid.x;
                int y = can.cur_grid.y;
                if ((x >= 12) && (y <= 2)) {
                    System.out.println("Reached the Goal State");
                    get_path();
                    return;
                }
            }
            if (can.cur_grid.equals(this.end)) {
                System.out.println("Reached End Stop");
                get_path();
                arena.display_solution(this.robot.path);
                return;
            } else {
                get_neighbours(can, arena);

            }
        }
    }



}
