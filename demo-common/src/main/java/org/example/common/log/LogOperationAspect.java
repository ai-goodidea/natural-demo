package org.example.common.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.common.auth.CurrentUser;
import org.example.common.auth.UserContextHolder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * 切面：捕获 @LogOperation 注解，方法成功执行后异步写日志。
 * 不依赖具体落库实现 —— 通过 OperationLogRecorder 接口转交给业务服务。
 * 业务服务里没有 OperationLogRecorder 时，本切面只打 log，不影响主业务。
 */
@Slf4j
@Aspect
@Component
public class LogOperationAspect {

    private final ObjectProvider<OperationLogRecorder> recorderProvider;

    public LogOperationAspect(ObjectProvider<OperationLogRecorder> recorderProvider) {
        this.recorderProvider = recorderProvider;
    }

    @Around("@annotation(org.example.common.log.LogOperation)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object ret;
        try {
            ret = pjp.proceed();
        } catch (Throwable ex) {
            // 失败不记录，避免脏数据
            throw ex;
        }

        try {
            MethodSignature sig = (MethodSignature) pjp.getSignature();
            Method method = sig.getMethod();
            LogOperation ann = method.getAnnotation(LogOperation.class);
            CurrentUser u = UserContextHolder.get();

            // 尝试从方法第一个参数推断 targetId，简单兜底，业务可自行写更精细的日志
            String targetId = "";
            Object[] args = pjp.getArgs();
            if (args != null && args.length > 0 && args[0] != null) {
                Object first = args[0];
                if (first instanceof Long || first instanceof String || first instanceof Integer) {
                    targetId = String.valueOf(first);
                }
            }

            OperationLogEntry entry = OperationLogEntry.builder()
                    .userId(u.getUserId())
                    .userName(u.getUsername())
                    .opTime(LocalDateTime.now())
                    .opType(ann.type())
                    .targetType(ann.targetType())
                    .targetId(targetId)
                    .summary(ann.value())
                    .build();

            OperationLogRecorder recorder = recorderProvider.getIfAvailable();
            if (recorder != null) {
                recorder.record(entry);
            } else {
                log.info("[OperationLog] {} args={}", entry, Arrays.toString(Objects.requireNonNullElse(args, new Object[0])));
            }
        } catch (Exception e) {
            log.warn("write operation log failed", e);
        }

        return ret;
    }
}
