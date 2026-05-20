import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { bookAPI, genreAPI, bookLoanAPI } from '../../services/api';
import {
  BookOpen, Users, BookMarked, TrendingUp,
  Clock, AlertTriangle, ArrowRight
} from 'lucide-react';
import { Link } from 'react-router-dom';
import './Dashboard.css';

const StatCard = ({ icon: Icon, label, value, color, loading }) => (
  <div className="stat-card card card-glow" style={{ '--stat-color': color }}>
    <div className="stat-icon" style={{ background: `${color}15`, color }}>
      <Icon size={22} />
    </div>
    <div className="stat-info">
      <span className="stat-value">
        {loading ? <div className="skeleton" style={{ width: '60px', height: '28px' }} /> : value}
      </span>
      <span className="stat-label">{label}</span>
    </div>
  </div>
);

const DashboardPage = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({ totalActiveBooks: 0, totalAvailableBooks: 0 });
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const [statsRes, loansRes] = await Promise.all([
          bookAPI.getStats(),
          bookLoanAPI.getMyLoans({ page: 0, size: 5 }),
        ]);
        setStats(statsRes.data);
        setLoans(loansRes.data?.content || []);
      } catch (err) {
        console.error('Dashboard fetch error:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchDashboard();
  }, []);

  const greeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good Morning';
    if (hour < 17) return 'Good Afternoon';
    return 'Good Evening';
  };

  return (
    <div className="dashboard-page animate-fadeIn" id="dashboard-page">
      <div className="dashboard-hero">
        <div>
          <h1 className="dashboard-greeting">
            {greeting()}, <span className="gradient-text">{user?.fullName?.split(' ')[0] || 'Reader'}</span> 📚
          </h1>
          <p className="dashboard-subtitle">
            Welcome to your library dashboard. Here&apos;s what&apos;s happening today.
          </p>
        </div>
      </div>

      <div className="stats-grid">
        <StatCard
          icon={BookOpen}
          label="Total Books"
          value={stats.totalActiveBooks?.toLocaleString()}
          color="#6366f1"
          loading={loading}
        />
        <StatCard
          icon={TrendingUp}
          label="Available Now"
          value={stats.totalAvailableBooks?.toLocaleString()}
          color="#10b981"
          loading={loading}
        />
        <StatCard
          icon={BookMarked}
          label="Active Loans"
          value={loans.filter(l => l.status === 'CHECKED_OUT').length}
          color="#f59e0b"
          loading={loading}
        />
        <StatCard
          icon={AlertTriangle}
          label="Overdue"
          value={loans.filter(l => l.status === 'OVERDUE').length}
          color="#ef4444"
          loading={loading}
        />
      </div>

      <div className="dashboard-sections">
        <div className="dashboard-section">
          <div className="section-header">
            <h2 className="section-title">
              <Clock size={18} />
              Recent Loans
            </h2>
            <Link to="/my-loans" className="section-link" id="view-all-loans">
              View all <ArrowRight size={14} />
            </Link>
          </div>

          {loading ? (
            <div className="loans-list">
              {[1, 2, 3].map(i => (
                <div key={i} className="loan-card card">
                  <div className="skeleton" style={{ width: '100%', height: '60px' }} />
                </div>
              ))}
            </div>
          ) : loans.length > 0 ? (
            <div className="loans-list">
              {loans.slice(0, 5).map((loan) => (
                <div key={loan.id} className="loan-card card card-glow" id={`loan-${loan.id}`}>
                  <div className="loan-book-info">
                    <h4 className="loan-book-title">{loan.bookTitle}</h4>
                    <span className="loan-book-author">{loan.bookAuthor}</span>
                  </div>
                  <div className="loan-meta">
                    <span className={`badge badge-${loan.status === 'CHECKED_OUT' ? 'info' : loan.status === 'RETURNED' ? 'success' : loan.status === 'OVERDUE' ? 'error' : 'warning'}`}>
                      {loan.status?.replace('_', ' ')}
                    </span>
                    {loan.dueDate && (
                      <span className="loan-due">Due: {loan.dueDate}</span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="empty-state" style={{ padding: '40px 20px' }}>
              <BookMarked size={40} className="empty-state-icon" />
              <p className="empty-state-title">No Active Loans</p>
              <p className="empty-state-text">Browse the catalog to borrow your first book!</p>
              <Link to="/books" className="btn btn-primary btn-sm" style={{ marginTop: '16px' }}>
                Browse Books
              </Link>
            </div>
          )}
        </div>

        <div className="dashboard-section quick-actions-section">
          <h2 className="section-title" style={{ marginBottom: '16px' }}>Quick Actions</h2>
          <div className="quick-actions">
            <Link to="/books" className="quick-action-card card card-glow" id="qa-browse-books">
              <BookOpen size={24} />
              <span>Browse Catalog</span>
            </Link>
            <Link to="/my-loans" className="quick-action-card card card-glow" id="qa-my-loans">
              <BookMarked size={24} />
              <span>My Loans</span>
            </Link>
            <Link to="/my-wishlist" className="quick-action-card card card-glow" id="qa-wishlist">
              <Users size={24} />
              <span>Wishlist</span>
            </Link>
            <Link to="/subscriptions" className="quick-action-card card card-glow" id="qa-subscriptions">
              <TrendingUp size={24} />
              <span>Subscription</span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
