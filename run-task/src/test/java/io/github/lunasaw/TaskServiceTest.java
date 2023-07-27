package io.github.lunasaw;

import io.github.lunasaw.service.SyncTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author weidian
 * @date 2023/7/27
 */
public class TaskServiceTest extends BaseTest {

    @Autowired
    private SyncTaskService syncTaskService;

    @Test
    public void atest() {
        syncTaskService.saveTask(1, "2");
    }
}
