package io.github.lunasaw;

import io.github.lunasaw.redisson.ReentrantLockRedisson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author weidian
 * @date 2023/7/14
 */
@Slf4j
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private ReentrantLockRedisson redisson;

    @Test
    public void a_test() {
        log.info("--------------start---------------");
        ReentrantLockRedisson reentrantLockDemo = new ReentrantLockRedisson();
        reentrantLockDemo.doSomething(1);
        log.info("执行完doSomething方法 是否还持有锁：{}", ReentrantLockRedisson.lock.isLocked());
        log.info("--------------end---------------");
    }

}
