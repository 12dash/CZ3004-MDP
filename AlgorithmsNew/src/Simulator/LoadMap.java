package Simulator;

import Utility.FileManager;
import Utility.MapDescriptor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoadMap extends JPanel {

    Map map;

    private JTextField file;

    private void loadingMap(){
        String[] p_string = FileManager.readFile(file.getText());
        int[][] obs = MapDescriptor.getMap(p_string[0], p_string[1]);
        map.arena.make_arena(obs);
        map.arena.setExplored();
        map.repaint();
    }
    class LoadMapInner extends SwingWorker<Integer, String> {
        protected Integer doInBackground() throws Exception {
            loadingMap();
            return 222;
        }
    }

    public LoadMap(Map map){
        this.map = map;

        this.setBorder(new EmptyBorder(10,10,10,10));

        this.add(new JLabel("File Name : "));
        file = new JTextField("*.txt",10);
        this.add(file);

        JButton btnCount = new JButton("Load");
        this.add(btnCount);
        btnCount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoadMapInner().execute();
            }
        });
        this.setVisible(true);
    }
}
