package Algo;

/**
 * The class for calculating the fast path using the AStar algorithm.
 * Find the shortest path from start and end.
 */

import Environment.*;
import Utility.PrintConsole;

import java.util.ArrayList;

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

    private double heuristicCost(Grid cur, Node Parent) {
        /*
            Heuristic cost to calculate the cost involved in expanding the cur candidate Node.
            Takes the straight line cost from cur to end goal.
         */
        double cost = (Math.pow((cur.getX() - this.end.getX()), 2) + Math.pow((cur.getY() - this.end.getY()), 2));
        return (Math.pow(cost, 0.5));
    }

    private boolean checkRange(int y, int x) {
        //Check if the position is valid position in the arena.
        return (x >= 0) && (x < ArenaConstants.ARENA_COLS) && (y >= 0) && (y < ArenaConstants.ARENA_ROWS);
    }

    private void addCandidate(Node cur, int y, int x) {
        /*
        Method to get the cost and make a node structure from the Grid
         */
        double cost = heuristicCost(arena.grids[y][x], cur);
        Node temp = new Node(arena.grids[y][x], cur, cost);
        candidate.add(temp);
        candidate_grid.add(temp.grid);
    }

    private boolean checkNeighbor(Grid grid) {
        /*
        Method to check if the grid in context is already expanded or in the list of candidate nodes and can be reached by the robot.
         */
        int x = grid.getX();//x position for the grid in the array
        int y = grid.getY();//y position for the grid in the array

        if (!visited_grid.contains(grid) && (!candidate_grid.contains(grid))) {
            //Checks if the grid has already been expanded or if the grid is already considered for expansion
            //Checks if the grid can be accessed(padded/obstacle)
            return arena.grids[y][x].getAcc();
        } else {
            return false;
        }
    }
    private void getNeighbours(Node cur) {
        /*
        Adds the neighbouring nodes into candidate after doing the validation.
         */

        int x = cur.grid.getX();
        int y = cur.grid.getY();

        if (checkRange(y - 1, x) && checkNeighbor(arena.grids[y - 1][x]) && arena.grids[y-1][x].isExplored()) {
            addCandidate(cur, y - 1, x);
        }//U
        if (checkRange(y + 1, x) && checkNeighbor(arena.grids[y + 1][x]) && arena.grids[y+1][x].isExplored()) {
            addCandidate(cur, y + 1, x);
        }//D
        if (checkRange(y, x - 1) && checkNeighbor(arena.grids[y][x - 1]) && arena.grids[y][x-1].isExplored()) {
            addCandidate(cur, y, x - 1);
        }//L
        if (checkRange(y, x + 1) && checkNeighbor(arena.grids[y][x + 1]) && arena.grids[y][x+1].isExplored()) {
            addCandidate(cur, y, x + 1);
        }//R

        //if (checkRange(y-1,x+1) && checkNeighbor(arena.grids[y-1][x+1])){addCandidate(cur,y-1,x+1);}//UR
        //if (checkRange(y-1,x-1) && checkNeighbor(arena.grids[y-1][x-1])){addCandidate(cur,y-1,x-1);}//UL
        //if (checkRange(y+1,x+1) && checkNeighbor(arena.grids[y+1][x+1])){addCandidate(cur,y+1,x+1);}//DR
        //if (checkRange(y+1,x-1) && checkNeighbor(arena.grids[y+1][x-1])){addCandidate(cur,y+1,x-1);}//Dl
    }

    private int nextCandidate() {
        /*
        Finds the node that has the minimum cost to expand from the list of candidate node.
         */

        Node node_min = this.candidate.get(0);//Initializing the minimum node to the first node
        int pos = 0;//Storing the position of the minimum node in the arrayList

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

    private boolean checkGoal(Node node) {
        /*
        Check if the node is in the Goal state
         */
        int x = node.grid.getX();
        int y = node.grid.getY();
        return (x >= ArenaConstants.GOAL_COL-1) && (y <= ArenaConstants.GOAL_ROW+1);
    }

    private void getSolution() {
        /*
        Generates the path or solution in terms of the Grids.
         */
        Node temp = this.visited.get(this.visited.size() - 1);
        while (!(temp == null)) {
            this.solution.add(0, temp.grid);
            temp = temp.parent_node;
        }
    }

    public void startSearch(Arena arena, Grid start, Grid end, boolean GoalState) {
        /*
        Entry point for searching the path from start to end.
        Goal State is used to decide if it needs to go to the goal state or the WayPoint.
         */

        this.end = end;//Initializing the end goal

        this.arena = arena;//Initializing the arena

        //Initializing the start Node.
        double s_h_cost = heuristicCost(start, null);
        Node start_node = new Node(start, null, s_h_cost);

        getNeighbours(start_node);

        this.visited.add(start_node);
        this.visited_grid.add(start_node.grid);

        while (!candidate.isEmpty()) {

            int posNextNode = nextCandidate();//Get the position of the minimum node in the candidate list
            Node candidateNode = candidate.get(posNextNode);

            //Remove it from the candidate list and pushing it to visited nodes list.
            this.candidate.remove(posNextNode);
            this.visited.add(candidateNode);
            this.visited_grid.add(candidateNode.grid);

            if (GoalState && checkGoal(candidateNode)) {
                // If the robot has to reach the Goal State.
                getSolution();
                return;
            } else if ((!GoalState) && candidateNode.grid.equals(this.end)) {
                // To reach a particular grid such as the waypoint.
                getSolution();
                return;
            } else {
                //Else get the next candidate nodes that needs to be expanded.
                getNeighbours(candidateNode);
            }
        }
    }

}
