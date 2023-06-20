package com.ezen.jjjw.service;



import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.BkBoardDto;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.repository.BkBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RequiredArgsConstructor
@Service
@Slf4j
public class BkBoardService {

    private final BkBoardRepository bkBoardRepository;
    private final TokenProvider tokenProvider;

//    @Transactional
//    public ResponseEntity<List<BkBoard>> getAllBkBoardDto() {
//        Member member = tokenProvider.getMemberFromAuthentication();
//        List<BkBoard> bkBoardList = bkBoardRepository.findAllByMemberId(member.getId());
//        return ResponseEntity.ok(bkBoardList);
//    }
    @Transactional
    public ResponseEntity<List<BkBoard>> getAllBkBoardDto(int page) {
        Member member = tokenProvider.getMemberFromAuthentication();
        PageRequest pageRequest = PageRequest.of(page, 7, Sort.by("postId").descending());
        Page<BkBoard> bkBoardPage = bkBoardRepository.findAllByMemberId(member.getId(), pageRequest);
        List<BkBoard> bkBoardList = bkBoardPage.getContent();
        return ResponseEntity.ok(bkBoardList);
    }

//    @Transactional
//    public ResponseEntity<List<BkBoard>> findAllByMemberIdAndCategory(String category) {
//        Member member = tokenProvider.getMemberFromAuthentication();
//        List<BkBoard> bkBoardList = bkBoardRepository.findAllByMemberIdAndCategory(member.getId(), category);
//        return ResponseEntity.ok(bkBoardList);
//    }
    @Transactional
    public ResponseEntity<List<BkBoard>> findAllByMemberIdAndCategory(String category, int page) {
        Member member = tokenProvider.getMemberFromAuthentication();
        PageRequest pageRequest = PageRequest.of(page, 7, Sort.by("postId").descending());
        Page<BkBoard> bkBoardPage = bkBoardRepository.findAllByMemberIdAndCategory(member.getId(), category, pageRequest);
        List<BkBoard> bkBoardList = bkBoardPage.getContent();
        return ResponseEntity.ok(bkBoardList);
    }

    @Transactional
    public ResponseEntity<?> getBkBoardById(Long postId) {
        BkBoard bkBoard = (bkBoardRepository.findById(postId)).get();
        if(bkBoard == null) {
//            throw new CustomException(ErrorCode.NOT_FOUND_POST);
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        return ResponseEntity.ok(bkBoard);
    }

    @Transactional
    public ResponseEntity<Long> create(BkBoardDto.Request bkrequest) {
        Member member = tokenProvider.getMemberFromAuthentication();
        BkBoard bkBoard = bkrequest.toEntity(member);
        bkBoardRepository.save(bkBoard);
//        return ResponseEntity.ok(bkBoard);
        log.info("게시글 작성 성공");
        return ResponseEntity.ok(bkBoard.getPostId());
    }

    @Transactional
    public ResponseEntity<Integer> update(Long postId, BkBoardDto.UpdateRequest bkrequest) {
//        BkBoard bkBoard = bkBoardRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));
        BkBoard bkBoard = (bkBoardRepository.findById(postId)).get();
        if(bkBoard == null) {
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        bkBoard.update(bkrequest);
        bkBoardRepository.save(bkBoard);
//        return ResponseEntity.ok(bkBoard);
        log.info("게시글 업데이트 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional
    public ResponseEntity<Integer> delete(Long postId) {
        String loggedInMemberId = (tokenProvider.getMemberFromAuthentication()).getMemberId();
//        BkBoard bkBoard = bkBoardRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));
        BkBoard bkBoard = (bkBoardRepository.findById(postId)).get();
        if(bkBoard == null) {
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        // 작성자의 memberId와 로그인한 사용자의 memberId 비교
        if (!(bkBoard.getMember().getMemberId()).equals(loggedInMemberId)) {
//            throw new CustomException(ErrorCode.INVALID_USER);
            log.info("사용자 불일치");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        bkBoardRepository.deleteById(postId);

        log.info("게시글 삭제 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional
    public ResponseEntity<Integer> updateCompletion(Long postId, BkBoardDto.RequestCompletion requestCompletion) {
        BkBoard bkBoard = (bkBoardRepository.findById(postId)).get();
        if(bkBoard == null) {
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        bkBoard.updateCompletion(requestCompletion);
        bkBoardRepository.save(bkBoard);

        log.info("완료 여부 변경 완료");
        return ResponseEntity.ok(bkBoard.getCompletion());
    }
}
