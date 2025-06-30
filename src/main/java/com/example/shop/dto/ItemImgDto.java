package com.example.shop.dto;

import com.example.shop.entity.ItemImg;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter@Setter
public class ItemImgDto {

    private Long id;

    private String imgName; //이미지 파일명

    private String oriImgName; //원본 이미지 명

    private String imgUrl; //이미지 조회 경로

    private String repimgYn; //대표이미지 여부

    private static ModelMapper modelMapper = new ModelMapper();

    //ItemImg 객체를 전달받아서, modelMapper이 ItemImg객체를 ItemImgDto객체로 자동 변환
    public static ItemImgDto of(ItemImg itemImg) {

        return modelMapper.map(itemImg, ItemImgDto.class);
    }
}
