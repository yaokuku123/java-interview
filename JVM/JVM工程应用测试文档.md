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

## 2 对象的创建

### 2.1 对象大小与指针压缩

```java
package com.yqj.jvm;

import org.openjdk.jol.info.ClassLayout;

public class JolSample {
    public static void main(String[] args) {
        ClassLayout layout1 = ClassLayout.parseInstance(new Object());
        System.out.println(layout1.toPrintable());

        System.out.println();
        ClassLayout layout2 = ClassLayout.parseInstance(new int[]{});
        System.out.println(layout2.toPrintable());

        System.out.println();
        ClassLayout layout3 = ClassLayout.parseInstance(new SampleA());
        System.out.println(layout3.toPrintable());
    }
}

//对象内存布局分析
// ‐XX:+UseCompressedOops 默认开启的压缩所有指针
// ‐XX:+UseCompressedClassPointers 默认开启的压缩对象头里的类型指针Klass Pointer
class SampleA {
  	//8B mark word
  	//4B Klass Pointer 如果关闭压缩‐XX:‐UseCompressedClassPointers或‐XX:‐UseCompressedOops，则占用8B
    int id; //4B
    String name; //4B 如果关闭压缩‐XX:‐UseCompressedOops，则占用8B
    byte b; //1B
    Object o; //4B 如果关闭压缩‐XX:‐UseCompressedOops，则占用8B
}
```

```java
// result
# WARNING: Unable to attach Serviceability Agent. Unable to attach even with module exceptions: [org.openjdk.jol.vm.sa.SASupportException: Sense failed., org.openjdk.jol.vm.sa.SASupportException: Sense failed., org.openjdk.jol.vm.sa.SASupportException: Sense failed.]
java.lang.Object object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1) // mark word
      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0) // mark word
      8     4        (object header)                           68 0f 00 00 (01101000 00001111 00000000 00000000) (3944) // Klass Pointer
     12     4        (loss due to the next object alignment)// Padding
Instance size: 16 bytes
Space losses: 0 bytes internal + 4 bytes external = 4 bytes total


[I object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1) // mark word
      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0) // mark word
      8     4        (object header)                           98 0b 00 00 (10011000 00001011 00000000 00000000) (2968) // Klass Pointer
     12     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0) // array length
     16     0    int [I.<elements>                             N/A//无需Padding，刚好对齐
Instance size: 16 bytes
Space losses: 0 bytes internal + 0 bytes external = 0 bytes total


com.yqj.jvm.SampleA object internals:
 OFFSET  SIZE               TYPE DESCRIPTION                               VALUE
      0     4                    (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1) // mark word
      4     4                    (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0) // mark word
      8     4                    (object header)                           98 13 08 00 (10011000 00010011 00001000 00000000) (529304) // Klass Pointer(指针压缩8B)
     12     4                int SampleA.id      // 4B int                          0
     16     1               byte SampleA.b  // 1B byte                                0
     17     3                    (alignment/padding gap)  // padding                
     20     4   java.lang.String SampleA.name  // 4B String (指针压缩8B)                         null
     24     4   java.lang.Object SampleA.o  // 4B Object (指针压缩8B)                              null
     28     4                    (loss due to the next object alignment) // padding
Instance size: 32 bytes
Space losses: 3 bytes internal + 4 bytes external = 7 bytes total
```

## 3 JVM调优工具使用

### 3.1 Jmap工具

1. 启动web应用服务

```shell
java -jar microservice-eureka-server.jar &
```

2. 查看当前java进程

```shell
jps
# result
11513 Jps
11434 jar
```

3. 查看内存信息

```shell
jmap -histo 11434 > ./log.txt
# result
# result
# num： 序号
# instances：实例数量
# bytes：占用空间大小
# class name：类名称（[C is a char[]，[S is a short[]，[I is a int[]，[B is a byte[]，[[I is a int[][]）
 num     #instances         #bytes  class name
----------------------------------------------
   1:        112571       16612816  [C
   2:         20347        9336952  [I
   3:         31169        2742872  java.lang.reflect.Method
   4:        100570        2413680  java.lang.String
   5:          6213        2408840  [B
   ....
```

4. 查看堆信息

