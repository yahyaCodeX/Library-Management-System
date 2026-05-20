import { useState, useEffect } from 'react';
import { bookAPI, userAPI, fineAPI, reservationAPI } from '../../services/api';
import { Users, BookOpen, Clock, AlertTriangle, ArrowRight, DollarSign } from 'lucide-react';
import { Link } from 'react-router-dom';
import './AdminShared.css';

const StatCard = ({ title, value, icon: Icon, colorClass, linkTo, loading }) => (
  <div className="card" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '24px' }}>
      <div style={{ padding: '12px', borderRadius: '12px', background: `var(--${colorClass}-bg)`, color: `var(--${colorClass})` }}>
        <Icon size={24} />
      </div>
    </div>
    <div>
      <h3 style={{ fontSize: '14px', color: 'var(--text-secondary)', marginBottom: '8px', fontWeight: 600 }}>
        {title}
      </h3>
      {loading ? (
        <div className="skeleton" style={{ width: '80px', height: '36px' }} />
      ) : (
        <div style={{ fontSize: '32px', fontWeight: 800, color: 'var(--text-primary)' }}>
          {value}
        </div>
      )}
    </div>
    <div style={{ marginTop: 'auto', paddingTop: '16px', borderTop: '1px solid var(--border-color)' }}>
      <Link to={linkTo} style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '13px', color: 'var(--accent-primary-hover)', fontWeight: 600 }}>
        View Details <ArrowRight size={14} />
      </Link>
    </div>
  </div>
);

const AdminDashboardPage = () => {
  const [stats, setStats] = useState({
    books: 0,
    users: 0,
    activeLoans: 0,
    unpaidFines: 0,
    pendingReservations: 0,
    totalFineAmount: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [booksRes, usersRes, finesRes, resRes] = await Promise.all([
          bookAPI.getStats().catch(() => ({ data: {} })),
          userAPI.getAllUsers().catch(() => ({ data: { content: [] } })), // Assuming returns list directly
          fineAPI.getAll({ fineStatus: 'PENDING', size: 50 }).catch(() => ({ data: { content: [] } })),
          reservationAPI.search({ status: 'PENDING', size: 1 }).catch(() => ({ data: { totalElements: 0 } }))
        ]);

        const fines = finesRes.data?.content || [];
        const totalFineAmount = fines.reduce((sum, f) => sum + (f.amount || 0), 0);

        setStats({
          books: booksRes.data?.totalBooks || 0,
          activeLoans: booksRes.data?.activeLoans || 0,
          users: Array.isArray(usersRes.data) ? usersRes.data.length : (usersRes.data?.totalElements || 0),
          unpaidFines: fines.length,
          totalFineAmount,
          pendingReservations: resRes.data?.totalElements || 0
        });
      } catch (err) {
        console.error('Failed to load admin stats:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  return (
    <div className="admin-page animate-fadeIn" id="admin-dashboard-page">
      <div className="admin-header">
        <div>
          <h1 className="page-title">Admin Dashboard</h1>
          <p className="page-subtitle">Overview of library operations and metrics</p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '24px', marginBottom: '40px' }}>
        <StatCard 
          title="Total Books" 
          value={stats.books} 
          icon={BookOpen} 
          colorClass="accent-primary" 
          linkTo="/admin/books"
          loading={loading}
        />
        <StatCard 
          title="Active Users" 
          value={stats.users} 
          icon={Users} 
          colorClass="color-success" 
          linkTo="/admin/users"
          loading={loading}
        />
        <StatCard 
          title="Active Loans" 
          value={stats.activeLoans} 
          icon={Clock} 
          colorClass="color-warning" 
          linkTo="/admin/loans"
          loading={loading}
        />
        <StatCard 
          title="Pending Reservations" 
          value={stats.pendingReservations} 
          icon={BookOpen} 
          colorClass="accent-secondary" 
          linkTo="/admin/reservations"
          loading={loading}
        />
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '24px' }}>
        {/* Quick Actions */}
        <div className="card">
          <h3 style={{ fontSize: '18px', fontWeight: 800, marginBottom: '20px' }}>Quick Actions</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <Link to="/admin/books" className="btn btn-secondary" style={{ justifyContent: 'flex-start', padding: '12px 16px' }}>
              <BookOpen size={18} style={{ marginRight: '8px' }} /> Manage Books Catalog
            </Link>
            <Link to="/admin/loans" className="btn btn-secondary" style={{ justifyContent: 'flex-start', padding: '12px 16px' }}>
              <Clock size={18} style={{ marginRight: '8px' }} /> Process Returns & Checkouts
            </Link>
            <Link to="/admin/fines" className="btn btn-secondary" style={{ justifyContent: 'flex-start', padding: '12px 16px' }}>
              <AlertTriangle size={18} style={{ marginRight: '8px' }} /> Manage Fines & Overdue
            </Link>
          </div>
        </div>

        {/* Financial Overview (Fines) */}
        <div className="card" style={{ background: 'linear-gradient(135deg, rgba(239, 68, 68, 0.05), transparent)' }}>
          <h3 style={{ fontSize: '18px', fontWeight: 800, marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <DollarSign size={20} className="text-error" /> Financial Overview
          </h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <div>
              <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginBottom: '4px' }}>Total Unpaid Fines Amount</p>
              {loading ? (
                <div className="skeleton" style={{ width: '120px', height: '32px' }} />
              ) : (
                <div style={{ fontSize: '28px', fontWeight: 800, color: 'var(--color-error)' }}>
                  ${stats.totalFineAmount.toFixed(2)}
                </div>
              )}
            </div>
            <div>
              <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginBottom: '4px' }}>Total Unpaid Fines Count</p>
              {loading ? (
                <div className="skeleton" style={{ width: '60px', height: '24px' }} />
              ) : (
                <div style={{ fontSize: '20px', fontWeight: 700, color: 'var(--text-primary)' }}>
                  {stats.unpaidFines} fines
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboardPage;
