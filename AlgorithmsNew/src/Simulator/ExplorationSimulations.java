package Simulator;

import Algo.RightWallHugging;
import Utility.PrintConsole;

import Robot.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.TimeUnit;

public class ExplorationSimulations extends JPanel {
    Map map;
    int step_exploration = 100;
    int minute = 5;
    int seconds = 59;

    public void simulateRobotMovement() {
        System.out.println("Calling movement");
        Algo.RightWallHugging obj = new RightWallHugging(map.arena, map.robotSimulator);
        System.out.println("Calling movement 1");
        obj.move();
        System.out.println("Calling movement 2");
        while (!map.robotSimulator.getCur().equals(map.arena.grids[RobotConstants.ROBOT_START_Y][RobotConstants.ROBOT_START_X])) {
            System.out.println("1234");
            try {
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in robot simulation!");
            }
            obj.move();
            map.repaint();
        }
    }

    class ExploreMap extends SwingWorker<Integer, String> {
        protected Integer doInBackground() throws Exception {
            simulateRobotMovement();
            return 222;
        }
    }

    private void setLimit(Integer[] steps, int max) {
        for (int i = 0; i <= max; i++)
            steps[i] = i;
    }

    public ExplorationSimulations(Map map) {
        this.map = map;

        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.add(new JLabel("Percent Explorations : "));
        Integer[] steps = new Integer[101]; // auto-upcast
        setLimit(steps, 100);

        final JComboBox<Integer> comboCount = new JComboBox<Integer>(steps);
        comboCount.setPreferredSize(new Dimension(60, 20));
        comboCount.setSelectedIndex(steps.length - 1);
        this.add(comboCount);
        comboCount.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    step_exploration = (Integer) comboCount.getSelectedItem();
                }
            }
        });

        this.add(new JLabel("Time : "));
        Integer[] min = new Integer[6]; // auto-upcast
        setLimit(min, 5);

        final JComboBox<Integer> comboMinute = new JComboBox<Integer>(min);
        comboMinute.setPreferredSize(new Dimension(60, 20));
        comboMinute.setSelectedIndex(min.length - 1);
        this.add(comboMinute);
        comboMinute.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    minute = (Integer) comboMinute.getSelectedItem();
                }
            }
        });

        this.add(new JLabel(" : "));
        Integer[] sec = new Integer[60]; // auto-upcast
        setLimit(sec, 59);

        final JComboBox<Integer> comboSec = new JComboBox<Integer>(sec);
        comboSec.setPreferredSize(new Dimension(60, 20));
        comboSec.setSelectedIndex(sec.length - 1);
        this.add(comboSec);
        comboSec.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    seconds = (Integer) comboSec.getSelectedItem();
                }
            }
        });


        JButton btnCount = new JButton("Explore");
        this.add(btnCount);
        btnCount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button being pressed for exploration");
                map.repaint();
                new ExploreMap().execute();
            }
        });
        this.setVisible(true);


    }
}
