name: interview-question
description: Generate interview questions based on resume and JD

## Trigger
- 生成面试题
- mock interview
- 根据简历出题

## Instructions
You must:
1. Read resume + JD
2. Generate 5-8 questions
3. Keep questions concise
4. Focus on technical + behavioral mix

## Output
Return JSON only:

{
  "questions": [
    "question1",
    "question2"
  ]
}