------------------------ 회원 관련 데이터 --------------------------------
-- MemberRole
INSERT INTO member_role (member_role_name)
VALUES ('ADMIN'),
       ('MEMBER');

-- MemberState
INSERT INTO member_state (member_state_name)
VALUES ('ACTIVE'),
       ('DORMANT'),
       ('WITHDRAW');

-- SocialAuth ENUM 기반
INSERT INTO social_auth (social_auth_name)
VALUES ('WEB'),
       ('PAYCO');

-- MemberRank
INSERT INTO member_rank (member_rank_name,
                         member_rank_tier_bonus_rate,
                         member_rank_require_amount)
VALUES ('NORMAL', 1, 0),
       ('ROYAL', 2, 100000),
       ('GOLD', 3, 200000),
       ('PLATINUM', 4, 300000);


-- 테스트를 위한 임의 유저, 관리자 생성
-- Customer password 12345
INSERT INTO customer (customer_email,
                      customer_password,
                      customer_name)
VALUES ('user@example.com', '$2a$10$uVFW.aTO5YgKVNm0g6YYi.cJpVD/tLxqQ2PNumx.PikXJOlWCR1c6', '유저'),
       ('admin@example.com', '$2a$10$uVFW.aTO5YgKVNm0g6YYi.cJpVD/tLxqQ2PNumx.PikXJOlWCR1c6', '관리자'),
       ('user1@example.com', '$2a$10$uVFW.aTO5YgKVNm0g6YYi.cJpVD/tLxqQ2PNumx.PikXJOlWCR1c6', '유저1'),
       ('user2@example.com', '$2a$10$uVFW.aTO5YgKVNm0g6YYi.cJpVD/tLxqQ2PNumx.PikXJOlWCR1c6', '유저2');;

-- Member
INSERT INTO member (customer_id,
                    member_id,
                    member_birth,
                    member_phone,
                    member_created_at,
                    member_login_latest,
                    member_rank_id,
                    member_state_id,
                    member_role_id,
                    social_auth_id)
VALUES (1,
        'user',
        DATE '1990-01-01',
        '01012345678',
        DATE '2024-01-01',
        DATE '2025-06-01',
        1, -- NORMAL
        1, -- ACTIVE
        2, -- Member
        1 -- PAYCO
       ),
       (2,
        'admin',
        DATE '1990-01-01',
        '01012345678',
        DATE '2024-01-01',
        DATE '2024-05-01',
        1, -- NORMAL
        1, -- ACTIVE
        1, -- Admin
        1 -- PAYCO
       ),
       (3,
        'user1',
        DATE '1990-05-01',
        '01012345678',
        DATE '2024-01-01',
        DATE '2024-05-01',
        1, -- NORMAL
        1, -- ACTIVE
        2, -- Member
        1 -- PAYCO
       ),
       (4,
        'user2',
        DATE '1990-05-01',
        '01012345678',
        DATE '2024-01-01',
        DATE '2024-05-01',
        1, -- NORMAL
        1, -- ACTIVE
        2, -- Member
        1 -- PAYCO
       );

-- Address
INSERT INTO address (address_detail,
                     address_code,
                     address_info,
                     address_extra,
                     address_alias,
                     address_default,
                     address_created_at,
                     customer_id,
                     address_receiver,
                     address_receiver_phone)
VALUES ('위워크 10층',
        '06130',
        '서울특별시 강남구 테헤란로',
        '삼성동 123-45',
        '회사',
        TRUE,
        CURRENT_TIMESTAMP,
        1,
        '김도윤',
        '010-9140-6307'),
       ('OO오피스텔 101호',
        '13560',
        '경기도 성남시 분당구 정자동',
        '정자역 5번 출구',
        '집',
        FALSE,
        CURRENT_TIMESTAMP,
        1,
        '최종성',
        '010-1234-5678');

-- Point History
INSERT INTO point_history (point_amount,
                           point_created_at,
                           point_reason,
                           customer_id)
VALUES (5000,
        DATE '2025-05-04',
        '회원가입',
        1);

------------------------ 도서 관련 데이터 --------------------------------
-- Position
INSERT INTO position (position_id, position_name)
VALUES (1, 'writer'),
       (2, 'translator');

-- Contributor
INSERT INTO contributor (contributor_id, position_id, contributor_name)
VALUES (1, 1, 'Kim'),
       (2, 1, 'Lee'),
       (3, 2, 'Park');

-- ProductState
INSERT INTO product_state (product_state_id, product_state_name)
VALUES (1, 'SALE'),
       (2, 'OUT'),
       (3, 'DELETE'),
       (4, 'END');

