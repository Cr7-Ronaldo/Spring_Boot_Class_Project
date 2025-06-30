package com.example.shop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        //현재 로그인 한 사용자 정보를 가져온다.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        log.info("Current user: {}", authentication.getName());

        String userId = "";
        if(authentication != null) {
            userId = authentication.getName();
        }

        return Optional.of(userId);
    }
}
