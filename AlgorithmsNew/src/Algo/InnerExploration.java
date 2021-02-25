package Algo;

import Environment.Arena;
import Environment.ArenaConstants;
import Environment.Grid;
import Robot.RobotSimulator;
import Utility.PrintConsole;

import java.util.ArrayList;

public class InnerExploration {

    Arena arena;
    RobotSimulator robot;
    public double percentExplored;

    private ArrayList<Grid> unexploredGrids = new ArrayList<>();
    private ArrayList<Grid> path = new ArrayList<>();

    public InnerExploration(Arena arena, RobotSimulator robot) {
        this.arena = arena;
        this.robot = robot;
        this.percentExplored = ExplorationUtility.percentExplore(this.arena);
        this.calUnexploredGrids();
    }

    public void calUnexploredGrids() {
        unexploredGrids.clear();
        for (int row = 0; row < ArenaConstants.ARENA_ROWS; row++) {
            for (int col = 0; col < ArenaConstants.ARENA_COLS; col++) {
                if (!arena.grids[row][col].isExplored()) {
                    unexploredGrids.add(arena.grids[row][col]);
                }
            }
        }
    }

    private boolean checkNeighbour(int x, int y) {
        if (((x >= 0) && (x < ArenaConstants.ARENA_COLS)) && ((y >= 0) && (y < ArenaConstants.ARENA_COLS))) {
            return this.arena.grids[y][x].getAcc() && this.arena.grids[y][x].isExplored();
        }
        return false;
    }

    private void getNeighbourPath(Grid cur) {
        int x = cur.getX();
        int y = cur.getY();

        int i = 1;
        AStar astar = new AStar();

        while (i < 4) {
            if (checkNeighbour(x - i, y)) {
                astar.startSearch(arena, robot.getCur(), arena.grids[y][x - i], false);
                path = new ArrayList<>(astar.solution);
                if (path.size() != 0) {
                    return;
                }
            } else if (checkNeighbour(x + i, y)) {
                astar.startSearch(arena, robot.getCur(), arena.grids[y][x + i], false);
                path = new ArrayList<>(astar.solution);
                if (path.size() != 0) {
                    return;
                }
            } else if (checkNeighbour(x, y - i)) {
                astar.startSearch(arena, robot.getCur(), arena.grids[y - i][x], false);
                path = new ArrayList<>(astar.solution);
                if (path.size() != 0) {
                    return;
                }
            } else if (checkNeighbour(x, y + i)) {
                astar.startSearch(arena, robot.getCur(), arena.grids[y + i][x], false);
                path = new ArrayList<>(astar.solution);
                if (path.size() != 0) {
                    return;
                }
            }
            i++;
        }
    }

    public void getCandidatePath() {
        for (Grid x : this.unexploredGrids) {
            getNeighbourPath(x);
            if (path.size() != 0) {
                return;
            }
        }
    }

    public void goToPath() {
        this.robot.updatePosition(this.robot.getPath().remove(0), this.robot.getOrientations().remove(0));
        this.robot.sense(this.arena);
    }

    public void move() {
        if (this.path.size() == 0) {
            calUnexploredGrids();
            getCandidatePath();
            this.robot.setPath(this.path);
            this.robot.getPathOrientation();
        } else {
            goToPath();
        }
        this.percentExplored = ExplorationUtility.percentExplore(this.arena);
    }

    public void startSearch() {
        while (percentExplored != 100) {
            move();
        }
    }
}
