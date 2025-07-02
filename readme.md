# 1. Shop Project

# 2. Tools
### 2.1 spring framework - 6.1.x
### 2.2 Springboot - 3.5
### 2.3 Language - java 17
### 2.4 DB - Mysql8.0
### 2.5 ORM - JPA
### 2.6 Build Tool - Gradle
### 2.7 IDE(통합개발환경) - IntelliJ
<br><br>

# 3. 8-4 장바구니 조회하기

## 📷 장바구니 상품 이미지 예시
![장바구니 상품 이미지](cartItem_item.itemImg.JPG)

<pre><code>'''psql code
@Query("select new com.example.shop.dto.CartDetailDto(ci.id, i.itemNm," +
"i.price, ci.count, im.imgUrl) " +
"from CartItem ci, ItemImg  im " +
"join ci.item i " +
"where ci.cart.id = :cartId " +
"and im.item.id = ci.item.id " +
"and im.repimgYn == 'Y' " +
"order by ci.regTime desc"
)
List<CartDetailDto> findCartDetatilDtolist(Long cartId);
</code></pre>

<pre><code>```sql code
select ci.cart_item_id, i.item_nm, i.price, ci.count, im.img_url
from cart_item ci 
join item i
on ci.item_id = i.item_id
join item_img im
on i.item_id  = im.item_id
where im.repimg_Yn = 'Y'
order by ci.reg_time desc;
</code></pre>

<pre><code>```queryDsl code 
QCartItem ci = QCartItem.cartItem;
QItem i = QItem.item;
QItemImg im = QItemImg.itemImg;

List<CartDetailDto> results = queryFactory
.select(Projections.constructor(
CartDetailDto.class,
ci.id,
i.itemNm,
i.price,
ci.count,
im.imgUrl
))
.from(ci)
.join(ci.item, i)
.join(im).on(im.item.id.eq(ci.item.id).and(im.repimgYn.eq("Y")))
.where(ci.cart.id.eq(cartId))
.orderBy(ci.regTime.desc())
.fetch();
</code></pre>