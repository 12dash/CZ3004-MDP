package Robot;

import Environment.Grid;
import Values.Orientation;

import java.util.ArrayList;

public class Robot {

    public ArrayList<Grid> path = new ArrayList<>();
    public ArrayList<Orientation> orientations = new ArrayList<>();

    public Orientation cur_or;
    public Grid cur;

    public Robot(Grid g) {
        cur_or = Orientation.North;
        cur = g;
    }

    public Robot(Orientation or, ArrayList<Grid> path) {
        this.cur_or = or;
        this.path = path;
        getOrientation();
    }

    public void setCur(Grid cur) {
        this.cur = cur;
    }

    public void updatePosition(Grid next_cur, Orientation next_or) {
        this.cur = next_cur;
        this.cur_or = next_or;
    }

    private Orientation nextOrientation(Grid cur, Grid next) {
        Orientation next_or = Orientation.East;

        int cur_X = cur.getX();
        int cur_Y = cur.getY();

        int next_X = next.getX();
        int next_Y = next.getY();

        int dif = Math.abs(cur_X - next_X) + Math.abs(cur_Y - next_Y);

        if (dif == 1) {
            if (cur_X == next_X) next_or = (cur_Y - next_Y) > 0 ? Orientation.North : Orientation.South;
            else next_or = (cur_X - next_X) > 0 ? Orientation.West : Orientation.East;
        } else {
            int x = cur_X - next_X;
            int y = cur_Y - next_Y;

            if ((x > 0) && (y > 0)) next_or = Orientation.NorthWest;
            else if ((x < 0) && (y > 0)) next_or = Orientation.NorthEast;
            else if ((x > 0) && (y < 0)) next_or = Orientation.SouthWest;
            else if ((x < 0) && (y < 0)) next_or = Orientation.SouthEast;
        }
        return next_or;
    }

    public void getOrientation() {
        orientations.add(Orientation.East);
        for (int i = 0; i < this.path.size() - 1; i++) {
            try {
                orientations.add(nextOrientation(path.get(i), path.get(i + 1)));
            } catch (Exception e) {
                System.out.println("Getting Orientations");
            }
        }
    }
}
