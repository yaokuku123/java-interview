package com.yqj.jvm;

import com.sun.crypto.provider.DESKeyFactory;
import sun.misc.Launcher;

import java.net.URL;

public class TestJDKClassLoader {
    public static void main(String[] args) {
        System.out.println(String.class.getClassLoader());
        System.out.println(DESKeyFactory.class.getClassLoader());
        System.out.println(TestJDKClassLoader.class.getClassLoader());
        System.out.println();

        ClassLoader appClassLoader = ClassLoader.getSystemClassLoader();
        ClassLoader extClassLoader = appClassLoader.getParent();
        ClassLoader bootstrapLoader = extClassLoader.getParent();
        System.out.println("bootstrapLoader: " + bootstrapLoader);
        System.out.println("extClassLoader: " + extClassLoader);
        System.out.println("appClassLoader: " + appClassLoader);
        System.out.println();

        System.out.println("bootstrapLoader 加载的文件：");
        URL[] urLs = Launcher.getBootstrapClassPath().getURLs();
        for (URL urL : urLs) {
            System.out.println(urL);
        }
        System.out.println();

        System.out.println("extClassLoader 加载的文件：");
        System.out.println(System.getProperty("java.ext.dirs"));
        System.out.println();

        System.out.println("appClassLoader 加载的文件：");
        System.out.println(System.getProperty("java.class.path"));

    }
}
