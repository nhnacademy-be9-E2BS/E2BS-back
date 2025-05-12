CREATE TABLE address
(
    address_id         BIGINT    AUTO_INCREMENT   NOT NULL,
    address_name       VARCHAR(255) NOT NULL,
    address_code       VARCHAR(5)   NOT NULL,
    address_info       VARCHAR(255) NULL,
    address_extra      VARCHAR(255) NOT NULL,
    address_alias      VARCHAR(20)  NULL,
    address_default    BIT(1)       NOT NULL,
    address_created_at datetime     NOT NULL,
    customer_id        BIGINT       NOT NULL,
    CONSTRAINT pk_address PRIMARY KEY (address_id)
);

CREATE TABLE cart
(
    cart_id     BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    CONSTRAINT pk_cart PRIMARY KEY (cart_id)
);

CREATE TABLE cart_items
(
    cart_items_id       BIGINT NOT NULL,
    cart_id             BIGINT NOT NULL,
    product_id          BIGINT NOT NULL,
    cart_items_quantity INT    NOT NULL,
    CONSTRAINT pk_cartitems PRIMARY KEY (cart_items_id)
);

CREATE TABLE category
(
    category_id   BIGINT   AUTO_INCREMENT   NOT NULL,
    category_id2  BIGINT      NULL,
    category_name VARCHAR(30) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (category_id)
);

CREATE TABLE category_coupon
(
    coupon_id   BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    CONSTRAINT pk_categorycoupon PRIMARY KEY (coupon_id)
);

CREATE TABLE contributor
(
    contributor_id   BIGINT   AUTO_INCREMENT   NOT NULL,
    contributor_name VARCHAR(20) NOT NULL,
    position_id      BIGINT      NOT NULL,
    CONSTRAINT pk_contributor PRIMARY KEY (contributor_id)
);

CREATE TABLE coupon
(
    coupon_id        BIGINT  AUTO_INCREMENT    NOT NULL,
    coupon_policy_id BIGINT      NOT NULL,
    coupon_name      VARCHAR(30) NOT NULL,
    CONSTRAINT pk_coupon PRIMARY KEY (coupon_id)
);

CREATE TABLE coupon_policy
(
    coupon_policy_id             BIGINT   AUTO_INCREMENT     NOT NULL,
    coupon_policy_minimum        BIGINT DEFAULT 0 NOT NULL,
    coupon_policy_maximum_amount BIGINT           NULL,
    coupon_policy_sale_price     BIGINT           NULL,
    coupon_policy_discount_rate  INT              NULL,
    coupon_policy_created_at     datetime         NOT NULL,
    coupon_policy_name           VARCHAR(50)      NOT NULL,
    CONSTRAINT pk_couponpolicy PRIMARY KEY (coupon_policy_id)
);

CREATE TABLE customer
(
    customer_id       BIGINT    AUTO_INCREMENT   NOT NULL,
    customer_email    VARCHAR(100) NOT NULL,
    customer_password VARCHAR(255)  NOT NULL,
    customer_name     VARCHAR(255)  NOT NULL,
    CONSTRAINT pk_customer PRIMARY KEY (customer_id)
);

CREATE TABLE delivery_fee
(
    delivery_fee_id          BIGINT AUTO_INCREMENT NOT NULL,
    delivery_fee_amount      BIGINT NOT NULL,
    delivery_fee_free_amount BIGINT NOT NULL,
    delivery_fee_date        date   NOT NULL,
    CONSTRAINT pk_deliveryfee PRIMARY KEY (delivery_fee_id)
);

CREATE TABLE `like`
(
    like_id     BIGINT AUTO_INCREMENT NOT NULL,
    product_id  BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    CONSTRAINT pk_like PRIMARY KEY (like_id)
);

CREATE TABLE member
(
    customer_id         BIGINT      NOT NULL,
    member_id           VARCHAR(20) NOT NULL,
    member_birth        date        NOT NULL,
    member_phone        VARCHAR(11) NOT NULL,
    member_created_at   date        NOT NULL,
    member_login_latest date        NULL,
    member_rank_id      BIGINT      NOT NULL,
    member_state_id     BIGINT      NOT NULL,
    member_role_id      BIGINT      NOT NULL,
    social_auth_id      BIGINT      NOT NULL,
    CONSTRAINT pk_member PRIMARY KEY (customer_id)
);