-- Publisher
INSERT INTO publisher (publisher_id, publisher_name)
VALUES (1, 'A pub'),
       (2, 'B pub'),
       (3, 'C pub');

-- Tag
INSERT INTO tag (tag_name)
VALUES ('A tag'),
       ('B tag'),
       ('C tag');

-- Category
INSERT INTO category (category_id, category_name, category_id2)
VALUES (1, '국내도서', null),
       (2, '소설', 1),
       (3, '대학교재', 1),
       (4, '과학', 3),
       (5, '수학', 3),
       (6, '국외도서', null),
       (7, '건강', 6),
       (8, '전자책', null),
       (9, '만화', 8),
       (10, '물리학', 4);

-- Product
INSERT INTO product (product_packageable, product_published_at, product_stock, product_id, product_regular_price,
                     product_sale_price, product_state_id, publisher_id, product_isbn, product_title, product_content,
                     product_description)
VALUES (1, '2024-01-15', 100, 1, 20000, 15000, 1, 1, '978-89-12345-01-1',
        'Spring 입문ㅇㅇ긴 문자인 경우 출력 테스트 ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ',
        'Spring 프레임워크 소개ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ',
        '초보자를 위한 Spring 가이드입니다.'),
       (0, '2023-11-30', 50, 2, 18000, 14000, 2, 2, '978-89-12345-02-2', 'JPA 기초', 'JPA 사용법 정리',
        'Entity 설계부터 관계 매핑까지 JPA의 핵심을 담았습니다.'),
       (1, '2022-06-10', 20, 3, 25000, 20000, 3, 1, '978-89-12345-03-3', 'Docker 실전', '컨테이너 기술',
        'Docker를 활용한 배포 환경 구성 실전서입니다.'),
       (0, '2025-03-01', 70, 4, 22000, 18000, 4, 2, '978-89-12345-04-4', 'CI/CD 이해하기', '지속적 통합과 배포',
        'CI/CD 파이프라인의 구성과 구현 방법을 설명합니다.'),
       (1, '2023-05-20', 80, 5, 23000, 18000, 1, 3, '978-89-12345-05-5', 'React 완벽 가이드', 'React 기초부터 고급까지',
        'React를 사용한 프론트엔드 개발에 대한 완벽 가이드입니다.'),
       (0, '2023-09-10', 150, 6, 27000, 21000, 2, 1, '978-89-12345-06-6', 'Vue.js 실전', 'Vue.js로 웹 애플리케이션 만들기',
        'Vue.js를 사용한 웹 애플리케이션 개발 실전서입니다.'),
       (1, '2025-01-15', 30, 7, 24000, 20000, 3, 2, '978-89-12345-07-7', 'Machine Learning', '머신러닝 기초부터 실습까지',
        '머신러닝을 활용한 데이터 분석 및 모델링 실습서를 제공합니다.'),
       (0, '2024-08-25', 60, 8, 25000, 22000, 4, 3, '978-89-12345-08-8', 'Kubernetes 실전', 'Kubernetes 클러스터 구축',
        'Kubernetes를 사용한 클러스터 구성 및 배포 전략에 대한 실전 가이드입니다.');

-- ProductImage
INSERT INTO product_image (product_id, product_image_path)
VALUES (1, 'https://image.aladin.co.kr/product/31688/89/coversum/k482833588_1.jpg'),
       (2, 'https://image.aladin.co.kr/product/31688/89/coversum/k482833587_1.jpg'),
       (3, 'https://image.aladin.co.kr/product/31688/89/coversum/k482833585_1.jpg'),
       (4, 'https://image.aladin.co.kr/product/31688/89/coversum/k482833584_1.jpg'),
       (5, 'https://image.aladin.co.kr/product/31688/89/coversum/k482833583_1.jpg'),
       (6, 'https://image.aladin.co.kr/product/31688/89/coversum/k482833582_1.jpg'),
       (7, 'https://image.aladin.co.kr/product/31688/89/coversum/k482833581_1.jpg'),
       (8, 'https://image.aladin.co.kr/product/31688/89/coversum/k482833580_1.jpg');

-- ProductContributor
INSERT INTO product_contributor (contributor_id, product_contributor_id, product_id)
VALUES (1, 1, 1),
       (3, 2, 1),
       (2, 3, 2),
       (1, 4, 5),
       (2, 5, 5),
       (3, 6, 6),
       (1, 7, 6),
       (2, 8, 7),
       (3, 9, 7),
       (1, 10, 8),
       (2, 11, 8);

