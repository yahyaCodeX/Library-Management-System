import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// ─── ProtectedRoute — Requires authentication ─────────────────────────────
export const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <div className="loading-page">
        <div className="spinner spinner-lg"></div>
        <p style={{ color: 'var(--text-secondary)' }}>Loading...</p>
      </div>
    );
  }

  if (!isAuthenticated()) {
    return <Navigate to="/auth/login" state={{ from: location }} replace />;
  }

  return children;
};

// ─── AdminRoute — Requires ADMIN role ─────────────────────────────────────
export const AdminRoute = ({ children }) => {
  const { isAuthenticated, isAdmin, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <div className="loading-page">
        <div className="spinner spinner-lg"></div>
      </div>
    );
  }

  if (!isAuthenticated()) {
    return <Navigate to="/auth/login" state={{ from: location }} replace />;
  }

  if (!isAdmin()) {
    return <Navigate to="/" replace />;
  }

  return children;
};

// ─── GuestRoute — Only for non-authenticated users ────────────────────────
export const GuestRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="loading-page">
        <div className="spinner spinner-lg"></div>
      </div>
    );
  }

  if (isAuthenticated()) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};
