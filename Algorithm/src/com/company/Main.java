package com.company;

import algo.*;
import arena.Arena;
import robot.Robot;

import values.Orientation;

import utility.Map_Descriptor;
import utility.File_Utility;

public class Main {

    public static void main(String[] args) {

        String path = "example_1.txt";
        String[] p_string = File_Utility.read_file(path);
        int[][] obs = Map_Descriptor.get_map(p_string[0], p_string[1]);

        Robot robot = new Robot(Orientation.East);

        Arena arena = new Arena(20, 15, robot);
        arena.make_arena();
        arena.update_arena(obs);
        arena.add_padding();

        robot.setCur(arena.arena[18][1]);

        AStar search = new AStar();
        search.start_search(arena, arena.arena[18][1], arena.arena[1][13], robot, true);
        arena.display_solution(robot.path);
    }
}
