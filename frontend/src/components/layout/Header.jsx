import { Link, useLocation, useNavigate } from 'react-router-dom';
import { LogOut, ShoppingBag, User as UserIcon } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { CartButton } from '@/components/cart/CartButton';
import { useAuth } from '@/auth/useAuth';

export function Header() {
  const { user, isAuthenticated, isLoading, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const handleSignOut = async () => {
    await logout();
    navigate('/');
  };

  const displayName = user?.displayName ?? 'Account';
  const initials = displayName
    .split(/\s+/)
    .map((part) => part[0])
    .filter(Boolean)
    .slice(0, 2)
    .join('')
    .toUpperCase();

  const signInHref = `/login?returnTo=${encodeURIComponent(location.pathname + location.search)}`;

  return (
    <header className="sticky top-0 z-40 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/80">
      <div className="container flex h-16 items-center gap-6">
        <Link
          to="/"
          className="flex items-center gap-2 transition-opacity hover:opacity-80"
          aria-label="ECOM home"
        >
          <ShoppingBag className="h-6 w-6 text-primary" />
          <span className="text-xl font-extrabold tracking-tight text-primary">
            ECOM
          </span>
        </Link>

        <div className="ml-auto flex items-center gap-2">
          <CartButton />

          {isLoading ? (
            <span className="text-xs text-muted-foreground">Loading...</span>
          ) : isAuthenticated ? (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-10 w-10 rounded-full"
                  aria-label="Open account menu"
                >
                  <Avatar>
                    <AvatarFallback>{initials || 'U'}</AvatarFallback>
                  </Avatar>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-56">
                <DropdownMenuLabel className="flex flex-col">
                  <span className="text-sm font-medium">{displayName}</span>
                  {user?.phoneNumber && (
                    <span className="text-xs font-normal text-muted-foreground">
                      {user.phoneNumber}
                    </span>
                  )}
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem asChild>
                  <Link to="/account">
                    <UserIcon />
                    My account
                  </Link>
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem onSelect={handleSignOut}>
                  <LogOut />
                  Sign out
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          ) : (
            <>
              <Button asChild variant="ghost">
                <Link to={signInHref}>Sign in</Link>
              </Button>
              <Button asChild>
                <Link to="/register">Sign up</Link>
              </Button>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
