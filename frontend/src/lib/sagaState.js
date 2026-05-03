/**
 * Maps order-service SagaState enum values to UI semantics.
 * Mirrors services/order-service/.../SagaState.java.
 */

const STATE_DEFINITIONS = {
  INITIATED:             { label: 'Order received',   stage: 'preparing',  variant: 'secondary', terminal: false },
  STOCK_RESERVED:        { label: 'Stock reserved',   stage: 'preparing',  variant: 'secondary', terminal: false },
  PROMOTION_APPLIED:     { label: 'Discount applied', stage: 'preparing',  variant: 'secondary', terminal: false },
  PAYMENT_REQUESTED:     { label: 'Awaiting payment', stage: 'payment',    variant: 'warning',   terminal: false },
  PAYMENT_PENDING_USER:  { label: 'Awaiting payment', stage: 'payment',    variant: 'warning',   terminal: false },
  COMMIT_REQUESTED:      { label: 'Finalizing',       stage: 'finalizing', variant: 'secondary', terminal: false },
  COMPLETED:             { label: 'Order confirmed',  stage: 'done',       variant: 'success',   terminal: true  },

  PROMOTION_FAILED:      { label: 'Coupon rejected',  stage: 'failed',     variant: 'destructive', terminal: true },
  PAYMENT_FAILED:        { label: 'Payment failed',   stage: 'failed',     variant: 'destructive', terminal: true },
  COMPENSATING_PROMOTION:{ label: 'Cleaning up',      stage: 'failed',     variant: 'destructive', terminal: false },
  COMPENSATING_STOCK:    { label: 'Cleaning up',      stage: 'failed',     variant: 'destructive', terminal: false },
  FAILED:                { label: 'Order failed',     stage: 'failed',     variant: 'destructive', terminal: true },
};

const FALLBACK = { label: 'Unknown', stage: 'preparing', variant: 'secondary', terminal: false };

export function describeSagaState(state) {
  if (!state) return FALLBACK;
  return STATE_DEFINITIONS[state] ?? FALLBACK;
}

export function isTerminalSagaState(state) {
  return describeSagaState(state).terminal;
}
