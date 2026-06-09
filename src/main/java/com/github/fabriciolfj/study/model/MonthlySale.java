package com.github.fabriciolfj.study.model;

import java.math.BigDecimal;
import java.time.Month;

public record MonthlySale(String product, int year, Month month, BigDecimal amount) {
}
