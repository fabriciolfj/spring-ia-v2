package com.github.fabriciolfj.study.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Configuration
public class RagConfig {

    @Value("classpath:/docs/brasil.txt")
    private Resource brasilDocument;

    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();

        List<Document> documents = new TextReader(brasilDocument).read();
        List<Document> chunks = TokenTextSplitter.builder()
                .withChunkSize(200)
                .build()
                .apply(documents);

        vectorStore.add(chunks);
        return vectorStore;
    }
}
