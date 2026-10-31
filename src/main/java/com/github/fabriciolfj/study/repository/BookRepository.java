package com.github.fabriciolfj.study.repository;

import com.github.fabriciolfj.study.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
}