```shell
jmap -heap 11434
# result
Attaching to process ID 11434, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.301-b09

using thread-local object allocation.
Mark Sweep Compact GC

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 1000341504 (954.0MB)
   NewSize                  = 20971520 (20.0MB)
   MaxNewSize               = 333447168 (318.0MB)
   OldSize                  = 41943040 (40.0MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
New Generation (Eden + 1 Survivor Space):
   capacity = 18939904 (18.0625MB)
   used     = 17072760 (16.28185272216797MB)
   free     = 1867144 (1.7806472778320312MB)
   90.14174517463235% used
Eden Space:
   capacity = 16842752 (16.0625MB)
   used     = 16113712 (15.367233276367188MB)
   free     = 729040 (0.6952667236328125MB)
   95.67149121473736% used
From Space:
   capacity = 2097152 (2.0MB)
   used     = 959048 (0.9146194458007812MB)
   free     = 1138104 (1.0853805541992188MB)
   45.73097229003906% used
To Space:
   capacity = 2097152 (2.0MB)
   used     = 0 (0.0MB)
   free     = 2097152 (2.0MB)
   0.0% used
tenured generation:
   capacity = 41943040 (40.0MB)
   used     = 40041376 (38.186431884765625MB)
   free     = 1901664 (1.813568115234375MB)
   95.46607971191406% used

24922 interned Strings occupying 3161616 bytes.
```

5. dump堆内存

```shell
jmap -dump:format=b,file=eureka.hprof 11434
Dumping heap to /root/eureka.hprof ...
Heap dump file created
```

6. 使用jvisualvm命令导入该dump文件

```shell
jvisualvm
```

![image-20220106142011343](../../img/image-20220106142011343.png)

### 3.2 Jstack工具

1. jstack 查找死锁

```java
//死锁程序
package com.yqj.jvm;

public class DeadLockTest {
    private static final Object lock1 = new Object();
    private static final Object lock2 = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (lock1) {
                try {
                    System.out.println("thread1 begin");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2) {
                    System.out.println("thread1 end");
                }
            }
        }).start();

        new Thread(() -> {
            synchronized (lock2) {
                try {
                    System.out.println("thread2 begin");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock1) {
                    System.out.println("thread2 end");
                }
            }
        }).start();

        System.out.println("main thread end");
    }
}
```

* jstack检查死锁

