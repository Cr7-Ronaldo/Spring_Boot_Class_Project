package com.example.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter@Setter
@Table(name = "item_img")
public class ItemImg extends BaseEntity{

    @Id
    @Column(name="item_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgName; //이미지 파일명

    private String oriImgName; //원본 이미지 명

    private String imgUrl; //이미지 조회 경로

    private String repimgYn; //대표이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    public void updateItemImg(String oriImgName, String imgName, String imgUrl){
        this.imgName = imgName;
        this.imgUrl = imgUrl;
        this.oriImgName = oriImgName;
    }
}
