import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Mail, Lock, Eye, EyeOff } from 'lucide-react';
import './Auth.css';

// Stunning custom brand logo component representing the futuristic shield with book pages
const PremiumLogo = ({ size = 40 }) => (
  <svg viewBox="0 0 100 100" style={{ width: size, height: size }} className="brand-svg">
    <defs>
      <linearGradient id="logoGrad" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stopColor="var(--accent-primary)" />
        <stop offset="50%" stopColor="var(--accent-secondary)" />
        <stop offset="100%" stopColor="#a78bfa" />
      </linearGradient>
      <filter id="glow" x="-20%" y="-20%" width="140%" height="140%">
        <feGaussianBlur stdDeviation="4" result="blur" />
        <feComposite in="SourceGraphic" in2="blur" operator="over" />
      </filter>
    </defs>
    {/* Outer glowing sci-fi shield */}
    <polygon 
      points="50,12 88,30 88,68 50,88 12,68 12,30" 
      fill="none" 
      stroke="url(#logoGrad)" 
      strokeWidth="6" 
      strokeLinecap="round" 
      strokeLinejoin="round"
      filter="url(#glow)"
    />
    {/* Middle spine */}
    <path 
      d="M50,28 L50,68" 
      fill="none" 
      stroke="url(#logoGrad)" 
      strokeWidth="5" 
      strokeLinecap="round" 
    />
    {/* Stylized geometric pages */}
    <path 
      d="M50,42 C56,34 76,34 76,55" 
      fill="none" 
      stroke="url(#logoGrad)" 
      strokeWidth="5" 
      strokeLinecap="round" 
    />
    <path 
      d="M50,42 C44,34 24,34 24,55" 
      fill="none" 
      stroke="url(#logoGrad)" 
      strokeWidth="5" 
      strokeLinecap="round" 
    />
    <path 
      d="M50,52 C56,44 76,44 76,65" 
      fill="none" 
      stroke="url(#logoGrad)" 
      strokeWidth="5" 
      strokeLinecap="round" 
    />
    <path 
      d="M50,52 C44,44 24,44 24,65" 
      fill="none" 
      stroke="url(#logoGrad)" 
      strokeWidth="5" 
      strokeLinecap="round" 
    />
    {/* Radiant star dot */}
    <circle cx="50" cy="18" r="4" fill="#ffffff" />
  </svg>
);

const LoginPage = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const errs = {};
    if (!formData.username.trim()) errs.username = 'Email or username is required';
    if (!formData.password) errs.password = 'Password is required';
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    const result = await login(formData.username, formData.password);
    setLoading(false);
    if (result.success) {
      const isAdmin = result.user?.roles?.includes('ROLE_ADMIN');
      navigate(isAdmin ? '/admin/dashboard' : '/dashboard');
    }
  };

  const handleGoogleLogin = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  return (
    <div className="auth-page" id="login-page">
      <div className="auth-split-container">
        
        {/* Left Side: Stunning Immersive Panel (hidden on mobile) */}
        <div className="auth-visual-panel">
          <div className="visual-background-effects">
            <div className="floating-glow glow-1"></div>
            <div className="floating-glow glow-2"></div>
            <div className="floating-glow glow-3"></div>
            <div className="geometric-grid"></div>
          </div>
          
          <div className="visual-content">
            <div className="brand-logo-container">
              <div className="brand-glowing-logo">
                <PremiumLogo size={40} />
              </div>
              <span className="brand-name">BIBLIOTECH</span>
            </div>
            
            <div className="visual-pitch">
              <h2 className="pitch-title">Discover the Future of Reading</h2>
              <p className="pitch-subtitle">
                Access a beautifully crafted library universe. Borrow premium books, manage active loans, and experience seamless reading tailored to modern standards.
              </p>
            </div>
            
            <div className="visual-stats-card">
              <div className="stat-item">
                <span className="stat-value">50K+</span>
                <span className="stat-label">Library Books</span>
              </div>
              <div className="stat-item">
                <span className="stat-value">99.9%</span>
                <span className="stat-label">System Uptime</span>
              </div>
              <div className="stat-item">
                <span className="stat-value">12K+</span>
                <span className="stat-label">Avid Readers</span>
              </div>
            </div>
          </div>
        </div>

        {/* Right Side: Elegant Login Form Card */}
        <div className="auth-form-panel">
          <div className="auth-form-card">
            
            {/* Mobile-only logo header */}
            <div className="mobile-brand-header">
              <div className="brand-glowing-logo mobile-logo">
                <PremiumLogo size={32} />
              </div>
              <span className="mobile-brand-name">BIBLIOTECH</span>
            </div>

            <div className="auth-form-header">
              <h1 className="auth-title">Welcome Back</h1>
              <p className="auth-subtitle">Sign in to your library account</p>
            </div>

            <form className="auth-form" onSubmit={handleSubmit} id="login-form">
              <div className="form-group">
                <label className="form-label" htmlFor="login-email">Email or Username</label>
                <div className="input-with-icon">
                  <Mail size={16} className="input-icon" />
                  <input
                    id="login-email"
                    type="text"
                    className={`form-input ${errors.username ? 'error' : ''}`}
                    placeholder="Enter your email or username"
                    value={formData.username}
                    onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                    autoComplete="username"
                  />
                </div>
                {errors.username && <span className="form-error">{errors.username}</span>}
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="login-password">Password</label>
                <div className="input-with-icon">
                  <Lock size={16} className="input-icon" />
                  <input
                    id="login-password"
                    type={showPassword ? 'text' : 'password'}
                    className={`form-input ${errors.password ? 'error' : ''}`}
                    placeholder="Enter your password"
                    value={formData.password}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    autoComplete="current-password"
                  />
                  <button
                    type="button"
                    className="toggle-password"
                    onClick={() => setShowPassword(!showPassword)}
                    id="toggle-password"
                    title={showPassword ? "Hide password" : "Show password"}
                  >
                    {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
                {errors.password && <span className="form-error">{errors.password}</span>}
              </div>

              <div className="auth-links">
                <span></span>
                <Link to="/auth/forgot-password" id="forgot-password-link">Forgot password?</Link>
              </div>

              <button
                type="submit"
                className="btn btn-primary auth-btn"
                disabled={loading}
                id="login-submit-btn"
              >
                {loading ? <div className="spinner" /> : 'Sign In'}
              </button>

              <div className="auth-divider">
                <span>or continue with</span>
              </div>

              <button
                type="button"
                className="google-btn"
                onClick={handleGoogleLogin}
                id="google-login-btn"
              >
                <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" alt="Google" />
                Sign in with Google
              </button>
            </form>

            <div className="auth-footer">
              Don&apos;t have an account?{' '}
              <Link to="/auth/signup" id="signup-link">Create one</Link>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
};

export default LoginPage;
