
CREATE TABLE IF NOT EXISTS `admin_account`
(
    `admin_id`      BINARY(16)   NOT NULL,
    `admin_role`    VARCHAR(30)  NOT NULL COMMENT 'system | admin',
    `status`        VARCHAR(30)  NOT NULL COMMENT 'pending | active | inactive',
    `username`      VARCHAR(255) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `created_at`    DATETIME     NOT NULL,
    `deleted_at`    DATETIME     NULL,
    PRIMARY KEY (`admin_id`),
    CONSTRAINT `uq_admin_account_username` UNIQUE (`username`)
);

CREATE TABLE IF NOT EXISTS `admin_profile`
(
    `admin_id`    BINARY(16)   NOT NULL,
    `employee_no` VARCHAR(20)  NOT NULL,
    `name`        VARCHAR(255) NOT NULL,
    `phone`       VARCHAR(30)  NOT NULL,
    `email`       VARCHAR(255) NOT NULL,
    PRIMARY KEY (`admin_id`),
    CONSTRAINT `uq_admin_profile_employee_no` UNIQUE (`employee_no`),
    CONSTRAINT `fk_admin_profile_admin_account`
        FOREIGN KEY (`admin_id`) REFERENCES `admin_account` (`admin_id`)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `branch_account`
(
    `branch_id`     BINARY(16)   NOT NULL,
    `branch_code`   CHAR(8)      NOT NULL COMMENT 'YYYYNNNN',
    `auth_key_hash` VARCHAR(255) NOT NULL,
    `status`        VARCHAR(30)  NOT NULL COMMENT 'active | inactive',
    `name`          VARCHAR(255) NOT NULL,
    `address`       VARCHAR(255) NOT NULL,
    `created_at`    DATETIME     NOT NULL,
    `deleted_at`    DATETIME     NULL,
    PRIMARY KEY (`branch_id`),
    CONSTRAINT `uq_branch_account_code` UNIQUE (`branch_code`),
    CONSTRAINT `uq_branch_account_name` UNIQUE (`name`)
);

CREATE TABLE IF NOT EXISTS `category`
(
    `category_id` BINARY(16)   NOT NULL,
    `name`        VARCHAR(255) NOT NULL,
    `created_at`  DATETIME     NOT NULL,
    PRIMARY KEY (`category_id`),
    CONSTRAINT `uq_category_name` UNIQUE (`name`)
);

CREATE TABLE IF NOT EXISTS `tag`
(
    `tag_id`     BINARY(16)   NOT NULL,
    `name`       VARCHAR(255) NOT NULL,
    `created_at` DATETIME     NOT NULL,
    PRIMARY KEY (`tag_id`),
    CONSTRAINT `uq_tag_name` UNIQUE (`name`)
);

CREATE TABLE IF NOT EXISTS `option_group`
(
    `option_group_id` BINARY(16)   NOT NULL,
    `name`            VARCHAR(255) NOT NULL,
    `description`     TEXT         NULL,
    `created_at`      DATETIME     NOT NULL,
    PRIMARY KEY (`option_group_id`)
);

CREATE TABLE IF NOT EXISTS `option_item`
(
    `option_item_id`  BINARY(16)   NOT NULL,
    `option_group_id` BINARY(16)   NOT NULL,
    `name`            VARCHAR(255) NOT NULL,
    `description`     TEXT         NULL,
    `price`           INT          NOT NULL,
    `created_at`      DATETIME     NOT NULL,
    PRIMARY KEY (`option_item_id`),
    CONSTRAINT `fk_option_item_option_group`
        FOREIGN KEY (`option_group_id`) REFERENCES `option_group` (`option_group_id`)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `product`
(
    `product_id`    BINARY(16)    NOT NULL,
    `name`          VARCHAR(255)  NOT NULL,
    `image_url`     VARCHAR(1000) NULL,
    `description`   TEXT          NULL,
    `price`         INT           NOT NULL,
    `category_id`   BINARY(16)    NULL,
    `kcal`          INT           NULL,
    `allergen_info` VARCHAR(1000) NULL,
    `kind`          VARCHAR(30)   NOT NULL COMMENT 'common | branch_exclusive',
    `branch_id`     BINARY(16)    NULL,
    `status`        VARCHAR(30)   NOT NULL COMMENT 'active | inactive',
    `created_at`    DATETIME      NOT NULL,
    PRIMARY KEY (`product_id`),
    CONSTRAINT `fk_product_category`
        FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
            ON DELETE SET NULL,
    CONSTRAINT `fk_product_branch`
        FOREIGN KEY (`branch_id`) REFERENCES `branch_account` (`branch_id`)
            ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS `product_tag`
(
    `product_tag_id` BINARY(16) NOT NULL,
    `product_id`     BINARY(16) NOT NULL,
    `tag_id`         BINARY(16) NOT NULL,
    `created_at`     DATETIME   NOT NULL,
    PRIMARY KEY (`product_tag_id`),
    CONSTRAINT `uq_product_tag` UNIQUE (`product_id`, `tag_id`),
    CONSTRAINT `fk_product_tag_product`
        FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_product_tag_tag`
        FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `product_option_group`
(
    `product_option_group_id` BINARY(16) NOT NULL,
    `product_id`              BINARY(16) NOT NULL,
    `option_group_id`         BINARY(16) NOT NULL,
    `is_required`             BOOLEAN    NOT NULL,
    `allow_multiple`          BOOLEAN    NOT NULL,
    `created_at`              DATETIME   NOT NULL,
    PRIMARY KEY (`product_option_group_id`),
    CONSTRAINT `uq_product_option_group` UNIQUE (`product_id`, `option_group_id`),
    CONSTRAINT `fk_product_option_group_product`
        FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_product_option_group_option_group`
        FOREIGN KEY (`option_group_id`) REFERENCES `option_group` (`option_group_id`)
            ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS `product_sales_override`
(
    `branch_id`                 BINARY(16)  NOT NULL,
    `product_id`                BINARY(16)  NOT NULL,
    `status`                    VARCHAR(30) NOT NULL COMMENT 'hidden | sold_out',
    `created_at`                DATETIME    NOT NULL,
    PRIMARY KEY (`branch_id`, `product_id`),
    CONSTRAINT `fk_product_sales_override_branch`
        FOREIGN KEY (`branch_id`) REFERENCES `branch_account` (`branch_id`)
            ON DELETE CASCADE,
    CONSTRAINT `fk_product_sales_override_product`
        FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `branch_code_sequence`
(
    `year`     SMALLINT UNSIGNED NOT NULL COMMENT '연도 (YYYY)',
    `next_seq` SMALLINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '다음 발급 시퀀스 (1~9999)',
    PRIMARY KEY (`year`)
);
