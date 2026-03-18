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
mvn spring-boot:run
```

前端：
```
cd frontend
npm install
npm run dev
```

## 5. 环境变量与安全
敏感信息禁止硬编码，统一通过环境变量注入：
- `AI_API_KEY`
- `AI_ENDPOINT`

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
- 替换真实 AI 供应商
- 引入 Redis 缓存
- 接入数据库持久化
- 接入 RBAC 权限控制

