# JVM工程应用测试

## 1 JVM类加载机制

### 1.1 类的逐步加载

说明：jar包或war包里的类不是一次性全部加载的，是使用到时才加载。

```java
package com.yqj.jvm;

public class TestDynamicLoad {
    static {
        System.out.println("load TestDynamicLoad");
    }

    public static void main(String[] args) {
        new A();
        System.out.println("load test");
        B b = null; //B不会加载，除非这里执行 new B()
    }
}

class A {
    static {
        System.out.println("load A");
    }

    public A() {
        System.out.println("init A");
    }
}

class B {
    static {
        System.out.println("load B");
    }

    public B() {
        System.out.println("init B");
    }
}
```

```java
// result
load TestDynamicLoad
load A
init A
load test
```

### 1.2 类加载器示例

说明：查看三种类型的类加载器实例，以及各种类加载器加载的路径信息

```java
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
```

```java
// result
null
sun.misc.Launcher$ExtClassLoader@4b1210ee
sun.misc.Launcher$AppClassLoader@18b4aac2

bootstrapLoader: null
extClassLoader: sun.misc.Launcher$ExtClassLoader@4b1210ee
appClassLoader: sun.misc.Launcher$AppClassLoader@18b4aac2

bootstrapLoader 加载的文件：
file:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/resources.jar
file:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/rt.jar
file:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/sunrsasign.jar
file:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/jsse.jar
file:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/jce.jar
file:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/charsets.jar
file:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/jfr.jar
file:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/classes

extClassLoader 加载的文件：
/Users/yorick/Library/Java/Extensions:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java

appClassLoader 加载的文件：
/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/crs-agent.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/cldrdata.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/dnsns.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/jaccess.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/legacy8ujsse.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/localedata.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/nashorn.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/openjsse.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/sunec.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/ext/zipfs.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/management-agent.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/lib/dt.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/lib/jconsole.jar:/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/lib/tools.jar:/Users/yorick/Documents/study/blog/面试/test-tmp/target/classes:/Users/yorick/.m2/repository/org/projectlombok/lombok/1.18.12/lombok-1.18.12.jar:/Users/yorick/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.12.4/jackson-databind-2.12.4.jar:/Users/yorick/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.12.4/jackson-annotations-2.12.4.jar:/Users/yorick/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.12.4/jackson-core-2.12.4.jar:/Users/yorick/.m2/repository/mysql/mysql-connector-java/5.1.6/mysql-connector-java-5.1.6.jar:/Users/yorick/.m2/repository/io/netty/netty-all/4.1.51.Final/netty-all-4.1.51.Final.jar:/Users/yorick/.m2/repository/org/junit/jupiter/junit-jupiter/5.8.2/junit-jupiter-5.8.2.jar:/Users/yorick/.m2/repository/org/junit/jupiter/junit-jupiter-api/5.8.2/junit-jupiter-api-5.8.2.jar:/Users/yorick/.m2/repository/org/opentest4j/opentest4j/1.2.0/opentest4j-1.2.0.jar:/Users/yorick/.m2/repository/org/junit/platform/junit-platform-commons/1.8.2/junit-platform-commons-1.8.2.jar:/Users/yorick/.m2/repository/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar:/Users/yorick/.m2/repository/org/junit/jupiter/junit-jupiter-params/5.8.2/junit-jupiter-params-5.8.2.jar:/Users/yorick/.m2/repository/org/junit/jupiter/junit-jupiter-engine/5.8.2/junit-jupiter-engine-5.8.2.jar:/Users/yorick/.m2/repository/org/junit/platform/junit-platform-engine/1.8.2/junit-platform-engine-1.8.2.jar:/Users/yorick/.m2/repository/org/openjdk/jol/jol-core/0.9/jol-core-0.9.jar:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar
```

### 1.3 双亲委派机制的沙箱安全机制示例

说明：自定义String类后，由于双亲委派的沙箱机制导致无法实现String的类加载

```java
package java.lang;

public class String {
    public static void main(String[] args) {
        System.out.println("hello");
    }
}
```

```java
// result
错误: 在类 java.lang.String 中找不到 main 方法, 请将 main 方法定义为:
   public static void main(String[] args)
否则 JavaFX 应用程序类必须扩展javafx.application.Application
```

### 1.4 自定义类加载器

说明：自定义类加载器只需要继承 java.lang.ClassLoader 类，该类有两个核心方法，一个是 loadClass(String, boolean)，实现了双亲委派机制，还有一个方法是findClass，默认实现是空 方法，所以我们自定义类加载器主要是重写方法。

```java
package com.yqj.jvm;

import java.io.FileInputStream;
import java.io.IOException;
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
    }

    public static void main(String[] args) throws Exception {
      //初始化自定义类加载器，会先初始化父类ClassLoader，其中会把自定义类加载器的父加载器设置为应用程序类加载器AppClassLoader
        String classPath1 = "/Users/yorick/Documents/study/code/tmp";
        MyClassLoader classLoader1 = new MyClassLoader(classPath1);
      //若classpath下有该类则由AppClassLoader加载类路径下的Hello类，若没有则由自定义类加载器加载Hello类
        Class<?> clazz1 = classLoader1.loadClass("com.yqj.jvm.Hello");
        Object obj1 = clazz1.newInstance();
        Method method1 = clazz1.getDeclaredMethod("sayHello", null);
        method1.invoke(obj1,null);
        System.out.println(clazz1.getClassLoader());
        System.out.println();
    }
}
```
```java
// Hello.java
package com.yqj.jvm;

public class Hello {
    public void sayHello() {
        System.out.println("version 2.0 ---- hello world!");
    }
}
```

```java
// classpath下存在com.yqj.jvm.Hello
version 2.0 ---- hello world!
sun.misc.Launcher$AppClassLoader@18b4aac2
// classpath下不存在com.yqj.jvm.Hello
version 1.0 ---- hello world!
com.yqj.jvm.MyClassLoaderTest$MyClassLoader@d716361
```

### 1.5 打破双亲委派机制

说明：尝试打破双亲委派机制，用自定义类加载器加载我们自己实现的com.yqj.jvm.String.class

```java
package com.yqj.jvm;

import java.io.FileInputStream;
import java.io.IOException;
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
                    c = findClass(name); //将所有类都由自定义类加载器加载导致Object.class类未找到而报错
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
    }
}

```

```java
//result
java.io.FileNotFoundException: /Users/yorick/Documents/study/code/tmp/java/lang/Object.class (No such file or directory)
	at java.io.FileInputStream.open0(Native Method)
	at java.io.FileInputStream.open(FileInputStream.java:195)
	at java.io.FileInputStream.<init>(FileInputStream.java:138)
	at java.io.FileInputStream.<init>(FileInputStream.java:93)
```

* 修复问题：将“com.yqj.jvm"下的类由自定义类加载器加载，其余类走双亲委派机制加载

```java
 @Override
protected Class<?> loadClass(String name, boolean resolve)
  throws ClassNotFoundException {
  synchronized (getClassLoadingLock(name)) {
    Class<?> c = findLoadedClass(name);
    if (c == null) {
      long t1 = System.nanoTime();
      //将“com.yqj.jvm"下的类由自定义类加载器加载，其余类走双亲委派机制加载
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
```

```java
//result
version 1.0 ---- hello world!
com.yqj.jvm.MyClassLoaderTest$MyClassLoader@d716361
```

