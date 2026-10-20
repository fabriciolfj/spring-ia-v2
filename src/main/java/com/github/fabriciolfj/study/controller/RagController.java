package com.github.fabriciolfj.study.controller;

import org.springaicommunity.typesafe.TypeSafeClient;
import org.springaicommunity.typesafe.rag.JevDocumentFilter;
import org.springaicommunity.typesafe.rag.JevDocumentReranker;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final ChatClient chatClient;
    private final RetrievalAugmentationAdvisor modularRag;

    public RagController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore,
                         TypeSafeClient typeSafeClient, TaskExecutor taskExecutor) {
        this.chatClient = chatClientBuilder.clone().build();

        // Separate builder for the LLM-backed stages, so they don't inherit the main client's advisors
        var ragClientBuilder = chatClientBuilder.clone();

        this.modularRag = RetrievalAugmentationAdvisor.builder()
                // 1. Pre-retrieval: rewrite the chatty question into a search-friendly query
                .queryTransformers(RewriteQueryTransformer.builder()
                        .chatClientBuilder(ragClientBuilder)
                        .build())
                // 2. Pre-retrieval: expand it into several diverse queries (original included)
                .queryExpander(MultiQueryExpander.builder()
                        .chatClientBuilder(ragClientBuilder)
                        .numberOfQueries(3)
                        .build())
                // 3. Retrieval: similarity search per query; the default joiner dedups the union
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .similarityThreshold(0.5)
                        .topK(4)
                        .build())
                // 4. Post-retrieval: Jev drops bad passages, then reranks and keeps the top 3
                .documentPostProcessors(
                        JevDocumentFilter.builder(typeSafeClient).build(),
                        JevDocumentReranker.builder(typeSafeClient).topK(3).build())
                // 5. Generation: stuff the surviving context into the prompt
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .build())
                // retrieval per query runs on Spring Boot's executor instead of ad-hoc non-daemon threads
                .taskExecutor(taskExecutor)
                .build();
    }

    @PostMapping
    ResponseEntity<String> ask(@RequestBody String question) {
        final String answer = chatClient.prompt()
                .advisors(modularRag)
                .user(question)
                .call()
                .content();

        return ResponseEntity.ok(answer);
    }
}
