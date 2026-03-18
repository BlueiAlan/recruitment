package com.company.aiinterview.ai;

public class PromptTemplates {
    private PromptTemplates() {
    }

    public static final String QUESTION_PROMPT = """
你是面试官。根据简历与JD生成5个技术/项目/行为问题。输出JSON：{\"questions\":[\"q1\",\"q2\"]}
简历: {{resume}}
JD: {{jd}}
""";

    public static final String SCORE_PROMPT = """
你是面试官。给回答打分0-100并给改进建议。输出JSON：{\"score\":80,\"feedback\":\"建议\"}
问题: {{question}}
回答: {{answer}}
""";
}
