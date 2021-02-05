package utility;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class File_Utility {
    public static String[] read_file(String base_path) {
        ArrayList<String> l = new ArrayList<String>();
        try {
            String path = System.getProperty("user.dir") + "\\src\\map_descriptor\\" + base_path;
            File file_obj = new File(path);
            Scanner obj = new Scanner(file_obj);
            while (obj.hasNextLine()) {
                String data = obj.nextLine();
                l.add(data);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred : " + e);
        }
        String p1 = l.get(0);
        String p2 = l.get(1);
        return new String[]{p1, p2};
    }

    public static void write_file(arena.Arena a) throws IOException {
        String path = System.getProperty("user.dir") + "\\src\\map_descriptor\\output\\out.txt";
        File obj = new File(path);
        if(obj.createNewFile()){
            System.out.println("File Created");
        }
        else{
            System.out.println("File Exists");
        };

        for(int i = 0 ;i<a.m;i++){
            for(int j = 0;j<a.n;j++){
                System.out.println();
            }
        }
    }
}
