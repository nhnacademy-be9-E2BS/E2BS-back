-- MemberRole ENUM 기반
INSERT INTO member_role (member_role_name) VALUES ('ADMIN'), ('MEMBER');

-- MemberState ENUM 기반
INSERT INTO member_state (member_state_name) VALUES ('ACTIVE'), ('DORMANT'), ('WITHDRAW');

-- SocialAuth ENUM 기반
INSERT INTO social_auth (social_auth_name) VALUES ('PAYCO'), ('WEB');

-- MemberRank (ENUM + 숫자 필드)
INSERT INTO member_rank (member_rank_name, member_rank_tier_bonus_rate, member_rank_require_amount)
VALUES
    ('NORMAL', 0, 0),
    ('ROYAL', 5, 100000),
    ('GOLD', 10, 500000),
    ('PLATINUM', 20, 900000);

-- Customer 선삽입 (Member와 @OneToOne, @MapsId 관계)
INSERT INTO customer (customer_email, customer_password, customer_name)
VALUES ('testuser@example.com', 'password123', '홍길동');

-- Member 삽입
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
) VALUES (
             1,
             'member01',
             '1990-01-01',
             '01012345678',
             '2024-01-01',
             '2024-05-01',
             1,  -- BRONZE
             1,  -- ACTIVE
             1,  -- USER
             1   -- GOOGLE
         );

INSERT INTO address (
    address_name,
    address_code,
    address_info,
    address_extra,
    address_alias,
    address_default,
    address_created_at,
    member_customer_id
) VALUES
      (
          '서울특별시 강남구 테헤란로',
          '06130',
          '삼성동 123-45',
          '위워크 10층',
          '회사',
          true,
          NOW(),
          1
      ),
      (
          '경기도 성남시 분당구 정자동',
          '13560',
          '정자역 5번 출구',
          'OO오피스텔 101호',
          '집',
          false,
          NOW(),
          1
      );