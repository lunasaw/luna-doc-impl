package io.github.lunasaw.counter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/counter")
public class CounterController {

    private static final String COUNTER_KEY = "my_counter";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/increment")
    public long increment() {
        log.info("Incrementing counter");
        try {
            Long incrementedValue = redisTemplate.opsForValue().increment(COUNTER_KEY);
            log.info("Incremented value: {}", incrementedValue);
            return incrementedValue;
        } catch (Exception e) {
            log.error("Error incrementing counter", e);
        }
        return 0;
    }

    @PostMapping("/decrement")
    public long decrement() {
        log.info("Decrementing counter");
        try {
            Long decrementedValue = redisTemplate.opsForValue().decrement(COUNTER_KEY);
            log.info("Decremented value: {}", decrementedValue);
            return decrementedValue;
        } catch (Exception e) {
            log.error("Error decrementing counter", e);
        }
        return 0;
    }

    @PostMapping("/reset")
    public String reset() {
        log.info("Resetting counter");
        try {
            String resetValue = redisTemplate.opsForValue().getAndSet(COUNTER_KEY, "0");
            log.info("Reset value: {}", resetValue);
            return resetValue;
        } catch (Exception e) {
            log.error("Error resetting counter", e);
        }
        return "0";
    }

}
