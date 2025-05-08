-- 이 파일의 구문 실행 전에 account.sql, product.sql 실행하기

-- Cart
-- 비회원/회원용 장바구니
INSERT INTO cart (cart_id, customer_id)
VALUES (1, 1);

-- CartItems
INSERT INTO cart_items (cart_items_id, cart_id, product_id, cart_items_quantity)
VALUES
    (1, 1, 1, 1),
    (2, 1, 2, 2);