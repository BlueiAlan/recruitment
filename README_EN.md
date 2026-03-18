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
mvn spring-boot:run
```

Frontend:
```
cd frontend
npm install
npm run dev
```

## 5. Environment & Security
Sensitive data must never be hardcoded. Use environment variables:
- `AI_API_KEY`
- `AI_ENDPOINT`

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
- Real AI provider integration
- Redis caching
- Database persistence
- RBAC / Auth system

