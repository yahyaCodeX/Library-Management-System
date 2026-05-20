import { useState, useEffect } from 'react';
import { paymentAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Search } from 'lucide-react';
import './AdminShared.css';

const AdminPaymentsPage = () => {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    fetchPayments();
  }, []);

  const fetchPayments = async () => {
    setLoading(true);
    try {
      const res = await paymentAPI.getAll({ page: 0, size: 50, sortBy: 'createdAt', sortDir: 'DESC' });
      setPayments(res.data?.content || []);
    } catch (err) {
      toast.error('Failed to load payments');
    } finally {
      setLoading(false);
    }
  };

  const filteredPayments = payments.filter(p => {
    const matchesSearch = 
      p.userName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      p.transactionId?.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || p.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="admin-page animate-fadeIn" id="admin-payments-page">
      <div className="admin-header">
        <div>
          <h1 className="page-title">Payment History</h1>
          <p className="page-subtitle">View all transaction records</p>
        </div>
      </div>

      <div className="admin-table-container">
        <div className="admin-table-toolbar" style={{ justifyContent: 'flex-start' }}>
          <div className="admin-table-search">
            <Search size={16} />
            <input 
              type="text" 
              placeholder="Search user or transaction ID..." 
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          <select 
            className="form-input" 
            style={{ width: 'auto', background: 'var(--bg-tertiary)', border: '1px solid var(--border-color)' }}
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="ALL">All Statuses</option>
            <option value="SUCCESS">Success</option>
            <option value="FAILED">Failed</option>
            <option value="PENDING">Pending</option>
          </select>
          <div style={{ marginLeft: 'auto', fontSize: '13px', color: 'var(--text-secondary)' }}>
            Total: {filteredPayments.length} records
          </div>
        </div>

        <div className="table-responsive">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Transaction ID</th>
                <th>User</th>
                <th>Type</th>
                <th>Amount</th>
                <th>Status</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '40px' }}>
                    <div className="spinner" style={{ margin: '0 auto' }}></div>
                  </td>
                </tr>
              ) : filteredPayments.length > 0 ? (
                filteredPayments.map(payment => (
                  <tr key={payment.id}>
                    <td style={{ fontFamily: 'monospace', fontSize: '12px' }}>{payment.transactionId || 'N/A'}</td>
                    <td>
                      <div style={{ fontWeight: 600 }}>{payment.userName}</div>
                      <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>{payment.userEmail}</div>
                    </td>
                    <td><span className="badge badge-neutral">{payment.paymentType}</span></td>
                    <td style={{ fontWeight: 700 }}>
                      ${(payment.amount || 0).toFixed(2)}
                    </td>
                    <td>
                      <span className={`badge ${
                        payment.status === 'SUCCESS' ? 'badge-success' : 
                        payment.status === 'FAILED' ? 'badge-error' : 'badge-warning'
                      }`}>
                        {payment.status}
                      </span>
                    </td>
                    <td style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
                      {new Date(payment.createdAt).toLocaleString()}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                    No payments found.
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

export default AdminPaymentsPage;
