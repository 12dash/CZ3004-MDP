package com.company;

import algo.*;
import arena.*;
import robot.Robot;
import robot.RobotConstants;

import values.Orientation;

import utility.Map_Descriptor;
import utility.File_Utility;
import values.Types;
import arena.ArenaConstants;


public class Main {

    public static void main(String[] args) {

        String path = "example_4.txt";
        String[] p_string = File_Utility.read_file(path);
        int[][] obs = Map_Descriptor.get_map(p_string[0], p_string[1]);

        Arena arena = new Arena(ArenaConstants.ARENA_ROWS, ArenaConstants.ARENA_COLS);
        arena.make_arena();
        arena.update_arena(obs);
        arena.add_padding();

<<<<<<< HEAD
        Robot robot = new Robot(Orientation.East);
        robot.setCur(arena.arena[RobotConstants.ROBOT_START_Y][RobotConstants.ROBOT_START_X]);
=======
        Robot robot = new Robot(Orientation.East,arena.arena[18][1] );
>>>>>>> 51cdd0667549f4d26500956a55a0f2206145e259

        FastestPath.findPath(arena,new int[] {13,16},robot);
    }
}
