package org.example.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.dto.PageResult;
import org.example.common.entity.PageQuery;
import org.example.common.exception.BusinessException;
import org.example.common.result.ResultCode;
import org.example.user.dto.UserConverter;
import org.example.user.dto.UserCreateRequest;
import org.example.user.dto.UserUpdateRequest;
import org.example.user.dto.UserVO;
import org.example.user.entity.User;
import org.example.user.mapper.UserMapper;
import org.example.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public UserVO getVoById(Long id) {
        User entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return UserConverter.toVO(entity);
    }

    @Override
    public PageResult<UserVO> pageVo(PageQuery query) {
        LambdaQueryWrapper<User> q = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            String kw = query.getKeyword();
            q.like(User::getUsername, kw)
                    .or().like(User::getNickname, kw)
                    .or().like(User::getEmail, kw);
        }
        q.orderByDesc(User::getCreateTime);
        Page<User> page = page(new Page<>(query.getCurrent(), query.getSize()), q);
        return PageResult.from(page, UserConverter::toVO);
    }

    @Override
    public UserVO create(UserCreateRequest request) {
        User entity = UserConverter.toEntity(request);
        save(entity);
        return UserConverter.toVO(entity);
    }

    @Override
    public UserVO update(UserUpdateRequest request) {
        User entity = getById(request.getId());
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        UserConverter.applyUpdate(request, entity);
        updateById(entity);
        return UserConverter.toVO(entity);
    }
}
