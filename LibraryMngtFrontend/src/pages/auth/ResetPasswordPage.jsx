import { useState } from 'react';
import { Link, useSearchParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Lock, Eye, EyeOff, CheckCircle } from 'lucide-react';
import './Auth.css';

// Stunning custom brand logo component representing the futuristic shield with book pages
const PremiumLogo = ({ size = 32 }) => (
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
    <polygon 
      points="50,12 88,30 88,68 50,88 12,68 12,30" 
      fill="none" 
      stroke="url(#logoGrad)" 
      strokeWidth="6" 
      strokeLinecap="round" 
      strokeLinejoin="round"
      filter="url(#glow)"
    />
    <path 
      d="M50,28 L50,68" 
      fill="none" 
      stroke="url(#logoGrad)" 
      strokeWidth="5" 
      strokeLinecap="round" 
    />
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
    <circle cx="50" cy="18" r="4" fill="#ffffff" />
  </svg>
);

const ResetPasswordPage = () => {
  const { resetPassword } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token') || '';

  const [formData, setFormData] = useState({ newPassword: '', confirmPassword: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const errs = {};
    if (!token) errs.token = 'Invalid or missing reset token';
    if (!formData.newPassword) errs.newPassword = 'Password is required';
    else if (formData.newPassword.length < 6) errs.newPassword = 'Password must be at least 6 characters';
    if (formData.newPassword !== formData.confirmPassword) errs.confirmPassword = 'Passwords do not match';
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    const result = await resetPassword(token, formData.newPassword);
    setLoading(false);
    if (result.success) {
      setSuccess(true);
      setTimeout(() => navigate('/auth/login'), 3000);
    }
  };

  return (
    <div className="auth-page" id="reset-password-page">
      <div className="auth-container">
        <div className="auth-card">
          {!success ? (
            <>
              <div className="auth-header">
                <div className="auth-logo" style={{ background: 'transparent', boxShadow: 'none', border: 'none' }}>
                  <PremiumLogo size={36} />
                </div>
                <h1 className="auth-title" style={{ marginTop: '12px' }}>Reset Password</h1>
                <p className="auth-subtitle">Enter your new password below</p>
              </div>

              <form className="auth-form" onSubmit={handleSubmit} id="reset-password-form">
                {errors.token && (
                  <div style={{ padding: '12px', background: 'var(--color-error-bg)', border: '1px solid rgba(239,68,68,0.2)', borderRadius: 'var(--radius-md)', color: 'var(--color-error)', fontSize: '13px' }}>
                    {errors.token}
                  </div>
                )}

                <div className="form-group">
                  <label className="form-label" htmlFor="new-password">New Password</label>
                  <div className="input-with-icon">
                    <Lock size={16} className="input-icon" />
                    <input
                      id="new-password"
                      type={showPassword ? 'text' : 'password'}
                      className={`form-input ${errors.newPassword ? 'error' : ''}`}
                      placeholder="Enter new password"
                      value={formData.newPassword}
                      onChange={(e) => setFormData({ ...formData, newPassword: e.target.value })}
                      autoComplete="new-password"
                    />
                    <button
                      type="button"
                      className="toggle-password"
                      onClick={() => setShowPassword(!showPassword)}
                    >
                      {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                    </button>
                  </div>
                  {errors.newPassword && <span className="form-error">{errors.newPassword}</span>}
                </div>

                <div className="form-group">
                  <label className="form-label" htmlFor="confirm-new-password">Confirm Password</label>
                  <div className="input-with-icon">
                    <Lock size={16} className="input-icon" />
                    <input
                      id="confirm-new-password"
                      type="password"
                      className={`form-input ${errors.confirmPassword ? 'error' : ''}`}
                      placeholder="Confirm new password"
                      value={formData.confirmPassword}
                      onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                      autoComplete="new-password"
                    />
                  </div>
                  {errors.confirmPassword && <span className="form-error">{errors.confirmPassword}</span>}
                </div>

                <button
                  type="submit"
                  className="btn btn-primary auth-btn"
                  disabled={loading}
                  id="reset-submit-btn"
                >
                  {loading ? <div className="spinner" /> : 'Reset Password'}
                </button>
              </form>
            </>
          ) : (
            <div className="auth-success">
              <div className="auth-success-icon">
                <CheckCircle size={28} />
              </div>
              <h2 className="auth-title" style={{ fontSize: '20px' }}>Password Reset!</h2>
              <p className="auth-subtitle" style={{ marginTop: '8px' }}>
                Your password has been reset successfully. Redirecting to login...
              </p>
            </div>
          )}

          <div className="auth-footer">
            <Link to="/auth/login" id="back-to-login-reset">Back to Sign In</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ResetPasswordPage;
