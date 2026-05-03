import { Navigate, createBrowserRouter } from 'react-router-dom';
import { AppShell } from '@/components/layout/AppShell';
import { ProtectedRoute } from '@/auth/ProtectedRoute';
import { HomePage } from '@/pages/HomePage';
import { LoginPage } from '@/pages/LoginPage';
import { RegisterPage } from '@/pages/RegisterPage';
import { CategoryListingPage } from '@/pages/CategoryListingPage';
import { ProductDetailPage } from '@/pages/ProductDetailPage';
import { CartPage } from '@/pages/CartPage';
import { CheckoutPage } from '@/pages/CheckoutPage';
import { PaymentReturnPage } from '@/pages/PaymentReturnPage';
import { OrderDetailPage } from '@/pages/OrderDetailPage';
import { AccountLayout } from '@/pages/account/AccountLayout';
import { AccountProfilePage } from '@/pages/account/AccountProfilePage';
import { AccountAddressesPage } from '@/pages/account/AccountAddressesPage';
import { AccountOrdersPage } from '@/pages/account/AccountOrdersPage';
import { NotFoundPage } from '@/pages/NotFoundPage';

export const router = createBrowserRouter([
  {
    element: <AppShell />,
    children: [
      { path: '/', element: <HomePage /> },
      { path: '/login', element: <LoginPage /> },
      { path: '/register', element: <RegisterPage /> },
      { path: '/c/:categorySlug', element: <CategoryListingPage /> },
      { path: '/p/:productId', element: <ProductDetailPage /> },
      { path: '/cart', element: <CartPage /> },
      { path: '/payments/return', element: <PaymentReturnPage /> },
      {
        path: '/checkout',
        element: (
          <ProtectedRoute>
            <CheckoutPage />
          </ProtectedRoute>
        ),
      },
      {
        path: '/orders/:orderId',
        element: (
          <ProtectedRoute>
            <OrderDetailPage />
          </ProtectedRoute>
        ),
      },
      {
        path: '/account',
        element: (
          <ProtectedRoute>
            <AccountLayout />
          </ProtectedRoute>
        ),
        children: [
          { index: true, element: <Navigate to="/account/profile" replace /> },
          { path: 'profile', element: <AccountProfilePage /> },
          { path: 'addresses', element: <AccountAddressesPage /> },
          { path: 'orders', element: <AccountOrdersPage /> },
        ],
      },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
]);
