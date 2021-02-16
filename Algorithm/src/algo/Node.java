package algo;

import arena.Grid;
import values.Orientation;

public class Node {

    Grid cur_grid;
    Node parent_grid;
    int cost_g;
    int cost_h;
    int total_cost;
    Orientation or;

    public Node(Grid cur_grid, Node  parent_grid, int cost_h, Orientation or){
        this.cur_grid = cur_grid;
        this.parent_grid = parent_grid;
        if (parent_grid != null)
            this.cost_g = parent_grid.cost_g + 1;
        else
            this.cost_g = 0;
        this.cost_h = cost_h;
        this.or = or;
        this.total_cost = this.cost_g + this.cost_h;
    }
}