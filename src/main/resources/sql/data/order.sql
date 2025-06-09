-- 이 파일의 구문 실행 전에 account.sql, product.sql, coupon.sql 실행하기

-- PaymentMethod
INSERT INTO payment_method (payment_method_name)
VALUES ('TOSS');

-- OrderState
INSERT INTO order_state (order_state_name)
VALUES ('WAIT'),
       ('DELIVERY'),
       ('COMPLETE'),
       ('RETURN'),
       ('CANCEL');

-- DeliveryFee
INSERT INTO delivery_fee (delivery_fee_amount, delivery_fee_free_amount, delivery_fee_date)
VALUES (3000, 50000, NOW());

-- Order
INSERT INTO `order` (order_code,
                     order_receiver_name,
                     order_receiver_phone,
                     order_receiver_tel,
                     order_address_code,
                     order_address_info,
                     order_address_detail,
                     order_address_extra,
                     order_point_amount,
                     order_payment_amount,
                     order_memo,
                     order_payment_status,
                     order_reward_amount,
                     order_pure_amount,
                     order_receive_date,
                     order_shipment_date,
                     order_created_at,
                     member_coupon_id,
                     delivery_fee_id,
                     customer_id,
                     order_state_id)
VALUES ('ORD20240506',
        '수신자',
        '01012345678',
        '021234567',
        '06000',
        '서울시 강남구 테헤란로',
        '101호',
        '3층',
        1000,
        15000,
        '문 앞에 놓아주세요',
        false,
        160,
        16000,
        NOW(),
        NOW(),
        NOW(),
        1,
        1,
        1,
        1);

-- OrderDetail
INSERT INTO order_detail (order_code, order_quantity, order_detail_per_price, product_id, wrapper_id,
                          review_id)
VALUES ('ORD20240506', 1, 1200000, 1, null, NULL),
       ('ORD20240506', 2, 700000, 2, null, NULL);

-- Payment
INSERT INTO payment (payment_key, total_payment_amount, payment_requested_at, payment_approved_at, payment_method_id,
                     order_code)
VALUES ('PAY1234567890', 10000, NOW(), NOW(), 1, 'ORD20240506');

-- OrderReturn
INSERT INTO order_return (order_return_reason, return_category, order_code, order_return_created_at,
                          order_return_amount)
VALUES ('상품이 파손되어 도착했습니다.', 'CHANGE_MIND', 'ORD20240506', NOW(), 5000),
       ('단순 변심으로 반품 요청합니다.', 'BREAK', 'ORD20240506', NOW(), 10000);

-- Wrapper (nullable)
INSERT INTO wrapper (wrapper_price, wrapper_name, wrapper_image, wrapper_saleable)
VALUES (5000, 'Gift Wrap', 'gift_wrap.jpg', TRUE),
       (3000, 'Standard Box', 'standard_box.jpg', TRUE);

-- Review (nullable) 적용 전에 proudct.sql 실행해서 데이터 넣어 놓기
INSERT INTO review (product_id, customer_id, review_content, review_grade, review_created_at, review_image)
VALUES (1, 1, 'Excellent laptop, highly recommend!', 5, '2025-05-06 16:30:00', 'review1.jpg'),
       (2, 1, 'Good smartphone, value for money.', 4, '2025-05-06 17:30:00', 'review2.jpg');
