package me;

import java.lang.instrument.Instrumentation;

public class AgentMain {

    public static void premain(String args1,Instrumentation args2 ){
              System.out.println("hellow agent1");
    }

    public static void premain(String args1){
        System.out.println("hellow agent2");
    }
}
