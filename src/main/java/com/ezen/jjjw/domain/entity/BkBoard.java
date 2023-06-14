package com.ezen.jjjw.domain.entity;


import com.ezen.jjjw.dto.BkBoardDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BkBoard extends Timestamped {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "postId")
        private Long postid;

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

        public BkBoard(BkBoardDto bkBoardDto) {
                this.postid = bkBoardDto.getPostId();
                this.content = bkBoardDto.getContent();
                this.title = bkBoardDto.getTitle();
                this.category = bkBoardDto.getCategory();
        }

        public void update(BkBoardDto.Request bkBoardRequestDto){
                this.title = bkBoardRequestDto.getTitle();
                this.content = bkBoardRequestDto.getContent();
                this.category = bkBoardRequestDto.getCategory();
        }
}
