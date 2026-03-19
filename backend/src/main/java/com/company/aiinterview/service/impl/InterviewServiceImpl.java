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
import com.company.aiinterview.repository.InterviewSessionRepository;
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
    private static final int MAX_QUESTIONS = 5;

    private final InterviewSessionRepository interviewSessionRepository;
    private final ResumeService resumeService;
    private final AiService aiService;

    public InterviewServiceImpl(InterviewSessionRepository interviewSessionRepository, ResumeService resumeService, AiService aiService) {
        this.interviewSessionRepository = interviewSessionRepository;
        this.resumeService = resumeService;
        this.aiService = aiService;
    }

    @Override
    public InterviewStartResponse start(InterviewStartRequest request) {
        Resume resume = resumeService.getById(request.getResumeId());
        String openingQuestion = aiService.generateOpeningQuestion(resume.getText(), request.getJdText());

        InterviewSession session = new InterviewSession();
        session.setId(IdUtil.newId());
        session.setResumeId(resume.getId());
        session.setJdText(request.getJdText());
        session.setCreatedAt(Instant.now());
        session.setCurrentIndex(0);

        Question firstQuestion = new Question();
        firstQuestion.setId(IdUtil.newId());
        firstQuestion.setContent(openingQuestion);
        session.getQuestions().add(firstQuestion);

        interviewSessionRepository.create(session);

        InterviewStartResponse resp = new InterviewStartResponse();
        resp.setSessionId(session.getId());
        resp.setFirstQuestion(toQuestionVO(firstQuestion));
        return resp;
    }

    @Override
    public AnswerFeedbackVO answer(AnswerRequest request) {
        InterviewSession session = interviewSessionRepository.findById(request.getSessionId());
        if (session == null) {
            throw new ApiException(ErrorCode.NOT_FOUND, "session not found");
        }

        Question current = session.getQuestions().stream()
            .filter(q -> q.getId().equals(request.getQuestionId()))
            .findFirst()
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "question not found"));

        Resume resume = resumeService.getById(session.getResumeId());
        int answeredCount = session.getAnswers().size() + 1;
        boolean allowNext = answeredCount < MAX_QUESTIONS;

        AiService.AiTurn turn = aiService.evaluateAndGenerateNext(
                resume.getText(),
                session.getJdText(),
                buildHistoryText(session),
                current.getContent(),
                request.getAnswer(),
                allowNext
        );

        Answer answer = new Answer();
        answer.setId(IdUtil.newId());
        answer.setQuestionId(current.getId());
        answer.setContent(request.getAnswer());
        answer.setScore(turn.score());
        answer.setFeedback(turn.feedback());

        int nextIndex = session.getCurrentIndex() + 1;

        AnswerFeedbackVO vo = new AnswerFeedbackVO();
        vo.setScore(turn.score());
        vo.setFeedback(turn.feedback());

        String nextQuestionText = safeTrim(turn.nextQuestion());
        boolean hasNextQuestion = allowNext && !turn.shouldEnd() && !nextQuestionText.isBlank();

        if (hasNextQuestion) {
            Question nextQuestion = new Question();
            nextQuestion.setId(IdUtil.newId());
            nextQuestion.setContent(nextQuestionText);

            interviewSessionRepository.appendAnswerAdvanceAndQuestion(
                    session.getId(),
                    answer,
                    nextIndex,
                    nextQuestion,
                    session.getQuestions().size() + 1
            );
            vo.setNextQuestion(toQuestionVO(nextQuestion));
        } else {
            interviewSessionRepository.appendAnswerAndAdvance(session.getId(), answer, nextIndex);
        }

        return vo;
    }

    @Override
    public InterviewResultResponse result(String sessionId) {
        InterviewSession session = interviewSessionRepository.findById(sessionId);
        if (session == null) {
            throw new ApiException(ErrorCode.NOT_FOUND, "session not found");
        }

        Resume resume = resumeService.getById(session.getResumeId());

        InterviewResultResponse resp = new InterviewResultResponse();
        resp.setSessionId(sessionId);

        OptionalDouble avg = session.getAnswers().stream().mapToInt(Answer::getScore).average();
        resp.setAverageScore(avg.orElse(0));
        resp.setOverallAdvice(aiService.summarize(resume.getText(), session.getJdText(), buildHistoryText(session)));

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

    private String buildHistoryText(InterviewSession session) {
        StringBuilder sb = new StringBuilder();
        for (Answer answer : session.getAnswers()) {
            Question question = session.getQuestions().stream()
                    .filter(q -> q.getId().equals(answer.getQuestionId()))
                    .findFirst()
                    .orElse(null);
            if (question == null) {
                continue;
            }
            sb.append("Q: ").append(question.getContent()).append('\n');
            sb.append("A: ").append(answer.getContent()).append('\n');
            sb.append("Score: ").append(answer.getScore()).append('\n');
            sb.append("Feedback: ").append(answer.getFeedback()).append("\n\n");
        }
        return sb.toString().trim();
    }

    private String safeTrim(String text) {
        return text == null ? "" : text.trim();
    }

    private QuestionVO toQuestionVO(Question q) {
        QuestionVO vo = new QuestionVO();
        vo.setId(q.getId());
        vo.setContent(q.getContent());
        return vo;
    }
}