CREATE TABLE member_coupon
(
    member_coupon_id         BIGINT       AUTO_INCREMENT        NOT NULL,
    customer_id              BIGINT               NOT NULL,
    coupon_id                BIGINT               NOT NULL,
    member_coupon_created_at datetime             NULL,
    member_coupon_period     datetime             NULL,
    member_coupon_code       VARCHAR(255)         NOT NULL,
    member_coupon_used       BIT(1) NOT NULL,
    CONSTRAINT pk_membercoupon PRIMARY KEY (member_coupon_id)
);

CREATE TABLE member_rank
(
    member_rank_id              BIGINT   AUTO_INCREMENT    NOT NULL,
    member_rank_name            VARCHAR(255) NOT NULL,
    member_rank_tier_bonus_rate INT          NOT NULL,
    member_rank_require_amount  BIGINT       NOT NULL,
    CONSTRAINT pk_memberrank PRIMARY KEY (member_rank_id)
);

CREATE TABLE member_role
(
    member_role_id   BIGINT   AUTO_INCREMENT    NOT NULL,
    member_role_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_memberrole PRIMARY KEY (member_role_id)
);

CREATE TABLE member_state
(
    member_state_id   BIGINT   AUTO_INCREMENT    NOT NULL,
    member_state_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_memberstate PRIMARY KEY (member_state_id)
);

CREATE TABLE `order`
(
    order_code           VARCHAR(255)  NOT NULL,
    order_receiver_name  VARCHAR(20)   NOT NULL,
    order_receiver_phone VARCHAR(20)   NOT NULL,
    order_receiver_tel   VARCHAR(20)   NULL,
    order_address_code   VARCHAR(5)    NOT NULL,
    order_address_info   VARCHAR(255)  NOT NULL,
    order_address_detail VARCHAR(255)  NULL,
    order_address_extra  VARCHAR(255)  NOT NULL,
    order_point_amount   BIGINT        NOT NULL,
    order_total_amount   BIGINT       NOT NULL,
    order_memo           TEXT          NULL,
    order_payment_status BIT(1) NOT NULL,
    order_receive_date   datetime      NULL,
    order_shipment_date  datetime      NULL,
    order_created_at     datetime      NOT NULL,
    member_coupon_id     BIGINT        NULL,
    delivery_fee_id      BIGINT        NOT NULL,
    customer_id          BIGINT        NOT NULL,
    CONSTRAINT pk_order PRIMARY KEY (order_code)
);

CREATE TABLE order_detail
(
    order_detail_id        BIGINT    AUTO_INCREMENT    NOT NULL,
    order_quantity         INT DEFAULT 0 NOT NULL,
    order_detail_per_price BIGINT        NOT NULL,
    order_code             VARCHAR(255)  NOT NULL,
    order_state_id         BIGINT        NOT NULL,
    wrapper_id             BIGINT        NULL,
    review_id              BIGINT        NULL,
    CONSTRAINT pk_orderdetail PRIMARY KEY (order_detail_id)
);

CREATE TABLE order_return
(
    order_return_id     BIGINT    AUTO_INCREMENT   NOT NULL,
    order_return_reason TEXT         NOT NULL,
    return_category     VARCHAR(255) NOT NULL,
    order_detail_id     BIGINT       NOT NULL,
    CONSTRAINT pk_orderreturn PRIMARY KEY (order_return_id)
);

CREATE TABLE order_state
(
    order_state_id   BIGINT   AUTO_INCREMENT    NOT NULL,
    order_state_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_orderstate PRIMARY KEY (order_state_id)
);

CREATE TABLE payment
(
    payment_id           BIGINT    AUTO_INCREMENT   NOT NULL,
    payment_key          VARCHAR(255) NOT NULL,
    total_payment_amount BIGINT       NOT NULL,
    payment_requested_at datetime     NOT NULL,
    payment_approved_at  datetime     NULL,
    payment_method_id    BIGINT       NOT NULL,
    order_code           VARCHAR(255) NOT NULL,
    CONSTRAINT pk_payment PRIMARY KEY (payment_id)
);

