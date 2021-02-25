package Algo;

/**
 * Class for finding the fastest path from Goal to End zone passing the waypoint in the middle
 */

import Environment.Arena;
import Environment.Grid;
import Utility.PrintConsole;

import java.util.ArrayList;

public class FastestPath {

    private static void combine(ArrayList<Grid> path, ArrayList<Grid> path1, int i) {
        /*
        Combine the two solution from the start to end
         */
        for (; i < path1.size(); i++) {
            //Add the path to the overall solution
            path.add(path1.get(i));
        }
    }

    public static ArrayList<Grid> findPath(Arena arena, int[] wayPoint_cord) {

        AStar astar = new AStar();
        ArrayList<Grid> path = new ArrayList<>();

        Grid wayPoint = arena.grids[wayPoint_cord[1]][wayPoint_cord[0]];

        //Finds the solution from the start to the waypoint.
        astar.startSearch(arena, arena.grids[18][1], wayPoint, false);
        path = new ArrayList<>(astar.solution);

        //Re-initializing the astar object
        astar = new AStar();

        //Search for the solution from the waypoint to the end goal state.
        astar.startSearch(arena, wayPoint, arena.grids[0][14], true);
        ArrayList<Grid> path2 = new ArrayList<>(astar.solution);

        //Combine the path from the two solution.
        combine(path,path2,1);

        PrintConsole.displaySolution(path,arena);

        //Return the final solution
        return path;
    }
}
