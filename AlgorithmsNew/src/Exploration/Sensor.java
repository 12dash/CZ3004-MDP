package Exploration;

import Values.Type;
//import map.Map;
//import robot.RobotConstants.Orientation;
import Values.Orientation;
import Simulator.Map;

/**
 * Represents a sensor mounted on the robot.
 *
 */

public class Sensor {
    private final int lowerRange;
    private final int upperRange;

    // These are according to the ALGO MAP REPRESENTATION i.e. the top-right cell is (0, 0)
    private int sensorPosRow;
    private int sensorPosCol;
    private Orientation sensorDir;
    private final String id;

    public Sensor(int lowerRange, int upperRange, int row, int col, Orientation dir, String id) {
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
        this.id = id;
    }

    public void setSensor(int row, int col, Orientation dir) {
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
    }


    /**
     * Returns the number of cells to the nearest detected obstacle or -1 if no obstacle is detected.
     */
    public int sense(Map exploredMap, Map realMap) {
        switch (sensorDir) {
            case North:
                return getSensorVal(exploredMap, realMap, -1, 0);
            case East:
                return getSensorVal(exploredMap, realMap, 0, 1);
            case South:
                return getSensorVal(exploredMap, realMap, 1, 0);
            case West:
                return getSensorVal(exploredMap, realMap, 0, -1);
        }
        return -1;
    }

    /**
     * Sets the appropriate obstacle cell in the map and returns the row or column value of the obstacle cell. Returns
     * -1 if no obstacle is detected.
     */
    private int getSensorVal(Map exploredMap, Map realMap, int rowInc, int colInc) {
        // Check if starting point is valid for sensors with lowerRange > 1.
        if (lowerRange > 1) {
            for (int i = 1; i < this.lowerRange; i++) {
                int row = this.sensorPosRow + (rowInc * i);
                int col = this.sensorPosCol + (colInc * i);

                if (!exploredMap.arena.areValidCoordinates(row, col)) return i;
                if (realMap.arena.getGrid(row, col).isObstacle()) return i;
            }
        }

        // Check if anything is detected by the sensor and return that value.
        for (int i = 1; i <= this.upperRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if (!exploredMap.arena.areValidCoordinates(row, col)) return i;
            exploredMap.arena.setGridExplored(row, col, true);

            if (realMap.arena.getGrid(row, col).isObstacle()) {
                exploredMap.arena.setObstacleCell(row, col, true);
                return i;
            }
        }

        // Else, return -1.
        return -1;
    }

    /**
     * Uses the sensor Orientation and given value from the actual sensor to update the map.
     */
    public void senseReal(Map exploredMap, int sensorVal) {
        String debug = String.format("%s:%s", this.id, this.sensorDir);
        System.out.println(debug);

        switch (this.sensorDir) {
            case North:
                processSensorVal(exploredMap, sensorVal, -1, 0);
                break;
            case East:
                processSensorVal(exploredMap, sensorVal, 0, 1);
                break;
            case South:
                processSensorVal(exploredMap, sensorVal, 1, 0);
                break;
            case West:
                processSensorVal(exploredMap, sensorVal, 0, -1);
                break;
        }
    }

    /**
     * Sets the correct cells to explored and/or obstacle according to the actual sensor value.
     */
    private void processSensorVal(Map exploredMap, int sensorVal, int rowInc, int colInc) {
        if (sensorVal == -1) return;  // return value for LR sensor if obstacle before lowerRange

        // If above fails, check if starting point is valid for sensors with lowerRange > 1.
        for (int i = 1; i < this.lowerRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if (!exploredMap.arena.areValidCoordinates(row, col)) return;
            if (exploredMap.arena.getGrid(row, col).isObstacle()) return;  // This means short range already explored as Obstacle and long range cannot see below it's minimum range
        }

        // Update map according to sensor's value.
        for (int i = 1; i <= this.upperRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if (!exploredMap.arena.areValidCoordinates(row, col)) continue;

            exploredMap.arena.setGridExplored(row, col, true);

            if (sensorVal == i) {
                exploredMap.arena.setObstacleCell(row, col, true);
                break;
            }

            // Override previous obstacle value if front sensors detect no obstacle.
            if (exploredMap.arena.getGrid(row, col).isObstacle()) {
                if (id.equals("FL") || id.equals("FC") || id.equals("FR")) {
                    exploredMap.arena.setObstacleCell(row, col, false);
                } else {
                    break;
                }
            }
        }
    }
}
