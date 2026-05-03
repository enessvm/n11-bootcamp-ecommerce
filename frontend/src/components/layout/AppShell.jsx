import { Outlet } from 'react-router-dom';
import { Header } from './Header';
import { Footer } from './Footer';
import { CategoryRail } from '@/components/category/CategoryRail';
import { SessionLostBridge } from '@/App';

export function AppShell() {
  return (
    <div className="flex min-h-screen flex-col bg-background">
      <SessionLostBridge />
      <Header />
      <CategoryRail />
      <main className="flex-1">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}
