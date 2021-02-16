package robot;

import values.Orientation;
import algo.Mapping;
import arena.*;

import javax.print.attribute.standard.OrientationRequested;
import java.util.ArrayList;

public class Robot {

    public Grid cur;
    public Orientation or;
    public ArrayList<Grid> path = new ArrayList<>();      // Stores the path robot moves
    public ArrayList<Orientation> orientations = new ArrayList<>();

    public Robot(Orientation or, Grid cur) {
        this.or = or;
        this.cur = cur;
    }

    public Robot(Orientation or, Grid cur){
        this.or = or;
        this.cur = cur;
    }

    public void setCur(Grid cur){
        this.cur = cur;
    }

    public void add_node() {
        this.path.add(this.cur);
        this.orientations.add(this.or);
    }

    public void reInitialisePathAndOrientations(){
        this.path = new ArrayList<Grid>();
        this.orientations = new ArrayList<Orientation>();

    }


    public void update_position(Grid new_grid, Orientation new_or) {
        this.cur = new_grid;
        this.or = new_or;
    }

    /**
      Returns the neighbouring cells (Straight, Left, Right)
     */
    public ArrayList<int[]> get_next_positions(Grid b) {

        Orientation or = this.or;

        int x = b.x;
        int y = b.y;

        int[] pos_S = null;
        int[] pos_L = null;
        int[] pos_R = null;

        switch (or) {
            case North: {
                pos_S = new int[]{x, y - 1};
                pos_L = new int[]{x - 1, y};
                pos_R = new int[]{x + 1, y};
                break;
            }
            case South: {
                pos_S = new int[]{x, y + 1};
                pos_L = new int[]{x + 1, y};
                pos_R = new int[]{x - 1, y};
                break;
            }
            case East: {
                pos_S = new int[]{x + 1, y};
                pos_L = new int[]{x, y - 1};
                pos_R = new int[]{x, y + 1};
                break;
            }
            case West: {
                pos_S = new int[]{x - 1, y};
                pos_L = new int[]{x, y + 1};
                pos_R = new int[]{x, y - 1};
                break;
            }
        }
        ArrayList<int[]> return_variable = new ArrayList<>();
        return_variable.add(pos_S);
        return_variable.add(pos_R);
        return_variable.add(pos_L);
        return return_variable;
    }

    /**
     * Returns the orientation of the robot after next move
     */

    public Orientation new_orientation(String next_move) {

        Orientation next_or;

        if (next_move.equals("S")) {
            next_or = this.or;
            return next_or;
        }

        switch (this.or) {
            case North:
                if (next_move.equals("L")) {
                    next_or = Orientation.West;
                } else {
                    next_or = Orientation.East;
                }
                return next_or;
            case East:
                if (next_move.equals("L")) {
                    next_or = Orientation.North;
                } else {
                    next_or = Orientation.South;
                }
                return next_or;
            case West:
                if (next_move.equals("L")) {
                    next_or = Orientation.South;
                } else {
                    next_or = Orientation.North;
                }
                return next_or;
            case South:
                if (next_move.equals("L")) {
                    next_or = Orientation.East;
                } else {
                    next_or = Orientation.West;
                }
                return next_or;
        }
        return null;
    }

    /**
     * Utility for mapping in DFS
     */

    public void fix_path(Grid a, Mapping mapping) {
        Grid parent = mapping.find_parent(a);
        Grid temp = this.path.get(this.path.size() - 2);

        if (parent.equals(temp)) {
            return;
        }
        while (!temp.equals(parent)) {
            this.path.remove(this.path.size() - 2);
            temp = this.path.get(this.path.size() - 2);
        }
    }

}