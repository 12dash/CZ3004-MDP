package com.company;

import algo.DepthFirstSearch;
import arena.Arena;
import robot.Robot;
import utility.Map_Descriptor;
import utility.File_Utility;

public class Main {

    public static void main(String[] args) {

        String path = "example_4.txt";
        String[] p_string=  File_Utility.read_file(path);
        int[][] obs = Map_Descriptor.get_map(p_string[0],p_string[1]);


        Arena arena = new Arena(20,15);
        arena.make_arena(obs);
        arena.add_padding();

        DepthFirstSearch search = new DepthFirstSearch();
        Robot robot = new Robot();
        search.start_search(arena, arena.arena[18][1], arena.arena[1][13], robot, true);
        arena.display_solution(robot.path);
    }
}
