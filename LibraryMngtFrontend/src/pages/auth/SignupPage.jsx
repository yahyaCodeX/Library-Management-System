import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Mail, Lock, Eye, EyeOff, User, Phone } from 'lucide-react';
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
    <circle cx="50" cy="18" r="4" fill="#ffffff" />
  </svg>
);

const SignupPage = () => {
  const { signup } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
    phone: '',
  });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const errs = {};
    if (!formData.fullName.trim()) errs.fullName = 'Full name is required';
    if (!formData.email.trim()) errs.email = 'Email is required';
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) errs.email = 'Invalid email format';
    if (!formData.password) errs.password = 'Password is required';
    else if (formData.password.length < 6) errs.password = 'Password must be at least 6 characters';
    if (formData.password !== formData.confirmPassword) errs.confirmPassword = 'Passwords do not match';
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;
    setLoading(true);
    const result = await signup({
      fullName: formData.fullName,
      email: formData.email,
      password: formData.password,
      phone: formData.phone || undefined,
    });
    setLoading(false);
    if (result.success) {
      navigate('/dashboard');
    }
  };

  const handleChange = (field) => (e) => {
    setFormData({ ...formData, [field]: e.target.value });
    if (errors[field]) setErrors({ ...errors, [field]: undefined });
  };

  return (
    <div className="auth-page" id="signup-page">
      <div className="auth-split-container">
        
        {/* Left Side: Immersive Visual Panel (Desktop only) */}
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
              <h2 className="pitch-title">Join a Community of Thinkers</h2>
              <p className="pitch-subtitle">
                Unlock instant access to a vast network of publications, reserve books effortlessly, and personalize your digital library bookshelf in seconds.
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

        {/* Right Side: Signup Form Panel */}
        <div className="auth-form-panel">
          <div className="auth-form-card" style={{ maxWidth: '440px' }}>
            
            {/* Mobile logo header */}
            <div className="mobile-brand-header">
              <div className="brand-glowing-logo mobile-logo">
                <PremiumLogo size={32} />
              </div>
              <span className="mobile-brand-name">BIBLIOTECH</span>
            </div>

            <div className="auth-form-header">
              <h1 className="auth-title">Create Account</h1>
              <p className="auth-subtitle">Join the library community today</p>
            </div>

            <form className="auth-form" onSubmit={handleSubmit} id="signup-form">
              <div className="form-group">
                <label className="form-label" htmlFor="signup-name">Full Name</label>
                <div className="input-with-icon">
                  <User size={16} className="input-icon" />
                  <input
                    id="signup-name"
                    type="text"
                    className={`form-input ${errors.fullName ? 'error' : ''}`}
                    placeholder="Enter your full name"
                    value={formData.fullName}
                    onChange={handleChange('fullName')}
                    autoComplete="name"
                  />
                </div>
                {errors.fullName && <span className="form-error">{errors.fullName}</span>}
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="signup-email">Email Address</label>
                <div className="input-with-icon">
                  <Mail size={16} className="input-icon" />
                  <input
                    id="signup-email"
                    type="email"
                    className={`form-input ${errors.email ? 'error' : ''}`}
                    placeholder="Enter your email"
                    value={formData.email}
                    onChange={handleChange('email')}
                    autoComplete="email"
                  />
                </div>
                {errors.email && <span className="form-error">{errors.email}</span>}
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="signup-phone">Phone (Optional)</label>
                <div className="input-with-icon">
                  <Phone size={16} className="input-icon" />
                  <input
                    id="signup-phone"
                    type="tel"
                    className="form-input"
                    placeholder="Enter your phone number"
                    value={formData.phone}
                    onChange={handleChange('phone')}
                    autoComplete="tel"
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="signup-password">Password</label>
                <div className="input-with-icon">
                  <Lock size={16} className="input-icon" />
                  <input
                    id="signup-password"
                    type={showPassword ? 'text' : 'password'}
                    className={`form-input ${errors.password ? 'error' : ''}`}
                    placeholder="Create a password (min 6 chars)"
                    value={formData.password}
                    onChange={handleChange('password')}
                    autoComplete="new-password"
                  />
                  <button
                    type="button"
                    className="toggle-password"
                    onClick={() => setShowPassword(!showPassword)}
                    title={showPassword ? "Hide password" : "Show password"}
                  >
                    {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
                {errors.password && <span className="form-error">{errors.password}</span>}
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="signup-confirm-password">Confirm Password</label>
                <div className="input-with-icon">
                  <Lock size={16} className="input-icon" />
                  <input
                    id="signup-confirm-password"
                    type="password"
                    className={`form-input ${errors.confirmPassword ? 'error' : ''}`}
                    placeholder="Confirm your password"
                    value={formData.confirmPassword}
                    onChange={handleChange('confirmPassword')}
                    autoComplete="new-password"
                  />
                </div>
                {errors.confirmPassword && <span className="form-error">{errors.confirmPassword}</span>}
              </div>

              <button
                type="submit"
                className="btn btn-primary auth-btn"
                disabled={loading}
                id="signup-submit-btn"
              >
                {loading ? <div className="spinner" /> : 'Create Account'}
              </button>
            </form>

            <div className="auth-footer">
              Already have an account?{' '}
              <Link to="/auth/login" id="login-link-from-signup">Sign in</Link>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
};

export default SignupPage;
