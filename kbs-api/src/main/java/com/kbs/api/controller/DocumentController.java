package com.kbs.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kbs.api.service.AppUserService;
import com.kbs.api.service.DocumentService;
import com.kbs.core.vo.DocumentPageRequest;
import com.kbs.core.vo.DocumentVO;
import com.kbs.aspect.annotation.LimitRequest;
import com.kbs.common.exception.BusinessException;
import com.kbs.common.model.Result;
import com.kbs.core.entity.AppUser;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文档控制器
 */
@Slf4j
@PreAuthorize("hasRole('SYSTEM')")
@RestController
@RequestMapping("/document")
public class DocumentController {

    @Resource
    private DocumentService documentService;

    @Resource
    private AppUserService appUserService;

    /**
     * 上传文档
     */
    @LimitRequest
    @PostMapping("/upload")
    public Result<Void> uploadDocument(@RequestParam("file") MultipartFile file,
                                        Authentication authentication) {
        String username = authentication.getName();
        AppUser user = appUserService.getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        documentService.uploadDocument(file, user.getId());
        return Result.success();
    }

    /**
     * 获取文档详情
     */
    @GetMapping("/{id}")
    public Result<DocumentVO> getDocumentById(@PathVariable("id") String documentId) {
        DocumentVO documentVO = documentService.getDocumentById(documentId);
        return Result.success(documentVO);
    }

    /**
     * 删除文档
     */
    @LimitRequest
    @DeleteMapping("/{id}")
    public Result<String> deleteDocument(@PathVariable("id") String documentId) {
        documentService.deleteDocument(documentId);
        return Result.success();
    }

    /**
     * 分页查询文档列表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    @PostMapping("/list")
    public Result<IPage<DocumentVO>> getDocumentPage(@RequestBody DocumentPageRequest request) {
        Page<DocumentVO> page = new Page<>(request.getPageNo(), request.getPageSize());
        IPage<DocumentVO> result = documentService.getDocumentPage(page, request);
        return Result.success(result);
    }

    /**
     * 下载文档
     */
    @LimitRequest
    @GetMapping("/download/{id}")
    public void downloadDocument(@PathVariable("id") String documentId,
                                  HttpServletResponse response) {
        DocumentVO document = documentService.getDocumentById(documentId);

        try (InputStream inputStream = documentService.getDocumentStream(documentId);
             OutputStream outputStream = response.getOutputStream()) {

            response.setContentType("application/octet-stream");
            // 处理中文文件名，使用URL编码
            String filename = document.getDocName();
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);
            response.setContentLengthLong(document.getFileSize());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (IOException e) {
            log.error("文档下载失败: {}", documentId, e);
            throw new BusinessException("文档下载失败");
        }
    }
}
