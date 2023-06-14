package com.ezen.jjjw.domain;

/**
 * packageName    : com.member.member_jwt.domain
 * fileName       : Authority
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                클래스처럼 보이게 하는 상수의 집합 클래스가 enum
 *                                서로 관련있는 상수들끼리를 모아 정의함
 */
public enum Authority {

    // 권한 구분을 위해 일반 계정과 관리자 계정을 enum으로 저장
    ROLE_MEMBER,
    ROLE_ADMIN
}
