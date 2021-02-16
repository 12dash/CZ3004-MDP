package com.company;

import algo.*;
import arena.*;
import robot.Robot;

import values.Orientation;

import utility.Map_Descriptor;
import utility.File_Utility;
import values.Types;

public class Main {

    public static void main(String[] args) {

        String path = "example_4.txt";
        String[] p_string = File_Utility.read_file(path);
        int[][] obs = Map_Descriptor.get_map(p_string[0], p_string[1]);

        Arena arena = new Arena(20, 15);
        arena.make_arena();
        arena.update_arena(obs);
        arena.add_padding();

        Robot robot = new Robot(Orientation.East,arena.arena[18][1] );

        FastestPath.findPath(arena,new int[] {13,16},robot);


    }
}
