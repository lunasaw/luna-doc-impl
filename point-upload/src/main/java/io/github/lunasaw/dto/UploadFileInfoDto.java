package io.github.lunasaw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UploadFileInfoDto {


    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 当前分片
     */
    private Integer currentChunk;

    /**
     * 总分片
     */
    private Integer chunks;


}
