package com.ezen.jjjw.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class OutMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long mId;

    @Column(nullable = false)
    private String memberId;

    @Column
    private String nickname;

    @Column
    private String password;


    @Builder
    public OutMember(final Long id, final Long mId, final String memberId, final String nickname, final String password){
        this.id = id;
        this.mId = mId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.password = password;
    }
}
