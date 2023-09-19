package io.github.lunasaw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileInfoDto {

    private long fileSize;

    private String fileName;

}
