import { useEffect } from 'react';
import { RouterProvider, useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { router } from './routes';
import { setOnSessionLost } from '@/api/client';
import { useAuth } from '@/auth/useAuth';
import { Toaster } from '@/components/ui/sonner';

export function App() {
  return (
    <>
      <RouterProvider router={router} />
      <Toaster />
    </>
  );
}

// Wired inside AppShell via SessionLostBridge below; App stays simple.
export function SessionLostBridge() {
  const navigate = useNavigate();
  const { logout } = useAuth();

  useEffect(() => {
    setOnSessionLost(async () => {
      await logout();
      toast.error('Your session has expired. Please sign in again.');
      navigate('/login', { replace: true });
    });
    return () => setOnSessionLost(null);
  }, [logout, navigate]);

  return null;
}
