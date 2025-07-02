package com.example.shop.service;

import com.example.shop.dto.CartItemDto;
import com.example.shop.entity.CartItem;
import com.example.shop.repository.CartItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@WithMockUser(username = "dog@dog.com", roles = "ADMIN")
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    public void testAddCart() {

        //given
        CartItemDto cartItemDto =new CartItemDto();
        cartItemDto.setItemId(3L);
        cartItemDto.setCount(10);
        String email = "dog@dog.com";

        //when
        Long result = cartService.addCart(cartItemDto,email);

        //then
        CartItem cartItem = cartItemRepository.findById(result)
                .orElseThrow(()->new EntityNotFoundException());

        log.info("result:{}",result);

        assertEquals(cartItem.getItem().getId(), cartItemDto.getItemId());
    }

}
