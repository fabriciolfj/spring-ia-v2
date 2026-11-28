package com.github.fabriciolfj.study.controller;


import com.github.fabriciolfj.study.dto.ReportRequest;
import com.github.fabriciolfj.study.dto.ReportResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.session.advisor.SessionAdvisors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testtodo")
public class ChatClientTodoWriteController {

    private final ChatClient chatClient;

    public ChatClientTodoWriteController(@Qualifier("toolWrite") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping
    public ReportResponse getPrompt(@RequestHeader("sessionId") final String sessionId,
                                    @RequestBody final ReportRequest reportRequest) {

        var result = chatClient.prompt()
                .user(reportRequest.prompt())
                .advisors(SessionAdvisors.session(sessionId))
                .call()
                .content();

        return new ReportResponse(result);

    }
}
