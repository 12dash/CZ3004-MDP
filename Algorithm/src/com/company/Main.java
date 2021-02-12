package com.company;

import algo.AStar;
import algo.Greedy;
import algo.DepthFirstSearch;
import arena.Arena;
import arena.Grid;
import robot.Robot;
import robot.Utility;
import utility.Map_Descriptor;
import utility.File_Utility;
import values.Orientation;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        String path = "example_5.txt";
        String[] p_string = File_Utility.read_file(path);
        int[][] obs = Map_Descriptor.get_map(p_string[0], p_string[1]);

        Arena arena = new Arena(20, 15);
        arena.make_arena(obs);
        arena.add_padding();

        Robot robot = new Robot(arena.arena[18][1], Orientation.East);

        AStar search = new AStar();
        search.start_search(arena, arena.arena[18][1], arena.arena[1][13], robot, true);
        arena.display_solution(robot.path);
    }
}
