package com.github.fabriciolfj.study.controller;

import lombok.RequiredArgsConstructor;
import org.springaicommunity.agent.tools.FileSystemTools;
import org.springaicommunity.agent.tools.ShellTools;
import org.springaicommunity.agent.tools.SkillsTool;
import org.springaicommunity.agent.utils.Skills;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/skill")
public class SkillController {

    private final ChatClient chatClientSkill;
    private final ChatClient.Builder builder;

    public SkillController(ChatClient chatClientSkill, ChatClient.Builder builder) {
        this.chatClientSkill = chatClientSkill;
        this.builder = builder;
    }

    @PostMapping
    ResponseEntity<ChatbotResponse> chat(@RequestBody ChatbotRequest chatbotRequest) {
        String answer = chatClientSkill
                .prompt()
                .user(chatbotRequest.question)
                .call()
                .content();
        return ResponseEntity.ok(new ChatbotResponse(answer));
    }

    @PostMapping("/v2")
    ResponseEntity<ChatbotResponse> chatV2(@RequestBody ChatbotRequest chatbotRequest) {
        final ChatClient chatClient =
                builder.defaultTools(
                        SkillsTool.builder()
                                .addSkillsDirectory(".claude/skills")
                                .build())
                        .defaultTools(FileSystemTools.builder().build())
                        .defaultTools(ShellTools.builder().build())
                        .build();

        String answer = chatClient
                .prompt()
                .user(chatbotRequest.question)
                .call()
                .content();

        return ResponseEntity.ok(new ChatbotResponse(answer));
    }

    record ChatbotRequest(String question) {}

    record ChatbotResponse(String answer) {}
}
