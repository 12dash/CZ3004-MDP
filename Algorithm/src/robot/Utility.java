package robot;

import arena.Arena;
import arena.Grid;
import values.Acc;
import values.Orientation;

import java.util.ArrayList;

public class Utility {

    public ArrayList<Grid> path;
    public ArrayList<Orientation> orientations;

    public Utility(ArrayList<Grid> path, ArrayList<Orientation> orientations) {
        this.path = new ArrayList<Grid>(path);
        this.orientations = new ArrayList<Orientation>(orientations);
    }

    private boolean check_change_or(int cur, int old) {
        for (int i = old + 1; i < cur - 1; i++) {
            if (this.orientations.get(i) != this.orientations.get(i + 1)) {
                return true;
            }
        }
        return false;
    }


    private String get_movement(Grid cur, Grid old, Orientation old_or) {

        if (cur.x == old.x) {
            int dif = cur.y - old.y;

            switch (old_or) {
                case East:
                    if (dif < 0) {
                        return "L";
                    } else {
                        return "R";
                    }
                case West:
                    if (dif < 0) {
                        return "R";
                    } else {
                        return "L";
                    }

            }
        } else {
            int dif = cur.x - old.x;

            switch (old_or) {
                case North:
                    if (dif > 0) {
                        return "R";
                    } else {
                        return "L";
                    }
                case South:
                    if (dif < 0) {
                        return "L";
                    } else {
                        return "R";
                    }

            }

        }
        return "S";

    }

    private boolean check_line(Grid cur, Grid old) {
        if (!((cur.x == old.x) || (cur.y == old.y))) {
            return false;
        }
        return true;
    }

    private boolean check_straight_path(Arena a, Grid cur, Grid old, String move, Orientation old_or) {

        switch (old_or) {
            case North:
                if (move.equals("L")) {
                    for (int i = old.x; i > 0; i--) {
                        if (a.arena[cur.y][i].getAcc() == Acc.FALSE) {
                            return false;
                        } else if (i == cur.x) {
                            return true;
                        }
                    }
                } else {
                    for (int i = old.x; i < a.n; i++) {
                        if (a.arena[cur.y][i].getAcc() == Acc.FALSE) {
                            return false;
                        } else if (i == cur.x) {
                            return true;
                        }
                    }
                }
            case South:
                if (move.equals("R")) {
                    for (int i = old.x; i > 0; i--) {
                        if (a.arena[cur.y][i].getAcc() == Acc.FALSE) {
                            return false;
                        } else if (i == cur.x) {
                            return true;
                        }
                    }
                } else {
                    for (int i = old.x; i < a.n; i++) {
                        if (a.arena[cur.y][i].getAcc() == Acc.FALSE) {
                            return false;
                        } else if (i == cur.x) {
                            return true;
                        }
                    }
                }
            case West:
                if (move.equals("R")) {
                    for (int i = old.y; i > 0; i--) {
                        if (a.arena[i][cur.x].getAcc() == Acc.FALSE) {
                            return false;
                        } else if (i == cur.y) {
                            return true;
                        }
                    }
                } else {
                    for (int i = old.y; i < a.m; i++) {
                        if (a.arena[i][cur.x].getAcc() == Acc.FALSE) {
                            return false;
                        } else if (i == cur.y) {
                            return true;
                        }
                    }
                }
            case East:
                if (move.equals("L")) {
                    for (int i = old.y; i > 0; i--) {
                        if (a.arena[i][cur.x].getAcc() == Acc.FALSE) {
                            return false;
                        } else if (i == cur.y) {
                            return true;
                        }
                    }
                } else {
                    for (int i = old.y; i < a.m; i++) {
                        if (a.arena[i][cur.x].getAcc() == Acc.FALSE) {
                            return false;
                        } else if (i == cur.y) {
                            return true;
                        }
                    }
                }
        }
        return true;

    }

