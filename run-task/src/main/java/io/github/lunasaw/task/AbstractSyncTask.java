package io.github.lunasaw.task;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.collect.Lists;
import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.SyncTaskStatus;
import io.github.lunasaw.mapper.SyncTaskDO;
import io.github.lunasaw.service.SyncTaskService;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "dataSync")
public abstract class AbstractSyncTask implements InitializingBean {

    @Value("${app.notify.userList}")
    private List<String>                                 notifyList;
    @Autowired
    protected SyncTaskService                            syncTaskService;
    @Autowired
    @Qualifier(value = "taskExecutor")
    protected ThreadPoolTaskExecutor                     taskExecutor;

    private static final Map<Integer, SyncTaskConfigDTO> CONFIG_MAP = new ConcurrentHashMap<>();

    /**
     * 根据任务类型获取配置
     * 
     * @param taskType
     * @return
     */
    public static SyncTaskConfigDTO getByType(Integer taskType) {
        return CONFIG_MAP.get(taskType);
    }

    /**
     * 获取任务类型
     *
     * @return
     */
    protected abstract int getTaskType();

    /**
     * 单个任务执行
     *
     * @param task
     */
    protected abstract SyncRespDTO runTask(SyncTaskDO task);

    /**
     * 获取任务运行配置
     *
     * @return
     */
    protected SyncTaskConfigDTO getConfig() {
        return getByType(getTaskType());
    }

    /**
     * 任务运行
     */
    protected void run() {
        SyncTaskConfigDTO configDTO = getConfig();
        try {
            log.info("{}-{} 开始运行", configDTO.getTaskType(), configDTO.getTaskName());
            long startTime = System.currentTimeMillis();
            Date fromTime = new Date(startTime - configDTO.getDelaySeconds() * 1000);
            List<SyncTaskDO> undoList =
                syncTaskService.selectToBeProcessed(getTaskType(), fromTime, configDTO.getMaxRetry(), configDTO.getQueryNum());
            if (CollectionUtils.isNotEmpty(undoList)) {
                List<List<SyncTaskDO>> partitionList = Lists.partition(undoList, configDTO.getBatchNum());
                for (List<SyncTaskDO> partition : partitionList) {
                    List<CompletableFuture<Void>> futureList = new ArrayList<>();
                    for (SyncTaskDO task : partition) {
                        // 延迟时间，便于监控
                        if (task.getStatus() == SyncTaskStatus.UNDO && task.getFailCnt() == 0) {
                            long now = System.currentTimeMillis();
                            delayNotify(configDTO, task, now - task.getResourceTime().getTime());
                        }
                        if (task.getStatus() == SyncTaskStatus.FAILED) {
                            // 有更新版本的任务时，结束该任务
                            List<SyncTaskDO> biggerVersionList =
                                syncTaskService.selectByMinVersion(task.getType(), task.getResourceKey(), task.getResourceVersion());
                            if (CollectionUtils.isNotEmpty(biggerVersionList)) {
                                task.setStatus(SyncTaskStatus.SUCCESS);
                                syncTaskService.update(task);
                                continue;
                            }
                        }
                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                            SyncRespDTO respDTO = null;
                            long taskStartTime = System.currentTimeMillis();
                            boolean needUpResult = true;
                            try {
                                task.setStatus(SyncTaskStatus.PROCESSING);
                                boolean updateRunningSuccess = syncTaskService.update(task);
                                // 单任务乐观锁获取失败
                                if (!updateRunningSuccess) {
                                    needUpResult = false;
                                    return;
                                }
                                respDTO = this.runTask(task);
                            } catch (Exception e) {
                                log.error("{}-{} 单任务执行异常，key:{}，id:{}", configDTO.getTaskType(), configDTO.getTaskName(), task.getResourceKey(),
                                    task.getId(), e);
                                respDTO = SyncRespDTO.buildFail();
                            } finally {
                                // 更新任务状态
                                if (needUpResult) {
                                    task.setVersion(task.getVersion() + 1);
                                    if (respDTO.getSkip()) {
                                        task.setStatus(SyncTaskStatus.FAILED);
                                    } else {
                                        if (!respDTO.getSuccess()) {
                                            task.setFailCnt(task.getFailCnt() + 1);
                                            if (task.getFailCnt() >= configDTO.getMaxRetry()) {
                                                failNotify(configDTO, task, RandomStrUtil.getUUID());
                                                log.error("{}-{} 任务重试次数耗尽，key:{}，id:{}", configDTO.getTaskType(), configDTO.getTaskName(),
                                                    task.getResourceKey(), task.getId());
                                            }
                                        }
                                        task.setStatus(respDTO.getSuccess() ? SyncTaskStatus.SUCCESS : SyncTaskStatus.FAILED);
                                    }
                                    syncTaskService.update(task);
                                }
                            }
                        }, taskExecutor);
                        futureList.add(future);
                    }
                    CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()])).join();
                }
            }
            long endTime = System.currentTimeMillis();
            log.info("{}-{} 运行完成，耗时{}ms", configDTO.getTaskType(), configDTO.getTaskName(), endTime - startTime);
        } catch (Throwable e) {
            log.error("{}-{} 运行异常", configDTO.getTaskType(), configDTO.getTaskName(), e);
        }
    }

    /**
     * 任务启动
     */
    protected void start() {
        while (true) {
            Long sleepTime = 10000L;
            try {
                SyncTaskConfigDTO configDTO = getConfig();
                sleepTime = configDTO.getSleepSeconds().longValue() * 1000L;
                if (configDTO.getTaskSwitch()) {
                    run();
                }
            } catch (Throwable e) {
                log.error("任务启动异常", e);
            }
            try {
                Thread.sleep(sleepTime);
            } catch (Throwable e) {
                log.error("任务休眠异常", e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        taskExecutor.execute(this::start);
    }

    private static final Map<Integer, Long> DELAY_NOTIFY_MAP        = new HashMap<>();
    /**
     * 每种任务5分钟内告警1次
     */
    private static final long               DELAY_NOTIFY_SLEEP_TIME = 5 * 60 * 1000L;

    private void delayNotify(SyncTaskConfigDTO configDTO, SyncTaskDO taskDO, long delayTime) {
        if (delayTime < configDTO.getDelayNotifyTime()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (DELAY_NOTIFY_MAP.containsKey(configDTO.getTaskType()) && now - DELAY_NOTIFY_MAP.get(configDTO.getTaskType()) < DELAY_NOTIFY_SLEEP_TIME) {
            return;
        }
        DELAY_NOTIFY_MAP.put(configDTO.getTaskType(), now);
        taskExecutor.execute(() -> {
            StringBuilder content = new StringBuilder(configDTO.getTaskName()).append("\n")
                .append("任务id：").append(taskDO.getId()).append("\n")
                .append("任务type：").append(configDTO.getTaskType()).append("\n")
                .append("任务key：").append(taskDO.getResourceKey()).append("\n")
                .append("延迟时间：").append(delayTime).append("ms");
            log.error("任务延迟告警", content, notifyList, null);
        });
    }

    private void failNotify(SyncTaskConfigDTO configDTO, SyncTaskDO taskDO, String traceId) {
        taskExecutor.execute(() -> {
            StringBuilder content = new StringBuilder(configDTO.getTaskName()).append("\n")
                .append("任务id：").append(taskDO.getId()).append("\n")
                .append("任务type：").append(configDTO.getTaskType()).append("\n")
                .append("任务key：").append(taskDO.getResourceKey());
            log.error("任务重试次数耗尽告警", content, notifyList, traceId);
        });
    }
}
