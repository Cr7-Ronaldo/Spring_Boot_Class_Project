package com.example.shop.service;

import com.example.shop.dto.MemberFormDto;
import com.example.shop.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(){

        MemberFormDto memberFormDto = MemberFormDto.builder()
                .email("test@email.com")
                .name("Test")
                .address("서울시 강동구")
                .password("1234")
                .build();

        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    public void saveMember(){
        Member member = createMember();
        Member savedMember = memberService.save(member);

        assertEquals(savedMember.getEmail(), member.getEmail());
        assertEquals(savedMember.getRole(), member.getRole());
    }
}