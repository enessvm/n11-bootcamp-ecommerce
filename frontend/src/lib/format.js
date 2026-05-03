const formatterCache = new Map();

function getMoneyFormatter(currency) {
  const code = currency || 'TRY';
  if (!formatterCache.has(code)) {
    formatterCache.set(
      code,
      new Intl.NumberFormat('tr-TR', {
        style: 'currency',
        currency: code,
        maximumFractionDigits: 2,
      }),
    );
  }
  return formatterCache.get(code);
}

export function formatMoney(money) {
  if (!money || money.amount == null) return '';
  const amount = typeof money.amount === 'string' ? Number(money.amount) : money.amount;
  return getMoneyFormatter(money.currency).format(amount);
}

export const STOCK_LABELS = {
  IN_STOCK: 'In stock',
  LOW_STOCK: 'Low stock',
  OUT_OF_STOCK: 'Out of stock',
};
