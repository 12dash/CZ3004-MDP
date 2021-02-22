package Simulator;

import Algo.AStar;
import Algo.Exploration;
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

    private static Map FastestPath_Map = null;        // real arena
    private static Map DefaultMap = null;
    public static void main(String[] args) {
        DefaultMap = new Map(new Arena());
        DefaultMap.robot = new Robot(DefaultMap.arena.grids[18][1]);
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


    public static void goToNextGrid() {
        System.out.println(DefaultMap.robot.path.size());
        for (int i = 0; i < DefaultMap.robot.path.size(); i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
            } catch (InterruptedException ex) {
                System.out.println("Something went wrong in robot simulation!");
            }
            DefaultMap.robot.sense(DefaultMap.arena);
            DefaultMap.robot.updatePosition(DefaultMap.robot.path.get(i), DefaultMap.robot.orientations.get(i));
            DefaultMap.repaint();
        }
    }

    public static void simulateExploration() {
        Robot robot = DefaultMap.robot;
        Arena arena = DefaultMap.arena;
        Exploration e = new Exploration(arena, robot);

        long start = System.currentTimeMillis();

        long minutes = Long.parseLong(DefaultMap.maxTime.split(":")[0]);
        long seconds = Long.parseLong(DefaultMap.maxTime.split(":")[1]);

        long time =(minutes*60)+seconds;
        time = time*1000;

        long end = start + time;
        System.out.println(time);

        boolean out = false;
        e.move();
        e.calNumberCellExplored();
        while (!robot.cur.equals(arena.grids[RobotConstants.ROBOT_START_Y][RobotConstants.ROBOT_START_X])) {
            try {
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
            } catch (InterruptedException ex) {
                System.out.println("Something went wrong in robot simulation!");
            }
            e.move();
            e.calNumberCellExplored();
            long tempTime = System.currentTimeMillis();
            if (tempTime > end){
                return;
            }
            if (e.percentCurrentExploration > DefaultMap.percent) {
                out = true;
                System.out.println("Break");
                break;
            }
            DefaultMap.repaint();
        }
        while ((e.percentCurrentExploration < DefaultMap.percent) && (!out)) {
            e.getUnexplored();
            Grid temp = e.getNextFree();
            if (temp == null) {
                temp = e.getFreeNeighbour(e.unexplored.get(0));
            }
            AStar a = new AStar();
            a.startSearch(arena, robot.cur, temp, false);
            ArrayList<Grid> path = a.solution;
            robot.path = path;
            robot.getOrientation();
            goToNextGrid();
            e.calNumberCellExplored();
            DefaultMap.repaint();
            long tempTime = System.currentTimeMillis();
            if (tempTime > end){
                return;
            }
        }
        System.out.println(DefaultMap.percent);
        if (DefaultMap.percent == 100) {
            AStar a = new AStar();
            a.startSearch(arena, robot.cur, DefaultMap.arena.grids[RobotConstants.ROBOT_START_Y][RobotConstants.ROBOT_START_X], false);
            ArrayList<Grid> path = a.solution;
            robot.path = path;
            robot.getOrientation();
            goToNextGrid();
            e.calNumberCellExplored();
            DefaultMap.repaint();
        }
    }

    public static void simulateRobotMovement() {
        int step = FastestPath_Map.step;
        if (step == 0) {
            return;
        }
        // Emulate real movement by pausing execution.
        int length = FastestPath_Map.robot.path.size();
        for (int i = 0; i < length; ) {
            try {
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in robot simulation!");
            }
            FastestPath_Map.path.add(FastestPath_Map.robot.path.get(i));
            FastestPath_Map.robot.updatePosition(FastestPath_Map.robot.path.get(i), FastestPath_Map.robot.orientations.get(i));
            FastestPath_Map.repaint();
            i += step;
            if (i >= length) {
                i -= step;
                i += 1;
            }
        }
    }

    public static void initialiseMap() {
        _mapCards.add(DefaultMap, "DEFAULT_MAP");
        CardLayout cl = ((CardLayout) _mapCards.getLayout());
        cl.show(_mapCards, "DEFAULT_MAP");
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
        // Load Map Button
        JButton btn_LoadMap = new JButton("Load Map");
        formatButton(btn_LoadMap);
        btn_LoadMap.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                DefaultMap.reset();
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
                        DefaultMap.arena.make_arena(obs);
                        DefaultMap.resetRobot();

                        CardLayout cl = ((CardLayout) _mapCards.getLayout());
                        cl.show(_mapCards, "DEFAULT_MAP");
                        DefaultMap.validate();
                        DefaultMap.repaint();
                    }
                });
                loadMapDialog.add(new JLabel("File Name: "));
                loadMapDialog.add(loadTF);
                loadMapDialog.add(loadMapButton);
                loadMapDialog.setVisible(true);
            }

        });
        _buttons.add(btn_LoadMap);

        class FastestPath extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                int x = FastestPath_Map.wayPoint.getX();
                int y = FastestPath_Map.wayPoint.getY();
                int[] pos = new int[]{x, y}; //x,y
                FastestPath_Map.robot.path = Algo.FastestPath.findPath(FastestPath_Map.arena, pos);
                FastestPath_Map.robot.getOrientation();
                Simulator.simulateRobotMovement();
                return 222;
            }
        }

        class Exploration extends SwingWorker<Integer, String> {
            protected Integer doInBackground() throws Exception {
                simulateExploration();
                return 222;
            }
        }
        // Fastest Path Button
        JButton btn_FastestPath = new JButton("Fastest Path");
        formatButton(btn_FastestPath);
        btn_FastestPath.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JDialog loadMapDialog = new JDialog(_appFrame, "Get Options", true);
                loadMapDialog.setSize(200, 220);
                loadMapDialog.setLayout(new FlowLayout());

                JLabel Step_l1 = new JLabel("Step : ");
                JTextField Step = new JTextField(15);

                JLabel WaypointX_label = new JLabel("WayPoint X : ");
                JTextField WaypointX_field = new JTextField(15);

                JLabel WaypointY_label = new JLabel("WayPoint Y : ");
                JTextField WaypointY_field = new JTextField(15);

                JButton go = new JButton("Go");

                go.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        int step_pass = Integer.parseInt(Step.getText());
                        int x = Integer.parseInt(WaypointX_field.getText());
                        int y = Integer.parseInt(WaypointY_field.getText());
                        FastestPath_Map = DefaultMap;
                        FastestPath_Map.fPath = true;
                        FastestPath_Map.setWayPoint(FastestPath_Map.arena.grids[y][x]);
                        FastestPath_Map.step = step_pass;
                        new FastestPath().execute();
                        loadMapDialog.setVisible(false);

                    }
                });
                loadMapDialog.add(Step_l1);
                loadMapDialog.add(Step);
                loadMapDialog.add(WaypointX_label);
                loadMapDialog.add(WaypointX_field);
                loadMapDialog.add(WaypointY_label);
                loadMapDialog.add(WaypointY_field);
                loadMapDialog.add(go);
                loadMapDialog.setVisible(true);
            }
        });
        _buttons.add(btn_FastestPath);

        JButton btn_Exploration = new JButton("Exploration");
        formatButton(btn_Exploration);
        btn_Exploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                DefaultMap.fPath = false;
                DefaultMap.reset();
                System.out.println("Exploration Map has been clicked");
                JDialog loadMapDialog = new JDialog(_appFrame, "Get Options", true);
                loadMapDialog.setSize(200, 220);
                loadMapDialog.setLayout(new FlowLayout());

                JLabel percent = new JLabel("Percent : ");
                JTextField percent_f = new JTextField(15);

                JLabel time = new JLabel("Time : ");
                JTextField time_f = new JTextField(15);

                JButton go = new JButton("Go");
                go.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        System.out.println("The button has been pressed");
                        if (!percent_f.getText().equals("")) {
                            DefaultMap.percent = Double.parseDouble(percent_f.getText());
                        } else {
                            DefaultMap.percent = 100;
                        }
                        if (!time_f.getText().equals("")) {
                            DefaultMap.maxTime = time_f.getText();
                        }

                        new Exploration().execute();
                        loadMapDialog.setVisible(false);
                    }
                });
                loadMapDialog.add(percent);
                loadMapDialog.add(percent_f);
                loadMapDialog.add(time);
                loadMapDialog.add(time_f);
                loadMapDialog.add(go);
                loadMapDialog.setVisible(true);
                System.out.println("Reset");
            }
        });
        _buttons.add(btn_Exploration);
    }
}
