insert into position (position_id, position_name)
values (1, 'writer'),
       (2, 'translator');

insert into contributor (contributor_id, position_id, contributor_name)
values (1, 1, 'Kim'),
        (2, 1, 'Lee'),
        (3, 2, 'Park');

insert into product_state (product_state_id, product_state_name)
values (1, 'SALE'),
       (2, 'OUT'),
       (3, 'DELETE'),
       (4, 'END');

insert into publisher (publisher_id, publisher_name)
values (1, 'A pub'),
       (2, 'B pub'),
       (3, 'C pub');

insert into tag (tag_name)
values ('A tag'),
       ('B tag'),
       ('C tag');

insert into category (category_id2, category_name)
values (null, 'Science'),
       (1, 'Computer Science'),
       (null, 'Math'),
       (3, 'Statistics');

insert into product (product_packageable, product_published_at, product_stock, product_id, product_regular_price, product_sale_price, product_state_id, publisher_id, product_isbn, product_title, product_content, product_description)
values (1, '2024-01-15', 100, 1, 20000, 15000, 1, 1, '978-89-12345-01-1', 'Spring 입문', 'Spring 프레임워크 소개', '초보자를 위한 Spring 가이드입니다.'),
       (0, '2023-11-30', 50, 2, 18000, 14000, 2, 2, '978-89-12345-02-2', 'JPA 기초', 'JPA 사용법 정리', 'Entity 설계부터 관계 매핑까지 JPA의 핵심을 담았습니다.'),
       (1, '2022-06-10', 20, 3, 25000, 20000, 3, 1, '978-89-12345-03-3', 'Docker 실전', '컨테이너 기술', 'Docker를 활용한 배포 환경 구성 실전서입니다.'),
       (0, '2025-03-01', 70, 4, 22000, 18000, 4, 2, '978-89-12345-04-4', 'CI/CD 이해하기', '지속적 통합과 배포', 'CI/CD 파이프라인의 구성과 구현 방법을 설명합니다.');

insert into product_image (product_id, product_image_path)
values (1, 'asdfageaafdbaefw.jpg'),
       (2, 'akdknjkbaierfdsna.png'),
       (3, 'skbkgeajifaseflknv.jpg'),
       (4, 'afbdijfknvmaklf2.jpeg');

insert into product_contributor (contributor_id, product_contributor_id, product_id)
values (1, 1, 1),
       (3, 2, 1),
       (2, 3, 2);

insert into product_tag (product_id, tag_id)
values (1, 1),
       (1, 2),
       (2, 1),
       (3, 3),
       (4, 3);

insert into product_category (category_id, product_id)
values (1, 1),
       (2, 4),
       (4, 2),
       (3, 3);

insert into coupon_policy (coupon_policy_discount_rate, coupon_policy_maximum_amount, coupon_policy_minimum, coupon_policy_sale_price, coupon_policy_created_at, coupon_policy_name)
values (20, 10000, 20000, null, '2025-05-06', '20% 할인쿠폰'),
       (null, null, 20000, 3000, '2025-05-06', '3000원 할인쿠폰'),
       (null, null, 50000, 10000, '2025-05-06', '10000원 할인쿠폰');

insert into coupon (coupon_policy_id, coupon_name)
values (1, '할인~!'),
       (2, '생일쿠폰'),
       (3, '새해 할인쿠폰');

insert into category_coupon (category_id, coupon_id)
values (1, 1),
       (3, 3);

insert into product_coupon (coupon_id, product_id)
values (2, 1);

insert into point_policy (point_policy_figure, point_policy_created_at, point_policy_name)
values (5000, '2025-05-06', '회원가입'),
       (500, '2025-05-06', '포토리뷰'),
       (200, '2025-05-06', '일반리뷰');