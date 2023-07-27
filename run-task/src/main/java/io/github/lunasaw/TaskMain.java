package io.github.lunasaw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author weidian
 * @date 2023/7/27
 */
@SpringBootApplication
@MapperScan("io.github.lunasaw.mapper")
public class TaskMain {

    public static void main(String[] args) {
        SpringApplication.run(TaskMain.class, args);
    }
}