package com.example.shop.entity;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.repository.ItemRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.example.shop.entity.QItem.item;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ItemTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    void setUp() {
        queryFactory = new JPAQueryFactory(em);
    }

    @Test
    public void testFindByItemNm(){

        List<Item> items =
                itemRepository.findByItemNm("자바");
        items.forEach(item -> log.info(item.toString()));
        //두개는 같은 코드
        //
        //items.forEach(System.out::println);
        log.info("------------------QueryDSL-------------------");

        QItem qItem = item;

        List<Item> item2 = queryFactory
                .selectFrom(qItem)
                .from(qItem)
                .where(qItem.itemNm.eq("자바"))
                .fetch();
        item2.forEach(item -> log.info(item.toString()));
    }

    @Test
    public void testFindByItemNmAndPrice(){
        QItem qItem = item;

        queryFactory
                .selectFrom(qItem)
                .where(
                        qItem.itemNm.eq("자바"),
                        qItem.price.gt(10000)
                )
                .fetch();

        log.info(item.toString());
    }

    @Test
    public void testFindByItemNmOrItemDetail(){
        QItem qItem = QItem.item;

        List<Item> items = queryFactory
                .select(qItem)
                .from(qItem)
                .where(
                        qItem.itemNm.contains("부트")
                                .or(qItem.itemDetail.contains("자바"))

                )
                .fetch();

        items.forEach(item->log.info(item.toString()));
    }

    //Enum 조건검색
    @Test
    public void testFindBySellStatus(){
        QItem qItem = QItem.item;


        List<Item> items = queryFactory
                .selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SOLD_OUT))
                .fetch();

        items.forEach(item->log.info(item.toString()));
    }

    //동적 조건 검색(BooleanBuilder사용)
    @Test
    public void testDynamicSearch(){
        QItem qItem = QItem.item;
        BooleanBuilder builder = new BooleanBuilder();

        String searchNm = "자바";
        Integer minPrice = 9000;

        if(searchNm != null){
            builder.and(qItem.itemNm.contains(searchNm));
        }

        if(minPrice != null){
            builder.and(qItem.price.gt(minPrice));
        }

        List<Item> items = queryFactory
                .selectFrom(qItem)
                .where(builder)
                .fetch();
        items.forEach(item->log.info(item.toString()));
    }

    // 정렬 + 페이징 처리
    @Test
    public void testPaging(){
        QItem qItem = QItem.item;

        List<Item> items = queryFactory
                .selectFrom(qItem)
                .where(qItem.price.gt(1000))
                .orderBy(qItem.price.asc())
                .fetch();

        log.info(items.toString());
    }

    @Test
    public void testPagingAndSort(){
        QItem qItem = QItem.item;

        List<Item> items = queryFactory
                .selectFrom(qItem)
                .where(qItem.price.gt(1000))
                .orderBy(qItem.price.asc())
                .offset(1)
                .limit(3)
                .fetch();

        log.info(items.toString());
    }

    //그룹화, 집계함수(countm, max, avg등)
    @Test
    public void testAggreegateFuntion(){
        QItem qItem = QItem.item;

        List<Tuple> fetch = queryFactory
                .select(
                        qItem.itemSellStatus,
                        qItem.price.avg()
                )
                .from(qItem)
                .groupBy(qItem.itemSellStatus)
                .fetch();

        fetch.stream().forEach(item-> log.info(item.toString()));
       // log.info("평균 가격 : {}", fetch.get());

    }

    //ItemImg 조회
    @Test
    public void testItemImg(){
        QItemImg qItemImg = QItemImg.itemImg;

        List<ItemImg> result = queryFactory
                .selectFrom(qItemImg)
                .where(qItemImg.repimgYn.eq("Y"))
                .fetch();
        result.forEach(itemImg->log.info(itemImg.toString()));
    }

    @Test
    public void testJoin(){
        QItem qItem = QItem.item;
        QItemImg qItemImg = QItemImg.itemImg;

        List<ItemImg> result = queryFactory
                .select(qItemImg)
                .join(qItemImg.item, qItem)
                .where(qItem.itemNm.contains("자바"))
                .fetch();

        log.info(result.toString());
    }



}