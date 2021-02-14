package arena;

import values.Orientation;
import values.Types;

import java.awt.*;
import javax.swing.*;

import robot.Robot;

public class Temp extends JPanel {

    public Arena arena;
    public Robot bot;

    public Temp(Arena arena, Robot robot){
        this.arena = arena;
        this.bot = robot;

    }

    public void setAllUnexplored() {
        for (int row = 0; row < arena.arena.length; row++) {
            for (int col = 0; col < arena.arena[0].length; col++) {
                if (inStartZone(row, col) || inGoalZone(row, col)) {
                    arena.arena[row][col].setExplored(true);
                } else {
                    arena.arena[row][col].setExplored(false);
                }
            }
        }
    }

    private boolean inStartZone(int row, int col) {
        return (row <= ArenaConstants.GOAL_ROW + 1 && row >= ArenaConstants.GOAL_ROW - 1 && col >= 0 && col <= 2);
    }

    private boolean inGoalZone(int row, int col) {
        return (row >= 0 && row <= 2 && col <= ArenaConstants.GOAL_COL + 1 && col >= ArenaConstants.GOAL_COL - 1);
    }

    /**
     * Overrides JComponent's paintComponent() method. It creates a two-dimensional array of _DisplayCell objects
     * to store the current map state. Then, it paints square cells for the grid with the appropriate colors as
     * well as the robot on-screen.
     */
    public void paintComponent(Graphics g) {
        // Create a two-dimensional array of _DisplayCell objects for rendering.
        Temp._DisplayGrid[][] _gridCells = new Temp._DisplayGrid[ArenaConstants.ARENA_ROWS][ArenaConstants.ARENA_COLS];
        for (int row = 0; row < ArenaConstants.ARENA_ROWS; row++) {
            for (int col = 0; col < ArenaConstants.ARENA_COLS; col++) {
                _gridCells[row][col] = new Temp._DisplayGrid(col * ArenaConstants.CELL_SIZE, (ArenaConstants.ARENA_ROWS - row) * ArenaConstants.CELL_SIZE, ArenaConstants.CELL_SIZE);
            }
        }

        // Paint the cells with the appropriate colors.
        for (int row = 0; row < ArenaConstants.ARENA_ROWS; row++) {
            for (int col = 0; col < ArenaConstants.ARENA_COLS; col++) {
                Color cellColor;

                if (inStartZone(row, col))
                    cellColor = ArenaConstants.C_START;
                else if (inGoalZone(row, col))
                    cellColor = ArenaConstants.C_GOAL;
                else {
                    if (!arena.arena[row][col].isExplored())
                        cellColor = ArenaConstants.C_UNEXPLORED;
                    else if (arena.arena[row][col].getType() == Types.OBSTACLE)
                        cellColor = ArenaConstants.C_OBSTACLE;
                    else
                        cellColor = ArenaConstants.C_FREE;
                }

                g.setColor(cellColor);
                g.fillRect(_gridCells[row][col].cellX + ArenaConstants.MAP_X_OFFSET, _gridCells[row][col].cellY, _gridCells[row][col].cellSize, _gridCells[row][col].cellSize);

            }
        }

        // Paint the robot on-screen.
        g.setColor(ArenaConstants.C_ROBOT);
        int r = ArenaConstants.ARENA_ROWS - this.bot.cur.y;
        int c = bot.cur.x;

        g.fillOval((c - 1) * ArenaConstants.CELL_SIZE + ArenaConstants.ROBOT_X_OFFSET + ArenaConstants.MAP_X_OFFSET, ArenaConstants.MAP_H - (r * ArenaConstants.CELL_SIZE + ArenaConstants.ROBOT_Y_OFFSET), ArenaConstants.ROBOT_W, ArenaConstants.ROBOT_H);

        // Paint the robot's direction indicator on-screen.
        g.setColor(ArenaConstants.C_ROBOT_DIR);
        Orientation o = bot.or;
        switch (o) {
            case North:
                g.fillOval(c * ArenaConstants.CELL_SIZE + 10 + ArenaConstants.MAP_X_OFFSET, ArenaConstants.MAP_H - r * ArenaConstants.CELL_SIZE - 15, ArenaConstants.ROBOT_DIR_W, ArenaConstants.ROBOT_DIR_H);
                break;
            case East:
                g.fillOval(c * ArenaConstants.CELL_SIZE + 35 + ArenaConstants.MAP_X_OFFSET, ArenaConstants.MAP_H - r * ArenaConstants.CELL_SIZE + 10, ArenaConstants.ROBOT_DIR_W, ArenaConstants.ROBOT_DIR_H);
                break;
            case South:
                g.fillOval(c * ArenaConstants.CELL_SIZE + 10 + ArenaConstants.MAP_X_OFFSET, ArenaConstants.MAP_H - r * ArenaConstants.CELL_SIZE + 35, ArenaConstants.ROBOT_DIR_W, ArenaConstants.ROBOT_DIR_H);
                break;
            case West:
                g.fillOval(c * ArenaConstants.CELL_SIZE - 15 + ArenaConstants.MAP_X_OFFSET, ArenaConstants.MAP_H - r * ArenaConstants.CELL_SIZE + 10, ArenaConstants.ROBOT_DIR_W, ArenaConstants.ROBOT_DIR_H);
                break;
        }
    }

    /**
     * Overrides JComponent's paintComponent() method. It creates a two-dimensional array of _DisplayCell objects
     * to store the current map state. Then, it paints square cells for the grid with the appropriate colors as
     * well as the robot on-screen.
     */
    private class _DisplayGrid {
        public final int cellX;
        public final int cellY;
        public final int cellSize;

        public _DisplayGrid(int borderX, int borderY, int borderSize) {
            this.cellX = borderX + ArenaConstants.CELL_LINE_WEIGHT;
            this.cellY = ArenaConstants.MAP_H - (borderY - ArenaConstants.CELL_LINE_WEIGHT);
            this.cellSize = borderSize - (ArenaConstants.CELL_LINE_WEIGHT * 2);
        }
    }
    public void setAllExplored() {
        for (int row = 0; row < arena.arena.length; row++) {
            for (int col = 0; col < arena.arena[0].length; col++) {
                arena.arena[row][col].setExplored(true);
            }
        }
    }

    /**
     * Sets all cells in the grid to an unexplored state except for the START & GOAL zone.
     */

}
