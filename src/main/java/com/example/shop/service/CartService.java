package com.example.shop.service;

import com.example.shop.dto.CartDetailDto;
import com.example.shop.dto.CartItemDto;
import com.example.shop.dto.CartOrderDto;
import com.example.shop.dto.OrderDto;
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
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

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
    } //end addCart

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());

        if(cart == null){
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtolist(cart.getId());

        return cartDetailDtoList;

    }//end getCartList


    public boolean validateCartItem(Long cartItemId, String email){
        Member member = memberRepository.findByEmail(email);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException());

        Member savedMember = cartItem.getCart().getMember();

        if(!StringUtils.equals(savedMember.getEmail(), member.getEmail())){
            return false;
        }
        return true;
    }

    public void updateCartItem(Long cartItemId, int count) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException());

        //스냅샷(cartItem)하고 비교해서 값이 변경감지(더티체킹)되기 때문에
        //update구문을 실행
        cartItem.setCount(count);
    }

    public void deleteCartItem(Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException());

        cartItemRepository.delete(cartItem);

    }


    public Long OrderCartItem(List<CartOrderDto> cartOrderDtoList, String email){

        List<OrderDto> orderDtoList = new ArrayList<>();

        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            log.info("cartOrderDto: {}", cartOrderDto);

            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(() -> new EntityNotFoundException());

            OrderDto orderDto = new OrderDto();

            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }
        //[{itemId : 1,count 2개}, {itemId : 2,count 5개}, {itemId : 3,count 4개}]
        Long orderId = orderService.orders(orderDtoList, email);

        //주문이 완료됐으므로 장바구니 비우기
        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(() -> new EntityNotFoundException());
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }
}