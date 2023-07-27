package com.ezen.jjjw.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * packageName    : com.ezen.jjjw.domain.entity
 * fileName       : Comment.java
 * author         : won
 * date           : 2023-07-27
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-07-27        won       최초 생성
 */
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @JoinColumn(name = "nickname", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JsonIgnore
    @JoinColumn(name = "postId", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private BkBoard bkBoard;

    @Column(nullable = false)
    private String CommentTxt;


}
