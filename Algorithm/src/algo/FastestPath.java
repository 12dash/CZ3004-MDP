package Algo;

import Environment.*;
import Utility.PrintConsole;

import java.util.ArrayList;

public class FastestPath {

    private static void combine(ArrayList<Grid> path, ArrayList<Grid> path1, int i){
        for(;i<path1.size();i++){path.add(path1.get(i));}
    }

    public static ArrayList<Grid> findPath(Arena arena, int[] wayPoint_cord){
        AStar search = new AStar();
        ArrayList<Grid> path = new ArrayList<Grid>();

        Grid wayPoint = arena.grids[wayPoint_cord[1]][wayPoint_cord[0]];
        search.startSearch(arena, arena.grids[18][1], wayPoint, false);
        ArrayList<Grid> path1 = new ArrayList<>(search.solution);
        combine(path,path1,0);

        search = new AStar();
        wayPoint = arena.grids[wayPoint_cord[1]][wayPoint_cord[0]];
        search.startSearch(arena, wayPoint, arena.grids[0][14], true);
        ArrayList<Grid> path2 = new ArrayList<>(search.solution);
        combine(path,path2,1);

        return path;
    }
}
