package Robot;

import Values.Orientation;
import Environment.*;
import Values.Type;

public class Sensors {

    int x;
    int y;
    int min_distance;
    int max_distance;
    Orientation or;

    public Sensors(int x, int y, String type) {
        this.x = x;
        this.y = y;

        if (type.equals("Long")) {
            this.min_distance = 1;
            this.max_distance = 5;
        } else {
            this.min_distance = 0;
            this.max_distance = 4;
        }
    }

    public void updateOr(Orientation or) {
        this.or = or;
    }

    public void updatePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void line1(Arena ar) {
        switch (or) {
            case North:
                for (int i = min_distance; i < max_distance; i++) {
                    if (y - i < 0) {
                        break;
                    }
                    ar.grids[y - i][x].setExplored(true);
                    if (ar.grids[y - i][x].getType() == Type.OBSTACLE) {
                        break;
                    }
                }
                break;
            case South:
                for (int i = min_distance; i < max_distance; i++) {
                    if (y + i >= Constants.ROWS) {
                        break;
                    }
                    ar.grids[y + i][x].setExplored(true);
                    if (ar.grids[y + i][x].getType() == Type.OBSTACLE) {
                        break;
                    }
                }
                break;
            case East:
                for (int i = min_distance; i < max_distance; i++) {
                    if (x + i >= Constants.COLUMNS) {
                        break;
                    }
                    ar.grids[y][x + i].setExplored(true);
                    if (ar.grids[y][x + i].getType() == Type.OBSTACLE) {
                        break;
                    }
                }
                break;
            case West:
                for (int i = min_distance; i < max_distance; i++) {
                    if (x - i < 0) {
                        break;
                    }
                    ar.grids[y][x - i].setExplored(true);
                    if (ar.grids[y][x - i].getType() == Type.OBSTACLE) {
                        break;
                    }
                }
                break;
        }
    }

    public void line2(Arena ar) {
        switch (or) {
            case North:
                for (int i = min_distance; i < max_distance; i++) {
                    if (y - i < 0) {
                        break;
                    }
                    ar.grids[y - i][x - 1].setExplored(true);
                    if (ar.grids[y - i][x - 1].getType() == Type.OBSTACLE) {
                        break;
                    }
                }
                break;
            case South:
                for (int i = min_distance; i < max_distance; i++) {
                    if (y + i >= Constants.ROWS) {
                        break;
                    }
                    ar.grids[y + i][x + 1].setExplored(true);
                    if (ar.grids[y + i][x + 1].getType() == Type.OBSTACLE) {
                        break;
                    }
                }
                break;
            case East:
                for (int i = min_distance; i < max_distance; i++) {
                    if (x + i >= Constants.COLUMNS) {
                        break;
                    }
                    ar.grids[y - 1][x + i].setExplored(true);
                    if (ar.grids[y - 1][x + i].getType() == Type.OBSTACLE) {
                        break;
                    }
                }
                break;
            case West:
                for (int i = min_distance; i < max_distance; i++) {
                    if (x - i < 0) {
                        break;
                    }
                    ar.grids[y + 1][x - i].setExplored(true);
                    if (ar.grids[y + 1][x - i].getType() == Type.OBSTACLE) {
                        break;
                    }
                }
                break;
        }
    }

    public void line3(Arena ar) {
        switch (or) {
            case North:
                for (int i = min_distance; i < max_distance; i++) {
                    if (y - i < 0) {
                        break;
                    }
                    ar.grids[y - i][x + 1].setExplored(true);
                    if (ar.grids[y - i][x + 1].getType() == Type.OBSTACLE) {
                        break;
                    }
                }
                break;
            case South:
                for (int i = min_distance; i < max_distance; i++) {
                    if (y + i >= Constants.ROWS) {
                        break;
                    }
                    ar.grids[y + i][x - 1].setExplored(true);
                    if (ar.grids[y + i][x - 1].getType() == Type.OBSTACLE) {
                        break;
                    }
                }
                break;
            case East:
                for (int i = min_distance; i < max_distance; i++) {
                    if (x + i >= Constants.COLUMNS) {
                        break;
                    }
                    ar.grids[y + 1][x + i].setExplored(true);
                    if (ar.grids[y + 1][x + i].getType() == Type.OBSTACLE) {
                        break;
                    }
                }
                break;
            case West:
                for (int i = min_distance; i < max_distance; i++) {
                    if (x - i < 0) {
                        break;
                    }
                    ar.grids[y - 1][x - i].setExplored(true);
                    if (ar.grids[y - 1][x - i].getType() == Type.OBSTACLE) {
                        break;
                    }
                }
                break;
        }
    }

    public void sense(Arena ar) {
        line1(ar);
        line2(ar);
        line3(ar);
    }
}
