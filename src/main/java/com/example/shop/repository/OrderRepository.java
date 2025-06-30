package com.example.shop.repository;


import com.example.shop.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    /*
    select o.*
    from orders o
    join member m
    on o.member_id = m.member_id
    where m.email = "test@test.com";
    */

    //JPQL
    @Query("select o from Order o " +
            "where o.member.email = :email " +
            "order by o.orderDate desc")
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    /*
        select count(o.order_id)
        from orders o
        join member m
        on o.member_id = m.member_id
        where m.email = "test@test.com";
     */


    @Query("select count(o) from Order o " +
            "where o.member.email = :email")
    Long countOrder(@Param("email") String email);
}
