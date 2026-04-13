# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

企业知识库系统 (Enterprise Knowledge Base System)

一个基于 Spring AI + Vue 3 的企业知识库系统，支持管理员上传文档、向量化处理存储到 ES，用户通过 AI 对话获取知识。

## Project Structure

```
kbs-parent/               # 父项目 (Maven)
├── kbs-common/           # 公共模块：无 Spring 依赖，统一返回结果、异常、工具类
├── kbs-core/             # 核心模块：实体类、Mapper、VO
├── kbs-security-starter/ # 安全模块：Spring Security + JWT + Redis 双 Token
├── kbs-file-starter/     # 文件模块：阿里云 OSS 文件服务封装
├── kbs-chatai-starter/   # AI 模块：Spring AI 千问模型封装
├── kbs-mcp/              # MCP 模块：提供 Tools 工具服务 (端口 8081)
├── kbs-api/              # API 模块：业务逻辑、Controller
└── kbs-vue/              # 前端项目：Vue 3 + Vite + Element Plus
```

## Common Commands

### Backend (Java)

```bash
# 进入后端目录
cd /Users/zhoujianbo/cc/kbs-parent

# 编译项目
mvn clean compile -DskipTests

# 运行后端服务
mvn spring-boot:run -pl kbs-api

# 后端端口：8080，Context-Path：/kbs
# 示例：http://localhost:8080/kbs/api/auth/login
```

### Frontend (Vue)

```bash
# 进入前端目录
cd /Users/zhoujianbo/cc/kbs-parent/kbs-vue

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 前端端口：3000 (vite.config.js 中配置)
```

## Architecture Highlights

### 后端架构

- **认证机制**：双 Token (accessToken + refreshToken)，存储在 Redis
- **权限控制**：Spring Security + `@PreAuthorize` / `@Secured`
- **分页查询**：超过3个字段使用实体类 (继承 `BasePage`)，使用原生 MyBatis
- **SSE 流式输出**：使用 `SseEmitter` 实现 Server-Sent Events
- **消息队列**：RocketMQ 异步处理 AI 对话，Redis 发布/订阅分发结果
- **数据库**：MySQL + MyBatis-Plus，所有表继承 `BaseEntity` (含 id, del_flag, create_time 等)

### 前端架构

- **状态管理**：Pinia
- **HTTP 请求**：Axios 封装，含 token 刷新、401 跳转登录逻辑
- **SSE**：使用原生 fetch API 实现流式接收
- **路由**：Vue Router，按 accountType 过滤菜单

### 代码规范

- 所有表 `id` 字段为 `varchar` 类型，使用雪花算法生成
- 实体类使用 Lombok 注解 (`@Data`, `@Slf4j`)
- 关键代码需添加中文注释，类注释、方法注释必填
- 编码符合阿里巴巴开发手册规范

## 环境变量配置

敏感配置通过环境变量注入，在 IDEA Run Configuration 的 Environment variables 中配置：

- `DASHSCOPE_API_KEY`: 阿里云 DashScope API Key
- `OSS_ACCESS_KEY_ID`: 阿里云 OSS AccessKeyId
- `OSS_ACCESS_KEY_SECRET`: 阿里云 OSS AccessKeySecret

## MCP 工具服务架构

- **kbs-mcp (端口 8081)**：MCP Server，提供 Tools 工具
  - endpoint: `/kbs-mcp`
  - 可用工具：`getUserInfo`、`deleteUserInfo`
- **kbs-api**：MCP Client，通过 `spring.ai.mcp.client` 配置连接

## 系统提示词

AI 系统提示词位于：`kbs-chatai-starter/src/main/resources/prompt/default_system.st`
