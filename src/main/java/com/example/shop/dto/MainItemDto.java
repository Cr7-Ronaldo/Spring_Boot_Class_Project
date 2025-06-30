package com.example.shop.dto;

import
        com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Setter@Getter
public class MainItemDto {

    private Long id;
    private String itemNm;
    private String itemDetail;
    private String imgUrl;
    private Integer price;

    @QueryProjection // Querydsl 결과 조회 나온 결과를 MainItemDto 객체로 전달 받음
    public  MainItemDto(Long id, String itemNm, String itemDetail,
                        String itemUrl, Integer price) {
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = itemUrl;
        this.price = price;
    }
}