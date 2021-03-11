package Communication;

public class CommunicationConstants {

    public static final String ANDROID = "an";
    public static final String ARDUINO = "ar";

    public static final String START = "start";
    public static final String WAYPOINT = "waypoint";

//    public static final String EX_START = "EX_START";       // Android --> PC
    public static final String FASTEST_PATH = "FS";                   // Android --> PC: Fastest Path
    public static final String EXPLORATION = "ES";                   // Android --> PC: Exploration
    public static final String MAP_STRINGS = "MAP";         // PC --> Android
    public static final String BOT_POS = "BOT_POS";         // PC --> Android
    public static final String BOT_START = "BOT_START";     // PC --> Arduino
    public static final String INSTRUCTIONS = "INSTR";      // PC --> Arduino
    public static final String SENSOR_DATA = "SDATA";       // Arduino --> PC
}
