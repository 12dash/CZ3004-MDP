package arena;

import values.Types;
import values.Acc;

public class Grid {

    private Types type;
    private Acc acc;
    private int x;
    private int y;
    private int pos[] = new int[2];


    public Grid(Types a, int x, int y){
        this.type = a;
        this.x = x;
        this.y = y;
        if (this.type == Types.OBSTACLE){
            this.acc = Acc.FALSE;
        }
        pos[0] = x;
        pos[1] = y;

    }

    public Types getType() {
        return type;
    }

    public void setType(Types a) {
        this.type = a;
    }

    public int[] get_pos(){
        return this.pos;
    }

    public Acc get_acc(){
        return this.acc;
    }

    public void set_acc(Acc acc){
        this.acc = acc;
    }
}
