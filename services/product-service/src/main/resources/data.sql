
-- Root categories
INSERT INTO category (name, slug, parent_id, created_at, updated_at)
VALUES ('Electronics', 'electronics', NULL, now(), now())
    ON CONFLICT (slug) DO NOTHING;

INSERT INTO category (name, slug, parent_id, created_at, updated_at)
VALUES ('Audio', 'audio', NULL, now(), now())
    ON CONFLICT (slug) DO NOTHING;

-- Children of Electronics
INSERT INTO category (name, slug, parent_id, created_at, updated_at)
VALUES (
           'Power Banks',
           'power-banks',
           (SELECT id FROM category WHERE slug = 'electronics'),
           now(),
           now()
       )
    ON CONFLICT (slug) DO NOTHING;

INSERT INTO category (name, slug, parent_id, created_at, updated_at)
VALUES (
           'Headphones',
           'headphones',
           (SELECT id FROM category WHERE slug = 'electronics'),
           now(),
           now()
       )
    ON CONFLICT (slug) DO NOTHING;

-- Children of Audio
INSERT INTO category (name, slug, parent_id, created_at, updated_at)
VALUES (
           'Speakers',
           'speakers',
           (SELECT id FROM category WHERE slug = 'audio'),
           now(),
           now()
       )
    ON CONFLICT (slug) DO NOTHING;