package io.github.lunasaw.dubbolog;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class DubboLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboLogApplication.class, args);
    }

}
