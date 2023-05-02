package io.github.lunasaw.test;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import io.github.lunasaw.log.entity.OperatorLog;
import io.github.lunasaw.log.service.LogService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/5/2
 */
@Service
public class LocalLogServiceImpl implements LogService {

    public static final Logger log = LoggerFactory.getLogger("OPERATE");

    @Override
    public void printLog(OperatorLog operatorLog) {
        if (operatorLog == null) {
            return;
        }
        Boolean condition = Optional.ofNullable(operatorLog.getCondition()).orElse(false);
        if (!condition) {
            return;
        }
        LogLevel logLevel = Optional.ofNullable(operatorLog.getLevel()).orElse(LogLevel.INFO);
        if (LogLevel.INFO == logLevel) {
            log.info("bizNo:{},operator:{},category:{},success:{}",
                operatorLog.getBizNo(), operatorLog.getOperator(),
                operatorLog.getCategory(), operatorLog.getSuccess());
        } else if (LogLevel.WARN == logLevel) {
            log.warn("bizNo:{},operator:{}, category:{},success:{}",
                operatorLog.getBizNo(), operatorLog.getOperator(),
                operatorLog.getCategory(), operatorLog.getSuccess());
        } else if (LogLevel.ERROR == logLevel) {
            log.error("bizNo:{},operator:{},category:{},fail:{}",
                operatorLog.getOperator(), operatorLog.getBizNo(),
                operatorLog.getCategory(), operatorLog.getFail());
        }
    }
}
