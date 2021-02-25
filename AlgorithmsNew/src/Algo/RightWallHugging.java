package Algo;

import Environment.*;
import Robot.Robot;
import Robot.RobotSimulator;
import Values.Orientation;

public class RightWallHugging{

    Arena arena;
    RobotSimulator robot;

    public Grid nextGrid;
    public Orientation nextOr;

    public RightWallHugging(Arena arena, RobotSimulator robot){
        this.arena = arena;
        this.robot = robot;
    }

    public boolean detectPossible(Grid temp) {
        int x = temp.getX();
        int y = temp.getY();

        if (!temp.getAcc()) {
            return false;
        } else if ((x < 0) || (x >= ArenaConstants.ARENA_COLS)) {
            return false;
        } else if ((y < 0) || (y >= ArenaConstants.ARENA_ROWS)) {
            return false;
        }
        return true;
    }

    public boolean moveForward() {
        System.out.println("forward");
        Grid temp;
        switch (robot.getOrientation()) {
            case North:
                temp = arena.grids[robot.getCur().getY() - 1][robot.getCur().getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.North;
                    return true;
                }
                break;

            case South:
                temp = arena.grids[robot.getCur().getY() + 1][robot.getCur().getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.South;
                    return true;
                }
                break;
            case East:
                temp = arena.grids[robot.getCur().getY()][robot.getCur().getX() + 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.East;
                    return true;
                }
                break;
            case West:
                temp = arena.grids[robot.getCur().getY()][robot.getCur().getX() - 1];
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
        switch (robot.getOrientation()) {
            case North:
                temp = arena.grids[robot.getCur().getY() + 1][robot.getCur().getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.South;
                    return true;
                }
                break;
            case South:
                temp = arena.grids[robot.getCur().getY() - 1][robot.getCur().getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.North;
                    return true;
                }
                break;
            case East:
                temp = arena.grids[robot.getCur().getY()][robot.getCur().getX() - 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.West;
                    return true;
                }
                break;
            case West:
                temp = arena.grids[robot.getCur().getY()][robot.getCur().getX() + 1];
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
        switch (robot.getOrientation()) {
            case North:
                temp = arena.grids[robot.getCur().getY()][robot.getCur().getX() - 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.West;
                    return true;
                }
                break;
            case South:
                temp = arena.grids[robot.getCur().getY()][robot.getCur().getX() + 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.East;
                    return true;
                }
                break;

            case East:
                temp = arena.grids[robot.getCur().getY() - 1][robot.getCur().getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.North;
                    return true;
                }
                break;
            case West:
                temp = arena.grids[robot.getCur().getY() + 1][robot.getCur().getX()];
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
        System.out.println("Right");
        Grid temp;
        switch (robot.getOrientation()) {
            case North:
                temp = arena.grids[robot.getCur().getY()][robot.getCur().getX() + 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.East;
                    return true;
                }
                break;

            case South:
                temp = arena.grids[robot.getCur().getY()][robot.getCur().getX() - 1];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.West;
                    return true;
                }
                break;
            case East:
                temp = arena.grids[robot.getCur().getY() + 1][robot.getCur().getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.South;
                    return true;
                }
                break;
            case West:
                temp = arena.grids[robot.getCur().getY() - 1][robot.getCur().getX()];
                if (detectPossible(temp)) {
                    nextGrid = temp;
                    nextOr = Orientation.North;
                    return true;
                }
                break;
        }
        return false;
    }
    private void setNext() {
        this.robot.updatePosition(this.nextGrid, this.nextOr);
        robot.sense(arena);
    }

    public void move() {
        robot.sense(arena);
        if (moveRight()) {
            //System.out.println("Move Right");
        } else if (moveForward()) {
           // System.out.println("Move Forward");
        } else if (moveLeft()) {
           // System.out.println("Move Left");
        } else if (moveBack()) {
           // System.out.println("Move Backward");
        }
        setNext();
    }

}
