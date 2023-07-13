package io.github.lunasaw;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import io.github.lunasaw.leaderboard.service.LeaderBoardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;

import com.google.common.collect.ImmutableMap;

import reactor.core.publisher.Mono;

@Slf4j
@SpringBootApplication
public class RedisDemoApplication implements ApplicationRunner {

    @Autowired
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    @Autowired
    private LeaderBoardService          leaderBoardService;

    public static void main(String[] args) {
        SpringApplication.run(RedisDemoApplication.class, args);
    }

    @Bean
    public ReactiveStringRedisTemplate reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ReactiveHashOperations<String, String, String> hashOps = reactiveStringRedisTemplate.opsForHash();
        Mono mono1 = hashOps.put("apple", "x", "6000");
        mono1.subscribe(System.out::println);
        Mono mono2 = hashOps.put("apple", "xr", "5000");
        mono2.subscribe(System.out::println);
        Mono mono3 = hashOps.put("apple", "xs max", "8000");
        mono3.subscribe(System.out::println);

        avalanche();

        loadData();
    }

    public void loadData() {
        leaderBoardService.updateScore("user1", 200);
        leaderBoardService.updateScore("user2", 100);
        leaderBoardService.updateScore("user3", 300);
        log.info(leaderBoardService.getLeaderboard(0, 2));
    }

    public void puncture() {
        // 前置校验
        Mono<Object> applePro = reactiveStringRedisTemplate.opsForHash().get("apple", "pro");
        applePro.subscribe(obj -> {
            if (obj != null) {
                System.out.println(obj);
            }
            // DB select apple pro
        });

    }

    public void avalanche() {
        ReactiveValueOperations<String, String> opsForValue = reactiveStringRedisTemplate.opsForValue();
        opsForValue.multiSet(ImmutableMap.of("1", "2"));
        Mono<Boolean> apple = opsForValue.setIfAbsent("orange", "x", Duration.of(200 + RandomUtils.nextInt(1, 200), ChronoUnit.SECONDS));
        apple.subscribe(System.out::println);
        // 防止雪崩 需要加随机处理
    }
}