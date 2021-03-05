package com.company;

import Algo.*;
import Environment.*;
import Robot.RobotConstants;
import Robot.RobotSimulator;
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

        Arena arena = new Arena(true);
        arena.make_arena(obs);

        RobotSimulator robot = new RobotSimulator(arena.grids[RobotConstants.START_ROW][RobotConstants.START_COL]);

        arena.setUnexplored();
        Exploration.start(arena, robot);

        //FastestPath.findPath(arena,new int[]{1,1});

    }
}
