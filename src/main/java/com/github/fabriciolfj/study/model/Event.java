package com.github.fabriciolfj.study.model;

import com.github.fabriciolfj.study.service.AskUserQuestionWebService;
import org.springaicommunity.agent.tools.AskUserQuestionTool;

import java.util.List;

public record Event(String sessionId,
                    AskUserQuestionWebService.Status status,
                    List<AskUserQuestionTool.Question> questions,
                    String answer) {}