CREATE TABLE payment_method
(
    payment_method_id   BIGINT   AUTO_INCREMENT    NOT NULL,
    payment_method_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_paymentmethod PRIMARY KEY (payment_method_id)
);

CREATE TABLE point_history
(
    point_history_id   BIGINT   AUTO_INCREMENT    NOT NULL,
    point_amount       BIGINT       NOT NULL,
    point_reason       VARCHAR(255) NOT NULL,
    point_created_at   datetime     NOT NULL,
    customer_id        BIGINT       NOT NULL,
    CONSTRAINT pk_pointhistory PRIMARY KEY (point_history_id)
);

CREATE TABLE point_policy
(
    point_policy_id         BIGINT   AUTO_INCREMENT   NOT NULL,
    point_policy_name       VARCHAR(20) NOT NULL,
    point_policy_figure     BIGINT      NOT NULL,
    point_policy_created_at datetime    NOT NULL,
    CONSTRAINT pk_pointpolicy PRIMARY KEY (point_policy_id)
);

CREATE TABLE position
(
    position_id   BIGINT   AUTO_INCREMENT   NOT NULL,
    position_name VARCHAR(10) NOT NULL,
    CONSTRAINT pk_position PRIMARY KEY (position_id)
);

CREATE TABLE product
(
    product_id            BIGINT   AUTO_INCREMENT    NOT NULL,
    product_state_id      BIGINT       NOT NULL,
    publisher_id          BIGINT       NOT NULL,
    product_title         VARCHAR(30)  NOT NULL,
    product_content       VARCHAR(255) NOT NULL,
    product_description   TEXT         NOT NULL,
    product_published_at  date         NOT NULL,
    product_isbn          VARCHAR(20)  NOT NULL,
    product_regular_price BIGINT       NOT NULL,
    product_sale_price    BIGINT       NOT NULL,
    product_packageable   BIT(1)   NOT NULL,
    product_stock         INT          NOT NULL,
    product_hits          BIGINT DEFAULT 0 NOT NULL,
    product_searches      BIGINT DEFAULT 0 NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (product_id)
);

CREATE TABLE product_category
(
    product_category_id BIGINT AUTO_INCREMENT NOT NULL,
    product_id          BIGINT NOT NULL,
    category_id         BIGINT NOT NULL,
    CONSTRAINT pk_productcategory PRIMARY KEY (product_category_id)
);

CREATE TABLE product_contributor
(
    product_contributor_id BIGINT AUTO_INCREMENT NOT NULL,
    contributor_id         BIGINT NOT NULL,
    product_id             BIGINT NOT NULL,
    CONSTRAINT pk_productcontributor PRIMARY KEY (product_contributor_id)
);

CREATE TABLE product_coupon
(
    coupon_id  BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT pk_productcoupon PRIMARY KEY (coupon_id)
);

CREATE TABLE product_image
(
    product_image_id   BIGINT   AUTO_INCREMENT    NOT NULL,
    product_id         BIGINT       NOT NULL,
    product_image_path VARCHAR(255) NOT NULL,
    CONSTRAINT pk_productimage PRIMARY KEY (product_image_id)
);

CREATE TABLE product_state
(
    product_state_id   BIGINT   AUTO_INCREMENT    NOT NULL,
    product_state_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_productstate PRIMARY KEY (product_state_id)
);

CREATE TABLE product_tag
(
    product_tag_id BIGINT AUTO_INCREMENT NOT NULL,
    product_id     BIGINT NOT NULL,
    tag_id         BIGINT NOT NULL,
    CONSTRAINT pk_producttag PRIMARY KEY (product_tag_id)
);

CREATE TABLE publisher
(
    publisher_id   BIGINT   AUTO_INCREMENT   NOT NULL,
    publisher_name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_publisher PRIMARY KEY (publisher_id)
);

CREATE TABLE review
(
    review_id         BIGINT   AUTO_INCREMENT    NOT NULL,
    product_id        BIGINT       NOT NULL,
    customer_id       BIGINT       NOT NULL,
    review_content    TEXT         NULL,
    review_grade      INT          NOT NULL,
    review_created_at datetime     NOT NULL,
    review_image      VARCHAR(255) NULL,
    CONSTRAINT pk_review PRIMARY KEY (review_id)
);

