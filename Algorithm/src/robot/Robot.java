package robot;

import values.Orientation;
import arena.*;

import java.util.ArrayList;

public class Robot {

    public Grid cur = null;
    public Orientation or = null;
    public ArrayList<Grid> path = new ArrayList<Grid>();
    public ArrayList<Orientation> orientations = new ArrayList<Orientation>();

    public void Robot() {
        return;
    }

    public void robot(Grid cur, Orientation or) {
        this.cur = cur;
        this.or = or;
    }

    public void add_node() {
        this.path.add(this.cur);
        this.orientations.add(this.or);
    }

    public void remove_node() {
        this.path.remove(this.path.size() - 1);
        this.orientations.remove(this.orientations.size() - 1);
    }

    public void update_position(Grid new_grid, Orientation new_or) {
        this.cur = new_grid;
        this.or = new_or;
    }

}
