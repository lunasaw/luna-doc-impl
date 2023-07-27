package io.github.lunasaw;

/**
 * 同步任务状态
 */
public interface SyncTaskStatus {
    /**
     * 未开始
     */
    int UNDO       = 0;
    /**
     * 处理中
     */
    int PROCESSING = 1;
    /**
     * 处理成功
     */
    int SUCCESS    = 2;
    /**
     * 处理失败
     */
    int FAILED     = 3;
}