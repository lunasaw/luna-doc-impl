package io.github.lunasaw.log.service.impl;

import io.github.lunasaw.log.service.OperatorGetService;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * @author luna
 * @date 2023/5/2
 */
@Service
public class DefaultOperatorGetServiceImpl implements OperatorGetService {
    @Override
    public String getUser() {
        Properties sysProperty = System.getProperties();
        return sysProperty.getProperty("user.name");
    }
}
