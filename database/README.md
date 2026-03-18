# 数据库设计（PostgreSQL + Drizzle ORM）

## 1. 设计目标
- 覆盖当前 MVP 业务链路：登录、简历、面试会话、问题、回答、评分。
- 可平滑替换现有内存存储（`InMemoryStore`）。
- 所有表统一包含 `id`、`created_at`、`updated_at`。

## 2. 表与关系
- `users` 1:N `resumes`
- `users` 1:N `interview_sessions`
- `resumes` 1:N `interview_sessions`
- `interview_sessions` 1:N `interview_questions`
- `interview_sessions` 1:N `interview_answers`
- `interview_questions` 1:N `interview_answers`
- `users` N:M `roles`（通过 `user_roles`）

## 3. 文件说明
- Drizzle Schema: `database/drizzle/schema.ts`
- PostgreSQL DDL: `database/drizzle/0001_init.sql`

## 4. 安全说明
- 数据库连接串仅通过本地环境变量 `DATABASE_URL` 注入。
- 禁止将真实连接串、密钥、生产账号提交到远程仓库。

