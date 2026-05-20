import { Link, useSearchParams } from 'react-router-dom';
import { CheckCircle, ArrowRight, LayoutDashboard } from 'lucide-react';
import './PaymentCallback.css';

const PaymentSuccessPage = () => {
  const [searchParams] = useSearchParams();
  const sessionId = searchParams.get('session_id');

  return (
    <div className="payment-callback-page animate-fadeIn" id="payment-success-page">
      <div className="payment-card success">
        <div className="payment-icon-wrapper">
          <CheckCircle size={40} />
        </div>
        
        <h1 className="payment-title">Payment Successful!</h1>
        <p className="payment-message">
          Thank you for your payment. Your transaction has been completed successfully. 
          The changes to your account (fine clearance or subscription activation) may take a few moments to reflect.
          {sessionId && <span style={{ display: 'block', marginTop: '8px', fontSize: '12px', color: 'var(--text-muted)' }}>Session ID: {sessionId.substring(0, 16)}...</span>}
        </p>

        <div className="payment-actions">
          <Link to="/dashboard" className="btn btn-primary" id="success-dashboard-btn">
            <LayoutDashboard size={16} /> Go to Dashboard
          </Link>
          <Link to="/my-fines" className="btn btn-secondary" id="success-fines-btn">
            View My Fines <ArrowRight size={16} />
          </Link>
          <Link to="/subscriptions" className="btn btn-secondary" id="success-subs-btn">
            View Subscription <ArrowRight size={16} />
          </Link>
        </div>
      </div>
    </div>
  );
};

export default PaymentSuccessPage;
