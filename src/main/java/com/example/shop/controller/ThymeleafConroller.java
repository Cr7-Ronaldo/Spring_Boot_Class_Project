package com.example.shop.controller;

import com.example.shop.dto.ItemDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/thymeleaf")
public class ThymeleafConroller {

    @GetMapping(value = "/ex01")
    public String ThymeleafExample01(Model model) {
        model.addAttribute("data", "타임리프 예제입니다.");

        model.addAttribute("message", "<strong>굵은글씨</strong>");

        return "thymeleafEx/thymeleafEx01";
    }

    @GetMapping(value = "/ex02")
    public String ThymeleafExample02(Model model) {

        ItemDto itemDto = ItemDto.builder()
                .itemDetail("상품 상세 설명")
                .itemNm("테스트 상품1")
                .price(10000)
                .regTime(LocalDateTime.now())
                .build();

        model.addAttribute("itemDto", itemDto);

        return "thymeleafEx/thymeleafEx02";
    }

    @GetMapping(value = "/ex03")
    public String ThymeleafExample03(Model model) {

        List<ItemDto> itemDtoList = new ArrayList<>();

        for(int i = 1; i <= 10; i++) {
            ItemDto itemDto = ItemDto.builder()
                    .itemDetail("상품 상세 설명"+i)
                    .itemNm("테스트 상품"+i)
                    .price(1000*i)
                    .regTime(LocalDateTime.now())
                    .build();

            itemDtoList.add(itemDto);
        }

        model.addAttribute("itemDtoList", itemDtoList);

        return "thymeleafEx/thymeleafEx03";
    }

    @GetMapping(value = "/ex04")
    public String ThymeleafExample04(Model model) {

        List<ItemDto> itemDtoList = new ArrayList<>();

        for(int i = 1; i <= 10; i++) {
            ItemDto itemDto = ItemDto.builder()
                    .itemDetail("상품 상세 설명"+i)
                    .itemNm("테스트 상품"+i)
                    .price(1000*i)
                    .regTime(LocalDateTime.now())
                    .build();

            itemDtoList.add(itemDto);
        }

        model.addAttribute("itemDtoList", itemDtoList);

        return "thymeleafEx/thymeleafEx04";
    }

    @GetMapping(value = "/ex05")
    public String ThymeleafExample05() {
        return "thymeleafEx/thymeleafEx05";
    }

    /*
    @GetMapping(value = "/ex06")
    public String ThymeleafExample06(@RequestParam("param1") String param1,
                                     @RequestParam("param2") String param2,
                                     Model model) {
     */
    @GetMapping(value = "/ex06")
    public String ThymeleafExample06( String param1, String param2,Model model) {
        model.addAttribute("param1", param1);
        model.addAttribute("param2", param2);
        return "thymeleafEx/thymeleafEx06";
    }

    @GetMapping(value = "/ex07")
    public String ThymeleafExample07() {
        return "thymeleafEx/thymeleafEx07";
    }

    @GetMapping(value = "/ex07_1")
    public String ThymeleafExample07_1() {
        return "thymeleafEx/thymeleafEx07_1";
    }

}