CREATE TABLE social_auth
(
    social_auth_id   BIGINT   AUTO_INCREMENT    NOT NULL,
    social_auth_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_socialauth PRIMARY KEY (social_auth_id)
);

CREATE TABLE tag
(
    tag_id   BIGINT  AUTO_INCREMENT    NOT NULL,
    tag_name VARCHAR(30) NOT NULL,
    CONSTRAINT pk_tag PRIMARY KEY (tag_id)
);

CREATE TABLE wrapper
(
    wrapper_id       BIGINT   AUTO_INCREMENT    NOT NULL,
    wrapper_price    BIGINT       NOT NULL,
    wrapper_name     VARCHAR(30)  NOT NULL,
    wrapper_image    VARCHAR(255) NOT NULL,
    wrapper_saleable BIT(1)       NOT NULL,
    CONSTRAINT pk_wrapper PRIMARY KEY (wrapper_id)
);

ALTER TABLE cart
    ADD CONSTRAINT uc_cart_customer UNIQUE (customer_id);

ALTER TABLE member_coupon
    ADD CONSTRAINT uc_membercoupon_membercouponcode UNIQUE (member_coupon_code);

ALTER TABLE `order`
    ADD CONSTRAINT uc_order_delivery_fee UNIQUE (delivery_fee_id);

ALTER TABLE `order`
    ADD CONSTRAINT uc_order_member_coupon UNIQUE (member_coupon_id);

ALTER TABLE order_detail
    ADD CONSTRAINT uc_orderdetail_product UNIQUE (product_id);

ALTER TABLE order_detail
    ADD CONSTRAINT uc_orderdetail_review UNIQUE (review_id);

ALTER TABLE payment
    ADD CONSTRAINT uc_payment_order_code UNIQUE (order_code);

ALTER TABLE payment
    ADD CONSTRAINT uc_payment_payment_method UNIQUE (payment_method_id);

ALTER TABLE product
    ADD CONSTRAINT uc_product_product_state UNIQUE (product_state_id);

ALTER TABLE address
    ADD CONSTRAINT FK_ADDRESS_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES member (customer_id);

ALTER TABLE cart_items
    ADD CONSTRAINT FK_CARTITEMS_ON_CART FOREIGN KEY (cart_id) REFERENCES cart (cart_id);

ALTER TABLE cart_items
    ADD CONSTRAINT FK_CARTITEMS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (product_id);

ALTER TABLE cart
    ADD CONSTRAINT FK_CART_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer (customer_id);

ALTER TABLE category_coupon
    ADD CONSTRAINT FK_CATEGORYCOUPON_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (category_id);

ALTER TABLE category_coupon
    ADD CONSTRAINT FK_CATEGORYCOUPON_ON_COUPON FOREIGN KEY (coupon_id) REFERENCES coupon (coupon_id);

ALTER TABLE category
    ADD CONSTRAINT FK_CATEGORY_ON_CATEGORY_ID2 FOREIGN KEY (category_id2) REFERENCES category (category_id);

ALTER TABLE contributor
    ADD CONSTRAINT FK_CONTRIBUTOR_ON_POSITION FOREIGN KEY (position_id) REFERENCES position (position_id);

ALTER TABLE coupon
    ADD CONSTRAINT FK_COUPON_ON_COUPON_POLICY FOREIGN KEY (coupon_policy_id) REFERENCES coupon_policy (coupon_policy_id);

ALTER TABLE `like`
    ADD CONSTRAINT FK_LIKE_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer (customer_id);

ALTER TABLE `like`
    ADD CONSTRAINT FK_LIKE_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (product_id);

ALTER TABLE member_coupon
    ADD CONSTRAINT FK_MEMBERCOUPON_ON_COUPON FOREIGN KEY (coupon_id) REFERENCES coupon (coupon_id);

ALTER TABLE member_coupon
    ADD CONSTRAINT FK_MEMBERCOUPON_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer (customer_id);

ALTER TABLE member
    ADD CONSTRAINT FK_MEMBER_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer (customer_id);

ALTER TABLE member
    ADD CONSTRAINT FK_MEMBER_ON_MEMBER_RANK FOREIGN KEY (member_rank_id) REFERENCES member_rank (member_rank_id);

ALTER TABLE member
    ADD CONSTRAINT FK_MEMBER_ON_MEMBER_ROLE FOREIGN KEY (member_role_id) REFERENCES member_role (member_role_id);

