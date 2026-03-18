package com.company.aiinterview.ai;

import java.util.List;

public class MockAiClient implements AiClient {
    @Override
    public String generateQuestionsJson(String resumeText, String jdText) {
        List<String> questions = List.of(
                "请简要介绍你的核心技术栈与最强项。",
                "结合JD，你最匹配的经验是什么？举例说明。",
                "请描述一次你解决线上问题的过程。",
                "你如何与产品或业务方协作推进项目？",
                "你最近一次性能优化的案例是什么？"
        );
        StringBuilder sb = new StringBuilder();
        sb.append("{\"questions\":[");
        for (int i = 0; i < questions.size(); i++) {
            sb.append("\"").append(questions.get(i)).append("\"");
            if (i < questions.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    @Override
    public String scoreAnswerJson(String question, String answer) {
        int score = Math.min(95, Math.max(60, 70 + answer.length() / 20));
        String feedback = "建议补充量化成果、技术细节和你的个人贡献。";
        return "{\"score\":" + score + ",\"feedback\":\"" + feedback + "\"}";
    }
}
