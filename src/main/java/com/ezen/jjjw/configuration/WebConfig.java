package com.ezen.jjjw.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * packageName    : com.member.member_jwt.configuration
 * fileName       : WebConfig
 * author         : sonjia
 * date           : 2023-06-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-06-08        sonjia       최초 생성
 *                                프론트랑 연결할 때 포트를 열어줘야 해서 만든 클래스
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 추가
    private String resourcePath = "/review/**"; // view 에서 접근할 경로
    private String savePath = "file:///C:/upload/"; // 실제 파일 저장 경로(win)

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(resourcePath)
                .addResourceLocations(savePath);
    }
    ///---///

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PATCH", "PUT", "OPTIONS", "DELETE")
                .exposedHeaders("Authorization", "RefreshToken");
    }
}