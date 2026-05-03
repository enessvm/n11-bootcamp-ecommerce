import { createContext, useCallback, useEffect, useRef, useState } from 'react';
import { tokenStorage } from './tokenStorage';
import * as authApi from '@/api/auth';

export const AuthContext = createContext(null);

const initialState = {
  user: null,
  isAuthenticated: false,
  isLoading: true,
};

export function AuthProvider({ children }) {
  const [state, setState] = useState(initialState);
  // Bootstrap must run exactly once per page load. React 18 StrictMode
  // double-invokes effects in dev; without this guard the second run
  // would send the same refresh token, Keycloak would reject it as
  // already-used (refreshTokenMaxReuse=0), and the user would be
  // logged out on every page refresh.
  const bootstrapStartedRef = useRef(false);

  useEffect(() => {
    if (bootstrapStartedRef.current) return;
    bootstrapStartedRef.current = true;

    const stored = tokenStorage.read();
    if (!stored.refreshToken) {
      setState({ user: null, isAuthenticated: false, isLoading: false });
      return;
    }
    authApi
      .refresh(stored.refreshToken)
      .then((res) => {
        tokenStorage.write({
          accessToken: res.accessToken,
          refreshToken: res.refreshToken,
          profile: stored.profile,
        });
        setState({
          user: stored.profile,
          isAuthenticated: true,
          isLoading: false,
        });
      })
      .catch(() => {
        tokenStorage.clear();
        setState({ user: null, isAuthenticated: false, isLoading: false });
      });
  }, []);

  const login = useCallback(async (email, password) => {
    const res = await authApi.login({ email, password });
    tokenStorage.write({
      accessToken: res.accessToken,
      refreshToken: res.refreshToken,
      profile: res.profile,
    });
    setState({ user: res.profile, isAuthenticated: true, isLoading: false });
  }, []);

  const register = useCallback(async (payload) => {
    const res = await authApi.register(payload);
    tokenStorage.write({
      accessToken: res.accessToken,
      refreshToken: res.refreshToken,
      profile: res.profile,
    });
    setState({ user: res.profile, isAuthenticated: true, isLoading: false });
  }, []);

  const logout = useCallback(async () => {
    const stored = tokenStorage.read();
    if (stored.refreshToken) {
      try {
        await authApi.logout(stored.refreshToken);
      } catch {
        /* best-effort */
      }
    }
    tokenStorage.clear();
    setState({ user: null, isAuthenticated: false, isLoading: false });
  }, []);

  const setUser = useCallback((user) => {
    tokenStorage.write({ profile: user });
    setState((prev) => ({ ...prev, user }));
  }, []);

  return (
    <AuthContext.Provider
      value={{ ...state, login, register, logout, setUser }}
    >
      {children}
    </AuthContext.Provider>
  );
}
