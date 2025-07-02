package com.example.shop.service;

import com.example.shop.dto.CartItemDto;
import com.example.shop.entity.Cart;
import com.example.shop.entity.CartItem;
import com.example.shop.entity.Item;
import com.example.shop.entity.Member;
import com.example.shop.repository.CartItemRepository;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.ItemRepository;
import com.example.shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public Long addCart(CartItemDto cartItemDto, String email) {

        // 상품 정보
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException());

        // 장바구니 주인
        Member member = memberRepository.findByEmail(email);

        // 카트 소유자
        Cart cart = cartRepository.findByMemberId(member.getId());

        // 소유자 카트 없다면?
        if(cart == null){
            cart = Cart.create(member);
            cartRepository.save(cart);
        }

        // 카트안에   동일 상품이 있는가?
        CartItem savedCartItem =
                cartItemRepository.findByCartIdAndItemId(cart.getId(), cartItemDto.getItemId());

        //동일 상품이있으면 갯수만 증가
        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        }else{ //동일 상품이 없으면 객체 생성해서 테이블 추가
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }
}