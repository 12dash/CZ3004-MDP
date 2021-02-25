package Algo;

import Environment.Grid;

/**
 * The Class is used in Fastest Path ALgorithm to use nodes as the data structure to store the nodes as candidates.
 */

public class Node {

    Grid grid;//The grid in  the arena for which the node is made

    Node parent_node;//The parent or the node from which the robot will be accessing current node.

    double cost_g;//Parent Cost
    double cost_h;//Heuristic Cost
    double turn_cost;//Cost for turning

    double total_cost;//Total Cost to expand the node.


    public Node() {
        //Default constructor for the Node class
    }

    private double turnCost(){
        /*
        Method to compute if the robot needs to turn to reach the candidate Grid. If yes, then an additional cost is added.
         */
        Node granParent;
        try {
            granParent = this.parent_node;
            if (granParent == null) {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }

        double turnCost = 0;

        //Change Vector (Px,Py) representing the direction of movement from Grandparent to Parent.
        int Px = Math.abs(this.grid.getX() - granParent.grid.getX());
        int Py = Math.abs(this.grid.getY() - granParent.grid.getY());


        //Change Vector (Px,Py) representing the direction of movement from Parent to Child.
        int Cx = Math.abs(this.grid.getX() - parent_node.grid.getX());
        int Cy = Math.abs(this.grid.getY() - parent_node.grid.getY());

        if ((Px != Cx) || (Py != Cy)) {
            //TurnCost is 20.
            turnCost = 10;
        }
        return turnCost;

    }
    private void setCost() {
        this.cost_g = this.parent_node != null? this.parent_node.cost_g + 1 : 0;//Setting the parent_cost by increment by 1.
        this.turn_cost = this.turnCost();
        this.total_cost = this.cost_g + this.cost_h + turn_cost;//The total cost is heuristic cost + past cost
    }

    public Node(Grid cur, Node parent_node, double cost_h) {
        /*
        Initializing the node
         */
        this.grid = cur;
        this.parent_node = parent_node;

        //Heuristic cost
        this.cost_h = cost_h;

        //Set the other cost derived from the heuristic cost and past cost
        this.setCost();
    }
}
