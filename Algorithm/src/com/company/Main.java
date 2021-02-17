package com.company;
import Algo.AStar;
import Algo.FastestPath;
import Utility.*;
import Environment.*;

public class Main {

    public static void main(String[] args) {
	// write your code here
        String path = "example_4.txt";
        String[] p_string = FileManager.read_file(path);
        int[][] obs = MapDescriptor.get_map(p_string[0], p_string[1]);

        Arena arena = new Arena();
        arena.make_arena(obs);

        FastestPath.findPath(arena,new int[]{13,18});
    }
}
