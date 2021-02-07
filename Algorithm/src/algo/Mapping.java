package algo;

import arena.Grid;

import java.util.ArrayList;

public class Mapping {

    //Mapping : parent -> child

    ArrayList<Grid[]> mapping = new ArrayList<>();

    public void add_pair(Grid parent, Grid child) {
        Grid[] temp = {parent, child};
        mapping.add(temp);
    }

    public Grid find_parent(Grid child) {
        Grid parent = null;
        for (int i = this.mapping.size()-1; i >= 0 ; i--) {
             if (mapping.get(i)[1].equals(child)){
                 parent = mapping.get(i)[0];
                 break;
             }
        }
        return parent;
    }
}
