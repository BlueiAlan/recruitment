# AI 模拟面试系统（MVP）

## 1. 项目简介
本项目为企业级最小可行产品（MVP）示例，包含前后端完整链路：
- 简历上传（PDF解析或文本输入）
- JD 输入
- DeepSeek 实时生成首题并入库
- 基于回答动态追问（问题实时入库）
- 自动评分与改进建议
- 当前版本默认无需登录，直接上传简历即可开始模拟面试

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
- `AI_ENDPOINT`（默认 `https://api.deepseek.com/chat/completions`）
- `AI_MODEL`（默认 `deepseek-chat`）
- `AI_TIMEOUT_MS`（可选，默认 10000）
- `DATABASE_URL`（支持 Neon 的 `postgresql://...`，程序启动时会自动转换为 JDBC）
- `DB_POOL_MAX_SIZE`（可选，默认 10）
- `DB_POOL_MIN_IDLE`（可选，默认 2）

本地开发建议使用 `backend/src/main/resources/application-local.yml` 写入密钥，
并通过 `-Dspring-boot.run.profiles=local` 启动。本文件已在 `.gitignore` 中忽略。

默认使用 DeepSeek（`AI_PROVIDER=deepseek`，走 OpenAI 兼容接口）。
如需本地演示可设置 `AI_PROVIDER=mock` 使用内置模拟客户端。

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
- 继续完善数据库持久化（用户、权限、会话状态等）
- 接入 RBAC 权限控制
- 使用 PostgreSQL + Drizzle ORM（参考 `database/drizzle/schema.ts` 与 `database/drizzle/0001_init.sql`）

当前版本后端已使用 PostgreSQL 存储简历、面试会话、问题与答案。首次运行前请先在 Neon 执行：
`database/drizzle/0001_init.sql`。

示例（PowerShell）：
```powershell
$env:DATABASE_URL="postgresql://<user>:<password>@<host>/<db>?sslmode=require&channel_binding=require"
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

数据库设计说明见：`database/README.md`。

