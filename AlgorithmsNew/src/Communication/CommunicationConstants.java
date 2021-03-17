package Communication;

public class CommunicationConstants {

    public static final String ANDROID = "an";
    public static final String ARDUINO = "ar";
    public static final String IR = "ir"; // TODO: Check this with RPI

    public static final String START = "start";
    public static final String WAYPOINT = "waypoint";


    public static final String FASTEST_PATH = "FS";         // Android --> PC: Fastest Path
    public static final String EXPLORATION = "ES";          // Android --> PC: Exploration
    public static final String START_EPLORATION = "E";      // PC --> Arduino: Start Exploration

    public static final String GO_HOME = "GH";              // PC --> Arduino: Once the exploration is complete

    public static final String FINISH = "done"; // TODO: Finalise this protocol

    // CALIBRATION CONSTANTS
    public static final String CALI_FRONT = "H";
    public static final String CALI_RIGHT = "T";
    public static final String CALI_LEFT = "J";
    public static final String CALI_RIGHT_FRONT = "K";
    public static final String CALI_LEFT_FRONT = "M";
    public static final String CALIBRATION_ACKNOWLEDGMENT = "A";

    public static final String IMAGE_CAPTURED = "{\"imageCaptured\" : \"true\"}";


    //TODO: Finalize this ^ initial calibration with arduino

//    public static final String MAP_STRINGS = "MAP";         // PC --> Android
//    public static final String BOT_POS = "BOT_POS";         // PC --> Android
//    public static final String BOT_START = "BOT_START";     // PC --> Arduino
//    public static final String INSTRUCTIONS = "INSTR";      // PC --> Arduino
//    public static final String SENSOR_DATA = "SDATA";       // Arduino --> PC
}
