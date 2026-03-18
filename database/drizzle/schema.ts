import {
  bigint,
  boolean,
  index,
  integer,
  jsonb,
  pgEnum,
  pgTable,
  primaryKey,
  text,
  timestamp,
  uniqueIndex,
  uuid,
  varchar,
} from "drizzle-orm/pg-core";
import { relations, sql } from "drizzle-orm";

export const resumeSourceEnum = pgEnum("resume_source", ["upload", "text"]);
export const sessionStatusEnum = pgEnum("session_status", [
  "in_progress",
  "completed",
  "abandoned",
]);

export const users = pgTable(
  "users",
  {
    // 主键ID：用户唯一标识
    id: uuid("id").defaultRandom().primaryKey(),
    // 登录名：用于账号登录，要求唯一
    username: varchar("username", { length: 64 }).notNull(),
    // 密码哈希：存储加密后的密码，不存明文
    passwordHash: varchar("password_hash", { length: 255 }).notNull(),
    // 显示名：用于界面展示
    displayName: varchar("display_name", { length: 100 }),
    // 邮箱：可选联系方式，唯一
    email: varchar("email", { length: 255 }),
    // 是否启用：控制账号可否登录
    isActive: boolean("is_active").notNull().default(true),
    // 创建时间：记录创建时刻
    createdAt: timestamp("created_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
    // 更新时间：记录最后更新时间
    updatedAt: timestamp("updated_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
  },
  (table) => [
    uniqueIndex("uk_users_username").on(table.username),
    uniqueIndex("uk_users_email").on(table.email),
  ],
);

export const roles = pgTable(
  "roles",
  {
    // 主键ID：角色唯一标识
    id: uuid("id").defaultRandom().primaryKey(),
    // 角色编码：如 admin/interviewer/candidate
    code: varchar("code", { length: 50 }).notNull(),
    // 角色名称：用于展示
    name: varchar("name", { length: 100 }).notNull(),
    // 角色描述：补充说明权限职责
    description: text("description"),
    // 创建时间：记录创建时刻
    createdAt: timestamp("created_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
    // 更新时间：记录最后更新时间
    updatedAt: timestamp("updated_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
  },
  (table) => [uniqueIndex("uk_roles_code").on(table.code)],
);

export const userRoles = pgTable(
  "user_roles",
  {
    // 主键ID：关联记录唯一标识
    id: uuid("id").defaultRandom().notNull(),
    // 用户ID：关联 users.id
    userId: uuid("user_id")
      .notNull()
      .references(() => users.id, { onDelete: "cascade", onUpdate: "cascade" }),
    // 角色ID：关联 roles.id
    roleId: uuid("role_id")
      .notNull()
      .references(() => roles.id, { onDelete: "cascade", onUpdate: "cascade" }),
    // 创建时间：记录创建时刻
    createdAt: timestamp("created_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
    // 更新时间：记录最后更新时间
    updatedAt: timestamp("updated_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
  },
  (table) => [
    primaryKey({ name: "pk_user_roles", columns: [table.id] }),
    uniqueIndex("uk_user_roles_user_role").on(table.userId, table.roleId),
    index("idx_user_roles_user_id").on(table.userId),
    index("idx_user_roles_role_id").on(table.roleId),
  ],
);

export const resumes = pgTable(
  "resumes",
  {
    // 主键ID：简历唯一标识
    id: uuid("id").defaultRandom().primaryKey(),
    // 用户ID：简历所属用户
    userId: uuid("user_id").references(() => users.id, {
      onDelete: "set null",
      onUpdate: "cascade",
    }),
    // 来源类型：upload=文件上传，text=文本粘贴
    sourceType: resumeSourceEnum("source_type").notNull().default("text"),
    // 文件名：上传文件原始名称，文本输入可为空
    fileName: varchar("file_name", { length: 255 }),
    // 简历文本：解析后的完整文本内容
    contentText: text("content_text").notNull(),
    // 原始文件URL：对象存储地址，可选
    fileUrl: text("file_url"),
    // 文件大小（字节）：用于上传审计
    fileSize: bigint("file_size", { mode: "number" }),
    // 内容摘要：用于快速检索和展示
    summary: text("summary"),
    // 创建时间：记录创建时刻
    createdAt: timestamp("created_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
    // 更新时间：记录最后更新时间
    updatedAt: timestamp("updated_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
  },
  (table) => [
    index("idx_resumes_user_id").on(table.userId),
    index("idx_resumes_source_type").on(table.sourceType),
  ],
);

export const interviewSessions = pgTable(
  "interview_sessions",
  {
    // 主键ID：面试会话唯一标识
    id: uuid("id").defaultRandom().primaryKey(),
    // 用户ID：会话所属用户
    userId: uuid("user_id").references(() => users.id, {
      onDelete: "set null",
      onUpdate: "cascade",
    }),
    // 简历ID：关联 resumes.id
    resumeId: uuid("resume_id")
      .notNull()
      .references(() => resumes.id, { onDelete: "restrict", onUpdate: "cascade" }),
    // JD 文本：岗位描述原文
    jdText: text("jd_text").notNull(),
    // 状态：进行中/已完成/已中断
    status: sessionStatusEnum("status").notNull().default("in_progress"),
    // 当前问题序号：与现有 currentIndex 语义一致
    currentIndex: integer("current_index").notNull().default(0),
    // 平均分：会话汇总分数，保留两位小数
    averageScore: integer("average_score"),
    // 总建议：面试结束后生成的整体建议
    overallAdvice: text("overall_advice"),
    // AI 元数据：记录模型、供应商、耗时等信息
    aiMeta: jsonb("ai_meta").default(sql`'{}'::jsonb`),
    // 会话开始时间：业务上等价创建时间，便于统计
    startedAt: timestamp("started_at", { withTimezone: true }).defaultNow(),
    // 会话结束时间：完成时写入
    endedAt: timestamp("ended_at", { withTimezone: true }),
    // 创建时间：记录创建时刻
    createdAt: timestamp("created_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
    // 更新时间：记录最后更新时间
    updatedAt: timestamp("updated_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
  },
  (table) => [
    index("idx_sessions_user_id").on(table.userId),
    index("idx_sessions_resume_id").on(table.resumeId),
    index("idx_sessions_status").on(table.status),
  ],
);

export const interviewQuestions = pgTable(
  "interview_questions",
  {
    // 主键ID：问题唯一标识
    id: uuid("id").defaultRandom().primaryKey(),
    // 会话ID：关联 interview_sessions.id
    sessionId: uuid("session_id")
      .notNull()
      .references(() => interviewSessions.id, {
        onDelete: "cascade",
        onUpdate: "cascade",
      }),
    // 问题序号：1,2,3...
    sortOrder: integer("sort_order").notNull(),
    // 问题内容：AI 生成题干
    content: text("content").notNull(),
    // 问题类型：技术/行为/项目经验等，可选
    questionType: varchar("question_type", { length: 50 }),
    // 创建时间：记录创建时刻
    createdAt: timestamp("created_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
    // 更新时间：记录最后更新时间
    updatedAt: timestamp("updated_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
  },
  (table) => [
    uniqueIndex("uk_questions_session_sort").on(table.sessionId, table.sortOrder),
    index("idx_questions_session_id").on(table.sessionId),
  ],
);

export const interviewAnswers = pgTable(
  "interview_answers",
  {
    // 主键ID：回答唯一标识
    id: uuid("id").defaultRandom().primaryKey(),
    // 会话ID：关联 interview_sessions.id
    sessionId: uuid("session_id")
      .notNull()
      .references(() => interviewSessions.id, {
        onDelete: "cascade",
        onUpdate: "cascade",
      }),
    // 问题ID：关联 interview_questions.id
    questionId: uuid("question_id")
      .notNull()
      .references(() => interviewQuestions.id, {
        onDelete: "cascade",
        onUpdate: "cascade",
      }),
    // 作答文本：用户输入回答内容
    answerText: text("answer_text").notNull(),
    // 得分：0~100 分
    score: integer("score"),
    // 反馈：AI 对该回答的评价
    feedback: text("feedback"),
    // 尝试次数：同一道题多次作答时递增
    attemptNo: integer("attempt_no").notNull().default(1),
    // 作答耗时（秒）：用于行为分析
    durationSeconds: integer("duration_seconds"),
    // 创建时间：记录创建时刻
    createdAt: timestamp("created_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
    // 更新时间：记录最后更新时间
    updatedAt: timestamp("updated_at", { withTimezone: true })
      .notNull()
      .defaultNow(),
  },
  (table) => [
    uniqueIndex("uk_answers_question_attempt").on(
      table.questionId,
      table.attemptNo,
    ),
    index("idx_answers_session_id").on(table.sessionId),
    index("idx_answers_question_id").on(table.questionId),
  ],
);

export const usersRelations = relations(users, ({ many }) => ({
  resumes: many(resumes),
  sessions: many(interviewSessions),
  userRoles: many(userRoles),
}));

export const rolesRelations = relations(roles, ({ many }) => ({
  userRoles: many(userRoles),
}));

export const userRolesRelations = relations(userRoles, ({ one }) => ({
  user: one(users, {
    fields: [userRoles.userId],
    references: [users.id],
  }),
  role: one(roles, {
    fields: [userRoles.roleId],
    references: [roles.id],
  }),
}));

export const resumesRelations = relations(resumes, ({ one, many }) => ({
  user: one(users, {
    fields: [resumes.userId],
    references: [users.id],
  }),
  sessions: many(interviewSessions),
}));

export const interviewSessionsRelations = relations(
  interviewSessions,
  ({ one, many }) => ({
    user: one(users, {
      fields: [interviewSessions.userId],
      references: [users.id],
    }),
    resume: one(resumes, {
      fields: [interviewSessions.resumeId],
      references: [resumes.id],
    }),
    questions: many(interviewQuestions),
    answers: many(interviewAnswers),
  }),
);

export const interviewQuestionsRelations = relations(
  interviewQuestions,
  ({ one, many }) => ({
    session: one(interviewSessions, {
      fields: [interviewQuestions.sessionId],
      references: [interviewSessions.id],
    }),
    answers: many(interviewAnswers),
  }),
);

export const interviewAnswersRelations = relations(interviewAnswers, ({ one }) => ({
  session: one(interviewSessions, {
    fields: [interviewAnswers.sessionId],
    references: [interviewSessions.id],
  }),
  question: one(interviewQuestions, {
    fields: [interviewAnswers.questionId],
    references: [interviewQuestions.id],
  }),
}));

