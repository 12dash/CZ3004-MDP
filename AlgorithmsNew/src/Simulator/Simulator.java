package Simulator;

import Environment.Arena;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Simulator {

    private static JFrame _appFrame = null;         // application JFrame
    private static JPanel _mapCards = null;         // JPanel for map views
    private static JPanel _buttons = null;          // JPanel for buttons

    public static Map map;   // real arena

    static FastestPathSimulations fPathSimulate ;
    static ExplorationSimulations eSimulate ;
    static LoadMap lMap;

    public static void main(String[] args) {
        map = new Map(new Arena(true), true);
        fPathSimulate = new FastestPathSimulations(map);
        eSimulate = new ExplorationSimulations(map);
        lMap = new LoadMap(map);
        displayAll();
    }

    private static void getNewMap(){
        map.resetMap(true);
        fPathSimulate = new FastestPathSimulations(map);
        lMap = new LoadMap(map);
    }

    public static void displayAll() {
        // Initialise main frame for display

        _appFrame = new JFrame();
        _appFrame.setTitle("Simulator");

        _appFrame.setSize(new Dimension(SimulatorConstants.DEFAULT_WIDTH, SimulatorConstants.DEFAULT_HEIGHT));

        _mapCards = new JPanel(new CardLayout());

        _buttons = new JPanel();

        initialiseMap();
        initButtonsLayout();

        // Add _mapCards & _buttons to the main frame's content pane
        Container contentPane = _appFrame.getContentPane();
        contentPane.add(_mapCards, BorderLayout.CENTER);
        contentPane.add(lMap, BorderLayout.PAGE_START);
        contentPane.add(_buttons, BorderLayout.PAGE_END);

        // Display the application

        _appFrame.setVisible(true);
        _appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static void formatButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setFocusPainted(false);
    }

    private static void initButtonsLayout() {
        _buttons.setLayout(new FlowLayout());
        addButtons();
    }

    private static void initialiseMap() {
        _mapCards.add(map, "MAP");
        CardLayout cl = ((CardLayout) _mapCards.getLayout());
        cl.show(_mapCards, "MAP");
    }

    private static void addButtons() {
        JButton btn_LoadMap = new JButton("Load Map");
        formatButton(btn_LoadMap);
        btn_LoadMap.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                System.out.println("Pressed Load Map");
                _appFrame.getContentPane().remove(fPathSimulate);
                _appFrame.getContentPane().remove(eSimulate);
                map.resetMap(true);
                _appFrame.getContentPane().add(lMap);
                _appFrame.repaint();
                _appFrame.getContentPane().invalidate();
                _appFrame.getContentPane().validate();
                _appFrame.repaint();
            }
        });
        _buttons.add(btn_LoadMap);

        JButton btn_FastestPath = new JButton("Fastest Path");
        formatButton(btn_FastestPath);
        btn_FastestPath.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                System.out.println("Pressed Fastest Path");
                map.arena.setExplored();
                _appFrame.getContentPane().remove(lMap);
                _appFrame.getContentPane().remove(eSimulate);
                _appFrame.getContentPane().add(fPathSimulate,BorderLayout.PAGE_START);
                _appFrame.repaint();
                _appFrame.getContentPane().invalidate();
                _appFrame.getContentPane().validate();
            }
        });
        _buttons.add(btn_FastestPath);

        JButton btn_Exploration = new JButton("Exploration");
        formatButton(btn_Exploration);
        btn_Exploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                System.out.println("Pressed Exploration");
                map.arena.setUnexplored();
                _appFrame.getContentPane().remove(lMap);
                _appFrame.getContentPane().remove(fPathSimulate);
                _appFrame.getContentPane().add(eSimulate,BorderLayout.PAGE_START);
                _appFrame.repaint();
                _appFrame.getContentPane().invalidate();
                _appFrame.getContentPane().validate();
            }
        });
        _buttons.add(btn_Exploration);
    }
}
