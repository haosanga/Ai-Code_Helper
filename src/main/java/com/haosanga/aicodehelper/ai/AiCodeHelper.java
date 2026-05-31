package com.haosanga.aicodehelper.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
public class AiCodeHelper {

    @Resource
    private ChatModel chatModel;

    private static final Map<String, String> SYSTEM_PROMPTS = new LinkedHashMap<>();

    static {
        SYSTEM_PROMPTS.put("generate", """
                你是一个专业的代码生成助手。根据用户的需求描述，生成高质量、可运行的代码。
                要求：
                - 包含必要的导入语句
                - 代码风格清晰规范
                - 标明代码语言
                - 提供简要的使用说明
                """);
        SYSTEM_PROMPTS.put("explain", """
                你是一个代码解释助手。请详细解释用户提交的代码，包括：
                - 代码的整体功能和目的
                - 关键部分的逻辑和工作原理
                - 使用的算法、设计模式或框架特性
                - 输入输出说明
                - 核心代码的逐行/逐段解释
                """);
        SYSTEM_PROMPTS.put("review", """
                你是一个代码审查助手。请从以下方面全面审查代码：
                - 代码质量和风格（是否符合规范、可读性）
                - 潜在 Bug 和边界情况
                - 性能问题和优化建议
                - 安全漏洞
                - 可维护性和扩展性
                - 测试覆盖建议
                最后给出整体评价和改进优先级。
                """);
        SYSTEM_PROMPTS.put("refactor", """
                你是一个代码重构助手。针对用户提供的代码，请：
                - 分析当前代码存在的问题（坏味道）
                - 给出具体的重构建议
                - 提供重构后的完整代码
                - 说明重构后的改进之处（可读性、性能、可维护性等）
                """);
        SYSTEM_PROMPTS.put("fix", """
                你是一个 Bug 修复助手。请帮助分析并修复代码中的 Bug：
                - 分析 Bug 的根本原因
                - 提供具体的修复方案
                - 给出修复后的完整代码
                - 解释修复原理和为什么这样可以解决问题
                """);
        SYSTEM_PROMPTS.put("qa", """
                你是一个通用编程助手。请回答用户提出的编程相关问题。
                要求：
                - 提供准确、详细的解答
                - 必要时附上代码示例
                - 解释相关概念和原理
                - 提供最佳实践建议
                """);
    }

    public String chat(String mode, String userMessage) {
        String systemPrompt = SYSTEM_PROMPTS.getOrDefault(mode, SYSTEM_PROMPTS.get("qa"));

        ChatRequest request = ChatRequest.builder()
                .messages(
                        SystemMessage.from(systemPrompt),
                        UserMessage.from(userMessage)
                )
                .build();

        AiMessage response = chatModel.chat(request).aiMessage();
        return response.text();
    }
}
