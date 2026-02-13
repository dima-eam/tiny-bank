package org.eam.tinybank.controller;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;

@Profile("ai")
@RestController
@RequestMapping("api/chat")
@Log4j2
@AllArgsConstructor
public class ChatController {

    private static final Cache<String, String> SESSION_CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.ofMinutes(5))
        .build();

    private final ChatClient chatClient;

    @SneakyThrows
    @PostMapping("inquire")
    String create(@RequestParam String prompt, WebSession session) {
        log.info("General inquiry: prompt={}", prompt);

        var response = chatClient.prompt()
            .user(prompt)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, session.getId()))
            .call()
            .content();
        log.info("AI response: message={}", response);

        return response;
    }

    @SneakyThrows
    @GetMapping("history")
    String history(@RequestParam String email, WebSession session) {
        var sessionId = SESSION_CACHE.get(email, session::getId);
        log.info("Account history: email={}, sessionId={}", email, sessionId);

        var response = chatClient.prompt()
            .user("Retrieve account history for email %s".formatted(email))
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
            .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "email == '%s'".formatted(email)))
            .call()
            .content();
        log.info("AI response: sessionId={}, message={}", sessionId, response);

        return response;
    }

}