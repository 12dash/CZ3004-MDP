package Actual_Run;

import Communication.*;
import Environment.*;
import Exploration.ExplorationAlgo;
import Simulator.Map;

import Simulator.SimulatorConstants;
import Utility.FileManager;
import Utility.MapDescriptor;
import Values.Orientation;
import org.w3c.dom.html.HTMLImageElement;

import javax.naming.CommunicationException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.nio.file.Watchable;

public class ActualSimulatorExploration {

    private static JFrame _appFrame = null;         // application JFrame
    private static JPanel _mapCards = null;         // JPanel for map views


    public static Map map;   // real arena
    private static final Communication comm = Communication.getCommunication();

    private static String INPUT_MAP_FILE = "example_4.txt";

    //#############################################
    //          SET THESE VALUES
    //#############################################

    private static final int timeLimit = 360;
    private static int coverage = ArenaConstants.MAX_COVERAGE;
    private static boolean simulate = true;

    //#############################################
    //
    //#############################################

    public static void main(String[] args) throws InterruptedException {

        map = new Map(new Arena(false), false, true);
        displayAll();
        if(!simulate) {
            comm.openConnection();

            while (true) {

                String msg = comm.recvMsg();
                String[] msgArr = msg.split(":");

                if (msgArr[0].equals(CommunicationConstants.START)) {
                    if (msgArr[1].equals(CommunicationConstants.EXPLORATION)) {
                        ExplorationAlgo explorationTask = new ExplorationAlgo(map, timeLimit, coverage, comm);
                        explorationTask.initialCalibration();
                        explorationTask.runExploration();
                    }
                }
            }
        }
        else{
            Map realMap = new Map(new Arena(true), true, true);
            loadMap(realMap);
            ExplorationAlgo explorationTask = new ExplorationAlgo(map, realMap, timeLimit, coverage);
            explorationTask.runExploration();
        }
    }

    public static void displayAll() {

        _appFrame = new JFrame();
        _appFrame.setTitle("Simulator");
        _appFrame.setSize(new Dimension(SimulatorConstants.DEFAULT_WIDTH, SimulatorConstants.DEFAULT_HEIGHT));
        _mapCards = new JPanel(new CardLayout());

        initialiseMap();

        Container contentPane = _appFrame.getContentPane();
        contentPane.add(_mapCards, BorderLayout.CENTER);

        _appFrame.setVisible(true);
        _appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static void initialiseMap() {
        _mapCards.add(map, "MAP");
        CardLayout cl = ((CardLayout) _mapCards.getLayout());
        cl.show(_mapCards, "MAP");
    }


    private static void loadMap(Map map){
        String[] p_string = FileManager.readFile(INPUT_MAP_FILE);
        int[][] obs = MapDescriptor.getMap(p_string[0], p_string[1]);
        map.arena.make_arena(obs);
        map.arena.setExplored();
        map.repaint();
    }


}
