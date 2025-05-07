-- 이 파일의 구문 실행 전에 account.sql, product.sql 실행하기

-- Cart
insert into cart (customer_id, product_id, cart_quantity)
values (1, 1, 5),
       (1, 2, 2);