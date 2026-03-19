# AI Interview System (MVP)

## 1. Overview
This is an enterprise-grade MVP that delivers a full interview flow:
- Resume upload (PDF parsing or text input)
- Job Description input
- DeepSeek-generated opening question (persisted)
- Dynamic follow-up questions based on each answer (persisted in real time)
- Automatic scoring and improvement suggestions
- Current version does not require login; users can start directly from resume upload

## 2. Tech Stack
Backend:
- JDK 17 / Spring Boot 3.x / Maven
- Spring Web / Validation / WebSocket
- Lombok

Frontend:
- Vue 3 / Vite
- Pinia / Vue Router
- Element Plus
- Axios wrapper

## 3. Project Structure
```
Recruitment/
  backend/
  frontend/
```

## 4. Local Run
Backend:
```
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Frontend:
```
cd frontend
npm install
npm run dev
```

## 5. Environment & Security
Sensitive data must never be hardcoded. Use environment variables:
- `AI_PROVIDER` (`deepseek`, `openai`, or `mock`)
- `AI_API_KEY`
- `AI_ENDPOINT` (default `https://api.deepseek.com/chat/completions`)
- `AI_MODEL` (default `deepseek-chat`)
- `AI_TIMEOUT_MS` (optional, default 10000)
- `DATABASE_URL` (supports Neon-style `postgresql://...`; auto-converted to JDBC at startup)
- `DB_POOL_MAX_SIZE` (optional, default 10)
- `DB_POOL_MIN_IDLE` (optional, default 2)

For local development, put secrets in `backend/src/main/resources/application-local.yml`
and run with `-Dspring-boot.run.profiles=local`. This file is ignored by `.gitignore`.

Default provider is DeepSeek (`AI_PROVIDER=deepseek`) through an OpenAI-compatible endpoint.
For local demo only, set `AI_PROVIDER=mock` to use the built-in mock client.

You may use a local `.env` file for development, but **do not commit it**.

## 6. Key APIs
- `POST /api/resume/upload`
- `POST /api/resume/text`
- `POST /api/interview/start`
- `POST /api/interview/answer`
- `GET /api/interview/result`

## 7. Privacy & Compliance
- Resumes and interview content are sensitive data and must not be committed.
- Store them locally (e.g., `uploads/`) or in a secured database.
- Do not commit real user data or samples containing personal information.

## 8. Notes
This MVP can be extended with:
- Redis caching
- Further database hardening (users, permissions, session lifecycle)
- RBAC / Auth system
- PostgreSQL + Drizzle ORM (see `database/drizzle/schema.ts` and `database/drizzle/0001_init.sql`)

The current backend persists resumes, interview sessions, questions, and answers in PostgreSQL.
Before first run, execute `database/drizzle/0001_init.sql` in Neon.

Example (PowerShell):
```powershell
$env:DATABASE_URL="postgresql://<user>:<password>@<host>/<db>?sslmode=require&channel_binding=require"
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Database design notes: `database/README.md`.

