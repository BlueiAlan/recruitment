CREATE EXTENSION IF NOT EXISTS "pgcrypto";

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'resume_source') THEN
    CREATE TYPE resume_source AS ENUM ('upload', 'text');
  END IF;
END $$;

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'session_status') THEN
    CREATE TYPE session_status AS ENUM ('in_progress', 'completed', 'abandoned');
  END IF;
END $$;

CREATE TABLE IF NOT EXISTS users (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  username varchar(64) NOT NULL,
  password_hash varchar(255) NOT NULL,
  display_name varchar(100),
  email varchar(255),
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT uk_users_username UNIQUE (username),
  CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS roles (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  code varchar(50) NOT NULL,
  name varchar(100) NOT NULL,
  description text,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT uk_roles_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS user_roles (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
  role_id uuid NOT NULL REFERENCES roles(id) ON UPDATE CASCADE ON DELETE CASCADE,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT uk_user_roles_user_role UNIQUE (user_id, role_id)
);

CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);

CREATE TABLE IF NOT EXISTS resumes (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL,
  source_type resume_source NOT NULL DEFAULT 'text',
  file_name varchar(255),
  content_text text NOT NULL,
  file_url text,
  file_size bigint,
  summary text,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_resumes_user_id ON resumes(user_id);
CREATE INDEX IF NOT EXISTS idx_resumes_source_type ON resumes(source_type);

CREATE TABLE IF NOT EXISTS interview_sessions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL,
  resume_id uuid NOT NULL REFERENCES resumes(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  jd_text text NOT NULL,
  status session_status NOT NULL DEFAULT 'in_progress',
  current_index integer NOT NULL DEFAULT 0,
  average_score integer,
  overall_advice text,
  ai_meta jsonb DEFAULT '{}'::jsonb,
  started_at timestamptz DEFAULT now(),
  ended_at timestamptz,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT ck_average_score_range CHECK (average_score IS NULL OR (average_score BETWEEN 0 AND 100))
);

CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON interview_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_sessions_resume_id ON interview_sessions(resume_id);
CREATE INDEX IF NOT EXISTS idx_sessions_status ON interview_sessions(status);

CREATE TABLE IF NOT EXISTS interview_questions (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  session_id uuid NOT NULL REFERENCES interview_sessions(id) ON UPDATE CASCADE ON DELETE CASCADE,
  sort_order integer NOT NULL,
  content text NOT NULL,
  question_type varchar(50),
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT uk_questions_session_sort UNIQUE (session_id, sort_order)
);

CREATE INDEX IF NOT EXISTS idx_questions_session_id ON interview_questions(session_id);

CREATE TABLE IF NOT EXISTS interview_answers (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  session_id uuid NOT NULL REFERENCES interview_sessions(id) ON UPDATE CASCADE ON DELETE CASCADE,
  question_id uuid NOT NULL REFERENCES interview_questions(id) ON UPDATE CASCADE ON DELETE CASCADE,
  answer_text text NOT NULL,
  score integer,
  feedback text,
  attempt_no integer NOT NULL DEFAULT 1,
  duration_seconds integer,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT uk_answers_question_attempt UNIQUE (question_id, attempt_no),
  CONSTRAINT ck_score_range CHECK (score IS NULL OR (score BETWEEN 0 AND 100)),
  CONSTRAINT ck_attempt_no_positive CHECK (attempt_no > 0),
  CONSTRAINT ck_duration_seconds_positive CHECK (duration_seconds IS NULL OR duration_seconds >= 0)
);

CREATE INDEX IF NOT EXISTS idx_answers_session_id ON interview_answers(session_id);
CREATE INDEX IF NOT EXISTS idx_answers_question_id ON interview_answers(question_id);

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_users_updated_at ON users;
CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_roles_updated_at ON roles;
CREATE TRIGGER trg_roles_updated_at BEFORE UPDATE ON roles
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_user_roles_updated_at ON user_roles;
CREATE TRIGGER trg_user_roles_updated_at BEFORE UPDATE ON user_roles
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_resumes_updated_at ON resumes;
CREATE TRIGGER trg_resumes_updated_at BEFORE UPDATE ON resumes
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_interview_sessions_updated_at ON interview_sessions;
CREATE TRIGGER trg_interview_sessions_updated_at BEFORE UPDATE ON interview_sessions
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_interview_questions_updated_at ON interview_questions;
CREATE TRIGGER trg_interview_questions_updated_at BEFORE UPDATE ON interview_questions
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_interview_answers_updated_at ON interview_answers;
CREATE TRIGGER trg_interview_answers_updated_at BEFORE UPDATE ON interview_answers
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

COMMENT ON TABLE users IS '系统用户表';
COMMENT ON COLUMN users.id IS '主键ID：用户唯一标识';
COMMENT ON COLUMN users.username IS '登录名：用于账号登录，要求唯一';
COMMENT ON COLUMN users.password_hash IS '密码哈希：存储加密后的密码，不存明文';
COMMENT ON COLUMN users.display_name IS '显示名：用于界面展示';
COMMENT ON COLUMN users.email IS '邮箱：可选联系方式，唯一';
COMMENT ON COLUMN users.is_active IS '是否启用：控制账号可否登录';
COMMENT ON COLUMN users.created_at IS '创建时间：记录创建时刻';
COMMENT ON COLUMN users.updated_at IS '更新时间：记录最后更新时间';

COMMENT ON TABLE roles IS '角色表';
COMMENT ON COLUMN roles.id IS '主键ID：角色唯一标识';
COMMENT ON COLUMN roles.code IS '角色编码：如 admin/interviewer/candidate';
COMMENT ON COLUMN roles.name IS '角色名称：用于展示';
COMMENT ON COLUMN roles.description IS '角色描述：补充说明权限职责';
COMMENT ON COLUMN roles.created_at IS '创建时间：记录创建时刻';
COMMENT ON COLUMN roles.updated_at IS '更新时间：记录最后更新时间';

COMMENT ON TABLE user_roles IS '用户角色关联表（多对多中间表）';
COMMENT ON COLUMN user_roles.id IS '主键ID：关联记录唯一标识';
COMMENT ON COLUMN user_roles.user_id IS '用户ID：关联 users.id';
COMMENT ON COLUMN user_roles.role_id IS '角色ID：关联 roles.id';
COMMENT ON COLUMN user_roles.created_at IS '创建时间：记录创建时刻';
COMMENT ON COLUMN user_roles.updated_at IS '更新时间：记录最后更新时间';

COMMENT ON TABLE resumes IS '简历表';
COMMENT ON COLUMN resumes.id IS '主键ID：简历唯一标识';
COMMENT ON COLUMN resumes.user_id IS '用户ID：简历所属用户';
COMMENT ON COLUMN resumes.source_type IS '来源类型：upload=文件上传，text=文本粘贴';
COMMENT ON COLUMN resumes.file_name IS '文件名：上传文件原始名称，文本输入可为空';
COMMENT ON COLUMN resumes.content_text IS '简历文本：解析后的完整文本内容';
COMMENT ON COLUMN resumes.file_url IS '原始文件URL：对象存储地址，可选';
COMMENT ON COLUMN resumes.file_size IS '文件大小（字节）：用于上传审计';
COMMENT ON COLUMN resumes.summary IS '内容摘要：用于快速检索和展示';
COMMENT ON COLUMN resumes.created_at IS '创建时间：记录创建时刻';
COMMENT ON COLUMN resumes.updated_at IS '更新时间：记录最后更新时间';

COMMENT ON TABLE interview_sessions IS '面试会话表';
COMMENT ON COLUMN interview_sessions.id IS '主键ID：面试会话唯一标识';
COMMENT ON COLUMN interview_sessions.user_id IS '用户ID：会话所属用户';
COMMENT ON COLUMN interview_sessions.resume_id IS '简历ID：关联 resumes.id';
COMMENT ON COLUMN interview_sessions.jd_text IS 'JD 文本：岗位描述原文';
COMMENT ON COLUMN interview_sessions.status IS '状态：进行中/已完成/已中断';
COMMENT ON COLUMN interview_sessions.current_index IS '当前问题序号：与现有 currentIndex 语义一致';
COMMENT ON COLUMN interview_sessions.average_score IS '平均分：会话汇总分数';
COMMENT ON COLUMN interview_sessions.overall_advice IS '总建议：面试结束后生成的整体建议';
COMMENT ON COLUMN interview_sessions.ai_meta IS 'AI 元数据：记录模型、供应商、耗时等信息';
COMMENT ON COLUMN interview_sessions.started_at IS '会话开始时间：便于统计';
COMMENT ON COLUMN interview_sessions.ended_at IS '会话结束时间：完成时写入';
COMMENT ON COLUMN interview_sessions.created_at IS '创建时间：记录创建时刻';
COMMENT ON COLUMN interview_sessions.updated_at IS '更新时间：记录最后更新时间';

COMMENT ON TABLE interview_questions IS '面试问题表';
COMMENT ON COLUMN interview_questions.id IS '主键ID：问题唯一标识';
COMMENT ON COLUMN interview_questions.session_id IS '会话ID：关联 interview_sessions.id';
COMMENT ON COLUMN interview_questions.sort_order IS '问题序号：1,2,3...';
COMMENT ON COLUMN interview_questions.content IS '问题内容：AI 生成题干';
COMMENT ON COLUMN interview_questions.question_type IS '问题类型：技术/行为/项目经验等，可选';
COMMENT ON COLUMN interview_questions.created_at IS '创建时间：记录创建时刻';
COMMENT ON COLUMN interview_questions.updated_at IS '更新时间：记录最后更新时间';

COMMENT ON TABLE interview_answers IS '面试回答表';
COMMENT ON COLUMN interview_answers.id IS '主键ID：回答唯一标识';
COMMENT ON COLUMN interview_answers.session_id IS '会话ID：关联 interview_sessions.id';
COMMENT ON COLUMN interview_answers.question_id IS '问题ID：关联 interview_questions.id';
COMMENT ON COLUMN interview_answers.answer_text IS '作答文本：用户输入回答内容';
COMMENT ON COLUMN interview_answers.score IS '得分：0~100 分';
COMMENT ON COLUMN interview_answers.feedback IS '反馈：AI 对该回答的评价';
COMMENT ON COLUMN interview_answers.attempt_no IS '尝试次数：同一道题多次作答时递增';
COMMENT ON COLUMN interview_answers.duration_seconds IS '作答耗时（秒）：用于行为分析';
COMMENT ON COLUMN interview_answers.created_at IS '创建时间：记录创建时刻';
COMMENT ON COLUMN interview_answers.updated_at IS '更新时间：记录最后更新时间';

