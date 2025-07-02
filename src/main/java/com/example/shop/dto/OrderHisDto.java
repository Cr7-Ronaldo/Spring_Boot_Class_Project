// DTO(Data Transfer Object) 패키지에 위치한 클래스
package com.example.shop.dto;

// 주문 상태를 정의한 enum 클래스 import
import com.example.shop.constant.OrderStatus;

// 주문 엔티티 클래스 import
import com.example.shop.entity.Order;

// Lombok 라이브러리로, getter 자동 생성
import lombok.Getter;

// setter 자동 생성
import lombok.Setter;

// toString() 메서드 자동 생성 (객체 정보 출력용)
import lombok.ToString;

// 날짜 포맷 변경을 위한 클래스 import
import java.time.format.DateTimeFormatter;

// 주문 상품 정보를 담기 위한 리스트 import
import java.util.ArrayList;
import java.util.List;

// Lombok을 통해 Getter, Setter, toString 메서드 자동 생성
@Getter
@Setter
@ToString
public class OrderHisDto {

    // 주문 번호 (Order의 PK 값)
    private Long orderId;

    // 주문 날짜 (yyyy-MM-dd HH:mm 형식의 문자열로 저장)
    private String orderDate;

    // 주문 상태 (enum 타입: 예 - ORDER, CANCEL)
    private OrderStatus orderStatus;

    // 주문한 상품들의 리스트 (OrderItemDto 객체로 구성)
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    /**
     * Order 엔티티를 인자로 받아, 해당 주문의 주요 정보만 DTO로 변환
     * @param order 주문 엔티티 객체
     */
    public OrderHisDto(Order order) {
        // 주문의 고유 ID를 가져와 저장
        this.orderId = order.getId();

        // 주문 날짜를 원하는 형식(yyyy-MM-dd HH:mm)으로 포맷팅하여 문자열로 저장
        this.orderDate = order.getOrderDate().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        );

        // 주문 상태(enum)를 그대로 저장
        this.orderStatus = order.getOrderStatus();
    }

    /**
     * 주문 상품 정보를 리스트에 추가하는 메서드
     * @param orderItemDto 주문 상품 DTO 객체
     */
    public void addOrderItemDto(OrderItemDto orderItemDto) {
        // 리스트에 상품 정보 추가
        this.orderItemDtoList.add(orderItemDto);
    }
}
