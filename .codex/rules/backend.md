# Backend Rules

适用范围：`backend/` 目录内的代码与接口设计（本项目当前默认不修改）。

## 规则
- REST 统一返回：`{ code, message, data }`
- DTO/VO 与 Entity 分离
- 全局异常处理统一入口：`backend/src/main/java/com/company/aiinterview/exception/GlobalExceptionHandler.java`
- AI 能力必须通过 `AiClient` 抽象调用
- 服务层优先覆盖单元测试
- 禁止修改 settings.xml
- 禁止修改 pom.xml（除非用户明确要求）
