import { useEffect, useRef, useState } from 'react';

/**
 * Invisible div that triggers onLoadMore when scrolled into view.
 *
 * The observer stays inactive until the user has scrolled at least once.
 * That prevents the sentinel from firing on initial render when the page
 * is short enough that the bottom is already within the viewport.
 */
export function InfiniteScrollSentinel({ onLoadMore, hasMore, isLoading }) {
  const ref = useRef(null);
  const [hasUserScrolled, setHasUserScrolled] = useState(false);

  useEffect(() => {
    if (hasUserScrolled) return;
    const onScroll = () => setHasUserScrolled(true);
    window.addEventListener('scroll', onScroll, { passive: true, once: true });
    return () => window.removeEventListener('scroll', onScroll);
  }, [hasUserScrolled]);

  useEffect(() => {
    const node = ref.current;
    if (!node || !hasMore || isLoading || !hasUserScrolled) return;

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          onLoadMore();
        }
      },
      { rootMargin: '0px' },
    );
    observer.observe(node);
    return () => observer.disconnect();
  }, [onLoadMore, hasMore, isLoading, hasUserScrolled]);

  return <div ref={ref} className="h-px w-full" aria-hidden="true" />;
}
