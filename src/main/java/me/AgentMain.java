package me;

import java.lang.instrument.Instrumentation;

public class AgentMain {

    public static void premain(String args1,Instrumentation args2 ){
        args2.addTransformer(new ClassTransformer());

    }
}
