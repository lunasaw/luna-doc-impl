package io.github.lunasaw.task;

import com.alibaba.fastjson.JSON;

import lombok.Data;

@Data
public class SyncTaskConfigDTO {
    /**
     * 任务类型
     */
    private Integer taskType;
    /**
     * 任务名称
     */
    private String  taskName;
    /**
     * 任务开关
     */
    private Boolean taskSwitch;
    /**
     * 延迟时间（秒）
     * 即 取DB中几秒前的数据
     */
    private Integer delaySeconds;
    /**
     * 每次从DB中捞取任务条数
     */
    private Integer queryNum;
    /**
     * 最大重试次数
     */
    private Integer maxRetry;
    /**
     * 任务运行过程中并发条数
     */
    private Integer batchNum;
    /**
     * 任务运行结束后休眠时间（秒）
     */
    private Integer sleepSeconds;
    /**
     * 延迟告警时间
     */
    private Long    delayNotifyTime;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
