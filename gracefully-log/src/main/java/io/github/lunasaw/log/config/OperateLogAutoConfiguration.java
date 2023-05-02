package io.github.lunasaw.log.config;

import io.github.lunasaw.log.anno.EnableAutoLogConfig;
import io.github.lunasaw.log.aspect.LogRecordAspect;
import io.github.lunasaw.log.service.LogService;
import io.github.lunasaw.log.service.OperatorGetService;
import io.github.lunasaw.log.service.impl.DefaultLogServiceImpl;
import io.github.lunasaw.log.service.impl.DefaultOperatorGetServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author luna
 * @date 2023/5/2
 */
@Slf4j
@Configuration
@ConditionalOnClass(EnableAutoLogConfig.class)
@ComponentScan("io.github.lunasaw.log")
public class OperateLogAutoConfiguration implements ImportAware {

    private AnnotationAttributes enableLogRecord;

    @Bean
    @ConditionalOnMissingBean(LogService.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public LogService logService() {
        return new DefaultLogServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(OperatorGetService.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public OperatorGetService operatorGetService() {
        return new DefaultOperatorGetServiceImpl();
    }


    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableLogRecord = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableAutoLogConfig.class.getName(), false));
        if (this.enableLogRecord == null) {
            log.info("@EnableAutoLogConfig is not present on importing class");
        }
    }
}
