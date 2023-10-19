package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.MypageRequestDto;
import com.ezen.jjjw.dto.response.MypageResponseDto;
import com.ezen.jjjw.exception.CustomExceptionHandler;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.repository.BkBoardRepository;
import com.ezen.jjjw.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final BkBoardRepository bkBoardRepository;
    private final CustomExceptionHandler customExceptionHandler;

    //맴버 정보 조회
    @Transactional
    public ResponseEntity<MypageResponseDto> getFindMember(Member member) {
        Integer totalCount = bkBoardRepository.countByMemberId(member.getId());
        Integer completeCount = bkBoardRepository.countByMemberIdAndCompletion(member.getId(), 1);
        Integer unCompleteCount = totalCount - completeCount;

        MypageResponseDto memberInfo = MypageResponseDto.builder()
                .nickname(member.getNickname())
                .totalCount(totalCount)
                .completeCount(completeCount)
                .unCompleteCount(unCompleteCount)
                .build();
        return ResponseEntity.ok(memberInfo);
    }

    //닉네임 변경
    @Transactional
    public ResponseEntity<Integer> updateNick(MypageRequestDto request, Member member) {
        customExceptionHandler.getNotFoundMemberStatus(member);

        try {
            Optional<Member> byNickname = memberRepository.findByNickname(request.getNickname());
            if(byNickname.isPresent()) {
                log.info("닉네임 중복");
                return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        member.updateNickname(request);
        memberRepository.save(member);

        log.info("닉네임 변경 완료");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional
    public ResponseEntity<Integer> updatePassword(MypageRequestDto request, Member member) {
        customExceptionHandler.getNotFoundMemberStatus(member);
        String oldPassword = member.getPassword();

        if(!passwordEncoder.matches(request.getPassword(), oldPassword)){
            log.info("비밀번호 불일치");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        member.updatePassword(passwordEncoder, request);
        memberRepository.save(member);

        log.info("비밀번호 변경 완료");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

}
