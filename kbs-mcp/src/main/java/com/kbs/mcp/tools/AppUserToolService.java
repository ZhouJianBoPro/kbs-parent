package com.kbs.mcp.tools;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.kbs.core.entity.AppUser;
import com.kbs.core.mapper.AppUserMapper;
import com.kbs.mcp.vo.AppUserVO;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Desc:
 * @Author: zhoujianbo
 * @Version: 1.0
 * @Date: 2026/4/10 11:31
 **/
@Component
public class AppUserToolService {

    @Resource
    private AppUserMapper appUserMapper;

    @Tool(description = "获取用户信息/查询用户信息/用户信息")
    public AppUserVO getUserInfo(@ToolParam(description = "用户名") String username) {

        AppUser appUser = new LambdaQueryChainWrapper<>(appUserMapper)
                .eq(AppUser::getUsername, username)
                .eq(AppUser::getDelFlag, 0)
                .one();

        if(Objects.isNull(appUser)) {
            return new AppUserVO();
        }

        AppUserVO appUserVO = new AppUserVO();
        BeanUtils.copyProperties(appUser, appUserVO);

        return appUserVO;
    }

    @Tool(description = "删除用户")
    public String deleteUserInfo(@ToolParam(description = "用户名") String username) {

        AppUser appUser = new LambdaQueryChainWrapper<>(appUserMapper)
                .eq(AppUser::getUsername, username)
                .eq(AppUser::getDelFlag, 0)
                .one();

        if(Objects.isNull(appUser)) {
            return "删除失败，系统中不存在该用户";
        }

        appUser.setDelFlag(1);
        appUserMapper.updateById(appUser);

        return "删除成功";
    }
}
