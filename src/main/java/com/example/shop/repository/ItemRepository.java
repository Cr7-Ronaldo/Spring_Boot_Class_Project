package com.example.shop.repository;

import com.example.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {

    List<Item> findByItemNm(String imteNm);

    List<Item> findByItemNmLike(String imteNm);

    List<Item> findByPriceLessThan(int price);

    //JQPL -> entity 객체 이용
    @Query("select i from Item i  where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    //navtivQuery -> db에 있는  table 이용
    @Query(value = "select * from item where item_detail " +
            "like %:itemDetail% order by price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
}