-- ProductTag
INSERT INTO product_tag (product_id, tag_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (3, 3),
       (4, 3),
       (5, 2),
       (5, 3),
       (6, 1),
       (6, 2),
       (7, 3),
       (7, 1),
       (8, 2),
       (8, 1);

-- ProductCategory
INSERT INTO product_category (category_id, product_id)
VALUES (1, 1),
       (2, 4),
       (1, 4),
       (4, 2),
       (3, 3),
       (2, 5),
       (1, 5),
       (3, 6),
       (4, 6),
       (5, 7),
       (6, 7),
       (4, 8),
       (7, 8);

-- Cart
INSERT INTO cart (cart_id, customer_id)
VALUES (1, 1);

-- CartItems
INSERT INTO cart_items (cart_items_id, cart_id, product_id, cart_items_quantity)
VALUES (1, 1, 1, 5),
       (2, 1, 2, 2),
       (3, 1, 3, 6),
       (4, 1, 4, 1);

-- Like
INSERT INTO `like` (like_id, product_id, customer_id, like_created_at)
VALUES (1, 1, 1, '2024-01-10T10:00:00'),
       (2, 1, 2, '2024-01-12T11:30:00'),
       (3, 1, 3, '2024-01-12T11:30:00'),
       (4, 1, 4, '2024-01-12T11:30:00'),
       (5, 2, 1, '2024-02-01T09:45:00'),
       (6, 2, 2, '2024-02-05T15:20:00'),
       (7, 2, 3, '2024-02-05T15:20:00'),
       (8, 2, 4, '2024-02-05T15:20:00'),
       (9, 3, 1, '2024-03-10T16:10:00'),
       (10, 3, 2, '2024-03-12T17:00:00'),
       (11, 4, 1, '2024-04-10T12:25:00'),
       (12, 4, 2, '2024-04-15T13:30:00'),
       (13, 5, 1, '2024-05-01T14:40:00'),
       (14, 5, 2, '2024-05-03T10:50:00'),
       (15, 6, 1, '2024-06-20T09:30:00'),
       (16, 6, 2, '2024-06-21T14:00:00'),
       (17, 7, 1, '2024-07-25T16:35:00'),
       (18, 7, 2, '2024-07-27T17:10:00'),
       (19, 8, 1, '2024-08-01T10:00:00'),
       (20, 8, 2, '2024-08-02T11:45:00');

-- Review
INSERT INTO review (product_id, customer_id, review_content, review_grade, review_created_at, review_image)
VALUES (1, 1, '노트북 최고네요!', 5, TIMESTAMP '2025-05-07 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (1, 2, '노트북 별로네요', 2, TIMESTAMP '2025-05-05 16:30:00', 'review1.jpg'),
       (2, 2, '스마트폰 별로', 1, TIMESTAMP '2025-05-07 18:30:00', 'review2.jpg'),
       (7, 1, 'Machine Learning 좋네', 5, TIMESTAMP '2025-05-08 21:30:00', 'review3.jpg'),
       (7, 2, 'Machine Learning 별로', 1, TIMESTAMP '2025-05-09 22:30:00', 'review3.jpg'),
       (8, 1, 'Kubernetes 좋네', 4, TIMESTAMP '2025-05-10 18:30:00', 'review4.jpg'),
       (8, 2, 'Kubernetes 별로', 1, TIMESTAMP '2025-05-11 20:30:00', 'review4.jpg');

----------------------- 주문 관련 데이터------------------------
-- DeliveryFee
INSERT INTO delivery_fee (delivery_fee_amount, delivery_fee_date, delivery_fee_free_amount)
VALUES (5000, DATE '2025-01-01', 30000);

-- Wrapper
INSERT INTO wrapper (wrapper_image, wrapper_name, wrapper_price, wrapper_saleable)
VALUES ('', '포장 안함', 0, true),
       ('', '빨간 포장지', 700, true),
       ('', '파랑 포장지', 1000, true),
       ('', '초록 포장지', 500, true);

-- OrderState
INSERT INTO order_state (order_state_name)
VALUES ('CANCEL'),
       ('COMPLETE'),
       ('DELIVERY'),
       ('RETURN'),
       ('WAIT');

-- CouponPolicy
INSERT INTO coupon_policy (coupon_policy_id, coupon_policy_created_at, coupon_policy_discount_rate,
                           coupon_policy_maximum_amount, coupon_policy_minimum, coupon_policy_name,
                           coupon_policy_sale_price)
VALUES (1, DATE '2025-01-01', null, null, 10000, '10,000원 이상 구매 시 1,000원 할인', 1000),
       (2, DATE '2025-01-01', 10, 3000, 10000, '10,000원 이상 구매 시 10% 할인 (최대 3,000원)', null),
       (3, DATE '2025-01-01', null, null, 50000, '50,000원 이상 구매 시 10,000원 할인', 10000),
       (4, DATE '2025-01-01', null, null, 20000, '20,000원 이상 구매 시 3,000원 할인', 3000),
       (5, DATE '2025-01-01', 20, 10000, 20000, '20,000원 이상 구매 시 20% 할인 (최대 10,000원)', null);


-- Coupon
INSERT INTO coupon (coupon_id, coupon_name, coupon_policy_id, coupon_is_active)
VALUES (1, '국내도서 쿠폰', 1, true),
       (2, 'spring 입문 쿠폰', 2, true),
       (3, '소설 쿠폰', 3, true),
       (4, '봄맞이 할인 쿠폰', 4, true),
       (5, '과학의 달 쿠폰', 5, true),
       (6, '5월 생일 쿠폰', 2, true),
       (7, '웰컴 쿠폰', 5, true);


-- MemberCoupon
INSERT INTO member_coupon (member_coupon_created_at, member_coupon_period, member_coupon_used, coupon_id, member_id)
VALUES (TIMESTAMP '2025-01-01 00:00:00.000000', TIMESTAMP '2026-01-01 00:00:00.000000', true, 1, 1),
       (TIMESTAMP '2025-01-01 00:00:00.000000', TIMESTAMP '2026-01-01 00:00:00.000000', false, 2, 1),
       (TIMESTAMP '2025-01-01 00:00:00.000000', TIMESTAMP '2026-01-01 00:00:00.000000', false, 3, 1),
       (TIMESTAMP '2025-01-01 00:00:00.000000', TIMESTAMP '2026-01-01 00:00:00.000000', true, 4, 1),
       (TIMESTAMP '2025-01-01 00:00:00.000000', TIMESTAMP '2026-01-01 00:00:00.000000', false, 5, 1),
       (TIMESTAMP '2025-01-01 00:00:00.000000', TIMESTAMP '2026-01-01 00:00:00.000000', false, 6, 1),
       (TIMESTAMP '2025-01-01 00:00:00.000000', TIMESTAMP '2026-01-01 00:00:00.000000', false, 7, 1),
       (TIMESTAMP '2025-01-01 00:00:00.000000', TIMESTAMP '2026-01-01 00:00:00.000000', false, 1, 1),
       (TIMESTAMP '2025-01-01 00:00:00.000000', TIMESTAMP '2025-04-01 00:00:00.000000', false, 1, 1);

-- PaymentMethod
INSERT INTO payment_method (payment_method_id, payment_method_name)
VALUES (1, 'TOSS'),
       (2, 'OTHER');


-- Order
INSERT INTO `order` (order_code, order_receiver_name, order_receiver_phone, order_receiver_tel, order_address_code,
                     order_address_info, order_address_detail, order_address_extra, order_point_amount,
                     order_payment_amount, order_memo, order_payment_status, order_receive_date, order_shipment_date,
                     order_created_at, member_coupon_id, delivery_fee_id, customer_id, order_state_id,
                     order_reward_amount, order_pure_amount)
VALUES ('TEST-ORDER-CODE', 'name', '01012345678', null, '12345', 'info', null, 'extra',
        1000, 5000, null, true, DATE '2025-06-01', null, TIMESTAMP '2025-05-25 00:00:00.000000', null, 1, 1, 5, 160,
        6000),
       ('TEST-DELIVERY-COMPLETE', 'name', '01012345678', null, '12345', 'info', null, 'extra',
        1000, 5000, null, true, DATE '2025-06-01', TIMESTAMP '2025-05-30 00:00:00.000000',
        TIMESTAMP '2025-05-29 00:00:00.000000', null, 1, 1, 2, 160, 6000);

-- OrderDetail
INSERT INTO order_detail (product_id, order_code, review_id, wrapper_id, order_quantity, order_detail_per_price)
VALUES (1, 'TEST-ORDER-CODE', 1, null, 1, 1000),
       (1, 'TEST-DELIVERY-COMPLETE', null, null, 2, 1000);

-- 카테고리 쿠폰
INSERT INTO category_coupon (coupon_id, category_id)
VALUES (1, 1),
       (3, 2);

-- 상품 쿠폰
INSERT INTO product_coupon (coupon_id, product_id)
VALUES (2, 1);

INSERT INTO point_policy (point_policy_id, point_policy_type, point_policy_name, point_policy_figure,
                          point_policy_created_at, point_policy_is_active)
VALUES (1, 'REGISTER', '기본 회원가입 정책', 5000, now(), true),
       (2, 'REVIEW', '기본 일반 리뷰 정책', 500, now(), true),
       (3, 'REVIEW_IMG', '기본 이미지 리뷰 정책', 800, now(), true),
       (4, 'BOOK', '기본 적립률(%) 정책', 5, now(), true);
