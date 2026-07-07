-- 개발 환경 전용 더미 데이터. db/migration과 별도 위치에 있어 배포 환경에서는 스캔되지 않는다.
INSERT INTO `category` (`category_id`, `name`, `created_at`)
VALUES
    (UNHEX(REPLACE('11111111-1111-1111-1111-111111111101', '-', '')), '커피', NOW()),
    (UNHEX(REPLACE('11111111-1111-1111-1111-111111111102', '-', '')), '음료', NOW()),
    (UNHEX(REPLACE('11111111-1111-1111-1111-111111111103', '-', '')), '디저트', NOW()) AS new
ON DUPLICATE KEY UPDATE `name` = new.`name`;

INSERT INTO `tag` (`tag_id`, `name`, `created_at`)
VALUES
    (UNHEX(REPLACE('22222222-2222-2222-2222-222222222201', '-', '')), '신메뉴', NOW()),
    (UNHEX(REPLACE('22222222-2222-2222-2222-222222222202', '-', '')), '인기', NOW()),
    (UNHEX(REPLACE('22222222-2222-2222-2222-222222222203', '-', '')), '비건', NOW()) AS new
ON DUPLICATE KEY UPDATE `name` = new.`name`;
