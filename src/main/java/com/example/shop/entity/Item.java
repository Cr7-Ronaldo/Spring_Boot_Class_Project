package com.example.shop.entity;

// 상품 판매 상태(SELL, SOLD_OUT 등)를 정의한 enum
import com.example.shop.constant.ItemSellStatus;

// 상품 등록/수정 폼 데이터를 전달받는 DTO
import com.example.shop.dto.ItemFormDto;

// 재고가 부족할 때 발생시키는 커스텀 예외
import com.example.shop.exception.OutOfStockException;

// JPA 관련 어노테이션
import jakarta.persistence.*;

// Lombok - getter, setter, toString 자동 생성
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 상품 정보를 담는 JPA 엔티티 클래스
 * 테이블명: item
 */
@Entity
@Table(name = "item")
@Getter  // 모든 필드에 대해 getter 메소드 자동 생성
@Setter  // 모든 필드에 대해 setter 메소드 자동 생성
@ToString // toString 메소드 자동 생성
public class Item extends BaseEntity { // BaseEntity: 등록일, 수정일, 등록자, 수정자 등 공통 필드 상속

    // -------------------- [기본 키 설정] --------------------
    @Id  // 기본 키(PK) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 방식
    @Column(name="item_id") // 실제 DB 컬럼명
    private Long id;  // 상품 고유 코드 (PK)

    // -------------------- [일반 속성들] --------------------
    @Column(nullable = false, length = 50) // null 불가 + 최대 50자 제한
    private String itemNm; // 상품명

    @Column(nullable = false, name="price") // null 불가, 컬럼명 명시
    private int price; // 상품 가격

    @Column(nullable = false)
    private int stockNumber; // 현재 재고 수량

    @Lob  // CLOB 또는 TEXT 컬럼으로 매핑됨 (길이가 긴 문자열에 적합)
    @Column(nullable = false)
    private String itemDetail; // 상품 상세 설명

    @Enumerated(EnumType.STRING) // Enum을 문자열로 DB에 저장
    private ItemSellStatus itemSellStatus; // 상품 판매 상태 (SELL, SOLD_OUT 등)

    // -------------------- [연관 관계는 따로 없음] --------------------
    // 이 Item 클래스 자체는 OrderItem과의 직접적인 연관관계를 선언하지 않음.
    // 연관 관계의 주인은 OrderItem 쪽임.
    // OrderItem에서 ManyToOne으로 Item을 참조함:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "item_id")
    // private Item item;

    // -------------------- [비즈니스 로직: 수정] --------------------
    /**
     * 상품 수정 메소드
     * - DTO에서 받은 값을 엔티티에 반영
     */
    public void upateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();              // 이름 수정
        this.price = itemFormDto.getPrice();                // 가격 수정
        this.stockNumber = itemFormDto.getStockNumber();    // 재고 수정
        this.itemDetail = itemFormDto.getItemDetail();      // 상세 설명 수정
        this.itemSellStatus = itemFormDto.getItemSellStatus(); // 판매 상태 수정
    }

    // -------------------- [비즈니스 로직: 재고 감소] --------------------
    /**
     * 재고 감소 메소드
     * - 주문 시 사용
     * - 재고가 부족할 경우 예외 발생
     */
    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber; // 차감 후 잔여 재고 계산

        if(restStock < 0) {
            // 재고 부족 예외 발생
            throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고 수량: "
                    + this.stockNumber + ")");
        }

        this.stockNumber = restStock; // 재고 반영
    }
}
