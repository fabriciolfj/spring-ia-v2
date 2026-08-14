package com.github.fabriciolfj.study.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat-in-memory")
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    @PostMapping
    ResponseEntity<String> chat(@RequestBody String question, @RequestHeader("X-Conversation-ID") String conversationId) {
        final String answer = chatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(question)
                .call()
                .content();

        return ResponseEntity.ok(answer);
    }
}
