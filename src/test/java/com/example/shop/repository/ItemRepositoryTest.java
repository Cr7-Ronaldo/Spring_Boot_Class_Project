package com.example.shop.repository;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.dto.ItemSearchDto;
import com.example.shop.entity.Item;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import static  org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

@SpringBootTest
@Slf4j
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){
        Item item = new Item();

        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        Item savedItem = itemRepository.save(item);

        log.info("savedItem : {}", savedItem.toString());
    }


    public void createItemList(){

        for(int i=1; i<=10; i++) {

            Item item = new Item();

            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());

            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest(){
        this.createItemList();

        List<Item> itemList= itemRepository.findByItemNm("테스트 상품1");
        itemList.forEach(item-> log.info("item : {}", item.toString()));
    }

    @Test
    @DisplayName("상품명 LIKE 조회 테스트")
    public void findByItemNmLikeTest(){
        this.createItemList();

        List<Item> itemList= itemRepository.findByItemNmLike("%테스트 상품1%");
        itemList.forEach(item-> log.info("item : {}", item.toString()));
    }

    @Test
    @DisplayName("가격 조회 테스트")
    public void findByPriceLessThanTest(){
        this.createItemList();

        List<Item> itemList= itemRepository.findByPriceLessThan(10005);
        itemList.forEach(item-> log.info("item : {}", item.toString()));
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createItemList();

        List<Item> itemList= itemRepository.findByItemDetail("설명1");
        itemList.forEach(item-> log.info("item : {}", item.toString()));
    }

    @Test
    @DisplayName("@Query Native를 이용한 상품 조회 테스트")
    public void findByItemDetailByNativeTest(){
        this.createItemList();

        List<Item> itemList= itemRepository.findByItemDetailByNative("설명1");
        itemList.forEach(item-> log.info("item : {}", item.toString()));
    }

    @Autowired
    private EntityManager em;


    @Test
    public void getGetAdminItemPage(){

        //given: 테스트를 위한 초기 상태 설정
        ItemSearchDto searchDto = new ItemSearchDto();
        searchDto.setSearchDateType("id");
        searchDto.setItemSellStatus(ItemSellStatus.SELL);
//        searchDto.setSearchBy("itemNm");
//        searchDto.setSearchQuery("자바");

        PageRequest pageRequest = PageRequest.of(0, 5);

        //when: 테스트할 동작 실행
        Page<Item> result = itemRepository.getAdminItemPage(searchDto, pageRequest);


        //then: 실행 결과 검증
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent().size()).isEqualTo(4);
//        assertThat(result.getContent().get(0).getItemNm()).contains("자바");

        result.getContent().forEach(item-> log.info("item : {}", item.toString()));
    }
}