package Algo;

import Environment.Arena;
import Environment.ArenaConstants;

public class ExplorationUtility {
    public static double percentExplore(Arena arena){
        double a = 0;
        for(int i  =0;i< ArenaConstants.ARENA_ROWS;i++){
            for(int j = 0;j<ArenaConstants.ARENA_COLS;j++){
                if(arena.grids[i][j].isExplored()){
                    a += 1;
                }
            }
        }
        return (a/ArenaConstants.ARENA_SIZE)*100;
    }
}
