package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.model.MonthlySale;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;

@Service
public class MonthlySalesService {

    public List<MonthlySale> getMonthlySalesForYear(int year) {
        return List.of(
                new MonthlySale("Product A", year, Month.JANUARY, new BigDecimal("1200")),
                new MonthlySale("Product A", year, Month.FEBRUARY, new BigDecimal("1325")),
                new MonthlySale("Product B", year, Month.NOVEMBER, new BigDecimal("1330")),
                new MonthlySale("Product B", year, Month.DECEMBER, new BigDecimal("1395"))
        );
    }
}