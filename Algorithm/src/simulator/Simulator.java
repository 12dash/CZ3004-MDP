package simulator;

import arena.Arena;
import arena.ArenaConstants;
import arena.Temp;
import robot.Robot;
import utility.File_Utility;
import utility.Map_Descriptor;
import values.Orientation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class Simulator {
    private static JFrame _appFrame = null;         // application JFrame

    private static JPanel _mapCards = null;         // JPanel for map views
    private static JPanel _buttons = null;          // JPanel for buttons

    private static Robot bot;

    private static Temp realArena = null;            // real arena
    private static Temp exploredArena =null;         // exploration map

    //-- private static int timeLimit = 3600;            // time limit
    //-- private static int coverageLimit = 300;         // coverage limit

    //-- private static final CommMgr comm = CommMgr.getCommMgr();

    private static final boolean realRun = false;

    /**
     * Initialisation
     */
    public static void main(String[] args){

        bot = new Robot(Orientation.East,null);
        if (!realRun) {
            realArena = new Temp(new Arena(ArenaConstants.ARENA_ROWS, ArenaConstants.ARENA_COLS), bot);
            realArena.arena.make_arena();
            realArena.setAllUnexplored();
        }

        exploredArena = new Temp(new Arena(ArenaConstants.ARENA_ROWS, ArenaConstants.ARENA_COLS), bot);
        exploredArena.arena.make_arena();
        exploredArena.setAllUnexplored();
        bot.setCur(realArena.arena.arena[18][1]);

        //-- if (realRun) comm.openConnection();
        displayAll();
    }

    public static void displayAll(){
        // Initialise main frame for display

        _appFrame = new JFrame();
        _appFrame.setTitle("Simulator");

        _appFrame.setSize(new Dimension(SimulatorConstants.DEFAULT_WIDTH, SimulatorConstants.DEFAULT_HEIGHT));
        // _appFrame.setResizable(false);

        // Center the main frame in the middle of the screen
        // Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        //_appFrame.setLocation(dim.width / 2 - _appFrame.getSize().width / 2, dim.height / 2 - _appFrame.getSize().height / 2);

        // Create the CardLayout for storing the different maps
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

    /**
     * Initialises the main map view by adding the different maps as cards in the CardLayout. Displays realMap
     * by default.
     */


    public static void initialiseMap(){

        if (!realRun) {
            _mapCards.add(realArena, "REAL_MAP");
        }
        _mapCards.add(exploredArena, "EXPLORATION");

        CardLayout cl = ((CardLayout) _mapCards.getLayout());
        if (!realRun) {
            cl.show(_mapCards, "REAL_MAP");
        } else {
            cl.show(_mapCards, "EXPLORATION");
        }
    }


    /**
     * Initialises the JPanel for the buttons.
     */
    private static void initButtonsLayout() {
        _buttons.setLayout(new FlowLayout());
        addButtons();
    }

    /**
     * Helper method to format the buttons.
     */
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
                    loadMapDialog.setSize(400, 60);
                    loadMapDialog.setLayout(new FlowLayout());

                    final JTextField loadTF = new JTextField(15);
                    JButton loadMapButton = new JButton("Load");

                    loadMapButton.addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent e) {
                            loadMapDialog.setVisible(false);

                            String[] p_string = File_Utility.read_file(loadTF.getText());
                            int[][] obs = Map_Descriptor.get_map(p_string[0], p_string[1]);
                            realArena.setAllExplored();
                            realArena.arena.update_arena(obs);
                            realArena.arena.get_view();

                            CardLayout cl = ((CardLayout) _mapCards.getLayout());
                            cl.show(_mapCards, "REAL_MAP");
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

            // Fastest Path Button
            JButton btn_FastestPath = new JButton("Fastest Path");
            formatButton(btn_FastestPath);
            btn_FastestPath.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    CardLayout cl = ((CardLayout) _mapCards.getLayout());
                    cl.show(_mapCards, "EXPLORATION");
                    //new FastestPath().execute();
                }
            });
            _buttons.add(btn_FastestPath);
        }
    }
}
