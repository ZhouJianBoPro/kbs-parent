package com.kbs.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kbs.api.service.AppUserService;
import com.kbs.api.service.ChatService;
import com.kbs.aspect.annotation.LimitRequest;
import com.kbs.common.entity.BasePage;
import com.kbs.common.exception.BusinessException;
import com.kbs.common.model.Result;
import com.kbs.core.entity.AppUser;
import com.kbs.core.vo.ChatMessageVO;
import com.kbs.core.vo.ChatRequest;
import com.kbs.core.vo.ConversationVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 对话控制器
 */
@Slf4j
@PreAuthorize("hasRole('NORMAL')")
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    @Resource
    private AppUserService appUserService;

    /**
     * 流式对话（创建会话或继续会话）
     */
    @LimitRequest
    @PostMapping(value = "/chat")
    public Result<?> chat(@Valid @RequestBody ChatRequest request,
                               Authentication authentication) {
        AppUser user = appUserService.getByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        chatService.chat(request, user.getId());
        return Result.success();
    }

    @LimitRequest
    @GetMapping(value = "/createNewConversation")
    public Result<ConversationVO> createNewConversation(Authentication authentication) {

        AppUser user = appUserService.getByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return Result.success(chatService.createNewConversation(user.getId()));
    }

    /**
     * 获取会话列表
     */
    @PostMapping("/getConversationList")
    public Result<IPage<ConversationVO>> getConversationList(@RequestBody BasePage basePage,
                                                            Authentication authentication) {
        AppUser user = appUserService.getByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        IPage<ConversationVO> pageResult = chatService.getConversationList(basePage, user.getId());
        return Result.success(pageResult);
    }

    /**
     * 获取会话详情
     */
    @GetMapping("/getConversationDetail")
    public Result<List<ChatMessageVO>> getConversationDetail(@RequestParam("id") String conversationId,
                                                             Authentication authentication) {
        AppUser user = appUserService.getByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        List<ChatMessageVO> list = chatService.getConversationDetail(conversationId);
        return Result.success(list);
    }

    /**
     * 删除会话
     */
    @LimitRequest
    @GetMapping("/deleteConversation")
    public Result<String> deleteConversation(@RequestParam("id") String conversationId,
                                              Authentication authentication) {
        AppUser user = appUserService.getByUsername(authentication.getName());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        chatService.deleteConversation(conversationId, user.getId());
        return Result.success();
    }
}
