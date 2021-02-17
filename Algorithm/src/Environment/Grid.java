package Environment;

import Values.*;

public class Grid {

    private Type type;
    private boolean acc;
    private int x;
    private int y;
    private boolean explored;

    public Grid() {
    }

    public Grid(Type type, boolean acc, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.acc = true;
        this.explored = false;
    }

    public Type getType() { return this.type;}
    public void setType(Type type){this.type = type;}
    public int getX(){return this.x;}
    public int getY() {return this.y;}
    public void setAcc(boolean acc){this.acc = acc;}
    public boolean getAcc(){return this.acc;}
    public boolean isExplored(){return this.explored;}
    public void setExplored(boolean explored){this.explored =explored;}

}
