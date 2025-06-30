package com.example.shop.entity;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.dto.MemberFormDto;
import com.example.shop.repository.ItemRepository;
import com.example.shop.repository.MemberRepository;
import com.example.shop.repository.OrderItemRepository;
import com.example.shop.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
class OrderTest {

    private static int i=1;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLodingTest(){
        Order order = this.crateOrder();

        Long OrderItemId = order.getOrderItems().get(0).getId();

        log.info("OrderItemId ==> {}", OrderItemId);

        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(OrderItemId)
                .orElseThrow(() -> new EntityNotFoundException("id값 없음"));

        log.info("orderItem  ==>  {}", orderItem);
        log.info("order class ==>  {}", orderItem.getOrder().getClass());
    }

    public Order crateOrder(){
        Order order = new Order();

        for(int i=0; i<3; i++){
            Item item = createItem();
            itemRepository.save(item);

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);

        return order;
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = crateOrder();
        order.getOrderItems().remove(0);
        em.flush();
    }

    public Item createItem(){
        Item item = new Item();

        item.setItemNm("테스트 상품"+i);
        item.setPrice(10000*i);
        item.setItemDetail("상세설명"+i);
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100*i);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        i++;
        return item;
    }

    @Test
    public void cacadeTest(){
        Order order = new Order();

        for(int i=0; i<3; i++){
            Item item = createItem();
            itemRepository.save(item);

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        log.info("--------------------------------");
        order.getOrderItems().forEach(orderItem -> {
            log.info(orderItem.toString());
        });

        orderRepository.save(order);

        em.flush();
        em.clear();

        Order savedOrder =  orderRepository.findById(order.getId())
                .orElseThrow(()-> new EntityNotFoundException("ID 없음"));
        assertEquals(3, savedOrder.getOrderItems().size());
    }
}