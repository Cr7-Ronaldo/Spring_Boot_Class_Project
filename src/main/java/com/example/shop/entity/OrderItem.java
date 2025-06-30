package com.example.shop.entity;

// JPA 어노테이션 임포트
import jakarta.persistence.*;

// Lombok을 통해 getter, setter, toString 자동 생성
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 주문 상세(주문 상품 한 개 단위)를 나타내는 JPA 엔티티 클래스
 * 테이블명: order_item
 */
@Entity
@Table(name="order_item") // 실제 DB에 생성될 테이블 이름
@Getter
@Setter
@ToString
public class OrderItem extends BaseEntity { // BaseEntity로부터 등록일, 수정일 등의 공통 필드를 상속받음

    // -------------------- [기본 키] --------------------
    @Id
    @GeneratedValue // 기본 키 자동 생성 (strategy = AUTO 기본)
    @Column(name="order_item_id") // 컬럼명 지정
    private Long id; // 주문 상세 고유 ID (PK)

    // -------------------- [연관 관계: Item] --------------------
    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계 (N:1), 지연 로딩 설정
    @JoinColumn(name = "item_id") // 외래 키(item_id) 설정
    private Item item; // 주문한 상품 (하나의 상품이 여러 주문에 들어갈 수 있음)

    // -------------------- [연관 관계: Order] --------------------
    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계 (N:1), 지연 로딩 설정
    @JoinColumn(name = "order_id") // 외래 키(order_id) 설정
//    @ToString.Exclude // 순환 참조 방지 (toString 무한루프 가능성 차단용 주석 처리됨)
    private Order order; // 이 주문 상세가 속한 주문 (하나의 주문은 여러 주문 상세를 가짐)

    // -------------------- [일반 속성] --------------------
    private int orderPrice; // 상품 주문 가격 (당시 가격 기준)
    private int count; // 주문 수량

    // -------------------- [비즈니스 로직] --------------------

    /**
     * 주문 상세 객체 생성 메소드 (정적 팩토리 메소드)
     * - item: 어떤 상품인지
     * - count: 몇 개 주문했는지
     * - 내부적으로: 가격 설정 + 재고 차감
     */
    public static OrderItem createOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem(); // 객체 생성

        orderItem.setItem(item);               // 상품 지정
        orderItem.setCount(count);             // 수량 지정
        orderItem.setOrderPrice(item.getPrice()); // 주문 당시 상품 가격 설정

        item.removeStock(count);               // 재고 차감 (Item 클래스 내부 로직 호출)

        return orderItem;
    }

    /**
     * 주문한 상품의 총 가격 계산
     * - 단가 × 수량
     */
    //주문할 때 마다 총합
    public int getTotalPrice() {
        return this.getOrderPrice() * this.count;
    }
}
