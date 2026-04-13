package com.kbs.api.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kbs.api.service.AppUserService;
import com.kbs.common.exception.BusinessException;
import com.kbs.core.entity.AppUser;
import com.kbs.core.mapper.AppUserMapper;
import com.kbs.core.vo.UserPageRequest;
import com.kbs.core.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 * 实现 Spring Security 的 UserDetailsService 接口，用于认证
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser> implements AppUserService, UserDetailsService {

    /**
     * Spring Security 认证方法
     * 根据用户名加载用户详情，用于登录认证
     *
     * @param username 用户名
     * @return 用户详情
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUser appUser = getByUsername(username);

        if (appUser == null || appUser.getStatus() == 1 || appUser.getDelFlag() == 1) {
            return null;
        }

        // 构建 Spring Security UserDetails
        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .roles(appUser.getAccountType())
                .disabled(appUser.getStatus() != null && appUser.getStatus() == 1)
                .build();
    }

    @Override
    public AppUser getByUsername(String username) {
        return new LambdaQueryChainWrapper<>(this.getBaseMapper())
                .eq(AppUser::getUsername, username)
                .one();
    }

    @Override
    public IPage<UserVO> getUserPage(UserPageRequest request) {
        // 使用原生 mybatis 分页查询
        Page<UserVO> page = new Page<>(request.getPageNo(), request.getPageSize());
        return this.baseMapper.selectUserPage(page, request);
    }

    @Override
    public void updateUserStatus(String id, Integer status) {
        AppUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        this.updateById(user);
    }
}
