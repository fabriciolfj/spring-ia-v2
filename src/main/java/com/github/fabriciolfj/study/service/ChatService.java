package com.github.fabriciolfj.study.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final Resource prompt;

    public ChatService(@Value("classpath:/prompts/template.st") final Resource prompt) {
        this.prompt = prompt;
    }

    public void execute(final String question) {


    }
}
