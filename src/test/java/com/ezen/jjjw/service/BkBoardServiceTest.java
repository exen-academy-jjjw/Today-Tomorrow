package com.ezen.jjjw.service;

import com.amazonaws.Response;
import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.BkBoardReqDto;
import com.ezen.jjjw.dto.request.BkBoardUpdateReqDto;
import com.ezen.jjjw.dto.request.CompletionReqDto;
import com.ezen.jjjw.dto.response.BkBoardResDto;
import com.ezen.jjjw.exception.CustomExceptionHandler;
import com.ezen.jjjw.jwt.TokenProvider;
import com.ezen.jjjw.repository.BkBoardRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

/**
 * packageName    : com.ezen.jjjw.service
 * fileName       : BkBoardServiceTest
 * author         : wldk9
 * date           : 2023-10-10
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-10-10        sonjia       최초 생성
 */
@ExtendWith(MockitoExtension.class)
public class BkBoardServiceTest {

    @InjectMocks
    private BkBoardService bkBoardService;

    @Mock
    private BkBoardRepository bkBoardRepository;

    @Mock
    private CustomExceptionHandler customExceptionHandler;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void success_create() {
        //given
        Member member = new Member();

        BkBoardReqDto bkBoardReqDto = new BkBoardReqDto();
        bkBoardReqDto.setPostId(1L);
        bkBoardReqDto.setCategory("study");
        bkBoardReqDto.setTitle("title");
        bkBoardReqDto.setContent("content");
        bkBoardReqDto.setCompletion(0);
        bkBoardReqDto.setShare(0);

        //stub
        when(bkBoardRepository.save(any())).thenReturn(new BkBoard());

        //when
        ResponseEntity<Long> responseEntity = bkBoardService.create(bkBoardReqDto, member);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(1L, responseEntity.getBody());

        //verify
        verify(bkBoardRepository, timeout(1)).save(any());
    }

    @Test
    void success_update() {
        //given
        Long postId = 1L;
        BkBoardUpdateReqDto bkBoardUpdateReqDto = new BkBoardUpdateReqDto();
        bkBoardUpdateReqDto.setCategory("study");
        bkBoardUpdateReqDto.setTitle("title");
        bkBoardUpdateReqDto.setContent("content");
        bkBoardUpdateReqDto.setCompletion(0);
        bkBoardUpdateReqDto.setShare(0);

        Member member = Member.builder()
                .id(1L)
                .memberId("testUser")
                .nickname("testNickname")
                .password(passwordEncoder.encode("password")).build();

        BkBoard bkBoard = BkBoard.builder()
                .postId(postId)
                .member(member)
                .category("study")
                .title("title")
                .content("testContent")
                .build();

        //stub
        when(bkBoardRepository.findById(postId)).thenReturn(Optional.of(bkBoard));
        when(bkBoardRepository.save(any())).thenReturn(new BkBoard());

        //when
        ResponseEntity<Integer> responseEntity = bkBoardService.update(postId, bkBoardUpdateReqDto, member);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());

