package com.example.shop.controller;

import com.example.shop.dto.ItemSearchDto;
import com.example.shop.dto.MainItemDto;
import com.example.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;
@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;

    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto,
                       Optional<Integer> page, Model model) {

        Pageable pageable = PageRequest.of(
                page.isPresent() ? page.get() : 1, 6
        );

        log.info("MainController: pageable: {}", itemSearchDto);
        log.info("MainController: pageable: {}", pageable.getOffset());
        log.info("MainController: pageable: {}", pageable.getPageSize());

        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);

        log.info("MainController: items number: {}", items.getNumber());

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);


        return "main";
    }
}
