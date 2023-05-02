package io.github.lunasaw.log.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.logging.LogLevel;

/**
 * @author luna
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperatorLog {
    private String success;
    private String operator;
    private String fail;
    private String bizNo;
    private String category;
    private Boolean condition;

    private LogLevel level;
}