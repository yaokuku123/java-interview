package com.yqj.jvm;

public class RuntimeConstantPool {
    public static void main(String[] args) {
        // 1
        /*
            1.1 s0和s1中的"yorick"都是字符串常量，编译器确定。
            均为指向常量池中字符串"yorick"的引用，所以相等。
            1.2 "yor" "ick" 也是字符串常量，由多个字符串常量连接而成时，
            其本身也是字符串常量。
         */
//        String s0 = "yorick";
//        String s1 = "yorick";
//        String s2 = "yor" + "ick";
//        System.out.println(s0 == s1); //true
//        System.out.println(s0 == s2); //true

        // 2
        /*
            2.1 用new String() 创建的字符串不是常量，不能在编译期就确定,
            new String() 创建的字符串不放入常量池中，有自己的地址空间。
            2.2 s2有后半部分 new String(”ick”)所以也无法在编译期确定，
            所以也是一个新创建的对象。
         */
//        String s0 = "yorick";
//        String s1 = new String("yorick");
//        String s2 = "yor" + new String("ick");
//        System.out.println(s0 == s1);
//        System.out.println(s0 == s2);
//        System.out.println(s1 == s2);

        // 3
        /*
            JVM对于字符串常量的"+"号连接，在程序编译期将常量字符串的"+"连接优化为连接后的值。
         */
//        String a = "a1";
//        String b = "a" + 1;
//        System.out.println(a == b);
//        String a1 = "atrue";
//        String b1 = "a" + "true";
//        System.out.println(a1 == b1);
//        String a2 = "a3.4";
//        String b2 = "a" + 3.4;
//        System.out.println(a2 == b2);

        // 4
        /*
            JVM对于字符串引用，由于在字符串的"+"连接中，
            有字符串引用存在，而引用的值在程序编译期是无法确定的,即"a" + bb无法被编译器优化。
         */
//        String a = "yorick";
//        String b = "ick";
//        String c = "yor" + b;
//        System.out.println(a == c);

        // 5
        /*
            对于final修饰的变量，它在编译时被解析为常量值的一个本地拷贝存储到自己的常量池中。
         */
//        String a = "yorick";
//        final String b = "ick";
//        String c = "yor" + b;
//        System.out.println(a == c);

        // 6
        /*
            b的值在编译期无法确定，只有在程序运行期调用方法后，
            将方法的返回值和"yor"来动态连接并分配地址为b。
         */
//        String a = "yorick";
//        final String b = getB();
//        String c = "yor" + b;
//        System.out.println(a == c);

        // 7
        /*
            s1的"+"操作会变成:
             StringBuilder temp = new StringBuilder();
             temp.append(a).append(b).append(c);
             String s = temp.toString(); // new String();

         */
//        String s = "a" + "b" + "c";
//        String a = "a";
//        String b = "b";
//        String c = "c";
//        String res = a + b + c;
//        System.out.println(s == res);

        // 8
        /*
            
         */
        String s1 = new String("he") + new String("llo");
        String s2 = s1.intern();
        System.out.println(s1 == s2);

        // 9
        /*
            由于在建立引用关系时，"hello"字符串已保存至字符串常量池，
            故调用s1.intern()方法返回常量池中字符串"hello"的引用，而s1指向堆中的字符串，故不同。
         */
//        String s1= new String("hello");
//        String s2=s1.intern();
//        System.out.println(s1 == s2);
    }

    private static String getB() {
        return "ick";
    }
}
