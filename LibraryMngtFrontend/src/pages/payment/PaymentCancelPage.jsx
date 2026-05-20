import { Link } from 'react-router-dom';
import { XCircle, ArrowLeft, RefreshCw } from 'lucide-react';
import './PaymentCallback.css';

const PaymentCancelPage = () => {
  return (
    <div className="payment-callback-page animate-fadeIn" id="payment-cancel-page">
      <div className="payment-card cancel">
        <div className="payment-icon-wrapper">
          <XCircle size={40} />
        </div>
        
        <h1 className="payment-title">Payment Cancelled</h1>
        <p className="payment-message">
          Your payment process was interrupted or cancelled. No charges were made to your account.
          You can safely try again whenever you are ready.
        </p>

        <div className="payment-actions">
          <button 
            className="btn btn-primary" 
            onClick={() => window.history.back()}
            id="cancel-retry-btn"
          >
            <RefreshCw size={16} /> Try Again
          </button>
          <Link to="/dashboard" className="btn btn-secondary" id="cancel-dashboard-btn">
            <ArrowLeft size={16} /> Return to Dashboard
          </Link>
        </div>
      </div>
    </div>
  );
};

export default PaymentCancelPage;
