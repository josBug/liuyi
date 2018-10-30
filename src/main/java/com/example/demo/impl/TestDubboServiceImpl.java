package com.example.demo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.example.demo.service.TestDubboService;
import org.springframework.stereotype.Component;

@Service(
        version = "${demo.service.version}",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}",
        interfaceClass = TestDubboService.class
)
@Component
public class TestDubboServiceImpl implements TestDubboService{
    @Override
    public String hello() {
        return "hello";
    }
}
