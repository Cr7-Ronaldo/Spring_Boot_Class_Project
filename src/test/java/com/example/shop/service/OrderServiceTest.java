// 서비스 계층 테스트를 위한 클래스 위치
package com.example.shop.service;

// 주문 생성에 필요한 DTO
import com.example.shop.dto.OrderDto;

// 회원, 주문 엔티티
import com.example.shop.dto.OrderHisDto;
import com.example.shop.entity.Member;
import com.example.shop.entity.Order;

// 주문 저장소 (JPA Repository)
import com.example.shop.repository.OrderRepository;

// 로그 출력을 위한 Lombok 어노테이션
import lombok.extern.slf4j.Slf4j;

// JUnit 5 테스트 어노테이션
import org.junit.jupiter.api.Test;

// 스프링 테스트에서 의존성 주입 등을 사용할 수 있도록 해주는 어노테이션
import org.springframework.beans.factory.annotation.Autowired;

// SpringBoot 전체 컨텍스트를 로딩하여 테스트
import org.springframework.boot.test.context.SpringBootTest;

// 인증 사용자 주입 (Spring Security 테스트 지원)
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;

// 트랜잭션 적용 (세션 유지용)
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

// JUnit 검증 메서드
import static org.junit.jupiter.api.Assertions.*;

// 실제 SpringBoot 테스트 클래스임을 명시
@SpringBootTest

// 클래스 전체에 로그를 사용할 수 있게 설정 (Slf4j는 log.info() 사용 가능)
@Slf4j

// 테스트 중 인증된 사용자 정보를 주입함 (AuditorAware에서 사용됨)
@WithMockUser(username = "test@test.com", roles = "ADMIN")
class OrderServiceTest {

    // 주문 서비스 의존성 주입 (테스트 대상)
    @Autowired
    private OrderService orderService;

    // 주문 저장소(Repository) 의존성 주입 (저장된 주문을 확인하기 위해 사용)
    @Autowired
    private OrderRepository orderRepository;

    // 테스트 메서드에만 트랜잭션 적용 (→ 세션을 유지해서 LAZY 로딩 가능하게 함)
    @Transactional
    // @Rollback(false)
    // JUnit 테스트 메서드 선언
    @Test
    public void test() {

        // 로그인된 사용자 이메일 설정 (AuditorAware, 회원 조회용)
        String email = "test@test.com";

        // 주문 정보를 담을 DTO 객체 생성
        OrderDto orderDto = new OrderDto();

        // 수량 설정 (예: 맥주 2병 주문)
        orderDto.setCount(2);

        // 상품 ID 설정 (상품이 DB에 이미 존재해야 함)
        orderDto.setItemId(1L);

        // 주문 서비스 호출 → 실제 주문을 생성하고 DB에 저장
        Long order = orderService.order(orderDto, email);

        // 주문 ID 확인 (정상적으로 저장되었는지 로그로 출력)
        log.info("---------order---------- : {}", order);

        // 저장된 주문을 DB에서 다시 조회 (검증용)
        Order savedOrder = orderRepository.findById(order)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 주문 객체 출력 (toString 결과)
        log.info("---------savedOrder---------- : {}", savedOrder);

        // 주문 상세 정보 출력 (지연 로딩으로 orderItems에 접근 → 세션 필요 → @Transactional 필수)
        savedOrder.getOrderItems()
                .forEach(orderitem -> log.info("OrderItem : {}", orderitem));
    }

    @Transactional // 테스트 실행 시 트랜잭션 적용 (테스트 후 자동 롤백)
    @Test // JUnit 테스트 메서드임을 나타냄
    public void getOrderListTest() {

        // 테스트할 회원 이메일 설정
        String email = "test@test.com";

        // 페이지 요청 정보 설정: 0페이지, 1페이지당 5건
        Pageable pageable = PageRequest.of(0, 5);

        // 주문 내역 조회 서비스 호출 (회원 이메일과 페이징 정보 전달)
        Page<OrderHisDto> orderHisDtoList = orderService.getOrderList(email, pageable);

        // 조회된 주문 내역 리스트 출력 (콘솔 로그로 확인)
        orderHisDtoList.getContent().forEach(list -> log.info("OrderList : {}", list));

        // 전체 주문 건수 출력
        log.info("totalCount : {}", orderHisDtoList.getTotalElements());
    }

}
