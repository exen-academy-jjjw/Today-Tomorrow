package com.ezen.jjjw.domain.entity;

import com.ezen.jjjw.dto.request.ReviewRequestDto;
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

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReviewFile> reviewFileList = new ArrayList<>();

    @Column(columnDefinition = "LONGTEXT")
    private String reviewContent;

//    public Review(ReviewRequestDto reviewRequestDto) {
//        this.reviewContent = reviewRequestDto.getReviewContent();
//    }

    public boolean validateMember(Member member) {
        return !this.bkBoard.getMember().equals(member);
    }

    public void update(ReviewRequestDto reviewRequestDto) {
        this.reviewContent = reviewRequestDto.getReviewContent();
    }
}
