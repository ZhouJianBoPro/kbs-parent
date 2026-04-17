# 企业知识库系统 (KBS)

基于 Spring AI + Vue 3 的企业知识库系统，支持管理员上传文档、向量化处理存储到 ES，用户通过 AI 对话获取知识。

## 技术栈

### 后端

| 技术 | 说明                       |
|------|--------------------------|
| Spring Boot 3.x | 基础框架                     |
| Spring AI | AI 能力封装（阿里云百炼 DashScope） |
| Spring Security + JWT | 认证授权 （双Token）            |
| MyBatis-Plus | 数据库访问                    |
| RocketMQ | 消息队列（异步处理 文档 及 AI 对话）    |
| Redis | 缓存、Token 存储、消息发布/订阅      |
| Elasticsearch | 向量存储（RAG 知识检索）           |
| MCP (Model Context Protocol) | 工具服务（Streamable HTTP）    |
| 阿里云 OSS | 文件存储                     |

### 前端

| 技术 | 说明 |
|------|------|
| Vue 3 | 框架 |
| Vite | 构建工具 |
| Element Plus | UI 组件库 |
| Pinia | 状态管理 |
| Axios | HTTP 请求 |
| SSE | 流式输出 |

## 系统架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         前端 (Vue 3)                            │
│                      http://localhost:3000                      │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                    kbs-api (Spring Boot)                        │
│                  http://localhost:8080/kbs                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐│
│  │ Controller  │  │  Service    │  │    RocketMQ Consumer    ││
│  └─────────────┘  └─────────────┘  └─────────────────────────┘│
│         │                │                      │              │
│         ▼                ▼                      ▼              │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Spring AI ChatClient (RAG + MCP)            │  │
│  └──────────────────────────────────────────────────────────┘  │
└──────────────┬──────────────────────┬───────────────────────────┘
               │                      │
               ▼                      ▼
┌──────────────────────┐    ┌─────────────────────────────────┐
│    kbs-mcp          │    │        外部服务                  │
│  (MCP Server)       │    │  ┌─────────┐ ┌──────────────┐  │
│  localhost:8081     │    │  │ES       │ │ 阿里云OSS    │  │
│  /data-query-mcp    │    │  │向量存储 │ │ 文件存储     │  │
└──────────────────────┘    │  └─────────┘ └──────────────┘  │
                           │  ┌─────────┐ ┌──────────────┐  │
                           │  │DashScope│ │ 阿里云百炼   │  │
                           │  │(Qwen)  │ │ AI模型       │  │
                           │  └─────────┘ └──────────────┘  │
                           │  ┌─────────┐ ┌──────────────┐  │
                           │  │Redis    │ │ MySQL        │  │
                           │  │Token/Pub│ │ 数据持久化   │  │
                           │  └─────────┘ └──────────────┘  │
                           └─────────────────────────────────┘
```

## 项目结构

```
kbs-parent/                    # 父项目
├── kbs-common/                # 公共模块（无 Spring 依赖）
│   ├── entity/                 # 基础实体类
│   ├── enums/                 # 枚举类
│   ├── exception/              # 异常类
│   └── validator/              # 校验器
├── kbs-core/                  # 核心模块
│   ├── entity/                 # 业务实体类
│   ├── mapper/                 # MyBatis Mapper
│   └── vo/                     # VO 对象
├── kbs-security-starter/       # 安全模块
│   └── JWT + Spring Security
├── kbs-file-starter/           # 文件模块
│   └── 阿里云 OSS 封装
├── kbs-chatai-starter/         # AI 模块
│   ├── rag/                    # RAG 实现
│   └── prompt/                 # 系统提示词
├── kbs-mcp/                    # MCP 工具服务 (端口 8081)
│   └── tools/                  # 工具服务
├── kbs-api/                    # API 模块
│   ├── controller/             # 控制器
│   ├── service/                 # 业务逻辑
│   └── rocketmq/                # 消息消费者
└── kbs-vue/                    # 前端项目
    └── src/
        ├── api/                 # API 请求
        ├── stores/              # Pinia 状态管理
        ├── router/              # 路由配置
        └── views/               # 页面组件
```

## 账户类型与功能

| 账户类型 | 说明 | 功能模块 |
|----------|------|----------|
| **NORMAL** | 普通用户 | AI 对话、个人中心 |
| **SYSTEM** | 系统管理员 | 文档管理、用户管理、Token 统计 |

### 功能说明

#### 普通用户 (NORMAL)

- **AI 对话**：基于 RAG + MCP 工具的智能对话
  - 支持流式输出
  - 上下文记忆
  - 可调用工具查询/删除用户信息
- **个人中心**
  - 查看/修改个人信息
  - 修改密码

#### 系统管理员 (SYSTEM)

- **文档管理**
  - 上传文档（PDF、Word、TXT）
  - 自动向量化处理
  - 文档状态查看
- **用户管理**
  - 查看用户列表
  - 禁用/启用用户
- **Token 统计**
  - 查看 Token 使用情况

## 数据库表结构

### 1. app_user（应用用户表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(19) | 用户ID（雪花算法） |
| account_type | char(6) | 账户类型（NORMAL/SYSTEM） |
| username | varchar(50) | 用户名（唯一） |
| password | varchar(255) | 密码（BCrypt 加密） |
| nickname | varchar(50) | 昵称 |
| phone | varchar(20) | 手机号 |
| email | varchar(100) | 邮箱 |
| status | tinyint | 状态（0-正常，1-禁用） |
| del_flag | tinyint | 删除标志（0-未删除，1-已删除） |

### 2. document（文档表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(19) | 文档ID（雪花算法） |
| doc_name | varchar(255) | 文档名称 |
| doc_type | varchar(50) | 文档类型（pdf、doc、docx、txt） |
| file_size | bigint | 文件大小（字节） |
| file_url | varchar(500) | OSS 存储路径 |
| file_key | varchar(500) | OSS 存储 key |
| status | tinyint | 状态（0-待处理，1-处理中，2-成功，3-失败） |
| upload_time | datetime | 上传时间 |
| process_time | datetime | 处理完成时间 |

### 3. user_conversation（用户会话表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(19) | 会话ID（雪花算法） |
| user_id | varchar(19) | 用户ID |
| title | varchar(255) | 会话标题 |

### 4. token_usage（Token 使用记录表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(19) | 主键ID（雪花算法） |
| conversation_id | varchar(19) | 会话ID |
| user_id | varchar(19) | 用户ID |
| input_token | int | 输入 Token 数量 |
| output_token | int | 输出 Token 数量 |
| total_token | int | 总 Token 数量 |

### 5. spring_ai_chat_memory（对话记忆表）

由 Spring AI 自动创建，用于存储对话历史。

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Elasticsearch 8.0+
- RocketMQ 4.9+

### 配置文件

敏感配置通过环境变量注入：

```bash
# 阿里云 DashScope
export DASHSCOPE_API_KEY="your-api-key"

# 阿里云 OSS
export OSS_ACCESS_KEY_ID="your-access-key-id"
export OSS_ACCESS_KEY_SECRET="your-access-key-secret"
```

### 启动服务

```bash
# 1. 编译项目
mvn clean compile -DskipTests

# 2. 启动 MCP 服务 (端口 8081)
mvn spring-boot:run -pl kbs-mcp

# 3. 启动 API 服务 (端口 8080)
mvn spring-boot:run -pl kbs-api

# 4. 启动前端 (端口 3000)
cd kbs-vue
npm install
npm run dev
```

### 访问地址

- 前端：http://localhost:3000
- 后端 API：http://localhost:8080/kbs
- MCP 服务：http://localhost:8081
