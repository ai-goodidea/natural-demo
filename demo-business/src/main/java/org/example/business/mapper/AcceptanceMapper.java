package org.example.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.common.model.acceptance.entity.Acceptance;

@Mapper
public interface AcceptanceMapper extends BaseMapper<Acceptance> {
}
