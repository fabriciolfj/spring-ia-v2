# Spring AI Agent System - User Guide

## Overview

This application is a Spring Boot-based AI agent orchestration system that integrates with Anthropic's Claude AI models. It provides intelligent document processing, code review, and technical documentation generation capabilities through a multi-agent architecture.

## Table of Contents

1. [Architecture](#architecture)
2. [Features](#features)
3. [Prerequisites](#prerequisites)
4. [Configuration](#configuration)
5. [Running the Application](#running-the-application)
6. [API Endpoints](#api-endpoints)
7. [Agent System](#agent-system)
8. [Skills](#skills)
9. [Use Cases](#use-cases)
10. [Troubleshooting](#troubleshooting)

## Architecture

### Technology Stack
- **Java**: Version 25
- **Spring Boot**: 4.1.0
- **Spring AI**: 2.0.0
- **AI Model**: Anthropic Claude Sonnet 4.5
- **Build Tool**: Gradle
- **Additional Libraries**: 
  - Spring AI Agent Utils (0.10.0)
  - Lombok
  - Jakarta Validation

### Components

```
┌─────────────────────────────────────────────────┐
│            REST Controllers                     │
├─────────────────────────────────────────────────┤
│  - SkillController      - AgentSkillsController │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│              Service Layer                      │
├─────────────────────────────────────────────────┤
│  - OrchestratorService  - AgentSkillsService    │
│  - MonthlySalesService                          │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│           ChatClient / ChatModel                │
├─────────────────────────────────────────────────┤
│  - Skills Tools    - FileSystem Tools           │
│  - Shell Tools     - Task Tools                 │
└─────────────────────────────────────────────────┘
```

### Layer Responsibilities

1. **Controllers**: Handle HTTP requests and responses
2. **Services**: Business logic and AI orchestration
3. **Configuration**: Bean definitions and AI tool setup
4. **Models**: Data structures (Records)
5. **Agents**: Specialized AI agents for specific tasks

## Features

### Core Capabilities

1. **AI-Powered Code Review**
   - Automated code quality analysis
   - Security vulnerability detection
   - Best practice recommendations

2. **Technical Documentation Generation**
   - Architecture documentation
   - API documentation
   - Developer guides

3. **Document Generation**
   - PDF reports
   - Excel spreadsheets
   - PowerPoint presentations
   - Word documents

4. **Article Summarization**
   - Extract key points from articles
   - Generate concise summaries
   - TL;DR generation

5. **Data Analysis**
   - Monthly sales reporting
   - Custom data visualization

## Prerequisites

### System Requirements
- Java Development Kit (JDK) 25 or higher
- Gradle (wrapper included)
- Internet connection for AI model access

### Required Credentials
- Anthropic API Key

## Configuration

### Environment Variables

Set the following environment variable:

```bash
export API_KEY=your_anthropic_api_key_here
```

### Application Configuration

The application is configured through `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: study
  ai:
    anthropic:
      api-key: ${API_KEY}
      chat:
        model: claude-sonnet-4-5-20250929

agent:
  tasks:
    paths: classpath:/agents/*.md
```

### Customization

You can modify:
- **Model**: Change the Claude model version in `application.yaml`
- **Max Tokens**: Adjust in `AgentSkillsService` (default: 8192)
- **Agent Definitions**: Modify or add agents in `src/main/resources/agents/`

## Running the Application

### Build the Application

```bash
./gradlew build
```

### Run the Application

```bash
./gradlew bootRun
```

Or using the JAR file:

```bash
java -jar build/libs/study-0.0.1-SNAPSHOT.jar
```

### CommandLineRunner

The application includes a CommandLineRunner that automatically executes on startup:
- Reviews code quality
- Generates technical documentation

Check the console output for results.

## API Endpoints

### 1. Skill Endpoint

**POST** `/skill`

Execute AI operations with skills support.

**Request:**
```json
{
  "question": "Your question or task here"
}
```

**Response:**
```json
{
  "answer": "AI-generated response"
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/skill \
  -H "Content-Type: application/json" \
  -d '{"question": "Summarize the latest Spring Boot features"}'
```

### 2. Report Generation (Commented Out)

**Note**: This endpoint is currently commented out in the code.

**GET** `/agent-skills/report`

Generate reports in various formats (PDF, DOCX, XLSX, PPTX).

**Request:**
```json
{
  "prompt": "Generate a sales report for 2025"
}
```

**Response:**
Binary file download (PDF, DOCX, etc.)

## Agent System

### Available Agents

#### 1. Code Reviewer Agent

**File**: `src/main/resources/agents/code-reviewser.md`

**Purpose**: Expert code review with focus on quality and security

**Capabilities**:
- Analyzes recent code changes via `git diff`
- Reviews code clarity and readability
- Checks naming conventions
- Identifies error handling issues
- Detects security vulnerabilities

**Tools**: Read, Grep, Glob (read-only)

**Usage**: Automatically invoked when code review is requested

#### 2. Documentation Writer Agent

**File**: `src/main/resources/agents/documentation-writer.md`

**Purpose**: Generate technical documentation

**Capabilities**:
- Architecture documentation
- Workflow summaries
- Developer-facing documentation
- Spring Boot application explanations

**Usage**: Invoked for documentation generation tasks

### Agent Configuration

Agents are defined in Markdown files with YAML frontmatter:

```markdown
---
name: agent-name
description: Agent description
tools: Read, Grep, Glob
disallowedTools: Edit, Write
model: sonnet
---

Agent instructions and behavior...
```

## Skills

### Article Summarizer Skill

**Location**: `.claude/skills/article-summarizer/`

**Purpose**: Summarize articles into concise digests

**Workflow**:
1. Receives URL or article content
2. Optionally runs `scripts/fetch_article.py` to retrieve content
3. Extracts main thesis and key points
4. Generates structured summary

**Output Format**:
- TL;DR
- Key Points
- Bottom Line

### Document Generation Skills

Supported formats:
- **DOCX**: Word documents
- **PDF**: PDF reports
- **PPTX**: PowerPoint presentations
- **XLSX**: Excel spreadsheets

## Use Cases

### 1. Code Quality Review

```java
String response = orchestratorService.ask(
    "Review the code quality of the UserService class"
);
```

### 2. Generate Documentation

```java
String response = orchestratorService.ask(
    "Generate technical documentation for the authentication flow"
);
```

### 3. Article Summarization

```bash
curl -X POST http://localhost:8080/skill \
  -H "Content-Type: application/json" \
  -d '{"question": "Summarize the article at https://example.com/article"}'
```

### 4. Sales Report Generation

```java
ReportRequest request = new ReportRequest(
    "Generate a monthly sales report with charts for 2025"
);
AnthropicDocument document = agentSkillsService.genReport(request);
```

### 5. Data Analysis

The application includes sample monthly sales data:
- Product A and B
- 2025 sales figures
- Monthly breakdown

## Data Models

### MonthlySale
```java
record MonthlySale(
    String product,
    int year,
    Month month,
    BigDecimal amount
)
```

### AnthropicDocument
```java
record AnthropicDocument(
    String fileName,
    String mimeType,
    byte[] content
)
```

### ReportRequest
```java
record ReportRequest(@NotNull String prompt)
```

## Troubleshooting

### Common Issues

#### 1. API Key Not Found
**Error**: Missing or invalid API_KEY

**Solution**: 
```bash
export API_KEY=your_actual_anthropic_api_key
```

#### 2. Port Already in Use
**Error**: Port 8080 already in use

**Solution**: Change port in application.yaml:
```yaml
server:
  port: 8081
```

#### 3. Java Version Mismatch
**Error**: Unsupported class file major version

**Solution**: Ensure JDK 25+ is installed and in PATH

#### 4. Agent Files Not Found
**Error**: Cannot load agent definitions

**Solution**: Verify files exist in `src/main/resources/agents/`

#### 5. Skill Execution Fails
**Error**: Skill not found or execution error

**Solution**: Check `.claude/skills/` directory and skill definitions

### Logging

Enable debug logging:
```yaml
logging:
  level:
    com.github.fabriciolfj.study: DEBUG
    org.springframework.ai: DEBUG
```

### Network Issues

If experiencing API timeout:
- Check internet connectivity
- Verify Anthropic API status
- Consider increasing timeout values

## Development Guide

### Adding a New Agent

1. Create a new `.md` file in `src/main/resources/agents/`
2. Define agent metadata in YAML frontmatter
3. Write agent instructions
4. Reference agent in OrchestratorService

Example:
```markdown
---
name: my-agent
description: Custom agent description
tools: Read, Write
model: sonnet
---

Your agent instructions here...
```

### Adding a New Skill

1. Create directory in `.claude/skills/`
2. Add skill definition with frontmatter
3. Implement skill logic
4. Register in ChatClient configuration

### Extending Services

Add new service methods:
```java
@Service
@RequiredArgsConstructor
public class MyService {
    private final ChatClient chatClient;
    
    public String processRequest(String input) {
        return chatClient.prompt(input).call().content();
    }
}
```

## Best Practices

1. **API Key Security**: Never commit API keys to version control
2. **Error Handling**: Implement proper exception handling
3. **Logging**: Use appropriate log levels
4. **Testing**: Write unit and integration tests
5. **Documentation**: Keep this guide updated with changes
6. **Resource Management**: Close resources properly
7. **Validation**: Always validate user input

## Performance Considerations

- **Token Limits**: Monitor token usage to control costs
- **Caching**: Consider caching frequently used responses
- **Async Processing**: Use async for long-running operations
- **Connection Pooling**: Configure HTTP client pool sizes

## Security Notes

- Secure API key storage
- Implement authentication/authorization
- Validate all user inputs
- Monitor file system access
- Audit agent actions
- Use HTTPS in production

## Support and Resources

- **Spring AI Documentation**: https://docs.spring.io/spring-ai/
- **Anthropic API**: https://docs.anthropic.com/
- **Spring Boot**: https://spring.io/projects/spring-boot
- **Project Repository**: Check README.md for links

## Version History

- **0.0.1-SNAPSHOT**: Initial release
  - Multi-agent orchestration
  - Code review capabilities
  - Document generation
  - Article summarization

## License

Check project repository for license information.

---

**Generated**: 2024
**Maintained by**: Project Team
**Last Updated**: See git history
