package org.example.common.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加在 Controller / Service 方法上，由 {@link LogOperationAspect} 异步记录操作日志。
 * 业务服务需要自行注入实现 {@link OperationLogRecorder} 来落库。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {

    /** 操作类型，如 CREATE / UPDATE / DELETE / STATUS_CHANGE / AI_SUMMARY */
    String type();

    /** 目标对象类型，如 ACCEPTANCE / ITEM */
    String targetType() default "";

    /** 描述模板，可写普通文案 */
    String value() default "";
}
