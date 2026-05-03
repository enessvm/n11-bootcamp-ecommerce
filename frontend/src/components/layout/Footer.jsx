import { Link } from 'react-router-dom';
import {
  Facebook,
  Instagram,
  Mail,
  ShoppingBag,
  Twitter,
  Youtube,
} from 'lucide-react';

const SHOP_LINKS = [
  { label: 'All products', to: '/' },
  { label: 'Electronics', to: '/c/electronics' },
  { label: 'Home & Kitchen', to: '/c/home-kitchen' },
  { label: 'Fashion', to: '/c/fashion' },
  { label: 'Sports', to: '/c/sports' },
];

const ACCOUNT_LINKS = [
  { label: 'My account', to: '/account/profile' },
  { label: 'My addresses', to: '/account/addresses' },
  { label: 'My orders', to: '/account/orders' },
  { label: 'Cart', to: '/cart' },
];

const HELP_LINKS = [
  { label: 'Shipping & delivery', to: '#' },
  { label: 'Returns & refunds', to: '#' },
  { label: 'FAQ', to: '#' },
  { label: 'Contact us', to: '#' },
];

const SOCIAL_LINKS = [
  { label: 'Instagram', icon: Instagram, href: '#' },
  { label: 'Twitter', icon: Twitter, href: '#' },
  { label: 'Facebook', icon: Facebook, href: '#' },
  { label: 'YouTube', icon: Youtube, href: '#' },
];

export function Footer() {
  return (
    <footer className="mt-16 border-t bg-muted/30">
      <div className="container py-12">
        <div className="grid gap-10 md:grid-cols-2 lg:grid-cols-4">
          {/* Brand column */}
          <div>
            <Link to="/" className="inline-flex items-center gap-2">
              <ShoppingBag className="h-5 w-5 text-primary" />
              <span className="text-lg font-extrabold tracking-tight text-primary">
                ECOM
              </span>
            </Link>
            <p className="mt-3 max-w-xs text-sm text-muted-foreground">
              A modern storefront built on Spring microservices, Keycloak, and
              React. Discover great products at fair prices.
            </p>
            <div className="mt-4 flex items-center gap-3">
              {SOCIAL_LINKS.map((s) => {
                const Icon = s.icon;
                return (
                  <a
                    key={s.label}
                    href={s.href}
                    aria-label={s.label}
                    className="text-muted-foreground transition-colors hover:text-primary"
                  >
                    <Icon className="h-5 w-5" />
                  </a>
                );
              })}
            </div>
          </div>

          <FooterColumn title="Shop" items={SHOP_LINKS} />
          <FooterColumn title="Account" items={ACCOUNT_LINKS} />
          <FooterColumn title="Help" items={HELP_LINKS} />
        </div>
      </div>

      <div className="border-t bg-background">
        <div className="container flex flex-col items-center justify-between gap-3 py-4 text-xs text-muted-foreground sm:flex-row">
          <p>© {new Date().getFullYear()} ECOM. All rights reserved.</p>
          <div className="flex items-center gap-4">
            <a href="#" className="hover:text-foreground">Privacy</a>
            <a href="#" className="hover:text-foreground">Terms</a>
            <a href="mailto:hello@example.com" className="inline-flex items-center gap-1 hover:text-foreground">
              <Mail className="h-3.5 w-3.5" />
              hello@example.com
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
}

function FooterColumn({ title, items }) {
  return (
    <div>
      <h3 className="text-sm font-semibold text-foreground">{title}</h3>
      <ul className="mt-3 space-y-2">
        {items.map((item) => (
          <li key={item.label}>
            <Link
              to={item.to}
              className="text-sm text-muted-foreground transition-colors hover:text-primary"
            >
              {item.label}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
