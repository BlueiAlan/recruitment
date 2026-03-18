# AI 模拟面试系统（MVP）

## 1. 项目简介
本项目为企业级最小可行产品（MVP）示例，包含前后端完整链路：
- 简历上传（PDF解析或文本输入）
- JD 输入
- 生成面试问题
- 逐题对话式回答
- 自动评分与改进建议

## 2. 技术栈
后端：
- JDK 17 / Spring Boot 3.x / Maven
- Spring Web / Validation / WebSocket
- Lombok

前端：
- Vue 3 / Vite
- Pinia / Vue Router
- Element Plus
- Axios 二次封装

## 3. 项目结构
```
Recruitment/
  backend/
  frontend/
```

## 4. 本地启动
后端：
```
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

前端：
```
cd frontend
npm install
npm run dev
```

## 5. 环境变量与安全
敏感信息禁止硬编码，统一通过环境变量注入：
- `AI_PROVIDER`（`openai` 或 `mock`）
- `AI_API_KEY`
- `AI_ENDPOINT`（OpenAI 兼容的 Chat Completions 接口地址）
- `AI_MODEL`
- `AI_TIMEOUT_MS`（可选，默认 10000）

本地开发建议使用 `backend/src/main/resources/application-local.yml` 写入密钥，
并通过 `-Dspring-boot.run.profiles=local` 启动。本文件已在 `.gitignore` 中忽略。

如需不接入真实供应商，可设置 `AI_PROVIDER=mock` 使用内置模拟客户端。

如需本地配置，可使用 `.env` 文件，但禁止提交到远程仓库。

## 6. 主要接口
- `POST /api/resume/upload`
- `POST /api/resume/text`
- `POST /api/interview/start`
- `POST /api/interview/answer`
- `GET /api/interview/result`

## 7. 隐私与合规
- 简历、面试内容属于敏感数据，不允许提交到远程仓库。
- 建议仅在本地 `uploads/` 或数据库中保存，并做好访问控制。
- 禁止提交包含真实用户信息的样例数据。

## 8. 说明
本项目为 MVP 样例，后续可扩展：
- 引入 Redis 缓存
- 接入数据库持久化
- 接入 RBAC 权限控制
- 使用 PostgreSQL + Drizzle ORM（参考 `database/drizzle/schema.ts` 与 `database/drizzle/0001_init.sql`）

当前版本使用内存存储简历与面试会话，使用流程建议为：
- 先在“上传简历”页面完成上传/粘贴，再进入 JD 页面点击“开始面试”。
- 前端会持久化 `resumeId`，但后端重启后内存数据会清空，需要重新上传简历后再开始面试。

数据库设计说明见：`database/README.md`。

