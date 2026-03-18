# AI Interview System (MVP)

## 1. Overview
This is an enterprise-grade MVP that delivers a full interview flow:
- Resume upload (PDF parsing or text input)
- Job Description input
- Interview question generation
- Chat-style Q&A
- Automatic scoring and improvement suggestions

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
- `AI_PROVIDER` (`openai` or `mock`)
- `AI_API_KEY`
- `AI_ENDPOINT` (OpenAI-compatible Chat Completions endpoint)
- `AI_MODEL`
- `AI_TIMEOUT_MS` (optional, default 10000)

For local development, put secrets in `backend/src/main/resources/application-local.yml`
and run with `-Dspring-boot.run.profiles=local`. This file is ignored by `.gitignore`.

To skip a real provider, set `AI_PROVIDER=mock` to use the built-in mock client.

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
- Database persistence
- RBAC / Auth system
- PostgreSQL + Drizzle ORM (see `database/drizzle/schema.ts` and `database/drizzle/0001_init.sql`)

The current version stores resumes and interview sessions in memory. Recommended flow:
- Upload or paste the resume first, then click "Start Interview" on the JD page.
- The frontend persists `resumeId`, but backend restart clears in-memory data, so you must re-upload the resume before starting again.

Database design notes: `database/README.md`.

