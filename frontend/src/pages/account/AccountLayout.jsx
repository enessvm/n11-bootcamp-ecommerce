import { NavLink, Outlet } from 'react-router-dom';
import { MapPin, Package, User } from 'lucide-react';
import { usePageTitle } from '@/hooks/usePageTitle';
import { cn } from '@/lib/utils';

const NAV_ITEMS = [
  { to: '/account/profile', label: 'Profile', icon: User },
  { to: '/account/addresses', label: 'Addresses', icon: MapPin },
  { to: '/account/orders', label: 'Orders', icon: Package },
];

export function AccountLayout() {
  usePageTitle('My account');
  return (
    <section className="container py-8">
      <h1 className="text-2xl font-bold tracking-tight">My account</h1>

      <div className="mt-6 grid gap-6 lg:grid-cols-[220px_minmax(0,1fr)]">
        <aside>
          <nav className="space-y-1" aria-label="Account">
            {NAV_ITEMS.map((item) => {
              const Icon = item.icon;
              return (
                <NavLink
                  key={item.to}
                  to={item.to}
                  className={({ isActive }) =>
                    cn(
                      'flex items-center gap-3 rounded-md px-3 py-2 text-sm transition-colors',
                      isActive
                        ? 'bg-primary/10 font-medium text-primary'
                        : 'text-foreground/80 hover:bg-accent hover:text-foreground',
                    )
                  }
                >
                  <Icon className="h-4 w-4" />
                  {item.label}
                </NavLink>
              );
            })}
          </nav>
        </aside>

        <div>
          <Outlet />
        </div>
      </div>
    </section>
  );
}
