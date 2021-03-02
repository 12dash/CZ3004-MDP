package Robot;

import Environment.Grid;
import Values.Orientation;

import java.util.ArrayList;
import java.util.HashMap;

public class RobotReal extends Robot{

    protected ArrayList<Grid> path = new ArrayList<>();  //Stores the fastest path solution
    private Orientation cur_or;     // For calculating the commands in Fastest Path

    public RobotReal(Grid g) {
        super(g);
    }

    public void intiliase_path(){
        this.path = new ArrayList<>();
    }


    private String nextMove(Grid cur, Grid next) {
        Orientation move_dir;

        int cur_X = cur.getX();
        int cur_Y = cur.getY();

        int next_X = next.getX();
        int next_Y = next.getY();

        if (cur_X == next_X) {
           move_dir = (cur_Y - next_Y) > 0 ? Orientation.North : Orientation.South;
        }
        else{
           move_dir = (cur_X - next_X) > 0 ? Orientation.West : Orientation.East;
        }

        String move="";

        switch(this.cur_or) {
            case North:
                switch (move_dir){
                    case North:
                        move = RobotConstants.STRAIGHT;
                        break;
                    case East:
                        move = RobotConstants.RIGHT + RobotConstants.STRAIGHT;
                        break;
                    case South:
                        move = RobotConstants.BACK + RobotConstants.STRAIGHT;
                        break;
                    case West:
                        move = RobotConstants.LEFT + RobotConstants.STRAIGHT;
                        break;
                }
            break;

            case East:
                switch (move_dir){
                    case North:
                        move = RobotConstants.LEFT + RobotConstants.STRAIGHT;
                        break;
                    case East:
                        move = RobotConstants.STRAIGHT;
                        break;
                    case South:
                        move = RobotConstants.RIGHT + RobotConstants.STRAIGHT;
                        break;
                    case West:
                        move = RobotConstants.BACK + RobotConstants.STRAIGHT;
                        break;
                }
            break;

            case South:
                switch (move_dir){
                    case North:
                        move = RobotConstants.BACK + RobotConstants.STRAIGHT;
                        break;
                    case East:
                        move = RobotConstants.LEFT + RobotConstants.STRAIGHT;
                        break;
                    case South:
                        move = RobotConstants.STRAIGHT;
                        break;
                    case West:
                        move = RobotConstants.RIGHT + RobotConstants.STRAIGHT;
                        break;
                }
            break;

            case West:
                switch (move_dir){
                    case North:
                        move = RobotConstants.RIGHT + RobotConstants.STRAIGHT;
                        break;
                    case East:
                        move = RobotConstants.BACK+ RobotConstants.STRAIGHT;
                        break;
                    case South:
                        move = RobotConstants.LEFT + RobotConstants.STRAIGHT;
                        break;
                    case West:
                        move = RobotConstants.STRAIGHT;
                        break;
                }
            break;
        }

        this.cur_or = move_dir;
        return move;
    }


    public String getCommandString(StringBuilder commands){  // Convert all the 'S' to numbers

        StringBuilder cmds = new StringBuilder();
        String temp = commands.toString();
        System.out.format("Commands: %s", temp);

        char[] commandsArray = temp.toCharArray();
        System.out.println();
        int i = 0;
        while(i < commandsArray.length){
            if (commandsArray[i] != 'S'){
                cmds.append(commandsArray[i]);
                i++;
            }
            else{
                int countS = -1;        // 0 => 1 step forward
                while(i < commandsArray.length && commandsArray[i] == 'S'){
                    countS++;
                    i++;
                }

                if (countS >= 10){      // For number of forward steps greater than 10
                    cmds.append(RobotConstants.NUMBER_MAPPINGS.get(countS+1));
                }
                else {
                    cmds.append(countS);
                }
            }
        }
        return cmds.toString();
    }

    public String generateMovementCommands(Orientation initial_or) {
        this.cur_or = initial_or;

        StringBuilder commands = new StringBuilder(); // Stores the commands to send to Arduino
        commands = new StringBuilder();

        for (int i = 0; i < this.path.size()-1; i++) {
                commands.append(nextMove(path.get(i), path.get(i + 1)));
        }
        return getCommandString(commands);
    }


    public ArrayList<Grid> getPath() {
        return this.path;
    }

    public void setPath(ArrayList<Grid> path) {
        this.path = path;

    }

}