```shell
# jstack + id 查找死锁
jstack 16909
# result
2022-01-06 14:26:27
Full thread dump OpenJDK 64-Bit Server VM (25.292-b10 mixed mode):

"Attach Listener" #14 daemon prio=9 os_prio=31 tid=0x000000013c8b4800 nid=0xa403 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"DestroyJavaVM" #13 prio=5 os_prio=31 tid=0x000000013a1ae000 nid=0x2903 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

# Thread-1 线程名
# prio 优先级5
# tid 线程id
# nid 线程对应的本地线程标识nid
"Thread-1" #12 prio=5 os_prio=31 tid=0x000000011d010800 nid=0x5703 waiting for monitor entry [0x00000001707a6000]
   java.lang.Thread.State: BLOCKED (on object monitor)
	at com.yqj.jvm.DeadLockTest.lambda$main$1(DeadLockTest.java:31)
	- waiting to lock <0x000000076aca1108> (a java.lang.Object)
	- locked <0x000000076aca1118> (a java.lang.Object)
	at com.yqj.jvm.DeadLockTest$$Lambda$2/793589513.run(Unknown Source)
	at java.lang.Thread.run(Thread.java:748)

"Thread-0" #11 prio=5 os_prio=31 tid=0x000000013c8ac000 nid=0xa603 waiting for monitor entry [0x000000017059a000]
   java.lang.Thread.State: BLOCKED (on object monitor)
	at com.yqj.jvm.DeadLockTest.lambda$main$0(DeadLockTest.java:17)
	- waiting to lock <0x000000076aca1118> (a java.lang.Object)
	- locked <0x000000076aca1108> (a java.lang.Object)
	at com.yqj.jvm.DeadLockTest$$Lambda$1/1915910607.run(Unknown Source)
	at java.lang.Thread.run(Thread.java:748)

"Service Thread" #10 daemon prio=9 os_prio=31 tid=0x000000011c818800 nid=0x5603 runnable [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C1 CompilerThread3" #9 daemon prio=9 os_prio=31 tid=0x000000013a01a800 nid=0x3e03 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread2" #8 daemon prio=9 os_prio=31 tid=0x000000013a019800 nid=0x4003 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread1" #7 daemon prio=9 os_prio=31 tid=0x000000013a020800 nid=0x3c03 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread0" #6 daemon prio=9 os_prio=31 tid=0x000000013a01f800 nid=0x3a03 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Monitor Ctrl-Break" #5 daemon prio=5 os_prio=31 tid=0x000000013a010000 nid=0x3803 runnable [0x000000016f746000]
   java.lang.Thread.State: RUNNABLE
	at java.net.SocketInputStream.socketRead0(Native Method)
	at java.net.SocketInputStream.socketRead(SocketInputStream.java:116)
	at java.net.SocketInputStream.read(SocketInputStream.java:171)
	at java.net.SocketInputStream.read(SocketInputStream.java:141)
	at sun.nio.cs.StreamDecoder.readBytes(StreamDecoder.java:284)
	at sun.nio.cs.StreamDecoder.implRead(StreamDecoder.java:326)
	at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:178)
	- locked <0x000000076adc4398> (a java.io.InputStreamReader)
	at java.io.InputStreamReader.read(InputStreamReader.java:184)
	at java.io.BufferedReader.fill(BufferedReader.java:161)
	at java.io.BufferedReader.readLine(BufferedReader.java:324)
	- locked <0x000000076adc4398> (a java.io.InputStreamReader)
	at java.io.BufferedReader.readLine(BufferedReader.java:389)
	at com.intellij.rt.execution.application.AppMainV2$1.run(AppMainV2.java:61)

"Signal Dispatcher" #4 daemon prio=9 os_prio=31 tid=0x000000013c04e800 nid=0x4203 runnable [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Finalizer" #3 daemon prio=8 os_prio=31 tid=0x000000013c022800 nid=0x4903 in Object.wait() [0x000000016f216000]
   java.lang.Thread.State: WAITING (on object monitor)
	at java.lang.Object.wait(Native Method)
	- waiting on <0x000000076ab08ef0> (a java.lang.ref.ReferenceQueue$Lock)
	at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:144)
	- locked <0x000000076ab08ef0> (a java.lang.ref.ReferenceQueue$Lock)
	at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:165)
	at java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:216)

"Reference Handler" #2 daemon prio=10 os_prio=31 tid=0x000000013b80b000 nid=0x2f03 in Object.wait() [0x000000016f00a000]
   java.lang.Thread.State: WAITING (on object monitor)
	at java.lang.Object.wait(Native Method)
	- waiting on <0x000000076ab06c08> (a java.lang.ref.Reference$Lock)
	at java.lang.Object.wait(Object.java:502)
	at java.lang.ref.Reference.tryHandlePending(Reference.java:191)
	- locked <0x000000076ab06c08> (a java.lang.ref.Reference$Lock)
	at java.lang.ref.Reference$ReferenceHandler.run(Reference.java:153)

"VM Thread" os_prio=31 tid=0x000000013c847000 nid=0x2e03 runnable

"ParGC Thread#0" os_prio=31 tid=0x000000013c010800 nid=0x2107 runnable

"ParGC Thread#1" os_prio=31 tid=0x000000013c01c000 nid=0x2003 runnable

"ParGC Thread#2" os_prio=31 tid=0x000000013c01d000 nid=0x5403 runnable

"ParGC Thread#3" os_prio=31 tid=0x000000013c01d800 nid=0x2b03 runnable

"ParGC Thread#4" os_prio=31 tid=0x000000013c01e800 nid=0x5103 runnable

"ParGC Thread#5" os_prio=31 tid=0x000000013c808800 nid=0x5003 runnable

"ParGC Thread#6" os_prio=31 tid=0x000000013c01f000 nid=0x4e03 runnable

"ParGC Thread#7" os_prio=31 tid=0x000000013c020000 nid=0x4d03 runnable

"VM Periodic Task Thread" os_prio=31 tid=0x000000011c821000 nid=0xa803 waiting on condition

JNI global references: 320


Found one Java-level deadlock:
=============================
"Thread-1":
  waiting to lock monitor 0x000000013c8afd60 (object 0x000000076aca1108, a java.lang.Object),
  which is held by "Thread-0"
"Thread-0":
  waiting to lock monitor 0x000000013b814780 (object 0x000000076aca1118, a java.lang.Object),
  which is held by "Thread-1"

Java stack information for the threads listed above:
===================================================
"Thread-1":
	at com.yqj.jvm.DeadLockTest.lambda$main$1(DeadLockTest.java:31)
	- waiting to lock <0x000000076aca1108> (a java.lang.Object)
	- locked <0x000000076aca1118> (a java.lang.Object)
	at com.yqj.jvm.DeadLockTest$$Lambda$2/793589513.run(Unknown Source)
	at java.lang.Thread.run(Thread.java:748)
"Thread-0":
	at com.yqj.jvm.DeadLockTest.lambda$main$0(DeadLockTest.java:17)
	- waiting to lock <0x000000076aca1118> (a java.lang.Object)
	- locked <0x000000076aca1108> (a java.lang.Object)
	at com.yqj.jvm.DeadLockTest$$Lambda$1/1915910607.run(Unknown Source)
	at java.lang.Thread.run(Thread.java:748)

Found 1 deadlock.
```

