package org.example.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.business.acceptance.entity.OperationLog;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
