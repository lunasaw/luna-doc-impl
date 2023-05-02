package io.github.lunasaw.log.service;

import io.github.lunasaw.log.entity.OperatorLog;

/**
 * @author luna
 * @date 2023/5/2
 */
public interface LogService {

    void printLog(OperatorLog operatorLog);
}
