package com.company.aiinterview.repository;

import com.company.aiinterview.domain.entity.InterviewSession;
import com.company.aiinterview.domain.entity.Resume;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryStore {
    private final Map<String, Resume> resumes = new ConcurrentHashMap<>();
    private final Map<String, InterviewSession> sessions = new ConcurrentHashMap<>();

    public Map<String, Resume> getResumes() {
        return resumes;
    }

    public Map<String, InterviewSession> getSessions() {
        return sessions;
    }
}
