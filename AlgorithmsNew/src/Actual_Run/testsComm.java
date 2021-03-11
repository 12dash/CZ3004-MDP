package Actual_Run;

import Communication.*;
import Communication.CommunicationConstants;
import Environment.Arena;
import Simulator.Map;
import Utility.MapDescriptor;

import java.util.Scanner;

public class testsComm {

    public static void main(String[] args){
        Communication comm = Communication.getCommunication();
        String[] p1p2 = new String[]{"FFFFFFFFFFFFFFFFFFF91FF001E003C007800F001E007C00F801F003E003C00F803F007E00FF","000700000000000001C000444444000004000020"};
        String anMssg = "{p1:" + p1p2[0] + ",p2:" + p1p2[1] +"}";
        comm.openConnection();
        Scanner sc= new Scanner(System.in);
        while(true) {
            sc.nextLine();
            comm.sendMsg(CommunicationConstants.ANDROID, anMssg);
        }
    }
}
