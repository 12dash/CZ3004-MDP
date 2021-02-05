package com.company;

import arena.Arena;
import utility.Map_Descriptor;
import utility.File_Read;

public class Main {

    public static void main(String[] args) {

        String path = "example_4.txt";
        String[] p_string=  File_Read.read_file(path);
        int[][] obs = Map_Descriptor.get_map(p_string[0],p_string[1]);

        Arena arena = new Arena(20,15);
        arena.make_arena(obs);
        arena.get_view();
            
    }
}
