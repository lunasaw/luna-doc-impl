package io.github.lunasaw.dubbolog.dubbo.service;

import io.github.lunasaw.dubbolog.dubbo.api.DemoService;

import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0.0")
public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}