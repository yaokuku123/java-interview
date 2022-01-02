package com.yqj.jvm;

import sun.misc.Launcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyClassLoaderTest {
    static class MyClassLoader extends ClassLoader {
        private String classPath;

        public MyClassLoader(String classPath) {
            this.classPath = classPath;
        }

        private byte[] loadByte(String name) throws IOException {
            name = name.replaceAll("\\.","/");
            FileInputStream fis = new FileInputStream(classPath + "/" + name + ".class");
            int len = fis.available();
            byte[] data = new byte[len];
            fis.read(data);
            fis.close();
            return data;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                byte[] data = loadByte(name);
                return defineClass(name,data,0,data.length);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ClassNotFoundException();
            }
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                Class<?> c = findLoadedClass(name);
                if (c == null) {
                    long t1 = System.nanoTime();
                    if (!name.startsWith("com.yqj.jvm")){
                        c = this.getParent().loadClass(name);
                    } else {
                        c = findClass(name);
                    }
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String classPath1 = "/Users/yorick/Documents/study/code/tmp";
        MyClassLoader classLoader1 = new MyClassLoader(classPath1);
        Class<?> clazz1 = classLoader1.loadClass("com.yqj.jvm.Hello");
        Object obj1 = clazz1.newInstance();
        Method method1 = clazz1.getDeclaredMethod("sayHello", null);
        method1.invoke(obj1,null);
        System.out.println(clazz1.getClassLoader());
        System.out.println();

        String classPath2 = "/Users/yorick/Documents/study/code/tmp2";
        MyClassLoader classLoader2 = new MyClassLoader(classPath2);
        Class<?> clazz2 = classLoader2.loadClass("com.yqj.jvm.Hello");
        Object obj2 = clazz2.newInstance();
        Method method2 = clazz2.getDeclaredMethod("sayHello", null);
        method2.invoke(obj2,null);
        System.out.println(clazz2.getClassLoader());
    }
}