* jvisualvm检查死锁

![image-20220106143646698](../../img/image-20220106143646698.png)

2. Stack 查找占有CPU最高的线程栈信息(Linux)

* 使用命令 top 查看java进程的cpu使用情况

```shell
top -p 12023
```

![image-20220106150135499](../../img/image-20220106150135499.png)

* 按H，获取每个线程的cpu使用情况

![image-20220106150229266](../../img/image-20220106150229266.png)

* 找到CPU占用最高的线程id，比如12024
* 转为16进制得到0x2ef8，为线程id的16进制形式
* 执行jstack命令，得到线程堆栈信息中2ef8这个线程所在行的后面10行，可以发现导致CPU升高的方法

```shell
jstack 12023 | grep -A 10 2ef8
```

![image-20220106150602206](../../img/image-20220106150602206.png)

* 查看对应的堆栈信息找出可能存在问题的代码

### 3.3 Jinfo工具

1. 查看进程使用的jvm参数

```shell
jinfo -flags 12023
# result
Attaching to process ID 12023, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.301-b09
Non-default VM flags: -XX:CICompilerCount=2 -XX:InitialHeapSize=62914560 -XX:MaxHeapSize=1000341504 -XX:MaxNewSize=333447168 -XX:MinHeapDeltaBytes=196608 -XX:NewSize=20971520 -XX:OldSize=41943040 -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseFastUnorderedTimeStamps
Command line:
```

2. 查看java系统参数

