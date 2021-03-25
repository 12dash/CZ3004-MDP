package Environment;

import Values.*;

public class Grid {

    private Type type;
    private boolean acc;
    private final int x;
    private final int y;
    private boolean explored;
    private boolean pictureClicked = false;
    private boolean frontSet = false;

    public Grid(Type type, boolean acc, int x, int y, boolean explored) {

        /*
         Position in the array of Grids of the class Arena.
         In order to access the grid in the grids array : Grids[y][x]
         */

        this.x = x; // Columns from right
        this.y = y; // Row from top

        this.type = type;// The type of grid (OBSTACLE or FREE)

        this.acc = true;//If the grid can be accessed by the robot. (OBSTACLE or FREE or PADDED)

        this.explored = explored;//If the grid has been explored by the robot in the exploration stage.
    }


    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setAcc(boolean acc) {
        this.acc = acc;
    }

    public boolean getAcc() {
        return this.acc;
    }

    public boolean isExplored() {
        return this.explored;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    public boolean isObstacle(){
        return this.type.equals(Type.OBSTACLE);
    }

    public void setPictureClicked(boolean click){
        this.pictureClicked = click;
    }
    public boolean getPictureClicked(){
        return this.pictureClicked;
    }

    public void setFrontSet(boolean set){
        frontSet = set;
    }

    public boolean getFrontSet(){
        return frontSet;
    }

}