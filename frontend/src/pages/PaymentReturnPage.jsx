import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const STORAGE_KEY = 'ecom.checkout.lastOrderId';

/**
 * Where the Iyzico backend callback sends the user's browser after payment.
 *
 * In the popup checkout flow this page lives in the popup window. It just
 * closes itself; the main tab is on /checkout polling the saga and will
 * navigate to /orders/:id once the saga reaches a terminal state.
 *
 * Fallback (no opener — popup blocked or user landed here directly):
 * read sessionStorage for the order id and navigate the same tab to it.
 */
export function PaymentReturnPage() {
  const navigate = useNavigate();

  useEffect(() => {
    if (window.opener && !window.opener.closed) {
      window.close();
      return;
    }
    const orderId = sessionStorage.getItem(STORAGE_KEY);
    if (orderId) {
      sessionStorage.removeItem(STORAGE_KEY);
      navigate(`/orders/${orderId}`, { replace: true });
    } else {
      navigate('/', { replace: true });
    }
  }, [navigate]);

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
