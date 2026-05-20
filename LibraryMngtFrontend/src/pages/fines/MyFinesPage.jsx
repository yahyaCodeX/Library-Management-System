import { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../../context/AuthContext';
import { fineAPI, paymentAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { AlertTriangle, CreditCard, Calendar, BookOpen, CheckCircle } from 'lucide-react';
import './MyFines.css';

const FineStatusBadge = ({ status }) => {
  if (status === 'PAID') return <span className="badge badge-success"><CheckCircle size={12}/> Paid</span>;
  if (status === 'WAIVED') return <span className="badge badge-neutral">Waived</span>;
  return <span className="badge badge-error">Pending</span>;
};

const MyFinesPage = () => {
  const { user } = useAuth();
  const [fines, setFines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [paymentLoading, setPaymentLoading] = useState(null);

  const fetchFines = useCallback(async () => {
    setLoading(true);
    try {
      const res = await fineAPI.getMyFines({ page: 0, size: 50, sortBy: 'createdAt', sortDir: 'DESC' });
      setFines(Array.isArray(res.data) ? res.data : (res.data?.content || []));
    } catch (err) {
      console.error('Failed to load fines:', err);
      toast.error('Failed to load your fines');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchFines();
  }, [fetchFines]);

  const totalUnpaid = fines
    .filter(f => f.fineStatus === 'PENDING')
    .reduce((sum, f) => sum + (f.amount || 0), 0);

  const handlePayFine = async (fine) => {
    setPaymentLoading(fine.id);
    try {
      // Stripe requires amount in smallest currency unit (cents for USD).
      // fine.amount is a decimal (e.g. 5.50), so we multiply by 100 → 550 cents.
      const amountInCents = Math.round((fine.amount || 0) * 100);
      const payload = {
        userId: user.id,
        fineId: fine.id,
        bookLoanId: fine.bookLoanId,
        paymentType: 'FINE',
        gateway: 'STRIPE',
        amount: amountInCents,
        currency: 'usd',
        description: `Payment for Fine #${fine.id} - ${fine.fineType}`,
        successUrl: `${window.location.origin}/payment/success`,
        cancelUrl: `${window.location.origin}/payment/cancel`
      };

      const res = await paymentAPI.initiate(payload);
      
      if (res.data?.checkoutUrl) {
        // Redirect to Stripe checkout
        window.location.href = res.data.checkoutUrl;
      } else {
        toast.error('Failed to generate payment link');
        setPaymentLoading(null);
      }
    } catch (err) {
      console.error('Payment initiation error:', err);
      toast.error(err.response?.data?.message || 'Failed to initiate payment');
      setPaymentLoading(null);
    }
  };

  return (
    <div className="fines-page animate-fadeIn" id="my-fines-page">
      <div className="page-header">
        <h1 className="page-title">My Fines</h1>
        <p className="page-subtitle">Manage and pay your library fines</p>
      </div>

      <div className="fines-summary">
        <div className="fines-summary-left">
          <div className="fines-summary-icon">
            <AlertTriangle size={28} />
          </div>
          <div className="fines-summary-text">
            <h2>Outstanding Balance</h2>
            <p>Please clear your dues to avoid account restrictions.</p>
          </div>
        </div>
        <div className="fines-summary-total">
          <span className="currency">$</span>
          {loading ? (
             <div className="skeleton" style={{ width: '80px', height: '36px' }} />
          ) : (
             totalUnpaid.toFixed(2)
          )}
        </div>
      </div>

      <div className="fines-list">
        {loading ? (
          [1, 2, 3].map(i => <div key={i} className="fine-card skeleton" style={{ height: '120px' }} />)
        ) : fines.length > 0 ? (
          fines.map(fine => (
            <div key={fine.id} className="fine-card" id={`fine-${fine.id}`}>
              <div className="fine-info">
                <span className="fine-type">{fine.fineType}</span>
                <div className="fine-reason">{fine.reason || 'Library Fine'}</div>
                
                <div className="fine-meta">
                  <div className="fine-meta-item">
                    <Calendar size={14} />
                    <span>Issued: {new Date(fine.createdAt).toLocaleDateString()}</span>
                  </div>
                  {fine.bookLoanId && (
                    <div className="fine-meta-item">
                      <BookOpen size={14} />
                      <span>Loan #{fine.bookLoanId}</span>
                    </div>
                  )}
                </div>
              </div>

              <div className="fine-amount-section">
                <div className="fine-amount">
                  <span style={{ fontSize: '14px', marginRight: '2px' }}>$</span>
                  {(fine.amount || 0).toFixed(2)}
                </div>
                
                {fine.fineStatus === 'PENDING' ? (
                  <button 
                    className="btn btn-primary btn-sm"
                    onClick={() => handlePayFine(fine)}
                    disabled={paymentLoading === fine.id}
                  >
                    {paymentLoading === fine.id ? (
                      <div className="spinner" />
                    ) : (
                      <>
                        <CreditCard size={14} /> Pay Now
                      </>
                    )}
                  </button>
                ) : (
                  <FineStatusBadge status={fine.fineStatus} />
                )}
              </div>
            </div>
          ))
        ) : (
          <div className="empty-state">
            <CheckCircle size={48} className="empty-state-icon" style={{ color: 'var(--color-success)' }} />
            <h3 className="empty-state-title">All Clear!</h3>
            <p className="empty-state-text">You have no fines on your account. Great job returning books on time!</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default MyFinesPage;
