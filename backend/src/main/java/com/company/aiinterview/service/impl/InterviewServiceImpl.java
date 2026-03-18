package com.company.aiinterview.service.impl;

import com.company.aiinterview.common.ErrorCode;
import com.company.aiinterview.domain.dto.AnswerRequest;
import com.company.aiinterview.domain.dto.InterviewStartRequest;
import com.company.aiinterview.domain.entity.Answer;
import com.company.aiinterview.domain.entity.InterviewSession;
import com.company.aiinterview.domain.entity.Question;
import com.company.aiinterview.domain.entity.Resume;
import com.company.aiinterview.domain.vo.AnswerFeedbackVO;
import com.company.aiinterview.domain.vo.InterviewResultResponse;
import com.company.aiinterview.domain.vo.InterviewStartResponse;
import com.company.aiinterview.domain.vo.QuestionVO;
import com.company.aiinterview.exception.ApiException;
import com.company.aiinterview.repository.InMemoryStore;
import com.company.aiinterview.service.AiService;
import com.company.aiinterview.service.InterviewService;
import com.company.aiinterview.service.ResumeService;
import com.company.aiinterview.util.IdUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
public class InterviewServiceImpl implements InterviewService {
    private final InMemoryStore store;
    private final ResumeService resumeService;
    private final AiService aiService;

    public InterviewServiceImpl(InMemoryStore store, ResumeService resumeService, AiService aiService) {
        this.store = store;
        this.resumeService = resumeService;
        this.aiService = aiService;
    }

    @Override
    public InterviewStartResponse start(InterviewStartRequest request) {
        Resume resume = resumeService.getById(request.getResumeId());
        List<String> questions = aiService.generateQuestions(resume.getText(), request.getJdText());

        InterviewSession session = new InterviewSession();
        session.setId(IdUtil.newId());
        session.setResumeId(resume.getId());
        session.setJdText(request.getJdText());
        session.setCreatedAt(Instant.now());
        session.setCurrentIndex(0);

        for (String q : questions) {
            Question question = new Question();
            question.setId(IdUtil.newId());
            question.setContent(q);
            session.getQuestions().add(question);
        }

        store.getSessions().put(session.getId(), session);

        InterviewStartResponse resp = new InterviewStartResponse();
        resp.setSessionId(session.getId());
        resp.setFirstQuestion(toQuestionVO(session.getQuestions().get(0)));
        return resp;
    }

    @Override
    public AnswerFeedbackVO answer(AnswerRequest request) {
        InterviewSession session = store.getSessions().get(request.getSessionId());
        if (session == null) {
            throw new ApiException(ErrorCode.NOT_FOUND, "session not found");
        }

        Question current = session.getQuestions().stream()
                .filter(q -> q.getId().equals(request.getQuestionId()))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "question not found"));

        AiService.AiScore score = aiService.scoreAnswer(current.getContent(), request.getAnswer());

        Answer answer = new Answer();
        answer.setId(IdUtil.newId());
        answer.setQuestionId(current.getId());
        answer.setContent(request.getAnswer());
        answer.setScore(score.score());
        answer.setFeedback(score.feedback());
        session.getAnswers().add(answer);

        int nextIndex = session.getCurrentIndex() + 1;
        session.setCurrentIndex(nextIndex);

        AnswerFeedbackVO vo = new AnswerFeedbackVO();
        vo.setScore(score.score());
        vo.setFeedback(score.feedback());

        if (nextIndex < session.getQuestions().size()) {
            vo.setNextQuestion(toQuestionVO(session.getQuestions().get(nextIndex)));
        }
        return vo;
    }

    @Override
    public InterviewResultResponse result(String sessionId) {
        InterviewSession session = store.getSessions().get(sessionId);
        if (session == null) {
            throw new ApiException(ErrorCode.NOT_FOUND, "session not found");
        }

        InterviewResultResponse resp = new InterviewResultResponse();
        resp.setSessionId(sessionId);

        OptionalDouble avg = session.getAnswers().stream().mapToInt(Answer::getScore).average();
        resp.setAverageScore(avg.orElse(0));

        resp.setOverallAdvice("建议围绕STAR法补充场景、行动和结果，并强化量化成果。" );

        List<InterviewResultResponse.AnswerSummary> summaries = session.getAnswers().stream().map(a -> {
            InterviewResultResponse.AnswerSummary s = new InterviewResultResponse.AnswerSummary();
            Question q = session.getQuestions().stream().filter(x -> x.getId().equals(a.getQuestionId())).findFirst().orElse(null);
            s.setQuestion(q == null ? "" : q.getContent());
            s.setAnswer(a.getContent());
            s.setScore(a.getScore());
            s.setFeedback(a.getFeedback());
            return s;
        }).collect(Collectors.toList());

        resp.setAnswers(summaries);
        return resp;
    }

    private QuestionVO toQuestionVO(Question q) {
        QuestionVO vo = new QuestionVO();
        vo.setId(q.getId());
        vo.setContent(q.getContent());
        return vo;
    }
}
