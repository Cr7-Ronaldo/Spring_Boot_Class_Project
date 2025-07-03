package com.example.shop.controller;

// 장바구니 DTO, 엔티티, 서비스 import
import com.example.shop.dto.CartDetailDto;
import com.example.shop.dto.CartItemDto;
import com.example.shop.dto.CartOrderDto;
import com.example.shop.entity.Cart;
import com.example.shop.service.CartService;

// 유효성 검사 관련 import
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 스프링 관련 import
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // 로그인한 사용자의 정보를 가져오기 위한 인터페이스
import java.util.List;

@Controller // 스프링 MVC의 컨트롤러로 등록
@RequiredArgsConstructor // final로 선언된 필드를 자동으로 주입해주는 롬복 어노테이션
@Slf4j // 로그를 사용하기 위한 롬복 어노테이션
public class CartController {

    private final CartService cartService; // 장바구니 서비스 객체 (비즈니스 로직 처리용)

    // 장바구니에 상품을 추가하는 POST 요청 처리
    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity<?> order(
            @RequestBody @Valid CartItemDto cartItemDto, // 요청 본문에 담긴 JSON을 CartItemDto로 변환 및 유효성 검사
            BindingResult bindingResult, // 유효성 검사 결과 저장 객체
            Principal principal) { // 로그인된 사용자의 정보를 가져오는 객체

        // 유효성 검사에서 에러가 있을 경우
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder(); // 에러 메시지를 저장할 StringBuilder
            List<FieldError> fieldErrors = bindingResult.getFieldErrors(); // 필드별 에러 수집

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage()); // 에러 메시지 누적
            }

            // 에러 메시지를 포함하여 400 응답
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName(); // 현재 로그인한 사용자의 이메일
        Long cartItemId; // 장바구니에 담긴 아이템 ID 저장 변수

        try {
            // 장바구니에 상품 추가
            cartItemId = cartService.addCart(cartItemDto, email);
            log.info("cartItemId : {}", cartItemId); // 로그 출력
        } catch (Exception e) {
            // 예외 발생 시 400 에러 반환
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // 성공적으로 추가되었을 경우 201 응답과 함께 cartItemId 반환
        return new ResponseEntity<>(cartItemId, HttpStatus.CREATED);
    }   // end order

    // 장바구니 목록을 조회하여 cartList.html로 전달
    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model) {
        // 현재 로그인한 사용자의 장바구니 아이템 리스트를 가져옴
        List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());

        // 모델에 cartItems라는 이름으로 리스트 추가
        model.addAttribute("cartItems", cartDetailList);

        // cartList.html로 이동 (템플릿 렌더링)
        return "cart/cartList";
    }

    // 장바구니 수량 수정 요청 처리
    // ex) var url = "/cartItem/" + cartItemId+"?count=" + count;
    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity<?> updateCartItem(
            @PathVariable("cartItemId") Long cartItemId, // URL 경로에서 장바구니 아이템 ID 추출
            @RequestParam("count") int count, // 수정할 수량은 쿼리 파라미터로 전달
            Principal principal) { // 현재 로그인한 사용자 정보

        // 수량이 0 이하일 경우 예외 처리
        if(count <= 0) {
            return new ResponseEntity<>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        }
        // 사용자가 해당 장바구니 아이템에 대한 권한이 있는지 확인
        else if(!cartService.validateCartItem(cartItemId, principal.getName())) {
            return new ResponseEntity<>("수정 권한이 없습니다", HttpStatus.FORBIDDEN);
        }

        // 장바구니 수량 업데이트 처리
        cartService.updateCartItem(cartItemId, count);

        // 성공 응답 반환
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    // 장바구니 아이템 삭제 요청 처리
    // ex) var url = "/cartItem/" + cartItemId;
    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity<?> deleteCartItem(
            @PathVariable("cartItemId") Long cartItemId, // URL 경로에서 cartItemId 추출
            Principal principal) { // 로그인 사용자 정보

        // 사용자가 해당 장바구니 아이템을 삭제할 권한이 있는지 확인
        if(!cartService.validateCartItem(cartItemId, principal.getName())) {
            return new ResponseEntity<>("수정 권한이 없습니다", HttpStatus.FORBIDDEN);
        }

        // 아이템 삭제 수행
        cartService.deleteCartItem(cartItemId);

        // 성공 응답
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    } // end DeleteMapping

    //  var url = "/cart/orders";
    // paramData['cartOrderDtoList'] = dataList;
    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity<?> orders(@RequestBody CartOrderDto CartOrderDto,
                                                  Principal principal) {
        log.info("---------------orders------------------");
        log.info("cartOrderDto : {}", CartOrderDto);

        List<CartOrderDto> cartOrderDtoList = CartOrderDto.getCartOrderDtoList();

        cartOrderDtoList.forEach(orderDto -> log.info(orderDto.toString()));
        log.info("----------------------------");


        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0) {
            return new ResponseEntity<>("주문 상품을 선택해 주세요", HttpStatus.BAD_REQUEST);
        }

        for(CartOrderDto cartOrderDto : cartOrderDtoList) {
            if(!cartService.validateCartItem(cartOrderDto.getCartItemId(), principal.getName())) {
                return new ResponseEntity<>("수정 권한이 없습니다", HttpStatus.FORBIDDEN);
            }

        }

        Long orderId = cartService.OrderCartItem(cartOrderDtoList, principal.getName());

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
