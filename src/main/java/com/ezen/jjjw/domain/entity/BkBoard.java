package com.ezen.jjjw.domain.entity;

import com.ezen.jjjw.dto.BkBoardDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BkBoard extends Timestamped {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "postId")
        private Long postId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "memberId")
        @JsonIgnore
        private Member member;

        @Column(unique = false)
        private String category;

        @Column(unique = false)
        private String title;

        @Column(unique = false)
        private String content;

        @ColumnDefault("0")
        @Builder.Default
        private int completion = 0;

        @ColumnDefault("0")
        @Builder.Default
        private int share = 0;

        @ColumnDefault("0")
        @Builder.Default
        private int existReview = 0;

        @JsonIgnore
        @OneToOne(mappedBy = "bkBoard", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
        private Review review;

        @JsonIgnore
        @OneToMany(mappedBy = "bkBoard", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
        private List<Comment> children = new ArrayList<>();

        public BkBoard(BkBoardDto bkBoardDto) {
                this.postId = bkBoardDto.getPostId();
                this.content = bkBoardDto.getContent();
                this.title = bkBoardDto.getTitle();
                this.category = bkBoardDto.getCategory();
        }

        public void update(BkBoardDto.UpdateRequest bkBoardRequestDto){
                this.title = bkBoardRequestDto.getTitle();
                this.content = bkBoardRequestDto.getContent();
                this.category = bkBoardRequestDto.getCategory();
                this.completion = bkBoardRequestDto.getCompletion();
                this.share = bkBoardRequestDto.getShare();
        }

        public void updateCompletion(BkBoardDto.RequestCompletion requestCompletion){
                this.completion = requestCompletion.getCompletion();
        }

        public void updateShare(BkBoardDto.RequestShare requestShare){
                this.share = requestShare.getShare();
        }

        public void updateExistReview(BkBoard bkBoard){
                this.existReview = 1;
        }

        public void deleteExistReview(BkBoard bkBoard){
                this.existReview = 0;
        }
}
