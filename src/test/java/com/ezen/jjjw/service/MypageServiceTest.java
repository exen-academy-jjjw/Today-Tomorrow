package com.ezen.jjjw.service;

import com.ezen.jjjw.domain.entity.BkBoard;
import com.ezen.jjjw.domain.entity.Member;
import com.ezen.jjjw.dto.request.MypageRequestDto;
import com.ezen.jjjw.dto.response.MypageResponseDto;
import com.ezen.jjjw.repository.BkBoardRepository;
import com.ezen.jjjw.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * packageName    : com.ezen.jjjw.service
 * fileName       : MypageServiceTest
 * author         : wldk9
 * date           : 2023-10-19
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-10-19        sonjia       최초 생성
 */
@ExtendWith(MockitoExtension.class)
public class MypageServiceTest {

    @InjectMocks
    private MypageService mypageService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BkBoardRepository bkBoardRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void success_getFindMember() {
        //given
        Member member = new Member();
        member.setId(1L);
        member.setMemberId("testUser");
        member.setNickname("testNick");
        member.setPassword(passwordEncoder.encode("password"));

        List<BkBoard> bkBoardList = new ArrayList<>();
        for(int i = 1; i <= 5; i++) {
            BkBoard bkBoard = BkBoard.builder()
                    .postId((long) i)
                    .title("title")
                    .content("content")
                    .member(member)
                    .completion(0)
                    .share(0)
                    .build();
            bkBoardList.add(bkBoard);
        }

        for(int j = 6; j <= 8; j++) {
            BkBoard bkBoard = BkBoard.builder()
                    .postId((long) j)
                    .title("title")
                    .content("content")
                    .member(member)
                    .completion(1)
                    .share(0)
                    .build();
            bkBoardList.add(bkBoard);
        }

        member.setBkBoardList(bkBoardList);

        //stub
        when(bkBoardRepository.countByMemberId(member.getId())).thenReturn(bkBoardList.size());
        when(bkBoardRepository.countByMemberIdAndCompletion(member.getId(), 1)).thenReturn(3);

        //when
        ResponseEntity<MypageResponseDto> responseEntity = mypageService.getFindMember(member);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("testNick", responseEntity.getBody().getNickname());
        assertEquals(8, responseEntity.getBody().getTotalCount());
        assertEquals(3, responseEntity.getBody().getCompleteCount());
        assertEquals(5, responseEntity.getBody().getUnCompleteCount());

        //verify
        verify(bkBoardRepository, times(1)).countByMemberId(member.getId());
        verify(bkBoardRepository, times(1)).countByMemberIdAndCompletion(member.getId(), 1);
    }

    @Test
    void success_updateNick() {
        //given
        Member member = Member.builder()
                .id(1L)
                .memberId("testUser")
                .nickname("testNick")
                .password(passwordEncoder.encode("password"))
                .build();

        MypageRequestDto mypageRequestDto = new MypageRequestDto();
        mypageRequestDto.setNickname("updateNick");

        //stub
        when(memberRepository.findByNickname(mypageRequestDto.getNickname())).thenReturn(Optional.empty());
        when(memberRepository.save(member)).thenReturn(any(Member.class));

        //when
        ResponseEntity<Integer> responseEntity = mypageService.updateNick(mypageRequestDto, member);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());

        //verify
        verify(memberRepository, times(1)).findByNickname(mypageRequestDto.getNickname());
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void fail_updateNick_cause_member() {
        //given
        Member member = null;

        MypageRequestDto mypageRequestDto = new MypageRequestDto();
        mypageRequestDto.setNickname("updateNick");

        //when
        ResponseEntity<Integer> responseEntity = mypageService.updateNick(mypageRequestDto, member);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(404, responseEntity.getBody());
    }

    @Test
    void fail_updateNick_cause_nickname() {
        //given
        Member member = Member.builder()
                .id(1L)
                .memberId("testUser")
                .nickname("testNick")
                .password(passwordEncoder.encode("password"))
                .build();

        MypageRequestDto mypageRequestDto = new MypageRequestDto();
        mypageRequestDto.setNickname("updateNick");

        //stub
        when(memberRepository.findByNickname(mypageRequestDto.getNickname())).thenReturn(Optional.ofNullable(member));

        //when
        ResponseEntity<Integer> responseEntity = mypageService.updateNick(mypageRequestDto, member);

        //then
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(400, responseEntity.getBody());

        //verify
        verify(memberRepository, times(1)).findByNickname(mypageRequestDto.getNickname());
    }

    @Test
    void success_updatePassword() throws JsonProcessingException {
        //given
        MypageRequestDto requestDto = new MypageRequestDto();
        requestDto.setPassword("password");
        requestDto.setNewPassword("updatePassword");

        Member member = Member.builder()
                .id(1L)
                .memberId("testUser")
                .nickname("testNick")
                .password(passwordEncoder.encode("password"))
                .build();

        System.out.println("패스워드 확인 : " + member.getPassword());
        //stub

        //when

        //then

        //verify
    }
}
