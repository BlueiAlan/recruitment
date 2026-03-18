# AI Interview System - Agent Context

## Project Overview
This is an AI-powered mock interview system.

## Tech Stack
- Backend: Spring Boot 3 + JDK17
- Frontend: Vue3 + Vite + Pinia + Element Plus

## Backend Rules
- Use Controller / Service / ServiceImpl layering
- Do NOT return Entity directly
- Always use DTO / VO
- Unified response: Result<T>

## Frontend Rules
- Composition API only
- Use Pinia for state
- Axios must be encapsulated

## AI Behavior
- Always output JSON for AI results
- Keep prompts concise (low token usage)

## Documentation Sync
- When generating or modifying features, update relevant sections in `README.md` and `README_EN.md` (e.g., change the `## 8. 说明` extension list when replacing the AI provider).
