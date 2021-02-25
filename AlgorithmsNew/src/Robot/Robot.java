package Robot;

import Environment.Grid;
import Values.Orientation;

public class Robot {

    protected Grid cur;
    protected Orientation orientation;

    public Robot(){
        //Default Constructor
    }
    public Robot(Grid g){
        //Constructor for initializing the position of the robot
        this.cur = g;

        //Initializing the direction of the Robot
        this.orientation = RobotConstants.START_DIR;
    }

    public void setCur(Grid g){
        //Setter method for the grid
        this.cur = g;
    }

    public Grid getCur(){
        //Getter method for the Grid
        return this.cur;
    }

    public void setOrientation(Orientation or){
        //Setter method for the orientation of the Robot.
        this.orientation = or;
    }

    public Orientation getOrientation(){
        //Getter method for the orientation
        return this.orientation;
    }
}
