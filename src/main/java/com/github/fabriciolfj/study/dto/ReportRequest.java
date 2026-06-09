package com.github.fabriciolfj.study.dto;

import jakarta.validation.constraints.NotNull;

public record ReportRequest(@NotNull String prompt) {
}