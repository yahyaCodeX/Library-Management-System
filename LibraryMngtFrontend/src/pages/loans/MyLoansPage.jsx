import { useState, useEffect, useCallback } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { bookLoanAPI } from '../../services/api';
import toast from 'react-hot-toast';
import {
  BookMarked, Calendar, AlertTriangle, Clock, RefreshCw, X, BookOpen
} from 'lucide-react';
import './MyLoans.css';

const LoanStatusBadge = ({ status }) => {
  const statusConfig = {
    CHECKED_OUT: { label: 'Active', className: 'badge-info' },
    RETURNED: { label: 'Returned', className: 'badge-success' },
    OVERDUE: { label: 'Overdue', className: 'badge-error' },
    CANCELLED: { label: 'Cancelled', className: 'badge-neutral' }
  };
  const config = statusConfig[status] || { label: status, className: 'badge-neutral' };
  
  return (
    <span className={`badge ${config.className}`}>
      {config.label}
    </span>
  );
};

const MyLoansPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);
  
  // Modal state for renewal
  const [renewModalOpen, setRenewModalOpen] = useState(false);
  const [selectedLoanId, setSelectedLoanId] = useState(null);
  const [renewDays, setRenewDays] = useState(14);
  const [renewLoading, setRenewLoading] = useState(false);

  const statusFilter = searchParams.get('status') || 'ALL';
  const page = parseInt(searchParams.get('page') || '0');

  const updateParams = (updates) => {
    const newParams = new URLSearchParams(searchParams);
    Object.entries(updates).forEach(([key, value]) => {
      if (!value || value === 'ALL') newParams.delete(key);
      else newParams.set(key, value);
    });
    setSearchParams(newParams);
  };

  const fetchLoans = useCallback(async () => {
    setLoading(true);
    try {
      const params = { page, size: 10, sortBy: 'checkoutDate', sortDir: 'DESC' };
      if (statusFilter !== 'ALL') params.status = statusFilter;
      
      const res = await bookLoanAPI.getMyLoans(params);
      setLoans(res.data?.content || []);
      setTotalElements(res.data?.totalElements || 0);
    } catch (err) {
      console.error('Failed to load loans:', err);
      toast.error('Failed to load your loans');
    } finally {
      setLoading(false);
    }
  }, [page, statusFilter]);

  useEffect(() => {
    fetchLoans();
  }, [fetchLoans]);

  const openRenewModal = (loanId) => {
    setSelectedLoanId(loanId);
    setRenewDays(14);
    setRenewModalOpen(true);
  };

  const handleRenew = async () => {
    if (!selectedLoanId) return;
    setRenewLoading(true);
    try {
      await bookLoanAPI.renew({ bookLoanId: selectedLoanId, extensionDays: renewDays });
      toast.success('Loan renewed successfully!');
      setRenewModalOpen(false);
      fetchLoans();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to renew loan');
    } finally {
      setRenewLoading(false);
    }
  };

  return (
    <div className="loans-page animate-fadeIn" id="my-loans-page">
      <div className="loans-header">
        <div>
          <h1 className="page-title">My Loans</h1>
          <p className="page-subtitle">Track and manage your borrowed books</p>
        </div>
      </div>

      <div className="loans-toolbar">
        <div className="status-filter">
          {['ALL', 'CHECKED_OUT', 'RETURNED', 'OVERDUE'].map(status => (
            <button
              key={status}
              className={`status-btn ${statusFilter === status ? 'active' : ''}`}
              onClick={() => updateParams({ status, page: 0 })}
            >
              {status === 'ALL' ? 'All Loans' : status.replace('_', ' ')}
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <div className="loans-list-container">
          {[1, 2, 3].map(i => (
            <div key={i} className="loan-item-card skeleton" style={{ height: '200px' }} />
          ))}
        </div>
      ) : loans.length > 0 ? (
        <div className="loans-list-container">
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
            Showing {loans.length} of {totalElements} loans
          </p>
          {loans.map(loan => (
            <div key={loan.id} className="loan-item-card" id={`loan-${loan.id}`}>
              <div className="loan-cover">
                 {(loan.bookCOverImage || loan.coverImageUrl) ? (
                    <img src={loan.bookCOverImage || loan.coverImageUrl} alt={loan.bookTitle} />
                 ) : (
                    <BookOpen size={32} style={{ color: 'var(--text-muted)' }} />
                 )}
              </div>
              
              <div className="loan-info">
                <div className="loan-header">
                  <div>
                    <h3 className="loan-title">
                      <Link to={`/books/${loan.bookId}`} className="hover-link">{loan.bookTitle}</Link>
                    </h3>
                    <p className="loan-author">by {loan.bookAuthor}</p>
                  </div>
                  <LoanStatusBadge status={loan.status} />
                </div>

                <div className="loan-dates">
                  <div className="date-item">
                    <span className="date-label">Checkout Date</span>
                    <span className="date-value"><Calendar size={14}/> {loan.checkoutDate}</span>
                  </div>
                  <div className="date-item">
                    <span className="date-label">Due Date</span>
                    <span className="date-value" style={{ color: loan.status === 'OVERDUE' ? 'var(--color-error)' : 'inherit' }}>
                      <Clock size={14}/> {loan.dueDate}
                    </span>
                  </div>
                  {loan.returnDate && (
                    <div className="date-item">
                      <span className="date-label">Returned On</span>
                      <span className="date-value"><Calendar size={14}/> {loan.returnDate}</span>
                    </div>
                  )}
                </div>

                <div className="loan-actions">
                  {['CHECKED_OUT', 'OVERDUE'].includes(loan.status) && (
                    <button 
                      className="btn btn-secondary btn-sm"
                      onClick={() => openRenewModal(loan.id)}
                    >
                      <RefreshCw size={14} /> Renew Loan
                    </button>
                  )}
                  {loan.status === 'OVERDUE' && (
                    <div className="loan-fine-warning">
                      <AlertTriangle size={16} />
                      Overdue Fine Accumulating
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="empty-state">
          <BookMarked size={48} className="empty-state-icon" />
          <h3 className="empty-state-title">No Loans Found</h3>
          <p className="empty-state-text">You haven&apos;t borrowed any books with this status.</p>
          <Link to="/books" className="btn btn-primary" style={{ marginTop: '16px' }}>
            Browse Books
          </Link>
        </div>
      )}

      {/* Renewal Modal */}
      {renewModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h2 style={{ fontSize: '20px', fontWeight: 'bold' }}>Renew Loan</h2>
              <button onClick={() => setRenewModalOpen(false)} style={{ background: 'none', color: 'var(--text-secondary)' }}>
                <X size={24} />
              </button>
            </div>
            
            <div className="renew-modal-content">
              <p style={{ color: 'var(--text-secondary)' }}>
                Select the number of days you want to extend this loan.
              </p>
              
              <div className="form-group">
                <label className="form-label">Extension Days</label>
                <select 
                  className="form-input" 
                  value={renewDays}
                  onChange={(e) => setRenewDays(parseInt(e.target.value))}
                >
                  <option value={7}>7 Days</option>
                  <option value={14}>14 Days</option>
                  <option value={21}>21 Days</option>
                  <option value={28}>28 Days</option>
                </select>
              </div>
              
              <div style={{ display: 'flex', gap: '12px', marginTop: '16px', justifyContent: 'flex-end' }}>
                <button className="btn btn-secondary" onClick={() => setRenewModalOpen(false)}>Cancel</button>
                <button className="btn btn-primary" onClick={handleRenew} disabled={renewLoading}>
                  {renewLoading ? <div className="spinner" /> : 'Confirm Renewal'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

    </div>
  );
};

export default MyLoansPage;
