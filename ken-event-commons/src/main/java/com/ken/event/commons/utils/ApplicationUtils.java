package com.ken.event.commons.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.function.Supplier;

/**
 * SpringIOC的工具类
 * 1、从IOC容器中获取指定类型的Bean对象
 * 2、将任意一个对象注册到IOC容器中（跟@Component @Bean注解的效果是完全一样的）
 *
 * author Ken
 * create_time 2022/9/17
 */
public class ApplicationUtils implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private static BeanDefinitionRegistry registry;

    //静态的IOC容器的引用
    private static ApplicationContext applicationContext;

    /**
     * 通过Bean的类型，从IOC容器中获取Bean对象
     * @param beanClas
     * 
     * @param <T>
     */
    public static <T> T getBean(Class<T> beanClas) {
       return applicationContext.getBean(beanClas);
    }

    /**
     * 手动注册Bean对象
     */
    public static void registerBean(String beanName, Object bean){
        //将自定义的对象，包装成BeanDefinition对象
        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.genericBeanDefinition(bean.getClass(), new Supplier() {
            @Override
            public Object get() {
                return bean;
            }
        });

        //注册Bean对象
        registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ApplicationUtils.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationUtils.applicationContext = applicationContext;
    }
}
