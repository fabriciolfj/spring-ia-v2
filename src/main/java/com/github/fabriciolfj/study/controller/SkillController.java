package com.github.fabriciolfj.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
public class SkillController {

    private final ChatClient chatClientSkill;

    @PostMapping
    ResponseEntity<ChatbotResponse> chat(@RequestBody ChatbotRequest chatbotRequest) {
        String answer = chatClientSkill
                .prompt()
                .user(chatbotRequest.question)
                .call()
                .content();
        return ResponseEntity.ok(new ChatbotResponse(answer));
    }

    record ChatbotRequest(String question) {}

    record ChatbotResponse(String answer) {}
}
