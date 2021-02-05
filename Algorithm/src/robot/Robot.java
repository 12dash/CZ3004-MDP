package robot;

import values.Orientation;
import arena.*;

public class Robot {

    public Grid cur = null;
    public Orientation or = null;

    public void robot(Grid cur, Orientation or){
        this.cur = cur;
        this.or = or;
    }

    public void update_position(Grid new_grid, Orientation new_or){
        this.cur = new_grid;
        this.or = new_or;
    }

}
