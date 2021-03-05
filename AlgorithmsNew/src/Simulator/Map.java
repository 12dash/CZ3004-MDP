package Simulator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;


import Environment.*;
import Values.*;
import Robot.*;
import Robot.Robot;

public class Map extends JPanel {

    public Arena arena;
    public Robot robot;
    public RobotSimulator robotSimulator;
    public RobotReal robotReal;
    public int waypoint_x;
    public int waypoint_y;
    public boolean simulate;
    boolean isExploration = false;

    public Map(Arena arena, boolean simulate) {
        this.arena = arena;
        this.simulate = simulate;

        if (simulate) {
            this.robotSimulator = new RobotSimulator(arena.grids[RobotConstants.START_ROW][RobotConstants.START_COL]);
            robotSimulator.setOrientation(RobotConstants.START_DIR);
            this.robot = robotSimulator;
        }else{
            this.robotReal = new RobotReal(arena.grids[RobotConstants.START_ROW][RobotConstants.START_COL]);
            robotReal.setOrientation(RobotConstants.START_DIR);
            this.robot = robotReal;
        }
    }

    // Override for exploration
    public Map(Arena arena, boolean simulate, boolean exploration) {
        this.arena = arena;
        this.simulate = simulate;
        this.isExploration = exploration;

        if (simulate) {
            this.robotSimulator = new RobotSimulator(arena.grids[RobotConstants.START_ROW][RobotConstants.START_COL]);
            robotSimulator.setOrientation(RobotConstants.START_DIR);
            this.robot = robotSimulator;
        }else{
            this.robotReal = new RobotReal(arena.grids[RobotConstants.START_ROW][RobotConstants.START_COL]);
            robotReal.setOrientation(RobotConstants.START_DIR);
            this.robot = robotReal;
        }
    }

    public void resetMap(boolean simulate){
        this.arena = new Arena(true);
        if (simulate) {
            this.robotSimulator = new RobotSimulator(arena.grids[RobotConstants.START_ROW][RobotConstants.START_COL]);
            robotSimulator.setOrientation(RobotConstants.START_DIR);
            this.robot = robotSimulator;
        }else{
            this.robotReal = new RobotReal(arena.grids[RobotConstants.START_ROW][RobotConstants.START_COL]);
            robotReal.setOrientation(RobotConstants.START_DIR);
            this.robot = robotReal;
        }
    }


    private static class _DisplayGrid {
        public final int cellX;
        public final int cellY;
        public final int cellSize;

        public _DisplayGrid(int borderX, int borderY, int borderSize) {
            this.cellX = borderX + SimulatorConstants.CELL_LINE_WEIGHT;
            this.cellY = SimulatorConstants.MAP_H - (borderY - SimulatorConstants.CELL_LINE_WEIGHT);
            this.cellSize = borderSize - (SimulatorConstants.CELL_LINE_WEIGHT * 2);
        }
    }

    protected static boolean inStartZone(int row, int col) {
        return (row >= ArenaConstants.START_ROW - 1 && row <= ArenaConstants.START_ROW + 1 && col >= ArenaConstants.START_COL - 1 && col <= ArenaConstants.START_COL + 1);
    }

    public static boolean inGoalZone(int row, int col) {
        return (row >= ArenaConstants.GOAL_ROW - 1 && row <= ArenaConstants.GOAL_ROW + 1 && col >= ArenaConstants.GOAL_COL - 1 && col <= ArenaConstants.GOAL_COL + 1);
    }

