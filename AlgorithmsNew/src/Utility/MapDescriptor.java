package Utility;

/**
 * Utility file for converting the P1, P2 string to array structure and vice-versa.
 */

import Environment.*;
import Values.Type;

import java.util.ArrayList;

public class MapDescriptor {
    public static int[] mapping(String str) {
        /*
        Converts a hexadecimal digit to equivalent binary digit.
         */
        int[] temp;
        switch (str) {
            case "0":
                temp = new int[]{0, 0, 0, 0};
                break;
            case "1":
                temp = new int[]{0, 0, 0, 1};
                break;
            case "2":
                temp = new int[]{0, 0, 1, 0};
                break;
            case "3":
                temp = new int[]{0, 0, 1, 1};
                break;
            case "4":
                temp = new int[]{0, 1, 0, 0};
                break;
            case "5":
                temp = new int[]{0, 1, 0, 1};
                break;
            case "6":
                temp = new int[]{0, 1, 1, 0};
                break;
            case "7":
                temp = new int[]{0, 1, 1, 1};
                break;
            case "8":
                temp = new int[]{1, 0, 0, 0};
                break;
            case "9":
                temp = new int[]{1, 0, 0, 1};
                break;
            case "A":
                temp = new int[]{1, 0, 1, 0};
                break;
            case "B":
                temp = new int[]{1, 0, 1, 1};
                break;
            case "C":
                temp = new int[]{1, 1, 0, 0};
                break;
            case "D":
                temp = new int[]{1, 1, 0, 1};
                break;
            case "E":
                temp = new int[]{1, 1, 1, 0};
                break;
            case "F":
                temp = new int[]{1, 1, 1, 1};
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + str);
        }
        return temp;
    }

    public static ArrayList<int[]> convertBinaryList(String s) {
        ArrayList<int[]> pos = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            int[] temp = mapping(Character.toString(s.charAt(i)));
            pos.add(temp);
        }
        return pos;
    }

    public static ArrayList<Integer> flatten(ArrayList<int[]> list) {
        /*
        Converts a 2D array into single arrayList.
         */
        ArrayList<Integer> flatten_list = new ArrayList<>();
        for (int[] ints : list) {
            for (int j = 0; j < list.get(0).length; j++) {
                flatten_list.add(ints[j]);
            }
        }
        return flatten_list;
    }

    public static int[][] getMap(String p1, String p2) {
        /*
        To generate an array pointing to the obstacles as 1 and free space as 0
         */

        ArrayList<int[]> p1_l = convertBinaryList(p1);
        ArrayList<Integer> p1_binary_list = flatten(p1_l);

        //P1_array is redundant for most of the cases especially for fastest path since all the areas are already explored.
        int[][] p1_array = new int[20][15];
        int pos = 0;

        for (int i = 19; i >= 0; i--) {
            for (int j = 0; j < 15; j++) {
                p1_array[i][j] = p1_binary_list.get(pos);
                pos += 1;
            }
        }

        ArrayList<int[]> temp = convertBinaryList(p2);
        ArrayList<Integer> binary_list = flatten(temp);

        int[][] obstacles = new int[20][15];
        pos = 0;


        for (int i = 19; i >= 0; i--) {
            for (int j = 0; j < 15; j++) {
                if (p1_array[i][j] == 1) {
                    obstacles[i][j] = binary_list.get(pos);
                    pos += 1;
                }
            }
        }
        return obstacles;
    }

    private static String mappingBooleanToHexadecimal(int a, int b, int c, int d) {
        if ((a == 0) && (b == 0) && (c == 0) && (d == 0)) return "0";
        else if ((a == 0) && (b == 0) && (c == 0) && (d == 1)) return "1";
        else if ((a == 0) && (b == 0) && (c == 1) && (d == 0)) return "2";
        else if ((a == 0) && (b == 0) && (c == 1) && (d == 1)) return "3";
        else if ((a == 0) && (b == 1) && (c == 0) && (d == 0)) return "4";
        else if ((a == 0) && (b == 1) && (c == 0) && (d == 1)) return "5";
        else if ((a == 0) && (b == 1) && (c == 1) && (d == 0)) return "6";
        else if ((a == 0) && (b == 1) && (c == 1) && (d == 1)) return "7";
        else if ((a == 1) && (b == 0) && (c == 0) && (d == 0)) return "8";
        else if ((a == 1) && (b == 0) && (c == 0) && (d == 1)) return "9";
        else if ((a == 1) && (b == 0) && (c == 1) && (d == 0)) return "A";
        else if ((a == 1) && (b == 0) && (c == 1) && (d == 1)) return "B";
        else if ((a == 1) && (b == 1) && (c == 0) && (d == 0)) return "C";
        else if ((a == 1) && (b == 1) && (c == 0) && (d == 1)) return "D";
        else if ((a == 1) && (b == 1) && (c == 1) && (d == 0)) return "E";
        else if ((a == 1) && (b == 1) && (c == 1) && (d == 1)) return "F";

        return null;
    }


    public static String[] generateMapDescriptor(Arena arena) {
        /*
        Used to convert the arena to P1, P2 string
         */

        ArrayList<Integer> p1_list = new ArrayList<>();
        ArrayList<Integer> p2_list = new ArrayList<>();

        p1_list.add(1);
        p1_list.add(1);

        for (int i = ArenaConstants.ARENA_ROWS - 1; i >= 0; i--) {
            for (int j = 0; j < ArenaConstants.ARENA_COLS; j++) {
                if (arena.grids[i][j].isExplored()) {
                    p1_list.add(1);
                    if (arena.grids[i][j].getType() == Type.OBSTACLE) {
                        p2_list.add(1);
                    } else {
                        p2_list.add(0);
                    }
                } else {
                    p1_list.add(0);
                }
            }
        }
        p1_list.add(1);
        p1_list.add(1);

        StringBuilder p1_string = new StringBuilder();

        for (int i = 0; i < p1_list.size() / 4; i++) {
            int a = p1_list.get(i * 4);
            int b = p1_list.get(i * 4 + 1);
            int c = p1_list.get(i * 4 + 2);
            int d = p1_list.get(i * 4 + 3);
            p1_string.append(mappingBooleanToHexadecimal(a, b, c, d));
        }


        while (p2_list.size() % 16 != 0) {
            p2_list.add(0,0);
        }
        StringBuilder p2_string = new StringBuilder();

        for (int i = 0; i < p2_list.size() / 4; i++) {
            int a = p2_list.get(i * 4);
            int b = p2_list.get(i * 4 + 1);
            int c = p2_list.get(i * 4 + 2);
            int d = p2_list.get(i * 4 + 3);
            p2_string.append(mappingBooleanToHexadecimal(a, b, c, d));
        }
        return new String[]{p1_string.toString(), p2_string.toString()};
    }
}
