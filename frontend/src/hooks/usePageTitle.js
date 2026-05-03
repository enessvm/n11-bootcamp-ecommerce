import { useEffect } from 'react';

const SUFFIX = 'ECOM';

/**
 * Sets document.title to "{title} — ECOM". Pass null/undefined to skip
 * (useful when waiting on async data — call once data arrives).
 * Restores the previous title on unmount.
 */
export function usePageTitle(title) {
  useEffect(() => {
    if (!title) return;
    const previous = document.title;
    document.title = `${title} — ${SUFFIX}`;
    return () => {
      document.title = previous;
    };
  }, [title]);
}
