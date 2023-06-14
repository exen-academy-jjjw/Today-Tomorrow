package com.ezen.jjjw.configuration;

import com.ezen.jjjw.handler.AccessDeniedHandlerException;
import com.ezen.jjjw.handler.AuthenticationEntryPointException;
import com.ezen.jjjw.impl.UserDetailsServiceImpl;
import com.ezen.jjjw.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * packageName    : com.member.member_jwt.configuration
 * fileName       : SecurityConfiguration
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                시큐리티 설정을 위한 클래스
 *                                내용이 복잡해 자세한 주석을 붙이기 어려운 파트이니 이해가 가지 않는 코드의 경우 검색을 해보자
 *                                다만, 검색결과 extends WebSecurityConfigurerAdapter 를 사용하는 내용이 있을 텐데
 *                                해당 내용은 이전 버전이며 현재는 아래와 같이 SecurityFilterChain을 사용한다.
 *                                그 안에 들어가는 내용에는 크게 차이가 없으므로 참고하여 보도록 하자.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

    @Value("${jwt.secret}")
    String SECRET_KEY;
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationEntryPointException authenticationEntryPointException;
    private final AccessDeniedHandlerException accessDeniedHandlerException;

    // Bean으로 등록해주지 않으면 PasswordEncoder가 제대로 동작하지 않음
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable()

                // 맨처음에 권한 없으면 막기 위한 설정
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointException)
                .accessDeniedHandler(accessDeniedHandlerException)

                // 우리는 jwt를 사용할 것이기 때문에 세션이 필요하지 않음
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/member/signup").permitAll()
                .antMatchers("/member/login").permitAll()
                .antMatchers("/member/reissue").permitAll()
                // 그 외 요청은 권한 필요
                .anyRequest().authenticated()

                // jwt 설정
                .and()
                .apply(new JwtSecurityConfiguration(SECRET_KEY, tokenProvider, userDetailsService));
        return http.build();
    }
}
