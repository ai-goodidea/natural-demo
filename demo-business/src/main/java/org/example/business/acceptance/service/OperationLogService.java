package org.example.business.acceptance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.business.acceptance.dto.AcceptanceConverter;
import org.example.common.model.acceptance.vo.OperationLogVO;
import org.example.common.model.acceptance.entity.OperationLog;
import org.example.business.mapper.OperationLogMapper;
import org.example.common.auth.UserContextHolder;
import org.example.common.log.OperationLogEntry;
import org.example.common.log.OperationLogRecorder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationLogService implements OperationLogRecorder {

    private final OperationLogMapper operationLogMapper;

    /** 注解切面回调，异步落库不影响主业务 */
    @Async
    @Override
    public void record(OperationLogEntry entry) {
        OperationLog log = new OperationLog();
        log.setUserId(entry.getUserId());
        log.setUserName(entry.getUserName());
        log.setOpTime(entry.getOpTime() == null ? LocalDateTime.now() : entry.getOpTime());
        log.setOpType(entry.getOpType());
        log.setTargetType(entry.getTargetType());
        log.setTargetId(entry.getTargetId());
        log.setSummary(entry.getSummary());
        operationLogMapper.insert(log);
    }

    /** 业务里有 before/after 时直接调这个 */
    public void writeManually(String type, String targetType, String targetId, String summary,
                              String beforeJson, String afterJson) {
        OperationLog log = new OperationLog();
        log.setUserId(UserContextHolder.get().getUserId());
        log.setUserName(UserContextHolder.get().getUsername());
        log.setOpTime(LocalDateTime.now());
        log.setOpType(type);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setSummary(summary);
        log.setBeforeJson(beforeJson);
        log.setAfterJson(afterJson);
        operationLogMapper.insert(log);
    }

    public List<OperationLogVO> listByTarget(String targetType, String targetId) {
        return operationLogMapper.selectList(new LambdaQueryWrapper<OperationLog>()
                .eq(OperationLog::getTargetType, targetType)
                .eq(OperationLog::getTargetId, targetId)
                .orderByDesc(OperationLog::getOpTime))
                .stream().map(AcceptanceConverter::toOperationLogVO).toList();
    }
}
