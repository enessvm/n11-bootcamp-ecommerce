const ACCESS_KEY = 'ecom.auth.accessToken';
const REFRESH_KEY = 'ecom.auth.refreshToken';
const PROFILE_KEY = 'ecom.auth.profile';

export const tokenStorage = {
  read() {
    const profileRaw = localStorage.getItem(PROFILE_KEY);
    let profile = null;
    if (profileRaw) {
      try {
        profile = JSON.parse(profileRaw);
      } catch {
        profile = null;
      }
    }
    return {
      accessToken: localStorage.getItem(ACCESS_KEY),
      refreshToken: localStorage.getItem(REFRESH_KEY),
      profile,
    };
  },

  write({ accessToken, refreshToken, profile }) {
    if (accessToken !== undefined) {
      if (accessToken) localStorage.setItem(ACCESS_KEY, accessToken);
      else localStorage.removeItem(ACCESS_KEY);
    }
    if (refreshToken !== undefined) {
      if (refreshToken) localStorage.setItem(REFRESH_KEY, refreshToken);
      else localStorage.removeItem(REFRESH_KEY);
    }
    if (profile !== undefined) {
      if (profile) localStorage.setItem(PROFILE_KEY, JSON.stringify(profile));
      else localStorage.removeItem(PROFILE_KEY);
    }
  },

  clear() {
    localStorage.removeItem(ACCESS_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(PROFILE_KEY);
  },
};
