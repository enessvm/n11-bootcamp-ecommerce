INSERT INTO category (name, slug, parent_id, created_at, updated_at)
VALUES ('Electronics', 'electronics', NULL, now(), now())
    ON CONFLICT (slug) DO NOTHING;

INSERT INTO category (name, slug, parent_id, created_at, updated_at)
VALUES ('Home & Kitchen', 'home-kitchen', NULL, now(), now())
    ON CONFLICT (slug) DO NOTHING;

INSERT INTO category (name, slug, parent_id, created_at, updated_at)
VALUES ('Fashion', 'fashion', NULL, now(), now())
    ON CONFLICT (slug) DO NOTHING;

INSERT INTO category (name, slug, parent_id, created_at, updated_at)
VALUES ('Sports', 'sports', NULL, now(), now())
    ON CONFLICT (slug) DO NOTHING;


INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Sony WH-1000XM5 Wireless Headphones',
       'Industry-leading noise cancellation, 30-hour battery life, and crystal-clear hands-free calling.',
       'Sony',
       (SELECT id FROM category WHERE slug = 'electronics'),
       12999.00, 'TRY',
       'https://picsum.photos/seed/sony-wh1000xm5/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Sony WH-1000XM5 Wireless Headphones');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Apple AirPods Pro (2nd Generation)',
       'Active noise cancellation, adaptive transparency, and personalized spatial audio with the H2 chip.',
       'Apple',
       (SELECT id FROM category WHERE slug = 'electronics'),
       8999.00, 'TRY',
       'https://picsum.photos/seed/airpods-pro-2/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Apple AirPods Pro (2nd Generation)');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Samsung 32" 4K UHD Smart Monitor M8',
       '32-inch 4K UHD smart monitor with Smart Hub, slim design, and built-in webcam.',
       'Samsung',
       (SELECT id FROM category WHERE slug = 'electronics'),
       19999.00, 'TRY',
       'https://picsum.photos/seed/samsung-m8-32/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Samsung 32" 4K UHD Smart Monitor M8');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Logitech MX Keys S Wireless Keyboard',
       'Premium wireless keyboard with smart backlighting, comfortable typing, and multi-device pairing.',
       'Logitech',
       (SELECT id FROM category WHERE slug = 'electronics'),
       3499.00, 'TRY',
       'https://picsum.photos/seed/logitech-mx-keys-s/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Logitech MX Keys S Wireless Keyboard');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Anker PowerCore 20000mAh Power Bank',
       'High-capacity 20000mAh power bank with 65W PD fast charging and dual USB-C ports.',
       'Anker',
       (SELECT id FROM category WHERE slug = 'electronics'),
       1299.00, 'TRY',
       'https://picsum.photos/seed/anker-powercore-20000/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Anker PowerCore 20000mAh Power Bank');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Nespresso Vertuo Plus Coffee Machine',
       'Single-serve coffee machine with Centrifusion technology. Brews five cup sizes from espresso to alto.',
       'Nespresso',
       (SELECT id FROM category WHERE slug = 'home-kitchen'),
       6499.00, 'TRY',
       'https://picsum.photos/seed/nespresso-vertuo-plus/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Nespresso Vertuo Plus Coffee Machine');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Philips Air Fryer XXL HD9650',
       '7.3L XXL air fryer with Twin TurboStar technology. Cooks family-sized portions with up to 90% less fat.',
       'Philips',
       (SELECT id FROM category WHERE slug = 'home-kitchen'),
       4999.00, 'TRY',
       'https://picsum.photos/seed/philips-airfryer-xxl/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Philips Air Fryer XXL HD9650');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Dyson V11 Absolute Cordless Vacuum',
       'Powerful cordless vacuum with intelligent suction across all floor types. Up to 60-minute runtime.',
       'Dyson',
       (SELECT id FROM category WHERE slug = 'home-kitchen'),
       24999.00, 'TRY',
       'https://picsum.photos/seed/dyson-v11-absolute/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Dyson V11 Absolute Cordless Vacuum');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'KitchenAid Artisan Stand Mixer',
       'Iconic 4.8L tilt-head stand mixer with 10 speeds and a wide range of attachments for every kitchen task.',
       'KitchenAid',
       (SELECT id FROM category WHERE slug = 'home-kitchen'),
       18999.00, 'TRY',
       'https://picsum.photos/seed/kitchenaid-artisan/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'KitchenAid Artisan Stand Mixer');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Tefal Hard Titanium 3-Piece Pan Set',
       'Non-stick titanium-coated cookware set with Thermo-Spot indicator. Includes 22cm, 26cm, and 30cm pans.',
       'Tefal',
       (SELECT id FROM category WHERE slug = 'home-kitchen'),
       1899.00, 'TRY',
       'https://picsum.photos/seed/tefal-titanium-set/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Tefal Hard Titanium 3-Piece Pan Set');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Nike Sportswear Tech Fleece Hoodie',
       'Premium full-zip hoodie made from soft, lightweight Tech Fleece for warmth without the bulk.',
       'Nike',
       (SELECT id FROM category WHERE slug = 'fashion'),
       3499.00, 'TRY',
       'https://picsum.photos/seed/nike-tech-fleece/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Nike Sportswear Tech Fleece Hoodie');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Levi''s 501 Original Fit Jeans',
       'The iconic straight leg jean since 1873. Original button fly and authentic indigo wash.',
       'Levi''s',
       (SELECT id FROM category WHERE slug = 'fashion'),
       2299.00, 'TRY',
       'https://picsum.photos/seed/levis-501/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Levi''s 501 Original Fit Jeans');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Ray-Ban Wayfarer Classic Sunglasses',
       'The original Wayfarer with timeless silhouette. UV-protective polarized lenses and acetate frame.',
       'Ray-Ban',
       (SELECT id FROM category WHERE slug = 'fashion'),
       4999.00, 'TRY',
       'https://picsum.photos/seed/rayban-wayfarer/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Ray-Ban Wayfarer Classic Sunglasses');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Adidas Originals Trefoil Tee',
       'Soft cotton T-shirt with the iconic embroidered Trefoil logo. Regular fit for everyday wear.',
       'Adidas',
       (SELECT id FROM category WHERE slug = 'fashion'),
       999.00, 'TRY',
       'https://picsum.photos/seed/adidas-trefoil-tee/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Adidas Originals Trefoil Tee');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Fossil Gen 6 Smartwatch',
       'Wear OS smartwatch with heart-rate tracking, GPS, and rapid charging. Compatible with Android and iOS.',
       'Fossil',
       (SELECT id FROM category WHERE slug = 'fashion'),
       7499.00, 'TRY',
       'https://picsum.photos/seed/fossil-gen6/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Fossil Gen 6 Smartwatch');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Wilson Pro Staff RF97 Tennis Racket',
       'The signature racket of Roger Federer. 97 sq inch head, 340g unstrung, classic feel for advanced players.',
       'Wilson',
       (SELECT id FROM category WHERE slug = 'sports'),
       8999.00, 'TRY',
       'https://picsum.photos/seed/wilson-prostaff-rf97/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Wilson Pro Staff RF97 Tennis Racket');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Quechua MH100 2-Person Camping Tent',
       'Easy-pitch dome tent for two people. Waterproof to 2000mm, ventilated for warm nights.',
       'Quechua',
       (SELECT id FROM category WHERE slug = 'sports'),
       2499.00, 'TRY',
       'https://picsum.photos/seed/quechua-mh100/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Quechua MH100 2-Person Camping Tent');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Adidas Predator Edge.3 Football Boots',
       'Firm-ground football boots with rubberized strike zones for shot precision and ZoneSkin upper.',
       'Adidas',
       (SELECT id FROM category WHERE slug = 'sports'),
       2999.00, 'TRY',
       'https://picsum.photos/seed/adidas-predator-edge3/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Adidas Predator Edge.3 Football Boots');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Nike Air Zoom Pegasus 40 Running Shoes',
       'Responsive everyday trainer with Zoom Air units in the forefoot and breathable engineered mesh.',
       'Nike',
       (SELECT id FROM category WHERE slug = 'sports'),
       4499.00, 'TRY',
       'https://picsum.photos/seed/nike-pegasus-40/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Nike Air Zoom Pegasus 40 Running Shoes');

