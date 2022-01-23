# Spring工程应用测试文档

## 1 核心方法测试

### 1.1 BeanDefinition

说明：BeanDefinition表示Bean定义，BeanDefinition中存在很多属性用来描述一个Bean的特点。编程式定义Bean，达到和声明式定义Bean相同的效果，最终都会被Spring解析为对应的BeanDefinition对象，并放入Spring容器中。

```java
public class TestSpringDemo {

    public static void main(String[] args) {
      //生成bean定义对象，设置beanClass，并注册到context容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition();
        beanDefinition.setBeanClass(OrderService.class);
        context.registerBeanDefinition("orderService",beanDefinition);
        OrderService orderService = (OrderService) context.getBean("orderService");
        System.out.println(orderService);
    }
}
```

### 1.2 BeanDefinitionReader

说明：BeanDefinition读取器。可以直接把某个类转换为BeanDefinition，并且会解析该类上的注解。

* AnnotatedBeanDefinitionReader，XmlBeanDefinitionReader

```java
public class TestSpringDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        AnnotatedBeanDefinitionReader annotatedBeanDefinitionReader = new AnnotatedBeanDefinitionReader(context);
        annotatedBeanDefinitionReader.register(OrderService.class);
        System.out.println(context.getBean("orderService"));
    }
}
```

### 1.3 ClassPathBeanDefinitionScanner

说明：扫描器，但是它的作用和BeanDefinitionReader类似，它可以 进行扫描，扫描某个包路径，对扫描到的类进行解析。扫描到的类上**如果存在@Component 注解**，那么就会把这个类解析为一个BeanDefinition。

```java
public class TestSpringDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.refresh();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
        scanner.scan("com.yqj.spring.service");
        System.out.println(context.getBean("orderService"));
    }
}
```

### 1.4 BeanFactory

说明：

1. BeanFactory表示Bean工厂，BeanFactory会负责创建Bean，并且提供获取Bean的 API。

2. ApplicationContext是BeanFactory的一种，拥有BeanFactory支持的所有功能，不过ApplicationContext比BeanFactory更加强大。比如MessageSource表示国际化， ApplicationEventPublisher表示事件发布，EnvironmentCapable表示获取环境变量。

3. 在Spring的源码实现中，当我们new一个ApplicationContext时，其底层会new一个BeanFactory出 来，当使用ApplicationContext的某些方法时，比如getBean()，底层调用的是BeanFactory的 getBean()方法。

```java
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
		MessageSource, ApplicationEventPublisher, ResourcePatternResolver{}
```

* 使用DefaultListableBeanFactory作为容器，生产Bean

```java
public class TestSpringDemo {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition().getBeanDefinition();
        beanDefinition.setBeanClass(OrderService.class);
        beanFactory.registerBeanDefinition("orderService",beanDefinition);
        System.out.println(beanFactory.getBean("orderService"));
    }
}
```

### 1.5 ApplicationContext

说明：ApplicationContext是个接口，实际上也是一个BeanFactory，不过比BeanFactory 更加强大。

1. HierarchicalBeanFactory:拥有获取父BeanFactory的功能
2. ListableBeanFactory:拥有获取beanNames的功能
3. ResourcePatternResolver:资源加载器，可以一次性获取多个资源(文件资源等等)
4. EnvironmentCapable:可以获取运行时环境(没有设置运行时环境功能)
5. ApplicationEventPublisher:拥有广播事件的功能(没有添加事件监听器的功能)
6. MessageSource:拥有国际化功能

两个重要实现类：AnnotationConfigApplicationContext，ClassPathXmlApplicationContext

* 资源加载功能

说明：直接利用ApplicationContext获取某个文件的内容。

```java
public class TestSpringDemo {

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        //本地文件资源
        Resource resource1 = context.getResource("file:///Users/yorick/Documents/study/blog/interview/code/test-tmp/src/main/java/com/yqj/spring/service/OrderService.java");
        System.out.println(resource1.contentLength());
        //网络资源
        Resource resource2 = context.getResource("https://www.baidu.com");
        System.out.println(resource2.contentLength());
        //获取类路径下资源
        Resource resource3 = context.getResource("classpath:com/yqj/spring/config/AppConfig.class");
        System.out.println(resource3.contentLength());
    }
}
```

* 获取运行时环境

```java
public class TestSpringDemo {

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println(context.getEnvironment().getProperty("sun.jnu.encoding"));
        System.out.println(context.getEnvironment().getProperty("PATH"));
    }
}
```

* 事件广播

1. 先定义一个事件监听器

```java
 @Bean
public ApplicationListener applicationListener() {
  return new ApplicationListener() {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
      System.out.println("get message: " + event);
    }
  };
}
```

2. 发布事件

