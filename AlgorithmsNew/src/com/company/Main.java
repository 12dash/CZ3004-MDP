package com.company;

import Algo.*;
import Environment.*;
import Utility.*;

public class Main {

    public static void main(String[] args) {
        /*
        The main here is just used for unit testing for the different algo and the making of the arena.
        The main code runs from the simulator.
         */
        String path = "example_2.txt";
        String[] p_string = FileManager.readFile(path);
        int[][] obs = MapDescriptor.getMap(p_string[0], p_string[1]);

        Arena arena = new Arena();
        arena.make_arena(obs);
        arena.setExplored();

        FastestPath.findPath(arena,new int[]{1,1});

    }
}
