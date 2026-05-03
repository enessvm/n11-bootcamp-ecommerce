import { useEffect } from 'react';

export function PaymentReturnPage() {
  useEffect(() => {
    try {
      const channel = new BroadcastChannel('payment-flow');
      channel.postMessage({ type: 'popup-returned' });
      channel.close();
    } catch {
      /* ignore */
    }
    try {
      window.close();
    } catch {
      /* ignore */
    }
  }, []);

  return (
    <div className="flex min-h-[60vh] items-center justify-center text-center">
      <div>
        <p className="text-sm text-muted-foreground">Payment received.</p>
        <p className="mt-1 text-xs text-muted-foreground">
          You can close this window and return to the original tab.
        </p>
      </div>
    </div>
  );
}