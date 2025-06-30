package com.example.shop.entity;

import com.example.shop.dto.MemberFormDto;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class CartTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    //@PersistenceContext -> @Autowired
    @PersistenceContext
    EntityManager em;


    public Member createMember(){
        MemberFormDto memberFormDto = MemberFormDto.builder()
                .email("test@email.com")
                .name("홍길동")
                .address("서울시 강남구")
                .password("1234")
                .build();

        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 맵핑 조회 테스트")
    public void findCartAndMemberTest(){

        Member member = createMember();
        memberRepository.save(member); //멤버 저장

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);   //카트 저장

        //em.flush();  //영속컨텍스트 있는 데이타를 DB저장
        //em.clear(); //영속컨텍스트 클리어

        Cart savedCart = cartRepository.findById(cart.getId())
                .orElseThrow(()-> new EntityNotFoundException("해당 ID가 존재하지 않아요"));

        log.info("savedCart ======>   {} ", savedCart);

        assertEquals(savedCart.getMember().getId(), member.getId());




    }
}