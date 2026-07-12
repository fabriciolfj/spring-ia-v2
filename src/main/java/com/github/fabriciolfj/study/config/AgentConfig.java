package com.github.fabriciolfj.study.config;

import org.springaicommunity.agent.common.task.subagent.SubagentType;
import org.springaicommunity.agent.tools.FileSystemTools;
import org.springaicommunity.agent.tools.ShellTools;
import org.springaicommunity.agent.tools.SkillsTool;
import org.springaicommunity.agent.tools.task.TaskTool;
import org.springaicommunity.agent.tools.task.claude.ClaudeSubagentReferences;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springaicommunity.agent.tools.task.claude.ClaudeSubagentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;

import java.util.List;

@Configuration
public class AgentConfig {

    @Value("${agent.tasks.paths}")
    private List<Resource> agentPaths;

   // @Bean
    //@Primary
    public ChatClient orchestratorChatClient(ChatClient.Builder chatClientBuilder) {

        SubagentType claudeType = ClaudeSubagentType.builder()
                .chatClientBuilder("default", chatClientBuilder.clone())
                .build();

        ToolCallback taskTool = TaskTool.builder()
                .subagentReferences(ClaudeSubagentReferences.fromResources(agentPaths))
                .subagentTypes(claudeType)
                .build();

        return chatClientBuilder.clone().defaultTools(taskTool)
                .build();
    }

    @Bean
    ChatClient chatClient(ChatModel chatModel) {
        var skillRootDirectory = ".claude/skills";
        return ChatClient
                .builder(chatModel)
                .defaultTools(SkillsTool
                        .builder()
                        .addSkillsDirectory(skillRootDirectory)
                        .build(),
                        FileSystemTools
                                .builder()
                                .allowedDirectory(skillRootDirectory)
                                .build(),
                        ShellTools.builder().build())
                .build();
    }
}