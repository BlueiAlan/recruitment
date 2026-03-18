# CLAUDE.md

## 1. 项目定位与范围
- AI 模拟面试系统（MVP）
- 目标：提供从简历与 JD 到问题生成、回答评分与建议的完整闭环
- 交付：前后端分离、可运行、可扩展的企业级最小可行项目

## 2. 目录结构
- `backend/` Spring Boot 3.x 服务
- `frontend/` Vue3 + Vite 前端

## 3. 开发流程（企业级）
- 分支策略：
  - `main` 为稳定发布分支
  - `develop` 为日常集成分支（可选）
  - 需求分支采用 `feat/<topic>`，修复分支采用 `fix/<topic>`
- 提交规范：
  - `feat: ...` 新功能
  - `fix: ...` Bug 修复
  - `chore: ...` 构建/工具链
  - `docs: ...` 文档
- 代码评审：
  - 所有合并必须通过至少 1 人 Code Review
  - 变更需提供可运行说明与影响面描述

## 4. 质量与测试
- 后端：单元测试优先覆盖 service 层
- 前端：核心交互（面试流程）需提供最小回归路径
- 每次变更必须保证本地可运行

## 5. 运行与环境
后端：
- 进入 `backend/`
- 执行 `mvn spring-boot:run`

前端：
- 进入 `frontend/`
- 执行 `npm install`
- 执行 `npm run dev`

## 6. 安全与合规
- AI Key 与敏感信息禁止硬编码
- 统一通过环境变量注入：`AI_API_KEY`、`AI_ENDPOINT`
- 不提交包含密钥的文件到仓库

## 7. 关键约定
- REST 统一返回结构：`{ code, message, data }`
- DTO/VO 与 Entity 分离
- 全局异常处理：`backend/src/main/java/com/company/aiinterview/exception/GlobalExceptionHandler.java`
- AI 能力必须通过 `AiClient` 抽象调用

## 8. 重要入口
- 后端入口：`backend/src/main/java/com/company/aiinterview/AiInterviewApplication.java`
- 前端入口：`frontend/src/main.js`
- 统一请求封装：`frontend/src/utils/request.js`
