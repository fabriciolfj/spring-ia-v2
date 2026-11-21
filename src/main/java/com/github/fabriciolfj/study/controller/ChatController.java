package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.model.Event;
import com.github.fabriciolfj.study.service.AskUserQuestionWebService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat-in-memory")
public class ChatController {

    private final ChatClient chatClient;
    private final AskUserQuestionWebService askUserQuestionWebService;

    public ChatController(ChatClient chatClient, AskUserQuestionWebService askUserQuestionWebService) {
        this.chatClient = chatClient;
        this.askUserQuestionWebService = askUserQuestionWebService;
    }


    @PostMapping
    ResponseEntity<String> chat(@RequestBody String question, @RequestHeader("X-Conversation-ID") String conversationId) {
        final String answer = chatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(question)
                .call()
                .content();

        return ResponseEntity.ok(answer);
    }

    @PostMapping("/asktool")
    ResponseEntity<Event> chatAskUserTool(@RequestBody final String question) {
        return ResponseEntity.ok(askUserQuestionWebService.start(question));
    }

    @PostMapping("/asktool/{sessionId}/answers")
    ResponseEntity<Event> answerAskUserTool(@PathVariable String sessionId,
                                            @RequestBody Map<String, String> answers) {
        return ResponseEntity.ok(askUserQuestionWebService.answer(sessionId, answers));
    }

}
