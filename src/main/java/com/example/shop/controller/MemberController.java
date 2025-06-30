package com.example.shop.controller;

import com.example.shop.dto.MemberFormDto;
import com.example.shop.entity.Member;
import com.example.shop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping(value = "/login")
    public String loginMember() {
        return "member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "member/memberLoginForm";
    }

    //localhost:8080/members/new
    //회원가입 폼 제공
    @GetMapping(value = "/new")
    public String MemberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/new")
    public String MemberForm(@Valid MemberFormDto memberFormDto,
                             BindingResult bindingResult, Model model) {

        // @Valid : memberFormDto의 필드에 설정된 유효성 검사를 수행
        // BindingResult : 유효성 검사 후 오류 정보를 담는 객체
        // Model : 뷰에 데이터를 전달하기 위한 Spring의 모델 객체

        if (bindingResult.hasErrors()) {
            // 유효성 검사에서 오류가 발생한 경우 회원가입 폼으로 다시 이동
            return "member/memberForm";
        }

        try {
            // 폼에서 받은 정보를 바탕으로 Member 엔티티 객체 생성 (비밀번호 암호화 포함)
            Member member = Member.createMember(memberFormDto, passwordEncoder);

            // 생성한 회원 정보를 DB에 저장
            memberService.save(member);

        } catch (IllegalArgumentException e) {
            // 회원 생성 중 IllegalArgumentException 발생 시 에러 메시지를 모델에 담아서 폼 페이지로 이동
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        // 회원가입 성공 시 홈 페이지로 리다이렉트
        return "redirect:/";
    }
}
