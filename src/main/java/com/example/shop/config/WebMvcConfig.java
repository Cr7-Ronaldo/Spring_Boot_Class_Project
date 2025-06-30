package com.example.shop.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${uploadPath}")
    String uploadPath;


    //http://localhost:8080/images/test.jpg  --> 이렇게 요청이 들어오면
    //c:/upload/item/test.jpg
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 정적 자원의 경로 매핑을 설정하기 위한 메서드 (Spring MVC에서 오버라이드하여 사용)

        registry.addResourceHandler("/images/**")
                // 웹 브라우저에서 "/images/파일명"으로 접근하면,
                // 실제 파일이 저장된 위치(uploadPath)에 있는 파일을 제공하도록 매핑 설정

                .addResourceLocations(uploadPath);
        // 실제 파일이 저장된 물리적 경로 또는 URL 경로
        // 예: "file:///C:/upload/" 또는 "classpath:/static/images/"
    }
}
