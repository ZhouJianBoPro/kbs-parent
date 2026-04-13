package com.kbs.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kbs.core.entity.AppUser;
import com.kbs.core.vo.UserPageRequest;
import com.kbs.core.vo.UserVO;

/**
 * 用户服务接口
 */
public interface AppUserService extends IService<AppUser> {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    AppUser getByUsername(String username);

    /**
     * 分页查询用户列表
     *
     * @param request 分页查询请求
     * @return 用户分页数据
     */
    IPage<UserVO> getUserPage(UserPageRequest request);

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 状态（0-正常，1-禁用）
     */
    void updateUserStatus(String id, Integer status);
}
