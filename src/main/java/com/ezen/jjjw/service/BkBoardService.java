package com.ezen.jjjw.service;



import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.BkBoardDto;
import com.ezen.jjjw.exception.CustomException;
import com.ezen.jjjw.exception.ErrorCode;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.repository.BkBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RequiredArgsConstructor
@Service
public class BkBoardService {

    private final BkBoardRepository bkBoardRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseEntity<List<BkBoard>> getAllBkBoardDto() {
        Member member = tokenProvider.getMemberFromAuthentication();
        List<BkBoard> bkBoardList = bkBoardRepository.findAllByMemberId(member.getId());
        return ResponseEntity.ok(bkBoardList);
    }

    @Transactional
    public ResponseEntity<List<BkBoard>> findAllByMemberIdAndCategory(String category) {
        Member member = tokenProvider.getMemberFromAuthentication();
        List<BkBoard> bkBoardList = bkBoardRepository.findAllByMemberIdAndCategory(member.getId(), category);
        return ResponseEntity.ok(bkBoardList);
    }

    @Transactional
    public ResponseEntity<BkBoard> getBkBoardById(Long postId) {
        BkBoard bkBoard = (bkBoardRepository.findById(postId)).get();
        if(bkBoard == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_POST);
        }
        return ResponseEntity.ok(bkBoard);
    }

    @Transactional
    public ResponseEntity<BkBoard> create(BkBoardDto.Request bkrequest) {
        Member member = tokenProvider.getMemberFromAuthentication();
        BkBoard bkBoard = bkrequest.toEntity(member);
        bkBoardRepository.save(bkBoard);
        return ResponseEntity.ok(bkBoard);
    }

    @Transactional
    public ResponseEntity<BkBoard> update(Long postId, BkBoardDto.Request bkrequest) {
        BkBoard bkBoard = bkBoardRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));
        bkBoard.update(bkrequest);
        bkBoardRepository.save(bkBoard);
        return ResponseEntity.ok(bkBoard);
    }

    @Transactional
    public void delete(Long postId, HttpServletRequest request) {
        String loggedInMemberId = (tokenProvider.getMemberFromAuthentication()).getMemberId();
        BkBoard bkBoard = bkBoardRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));

        // 작성자의 memberId와 로그인한 사용자의 memberId 비교
        if (!(bkBoard.getMember().getMemberId()).equals(loggedInMemberId)) {
            throw new CustomException(ErrorCode.INVALID_USER);
        }

        bkBoardRepository.deleteById(postId);
    }
}
