package me;



import javassist.*;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class ClassTransformer implements ClassFileTransformer {

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String thisClassName=className.replace("/",".");
        if (!thisClassName.equals("com.thunisoft.agent")&&thisClassName.equals("com.mysql.cj.jdbc.NonRegisteringDriver")) {
            //modify byte-code
            System.out.println(className+"@@@@@@@@@@@");
            return createNewClass(loader,className,classfileBuffer);
        }
        return classfileBuffer;
        }


        private byte[] createNewClass(ClassLoader loader, String className, byte[] byteCode){
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = null;
                try {
                    cc = cp.get(className);
                } catch (NotFoundException e) {
                    cp.insertClassPath(new LoaderClassPath(loader));
                    cc = cp.get(className);
                }
                byteCode = aopLog(cc, className, byteCode);
            } catch (Exception ex) {
                System.err.println(ex);
            }
            return byteCode;
        }
    private byte[] aopLog(CtClass cc, String className, byte[] byteCode) throws CannotCompileException, IOException {
        if (null == cc) {
            return byteCode;
        }
        if (!cc.isInterface()) {
            CtMethod[] methods = cc.getDeclaredMethods();
            if (null != methods && methods.length > 0) {
                for (CtMethod m : methods) {
                    if (m.getName().equals("connect")) {
                        aopLog(className, m);
                    }
                }
                byteCode = cc.toBytecode();
            }
        }
        cc.detach();
        return byteCode;
    }

    private void aopLog(String className,
                        CtMethod m) throws CannotCompileException {
        if (null == m || m.isEmpty()) {
            return;
        }
        boolean isMethodStatic = Modifier.isStatic(m.getModifiers());
        String aopClassName = isMethodStatic ? "\"" + className + "\""
                : "this.getClass().getName()";
        //避免变量名重复
        m.addLocalVariable("dingjsh_javaagent_elapsedTime", CtClass.longType);
        m.insertBefore(
                "dingjsh_javaagent_elapsedTime = java.lang.System.currentTimeMillis();");
        m.insertAfter(
                "dingjsh_javaagent_elapsedTime = java.lang.System.currentTimeMillis() - dingjsh_javaagent_elapsedTime;");
    }
    }
