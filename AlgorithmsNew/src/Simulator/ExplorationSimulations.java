package Simulator;

import Algo.AStar;
import Algo.InnerExploration;
import Algo.RightWallHugging;
import Environment.ArenaConstants;
import Utility.MapDescriptor;
import Utility.PrintConsole;

import Robot.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ExplorationSimulations extends JPanel {
    Map map;
    int percentExploration = 100;
    long minute = 5;
    long seconds = 59;
    long time;

    public void simulateRobotMovement() {
        time = ((minute * 60) + (seconds)) * 1000;
        long start = System.currentTimeMillis();
        long end = start + time;

        Algo.RightWallHugging obj = new RightWallHugging(map.arena, map.robotSimulator);
        obj.move();
        while (!map.robotSimulator.getCur().equals(map.arena.grids[RobotConstants.ROBOT_START_Y][RobotConstants.ROBOT_START_X]) && (obj.percentExplored < percentExploration) && (System.currentTimeMillis() < end)) {
            try {
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in robot simulation!");
            }
            obj.move();
            map.repaint();
        }

        Algo.InnerExploration innerExploration = new InnerExploration(map.arena, map.robotSimulator);
        while ((innerExploration.percentExplored < percentExploration) && (System.currentTimeMillis() < end)) {
            try {
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in robot simulation!");
            }
            innerExploration.move();
            map.repaint();
        }
        /*
        To return back to the base.....
         */

        AStar astar = new AStar();
        astar.startSearch(this.map.arena, this.map.robotSimulator.getCur(), this.map.arena.grids[ArenaConstants.START_ROW][ArenaConstants.START_COL], false);

        System.out.println(astar.solution.size());

        this.map.robotSimulator.setPath(astar.solution);
        this.map.robotSimulator.getPathOrientation();

        while ((this.map.robotSimulator.getPath().size() != 0)) {
            try {
                TimeUnit.MILLISECONDS.sleep(RobotConstants.SPEED);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in robot simulation!");
            }
            this.map.robotSimulator.updatePosition(this.map.robotSimulator.getPath().remove(0), this.map.robotSimulator.getOrientations().remove(0));
            this.map.repaint();
        }

        MapDescriptor.generateMapDescriptor(this.map.arena);
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
                    percentExploration = (int) comboCount.getSelectedItem();
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
