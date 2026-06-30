package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.dto.ReportRequest;
import com.github.fabriciolfj.study.model.AnthropicDocument;
import com.github.fabriciolfj.study.service.AgentSkillsService;
import com.github.fabriciolfj.study.service.OrchestratorService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/agent-skills")
@Validated
public class AgentSkillsController {
    private final AgentSkillsService agentSkillsService;
    private final OrchestratorService orchestratorService;

    public AgentSkillsController(AgentSkillsService agentSkillsService, OrchestratorService orchestratorService) {
        this.agentSkillsService = agentSkillsService;
        this.orchestratorService = orchestratorService;
    }

    @GetMapping
    public void requestSubAgents() {
        var result = orchestratorService.ask("""
                    Perform the following tasks:
                    - Review the code quality example.
                    - Generate concise technical documentation like user guide.
                    """);

        log.info(result);
    }

    @GetMapping("/report")
    public ResponseEntity<byte[]> genReport(@RequestBody @Valid ReportRequest reportRequest) {
        AnthropicDocument document = agentSkillsService.genReport(reportRequest);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(document.fileName())
                                .build()
                                .toString())
                .body(document.content());
    }
}