INSERT INTO product (name, description, brand, category_id,
                     list_price_amount, list_price_currency,
                     primary_image_url, deleted, created_at, updated_at)
SELECT 'Yonex Astrox 88D Pro Badminton Racquet',
       'Power-oriented badminton racquet with rotational generator system and 4U weight for fast doubles play.',
       'Yonex',
       (SELECT id FROM category WHERE slug = 'sports'),
       5499.00, 'TRY',
       'https://picsum.photos/seed/yonex-astrox-88d/600/600',
       false, clock_timestamp(), clock_timestamp()
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Yonex Astrox 88D Pro Badminton Racquet');


-- Sony WH-1000XM5 — 3 additional images
INSERT INTO product_image (product_id, url, position)
SELECT (SELECT id FROM product WHERE name = 'Sony WH-1000XM5 Wireless Headphones'),
       'https://picsum.photos/seed/sony-wh1000xm5-side/600/600', 1
WHERE NOT EXISTS (
    SELECT 1 FROM product_image pi
    JOIN product p ON p.id = pi.product_id
    WHERE p.name = 'Sony WH-1000XM5 Wireless Headphones' AND pi.position = 1
);

INSERT INTO product_image (product_id, url, position)
SELECT (SELECT id FROM product WHERE name = 'Sony WH-1000XM5 Wireless Headphones'),
       'https://picsum.photos/seed/sony-wh1000xm5-folded/600/600', 2
