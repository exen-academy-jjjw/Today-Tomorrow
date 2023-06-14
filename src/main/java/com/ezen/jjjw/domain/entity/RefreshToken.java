package com.ezen.jjjw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * packageName    : com.member.member_jwt.domain.entity
 * fileName       : RefreshToken
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RefreshToken {

    @Id
    @Column(nullable = false)
    private Long id;

    // 하나의 토큰은 하나의 계정을 가질 수 있음
    @JoinColumn(name = "member_table_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    // 실제 토큰값
    @Column(nullable = false)
    private String value;

    // 토큰 재발급에 쓰일 예정
    public void updateValue(String token) {
        this.value = token;
    }
}
