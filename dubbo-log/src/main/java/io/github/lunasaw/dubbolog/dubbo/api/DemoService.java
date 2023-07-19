package io.github.lunasaw.dubbolog.dubbo.api;

import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0.0")
public interface DemoService {

    String sayHello(String name);

}
