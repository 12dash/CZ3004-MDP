package Robot;

import Environment.Arena;
import Environment.Grid;
import Values.Orientation;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RobotSimulator extends Robot {

    protected ArrayList<Grid> path = new ArrayList<>();  //Stores the fastest path solution
    protected ArrayList<Orientation> orientations = new ArrayList<>(); // Stores the orientation as the robot goes through the fastest Path

    protected Sensors sensorF;
    protected Sensors sensorB;
    protected Sensors sensorL;
    protected Sensors sensorR;

    public RobotSimulator(Grid g) {
        super(g);
        this.setSensor();
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

    public void getPathOrientation() {
        orientations.removeAll(orientations);
        for (int i = 0; i < this.path.size() - 1; i++) {
            try {
                orientations.add(nextOrientation(path.get(i), path.get(i + 1)));
            } catch (Exception e) {
                System.out.println("Error in Getting Path Orientations... Please check the issue." + e);
            }
        }
        orientations.add(this.orientation);
    }

    public void updatePosition(Grid g, Orientation o) {
        super.setCur(g);
        super.setOrientation(o);
        sensorF.updatePosition(cur.getX(), cur.getY());
        sensorB.updatePosition(cur.getX(), cur.getY());
        sensorL.updatePosition(cur.getX(), cur.getY());
        sensorR.updatePosition(cur.getX(), cur.getY());
        updateSensorOrientation();
    }


    public void updateSensorOrientation() {
        this.sensorF.updateOr(this.orientation);
        switch (this.getOrientation()) {
            case North:
                this.sensorB.updateOr(Orientation.South);
                this.sensorL.updateOr(Orientation.West);
                this.sensorR.updateOr(Orientation.East);
                break;
            case South:
                this.sensorB.updateOr(Orientation.North);
                this.sensorL.updateOr(Orientation.East);
                this.sensorR.updateOr(Orientation.South);
                break;
            case East:
                this.sensorB.updateOr(Orientation.West);
                this.sensorL.updateOr(Orientation.North);
                this.sensorR.updateOr(Orientation.South);
                break;
            case West:
                this.sensorB.updateOr(Orientation.East);
                this.sensorL.updateOr(Orientation.South);
                this.sensorR.updateOr(Orientation.North);
                break;
        }
    }

    public void sense(Arena ar) {
        this.sensorF.sense(ar);
        this.sensorB.sense(ar);
        this.sensorR.sense(ar);
        this.sensorL.sense(ar);
    }

    public void setSensor() {
        this.sensorF = new Sensors(this.cur.getX(), this.cur.getY(), "Short");
        this.sensorB = new Sensors(this.cur.getX(), this.cur.getY(), "Short");
        this.sensorR = new Sensors(this.cur.getX(), this.cur.getY(), "Short");
        this.sensorL = new Sensors(this.cur.getX(), this.cur.getY(), "Short");
        updateSensorOrientation();
    }

    public ArrayList<Grid> getPath() {
        return this.path;
    }

    public void setPath(ArrayList<Grid> path) {
        this.path = path;
    }

    public ArrayList<Orientation> getOrientations() {
        return this.orientations;
    }
}
