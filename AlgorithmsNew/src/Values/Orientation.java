package Values;

import java.util.HashMap;

public enum Orientation {
    North,
    East,
    South,
    West,
    NorthEast,
    NorthWest,
    SouthEast,
    SouthWest;

    public static Orientation[] orientations = {North, East, South, West};

    public static HashMap<Orientation,Integer> ORIENTATION_MAPPINGS;
    static {
        ORIENTATION_MAPPINGS = new HashMap<>();
        ORIENTATION_MAPPINGS.put(North, 0);
        ORIENTATION_MAPPINGS.put(East, 1);
        ORIENTATION_MAPPINGS.put(South, 2);
        ORIENTATION_MAPPINGS.put(West, 3);
    }

    public static Orientation getNextOrientation(Orientation curOrientation) {
        return orientations[(ORIENTATION_MAPPINGS.get(curOrientation) + 1) % orientations.length];
    }

    public static Orientation getPreviousOrientation(Orientation curDirection) {
        return orientations[(ORIENTATION_MAPPINGS.get(curDirection) + orientations.length -1) % orientations.length];
    }

}

