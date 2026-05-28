package org.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.dto.PageResult;
import org.example.common.entity.PageQuery;
import org.example.common.model.user.dto.UserCreateRequest;
import org.example.common.model.user.dto.UserUpdateRequest;
import org.example.common.model.user.vo.UserVO;
import org.example.common.model.user.entity.User;

public interface UserService extends IService<User> {

    UserVO getVoById(Long id);

    PageResult<UserVO> pageVo(PageQuery query);

    UserVO create(UserCreateRequest request);

    UserVO update(UserUpdateRequest request);
}
