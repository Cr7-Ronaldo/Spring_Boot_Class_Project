package com.example.shop.repository;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.dto.ItemSearchDto;
import com.example.shop.dto.MainItemDto;
import com.example.shop.dto.QMainItemDto;
import com.example.shop.entity.Item;
import com.example.shop.entity.QItem;
import com.example.shop.entity.QItemImg;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.shop.entity.QItem.item;

@Slf4j
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus itemSellStatus) {
        return itemSellStatus == null ?
                null :
                item.itemSellStatus.eq(itemSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType) {

        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        }else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        }else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        }else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusDays(6);
        }

        return item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if(StringUtils.equals("itemNm", searchBy)) {
            return item.itemNm.contains(searchQuery);
        }else if(StringUtils.equals("createBy", searchBy)) {
            return item.createdBy.contains(searchQuery);
        }
        return null;
    }


    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItem item = QItem.item;

        log.info("itemSearchDto.getSearchDateType() : {}", itemSearchDto.getSearchDateType());
        log.info("itemSearchDto.getItemSellStatus() : {}", itemSearchDto.getItemSellStatus());
        log.info("searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()) : {}",
                searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()));

        // where 조건
        BooleanExpression dateCondition = regDtsAfter(itemSearchDto.getSearchDateType());
        BooleanExpression sellStatusCondition = searchSellStatusEq(itemSearchDto.getItemSellStatus());
        BooleanExpression searchByCondition = searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery());

        // 실제 데이터 조회
        List<Item> content = queryFactory
                .selectFrom(item)
                .where(dateCondition, sellStatusCondition, searchByCondition)
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 건수 조회
        long total = queryFactory
                .select(item.count())
                .from(item)
                .where(dateCondition, sellStatusCondition, searchByCondition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }


    private  BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ?
                null : item.itemNm.contains(searchQuery);
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = queryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price
                        )

                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();


        return new PageImpl<>(content, pageable, total);
    }


}
