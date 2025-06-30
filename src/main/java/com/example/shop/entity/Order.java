package com.example.shop.entity; // 엔티티 클래스가 위치한 패키지

// 주문 상태(ORDER, CANCEL 등)를 정의한 enum 타입 import
import com.example.shop.constant.OrderStatus;

// JPA 어노테이션 관련 import
import jakarta.persistence.*;

// Lombok을 사용하여 getter/setter 메서드를 자동 생성
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime; // 날짜/시간 관련 클래스
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 정보를 담는 JPA 엔티티 클래스
 * 테이블 이름은 'order'가 SQL 예약어이므로 'orders'로 지정
 */
@Entity // JPA에서 엔티티로 인식하도록 설정
@Table(name = "orders") // 테이블 이름을 "orders"로 명시
@Getter // Lombok: 모든 필드의 getter 메서드 자동 생성
@Setter // Lombok: 모든 필드의 setter 메서드 자동 생성
//@ToString // 순환 참조 방지를 위해 주석 처리됨
public class Order extends BaseEntity { // 등록일, 수정일, 등록자, 수정자 등을 상속받는 엔티티

    // -------------------- [기본 키] --------------------
    @Id // 기본 키(PK) 필드임을 명시
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동으로 ID 증가
    @Column(name="order_id") // 테이블의 컬럼명을 "order_id"로 지정
    private Long id; // 주문 번호 (기본 키)

    // -------------------- [연관 관계: Member] --------------------
    @ManyToOne(fetch = FetchType.LAZY) // 다대일(N:1) 관계, 지연 로딩 전략 사용
    @JoinColumn(name = "member_id") // 외래 키(FK) 이름을 "member_id"로 지정
    private Member member; // 주문한 회원 (한 명의 회원은 여러 개의 주문 가능)

    // -------------------- [연관 관계: OrderItem] --------------------
    @OneToMany(mappedBy = "order", // OrderItem 엔티티의 "order" 필드에 의해 매핑됨
            cascade = CascadeType.ALL, // Order 저장/삭제 시 OrderItem도 같이 저장/삭제됨
            orphanRemoval = true) // 리스트에서 제거된 객체는 자동으로 DB에서 삭제됨
    //@ToString.Exclude // 무한 순환 참조 방지를 위해 toString에서 제외 가능
    private List<OrderItem> orderItems = new ArrayList<>(); // 주문 항목 리스트 (초기값은 빈 리스트)

    // -------------------- [기타 필드] --------------------
    private LocalDateTime orderDate; // 주문 시간 (날짜+시간 정보)

    @Enumerated(EnumType.STRING) // enum 타입을 문자열로 저장 (예: "ORDER", "CANCEL")
    private OrderStatus orderStatus; // 주문 상태 (enum)

    // -------------------- [비즈니스 로직] --------------------

    /**
     * 주문 항목(OrderItem)을 현재 주문(Order)에 추가하는 메서드
     * - 양방향 연관관계를 동시에 설정해주는 편의 메서드
     */
    public void addOrderitem(OrderItem orderItem) {
        orderItems.add(orderItem);       // 리스트에 OrderItem 추가
        orderItem.setOrder(this);        // OrderItem 입장에서도 이 Order를 참조하도록 설정
    }

    /**
     * 주문 객체를 생성하는 정적 팩토리 메서드
     * - 주문한 회원과 주문 항목 리스트를 받아 Order 객체를 생성하고 초기화
     */
    public static Order createOrder(Member member, List<OrderItem> orderItemList) {

        Order order = new Order(); // 빈 주문 객체 생성

        order.setMember(member); // 주문자 설정

        // 주문 항목들을 주문에 추가 (양방향 관계도 설정됨)
        for (OrderItem orderItem : orderItemList) {
            order.addOrderitem(orderItem);
        }

        order.setOrderStatus(OrderStatus.ORDER); // 주문 상태를 ORDER로 설정
        order.setOrderDate(LocalDateTime.now()); // 현재 시각을 주문일로 설정

        return order; // 완성된 주문 객체 반환
    }

    /**
     * 주문 전체 금액 계산 메서드
     * - 각 주문 항목의 금액을 합산하여 반환
     */
    public int getTotalPrice() {
        int totalPrice = 0; // 합계 초기값
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice(); // 각 주문 항목의 금액을 누적
        }
        return totalPrice; // 총합 반환
    }

}