```shell
jinfo -sysprops 12023
# result
Attaching to process ID 12023, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.301-b09
java.runtime.name = Java(TM) SE Runtime Environment
java.vm.version = 25.301-b09
sun.boot.library.path = /usr/local/jdk1.8.0_301/jre/lib/amd64
java.protocol.handler.pkgs = org.springframework.boot.loader
java.vendor.url = http://java.oracle.com/
java.vm.vendor = Oracle Corporation
path.separator = :
file.encoding.pkg = sun.io
java.vm.name = Java HotSpot(TM) 64-Bit Server VM
sun.os.patch.level = unknown
sun.java.launcher = SUN_STANDARD
user.country = US
user.dir = /root
java.vm.specification.name = Java Virtual Machine Specification
PID = 12023
java.runtime.version = 1.8.0_301-b09
java.awt.graphicsenv = sun.awt.X11GraphicsEnvironment
os.arch = amd64
java.endorsed.dirs = /usr/local/jdk1.8.0_301/jre/lib/endorsed
org.jboss.logging.provider = slf4j
line.separator =

java.io.tmpdir = /tmp
java.vm.specification.vendor = Oracle Corporation
os.name = Linux
sun.jnu.encoding = UTF-8
java.library.path = /usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib
spring.beaninfo.ignore = true
com.netflix.servo.DefaultMonitorRegistry.registryClass = com.netflix.servo.BasicMonitorRegistry
java.specification.name = Java Platform API Specification
java.class.version = 52.0
sun.management.compiler = HotSpot 64-Bit Tiered Compilers
os.version = 4.4.0-210-generic
user.home = /root
user.timezone = Asia/Shanghai
catalina.useNaming = false
java.awt.printerjob = sun.print.PSPrinterJob
file.encoding = UTF-8
@appId = application
java.specification.version = 1.8
catalina.home = /tmp/tomcat.6949533165135200763.8761
user.name = root
java.class.path = microservice-eureka-server.jar
java.vm.specification.version = 1.8
sun.arch.data.model = 64
sun.java.command = microservice-eureka-server.jar
java.home = /usr/local/jdk1.8.0_301/jre
user.language = en
java.specification.vendor = Oracle Corporation
awt.toolkit = sun.awt.X11.XToolkit
java.vm.info = mixed mode
java.version = 1.8.0_301
java.ext.dirs = /usr/local/jdk1.8.0_301/jre/lib/ext:/usr/java/packages/lib/ext
sun.boot.class.path = /usr/local/jdk1.8.0_301/jre/lib/resources.jar:/usr/local/jdk1.8.0_301/jre/lib/rt.jar:/usr/local/jdk1.8.0_301/jre/lib/sunrsasign.jar:/usr/local/jdk1.8.0_301/jre/lib/jsse.jar:/usr/local/jdk1.8.0_301/jre/lib/jce.jar:/usr/local/jdk1.8.0_301/jre/lib/charsets.jar:/usr/local/jdk1.8.0_301/jre/lib/jfr.jar:/usr/local/jdk1.8.0_301/jre/classes
java.awt.headless = true
java.vendor = Oracle Corporation
catalina.base = /tmp/tomcat.6949533165135200763.8761
file.separator = /
java.vendor.url.bug = http://bugreport.sun.com/bugreport/
sun.io.unicode.encoding = UnicodeLittle
sun.cpu.endian = little
sun.cpu.isalist =
```

### 3.4 Jstat工具

jstat命令可以查看堆内存各部分的使用量，以及加载类的数量。

1. 垃圾回收统计

```shell
jstat -gc 12023
# result
# S0C:第一个幸存区的大小，单位KB 
# S1C:第二个幸存区的大小 
# S0U:第一个幸存区的使用大小
# S1U:第二个幸存区的使用大小 
# EC:伊甸园区的大小 
# EU:伊甸园区的使用大小 
# OC:老年代大小
# OU:老年代使用大小 
# MC:方法区大小(元空间)
# MU:方法区使用大小
# CCSC:压缩类空间大小
# CCSU:压缩类空间使用大小
# YGC:年轻代垃圾回收次数
# YGCT:年轻代垃圾回收消耗时间，单位s
# FGC:老年代垃圾回收次数 
# FGCT:老年代垃圾回收消耗时间，单位s 
# GCT:垃圾回收消耗总时间，单位s
S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT
2048.0 2048.0  0.0   621.1  16448.0   6621.0   40960.0    39258.3   48896.0 47992.5 6400.0 6124.1    201    0.799   2      0.081    0.880
```

2. 堆内存统计

```shell
jstat -gccapacity 12023
# result
# NGCMN:新生代最小容量
# NGCMX:新生代最大容量
# NGC:当前新生代容量 
# S0C:第一个幸存区大小 
# S1C:第二个幸存区的大小 
# EC:伊甸园区的大小 
# OGCMN:老年代最小容量 
# OGCMX:老年代最大容量 
# OGC:当前老年代大小 
# OC:当前老年代大小 
# MCMN:最小元数据容量 
# MCMX:最大元数据容量 
# MC:当前元数据空间大小 
# CCSMN:最小压缩类空间大小 
# CCSMX:最大压缩类空间大小 
# CCSC:当前压缩类空间大小 
# YGC:年轻代gc次数 
# FGC:老年代GC次数
 NGCMN    NGCMX     NGC     S0C   S1C       EC      OGCMN      OGCMX       OGC         OC       MCMN     MCMX      MC     CCSMN    CCSMX     CCSC    YGC    FGC
 20480.0 325632.0  20544.0 2048.0 2048.0  16448.0    40960.0   651264.0    40960.0    40960.0      0.0 1091584.0  48896.0      0.0 1048576.0   6400.0    201     2
```

## 4 常量池

### 4.1 字符串常量池

```java

```

