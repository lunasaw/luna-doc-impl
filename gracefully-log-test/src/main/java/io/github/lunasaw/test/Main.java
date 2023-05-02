package io.github.lunasaw.test;

import io.github.lunasaw.log.LogApplication;
import io.github.lunasaw.log.anno.EnableAutoLogConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author luna
 * @date 2023/5/2
 */
@SpringBootApplication
@EnableAutoLogConfig
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(LogApplication.class, args);
    }
}