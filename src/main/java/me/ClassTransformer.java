package me;



import javassist.*;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class ClassTransformer implements ClassFileTransformer {

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] myclassfileBuffer=classfileBuffer;
        String thisClassName = className.replace("/", ".");
        if (thisClassName.equals("com.mysql.cj.jdbc.NonRegisteringDriver")) {
            CtClass CtClassObject=getCtClass(className);
            if(CtClassObject!=null){
                return getAopClass(CtClassObject);
            }else{
                return null;
            }
        }
        return classfileBuffer;
    }

    private CtClass getCtClass(String pathName){
        ClassPool singleClassPool=ClassPool.getDefault();
        ClassPath classPath=null;
        try {
            classPath=singleClassPool.insertClassPath(pathName);
            System.out.println("classPath"+classPath);
        } catch (NotFoundException e) {
            singleClassPool=null;
            e.printStackTrace();
        }
        CtClass returnCtClass=null;
        try {
            returnCtClass=singleClassPool.get("com.mysql.cj.jdbc.NonRegisteringDriver");
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return returnCtClass;
    }

    private byte[] getAopClass( CtClass CtClassObject) {
        CtMethod[] methods = CtClassObject.getDeclaredMethods();
        byte[] byteCode=null;
        if (null != methods && methods.length > 0) {
            for (CtMethod m : methods) {
                if (m.getName().equals("connect")) {
                    try {
                        m.addLocalVariable("zhanggq_startTime", CtClass.longType);
                        m.addLocalVariable("zhanggq_totalTime", CtClass.longType);
                        m.insertBefore("zhanggq_startTime = java.lang.System.currentTimeMillis();");
                        m.insertAfter("zhanggq_totalTime = java.lang.System.currentTimeMillis() - zhanggq_startTime;");
                        m.insertAfter("System.out.println(\"connection 总共耗时:\"+zhanggq_totalTime);");
                    } catch (CannotCompileException e) {
                        e.printStackTrace();
                    }
                    try {
                        byteCode = CtClassObject.toBytecode();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (CannotCompileException e) {
                        e.printStackTrace();
                    }
                }
            }
            CtClassObject.detach();
        }
        return byteCode;
    }
}
