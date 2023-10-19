package com.ezen.jjjw.domain.entity;

import com.ezen.jjjw.dto.request.MypageRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * packageName    : com.member.member_jwt.entity
 * fileName       : Member
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                회원 엔터티(고유 아이디, 멤버 아이디, 닉네임, 비밀번호)
 *                                스프링 내에 user라는 예약어가 존재하기 때문에 혼동을 막기 위해 member 사용
 */
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends Timestamped {

    // 멤버 엔터티의 고유 아이디 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자가 입력하게 될 본인의 아이디
    @Column(unique = true)
    private String memberId;

    // 사용자가 입력하게 될 본인의 닉네임
    @Column(nullable = false)
    private String nickname;

    // 사용자가 입력하게 될 본인의 비밀번호
    // 비밀번호는 민감한 정보이기 때문에 JsonIgnore처리
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    //신고 누적 수
    @ColumnDefault("0")
    @Builder.Default
    private int report = 0;

    // 하나의 계정은 하나의 토큰을 가지고 있을 수 있으며 계정이 삭제될 경우 토큰도 함께 삭제된다
    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private RefreshToken refreshTokenList = new RefreshToken();

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BkBoard> bkBoardList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    // DB에 들어있는 비밀번호와 입력받은 비밀번호의 일치 여부를 확인할 때 사용
    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password);
    }

    // 추후 비밀번호, 비밀번호 확인의 일치 여부와 토큰 재발급 메서드에서 사용
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Member member = (Member) o;
        return id != null && Objects.equals(id, member.id);
    }

    public void updateNickname(MypageRequestDto request) {
        this.nickname = request.getNickname();
    }

    public void updatePassword(PasswordEncoder passwordEncoder, MypageRequestDto request) {
        this.password = passwordEncoder.encode(request.getNewPassword());
    }
}
