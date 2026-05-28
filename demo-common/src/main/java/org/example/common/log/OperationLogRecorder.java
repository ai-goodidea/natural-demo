package org.example.common.log;

/**
 * 业务服务自行实现并注入 Spring 容器。LogOperationAspect 会调用它落库。
 */
public interface OperationLogRecorder {

    void record(OperationLogEntry entry);
}
