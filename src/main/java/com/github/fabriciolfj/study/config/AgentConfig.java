package com.github.fabriciolfj.study.config;

import org.springaicommunity.agent.advisors.AutoMemoryToolsAdvisor;
import org.springaicommunity.agent.common.task.subagent.SubagentType;
import org.springaicommunity.agent.tools.*;
import org.springaicommunity.agent.tools.task.TaskTool;
import org.springaicommunity.agent.tools.task.claude.ClaudeSubagentReferences;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
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

    @Value("classpath:/prompts/AUTO_MEMORY_TOOLS_SYSTEM_PROMPT.md")
    Resource memorySystemPromptAutoMemoryTools;

    @Value("classpath:/prompts/AUTO_MEMORY_FILESYSTEM_TOOLS_SYSTEM_PROMPT.md")
    Resource memorySystemPromptFilesystemTools;

    @Value("${agent.memory.dir}")
    private String memoryDirectory;

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

    //@Bean
    ChatClient chatClientWithoutAutoMemoryTools(ChatModel chatModel) {
        return ChatClient
                .builder(chatModel)
                .defaultSystem(p -> p
                        .text(memorySystemPromptFilesystemTools)
                        .param("MEMORIES_ROOT_DIERCTORY", memoryDirectory))   // tells the agent where to write
                .defaultTools(
                        ShellTools.builder()
                                .build(),         // Bash — mkdir, ls, etc.
                        FileSystemTools.builder()
                                .build())    // Read, Write, Edit — memory file operations
                .defaultAdvisors(ToolCallingAdvisor.builder()
                        .build())
                .build();
    }

   // @Bean
    ChatClient chatClientWithMoreSystemPrompt(ChatModel chatModel) {
        return ChatClient
                .builder(chatModel)
                .defaultSystem(p -> p
                        .text(memorySystemPromptAutoMemoryTools)
                        .param("MEMORIES_ROOT_DIERCTORY", memoryDirectory))
                .defaultTools(
                        AutoMemoryTools.builder()
                                .memoriesDir(memoryDirectory)
                                .build(),
                        TodoWriteTool.builder()
                                .build())
                .defaultAdvisors(ToolCallingAdvisor.builder()
                        .build())
                .build();
    }

    @Bean
    ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(AutoMemoryToolsAdvisor.builder()
                        .memoriesRootDirectory(memoryDirectory).build(),
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder()
                                //.maxMessages(10000)
                                .build())
                                .build(),
                        ToolCallingAdvisor.builder()
                                .build())
                .build();
    }

    //@Bean
    ChatClient chatClientSkill(ChatModel chatModel) {
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