    public Orientation new_orientation(String next_move, Orientation or) {

        Orientation next_or;

        switch (or) {
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

    public void start_path(Arena arena) {
        while (improve_path(arena)) ;
    }

    public boolean improve_path(Arena arena) {

        for (int i = this.path.size() - 1; i > 0; i--) {
            Orientation cur = this.orientations.get(i);
            for (int j = 0; j < i; j++) {
                Orientation old = this.orientations.get(j);
                if (check_change_or(i, j) && check_line(this.path.get(i), this.path.get(j))) {
                    String move = get_movement(this.path.get(i), this.path.get(j), old);
                    if (move.equals("S")) {
                        System.out.println("Error");
                        return false;
                    }
                    cur = new_orientation(move, old);
                    if (check_straight_path(arena, this.path.get(i), this.path.get(j), move, old)) {
                     //   System.out.println(this.path.get(i).x + " " + this.path.get(i).y + "\t" + this.path.get(j).x + " " + this.path.get(j).y);
                       // System.out.println(move + " " + cur + " " + old);
                        fix_path(arena, move, i, j, cur, old);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void fix_path(Arena a, String move, int cur, int old, Orientation new_or, Orientation old_or) {
        ArrayList<Grid> new_path = new ArrayList<>();
        ArrayList<Orientation> new_orientations = new ArrayList<>();

        for (int i = 0; i <= old; i++) {
            new_path.add(path.get(i));
            new_orientations.add(this.orientations.get(i));
        }

        switch (old_or) {
            case North:
                if (move.equals("L")) {
                    for (int i = path.get(old).x; i > 0; i--) {
                        new_path.add(a.arena[path.get(old).y][i]);
                        new_orientations.add(new_or);
                        if (path.get(old).x == i) {
                            break;
                        }
                    }
                }
                if (move.equals("R")) {
                    for (int i = path.get(old).x; i < a.n; i++) {
                        new_path.add(a.arena[path.get(old).y][i]);
                        new_orientations.add(new_or);
                        if (path.get(old).x == i) {
                            break;
                        }
                    }
                }
                break;
            case South:
                if (move.equals("R")) {
                    for (int i = path.get(old).x; i >= 0; i--) {
                        new_path.add(a.arena[path.get(old).y][i]);
                        new_orientations.add(new_or);
                        if (path.get(old).x == i) {
                            break;
                        }
                    }
                }
                if (move.equals("L")) {
                    for (int i = path.get(old).x; i < a.n; i++) {
                        new_path.add(a.arena[path.get(old).y][i]);
                        new_orientations.add(new_or);
                        if (path.get(old).x == i) {
                            break;
                        }
                    }
                }
                break;
            case East:
                if (move.equals("R")) {
                    for (int i = path.get(old).y; i < a.m; i++) {
                        new_path.add(a.arena[i][path.get(old).x]);
                        new_orientations.add(new_or);
                        if (path.get(cur).y == i) {
                            break;
                        }
                    }
                }
                if (move.equals("L")) {
                    for (int i = path.get(old).y; i >= 0; i--) {
                        new_path.add(a.arena[i][path.get(old).x]);
                        new_orientations.add(new_or);
                        if (path.get(cur).y == i) {

                            break;
                        }
                    }

                }
                break;
            case West:
                if (move.equals("L")) {
                    for (int i = path.get(old).y; i < a.m; i++) {
                        System.out.println("");
                        new_path.add(a.arena[i][path.get(old).x]);
                        new_orientations.add(new_or);
                        if (path.get(cur).y == i) {
                            break;
                        }
                    }
                }
                if (move.equals("R")) {
                    for (int i = path.get(old).y; i >= 0; i--) {
                        new_path.add(a.arena[i][path.get(old).x]);
                        new_orientations.add(new_or);
                        if (path.get(cur).y == i) {
                            break;
                        }
                    }
                }
                break;
        }


        for (int i = cur; i < this.path.size(); i++) {
            new_path.add(this.path.get(i));
            new_orientations.add(this.orientations.get(i));
        }
        this.path = new_path;
        this.orientations = new_orientations;
    }
}
