import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Mail, CheckCircle, ArrowLeft } from 'lucide-react';
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

const ForgotPasswordPage = () => {
  const { forgotPassword } = useAuth();
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [sent, setSent] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email.trim()) {
      setError('Email is required');
      return;
    }
    setLoading(true);
    const result = await forgotPassword(email);
    setLoading(false);
    if (result.success) {
      setSent(true);
    }
  };

  return (
    <div className="auth-page" id="forgot-password-page">
      <div className="auth-container">
        <div className="auth-card">
          {!sent ? (
            <>
              <div className="auth-header">
                <div className="auth-logo" style={{ background: 'transparent', boxShadow: 'none', border: 'none' }}>
                  <PremiumLogo size={36} />
                </div>
                <h1 className="auth-title" style={{ marginTop: '12px' }}>Forgot Password?</h1>
                <p className="auth-subtitle">
                  No worries, we&apos;ll send you a reset link
                </p>
              </div>

              <form className="auth-form" onSubmit={handleSubmit} id="forgot-password-form">
                <div className="form-group">
                  <label className="form-label" htmlFor="forgot-email">Email Address</label>
                  <div className="input-with-icon">
                    <Mail size={16} className="input-icon" />
                    <input
                      id="forgot-email"
                      type="email"
                      className={`form-input ${error ? 'error' : ''}`}
                      placeholder="Enter your email address"
                      value={email}
                      onChange={(e) => { setEmail(e.target.value); setError(''); }}
                      autoComplete="email"
                    />
                  </div>
                  {error && <span className="form-error">{error}</span>}
                </div>

                <button
                  type="submit"
                  className="btn btn-primary auth-btn"
                  disabled={loading}
                  id="forgot-submit-btn"
                >
                  {loading ? <div className="spinner" /> : 'Send Reset Link'}
                </button>
              </form>
            </>
          ) : (
            <div className="auth-success">
              <div className="auth-success-icon">
                <CheckCircle size={28} />
              </div>
              <h2 className="auth-title" style={{ fontSize: '20px' }}>Check Your Email</h2>
              <p className="auth-subtitle" style={{ marginTop: '8px', marginBottom: '20px' }}>
                We&apos;ve sent a password reset link to <strong>{email}</strong>
              </p>
              <button
                className="btn btn-secondary"
                onClick={() => { setSent(false); setEmail(''); }}
                id="resend-btn"
              >
                Didn&apos;t receive it? Resend
              </button>
            </div>
          )}

          <div className="auth-footer">
            <Link to="/auth/login" id="back-to-login-link" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}>
              <ArrowLeft size={14} />
              Back to Sign In
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ForgotPasswordPage;