WHERE NOT EXISTS (
    SELECT 1 FROM product_image pi
    JOIN product p ON p.id = pi.product_id
    WHERE p.name = 'Sony WH-1000XM5 Wireless Headphones' AND pi.position = 2
);

INSERT INTO product_image (product_id, url, position)
SELECT (SELECT id FROM product WHERE name = 'Sony WH-1000XM5 Wireless Headphones'),
       'https://picsum.photos/seed/sony-wh1000xm5-case/600/600', 3
WHERE NOT EXISTS (
    SELECT 1 FROM product_image pi
    JOIN product p ON p.id = pi.product_id
    WHERE p.name = 'Sony WH-1000XM5 Wireless Headphones' AND pi.position = 3
);

INSERT INTO product_image (product_id, url, position)
SELECT (SELECT id FROM product WHERE name = 'Dyson V11 Absolute Cordless Vacuum'),
       'https://picsum.photos/seed/dyson-v11-handle/600/600', 1
WHERE NOT EXISTS (
    SELECT 1 FROM product_image pi
    JOIN product p ON p.id = pi.product_id
    WHERE p.name = 'Dyson V11 Absolute Cordless Vacuum' AND pi.position = 1
);

INSERT INTO product_image (product_id, url, position)
SELECT (SELECT id FROM product WHERE name = 'Dyson V11 Absolute Cordless Vacuum'),
       'https://picsum.photos/seed/dyson-v11-attachments/600/600', 2
WHERE NOT EXISTS (
    SELECT 1 FROM product_image pi
    JOIN product p ON p.id = pi.product_id
    WHERE p.name = 'Dyson V11 Absolute Cordless Vacuum' AND pi.position = 2
);

INSERT INTO product_image (product_id, url, position)
SELECT (SELECT id FROM product WHERE name = 'Dyson V11 Absolute Cordless Vacuum'),
       'https://picsum.photos/seed/dyson-v11-dock/600/600', 3
WHERE NOT EXISTS (
    SELECT 1 FROM product_image pi
    JOIN product p ON p.id = pi.product_id
    WHERE p.name = 'Dyson V11 Absolute Cordless Vacuum' AND pi.position = 3
);

INSERT INTO product_image (product_id, url, position)
SELECT (SELECT id FROM product WHERE name = 'Nike Air Zoom Pegasus 40 Running Shoes'),
       'https://picsum.photos/seed/nike-pegasus-40-side/600/600', 1
WHERE NOT EXISTS (
    SELECT 1 FROM product_image pi
    JOIN product p ON p.id = pi.product_id
    WHERE p.name = 'Nike Air Zoom Pegasus 40 Running Shoes' AND pi.position = 1
);

INSERT INTO product_image (product_id, url, position)
SELECT (SELECT id FROM product WHERE name = 'Nike Air Zoom Pegasus 40 Running Shoes'),
       'https://picsum.photos/seed/nike-pegasus-40-sole/600/600', 2
WHERE NOT EXISTS (
    SELECT 1 FROM product_image pi
    JOIN product p ON p.id = pi.product_id
    WHERE p.name = 'Nike Air Zoom Pegasus 40 Running Shoes' AND pi.position = 2
);
