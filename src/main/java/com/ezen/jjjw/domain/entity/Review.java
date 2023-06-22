package com.ezen.jjjw.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : com.RSMboard.RSMboard.entity
 * fileName       : ReviewEntity.java
 * author         : won
 * date           : 2023-06-04
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-04        won       최초 생성
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review extends Timestamped {
    @Id // pk
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @JoinColumn(name = "postId", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private BkBoard bkBoard;

    @JsonIgnore
    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReviewFile> reviewFileList = new ArrayList<>();

    @Column(columnDefinition = "LONGTEXT")
    private String reviewContent;

    public void update(String reviewRequestDto) {
        this.reviewContent = reviewRequestDto;
    }
}
