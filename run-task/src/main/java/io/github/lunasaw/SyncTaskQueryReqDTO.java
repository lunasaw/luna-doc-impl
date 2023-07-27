package io.github.lunasaw;

import lombok.Data;

import java.io.Serializable;

@Data
public class SyncTaskQueryReqDTO implements Serializable {
    /**
     * 任务类型 {@link SyncTaskType}
     */
    private Integer type;
    /**
     * 任务key
     */
    private String  resourceKey;
    /**
     * 任务状态 {@link SyncTaskStatus}
     */
    private Integer status;
    /**
     * 0开始
     */
    private Integer offset;
    /**
     * 分页条数
     */
    private Integer limit;
}
