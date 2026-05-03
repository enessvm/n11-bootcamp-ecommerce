/**
 * Best-effort frontend preview of how a validated coupon will affect the cart.
 * Backend is the source of truth at order creation; this is just for UX.
 *
 * promotion: PromotionValidationResponse with valid=true
 *   { code, valid, discountType, discountValue, scope, minCartTotal, reason }
 *
 * Returns the discount amount as a Number (TRY).
 */
export function computeCartDiscount(subtotalAmount, lineCount, promotion) {
  if (!promotion?.valid) return 0;
  const value = Number(promotion.discountValue);
  const subtotal = Number(subtotalAmount);

  if (promotion.discountType === 'PERCENTAGE') {
    return round2(subtotal * (value / 100));
  }
  // FIXED_AMOUNT
  if (promotion.scope === 'LINE_ITEM') {
    return round2(value * lineCount);
  }
  return round2(value); // CART_TOTAL × FIXED_AMOUNT
}

export function describePromotion(promotion) {
  if (!promotion?.valid) return '';
  const value = Number(promotion.discountValue);
  const type = promotion.discountType === 'PERCENTAGE' ? `${value}%` : `${value} TRY`;
  const scope = promotion.scope === 'LINE_ITEM' ? 'each item' : 'cart total';
  return `${type} off ${scope}`;
}

const REASON_LABELS = {
  NOT_FOUND: 'Coupon code not found.',
  INACTIVE: 'This coupon is no longer active.',
  NOT_YET_VALID: 'This coupon is not valid yet.',
  EXPIRED: 'This coupon has expired.',
  MAX_USES_REACHED: 'This coupon has reached its usage limit.',
  CART_BELOW_MINIMUM: 'Your cart total is below the minimum required for this coupon.',
};

export function describePromotionFailure(promotion) {
  if (!promotion || promotion.valid) return '';
  return REASON_LABELS[promotion.reason] ?? 'This coupon cannot be applied.';
}

function round2(n) {
  return Math.round(n * 100) / 100;
}
