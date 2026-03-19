package com.company.aiinterview.repository;

import com.company.aiinterview.domain.entity.Answer;
import com.company.aiinterview.domain.entity.InterviewSession;
import com.company.aiinterview.domain.entity.Question;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class InterviewSessionRepository {
    private final JdbcTemplate jdbcTemplate;

    public InterviewSessionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void create(InterviewSession session) {
        jdbcTemplate.update("""
                INSERT INTO interview_sessions(id, resume_id, jd_text, current_index, started_at, created_at, updated_at)
                VALUES (?::uuid, ?::uuid, ?, ?, ?, ?, ?)
                """,
            UUID.fromString(session.getId()),
            UUID.fromString(session.getResumeId()),
            session.getJdText(),
            session.getCurrentIndex(),
            Timestamp.from(session.getCreatedAt()),
            Timestamp.from(session.getCreatedAt()),
            Timestamp.from(session.getCreatedAt()));

        for (int i = 0; i < session.getQuestions().size(); i++) {
            Question q = session.getQuestions().get(i);
            jdbcTemplate.update("""
                    INSERT INTO interview_questions(id, session_id, sort_order, content, created_at, updated_at)
                    VALUES (?::uuid, ?::uuid, ?, ?, ?, ?)
                    """,
                UUID.fromString(q.getId()),
                UUID.fromString(session.getId()),
                i + 1,
                q.getContent(),
                Timestamp.from(session.getCreatedAt()),
                Timestamp.from(session.getCreatedAt()));
        }
    }

    public InterviewSession findById(String sessionId) {
        InterviewSession session = jdbcTemplate.query("""
                SELECT id, resume_id, jd_text, current_index, created_at
                FROM interview_sessions
                WHERE id = ?::uuid
                """,
            ps -> ps.setObject(1, UUID.fromString(sessionId)),
            rs -> {
                if (!rs.next()) {
                    return null;
                }
                InterviewSession s = new InterviewSession();
                s.setId(rs.getObject("id", UUID.class).toString());
                s.setResumeId(rs.getObject("resume_id", UUID.class).toString());
                s.setJdText(rs.getString("jd_text"));
                s.setCurrentIndex(rs.getInt("current_index"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                s.setCreatedAt(createdAt == null ? Instant.now() : createdAt.toInstant());
                return s;
            });
        if (session == null) {
            return null;
        }

        List<Question> questions = jdbcTemplate.query("""
                SELECT id, content
                FROM interview_questions
                WHERE session_id = ?::uuid
                ORDER BY sort_order ASC
                """,
            (rs, rowNum) -> {
                Question q = new Question();
                q.setId(rs.getObject("id", UUID.class).toString());
                q.setContent(rs.getString("content"));
                return q;
            },
            UUID.fromString(sessionId));
        session.setQuestions(questions);

        List<Answer> answers = jdbcTemplate.query("""
                SELECT id, question_id, answer_text, score, feedback
                FROM interview_answers
                WHERE session_id = ?::uuid
                ORDER BY created_at ASC
                """,
            (rs, rowNum) -> {
                Answer a = new Answer();
                a.setId(rs.getObject("id", UUID.class).toString());
                a.setQuestionId(rs.getObject("question_id", UUID.class).toString());
                a.setContent(rs.getString("answer_text"));
                a.setScore(rs.getInt("score"));
                a.setFeedback(rs.getString("feedback"));
                return a;
            },
            UUID.fromString(sessionId));
        session.setAnswers(answers);
        return session;
    }

    @Transactional
    public void appendAnswerAndAdvance(String sessionId, Answer answer, int nextIndex) {
        jdbcTemplate.update("""
                INSERT INTO interview_answers(id, session_id, question_id, answer_text, score, feedback, attempt_no, created_at, updated_at)
                VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, ?, 1, now(), now())
                """,
            UUID.fromString(answer.getId()),
            UUID.fromString(sessionId),
            UUID.fromString(answer.getQuestionId()),
            answer.getContent(),
            answer.getScore(),
            answer.getFeedback());

        jdbcTemplate.update("""
                UPDATE interview_sessions
                SET current_index = ?, updated_at = now()
                WHERE id = ?::uuid
                """,
            nextIndex,
            UUID.fromString(sessionId));
    }

    @Transactional
    public void appendAnswerAdvanceAndQuestion(String sessionId, Answer answer, int nextIndex, Question nextQuestion, int sortOrder) {
        jdbcTemplate.update("""
                INSERT INTO interview_answers(id, session_id, question_id, answer_text, score, feedback, attempt_no, created_at, updated_at)
                VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, ?, 1, now(), now())
                """,
            UUID.fromString(answer.getId()),
            UUID.fromString(sessionId),
            UUID.fromString(answer.getQuestionId()),
            answer.getContent(),
            answer.getScore(),
            answer.getFeedback());

        jdbcTemplate.update("""
                INSERT INTO interview_questions(id, session_id, sort_order, content, created_at, updated_at)
                VALUES (?::uuid, ?::uuid, ?, ?, now(), now())
                """,
            UUID.fromString(nextQuestion.getId()),
            UUID.fromString(sessionId),
            sortOrder,
            nextQuestion.getContent());

        jdbcTemplate.update("""
                UPDATE interview_sessions
                SET current_index = ?, updated_at = now()
                WHERE id = ?::uuid
                """,
            nextIndex,
            UUID.fromString(sessionId));
    }
}
