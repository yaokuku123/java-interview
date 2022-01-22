package com.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YqjApplicationContext {
    private Class appConfig;
    private Map<String,BeanDefinition> beanDefinitionMap = new HashMap<>(); //beanDefinition容器
    private Map<String,Object> singletonObjects = new HashMap<>(); //单例池
    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>(); //后置处理器列表


    public YqjApplicationContext(Class appConfig) {
        //配置类
        this.appConfig = appConfig;
        //扫描配置类
        scan(appConfig);
        //加载单例bean
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            //单例bean
            if ("singleton".equals(beanDefinition.getScope())) {
                Object bean = createBean(beanName,beanDefinition);
                singletonObjects.put(beanName,bean);
            }
        }
    }

    private Object createBean(String beanName,BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        Object instance = null;
        try {
            //通过构造器创建bean实例
            instance = clazz.getConstructor().newInstance();
            //依赖注入
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 找到需要依赖注入的属性
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    field.set(instance,getBean(field.getName()));
                }
            }
            //aware回掉
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware)instance).setBeanName(beanName);
            }

            //后置处理器
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance,beanName);
            }
            //初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean)instance).afterPropertiesSet();
            }
            //后置处理器
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            }

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public Object getBean(String beanName) {
        //找到beanDefinition
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new RuntimeException("no such bean in spring contain");
        }
        String scope = beanDefinition.getScope();
        //单例bean从单例池中获取
        if ("singleton".equals(scope)) {
            Object instance = singletonObjects.get(beanName);
            //单例池中未找到bean
            if (instance == null) {
                instance = createBean(beanName,beanDefinition);
                singletonObjects.put(beanName,instance);
            }
            return instance;
        } else { //原型bean直接创建
            return createBean(beanName,beanDefinition);
        }
    }

    private void scan(Class appConfig) {
        //查看是否存在ComponentScan注解
        if (appConfig.isAnnotationPresent(ComponentScan.class)) {
            //获取ComponentScan注解中规定的扫描路径
            ComponentScan componentScanAnnotation = (ComponentScan) appConfig.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();
            //转换为文件绝对路径
            path = path.replace(".","/");
            URL resource = appConfig.getClassLoader().getResource(path);
            File resourceFile = new File(resource.getPath());
            //获取路径下的全部类字节码文件
            for (File file : resourceFile.listFiles()) {
                try {
                    //类加载
                    String classPath = file.getAbsolutePath();
                    String className = classPath.substring(classPath.indexOf("com"),classPath.indexOf(".class"));
                    className = className.replace("/",".");
                    Class clazz = appConfig.getClassLoader().loadClass(className);
                    //判断加载的类是否存在Component注解
                    if (clazz.isAnnotationPresent(Component.class)) {
                        Component componentAnnotation = (Component) clazz.getAnnotation(Component.class);
                        //添加后置处理器
                        if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                            BeanPostProcessor instance = (BeanPostProcessor) clazz.getConstructor().newInstance();
                            beanPostProcessorList.add(instance);
                        }
                        //获取beanName
                        String beanName = componentAnnotation.value();
                        //如果注解中未规定beanName，则从类名生成
                        if ("".equals(beanName)) {
                            beanName = Introspector.decapitalize(clazz.getSimpleName());
                        }
                        //生成beanDefinition
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setClazz(clazz);
                        //判断是否存在Scope注解
                        if (clazz.isAnnotationPresent(Scope.class)) {
                            Scope scopeAnnotation = (Scope) clazz.getAnnotation(Scope.class);
                            beanDefinition.setScope(scopeAnnotation.value());
                        } else {
                            beanDefinition.setScope("singleton");
                        }
                        //将beanDefinition添加到容器
                        beanDefinitionMap.put(beanName,beanDefinition);
                    }
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
