package Algo;

import java.util.ArrayList;

import Environment.*;
import Utility.PrintConsole;

public class AStar {

    ArrayList<Node> visited = new ArrayList<Node>();
    ArrayList<Node> candidate = new ArrayList<Node>();

    ArrayList<Grid> candidate_grid = new ArrayList<Grid>();
    ArrayList<Grid> visited_grid = new ArrayList<Grid>();

    public ArrayList<Grid> solution = new ArrayList<>();

    Grid end;
    Arena arena;

    public AStar() {
    }

    ;

    private double turnCost(Grid cur, Node parent) {

        Node granParent;
        try {
            granParent = parent.parent_grid;
            if (granParent == null) {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }

        double turnCost = 0;

        int Px = Math.abs(parent.grid.getX() - granParent.grid.getX());
        int Py = Math.abs(parent.grid.getY() - granParent.grid.getY());

        int Cx = Math.abs(cur.getX() - parent.grid.getX());
        int Cy = Math.abs(cur.getY() - parent.grid.getY());

        if ((Px != Cx) || (Py != Cy)) {
            turnCost = 5;
            System.out.println("Added Turn Cost");
        }
        return turnCost;

    }

    private double heuristicCost(Grid cur, Node Parent) {
        double cost = (Math.pow((cur.getX() - this.end.getX()), 2) + Math.pow((cur.getY() - this.end.getY()), 2));
        //cost += turnCost(cur, Parent);
        return (Math.pow(cost, 0.5));
    }

    private boolean checkRange(int y, int x) {
        return (x >= 0) && (x < Constants.COLUMNS) && (y >= 0) && (y < Constants.ROWS);
    }

    private boolean checkNeighbor(Grid grid) {
        int x = grid.getX();
        int y = grid.getY();

        if (!visited_grid.contains(grid) && (!candidate_grid.contains(grid))) {
            return arena.grids[y][x].getAcc();
        } else {
            return false;
        }
    }

    public void addCandidate(Node cur, int y, int x) {
        double cost = heuristicCost(arena.grids[y][x], cur);
        Node temp = new Node(arena.grids[y][x], cur, cost);
        candidate.add(temp);
        candidate_grid.add(temp.grid);
    }

    private int nextCandidate() {

        Node node_min = this.candidate.get(0);
        int pos = 0;

        for (int i = 0; i < this.candidate.size(); i++) {
            Node can = candidate.get(i);
            if (can.grid.equals(this.end)) {
                pos = i;
                break;
            } else if (node_min.total_cost >= can.total_cost) {
                node_min = can;
                pos = i;
            }
        }
        return pos;
    }

    private void getNeighbours(Node cur) {

        int x = cur.grid.getX();
        int y = cur.grid.getY();

        if (checkRange(y - 1, x) && checkNeighbor(arena.grids[y - 1][x])) {
            addCandidate(cur, y - 1, x);
        }//U
        if (checkRange(y + 1, x) && checkNeighbor(arena.grids[y + 1][x])) {
            addCandidate(cur, y + 1, x);
        }//D
        if (checkRange(y, x - 1) && checkNeighbor(arena.grids[y][x - 1])) {
            addCandidate(cur, y, x - 1);
        }//L
        if (checkRange(y, x + 1) && checkNeighbor(arena.grids[y][x + 1])) {
            addCandidate(cur, y, x + 1);
        }//R
        //if (checkRange(y-1,x+1) && checkNeighbor(arena.grids[y-1][x+1])){addCandidate(cur,y-1,x+1);}//UR
        //if (checkRange(y-1,x-1) && checkNeighbor(arena.grids[y-1][x-1])){addCandidate(cur,y-1,x-1);}//UL
        //if (checkRange(y+1,x+1) && checkNeighbor(arena.grids[y+1][x+1])){addCandidate(cur,y+1,x+1);}//DR
        //if (checkRange(y+1,x-1) && checkNeighbor(arena.grids[y+1][x-1])){addCandidate(cur,y+1,x-1);}//Dl
    }

    private boolean checkGoal(Node node) {
        int x = node.grid.getX();
        int y = node.grid.getY();
        return (x >= 12) && (y <= 2);
    }

    private void getSolution() {
        Node temp = this.visited.get(this.visited.size() - 1);
        while (!(temp == null)) {
            this.solution.add(0, temp.grid);
            temp = temp.parent_grid;
        }
    }

    public void startSearch(Arena arena, Grid start, Grid end, boolean GoalState) {
        this.end = end;
        this.arena = arena;

        double s_h_cost = heuristicCost(start, null);
        Node start_node = new Node(start, null, s_h_cost);

        getNeighbours(start_node);

        this.visited.add(start_node);
        this.visited_grid.add(start_node.grid);
        while (!candidate.isEmpty()) {

            int posNextNode = nextCandidate();

            Node candidateNode = candidate.get(posNextNode);
            //System.out.println("Can : "+candidateNode.grid.getY()+" "+candidateNode.grid.getX()+" "+candidateNode.total_cost);
            this.candidate.remove(posNextNode);

            this.visited.add(candidateNode);
            this.visited_grid.add(candidateNode.grid);

            if (GoalState && checkGoal(candidateNode)) {
                getSolution();
                return;
            } else if ((!GoalState) && candidateNode.grid.equals(this.end)) {
                getSolution();
                return;
            } else {
                getNeighbours(candidateNode);
            }
        }
    }
}
