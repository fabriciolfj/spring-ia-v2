package com.github.fabriciolfj.study.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

import java.time.OffsetDateTime;
import java.util.Map;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "book")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @JdbcTypeCode((SqlTypes.VECTOR))
    @Array(length = 768)
    public float[] embedding;

    @CreationTimestamp
    public OffsetDateTime createAt;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;

}