        //verify
        verify(bkBoardRepository, times(1)).findById(postId);
        verify(bkBoardRepository, times(1)).save(any());
    }

    @Test
    void fail_update_cause_postId() {
        //given
        Long postId = 1L;
        BkBoardUpdateReqDto bkBoardUpdateReqDto = new BkBoardUpdateReqDto();
        Member member = new Member();

        //stub
        when(bkBoardRepository.findById(postId)).thenReturn(Optional.empty());

        //when
        ResponseEntity<Integer> responseEntity = bkBoardService.update(postId, bkBoardUpdateReqDto, member);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(404, responseEntity.getBody());

        //verify
        verify(bkBoardRepository, times(1)).findById(postId);
    }

    @Test
    void fail_update_cause_author() {
        //given
        Long postId = 1L;
        BkBoardUpdateReqDto bkBoardUpdateReqDto = new BkBoardUpdateReqDto();
        bkBoardUpdateReqDto.setCategory("study");
        bkBoardUpdateReqDto.setTitle("title");
        bkBoardUpdateReqDto.setContent("content");
        bkBoardUpdateReqDto.setCompletion(0);
        bkBoardUpdateReqDto.setShare(0);

        Member member1 = Member.builder()
                .id(1L)
                .memberId("testUser")
                .nickname("testNickname")
                .password(passwordEncoder.encode("password")).build();

        Member member2 = Member.builder()
                .id(2L)
                .memberId("testUser2")
                .nickname("testNickname2")
                .password(passwordEncoder.encode("password")).build();

        BkBoard bkBoard = BkBoard.builder()
                .postId(postId)
                .member(member1)
                .category("study")
                .title("title")
                .content("testContent")
                .build();

        //stub
        when(bkBoardRepository.findById(postId)).thenReturn(Optional.of(bkBoard));

        //when
        ResponseEntity<Integer> responseEntity = bkBoardService.update(postId, bkBoardUpdateReqDto, member2);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(400, responseEntity.getBody());

        //verify
        verify(bkBoardRepository, times(1)).findById(postId);
    }

    @Test
    void success_delete() {
        //given
        Long postId = 1L;
        BkBoard bkBoard = BkBoard.builder()
                .postId(postId)
                .category("etc")
                .title("title")
                .content("content")
                .completion(0)
                .share(0)
                .build();

        //stub
        when(bkBoardRepository.findById(postId)).thenReturn(Optional.of(bkBoard));
        doNothing().when(bkBoardRepository).deleteById(postId);

        //when
        ResponseEntity<Integer> responseEntity = bkBoardService.delete(postId);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(200, responseEntity.getBody());

        //verify
        verify(bkBoardRepository, times(1)).findById(postId);
        verify(bkBoardRepository, times(1)).deleteById(postId);
    }

    @Test
    void fail_delete_cause_postId() {
        //given
        Long postId = 1L;

        //stub
        when(bkBoardRepository.findById(postId)).thenReturn(Optional.empty());

        //when
        ResponseEntity<Integer> responseEntity = bkBoardService.delete(postId);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(404, responseEntity.getBody());

        //verify
        verify(bkBoardRepository, times(1)).findById(postId);
    }

    @Test
    void success_getBkBoardById() {
        //given
        Long postId = 1L;
        Member member = Member.builder()
                .id(1L)
                .memberId("testUser")
                .nickname("testNick")
                .build();

        BkBoard bkBoard = BkBoard.builder()
                .postId(postId)
                .member(member)
                .build();

        //stub
        when(bkBoardRepository.findById(postId)).thenReturn(Optional.of(bkBoard));

        //when
        ResponseEntity<?> responseEntity = bkBoardService.getBkBoardById(postId);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(1L, ((BkBoardResDto) responseEntity.getBody()).getPostId());

        //verify
        verify(bkBoardRepository, times(1)).findById(postId);
    }

    @Test
    void fail_getBkBoardById_cause_postId() {
        //given
        Long postId = 1L;

        //stub
        when(bkBoardRepository.findById(postId)).thenReturn(Optional.empty());

        //when
        ResponseEntity<?> responseEntity = bkBoardService.getBkBoardById(postId);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(404, responseEntity.getBody());

        //verify
        verify(bkBoardRepository, times(1)).findById(postId);
    }

    @Test
    void success_1st_case_getAllBkBoardDto() {
        //given
        int page = 0;
        Member member = new Member();
        member.setId(1L);
        member.setMemberId("testUser");
        member.setNickname("testNick");
        member.setPassword(passwordEncoder.encode("password"));

        List<BkBoard> bkBoardList = new ArrayList<>();
        for(int i = 1; i <= 5; i++) {
            BkBoard bkBoard = BkBoard.builder()
                    .postId((long) i)
                    .category("etc")
                    .title("title")
                    .content("content")
                    .member(member)
                    .completion(0)
                    .share(0)
                    .build();
            bkBoardList.add(bkBoard);
        };

        member.setBkBoardList(bkBoardList);

        Pageable pageRequest = PageRequest.of(page, 10, Sort.by("postId").descending());
        Page<BkBoard> pageResult = new PageImpl<BkBoard>(bkBoardList, pageRequest, 5);
        List<BkBoard> bkListResult = pageResult.getContent();

        //stub
        when(bkBoardRepository.findAllByMemberId(member.getId(), pageRequest)).thenReturn(pageResult);

        //when
        ResponseEntity<List<BkBoard>> responseEntity = bkBoardService.getAllBkBoardDto(page, member);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(1, responseEntity.getBody().get(0).getPostId());
        assertEquals(2, responseEntity.getBody().get(1).getPostId());
        assertEquals(5, responseEntity.getBody().get(4).getPostId());

        //verify
        verify(bkBoardRepository, times(1)).findAllByMemberId(member.getId(), pageRequest);
    }

    @Test
    void success_2st_case_getAllBkBoardDto() {
        //given
        int page = 0;
        List<BkBoard> bkBoardList = new ArrayList<>();
        Member member = new Member();
        member.setId(1L);
        member.setMemberId("testUser");
        member.setNickname("testNick");
        member.setPassword(passwordEncoder.encode("password"));
        member.setBkBoardList(bkBoardList);

        Pageable pageRequest = PageRequest.of(page, 10, Sort.by("postId").descending());
        Page<BkBoard> pageResult = new PageImpl<BkBoard>(bkBoardList, pageRequest, 0);
        List<BkBoard> bkListResult = pageResult.getContent();

        //stub
        when(bkBoardRepository.findAllByMemberId(member.getId(), pageRequest)).thenReturn(pageResult);

        //when
        ResponseEntity<List<BkBoard>> responseEntity = bkBoardService.getAllBkBoardDto(page, member);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());

        //verify
        verify(bkBoardRepository, times(1)).findAllByMemberId(member.getId(), pageRequest);
    }

    @Test
    void fail_getAllBkBoardDto_cause_pageNum() {
        //given
        int page = 1;
        Member member = new Member();
        member.setId(1L);
        member.setMemberId("testUser");
        member.setNickname("testNick");
        member.setPassword(passwordEncoder.encode("password"));

        List<BkBoard> bkBoardList = new ArrayList<>();
        for(int i = 1; i <= 5; i++) {
            BkBoard bkBoard = BkBoard.builder()
                    .postId((long) i)
                    .category("etc")
                    .title("title")
                    .content("content")
                    .member(member)
                    .completion(0)
                    .share(0)
                    .build();
            bkBoardList.add(bkBoard);
        };

        member.setBkBoardList(bkBoardList);

        Pageable pageRequest = PageRequest.of(page, 10, Sort.by("postId").descending());

        //stub
        when(bkBoardRepository.findAllByMemberId(member.getId(), pageRequest)).thenReturn(Page.empty());

        //when
        ResponseEntity<List<BkBoard>> responseEntity = bkBoardService.getAllBkBoardDto(page, member);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());

        //verify
        verify(bkBoardRepository, times(1)).findAllByMemberId(member.getId(), pageRequest);
    }

    @Test
    void fail_getAllBkBoardDto_cause_member() {
        //given
        int page = 1;
        Member member = new Member();
        member.setId(1L);
        member.setMemberId("testUser");
        member.setNickname("testNick");
        member.setPassword(passwordEncoder.encode("password"));

        Member member2 = new Member();
        member.setId(1L);
        member.setMemberId("testUser");
        member.setNickname("testNick");
        member.setPassword(passwordEncoder.encode("password"));

        List<BkBoard> bkBoardList = new ArrayList<>();
        for(int i = 1; i <= 5; i++) {
            BkBoard bkBoard = BkBoard.builder()
                    .postId((long) i)
                    .category("etc")
                    .title("title")
                    .content("content")
                    .member(member2)
                    .completion(0)
                    .share(0)
                    .build();
            bkBoardList.add(bkBoard);
        };

        member.setBkBoardList(bkBoardList);

        Pageable pageRequest = PageRequest.of(page, 10, Sort.by("postId").descending());

        //stub
        when(bkBoardRepository.findAllByMemberId(member.getId(), pageRequest)).thenReturn(Page.empty());

        //when
        ResponseEntity<List<BkBoard>> responseEntity = bkBoardService.getAllBkBoardDto(page, member);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertNull(responseEntity.getBody());

        //verify
        verify(bkBoardRepository, times(1)).findAllByMemberId(member.getId(), pageRequest);
    }

    @Test
    void success_updateCompletion() {
        //given
        Long postId = 1L;
        Member member = new Member();
        member.setId(1L);
        member.setMemberId("testUser");
        member.setNickname("testNick");
        member.setPassword(passwordEncoder.encode("password"));

        List<BkBoard> bkBoardList = new ArrayList<>();
        BkBoard bkBoard = BkBoard.builder()
                .postId(postId)
                .member(member)
                .title("title")
                .content("content")
                .completion(0)
                .share(0)
                .build();
        bkBoardList.add(bkBoard);

        member.setBkBoardList(bkBoardList);

        CompletionReqDto completionReqDto = new CompletionReqDto();
        completionReqDto.setPostId(postId);
        completionReqDto.setCompletion(1);

        //stub
        when(bkBoardRepository.findById(postId)).thenReturn(Optional.ofNullable(bkBoard));
        when(bkBoardRepository.save(bkBoard)).thenReturn(new BkBoard());

        //when
        ResponseEntity<Integer> responseEntity = bkBoardService.updateCompletion(postId, completionReqDto, member);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(1, responseEntity.getBody());

        //verify
        verify(bkBoardRepository, times(1)).findById(postId);
        verify(bkBoardRepository, times(1)).save(bkBoard);
    }

    @Test
    void fail_updateCompletion_cause_postId() {
        //given
        Long postId = 2L;
        Member member = new Member();
        member.setId(1L);
        member.setMemberId("testUser");
        member.setNickname("testNick");
        member.setPassword(passwordEncoder.encode("password"));

        List<BkBoard> bkBoardList = new ArrayList<>();
        BkBoard bkBoard = BkBoard.builder()
                .postId(1L)
                .member(member)
                .title("title")
                .content("content")
                .completion(0)
                .share(0)
                .build();
        bkBoardList.add(bkBoard);

        member.setBkBoardList(bkBoardList);

        CompletionReqDto completionReqDto = new CompletionReqDto();
        completionReqDto.setPostId(postId);
        completionReqDto.setCompletion(1);

        //stub
        when(bkBoardRepository.findById(postId)).thenReturn(Optional.empty());

        //when
        ResponseEntity<Integer> responseEntity = bkBoardService.updateCompletion(postId, completionReqDto, member);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(404, responseEntity.getBody());

        //verify
        verify(bkBoardRepository, times(1)).findById(postId);
    }

    @Test
    void fail_updateCompletion_cause_member() {
        //given
        Long postId = 1L;
        Member member = new Member();
        member.setId(1L);
        member.setMemberId("testUser");
        member.setNickname("testNick");
        member.setPassword(passwordEncoder.encode("password"));

        Member member2 = new Member();
        member.setId(2L);
        member.setMemberId("testUser");
        member.setNickname("testNick");
        member.setPassword(passwordEncoder.encode("password"));

        BkBoard bkBoard = BkBoard.builder()
                .postId(postId)
                .member(member)
                .title("title")
                .content("content")
                .completion(0)
                .share(0)
                .build();

        CompletionReqDto completionReqDto = new CompletionReqDto();
        completionReqDto.setPostId(postId);
        completionReqDto.setCompletion(1);

        //stub
        when(bkBoardRepository.findById(postId)).thenReturn(Optional.ofNullable(bkBoard));

        //when
        ResponseEntity<Integer> responseEntity = bkBoardService.updateCompletion(postId, completionReqDto, member2);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(400, responseEntity.getBody());

        //verify
        verify(bkBoardRepository, times(1)).findById(postId);
    }
}
