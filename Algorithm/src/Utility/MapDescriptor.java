package Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class MapDescriptor {

    public static int[] mapping(String str) {
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

    public static ArrayList<int[]> convert_binary_list(String s) {
        ArrayList<int[]> pos = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            int[] temp = mapping(Character.toString(s.charAt(i)));
            pos.add(temp);
        }
        return pos;
    }

    public static ArrayList<Integer> flatten(ArrayList<int[]> list) {
        ArrayList<Integer> flatten_list = new ArrayList<>();
        for (int[] ints : list) {
            for (int j = 0; j < list.get(0).length; j++) {
                flatten_list.add(ints[j]);
            }
        }
        return flatten_list;
    }

    public static int[][] get_map(String p1, String p2) {

        ArrayList<int[]> p1_l = convert_binary_list(p1);
        ArrayList<Integer> p1_binary_list = flatten(p1_l);
        int[][] p1_array = new int[20][15];
        int pos = 0;
        for (int i = 19; i >= 0; i--) {
            for (int j = 0; j < 15; j++) {
                p1_array[i][j] = p1_binary_list.get(pos);
                pos += 1;
            }
        }

        ArrayList<int[]> temp = convert_binary_list(p2);
        ArrayList<Integer> binary_list = flatten(temp);

        pos = 0;
        int[][] obstacles = new int[20][15];
        for (int i = 19; i >= 0; i--) {
            for (int j = 0; j < 15; j++) {
                if(p1_array[i][j] == 1) {
                    obstacles[i][j] = binary_list.get(pos);
                    pos += 1;
                }
            }
        }
        return obstacles;
    }

}
