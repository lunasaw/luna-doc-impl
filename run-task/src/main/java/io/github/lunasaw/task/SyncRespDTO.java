package io.github.lunasaw.task;

import lombok.Data;

@Data
public class SyncRespDTO {
    /**
     * 是否成功
     */
    private Boolean success = false;
    /**
     * 是否跳过本次任务执行
     */
    private Boolean skip    = false;

    public static SyncRespDTO buildSuccess() {
        SyncRespDTO respDTO = new SyncRespDTO();
        respDTO.setSuccess(true);
        return respDTO;
    }

    public static SyncRespDTO buildFail() {
        SyncRespDTO respDTO = new SyncRespDTO();
        return respDTO;
    }

    public static SyncRespDTO buildSkip() {
        SyncRespDTO respDTO = new SyncRespDTO();
        respDTO.setSkip(true);
        return respDTO;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getSkip() {
        return skip;
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }
}
