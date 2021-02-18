package com.company;
import Algo.AStar;
import Algo.Exploration;
import Algo.FastestPath;
import Robot.Robot;
import Robot.RobotConstants;
import Utility.*;
import Environment.*;

public class Main {

    public static void main(String[] args) {
	// write your code here
        String path = "example_1.txt";
        String[] p_string = FileManager.read_file(path);
        int[][] obs = MapDescriptor.get_map(p_string[0], p_string[1]);

        Arena arena = new Arena();
        arena.make_arena(obs);

        //FastestPath.findPath(arena,new int[]{8,16});
        Robot r = new Robot(arena.grids[RobotConstants.ROBOT_START_Y][RobotConstants.ROBOT_START_X]);
        Exploration e = new Exploration(arena, r);
        e.startExploration();
    }
}
