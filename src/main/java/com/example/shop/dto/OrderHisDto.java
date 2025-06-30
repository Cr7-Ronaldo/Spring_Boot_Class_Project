package com.example.shop.dto;

import com.example.shop.constant.OrderStatus;
import com.example.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter@Setter
@ToString
public class OrderHisDto {

    private Long orderId; //주문자 아이디
    private String orderDate; //주문 날짜
    private OrderStatus orderStatus; //주문 상태

    //주문 리스트
    private List<OrderItemDto> orderItemList = new ArrayList<>();

    public OrderHisDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        );
        this.orderStatus = order.getOrderStatus();
    }

    public void addOrderItemDto(OrderItemDto orderItemDto) {
        this.orderItemList.add(orderItemDto);
    }
}
