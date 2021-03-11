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


    //#############################################
    //          SET THESE VALUES
    //#############################################

    private static int timeLimit = ArenaConstants.MAX_TIME_LIMIT;
    private static int coverage = ArenaConstants.MAX_COVERAGE;

    //#############################################
    //
    //#############################################

    public static void main(String[] args) {

        map = new Map(new Arena(false), false, true);
        displayAll();
        comm.openConnection();

        ExplorationAlgo.initialCalibration(map);

        while (true) {

            String msg = comm.recvMsg();
            String[] msgArr = msg.split(":");

           if (msgArr[0].equals(CommunicationConstants.START)) {
               if(msgArr[1].equals(CommunicationConstants.EXPLORATION)) {
                    ExplorationAlgo explorationTask = new ExplorationAlgo(map, timeLimit, coverage, comm);
                    explorationTask.runExploration();
                }
            }
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



}
