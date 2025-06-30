package com.example.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter@Setter
@ToString
@Table(name="cart_item")
public class CartItem  extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name="cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    private int count;  //카트안 상품 갯수
}
