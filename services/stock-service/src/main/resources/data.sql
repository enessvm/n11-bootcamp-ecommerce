INSERT INTO stock_level (product_id, available_quantity, reserved_quantity, created_at, updated_at)
VALUES
    (1,  21,  0, now(), now()),
    (2,  50, 0, now(), now()),
    (3,  50, 0, now(), now()),
    (4,  50, 0, now(), now()),
    (5,  13, 0, now(), now()),
    (6,  0, 0, now(), now()),
    (7,  50, 0, now(), now()),
    (8,  50, 0, now(), now()),
    (9,  50, 0, now(), now()),
    (10, 50, 0, now(), now()),
    (11, 50, 0, now(), now()),
    (12, 6, 0, now(), now()),
    (13, 200, 0, now(), now()),
    (14, 100, 0, now(), now()),
    (15, 50, 0, now(), now()),
    (16, 50, 0, now(), now()),
    (17, 50, 0, now(), now()),
    (18, 3, 0, now(), now()),
    (19, 50, 0, now(), now()),
    (20, 1,  0, now(), now())
    ON CONFLICT (product_id) DO NOTHING;