ALTER TABLE member
    ADD CONSTRAINT FK_MEMBER_ON_MEMBER_STATE FOREIGN KEY (member_state_id) REFERENCES member_state (member_state_id);

ALTER TABLE member
    ADD CONSTRAINT FK_MEMBER_ON_SOCIAL_AUTH FOREIGN KEY (social_auth_id) REFERENCES social_auth (social_auth_id);

ALTER TABLE order_detail
    ADD CONSTRAINT FK_ORDERDETAIL_ON_ORDER_CODE FOREIGN KEY (order_code) REFERENCES `order` (order_code);

ALTER TABLE order_detail
    ADD CONSTRAINT FK_ORDERDETAIL_ON_ORDER_STATE FOREIGN KEY (order_state_id) REFERENCES order_state (order_state_id);

ALTER TABLE order_detail
    ADD CONSTRAINT FK_ORDERDETAIL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (product_id);

ALTER TABLE order_detail
    ADD CONSTRAINT FK_ORDERDETAIL_ON_REVIEW FOREIGN KEY (review_id) REFERENCES review (review_id);

ALTER TABLE order_detail
    ADD CONSTRAINT FK_ORDERDETAIL_ON_WRAPPER FOREIGN KEY (wrapper_id) REFERENCES wrapper (wrapper_id);

ALTER TABLE order_return
    ADD CONSTRAINT FK_ORDERRETURN_ON_ORDER_DETAIL FOREIGN KEY (order_detail_id) REFERENCES order_detail (order_detail_id);

ALTER TABLE `order`
    ADD CONSTRAINT FK_ORDER_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer (customer_id);

ALTER TABLE `order`
    ADD CONSTRAINT FK_ORDER_ON_DELIVERY_FEE FOREIGN KEY (delivery_fee_id) REFERENCES delivery_fee (delivery_fee_id);

ALTER TABLE `order`
    ADD CONSTRAINT FK_ORDER_ON_MEMBER_COUPON FOREIGN KEY (member_coupon_id) REFERENCES member_coupon (member_coupon_id);

ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_ON_ORDER_CODE FOREIGN KEY (order_code) REFERENCES `order` (order_code);

ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_ON_PAYMENT_METHOD FOREIGN KEY (payment_method_id) REFERENCES payment_method (payment_method_id);

ALTER TABLE point_history
    ADD CONSTRAINT FK_POINTHISTORY_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES member (customer_id);

ALTER TABLE product_category
    ADD CONSTRAINT FK_PRODUCTCATEGORY_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (category_id);

ALTER TABLE product_category
    ADD CONSTRAINT FK_PRODUCTCATEGORY_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (product_id);

ALTER TABLE product_contributor
    ADD CONSTRAINT FK_PRODUCTCONTRIBUTOR_ON_CONTRIBUTOR FOREIGN KEY (contributor_id) REFERENCES contributor (contributor_id);

ALTER TABLE product_contributor
    ADD CONSTRAINT FK_PRODUCTCONTRIBUTOR_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (product_id);

ALTER TABLE product_coupon
    ADD CONSTRAINT FK_PRODUCTCOUPON_ON_COUPON FOREIGN KEY (coupon_id) REFERENCES coupon (coupon_id);

ALTER TABLE product_coupon
    ADD CONSTRAINT FK_PRODUCTCOUPON_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (product_id);

ALTER TABLE product_image
    ADD CONSTRAINT FK_PRODUCTIMAGE_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (product_id);

ALTER TABLE product_tag
    ADD CONSTRAINT FK_PRODUCTTAG_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (product_id);

ALTER TABLE product_tag
    ADD CONSTRAINT FK_PRODUCTTAG_ON_TAG FOREIGN KEY (tag_id) REFERENCES tag (tag_id);

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_PRODUCT_STATE FOREIGN KEY (product_state_id) REFERENCES product_state (product_state_id);

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_PUBLISHER FOREIGN KEY (publisher_id) REFERENCES publisher (publisher_id);

ALTER TABLE review
    ADD CONSTRAINT FK_REVIEW_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer (customer_id);

ALTER TABLE review
    ADD CONSTRAINT FK_REVIEW_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (product_id);