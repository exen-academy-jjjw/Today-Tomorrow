package com.ezen.jjjw.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageResponseDto {
    private String nickname;
    private int totalCount;
    private int completeCount;
    private int unCompleteCount;

    @Builder
    public MypageResponseDto(final String nickname, final int totalCount, final int completeCount, final int unCompleteCount) {
        this.nickname = nickname;
        this.totalCount = totalCount;
        this.completeCount = completeCount;
        this.unCompleteCount = unCompleteCount;
    }
}
