package arena;

import values.Types;
import values.Acc;

public class Grid {

    private Types type;
    private Acc acc;
    private int x;
    private int y;
    private int pos[] = new int[2];


    public Grid(Types a, int x, int y) {
        this.type = a;
        this.x = x;
        this.y = y;
        if (this.type == Types.OBSTACLE) {
            this.acc = Acc.FALSE;
        } else {
            this.acc = Acc.TRUE;
        }
        pos[0] = y;
        pos[1] = x;
    }

    public Types getType() {
        return type;
    }

    public void setType(Types a) {
        this.type = a;
    }

    public int[] getPos() {
        return this.pos;
    }

    public Acc getAcc() {
        return this.acc;
    }

    public void setAcc(Acc acc) {
        this.acc = acc;
    }
}
