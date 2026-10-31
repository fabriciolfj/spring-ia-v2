package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.entity.BookEntity;
import com.github.fabriciolfj.study.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingFileService {

    private final EmbeddingModel embeddingModel;
    private final TokenTextSplitter tokenTextSplitter;
    private final BookRepository bookRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void embeddingSave(final MultipartFile multipartFile) throws IOException {
        var resource = new ByteArrayResource(multipartFile.getBytes());

        var docs = new TikaDocumentReader(resource).get();

        final var chunks = tokenTextSplitter.split(docs);
        chunks.forEach(d -> {
            final var document = getDocuments(d.getFormattedContent(), multipartFile.getOriginalFilename());

            bookRepository.saveAndFlush(document);
            log.info("book save success {}", d.getId());
        }); ;

    }

    private BookEntity getDocuments(final String texts, final String source) {
        final var response = embeddingModel.embedForResponse(List.of(texts))
                .getResult()
                .getOutput();

        return BookEntity
                .builder()
                .content(texts)
                .embedding(response)
                .metadata(Map.of(
                        "source", source,
                        "createAt", Instant.now().toEpochMilli()))
                .build();
    }
}
