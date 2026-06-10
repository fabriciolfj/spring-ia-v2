package com.github.fabriciolfj.study.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OrchestratorService {

    private final ChatClient orchestratorChatClient;

    public OrchestratorService(ChatClient orchestratorChatClient) {
        this.orchestratorChatClient = orchestratorChatClient;
    }

    public String ask(String userMessage) {
        return orchestratorChatClient.prompt(userMessage).call().content();
    }
}