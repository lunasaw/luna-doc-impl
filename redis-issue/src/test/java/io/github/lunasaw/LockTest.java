package io.github.lunasaw;

import com.luna.common.text.RandomStrUtil;
import io.github.lunasaw.lock.luna.LockUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author weidian
 * @date 2023/7/27
 */
@Slf4j
public class LockTest extends BaseTest {

    @Autowired
    private LockUtil lockUtil;

    @Test
    public void atest() {
        String lockValue = RandomStrUtil.getUUID();
        boolean getLock = lockUtil.tryLock("lockKey", lockValue, 1, 3, 200L);
        if (!getLock) {
            return;
        }
        try {
            // do something
        } catch (Exception e) {
            log.error("get exception", e);
        } finally {
            boolean lockKey = lockUtil.releaseLock("lockKey", lockValue);
            System.out.println(lockKey);
        }
    }
}
