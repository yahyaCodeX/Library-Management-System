import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute, GuestRoute } from './components/ProtectedRoute';
import Layout from './components/layout/Layout';

// Auth Pages
import LoginPage from './pages/auth/LoginPage';
import SignupPage from './pages/auth/SignupPage';
import ForgotPasswordPage from './pages/auth/ForgotPasswordPage';
import ResetPasswordPage from './pages/auth/ResetPasswordPage';

// Main Pages
import DashboardPage from './pages/dashboard/DashboardPage';

// Book Pages
import BookCatalogPage from './pages/books/BookCatalogPage';
import BookDetailPage from './pages/books/BookDetailPage';

// Library Pages
import MyLoansPage from './pages/loans/MyLoansPage';
import MyReservationsPage from './pages/reservations/MyReservationsPage';
import MyWishlistPage from './pages/wishlist/MyWishlistPage';
import MyFinesPage from './pages/fines/MyFinesPage';

// Subscription & Payment Pages
import SubscriptionsPage from './pages/subscriptions/SubscriptionsPage';
import PaymentSuccessPage from './pages/payment/PaymentSuccessPage';
import PaymentCancelPage from './pages/payment/PaymentCancelPage';

// User Settings
import ProfilePage from './pages/profile/ProfilePage';

// Admin Pages
import AdminDashboardPage from './pages/admin/AdminDashboardPage';
import AdminBooksPage from './pages/admin/AdminBooksPage';
import AdminUsersPage from './pages/admin/AdminUsersPage';
import AdminLoansPage from './pages/admin/AdminLoansPage';
import AdminFinesPage from './pages/admin/AdminFinesPage';
import AdminPlansPage from './pages/admin/AdminPlansPage';
import AdminGenresPage from './pages/admin/AdminGenresPage';
import AdminReservationsPage from './pages/admin/AdminReservationsPage';
import AdminPaymentsPage from './pages/admin/AdminPaymentsPage';
import AdminSubscriptionsPage from './pages/admin/AdminSubscriptionsPage';

function App() {
  return (
    <Router>
      <AuthProvider>
        <Toaster
          position="top-right"
          toastOptions={{
            duration: 4000,
            style: {
              background: '#1a1a24',
              color: '#f1f1f4',
              border: '1px solid rgba(255,255,255,0.06)',
              borderRadius: '12px',
              fontSize: '14px',
              padding: '12px 16px',
              boxShadow: '0 10px 25px rgba(0,0,0,0.5)',
            },
            success: {
              iconTheme: { primary: '#10b981', secondary: '#fff' },
            },
            error: {
              iconTheme: { primary: '#ef4444', secondary: '#fff' },
            },
          }}
        />

        <Routes>
          {/* Auth Routes (Guest Only) */}
          <Route path="/auth/login" element={<GuestRoute><LoginPage /></GuestRoute>} />
          <Route path="/auth/signup" element={<GuestRoute><SignupPage /></GuestRoute>} />
          <Route path="/auth/forgot-password" element={<GuestRoute><ForgotPasswordPage /></GuestRoute>} />
          <Route path="/auth/reset-password" element={<GuestRoute><ResetPasswordPage /></GuestRoute>} />

          {/* Protected Routes (Authenticated) */}
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <Layout><DashboardPage /></Layout>
            </ProtectedRoute>
          } />

          {/* Book Routes */}
          <Route path="/books" element={
            <ProtectedRoute><Layout><BookCatalogPage /></Layout></ProtectedRoute>
          } />
          <Route path="/books/:id" element={
            <ProtectedRoute><Layout><BookDetailPage /></Layout></ProtectedRoute>
          } />
          <Route path="/my-loans" element={
            <ProtectedRoute><Layout><MyLoansPage /></Layout></ProtectedRoute>
          } />
          <Route path="/my-reservations" element={
            <ProtectedRoute><Layout><MyReservationsPage /></Layout></ProtectedRoute>
          } />
          <Route path="/my-wishlist" element={
            <ProtectedRoute><Layout><MyWishlistPage /></Layout></ProtectedRoute>
          } />
          <Route path="/my-fines" element={
            <ProtectedRoute><Layout><MyFinesPage /></Layout></ProtectedRoute>
          } />
          <Route path="/subscriptions" element={
            <ProtectedRoute><Layout><SubscriptionsPage /></Layout></ProtectedRoute>
          } />
          <Route path="/profile" element={
            <ProtectedRoute><Layout><ProfilePage /></Layout></ProtectedRoute>
          } />

          {/* Admin Routes */}
          <Route path="/admin/dashboard" element={
            <ProtectedRoute><Layout><AdminDashboardPage /></Layout></ProtectedRoute>
          } />
          <Route path="/admin/books" element={
            <ProtectedRoute><Layout><AdminBooksPage /></Layout></ProtectedRoute>
          } />
          <Route path="/admin/genres" element={
            <ProtectedRoute><Layout><AdminGenresPage /></Layout></ProtectedRoute>
          } />
          <Route path="/admin/loans" element={
            <ProtectedRoute><Layout><AdminLoansPage /></Layout></ProtectedRoute>
          } />
          <Route path="/admin/reservations" element={
            <ProtectedRoute><Layout><AdminReservationsPage /></Layout></ProtectedRoute>
          } />
          <Route path="/admin/fines" element={
            <ProtectedRoute><Layout><AdminFinesPage /></Layout></ProtectedRoute>
          } />
          <Route path="/admin/payments" element={
            <ProtectedRoute><Layout><AdminPaymentsPage /></Layout></ProtectedRoute>
          } />
          <Route path="/admin/subscriptions" element={
            <ProtectedRoute><Layout><AdminSubscriptionsPage /></Layout></ProtectedRoute>
          } />
          <Route path="/admin/plans" element={
            <ProtectedRoute><Layout><AdminPlansPage /></Layout></ProtectedRoute>
          } />
          <Route path="/admin/users" element={
            <ProtectedRoute><Layout><AdminUsersPage /></Layout></ProtectedRoute>
          } />

          {/* Payment callbacks */}
          <Route path="/payment/success" element={
            <ProtectedRoute><Layout><PaymentSuccessPage /></Layout></ProtectedRoute>
          } />
          <Route path="/payment/cancel" element={
            <ProtectedRoute><Layout><PaymentCancelPage /></Layout></ProtectedRoute>
          } />

          {/* Default Redirect */}
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

// Temporary placeholder for pages not yet built
function PlaceholderPage({ title, description }) {
  return (
    <div className="animate-fadeIn" style={{ padding: '40px 0' }}>
      <div className="page-header">
        <h1 className="page-title">{title}</h1>
        <p className="page-subtitle">{description}</p>
      </div>
      <div className="card" style={{ textAlign: 'center', padding: '80px 24px' }}>
        <p style={{ fontSize: '48px', marginBottom: '16px' }}>🚧</p>
        <h2 style={{ color: 'var(--text-primary)', marginBottom: '8px' }}>Under Construction</h2>
        <p style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>
          This page will be implemented in the next step.
        </p>
      </div>
    </div>
  );
}

export default App;
