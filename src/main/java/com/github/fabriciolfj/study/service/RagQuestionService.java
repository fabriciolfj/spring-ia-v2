package com.github.fabriciolfj.study.service;


import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class RagQuestionService {

    private final VectorStore vectorStore;

    public RagQuestionService(final VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<Document> execute(final String question) {
        final SearchRequest request = SearchRequest.builder()
                .topK(10)
                .query(question)
                .similarityThreshold(0.5)
                .build();

        return vectorStore.similaritySearch(request)
                .stream()
                .sorted(Comparator.comparingLong(RagQuestionService::createAt).reversed()
                        .thenComparing(Document::getScore, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private static long createAt(final Document doc) {
        return ((Number) doc.getMetadata().getOrDefault("createAt", 0L)).longValue();
    }
}
