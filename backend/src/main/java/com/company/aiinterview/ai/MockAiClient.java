package com.company.aiinterview.ai;

public class MockAiClient implements AiClient {
    @Override
    public String generateOpeningQuestionJson(String resumeText, String jdText) {
        int level = Math.abs((safe(resumeText) + safe(jdText)).hashCode()) % 3;
        String question;
        if (level == 0) {
            question = "请介绍一个与你当前岗位描述最相关的项目，并重点说明你的核心贡献。";
        } else if (level == 1) {
            question = "结合你的简历，哪个技术决策最能体现你的工程判断力？为什么？";
        } else {
            question = "如果你加入这个岗位，基于你过去的经历，你认为自己前 30 天最能交付什么价值？";
        }
        return "{\"question\":\"" + escape(question) + "\"}";
    }

    @Override
    public String evaluateAnswerJson(String resumeText, String jdText, String historyText, String question, String answer, boolean allowNextQuestion) {
        int base = 65 + Math.min(30, safe(answer).length() / 12);
        int score = Math.max(0, Math.min(100, base));
        String feedback = "建议补充可量化结果、技术取舍过程，以及你个人承担的关键职责。";
        boolean shouldEnd = !allowNextQuestion;
        String next = shouldEnd ? "" : "请继续讲一个该项目中最棘手的问题，并说明你是如何定位和解决它的。";

        return "{\"score\":" + score
                + ",\"feedback\":\"" + escape(feedback) + "\""
                + ",\"shouldEnd\":" + shouldEnd
                + ",\"nextQuestion\":\"" + escape(next) + "\"}";
    }

    @Override
    public String summarizeJson(String resumeText, String jdText, String historyText) {
        String advice = "建议使用 STAR 结构回答，强化量化成果，并更清晰地体现技术深度与业务价值。";
        return "{\"overallAdvice\":\"" + escape(advice) + "\"}";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String escape(String value) {
        return safe(value).replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
