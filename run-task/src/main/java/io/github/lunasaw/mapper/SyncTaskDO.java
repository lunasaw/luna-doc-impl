package io.github.lunasaw.mapper;

import java.util.Date;

import lombok.Data;

@Data
public class SyncTaskDO {
    /**
     * 任务ID
     */
    private Long    id;

    private Date    gmtCreate;

    private Date    gmtUpdate;

    /**
     * 任务类型
     */
    private Integer type;

    /**
     * 资源key
     */
    private String  resourceKey;

    /**
     * 资源版本
     */
    private Integer resourceVersion;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 失败次数
     */
    private Integer failCnt;

    /**
     * 扩展字段
     */
    private String  extend;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 资源时间
     */
    private Date    resourceTime;

    /**
     * 环境
     */
    private String  env;

}