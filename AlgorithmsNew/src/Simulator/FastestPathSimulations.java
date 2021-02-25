package Simulator;

import Robot.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.TimeUnit;

public class FastestPathSimulations extends JPanel {

    public int step = 1;

    public int x = 0;
    public int y = 0;

    private final JTextField WayPoint_x;
    private final JTextField WayPoint_y;

    Map map;

    public void simulateRobotMovement() {
        System.out.println("Calling movement");
        int length = this.map.robotSimulator.getPath().size();
        System.out.println(length);
        System.out.println(this.map.robotSimulator.getOrientations().size());
        for (int i = 0; i < length; ) {
            try {
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in robot simulation!");
            }
            map.robotSimulator.updatePosition(map.robotSimulator.getPath().get(i), map.robotSimulator.getOrientations().get(i));
            map.repaint();
            i += step;
            if (i >= length) i = i - step + 1;

        }
    }

    class FastestPath extends SwingWorker<Integer, String> {
        protected Integer doInBackground() throws Exception {
            System.out.println("Fast1");
            int[] pos = new int[]{x, y}; //x,y
            map.robotSimulator.setPath(Algo.FastestPath.findPath(map.arena, pos));
            map.robotSimulator.getPathOrientation();
            simulateRobotMovement();
            return 222;
        }
    }


    public FastestPathSimulations(Map map) {
        this.map = map;

        this.setBorder(new EmptyBorder(10,10,10,10));

        this.add(new JLabel("Waypoint x:"));
        WayPoint_x = new JTextField("0", 3);
        this.add(WayPoint_x);


        this.add(new JLabel("Waypoint y:"));
        WayPoint_y = new JTextField("0", 3);
        this.add(WayPoint_y);

        this.add(new JLabel("Step"));
        final Integer[] steps = {1, 2, 3, 4, 5};  // auto-upcast
        final JComboBox<Integer> comboCount = new JComboBox<Integer>(steps);

        comboCount.setPreferredSize(new Dimension(60, 20));
        this.add(comboCount);

        comboCount.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    step = (Integer) comboCount.getSelectedItem();
                }
            }
        });

        JButton btnCount = new JButton("Go");
        this.add(btnCount);
        btnCount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                map.arena.setExplored();
                x = Integer.parseInt(WayPoint_x.getText());
                y = Integer.parseInt(WayPoint_y.getText());
                new FastestPath().execute();
            }
        });
        this.setVisible(true);
    }

}
