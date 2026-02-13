package org.eam.tinybank.config;

import org.eam.tinybank.tool.AccountTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("ai")
public class AiConfiguration {

    @Bean
    PromptChatMemoryAdvisor promptChatMemoryAdvisor(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return PromptChatMemoryAdvisor.builder(MessageWindowChatMemory.builder()
                                                   .chatMemoryRepository(jdbcChatMemoryRepository)
                                                   .build())
            .build();
    }

    @Bean
    QuestionAnswerAdvisor qaAdvisor(VectorStore vectorStore) {
        return QuestionAnswerAdvisor.builder(vectorStore)
            .searchRequest(SearchRequest.builder()
                               .similarityThreshold(0.1d)
                               .topK(10)
                               .build())
            .build();
    }

    @Bean
    ChatClient bankClient(ChatModel chatModel,
                          PromptChatMemoryAdvisor promptChatMemoryAdvisor,
                          AccountTool accountTool,
                          QuestionAnswerAdvisor qaAdvisor) {
        var systemPrompt = """
            You are an AI bank assistant. Users will inquiry about their account history, or ask to create new accounts.
            To create an account, a user must provide an email. When calling a tool, analyze tool response, and print it too.
            """;

        return ChatClient.builder(chatModel)
            .defaultTools(accountTool)
            .defaultAdvisors(promptChatMemoryAdvisor, qaAdvisor)
            .defaultSystem(systemPrompt)
            .build();
    }

}
