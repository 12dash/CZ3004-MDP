package Algo;

import Environment.*;

public class Node {
    Grid grid;
    Node parent_grid;
    int cost_g;
    int cost_h;
    int total_cost;

    public Node(){};
    public Node(Grid cur_grid, Node  parent_grid, int cost_h){
        this.grid = cur_grid;
        this.parent_grid = parent_grid;
        if (parent_grid != null)
            this.cost_g = parent_grid.cost_g+1;
        else
            this.cost_g = 0;
        this.cost_h = cost_h;
        this.total_cost = this.cost_g + this.cost_h;
    }
}
