package com.example.shop.dto;

import com.example.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@ToString
public class ItemSearchDto {

    private String searchDateType; //날짜 조회

    private ItemSellStatus itemSellStatus; //판매 상태 조회

    private String searchBy; // 상품명, 상품등록자 조회

    private String searchQuery = "";

}
