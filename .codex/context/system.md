# System Context

本文件为 Codex 运行约定，补充项目层面的规则与输出规范。

## 1. 不修改目录
- 禁止修改 `backend/` 与 `frontend/` 目录及其子目录（除非明确授权）

## 2. 安全与合规
- 任何密钥与敏感信息通过环境变量注入：`AI_API_KEY`、`AI_ENDPOINT`
- 不生成或提交真实用户数据

## 3. 输出规范
- 接口返回结构统一为：`{ code, message, data }`
- 变更尽量集中在根目录与 `.codex/` 目录

## 4. 推荐工作流
- 需求澄清与流程设计：先更新 `.codex/prompts/` 与 `.codex/rules/`
- 文档同步：更新 `README.md` / `README_EN.md`（如需要）
