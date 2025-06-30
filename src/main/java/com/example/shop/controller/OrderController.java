package com.example.shop.controller; // 컨트롤러 클래스가 위치한 패키지 경로

import com.example.shop.dto.OrderDto; // 주문 정보를 담는 DTO 클래스 import
import com.example.shop.service.OrderService; // 주문 비즈니스 로직을 처리하는 서비스 클래스 import
import jakarta.validation.Valid; // 요청 데이터의 유효성 검사를 위한 어노테이션
import lombok.RequiredArgsConstructor; // final 필드 생성자 자동 생성 어노테이션
import lombok.extern.slf4j.Slf4j; // 로깅 기능 제공 어노테이션
import org.springframework.http.HttpStatus; // HTTP 상태 코드 사용을 위한 클래스
import org.springframework.http.ResponseEntity; // HTTP 응답 데이터를 담는 객체
import org.springframework.stereotype.Controller; // Spring MVC 컨트롤러임을 나타내는 어노테이션
import org.springframework.ui.Model; // 뷰(View)로 데이터 전달 시 사용하는 객체 (이 코드에서는 사용되지 않음)
import org.springframework.validation.BindingResult; // 유효성 검사 결과를 담는 객체
import org.springframework.validation.FieldError; // 필드 단위 유효성 오류 정보를 담는 객체
import org.springframework.web.bind.annotation.PostMapping; // POST 요청을 처리하기 위한 어노테이션
import org.springframework.web.bind.annotation.RequestBody; // 요청 본문을 자바 객체로 매핑하는 어노테이션
import org.springframework.web.bind.annotation.RequestParam; // 요청 파라미터 값을 가져오는 어노테이션 (이 코드에서는 사용되지 않음)
import org.springframework.web.bind.annotation.ResponseBody; // 반환값을 HTTP 응답 본문으로 보내도록 지정하는 어노테이션

import java.security.Principal; // 로그인한 사용자의 정보를 담는 객체
import java.util.List; // 리스트 자료형 import

@Controller // 해당 클래스를 Spring MVC에서 컨트롤러로 인식하도록 지정
@RequiredArgsConstructor // final 필드에 대해 생성자를 자동 생성 (orderService 주입)
@Slf4j // log.info(), log.error() 등 로그 메서드를 사용할 수 있도록 해주는 어노테이션
public class OrderController {

    private final OrderService orderService; // 주문 비즈니스 로직을 수행할 서비스 객체

    @PostMapping("/order") // "/order" 경로로 POST 요청이 들어오면 이 메서드가 실행됨
    public @ResponseBody ResponseEntity<?> order(
            @RequestBody @Valid OrderDto orderDto, // JSON 요청 데이터를 OrderDto 객체로 받고 유효성 검사 수행
            BindingResult bindingResult, // 유효성 검사 결과를 담는 객체
            Principal principal) { // 로그인한 사용자의 정보를 담고 있는 객체

        if(bindingResult.hasErrors()){ // 유효성 검사에 실패한 경우
            StringBuilder sb = new StringBuilder(); // 에러 메시지를 누적할 StringBuilder 생성

            List<FieldError> fieldErrors = bindingResult.getFieldErrors(); // 유효성 검사 실패한 필드 목록 가져오기

            for(FieldError fieldError : fieldErrors){ // 모든 오류 필드를 반복
                sb.append(fieldError.getDefaultMessage()); // 각 오류의 기본 메시지를 누적
            }

            log.info("sb >>>>>>>>>>> : {}", sb.toString()); // 누적된 오류 메시지를 로그로 출력

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST); // 400 상태와 함께 에러 메시지 반환
        }

        String email = principal.getName(); // 로그인한 사용자의 이메일을 가져옴
        Long orderId = 0L; // 주문 ID 초기화
        try {
            orderId = orderService.order(orderDto, email); // 주문 생성 로직 호출 (주문 DTO와 사용자 이메일 전달)
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST); // 예외 발생 시 400 상태와 에러 메시지 반환
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK); // 주문 성공 시 주문 ID와 200 OK 상태 반환
    }
}
