package Simulator;

import Environment.*;
import Robot.*;
import Robot.Robot;

import Utility.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Simulator {

    private static JFrame _appFrame = null;         // application JFrame

    private static JPanel _mapCards = null;         // JPanel for map views
    private static JPanel _buttons = null;          // JPanel for buttons

    private static Map realArena = null;            // real arena
    private static Map exploredArena = null;         // exploration map

    private static final boolean realRun = false;

    public static void main(String[] args) {
        if (!realRun) {
            realArena = new Map(new Arena());
            realArena.setAllExplored();
            realArena.robot = new Robot(realArena.arena.grids[18][1]);
        }

        exploredArena = new Map(new Arena());
        displayAll();
    }

    public static void displayAll() {
        // Initialise main frame for display

        _appFrame = new JFrame();
        _appFrame.setTitle("Simulator");

        _appFrame.setSize(new Dimension(SimulatorConstants.DEFAULT_WIDTH, SimulatorConstants.DEFAULT_HEIGHT));
        _mapCards = new JPanel(new CardLayout());

        // Create the JPanel for the buttons
        _buttons = new JPanel();

        // Add _mapCards & _buttons to the main frame's content pane
        Container contentPane = _appFrame.getContentPane();
        contentPane.add(_mapCards, BorderLayout.CENTER);
        contentPane.add(_buttons, BorderLayout.PAGE_END);

        // Initialize the main map view
        initialiseMap();

        // Initialize the buttons
        initButtonsLayout();

        // Display the application
        _appFrame.setVisible(true);
        _appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void simulateRobotMovement() {
        System.out.println(realArena.robot.path.size());
        // Emulate real movement by pausing execution.
        for (int i = 0; i < realArena.robot.path.size(); i++) {
             try {
             TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
             } catch (InterruptedException e) {
             System.out.println("Something went wrong in robot simulation!");
             }
            realArena.robot.updatePosition(realArena.robot.path.get(i), realArena.robot.orientations.get(i));
            realArena.repaint();
        }
    }

    public static void initialiseMap() {
        if (!realRun) {
            _mapCards.add(realArena, "REAL_MAP");
        }
        CardLayout cl = ((CardLayout) _mapCards.getLayout());
        if (!realRun) {
            cl.show(_mapCards, "REAL_MAP");
        }
    }

    private static void initButtonsLayout() {
        _buttons.setLayout(new FlowLayout());
        addButtons();
    }

    private static void formatButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
    }

    private static void addButtons() {
        if (!realRun) {
            // Load Map Button
            JButton btn_LoadMap = new JButton("Load Map");
            formatButton(btn_LoadMap);
            btn_LoadMap.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    JDialog loadMapDialog = new JDialog(_appFrame, "Load Map", true);
                    loadMapDialog.setSize(400, 80);
                    loadMapDialog.setLayout(new FlowLayout());

                    final JTextField loadTF = new JTextField(15);
                    JButton loadMapButton = new JButton("Load");

                    loadMapButton.addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent e) {
                            loadMapDialog.setVisible(false);

                            String[] p_string = FileManager.read_file(loadTF.getText());
                            int[][] obs = MapDescriptor.get_map(p_string[0], p_string[1]);
                            realArena.setAllExplored();
                            realArena.arena.make_arena(obs);
                            realArena.robot = new Robot(realArena.arena.grids[18][1]);

                            CardLayout cl = ((CardLayout) _mapCards.getLayout());
                            cl.show(_mapCards, "REAL_MAP");
                            realArena.validate();
                            realArena.repaint();


                        }
                    });
                    loadMapDialog.add(new JLabel("File Name: "));
                    loadMapDialog.add(loadTF);
                    loadMapDialog.add(loadMapButton);
                    loadMapDialog.setVisible(true);
                }

            });
            _buttons.add(btn_LoadMap);

        }
        class FastestPath extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                ArrayList<Grid> path = Algo.FastestPath.findPath(realArena.arena, new int[]{1, 16});
                realArena.robot.path = path;
                realArena.robot.getOrientation();
                Simulator.simulateRobotMovement();
                return 222;
            }
        }
        // Fastest Path Button
        JButton btn_FastestPath = new JButton("Fastest Path");
        formatButton(btn_FastestPath);
        btn_FastestPath.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) _mapCards.getLayout());
                cl.show(_mapCards, "REAL_MAP");
                if (realRun) {
                    while (true) {
                        System.out.println("Waiting for FP_START...");
                    }
                }
                new FastestPath().execute();
            }
        });
        _buttons.add(btn_FastestPath);
    }
}
