import { useState, useEffect } from 'react';
import { subscriptionAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Search } from 'lucide-react';
import './AdminShared.css';

const AdminSubscriptionsPage = () => {
  const [subscriptions, setSubscriptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchSubscriptions();
  }, []);

  const fetchSubscriptions = async () => {
    setLoading(true);
    try {
      const res = await subscriptionAPI.getAll({ page: 0, size: 50 });
      setSubscriptions(res.data?.content || []);
    } catch (err) {
      toast.error('Failed to load subscriptions');
    } finally {
      setLoading(false);
    }
  };

  const filteredSubs = subscriptions.filter(s => 
    s.userName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    s.userEmail?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="admin-page animate-fadeIn" id="admin-subscriptions-page">
      <div className="admin-header">
        <div>
          <h1 className="page-title">User Subscriptions</h1>
          <p className="page-subtitle">View and monitor active subscription plans</p>
        </div>
      </div>

      <div className="admin-table-container">
        <div className="admin-table-toolbar">
          <div className="admin-table-search">
            <Search size={16} />
            <input 
              type="text" 
              placeholder="Search user..." 
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
            Total: {filteredSubs.length} subscriptions
          </div>
        </div>

        <div className="table-responsive">
          <table className="admin-table">
            <thead>
              <tr>
                <th>User</th>
                <th>Plan</th>
                <th>Validity</th>
                <th>Limits</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', padding: '40px' }}>
                    <div className="spinner" style={{ margin: '0 auto' }}></div>
                  </td>
                </tr>
              ) : filteredSubs.length > 0 ? (
                filteredSubs.map(sub => (
                  <tr key={sub.id}>
                    <td>
                      <div style={{ fontWeight: 600 }}>{sub.userName}</div>
                      <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>{sub.userEmail}</div>
                    </td>
                    <td>
                      <span className="badge badge-info">{sub.planName}</span>
                    </td>
                    <td>
                      <div style={{ fontSize: '13px' }}>Start: {sub.startDate || '-'}</div>
                      <div style={{ fontSize: '13px' }}>End: {sub.endDate || '-'}</div>
                    </td>
                    <td style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
                      {sub.maxBooksAllowed} books / {sub.maxDaysPerBook} days
                    </td>
                    <td>
                      <span className={`badge ${sub.isActive ? 'badge-success' : 'badge-error'}`}>
                        {sub.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                    No subscriptions found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default AdminSubscriptionsPage;
