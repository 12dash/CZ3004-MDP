package Utility;

import Environment.*;
import Values.Type;

import java.util.ArrayList;

public class PrintConsole {

    public static void displaySolution(ArrayList<Grid> path, Arena arena) {
        /*
            Displays the solution given the arena and the path
         */

        String[][] solution = new String[ArenaConstants.ARENA_ROWS][ArenaConstants.ARENA_COLS];

        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                solution[i][j] =(arena.grids[i][j].getType() == Type.OBSTACLE) ? "1" : " ";
            }
        }

        for (Grid temp : path)
            solution[temp.getY()][temp.getX()] = "O";

        System.out.println("\nSolution\t");

        for (int j = 0; j < 15; j++)
            System.out.print(j + "\t|");

        System.out.println();

        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
            System.out.print("\t|");
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                System.out.print(solution[i][j] + "\t|");
            }
            System.out.println(" " + i);
        }
    }

    public static void getObstacleView(Grid[][] grids) {
        for (int i = 0; i < ArenaConstants.ARENA_ROWS; i++) {
            System.out.print("|");
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                if (Type.OBSTACLE == grids[i][j].getType())
                    System.out.print("X|");
                else {
                    System.out.print(" |");
                }
            }
            System.out.println();
        }
    }
}
