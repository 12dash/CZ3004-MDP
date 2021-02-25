package Algo;

import Environment.*;
import Robot.*;
import Utility.PrintConsole;
import Values.Orientation;

import java.util.ArrayList;


public class Exploration {

    public Arena arena;
    public Robot robot;

    public Grid nextGrid;
    public Orientation nextOr;

    public double numberCellExplored = 0;
    public double percentCurrentExploration = 0;

    public ArrayList<Grid> unexplored = new ArrayList<Grid>();

    public Exploration(Arena arena, Robot robot) {
        this.arena = arena;
        this.robot = robot;
    }

    public boolean detectPossible(Grid temp) {
        int x = temp.getX();
        int y = temp.getY();

        if (!temp.getAcc()) {
            return false;
        } else if ((x < 0) || (x >= Constants.COLUMNS)) {
            return false;
        } else if ((y < 0) || (y >= Constants.ROWS)) {
            return false;
        }
        return true;
    }

    public boolean moveForward() {
        Grid temp;
        switch (robot.cur_or) {
            case North:
                temp = arena.grids[robot.cur.getY() - 1][robot.cur.getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.North;
                    return true;
                }
                break;

            case South:
                temp = arena.grids[robot.cur.getY() + 1][robot.cur.getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.South;
                    return true;
                }
                break;
            case East:
                temp = arena.grids[robot.cur.getY()][robot.cur.getX() + 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.East;
                    return true;
                }
                break;
            case West:
                temp = arena.grids[robot.cur.getY()][robot.cur.getX() - 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.West;
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean moveBack() {
        Grid temp;
        switch (robot.cur_or) {
            case North:
                temp = arena.grids[robot.cur.getY() + 1][robot.cur.getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.South;
                    return true;
                }
                break;
            case South:
                temp = arena.grids[robot.cur.getY() - 1][robot.cur.getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.North;
                    return true;
                }
                break;
            case East:
                temp = arena.grids[robot.cur.getY()][robot.cur.getX() - 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.West;
                    return true;
                }
                break;
            case West:
                temp = arena.grids[robot.cur.getY()][robot.cur.getX() + 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.East;
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean moveLeft() {
        Grid temp;
        switch (robot.cur_or) {
            case North:
                temp = arena.grids[robot.cur.getY()][robot.cur.getX() - 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.West;
                    return true;
                }
                break;
            case South:
                temp = arena.grids[robot.cur.getY()][robot.cur.getX() + 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.East;
                    return true;
                }
                break;

            case East:
                temp = arena.grids[robot.cur.getY() - 1][robot.cur.getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.North;
                    return true;
                }
                break;
            case West:
                temp = arena.grids[robot.cur.getY() + 1][robot.cur.getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.South;
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean moveRight() {
        Grid temp;
        switch (robot.cur_or) {
            case North:
                temp = arena.grids[robot.cur.getY()][robot.cur.getX() + 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.East;
                    return true;
                }
                break;

            case South:
                temp = arena.grids[robot.cur.getY()][robot.cur.getX() - 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.West;
                    return true;
                }
                break;
            case East:
                temp = arena.grids[robot.cur.getY() + 1][robot.cur.getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.South;
                    return true;
                }
                break;
            case West:
                temp = arena.grids[robot.cur.getY() - 1][robot.cur.getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.North;
                    return true;
                }
                break;
        }
        return false;
    }

    public void calNumberCellExplored() {
        int num = 0;
        for (int row = 0; row < Constants.ROWS; row++) {
            for (int col = 0; col < Constants.COLUMNS; col++) {
                if (arena.grids[row][col].isExplored()) {
                    num += 1;
                }
            }
        }
        this.numberCellExplored = num;
        this.percentCurrentExploration = (num / 300.0) * 100;
    }

    private void setNext() {
        this.robot.updatePosition(this.nextGrid, this.nextOr);
        robot.sense(arena);
    }

    public void move() {
        robot.sense(arena);
        if (moveRight()) {
            System.out.println("Move Right");
        } else if (moveForward()) {
            System.out.println("Move Forward");
        } else if (moveLeft()) {
            System.out.println("Move Left");
        } else if (moveBack()) {
            System.out.println("Move Backward");
        }
        setNext();
    }

    public Grid getFreeNeighbour(Grid temp) {
        int x = temp.getX();
        int y = temp.getY();
        for (int i = 1; i < 3; i++) {
            try {
                if (arena.grids[y][i + x].getAcc()) {
                    return arena.grids[y][i + x];
                }
            } catch (Exception ignored) {
            }
            try {
                if (arena.grids[y][x - i].getAcc()) {
                    return arena.grids[y][x - i];
                }
            } catch (Exception ignored) {
            }
        }
        for (int i = 1; i < 4; i++) {
            try {
                if (arena.grids[y + i][x].getAcc()) {
                    return arena.grids[y + i][x];
                }
            } catch (Exception ignored) {
            }
            try {
                if (arena.grids[y - i][x - i].getAcc()) {
                    return arena.grids[y - i][x];
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public void getUnexplored() {
        unexplored.removeAll(unexplored);
        for (int i = 0; i < Constants.ROWS; i++) {
            for (int j = 0; j < Constants.COLUMNS; j++) {
                if (!arena.grids[i][j].isExplored()) {
                    unexplored.add(arena.grids[i][j]);
                }
            }
        }
    }

    public Grid getNextFree() {
        for (Grid grid : unexplored) {
            if (grid.getAcc()) {
                return grid;
            }
        }
        return null;
    }

    public void goToNextGrid() {
        robot.sense(arena);
        for (int i = 0; i < robot.path.size(); i++) {
            robot.updatePosition(robot.path.get(i), robot.orientations.get(i));
            robot.sense(arena);
        }
    }

    public void startExploration(double percentExploration) {

        if (percentExploration == 100) {
            move();
            while (!robot.cur.equals(arena.grids[RobotConstants.ROBOT_START_Y][RobotConstants.ROBOT_START_X])) {
                move();
            }
            calNumberCellExplored();
            getUnexplored();
            PrintConsole.getExploration(arena);
            System.out.println(unexplored.size());
            while (!(numberCellExplored == 300)) {
                Grid temp = getNextFree();
                if (temp == null) {
                    temp = getFreeNeighbour(unexplored.get(0));
                }
                AStar a = new AStar();
                a.startSearch(arena, robot.cur, temp, false);
                robot.path = a.solution;
                robot.getOrientation();
                goToNextGrid();
                calNumberCellExplored();
                System.out.println("Unexplored size : " + unexplored.size());
                getUnexplored();
            }
        } else {
            boolean out = false;
            move();
            calNumberCellExplored();
            while (!robot.cur.equals(arena.grids[RobotConstants.ROBOT_START_Y][RobotConstants.ROBOT_START_X])) {
                move();
                calNumberCellExplored();
                System.out.println("% explored "+this.percentCurrentExploration);
                if (this.percentCurrentExploration > percentExploration) {
                    out = true;
                    System.out.println("Break");
                    break;
                }
            }
            while ((this.percentCurrentExploration <= percentExploration) && (!out)) {
                getUnexplored();
                Grid temp = getNextFree();
                if (temp == null) {
                    temp = getFreeNeighbour(unexplored.get(0));
                }
                AStar a = new AStar();
                a.startSearch(arena, robot.cur, temp, false);
                robot.path = a.solution;
                robot.getOrientation();
                goToNextGrid();
                calNumberCellExplored();
                System.out.println("Unexplored size : " + unexplored.size());
            }
        }
        PrintConsole.getExploration(arena);
        System.out.println(unexplored.size());
    }
}
