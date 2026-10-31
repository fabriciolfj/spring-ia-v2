package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.service.EmbeddingFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/embedding")
@RequiredArgsConstructor
public class FileController {

    private final EmbeddingFileService embeddingFileService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void receiveFile(@RequestParam("file") final MultipartFile multipartFile) {
        try {
            embeddingFileService.embeddingSave(multipartFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
