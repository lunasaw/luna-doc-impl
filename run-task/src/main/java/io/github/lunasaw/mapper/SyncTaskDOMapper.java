package io.github.lunasaw.mapper;

import java.util.List;

import io.github.lunasaw.SyncTaskQueryReqDTO;
import org.apache.ibatis.annotations.Param;

public interface SyncTaskDOMapper {

    List<SyncTaskDO> selectByMinVersion(@Param("type") Integer type, @Param("resourceKey") String resourceKey,
        @Param("resourceVersion") Integer resourceVersion, @Param("env") String env);

    List<SyncTaskDO> selectAlwaysRunning(@Param("fromTime") String fromTime);

    List<SyncTaskDO> selectToBeProcessed(@Param("type") Integer type, @Param("fromTime") String fromTime, @Param("maxRetry") Integer maxRetry,
        @Param("env") String env, @Param("limit") Integer limit);

    SyncTaskDO selectByMaxResourceVersion(@Param("type") Integer type, @Param("resourceKey") String resourceKey, @Param("env") String env);

    int countByStatus(@Param("status") Integer status);

    int insert(SyncTaskDO record);

    SyncTaskDO selectByPrimaryKey(Long id);

    int update(SyncTaskDO record);

    int count(SyncTaskQueryReqDTO reqDTO);

    List<SyncTaskDO> query(SyncTaskQueryReqDTO reqDTO);

    List<SyncTaskDO> queryByLastId(@Param("lastId") Long lastId, @Param("maxResourceTime") String maxResourceTime, @Param("limit") Integer limit);

    int delete(@Param("id") Long id, @Param("version") Integer version);
}