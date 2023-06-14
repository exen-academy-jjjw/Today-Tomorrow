package com.ezen.jjjw.service;



import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.BkBoardDto;
import com.ezen.jjjw.dto.response.ResponseDto;
import com.ezen.jjjw.exception.CustomException;
import com.ezen.jjjw.exception.ErrorCode;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.repository.BkBoardRepository;
import com.ezen.jjjw.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class BkBoardService {

    private final BkBoardRepository bkBoardRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;


    public List<BkBoard> getAllBkBoardDto(HttpServletRequest request) {
        if (!tokenProvider.validateToken(tokenProvider.resolveToken(request))) {
            String errorMessage = "Token이 유효하지 않습니다.";

            return (List<BkBoard>) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        }
        return bkBoardRepository.findAll();
    }

    @Transactional
    public Object create(BkBoardDto.Request bkrequest, HttpServletRequest request) {

        if (!tokenProvider.validateToken(tokenProvider.resolveToken(request))) {
            ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
            return null;
        }

        // 로그인한 멤버의 ID 가져오기
        String memberId = tokenProvider.getLoggedInMemberId(request);
        Optional<Member> optionalMember = Optional.ofNullable((Member) (Member) memberRepository.getByMemberId(memberId));
        if (optionalMember.isEmpty()) {
            ResponseDto.fail("member_not_found", "회원을 찾을 수 없습니다.");
            return null;
        }
        Member member = optionalMember.get();
        bkrequest.setMemberId(member.getMemberId());

        BkBoard bkBoard = bkrequest.toEntity(member);
        bkBoardRepository.save(bkBoard);

        return bkBoard.getPostid();
    }


    @Transactional
    public ResponseDto<Object> update(Long postid, BkBoardDto.Request bkrequest) {

        BkBoard bkBoard = bkBoardRepository.findById(postid).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));

        bkBoard.update(bkrequest);
        bkBoardRepository.save(bkBoard);

        return BkBoardDto.Response.toResponseDto(BkBoardDto.Response.of(bkBoard));
    }

    @Transactional
    public void delete(Long postid, HttpServletRequest request) {
        String loggedInMemberId = tokenProvider.getMemberIdFromToken(tokenProvider.resolveToken(request));

        // 토큰 유효성 검증
        if (!tokenProvider.validateToken(tokenProvider.resolveToken(request))) {
            ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
            return;
        }

        BkBoard bkBoard = bkBoardRepository.findById(postid).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));

        // 작성자의 memberId와 로그인한 사용자의 memberId 비교
        if (!bkBoard.getMember().getMemberId().equals(loggedInMemberId)) {
            ResponseDto.fail("FORBIDDEN", "작성자만 게시글을 삭제할 수 있습니다.");
            return;
        }

        bkBoardRepository.deleteById(postid);
    }

}
