import { useState, useEffect } from 'react';
import { bookLoanAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Search, CheckCircle, RefreshCw, AlertTriangle } from 'lucide-react';
import './AdminShared.css';

const AdminLoansPage = () => {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    fetchLoans();
  }, []);

  const fetchLoans = async () => {
    setLoading(true);
    try {
      const searchData = { page: 0, size: 50 };
      const res = await bookLoanAPI.searchLoans(searchData);
      setLoans(res.data?.content || []);
    } catch (err) {
      toast.error('Failed to load loans');
    } finally {
      setLoading(false);
    }
  };

  const handleCheckin = async (loanId) => {
    if (!window.confirm('Mark this book as returned?')) return;
    try {
      await bookLoanAPI.checkin({ bookLoanId: loanId });
      toast.success('Book returned successfully');
      fetchLoans();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to return book');
    }
  };

  const filteredLoans = loans.filter(l => {
    const matchesSearch = 
      l.bookTitle?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      l.userName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      l.userEmail?.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || l.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="admin-page animate-fadeIn" id="admin-loans-page">
      <div className="admin-header">
        <div>
          <h1 className="page-title">Manage Loans</h1>
          <p className="page-subtitle">Process check-ins and monitor active loans</p>
        </div>
      </div>

      <div className="admin-table-container">
        <div className="admin-table-toolbar" style={{ justifyContent: 'flex-start' }}>
          <div className="admin-table-search">
            <Search size={16} />
            <input 
              type="text" 
              placeholder="Search book or user..." 
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
            <option value="CHECKED_OUT">Checked Out</option>
            <option value="OVERDUE">Overdue</option>
            <option value="RETURNED">Returned</option>
          </select>
          <div style={{ marginLeft: 'auto', fontSize: '13px', color: 'var(--text-secondary)' }}>
            Total: {filteredLoans.length} loans
          </div>
        </div>

        <div className="table-responsive">
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Book</th>
                <th>User</th>
                <th>Dates</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '40px' }}>
                    <div className="spinner" style={{ margin: '0 auto' }}></div>
                  </td>
                </tr>
              ) : filteredLoans.length > 0 ? (
                filteredLoans.map(loan => (
                  <tr key={loan.id}>
                    <td>#{loan.id}</td>
                    <td>
                      <div style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{loan.bookTitle}</div>
                    </td>
                    <td>
                      <div style={{ fontWeight: 600, color: 'var(--text-primary)', marginBottom: '4px' }}>{loan.userName}</div>
                      <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>{loan.userEmail}</div>
                    </td>
                    <td>
                      <div style={{ fontSize: '12px' }}>Out: {loan.checkoutDate}</div>
                      <div style={{ fontSize: '12px', color: loan.status === 'OVERDUE' ? 'var(--color-error)' : 'inherit' }}>
                        Due: {loan.dueDate}
                      </div>
                    </td>
                    <td>
                      <span className={`badge ${
                        loan.status === 'CHECKED_OUT' ? 'badge-info' : 
                        loan.status === 'RETURNED' ? 'badge-success' : 
                        loan.status === 'OVERDUE' ? 'badge-error' : 'badge-neutral'
                      }`}>
                        {loan.status}
                      </span>
                    </td>
                    <td>
                      {['CHECKED_OUT', 'OVERDUE'].includes(loan.status) ? (
                        <button 
                          className="btn btn-secondary btn-sm"
                          onClick={() => handleCheckin(loan.id)}
                        >
                          <CheckCircle size={14} /> Check In
                        </button>
                      ) : (
                        <span style={{ color: 'var(--text-muted)', fontSize: '13px' }}>Returned</span>
                      )}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                    No loans found.
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

export default AdminLoansPage;
