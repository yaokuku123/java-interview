# JVM面试总结

1. 如何分析系统的订单生成情况，根据实际场景设置JVM参数？

说明：通过对订单系统分析，梳理JVM参数优化方案的思考方向。尽可能让对象都在新生代里分配和回收，尽量别让太多对象频繁进入老年代，避免频繁对老年代进行垃圾回收，同时给系统充足的内存大小，避免新生代频繁的进行垃圾回收。

![image-20220102211252094](../../img/image-20220102211252094.png)

