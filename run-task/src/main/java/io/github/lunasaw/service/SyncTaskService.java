package io.github.lunasaw.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.luna.common.date.DateUtils;

import io.github.lunasaw.SyncTaskQueryReqDTO;
import io.github.lunasaw.SyncTaskStatus;
import io.github.lunasaw.mapper.SyncTaskDO;
import io.github.lunasaw.mapper.SyncTaskDOMapper;

@Service
public class SyncTaskService {
    @Autowired
    private SyncTaskDOMapper syncTaskDOMapper;
    @Value("${app.env}")
    private String           env;

    /**
     * 保存新任务
     * 
     * @param type
     * @param resourceKey
     * @return
     */
    public boolean saveTask(Integer type, String resourceKey) {
        // 取最大resourceVersion任务
        SyncTaskDO exist = selectByMaxResourceVersion(type, resourceKey);
        // 任务不存在时，插入新任务
        if (exist == null) {
            insert(type, resourceKey, 0);
            return true;
        }
        // 任务在处理中，resourceVersion+1并插入新记录，
        if (SyncTaskStatus.PROCESSING == exist.getStatus()) {
            insert(type, resourceKey, exist.getResourceVersion() + 1);
            return true;
        }
        // 任务在其它状态，重置任务
        exist.setStatus(SyncTaskStatus.UNDO);
        exist.setFailCnt(0);
        exist.setResourceTime(new Date());
        int updateRows = syncTaskDOMapper.update(exist);
        return updateRows > 0;
    }

    public SyncTaskDO selectById(Long id) {
        return syncTaskDOMapper.selectByPrimaryKey(id);
    }

    public SyncTaskDO selectByMaxResourceVersion(Integer type, String resourceKey) {
        return syncTaskDOMapper.selectByMaxResourceVersion(type, resourceKey, env);
    }

    public List<SyncTaskDO> selectAlwaysRunning(Date fromTime) {
        String fromTimeStr = DateUtils.formatDate(fromTime);
        return syncTaskDOMapper.selectAlwaysRunning(fromTimeStr);
    }

    public void insert(Integer type, String resourceKey, Integer resourceVersion) {
        SyncTaskDO syncTaskDO = new SyncTaskDO();
        syncTaskDO.setType(type);
        syncTaskDO.setResourceKey(resourceKey);
        syncTaskDO.setResourceVersion(resourceVersion);
        insert(syncTaskDO);
    }

    public void insert(SyncTaskDO syncTaskDO) {
        syncTaskDO.setEnv(env);
        syncTaskDOMapper.insert(syncTaskDO);
    }

    public boolean update(SyncTaskDO syncTaskDO) {
        int rows = syncTaskDOMapper.update(syncTaskDO);
        return rows > 0;
    }

    public List<SyncTaskDO> selectToBeProcessed(Integer type, Date fromTime, Integer maxRetry, Integer limit) {
        String fromTimeStr = DateUtils.formatDate(fromTime);
        return syncTaskDOMapper.selectToBeProcessed(type, fromTimeStr, maxRetry, env, limit);
    }

    public List<SyncTaskDO> selectByMinVersion(Integer type, String resourceKey, Integer resourceVersion) {
        return syncTaskDOMapper.selectByMinVersion(type, resourceKey, resourceVersion, env);
    }

    public int countByStatus(int stats) {
        return syncTaskDOMapper.countByStatus(stats);
    }

    public int count(SyncTaskQueryReqDTO reqDTO) {
        return syncTaskDOMapper.count(reqDTO);
    }

    public List<SyncTaskDO> query(SyncTaskQueryReqDTO reqDTO) {
        return syncTaskDOMapper.query(reqDTO);
    }

    public List<SyncTaskDO> queryByLastId(Long lastId, Date maxResourceTime, Integer limit) {
        String fromTimeStr = DateUtils.formatDate(maxResourceTime);
        return syncTaskDOMapper.queryByLastId(lastId, fromTimeStr, limit);
    }

    public int delete(Long id, Integer version) {
        return syncTaskDOMapper.delete(id, version);
    }

}