    public void paintComponent(Graphics g) {

        // Create a two-dimensional array of _DisplayCell objects for rendering.
        super.paintComponent(g);

        Map._DisplayGrid[][] _gridCells = new Map._DisplayGrid[ArenaConstants.ARENA_ROWS][ArenaConstants.ARENA_COLS];

        for (int row = 0; row < ArenaConstants.ARENA_ROWS; row++) {
            for (int col = 0; col < ArenaConstants.ARENA_COLS; col++) {
                _gridCells[row][col] = new _DisplayGrid(col * SimulatorConstants.CELL_SIZE, (ArenaConstants.ARENA_ROWS - row) * SimulatorConstants.CELL_SIZE, SimulatorConstants.CELL_SIZE);
            }
        }

        for (int row = 0; row < ArenaConstants.ARENA_ROWS; row++) {
            for (int col = 0; col < ArenaConstants.ARENA_COLS; col++) {
                Color cellColor = SimulatorConstants.C_FREE;
                if (inStartZone(row, col))
                    cellColor = SimulatorConstants.C_START;
                else if (inGoalZone(row, col))
                    cellColor = SimulatorConstants.C_GOAL;
                else if (this.arena.grids[row][col].getType() == Type.OBSTACLE && this.arena.grids[row][col].isExplored() ){
                    cellColor = SimulatorConstants.C_OBSTACLE;
                }
                else if (!this.arena.grids[row][col].isExplored()){
                    cellColor = SimulatorConstants.C_UNEXPLORED;
                }
                g.setColor(cellColor);

                g.fillRect(_gridCells[row][col].cellX + SimulatorConstants.MAP_X_OFFSET, _gridCells[row][col].cellY, _gridCells[row][col].cellSize, _gridCells[row][col].cellSize);
            }
        }

        if(!simulate) {
            // Paint Fastest Path
            for (Grid gr : robotReal.getPath()) {
                g.setColor(SimulatorConstants.C_PATH);
                g.fillRect(_gridCells[gr.getY()][gr.getX()].cellX + SimulatorConstants.MAP_X_OFFSET, _gridCells[gr.getY()][gr.getX()].cellY, _gridCells[gr.getY()][gr.getX()].cellSize, _gridCells[gr.getY()][gr.getX()].cellSize);
            }

            if(!isExploration) {
                // Paint WayPoint
                g.setColor(SimulatorConstants.C_WAYPOINT);
                g.fillRect(_gridCells[waypoint_y][waypoint_x].cellX + SimulatorConstants.MAP_X_OFFSET, _gridCells[waypoint_y][waypoint_x].cellY, _gridCells[waypoint_y][waypoint_x].cellSize, _gridCells[waypoint_y][waypoint_x].cellSize);
            }
            // Paint the robot on-screen.
            g.setColor(SimulatorConstants.C_ROBOT);
            int r = ArenaConstants.ARENA_ROWS - this.robotReal.getRobotPosRow();
            int c = robotReal.getRobotPosCol();

            g.fillOval((c - 1) * SimulatorConstants.CELL_SIZE + SimulatorConstants.ROBOT_X_OFFSET + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - (r * SimulatorConstants.CELL_SIZE + SimulatorConstants.ROBOT_Y_OFFSET), SimulatorConstants.ROBOT_W, SimulatorConstants.ROBOT_H);

            // Paint the robot's direction indicator on-screen.
            g.setColor(SimulatorConstants.C_ROBOT_DIR);
            Orientation o = robot.getOrientation();
            switch (o) {
                case North:
                    g.fillOval(c * SimulatorConstants.CELL_SIZE + 10 + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - r * SimulatorConstants.CELL_SIZE - 15, SimulatorConstants.ROBOT_DIR_W, SimulatorConstants.ROBOT_DIR_H);
                    break;
                case East:
                    g.fillOval(c * SimulatorConstants.CELL_SIZE + 35 + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - r * SimulatorConstants.CELL_SIZE + 10, SimulatorConstants.ROBOT_DIR_W, SimulatorConstants.ROBOT_DIR_H);
                    break;
                case South:
                    g.fillOval(c * SimulatorConstants.CELL_SIZE + 10 + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - r * SimulatorConstants.CELL_SIZE + 35, SimulatorConstants.ROBOT_DIR_W, SimulatorConstants.ROBOT_DIR_H);
                    break;
                case West:
                    g.fillOval(c * SimulatorConstants.CELL_SIZE - 15 + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - r * SimulatorConstants.CELL_SIZE + 10, SimulatorConstants.ROBOT_DIR_W, SimulatorConstants.ROBOT_DIR_H);
                    break;
                case NorthEast:
                    g.fillOval(c * SimulatorConstants.CELL_SIZE + 20 + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - r * SimulatorConstants.CELL_SIZE - 15, SimulatorConstants.ROBOT_DIR_W, SimulatorConstants.ROBOT_DIR_H);
                    break;
                case NorthWest:
                    g.fillOval(c * SimulatorConstants.CELL_SIZE - 10 + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - r * SimulatorConstants.CELL_SIZE - 1, SimulatorConstants.ROBOT_DIR_W, SimulatorConstants.ROBOT_DIR_H);
                    break;
            }
        }

        else {
            // Paint the robot on-screen.
            g.setColor(SimulatorConstants.C_ROBOT);
            int r = ArenaConstants.ARENA_ROWS - this.robot.getCur().getY();
            int c = robot.getCur().getX();

            g.fillOval((c - 1) * SimulatorConstants.CELL_SIZE + SimulatorConstants.ROBOT_X_OFFSET + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - (r * SimulatorConstants.CELL_SIZE + SimulatorConstants.ROBOT_Y_OFFSET), SimulatorConstants.ROBOT_W, SimulatorConstants.ROBOT_H);

            // Paint the robot's direction indicator on-screen.
            g.setColor(SimulatorConstants.C_ROBOT_DIR);
            Orientation o = robot.getOrientation();
            switch (o) {
                case North:
                    g.fillOval(c * SimulatorConstants.CELL_SIZE + 10 + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - r * SimulatorConstants.CELL_SIZE - 15, SimulatorConstants.ROBOT_DIR_W, SimulatorConstants.ROBOT_DIR_H);
                    break;
                case East:
                    g.fillOval(c * SimulatorConstants.CELL_SIZE + 35 + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - r * SimulatorConstants.CELL_SIZE + 10, SimulatorConstants.ROBOT_DIR_W, SimulatorConstants.ROBOT_DIR_H);
                    break;
                case South:
                    g.fillOval(c * SimulatorConstants.CELL_SIZE + 10 + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - r * SimulatorConstants.CELL_SIZE + 35, SimulatorConstants.ROBOT_DIR_W, SimulatorConstants.ROBOT_DIR_H);
                    break;
                case West:
                    g.fillOval(c * SimulatorConstants.CELL_SIZE - 15 + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - r * SimulatorConstants.CELL_SIZE + 10, SimulatorConstants.ROBOT_DIR_W, SimulatorConstants.ROBOT_DIR_H);
                    break;
                case NorthEast:
                    g.fillOval(c * SimulatorConstants.CELL_SIZE + 20 + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - r * SimulatorConstants.CELL_SIZE - 15, SimulatorConstants.ROBOT_DIR_W, SimulatorConstants.ROBOT_DIR_H);
                    break;
                case NorthWest:
                    g.fillOval(c * SimulatorConstants.CELL_SIZE - 10 + SimulatorConstants.MAP_X_OFFSET, SimulatorConstants.MAP_H - r * SimulatorConstants.CELL_SIZE - 1, SimulatorConstants.ROBOT_DIR_W, SimulatorConstants.ROBOT_DIR_H);
                    break;
            }
        }

    }

    public void setWaypoint(int x, int y){
        waypoint_x = x;
        waypoint_y = y;
    }

}