package com.example.demo.fragment;

import com.example.demo.controller.OperationController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class TestApplicationContext implements BeanPostProcessor{
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        if (o instanceof OperationController) {
            System.out.println("DemoServiceImpl init==========================");
        }
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }
}
