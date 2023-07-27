package io.github.lunasaw.mapper;

import java.util.Date;

import lombok.Data;

@Data
public class SyncTaskDO {
    private Long    id;

    private Date    gmtCreate;

    private Date    gmtUpdate;

    private Integer type;

    private String  resourceKey;

    private Integer resourceVersion;

    private Integer status;

    private Integer failCnt;

    private String  extend;

    private Integer version;

    private Date    resourceTime;

    private String  env;

}