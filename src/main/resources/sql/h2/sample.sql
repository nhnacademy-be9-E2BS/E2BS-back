-- 회원과 상품 기반 샘플 데이터

-- MemberRole
INSERT INTO member_role (member_role_name) VALUES
                                               ('ADMIN'),
                                               ('MEMBER');

-- MemberState
INSERT INTO member_state (member_state_name) VALUES
                                                 ('ACTIVE'),
                                                 ('DORMANT'),
                                                 ('WITHDRAW');

-- SocialAuth ENUM 기반
INSERT INTO social_auth (social_auth_name) VALUES
                                               ('PAYCO'),
                                               ('WEB');

-- MemberRank
INSERT INTO member_rank (
    member_rank_name,
    member_rank_tier_bonus_rate,
    member_rank_require_amount
) VALUES
      ('NORMAL', 0, 0),
      ('ROYAL', 5, 100000),
      ('GOLD', 10, 500000),
      ('PLATINUM', 20, 900000);

-- Customer (@OneToOne 관계 전 customer 먼저 삽입)
INSERT INTO customer (
    customer_email,
    customer_password,
    customer_name
) VALUES
      ('user1@example.com', 'password123', '홍길동1'),
      ('user2@example.com', 'password123', '홍길동2'),
      ('user3@example.com', 'password123', '홍길동3'),
      ('user4@example.com', 'password123', '홍길동4'),
      ('user5@example.com', 'password123', '홍길동5'),
      ('user6@example.com', 'password123', '홍길동6'),
      ('user7@example.com', 'password123', '홍길동7'),
      ('user8@example.com', 'password123', '홍길동8'),
      ('user9@example.com', 'password123', '홍길동9'),
      ('user10@example.com', 'password123', '홍길동10');

-- Member (MapsId 관계라 customer_id = member PK)
-- Member 10명 추가 (customer_id = member의 PK)
INSERT INTO member (
    customer_id,
    member_id,
    member_birth,
    member_phone,
    member_created_at,
    member_login_latest,
    member_rank_id,
    member_state_id,
    member_role_id,
    social_auth_id
) VALUES
      (1,  'member01', DATE '1990-01-01', '01011110001', DATE '2024-01-01', DATE '2024-05-01', 1, 1, 1, 1),
      (2,  'member02', DATE '1991-01-01', '01011110002', DATE '2024-01-02', DATE '2024-05-02', 1, 2, 1, 1),
      (3,  'member03', DATE '1992-01-01', '01011110003', DATE '2024-01-03', DATE '2024-05-03', 1, 2, 1, 1),
      (4,  'member04', DATE '1993-01-01', '01011110004', DATE '2024-01-04', DATE '2024-05-04', 1, 1, 1, 1),
      (5,  'member05', DATE '1994-01-01', '01011110005', DATE '2024-01-05', DATE '2024-05-05', 1, 2, 1, 1),
      (6,  'member06', DATE '1995-01-01', '01011110006', DATE '2024-01-06', DATE '2024-05-06', 1, 1, 1, 1),
      (7,  'member07', DATE '1996-01-01', '01011110007', DATE '2024-01-07', DATE '2024-05-07', 1, 1, 1, 1),
      (8,  'member08', DATE '1997-01-01', '01011110008', DATE '2024-01-08', DATE '2024-05-08', 1, 1, 1, 1),
      (9,  'member09', DATE '1998-01-01', '01011110009', DATE '2024-01-09', DATE '2024-05-09', 1, 3, 1, 1),
      (10, 'member10', DATE '1999-01-01', '01011110010', DATE '2024-01-10', DATE '2024-05-10', 1, 1, 1, 1);


-- Address
INSERT INTO address (
    address_name,
    address_code,
    address_info,
    address_extra,
    address_alias,
    address_default,
    address_created_at,
    customer_id
) VALUES
      (
          '서울특별시 강남구 테헤란로',
          '06130',
          '삼성동 123-45',
          '위워크 10층',
          '회사',
          TRUE,
          CURRENT_TIMESTAMP,
          1
      ),
      (
          '경기도 성남시 분당구 정자동',
          '13560',
          '정자역 5번 출구',
          'OO오피스텔 101호',
          '집',
          FALSE,
          CURRENT_TIMESTAMP,
          1
      );

-- Point History
INSERT INTO point_history (
    point_amount,
    point_created_at,
    point_reason,
    customer_id
) VALUES (
             5000,
             DATE '2025-05-04',
             '회원가입',
             1
         );


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
INSERT INTO category (category_id, category_name)
VALUES (1, 'Science'),
       (2, 'Computer Science'),
       (3, 'Math'),
       (4, 'Statistics');

