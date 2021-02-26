package Algo;

import Environment.Arena;
import Robot.RobotConstants;
import Robot.RobotSimulator;
import Utility.PrintConsole;

public class Exploration {

    public static void start(Arena arena, RobotSimulator robot){
        RightWallHugging a = new RightWallHugging(arena,robot);
        a.move();
        while (!robot.getCur().equals(arena.grids[RobotConstants.ROBOT_START_Y][RobotConstants.ROBOT_START_X])){
            a.move();
        }
        InnerExploration b = new InnerExploration(arena, robot);
        b.startSearch();
    }
}
