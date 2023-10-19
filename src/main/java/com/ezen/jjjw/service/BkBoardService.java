package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.BkBoardReqDto;
import com.ezen.jjjw.dto.request.BkBoardUpdateReqDto;
import com.ezen.jjjw.dto.request.CompletionReqDto;
import com.ezen.jjjw.dto.request.ShareReqDto;
import com.ezen.jjjw.dto.response.BkBoardResDto;
import com.ezen.jjjw.exception.CustomExceptionHandler;
import com.ezen.jjjw.repository.BkBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
@Slf4j
public class BkBoardService {

    private final BkBoardRepository bkBoardRepository;
    private final CustomExceptionHandler customExceptionHandler;

    @Transactional
    public ResponseEntity<Long> create(BkBoardReqDto bkrequest, Member member) {
        BkBoard bkBoard = BkBoard.builder()
                .member(member)
                .postId(bkrequest.getPostId())
                .category(bkrequest.getCategory())
                .title(bkrequest.getTitle())
                .content(bkrequest.getContent())
                .completion(bkrequest.getCompletion())
                .share(bkrequest.getShare())
                .build();
        bkBoardRepository.save(bkBoard);
        log.info("게시글 작성 성공");
        return ResponseEntity.ok(bkBoard.getPostId());
    }

    @Transactional
    public ResponseEntity<Integer> update(Long postId, BkBoardUpdateReqDto bkrequest, Member member) {
        Optional<BkBoard> optionalBkBoard = bkBoardRepository.findById(postId);
        if(!optionalBkBoard.isPresent()) {
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        BkBoard bkBoard = optionalBkBoard.get();

        Member author = bkBoard.getMember();
        if(!author.getMemberId().equals(member.getMemberId())) {
            log.info("일치하지 않는 사용자");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        bkBoard.update(bkrequest);
        bkBoardRepository.save(bkBoard);
        log.info("게시글 업데이트 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional
    public ResponseEntity<Integer> updateGetMember(Long postId, Member member) {
        Optional<BkBoard> optionalBkBoard = bkBoardRepository.findById(postId);
        if(!optionalBkBoard.isPresent()) {
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        BkBoard bkBoard = optionalBkBoard.get();

        Member author = bkBoard.getMember();
        if(!author.getMemberId().equals(member.getMemberId())) {
            log.info("일치하지 않는 사용자");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional
    public ResponseEntity<Integer> delete(Long postId) {
        Optional<BkBoard> optionalBkBoard = bkBoardRepository.findById(postId);
        if(!optionalBkBoard.isPresent()) {
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        bkBoardRepository.deleteById(postId);

        log.info("게시글 삭제 성공");
        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional
    public ResponseEntity<Integer> deleteGetMember(Long postId, Member member) {
        Optional<BkBoard> optionalBkBoard = bkBoardRepository.findById(postId);
        if(!optionalBkBoard.isPresent()) {
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        BkBoard bkBoard = optionalBkBoard.get();

        Member author = bkBoard.getMember();
        if(!author.getMemberId().equals(member.getMemberId())) {
            log.info("일치하지 않는 사용자");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        return ResponseEntity.ok(HttpServletResponse.SC_OK);
    }

    @Transactional
    public ResponseEntity<?> getBkBoardById(Long postId) {
        Optional<BkBoard> optionalBkBoard = bkBoardRepository.findById(postId);
        if(!optionalBkBoard.isPresent()) {
            log.info("존재하지 않는 게시글");
            return ResponseEntity.ok(HttpServletResponse.SC_NOT_FOUND);
        }

        BkBoard bkBoard = optionalBkBoard.get();

        BkBoardResDto bkBoardResDto = BkBoardResDto.builder()
                .postId(bkBoard.getPostId())
                .nickname(bkBoard.getMember().getNickname())
                .category(bkBoard.getCategory())
                .title(bkBoard.getTitle())
                .content(bkBoard.getContent())
                .completion(bkBoard.getCompletion())
                .share(bkBoard.getShare())
                .existReview(bkBoard.getExistReview())
                .existComment(bkBoard.getExistComment())
                .build();

        return ResponseEntity.ok(bkBoardResDto);
    }

    @Transactional
    public ResponseEntity<List<BkBoard>> getAllBkBoardDto(int page, Member member) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("postId").descending());
        Page<BkBoard> bkBoardPage = bkBoardRepository.findAllByMemberId(member.getId(), pageRequest);
        List<BkBoard> bkBoardList;
        if(bkBoardPage != null && bkBoardPage.getTotalElements() != 0) {
            bkBoardList = bkBoardPage.getContent();
        } else {
            bkBoardList = null;
        }
        return ResponseEntity.ok(bkBoardList);
    }

    @Transactional
    public ResponseEntity<List<BkBoard>> getAllShareBkBoardDto(int page) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("postId").descending());
        Page<BkBoard> bkBoardPage = bkBoardRepository.findAllByShare(1, pageRequest);
        List<BkBoard> bkBoardList = bkBoardPage.getContent();
        return ResponseEntity.ok(bkBoardList);
    }

    @Transactional
    public ResponseEntity<List<BkBoard>> findAllByMemberIdAndCategory(String category, int page, Member member) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by("postId").descending());
        Page<BkBoard> bkBoardPage = bkBoardRepository.findAllByMemberIdAndCategory(member.getId(), category, pageRequest);
        List<BkBoard> bkBoardList = bkBoardPage.getContent();
        return ResponseEntity.ok(bkBoardList);
    }

    @Transactional
    public ResponseEntity<Integer> updateCompletion(Long postId, CompletionReqDto requestCompletion, Member member) {
        BkBoard bkBoard = (bkBoardRepository.findById(postId)).get();
        customExceptionHandler.getNotFoundBoardStatus(bkBoard);

        Member author = bkBoard.getMember();
        if(!author.getMemberId().equals(member.getMemberId())) {
//            return customExceptionHandler.getNotMatchMemberStatus();
            log.info("일치하지 않는 사용자");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        bkBoard.updateCompletion(requestCompletion);
        bkBoardRepository.save(bkBoard);

        log.info("완료 여부 변경 완료");
        return ResponseEntity.ok(bkBoard.getCompletion());
    }

    @Transactional
    public ResponseEntity<Integer> updateShare(Long postId, ShareReqDto requestShare, Member member) {
        BkBoard bkBoard = (bkBoardRepository.findById(postId)).get();
        customExceptionHandler.getNotFoundBoardStatus(bkBoard);

        Member author = bkBoard.getMember();
        if(!author.getMemberId().equals(member.getMemberId())) {
//            return customExceptionHandler.getNotMatchMemberStatus();
            log.info("일치하지 않는 사용자");
            return ResponseEntity.ok(HttpServletResponse.SC_BAD_REQUEST);
        }

        bkBoard.updateShare(requestShare);
        bkBoardRepository.save(bkBoard);

        log.info("공유 여부 변경 완료");
        return ResponseEntity.ok(bkBoard.getShare());
    }
}
