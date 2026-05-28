package org.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.dto.PageResult;
import org.example.common.entity.PageQuery;
import org.example.user.dto.UserCreateRequest;
import org.example.user.dto.UserUpdateRequest;
import org.example.user.dto.UserVO;
import org.example.user.entity.User;

public interface UserService extends IService<User> {

    UserVO getVoById(Long id);

    PageResult<UserVO> pageVo(PageQuery query);

    UserVO create(UserCreateRequest request);

    UserVO update(UserUpdateRequest request);
}
