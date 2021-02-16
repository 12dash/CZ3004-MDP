package Utility;

import Environment.*;
import Values.Type;

import java.util.ArrayList;

public class PrintConsole {

    public static void getObstacleView(Grid[][] grids) {
        for (int i = 0; i < Constants.ROWS; i++) {
            System.out.print("|");
            for (int j = 0; j < Constants.COLUMNS; j++) {
                if (Type.OBSTACLE == grids[i][j].getType())
                    System.out.print("X|");
                else {
                    System.out.print(" |");
                }
            }
            System.out.println();
        }
    }

    public static void getAccView(Grid[][] grids) {
        for (int i = 0; i < Constants.ROWS; i++) {
            System.out.print("|");
            for (int j = 0; j < Constants.COLUMNS; j++) {
                if (!grids[i][j].getAcc())
                    System.out.print("X|");
                else {
                    System.out.print(" |");
                }
            }
            System.out.println();
        }
    }

    public static void displaySolution(ArrayList<Grid> path, Arena arena) {

        String[][] solution = new String[Constants.ROWS][Constants.COLUMNS];

        for (int i = 0; i < Constants.ROWS; i++) {
            for (int j = 0; j < Constants.COLUMNS; j++) {
                if (arena.grids[i][j].getType() == Type.OBSTACLE) solution[i][j] = "1";
                else solution[i][j] = " ";
            }
        }

        for (int i = 0; i < path.size(); i++) {
            Grid temp = path.get(i);
            solution[temp.getY()][temp.getX()] = "O";
        }
        System.out.println("\nSolution");
        System.out.print("\t");

        for (int j = 0; j < 15; j++) {
            System.out.print(j + "\t|");
        }
        System.out.println();

        for (int i = 0; i < Constants.ROWS; i++) {
            System.out.print("\t|");
            for (int j = 0; j < Constants.COLUMNS; j++) {
                System.out.print(solution[i][j] + "\t|");
            }
            System.out.println(" " + i);
        }
    }
}