```java
public class TestSpringDemo {

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        context.publishEvent("hello world");
    }
}
```

### 1.6 OrderComparator

说明：OrderComparator是Spring所提供的一种比较器，可以用来根据@Order注解或实现Ordered接口 来执行值进行笔记，从而可以进行排序。

```java
public class TestSpringDemo {

    public static void main(String[] args) throws IOException {
        A a = new A();
        B b = new B();
        OrderComparator comparator = new OrderComparator();
        System.out.println(comparator.compare(a,b));
        List list = new ArrayList();
        list.add(a);
        list.add(b);
        list.sort(comparator);
        System.out.println(list);
    }
}

class A implements Ordered {

    @Override
    public int getOrder() {
        return 3;
    }
}

class B implements Ordered {

    @Override
    public int getOrder() {
        return 2;
    }
}
```

### 1.7 AnnotationAwareOrderComparator

说明：Spring中还提供了一个OrderComparator的子类: AnnotationAwareOrderComparator，它支持用@Order来指定order值。

```java
public class TestSpringDemo {

    public static void main(String[] args) throws IOException {
        A a = new A();
        B b = new B();
        AnnotationAwareOrderComparator comparator = new AnnotationAwareOrderComparator();
        System.out.println(comparator.compare(a,b));
        List list = new ArrayList();
        list.add(a);
        list.add(b);
        list.sort(comparator);
        System.out.println(list);
    }
}

@Order(3)
class A {
}

@Order(2)
class B {
}
```

### 1.8 BeanPostProcessor

说明：

1. BeanPostProcess表示Bean的后置处理器，我们可以定义一个或多个BeanPostProcessor，来干涉Spring创建Bean的过程。
2. 一个BeanPostProcessor可以在任意一个Bean的初始化之前以及初始化之后去额外的做一些用户自定义的逻辑。可以通过判断beanName来进行针对性处理(针对某个Bean或某部分 Bean)。

```java
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("before init: " + beanName );
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("after init: " + beanName );
        return bean;
    }
}
```

### 1.9 BeanFactoryPostProcessor

说明：BeanFactoryPostProcessor表示Bean工厂的后置处理器，其实和BeanPostProcessor类似， BeanPostProcessor是干涉Bean的创建过程，BeanFactoryPostProcessor是干涉BeanFactory的创建过程。

```java
@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("handle beanFactory");
    }
}
```

### 1.10 FactoryBean

说明：我们可以通过BeanPostPorcessor来干涉Spring创建Bean的过程，但是如果我们想一个 Bean完完全全由我们来创造，也是可以的。但是通过这种方式创造出来的UserService的Bean，只会经过初始化后，其他Spring的生命周期步骤是不会经过的，比如依赖注入。**与@Bean的区别：@Bean定 义的Bean是会经过完整的Bean生命周期的。**

```java
@Component
public class MyFactoryBean implements FactoryBean {
    @Override
    public Object getObject() throws Exception {
        return new OrderService();
    }

    @Override
    public Class<?> getObjectType() {
        return OrderService.class;
    }
}
```

```java
public class TestSpringDemo {

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
      //获取通过工程对象创建的实例
        OrderService orderService = (OrderService) context.getBean("myFactoryBean");
        System.out.println(orderService);
      //获取工厂对象本身
        MyFactoryBean myFactoryBean = (MyFactoryBean) context.getBean("&myFactoryBean");
        System.out.println(myFactoryBean);
    }
}
```

```java
// result
com.yqj.spring.service.OrderService@345965f2
com.yqj.spring.service.MyFactoryBean@429bd883
```

### 1.11 MetadataReader、ClassMetadata、AnnotationMetadata

说明：

1. 在Spring中需要去解析类的信息，比如类名、类中的方法、类上的注解，这些都可以称之为类的元数据，所以Spring中对类的元数据做了抽象，并提供了一些工具类。

2. 使用的是ASM技术。Spring启动的时候需要去扫描，如果指定的包路径比较宽泛，那么扫描的类是非常多的，如果在Spring启动时就把这些类全部加载进JVM，违背JVM设计原则，造成大量类的加载，所以使用了 ASM技术。

```java
public class TestSpringDemo {

    public static void main(String[] args) throws IOException {
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        // 构造一个MetadataReader
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader("com.yqj.spring.config.AppConfig");
        // 得到ClassMetadata，并读取类名
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        System.out.println(classMetadata.getClassName());
        // 得到AnnotationMetadata，并获取类上注解信息
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        for (String annotationType : annotationMetadata.getAnnotationTypes()) {
            System.out.println(annotationType);
        }
    }
}
```

```java
// result
com.yqj.spring.config.AppConfig
org.springframework.context.annotation.Configuration
org.springframework.context.annotation.ComponentScan
```

