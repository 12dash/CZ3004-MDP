package Algo;

/**
 * This class is the AStar search algorithm which provides the shortest path from the start to end path
 * The heuristic function used is direct/diagonal distance from Grid a to target Grid b.
 * There is an additional cost for the turning simulating the additional time cost for the required by the Robot to turn.
 */


import java.util.ArrayList;

import Environment.*;

public class AStar {

    ArrayList<Node> visited = new ArrayList<>();//Stores the Grids that have already been visited in the form of a node containing additional details.
    ArrayList<Node> candidate = new ArrayList<>();//Stores the Grids which can be reached by the robot. There the terminal edges for the current node.

    ArrayList<Grid> candidate_grid = new ArrayList<>(); // Stores the Grids that are candidate for the nodes to be expanded.
    ArrayList<Grid> visited_grid = new ArrayList<>();//Stores the Grids visited

    public ArrayList<Grid> solution = new ArrayList<>(); //Stores the final solution from the start to the end goal.

    Grid end; //To store the end goal (one cell)
    Arena arena; //Stores the arena data-structure consisting of 2D-array representing an actual grid.

    public AStar() {
        //Default constructor
    }

    private double turnCost(Grid cur, Node parent) {

        /*
        Method to compute if the robot needs to turn to reach the candidate Grid. If yes, then an additional cost is added.
         */
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

        //Change Vector (Px,Py) representing the direction of movement from Grandparent to Parent.
        int Px = Math.abs(parent.grid.getX() - granParent.grid.getX());
        int Py = Math.abs(parent.grid.getY() - granParent.grid.getY());


        //Change Vector (Px,Py) representing the direction of movement from Parent to Child.
        int Cx = Math.abs(cur.getX() - parent.grid.getX());
        int Cy = Math.abs(cur.getY() - parent.grid.getY());

        if ((Px != Cx) || (Py != Cy)) {
            //TurnCost is 5.
            turnCost = 5;
        }
        return turnCost;

    }

    private double heuristicCost(Grid cur, Node Parent) {
        /*
        Heuristic cost to calculate the cost involved in expanding the cur candidate Node.
         */
        double cost = (Math.pow((cur.getX() - this.end.getX()), 2) + Math.pow((cur.getY() - this.end.getY()), 2));
        cost += turnCost(cur, Parent);
        return (Math.pow(cost, 0.5));
    }

    private boolean checkRange(int y, int x) {
        /*
            Check if the position is valid position in the arena.
         */
        return (x >= 0) && (x < Constants.COLUMNS) && (y >= 0) && (y < Constants.ROWS);
    }

    private boolean checkNeighbor(Grid grid) {
        /*
        Method to check if the grid in context is already expanded or in the list of candidate nodes and can be reached by the robot.
         */
        int x = grid.getX();
        int y = grid.getY();

        if (!visited_grid.contains(grid) && (!candidate_grid.contains(grid))) {
            return arena.grids[y][x].getAcc();
        } else {
            return false;
        }
    }

    public void addCandidate(Node cur, int y, int x) {
        /*
        Method to get the cost and make a node structure from the Grid
         */
        double cost = heuristicCost(arena.grids[y][x], cur);
        Node temp = new Node(arena.grids[y][x], cur, cost);
        candidate.add(temp);
        candidate_grid.add(temp.grid);
    }

    private int nextCandidate() {
        /*
        Finds the node that has the minimum cost to expand from the list of candidate node.
         */

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
        /*
        Adds the neighbouring nodes into candidate after doing the validation.
         */

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
        /*
        Check if the node is in the Goal state
         */
        int x = node.grid.getX();
        int y = node.grid.getY();
        return (x >= 12) && (y <= 2);
    }

    private void getSolution() {
        /*
        Generates the path or solution in terms of the Grids.
         */
        Node temp = this.visited.get(this.visited.size() - 1);
        while (!(temp == null)) {
            this.solution.add(0, temp.grid);
            temp = temp.parent_grid;
        }
    }

    public void startSearch(Arena arena, Grid start, Grid end, boolean GoalState) {
        this.end = end;
        this.arena = arena;

        //Initializing the start Node.
        double s_h_cost = heuristicCost(start, null);
        Node start_node = new Node(start, null, s_h_cost);

        getNeighbours(start_node);

        this.visited.add(start_node);
        this.visited_grid.add(start_node.grid);

        while (!candidate.isEmpty()) {

            int posNextNode = nextCandidate();//Get the position of the minimum node in the candidate list
            Node candidateNode = candidate.get(posNextNode);
            this.candidate.remove(posNextNode);

            this.visited.add(candidateNode);
            this.visited_grid.add(candidateNode.grid);

            if (GoalState && checkGoal(candidateNode)) {
                // If the robot needs to reach the Goal State.
                getSolution();
                return;
            } else if ((!GoalState) && candidateNode.grid.equals(this.end)) {
                // To reach a particular grid such as the waypoint.
                getSolution();
                return;
            } else {
                getNeighbours(candidateNode);
            }
        }
    }
}
