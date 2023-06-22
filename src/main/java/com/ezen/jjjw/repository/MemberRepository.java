package com.ezen.jjjw.repository;


import com.ezen.jjjw.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * packageName    : com.member.member_jwt.repository
 * fileName       : MemberRepository
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                DB와 직접적인 소통을 하는 인터페이스
 *                                JpaRepository<?, ?>에는 항상 엔터티와 고유값의 데이터 타입을 입력해주어야 함
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(String memberId);

    /** bkBoard memberId 정수꺼 */
    Object getByMemberId(String memberId);

}
