package algo;

import arena.*;
import robot.Robot;
import robot.Utility;

public class FastestPath {

    public static void print(Robot r){
        for(int i = 0;i<r.path.size();i++){
            System.out.println(r.path.get(i).x +" "+r.path.get(i).y + " " + r.orientations.get(i));
        }
    }

    public static void findPath(Arena arena, int[] wayPoint_cord, Robot robot){

        AStar search = new AStar();
        Grid wayPoint = arena.arena[wayPoint_cord[1]][wayPoint_cord[0]];
        search.start_search(arena, wayPoint, robot, false);

        search = new AStar();
        search.start_search(arena, arena.arena[1][13], robot, true);
        arena.display_solution(robot.path);

        print(robot);

    }
}
