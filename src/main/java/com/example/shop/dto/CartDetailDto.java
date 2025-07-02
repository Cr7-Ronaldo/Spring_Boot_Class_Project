package com.example.shop.dto; // DTO 클래스가 속한 패키지 경로

import lombok.Getter;
import lombok.Setter;

// Lombok 어노테이션
// → 각 필드에 대해 getter/setter 메서드를 자동 생성해줌
@Getter
@Setter
public class CartDetailDto {

    // 장바구니 상세 정보를 담기 위한 DTO (Data Transfer Object)

    // 장바구니 상품의 고유 ID (CartItem 엔티티의 PK)
    private Long cartItemId;

    // 상품명 (Item 엔티티의 itemNm 필드)
    private String itemNm;

    // 상품 가격 (Item 엔티티의 price 필드)
    private int price;

    // 장바구니에 담긴 수량 (CartItem의 count 필드)
    private int count;

    // 대표 상품 이미지의 경로 (ItemImg 엔티티의 imgUrl 필드)
    private String imgUrl;

    // 모든 필드를 초기화하는 생성자
    // → JPQL 쿼리에서 new 명령어로 바로 객체 생성 시 사용됨
    public CartDetailDto(Long cartItemId, String itemNm, int price, int count, String imgUrl) {
        this.cartItemId = cartItemId; // 장바구니 상품 ID 설정
        this.itemNm = itemNm;         // 상품명 설정
        this.price = price;           // 가격 설정
        this.count = count;           // 수량 설정
        this.imgUrl = imgUrl;         // 이미지 경로 설정
    }
}
