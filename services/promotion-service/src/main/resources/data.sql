INSERT INTO promotion (code, name, discount_type, discount_value, scope,
                       min_cart_total, max_uses, times_redeemed,
                       valid_from, valid_until, active, created_at, updated_at)
VALUES
  ('WELCOME10', '10% off your first order',          'PERCENTAGE',   10.00,  'CART_TOTAL', NULL,    NULL, 0, now(), now() + interval '1 year', true, now(), now()),
  ('SAVE100',   '100 TRY off orders over 1000 TRY',  'FIXED_AMOUNT', 100.00, 'CART_TOTAL', 1000.00, NULL, 0, now(), now() + interval '1 year', true, now(), now()),
  ('ITEM20',    '20% off each item',                 'PERCENTAGE',   20.00,  'LINE_ITEM',  NULL,    NULL, 0, now(), now() + interval '1 year', true, now(), now()),
  ('BULK50',    '50 TRY off each item, min cart 500','FIXED_AMOUNT', 50.00,  'LINE_ITEM',  500.00,  NULL, 0, now(), now() + interval '1 year', true, now(), now())
  ON CONFLICT (code) DO NOTHING;
