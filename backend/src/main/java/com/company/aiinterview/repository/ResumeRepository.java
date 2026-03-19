package com.company.aiinterview.repository;

import com.company.aiinterview.domain.entity.Resume;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ResumeRepository {
    private final JdbcTemplate jdbcTemplate;

    public ResumeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Resume resume, String sourceType) {
        jdbcTemplate.update("""
                INSERT INTO resumes(id, source_type, file_name, content_text, created_at, updated_at)
                VALUES (?::uuid, ?::resume_source, ?, ?, ?, ?)
                """,
            UUID.fromString(resume.getId()),
            sourceType,
            resume.getFileName(),
            resume.getText(),
            Timestamp.from(resume.getCreatedAt()),
            Timestamp.from(resume.getCreatedAt()));
    }

    public Optional<Resume> findById(String id) {
        return jdbcTemplate.query("""
                SELECT id, file_name, content_text, created_at
                FROM resumes
                WHERE id = ?::uuid
                """,
            ps -> ps.setObject(1, UUID.fromString(id)),
            rs -> {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Resume resume = new Resume();
                resume.setId(rs.getObject("id", UUID.class).toString());
                resume.setFileName(rs.getString("file_name"));
                resume.setText(rs.getString("content_text"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                resume.setCreatedAt(createdAt == null ? Instant.now() : createdAt.toInstant());
                return Optional.of(resume);
            });
    }
}
