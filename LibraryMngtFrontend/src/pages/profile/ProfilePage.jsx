import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { userAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { User, Shield, Calendar, Mail, CheckCircle } from 'lucide-react';
import './Profile.css';

const ProfilePage = () => {
  const { user, logout } = useAuth();
  const [profileData, setProfileData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await userAPI.getProfile();
        setProfileData(res.data);
      } catch (err) {
        toast.error('Failed to load profile details');
        // Fallback to context user
        setProfileData(user);
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, [user]);

  if (loading) {
    return (
      <div className="profile-page animate-fadeIn">
        <div className="skeleton" style={{ height: '400px', borderRadius: '24px' }} />
      </div>
    );
  }

  const getInitials = (name) => {
    if (!name) return 'U';
    return name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
  };

  const displayUser = profileData || user;

  return (
    <div className="profile-page animate-fadeIn" id="profile-page">
      <div className="page-header">
        <h1 className="page-title">My Profile</h1>
        <p className="page-subtitle">Manage your personal information and account settings</p>
      </div>

      <div className="profile-card">
        <div className="profile-header">
          <div className="profile-avatar">
            {getInitials(displayUser?.name)}
          </div>
          <h2 className="profile-name">{displayUser?.name || 'Library User'}</h2>
          <p className="profile-email">{displayUser?.email}</p>
          <div className="profile-role-badge">
            {displayUser?.role === 'ADMIN' ? <><Shield size={12} style={{ display: 'inline', marginRight: '4px' }} /> Administrator</> : 'Member'}
          </div>
        </div>

        <div className="profile-body">
          <h3 className="profile-section-title">
            <User size={18} /> Account Details
          </h3>
          
          <div className="profile-info-grid">
            <div className="info-item">
              <span className="info-label">Full Name</span>
              <span className="info-value">{displayUser?.name || 'Not provided'}</span>
            </div>
            
            <div className="info-item">
              <span className="info-label">Email Address</span>
              <span className="info-value" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Mail size={14} className="text-muted" /> 
                {displayUser?.email}
              </span>
            </div>

            <div className="info-item">
              <span className="info-label">Account Status</span>
              <span className="info-value" style={{ color: 'var(--color-success)', display: 'flex', alignItems: 'center', gap: '4px' }}>
                <CheckCircle size={14} /> Active
              </span>
            </div>

            <div className="info-item">
              <span className="info-label">Member Since</span>
              <span className="info-value" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                <Calendar size={14} className="text-muted" />
                {displayUser?.createdAt ? new Date(displayUser.createdAt).toLocaleDateString('en-US', {
                  year: 'numeric', month: 'long', day: 'numeric'
                }) : 'Unknown'}
              </span>
            </div>
          </div>

          <div style={{ marginTop: '40px', paddingTop: '24px', borderTop: '1px solid var(--border-color)', display: 'flex', justifyContent: 'center' }}>
            <button 
              className="btn btn-secondary" 
              onClick={logout}
              style={{ color: 'var(--color-error)', borderColor: 'rgba(239, 68, 68, 0.3)' }}
              id="profile-logout-btn"
            >
              Sign Out of Account
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
