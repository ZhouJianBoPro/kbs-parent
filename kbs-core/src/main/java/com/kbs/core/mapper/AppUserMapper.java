package com.kbs.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kbs.core.entity.AppUser;
import com.kbs.core.vo.UserPageRequest;
import com.kbs.core.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 应用用户 Mapper 接口
 */
@Mapper
public interface AppUserMapper extends BaseMapper<AppUser> {

    /**
     * 分页查询用户列表
     *
     * @param page    分页参数
     * @param request 查询条件
     * @return 用户分页数据
     */
    IPage<UserVO> selectUserPage(Page<UserVO> page, @Param("request") UserPageRequest request);
}
