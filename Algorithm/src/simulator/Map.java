package Simulator;

import Environment.*;
import Values.*;
import Robot.Robot;
import Robot.*;

import java.awt.*;
import javax.swing.*;

public class Map extends JPanel {
    public Arena arena;
    public Robot robot;
    public Grid wayPoint = null;
    public int step = 0;
    public double percent = 100;
    public boolean fPath = true;
    public String maxTime = "6:00";
    Map(Arena arena) {
        this.arena = arena;
        this.arena.initializeArena();
        this.robot = new Robot(arena.grids[-1 + Constants.ROWS - RobotConstants.START_ROW][RobotConstants.START_COL]);
    }

    public void reset() {
        this.robot = new Robot(arena.grids[-1 + Constants.ROWS - RobotConstants.START_ROW][RobotConstants.START_COL]);
        this.wayPoint = null;
        for (int i = 0; i < Constants.ROWS; i++) {
            for (int j = 0; j < Constants.COLUMNS; j++) {
                arena.grids[i][j].setExplored(false);
            }
        }
    }

    public void resetRobot() {
        this.robot = new Robot(arena.grids[-1 + Constants.ROWS - RobotConstants.START_ROW][RobotConstants.START_COL]);
    }

    public void setWayPoint(Grid wayPoint) {
        this.wayPoint = wayPoint;
    }

    private boolean inStartZone(int row, int col) {
        return (row <= Constants.GOAL_ROW + 1 && row >= Constants.GOAL_ROW - 1 && col >= 0 && col <= 2);
    }

    private boolean inGoalZone(int row, int col) {
        return (row >= 0 && row <= 2 && col <= Constants.GOAL_COL + 1 && col >= Constants.GOAL_COL - 1);
    }

    public void paintComponent(Graphics g) {
        // Create a two-dimensional array of _DisplayCell objects for rendering.
        super.paintComponent(g);
        Map._DisplayGrid[][] _gridCells = new Map._DisplayGrid[Constants.ARENA_ROWS][Constants.ARENA_COLS];
        for (int row = 0; row < Constants.ARENA_ROWS; row++) {
            for (int col = 0; col < Constants.ARENA_COLS; col++) {
                _gridCells[row][col] = new Map._DisplayGrid(col * Constants.CELL_SIZE, (Constants.ARENA_ROWS - row) * Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }

        // Paint the cells with the appropriate colors.
        for (int row = 0; row < Constants.ARENA_ROWS; row++) {
            for (int col = 0; col < Constants.ARENA_COLS; col++) {
                Color cellColor = null;
                if (inStartZone(row, col))
                    cellColor = Constants.C_START;
                else if (inGoalZone(row, col))
                    cellColor = Constants.C_GOAL;
                else if (!fPath) {
                    if (arena.grids[row][col].isExplored()) {
                        if (arena.grids[row][col].getType() == Type.OBSTACLE)
                            cellColor = Constants.C_OBSTACLE;
                        else
                            cellColor = Constants.C_FREE;
                    } else {
                        cellColor = Constants.C_UNEXPLORED;
                    }
                } else {
                    cellColor = Constants.C_FREE;
                    if (arena.grids[row][col].getType() == Type.OBSTACLE)
                        cellColor = Constants.C_OBSTACLE;
                    else if (arena.grids[row][col].equals(this.wayPoint)) {
                        cellColor = Constants.C_WAYPOINT;
                    }
                }
                g.setColor(cellColor);
                g.fillRect(_gridCells[row][col].cellX + Constants.MAP_X_OFFSET, _gridCells[row][col].cellY, _gridCells[row][col].cellSize, _gridCells[row][col].cellSize);
            }
        }


        // Paint the robot on-screen.
        g.setColor(Constants.C_ROBOT);
        int r = Constants.ARENA_ROWS - this.robot.cur.getY();
        int c = robot.cur.getX();

        g.fillOval((c - 1) * Constants.CELL_SIZE + Constants.ROBOT_X_OFFSET + Constants.MAP_X_OFFSET, Constants.MAP_H - (r * Constants.CELL_SIZE + Constants.ROBOT_Y_OFFSET), Constants.ROBOT_W, Constants.ROBOT_H);

        // Paint the robot's direction indicator on-screen.
        g.setColor(Constants.C_ROBOT_DIR);
        Orientation o = robot.cur_or;
        switch (o) {
            case North:
                g.fillOval(c * Constants.CELL_SIZE + 10 + Constants.MAP_X_OFFSET, Constants.MAP_H - r * Constants.CELL_SIZE - 15, Constants.ROBOT_DIR_W, Constants.ROBOT_DIR_H);
                break;
            case East:
                g.fillOval(c * Constants.CELL_SIZE + 35 + Constants.MAP_X_OFFSET, Constants.MAP_H - r * Constants.CELL_SIZE + 10, Constants.ROBOT_DIR_W, Constants.ROBOT_DIR_H);
                break;
            case South:
                g.fillOval(c * Constants.CELL_SIZE + 10 + Constants.MAP_X_OFFSET, Constants.MAP_H - r * Constants.CELL_SIZE + 35, Constants.ROBOT_DIR_W, Constants.ROBOT_DIR_H);
                break;
            case West:
                g.fillOval(c * Constants.CELL_SIZE - 15 + Constants.MAP_X_OFFSET, Constants.MAP_H - r * Constants.CELL_SIZE + 10, Constants.ROBOT_DIR_W, Constants.ROBOT_DIR_H);
                break;
            case NorthEast:
                g.fillOval(c * Constants.CELL_SIZE + 20 + Constants.MAP_X_OFFSET, Constants.MAP_H - r * Constants.CELL_SIZE - 15, Constants.ROBOT_DIR_W, Constants.ROBOT_DIR_H);
                break;
            case NorthWest:
                g.fillOval(c * Constants.CELL_SIZE - 10 + Constants.MAP_X_OFFSET, Constants.MAP_H - r * Constants.CELL_SIZE - 1, Constants.ROBOT_DIR_W, Constants.ROBOT_DIR_H);
                break;
        }
    }

    private class _DisplayGrid {
        public final int cellX;
        public final int cellY;
        public final int cellSize;

        public _DisplayGrid(int borderX, int borderY, int borderSize) {
            this.cellX = borderX + Constants.CELL_LINE_WEIGHT;
            this.cellY = Constants.MAP_H - (borderY - Constants.CELL_LINE_WEIGHT);
            this.cellSize = borderSize - (Constants.CELL_LINE_WEIGHT * 2);
        }
    }

    public void setAllExplored() {
        for (int row = 0; row < arena.grids.length; row++) {
            for (int col = 0; col < arena.grids[0].length; col++) {
                arena.grids[row][col].setExplored(true);
            }
        }
    }

    public void setAllUnexplored() {
        for (int row = 0; row < arena.grids.length; row++) {
            for (int col = 0; col < arena.grids[0].length; col++) {
                if (inStartZone(row, col) || inGoalZone(row, col)) {
                    arena.grids[row][col].setExplored(true);
                } else {
                    arena.grids[row][col].setExplored(false);
                }
            }
        }
    }

}