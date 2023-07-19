package io.github.lunasaw.dubbolog.dubbo.consumer;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import io.github.lunasaw.dubbolog.dubbo.api.DemoService;

@Component
public class Consumer implements CommandLineRunner {

    @DubboReference(version = "1.0.0")
    private DemoService demoService;

    @Override
    public void run(String... args) throws Exception {

        String result = demoService.sayHello("world");
        System.out.println("Receive result ======> " + result);
    }
}