-- Product
INSERT INTO product (product_packageable, product_published_at, product_stock, product_id, product_regular_price, product_sale_price, product_state_id, publisher_id, product_isbn, product_title, product_content, product_description)
VALUES (1, '2024-01-15', 100, 1, 20000, 15000, 1, 1, '978-89-12345-01-1', 'Spring 입문', 'Spring 프레임워크 소개', '초보자를 위한 Spring 가이드입니다.'),
       (0, '2023-11-30', 50, 2, 18000, 14000, 2, 2, '978-89-12345-02-2', 'JPA 기초', 'JPA 사용법 정리', 'Entity 설계부터 관계 매핑까지 JPA의 핵심을 담았습니다.'),
       (1, '2022-06-10', 20, 3, 25000, 20000, 3, 1, '978-89-12345-03-3', 'Docker 실전', '컨테이너 기술', 'Docker를 활용한 배포 환경 구성 실전서입니다.'),
       (0, '2025-03-01', 70, 4, 22000, 18000, 4, 2, '978-89-12345-04-4', 'CI/CD 이해하기', '지속적 통합과 배포', 'CI/CD 파이프라인의 구성과 구현 방법을 설명합니다.');

-- ProductImage
INSERT INTO product_image (product_id, product_image_path)
VALUES (1, 'asdfageaafdbaefw.jpg'),
       (2, 'akdknjkbaierfdsna.png'),
       (3, 'skbkgeajifaseflknv.jpg'),
       (4, 'afbdijfknvmaklf2.jpeg');

-- ProductContributor
INSERT INTO product_contributor (contributor_id, product_contributor_id, product_id)
VALUES (1, 1, 1),
       (3, 2, 1),
       (2, 3, 2);

-- ProductTag
INSERT INTO product_tag (product_id, tag_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (3, 3),
       (4, 3);

-- ProductCategory
INSERT INTO product_category (category_id, product_id)
VALUES (1, 1),
       (2, 4),
       (4, 2),
       (3, 3);

-- 주문 테스트를 위한 데이터

-- DeliveryFee
INSERT INTO delivery_fee (delivery_fee_amount, delivery_fee_date, delivery_fee_free_amount)
VALUES (5000, DATE '2025-01-01', 30000);

-- Wrapper
INSERT INTO wrapper (wrapper_image, wrapper_name, wrapper_price, wrapper_saleable)
VALUES ('', '포장 안함', 0, true),
       ('', '빨간 포장지', 700, true);

-- OrderState
INSERT INTO order_state (order_state_name)
VALUES ('CANCEL'), ('COMPLETE'),('DELIVERY'), ('RETURN'), ('WAIT');

-- CouponPolicy
INSERT INTO coupon_policy (coupon_policy_id ,coupon_policy_created_at, coupon_policy_discount_rate, coupon_policy_maximum_amount, coupon_policy_minimum, coupon_policy_name, coupon_policy_sale_price)
VALUES (1,DATE '2025-01-01', null, null, 10000, '10,000원 이상 구매 시 1,000원 할인', 1000),
       (2, DATE '2025-01-01', 10, 3000, 10000, '10,000원 이상 구매 시 10% 할인 (최대 3,000원)', null),
       (3, DATE '2025-01-01', null, null, 50000, '50,000원 이상 구매 시 10,000원 할인', 10000),
       (4, DATE '2025-01-01', null, null, 20000, '20,000원 이상 구매 시 3,000원 할인', 3000),
       (5, DATE '2025-01-01', 20, 10000, 20000, '20,000원 이상 구매 시 20% 할인 (최대 10,000원)', null);


-- Coupon
INSERT INTO coupon (coupon_id, coupon_name, coupon_policy_id, coupon_is_active)
VALUES (1,'1,000원 쿠폰', 1, true),
       (2,'10% 쿠폰',2, true);

-- MemberCoupon
INSERT INTO member_coupon (member_coupon_created_at, member_coupon_period, member_coupon_used, coupon_id, member_id)
VALUES (TIMESTAMP '2025-01-01 00:00:00.000000', TIMESTAMP '2026-01-01 00:00:00.000000', false, 1, 1),
       (TIMESTAMP '2025-01-01 00:00:00.000000', TIMESTAMP '2026-01-01 00:00:00.000000', false, 2, 1);

-- PaymentMethod
INSERT INTO payment_method (payment_method_id, payment_method_name)
VALUES (1, 'TOSS')