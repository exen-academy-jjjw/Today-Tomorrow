package com.ezen.jjjw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * packageName    : com.connectiontest.test.entity
 * fileName       : File.java
 * author         : won
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        won       최초 생성
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ReviewFile extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "review_table_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Column
    private String fileUrl;

    public ReviewFile(String localPath) {
        this.fileUrl = "C:/upload" + "/" + review;
    }
}
