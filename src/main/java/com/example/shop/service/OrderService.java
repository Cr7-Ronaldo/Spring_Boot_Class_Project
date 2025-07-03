package com.example.shop.service; // 서비스 클래스가 위치한 패키지

// -------------------- [DTO 및 엔티티 import] --------------------
import com.example.shop.dto.OrderDto;
import com.example.shop.dto.OrderHisDto;
import com.example.shop.dto.OrderItemDto;
import com.example.shop.entity.Item;
import com.example.shop.entity.ItemImg;
import com.example.shop.entity.Member;
import com.example.shop.entity.Order;
import com.example.shop.entity.OrderItem;

// -------------------- [JPA Repository import] --------------------
import com.example.shop.repository.ItemImgRepository;
import com.example.shop.repository.ItemRepository;
import com.example.shop.repository.MemberRepository;
import com.example.shop.repository.OrderRepository;

// -------------------- [예외 처리 관련] --------------------
import jakarta.persistence.EntityNotFoundException;

// -------------------- [Lombok 및 스프링 import] --------------------
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 주문 처리 로직을 담당하는 서비스 클래스
 */
@Service // 해당 클래스를 스프링의 서비스 컴포넌트로 등록
@RequiredArgsConstructor // final 필드 기반으로 생성자 자동 생성
@Transactional // 모든 public 메서드에 트랜잭션 적용
@Slf4j // 로그 기능을 위한 Lombok 어노테이션 (log.info 등 사용 가능)
public class OrderService {

    // -------------------- [필드: 의존성 주입] --------------------
    private final OrderRepository orderRepository;       // 주문 저장소
    private final ItemRepository itemRepository;         // 상품 저장소
    private final MemberRepository memberRepository;     // 회원 저장소
    private final ItemImgRepository itemImgRepository;   // 상품 이미지 저장소

    // -------------------- [주문 생성 메서드] --------------------
    /**
     * 주문 생성 로직
     * @param orderDto 주문 요청 정보 (상품 ID, 수량 등)
     * @param email 주문자 이메일 (회원 조회용)
     * @return 생성된 주문의 ID
     */
    public Long order(OrderDto orderDto, String email) {

        // 1. 상품 ID로 상품 조회 (없으면 예외 발생)
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException());

        // 2. 이메일로 회원 조회
        Member member = memberRepository.findByEmail(email);

        // 3. 주문 항목 리스트 생성
        List<OrderItem> orderItemList = new ArrayList<>();

        // 4. 주문 항목 객체 생성 (상품, 수량 포함)
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());

        // 5. 리스트에 주문 항목 추가
        orderItemList.add(orderItem);

        // 6. 주문 생성 (정적 팩토리 메서드 사용)
        Order order = Order.createOrder(member, orderItemList);

        // 7. 주문 저장 (Cascade 설정으로 orderItem도 함께 저장됨)
        orderRepository.save(order);

        // 8. 주문 ID 반환
        return order.getId();
    }

    // -------------------- [주문 이력 조회 메서드] --------------------
    /**
     * 주문 이력 목록 조회 (회원 이메일 기반, 페이징 포함)
     * @param email 조회할 회원 이메일
     * @param pageable 페이징 정보
     * @return 주문 이력 DTO 리스트 (페이지 형식)
     */
    @Transactional(readOnly = true) // 조회만 수행하므로 readOnly 설정
    public Page<OrderHisDto> getOrderList(String email, Pageable pageable){

        // 1. 이메일로 해당 회원의 주문 리스트 조회 (페이징 적용됨)
        List<Order> orders = orderRepository.findOrders(email, pageable);

        log.info("-----------------------------------");
        orders.forEach(order -> log.info(order.toString()));

        // 2. 총 주문 수 조회 (페이지 계산용)
        Long totalcount = orderRepository.countOrder(email);


        // 3. 최종 반환할 주문 이력 DTO 리스트 생성
        List<OrderHisDto> orderHisDtoList = new ArrayList<>();

        // 4. 각 주문에 대해 DTO 변환 작업 수행
        for (Order order : orders) {
            OrderHisDto orderHisDto = new OrderHisDto(order); // 주문 → DTO

            Member member = order.getMember();
            log.info(member.toString());

            List<OrderItem> orderItems = order.getOrderItems(); // 주문 항목 목록 가져오기

            // 5. 각 주문 항목에 대해 상품 이미지 포함하여 DTO로 변환
            for (OrderItem orderItem : orderItems) {

                // 대표 이미지 조회 (repimgYn이 'Y'인 이미지)
                ItemImg itemImg = itemImgRepository
                        .findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");

                // 주문 상품 항목 DTO 생성 (상품 + 이미지 URL)
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());

                // 주문 이력 DTO에 항목 추가
                orderHisDto.addOrderItemDto(orderItemDto);
            }

            // 리스트에 완성된 주문 이력 DTO 추가
            orderHisDtoList.add(orderHisDto);
        }

        // 6. PageImpl 객체로 래핑하여 반환 (Pageable + 총개수 포함)
        return new PageImpl<>(orderHisDtoList, pageable, totalcount);
    }

    //email(로그인 사용자), orderId(주문번호)
    public boolean validateOrder(Long orderId, String email) {

        Member curmember = memberRepository.findByEmail(email);

        Order order = orderRepository.findById(orderId).
                orElseThrow(() -> new EntityNotFoundException());

        Member savedmember = order.getMember();

        if(!StringUtils.equals(curmember.getEmail(), savedmember.getEmail())) {
            return false;
        }
        return true;
    }





    //주문 취소
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException());

        order.cancelOrder();
    }

    //주문
    public Long orders(List<OrderDto> orderDtoList, String email) {

        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(() -> new EntityNotFoundException());

            OrderItem orderItem =
                    OrderItem.createOrderItem(item, orderDto.getCount());

            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);

        orderRepository.save(order);

        return order.getId();
    } // end orders
}
