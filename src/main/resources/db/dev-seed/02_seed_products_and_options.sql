-- 개발 환경 전용 더미 데이터. 01_seed_categories_and_tags.sql이 먼저 적용된 뒤 실행되어야 한다
-- (product가 category_id를 참조하므로, 파일명 앞 번호로 순서를 명시적으로 고정함).

-- 옵션그룹 + 옵션 항목
INSERT INTO `option_group` (`option_group_id`, `name`, `description`, `created_at`)
VALUES
    (UNHEX(REPLACE('33333333-3333-3333-3333-333333333301', '-', '')), '샷 추가', '에스프레소 샷 추가', NOW()),
    (UNHEX(REPLACE('33333333-3333-3333-3333-333333333302', '-', '')), '사이즈 변경', '음료 사이즈 선택', NOW()) AS new
ON DUPLICATE KEY UPDATE `name` = new.`name`, `description` = new.`description`;

INSERT INTO `option_item` (`option_item_id`, `option_group_id`, `name`, `description`, `price`, `created_at`)
VALUES
    (UNHEX(REPLACE('44444444-4444-4444-4444-444444444401', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333301', '-', '')), '1샷 추가', NULL, 500, NOW()),
    (UNHEX(REPLACE('44444444-4444-4444-4444-444444444402', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333301', '-', '')), '2샷 추가', NULL, 1000, NOW()),
    (UNHEX(REPLACE('44444444-4444-4444-4444-444444444403', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333302', '-', '')), 'Tall', NULL, 0, NOW()),
    (UNHEX(REPLACE('44444444-4444-4444-4444-444444444404', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333302', '-', '')), 'Grande', NULL, 500, NOW()),
    (UNHEX(REPLACE('44444444-4444-4444-4444-444444444405', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333302', '-', '')), 'Venti', NULL, 1000, NOW()) AS new
ON DUPLICATE KEY UPDATE `name` = new.`name`, `price` = new.`price`;

-- 상품 (전 지점 공통 판매 상품, branch_id 없음)
INSERT INTO `product` (`product_id`, `name`, `image_url`, `description`, `price`, `category_id`, `kcal`, `allergen_info`, `kind`, `branch_id`, `status`, `created_at`)
VALUES
    (UNHEX(REPLACE('55555555-5555-5555-5555-555555555501', '-', '')), '아메리카노', NULL, '깔끔한 에스프레소 베이스', 4500, UNHEX(REPLACE('11111111-1111-1111-1111-111111111101', '-', '')), 10, '', 'common', NULL, 'active', NOW()),
    (UNHEX(REPLACE('55555555-5555-5555-5555-555555555502', '-', '')), '카페라떼', NULL, '부드러운 우유 거품', 5000, UNHEX(REPLACE('11111111-1111-1111-1111-111111111101', '-', '')), 180, 'milk', 'common', NULL, 'active', NOW()),
    (UNHEX(REPLACE('55555555-5555-5555-5555-555555555503', '-', '')), '콜드브루', NULL, '저온 장시간 추출', 5500, UNHEX(REPLACE('11111111-1111-1111-1111-111111111101', '-', '')), 5, '', 'common', NULL, 'active', NOW()),
    (UNHEX(REPLACE('55555555-5555-5555-5555-555555555504', '-', '')), '얼그레이 티', NULL, '베르가못 향 홍차', 4800, UNHEX(REPLACE('11111111-1111-1111-1111-111111111102', '-', '')), 2, '', 'common', NULL, 'active', NOW()),
    (UNHEX(REPLACE('55555555-5555-5555-5555-555555555505', '-', '')), '티라미수', NULL, '마스카포네 크림', 6500, UNHEX(REPLACE('11111111-1111-1111-1111-111111111103', '-', '')), 420, 'milk,egg', 'common', NULL, 'active', NOW()) AS new
ON DUPLICATE KEY UPDATE `name` = new.`name`, `price` = new.`price`, `status` = new.`status`;

-- 상품-태그 연결
INSERT INTO `product_tag` (`product_tag_id`, `product_id`, `tag_id`, `created_at`)
VALUES
    (UNHEX(REPLACE('66666666-6666-6666-6666-666666666601', '-', '')), UNHEX(REPLACE('55555555-5555-5555-5555-555555555501', '-', '')), UNHEX(REPLACE('22222222-2222-2222-2222-222222222202', '-', '')), NOW()),
    (UNHEX(REPLACE('66666666-6666-6666-6666-666666666602', '-', '')), UNHEX(REPLACE('55555555-5555-5555-5555-555555555502', '-', '')), UNHEX(REPLACE('22222222-2222-2222-2222-222222222202', '-', '')), NOW()),
    (UNHEX(REPLACE('66666666-6666-6666-6666-666666666603', '-', '')), UNHEX(REPLACE('55555555-5555-5555-5555-555555555503', '-', '')), UNHEX(REPLACE('22222222-2222-2222-2222-222222222201', '-', '')), NOW()),
    (UNHEX(REPLACE('66666666-6666-6666-6666-666666666604', '-', '')), UNHEX(REPLACE('55555555-5555-5555-5555-555555555504', '-', '')), UNHEX(REPLACE('22222222-2222-2222-2222-222222222203', '-', '')), NOW()),
    (UNHEX(REPLACE('66666666-6666-6666-6666-666666666605', '-', '')), UNHEX(REPLACE('55555555-5555-5555-5555-555555555505', '-', '')), UNHEX(REPLACE('22222222-2222-2222-2222-222222222201', '-', '')), NOW()) AS new
ON DUPLICATE KEY UPDATE `created_at` = new.`created_at`;

-- 상품-옵션그룹 연결 (아메리카노/카페라떼/콜드브루에 샷 추가 + 사이즈 변경)
INSERT INTO `product_option_group` (`product_option_group_id`, `product_id`, `option_group_id`, `is_required`, `allow_multiple`, `created_at`)
VALUES
    (UNHEX(REPLACE('77777777-7777-7777-7777-777777777701', '-', '')), UNHEX(REPLACE('55555555-5555-5555-5555-555555555501', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333301', '-', '')), FALSE, FALSE, NOW()),
    (UNHEX(REPLACE('77777777-7777-7777-7777-777777777702', '-', '')), UNHEX(REPLACE('55555555-5555-5555-5555-555555555501', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333302', '-', '')), TRUE, FALSE, NOW()),
    (UNHEX(REPLACE('77777777-7777-7777-7777-777777777703', '-', '')), UNHEX(REPLACE('55555555-5555-5555-5555-555555555502', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333301', '-', '')), FALSE, FALSE, NOW()),
    (UNHEX(REPLACE('77777777-7777-7777-7777-777777777704', '-', '')), UNHEX(REPLACE('55555555-5555-5555-5555-555555555502', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333302', '-', '')), TRUE, FALSE, NOW()),
    (UNHEX(REPLACE('77777777-7777-7777-7777-777777777705', '-', '')), UNHEX(REPLACE('55555555-5555-5555-5555-555555555503', '-', '')), UNHEX(REPLACE('33333333-3333-3333-3333-333333333302', '-', '')), TRUE, FALSE, NOW()) AS new
ON DUPLICATE KEY UPDATE `is_required` = new.`is_required`, `allow_multiple` = new.`allow_multiple`;
