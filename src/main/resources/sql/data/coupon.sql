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

insert into member_coupon (member_coupon_used, coupon_id, customer_id, member_coupon_created_at, member_coupon_period, member_coupon_code)
values (0, 1, 1, '2025-03-12', '2025-07-01', 'qwer1234'),
       (0, 3, 1, '2025-01-01', '2025-05-01', 'awsd5678');
