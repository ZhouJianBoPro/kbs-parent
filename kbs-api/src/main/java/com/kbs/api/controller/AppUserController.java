package com.kbs.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kbs.api.service.AppUserService;
import com.kbs.common.model.Result;
import com.kbs.core.vo.UserPageRequest;
import com.kbs.core.vo.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@Slf4j
@PreAuthorize("hasRole('SYSTEM')")
@RestController
@RequestMapping("/user")
public class AppUserController {

    @Resource
    private AppUserService appUserService;

    /**
     * 分页查询用户列表
     *
     * @param request 分页查询请求
     * @return 用户分页数据
     */
    @GetMapping("/page")
    public Result<IPage<UserVO>> getUserPage(UserPageRequest request) {
        IPage<UserVO> page = appUserService.getUserPage(request);
        return Result.success(page);
    }

    /**
     * 启用或禁用用户
     *
     * @param id     用户ID
     * @param status 状态（0-正常，1-禁用）
     * @return 操作结果
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateUserStatus(@PathVariable("id") String id, @RequestParam("status") Integer status) {
        appUserService.updateUserStatus(id, status);
        return Result.success();
    }
}
