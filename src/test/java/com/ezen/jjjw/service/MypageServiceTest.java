package com.ezen.jjjw.service;

import com.ezen.jjjw.repository.BkBoardRepository;
import com.ezen.jjjw.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    void test() {
        //given

        //stub

        //when

        //then

        //verify
    }
}
