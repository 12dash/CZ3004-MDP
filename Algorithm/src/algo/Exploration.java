package algo;

import arena.*;
import values.*;
import robot.*;

public class Exploration {

    Arena arena;
    Robot robot;

    final int LONG_DETECTION = 5;
    final int SHORT_DETECTION = 5;

    public void set_values(Arena ar, Robot r) {
        this.arena = ar;
        this.robot = r;
    }

    public int detect_front() {
        Grid cur = robot.cur;
        switch (robot.or) {
            case East:
                for (int i = cur.x; i < LONG_DETECTION; i++) {
                    if (i == this.arena.n - 1) {
                        return -1;
                    }
                    if (arena.arena[cur.y][i].equals(Types.OBSTACLE)) {
                        return i;
                    }
                }
                return -1;
            case West:
                for (int i = cur.x; i > cur.x - LONG_DETECTION; i--) {
                    if (i == 0) {
                        return -1;
                    }
                    if (arena.arena[cur.y][i].equals(Types.OBSTACLE)) {
                        return i;
                    }
                }
                return -1;
            case North:
                for (int i = cur.y; i > cur.y - LONG_DETECTION; i--) {


                }


        }
        return -1;
    }

    public void start_exploration(Arena ar, Robot r) {
        set_values(ar, r);
        if (!robot.or.equals(Orientation.East)) {
            System.out.println("Following RIGHT WALL... Keep it in EAST orientation");
        } else {

        }
    }
}
