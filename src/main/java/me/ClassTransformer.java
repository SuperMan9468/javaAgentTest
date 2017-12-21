package me;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class ClassTransformer implements ClassFileTransformer {

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if(className.replace("/",".").equals("com.mysql.cj.jdbc.NonRegisteringDriver")){
            System.out.println(className+"@@@@@@@@@@@");
            /*ClassPool classPool=ClassPool.getDefault();
            try {
                CtClass ctClass=classPool.getCtClass(className);
                System.out.println(ctClass+"-------");
            } catch (NotFoundException e) {
                e.printStackTrace();
            }*/
        }
        return classfileBuffer;
    }
}
