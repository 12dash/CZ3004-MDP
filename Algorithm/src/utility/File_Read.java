package utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class File_Read {
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
}
