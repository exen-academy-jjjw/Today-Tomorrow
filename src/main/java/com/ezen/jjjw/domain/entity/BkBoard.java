package com.ezen.jjjw.domain.entity;

import com.ezen.jjjw.dto.request.BkBoardUpdateReqDto;
import com.ezen.jjjw.dto.request.CompletionReqDto;
import com.ezen.jjjw.dto.request.ShareReqDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
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

        @ColumnDefault("0")
        @Builder.Default
        private int existComment = 0;

        @JsonIgnore
        @OneToOne(mappedBy = "bkBoard", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
        private Review review;

        @JsonIgnore
        @OneToMany(mappedBy = "bkBoard", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
        private List<Comment> comments = new ArrayList<>();

        public void update(BkBoardUpdateReqDto bkBoardRequestDto){
                this.title = bkBoardRequestDto.getTitle();
                this.content = bkBoardRequestDto.getContent();
                this.category = bkBoardRequestDto.getCategory();
                this.completion = bkBoardRequestDto.getCompletion();
                this.share = bkBoardRequestDto.getShare();
        }

        public void updateCompletion(CompletionReqDto requestCompletion){
                this.completion = requestCompletion.getCompletion();
        }

        public void updateShare(ShareReqDto requestShare){
                this.share = requestShare.getShare();
        }

        public void updateExistReview(BkBoard bkBoard){
                this.existReview = 1;
        }

        public void deleteExistReview(BkBoard bkBoard){
                this.existReview = 0;
        }

        public void updateExistComment(BkBoard bkBoard){
                this.existComment = 1;
        }
        public void deleteExistComment(BkBoard bkBoard){
                this.existComment = 0;
        }
}
