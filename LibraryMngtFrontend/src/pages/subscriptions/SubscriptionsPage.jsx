import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { subscriptionAPI, subscriptionPlanAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Crown, CheckCircle, CreditCard, Calendar, XCircle, ArrowRight } from 'lucide-react';
import './Subscriptions.css';

const SubscriptionsPage = () => {
  const { user } = useAuth();
  const [plans, setPlans] = useState([]);
  const [activeSub, setActiveSub] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(null);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [plansRes, subRes] = await Promise.all([
        subscriptionPlanAPI.getAll(),
        subscriptionAPI.getMyActive().catch(() => ({ data: null })) // May 404 if no active sub
      ]);
      setPlans(plansRes.data || []);
      
      // If a subscription is returned and it's valid, set it
      if (subRes.data && subRes.data.isValid) {
        setActiveSub(subRes.data);
      } else {
        setActiveSub(null);
      }
    } catch (err) {
      console.error('Failed to load subscription data:', err);
      toast.error('Failed to load subscriptions');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSubscribe = async (plan) => {
    if (activeSub) {
      toast.error('You already have an active subscription.');
      return;
    }
    
    setActionLoading(plan.id);
    try {
      const res = await subscriptionAPI.subscribe({ subscriptionPlanId: plan.id });
      if (res.data?.checkoutUrl) {
        window.location.href = res.data.checkoutUrl;
      } else {
        toast.error('Failed to generate payment link');
        setActionLoading(null);
      }
    } catch (err) {
      console.error('Subscription error:', err);
      toast.error(err.response?.data?.message || 'Failed to initiate subscription');
      setActionLoading(null);
    }
  };

  const handleCancel = async () => {
    if (!window.confirm('Are you sure you want to cancel your subscription? You will lose access immediately.')) return;
    
    setActionLoading('cancel');
    try {
      await subscriptionAPI.cancel(activeSub.id, { cancellationReason: 'User cancelled via portal' });
      toast.success('Subscription cancelled successfully');
      fetchData();
    } catch (err) {
      console.error('Cancel error:', err);
      toast.error(err.response?.data?.message || 'Failed to cancel subscription');
    } finally {
      setActionLoading(null);
    }
  };

  if (loading) {
    return (
      <div className="subscriptions-page">
        <div className="skeleton" style={{ height: '160px', borderRadius: '16px', marginBottom: '40px' }} />
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '24px' }}>
          {[1, 2, 3].map(i => <div key={i} className="skeleton" style={{ height: '400px', borderRadius: '24px' }} />)}
        </div>
      </div>
    );
  }

  return (
    <div className="subscriptions-page animate-fadeIn" id="subscriptions-page">
      <div className="page-header">
        <h1 className="page-title">Subscriptions</h1>
        <p className="page-subtitle">Unlock unlimited reading with our premium plans</p>
      </div>

      {activeSub && (
        <div className="active-subscription-banner" id="active-sub-banner">
          <div className="active-sub-info">
            <div className="active-sub-icon">
              <Crown size={32} />
            </div>
            <div className="active-sub-details">
              <h2>{activeSub.planName} Plan Active</h2>
              <p>You have full access to library features.</p>
              
              <div className="active-sub-meta">
                <div className="sub-meta-item">
                  <span>Limits</span>
                  <span>{activeSub.maxBooksAllowed} books / {activeSub.maxDaysPerBook} days</span>
                </div>
                <div className="sub-meta-item">
                  <span>Valid Until</span>
                  <span><Calendar size={14} style={{ display: 'inline', marginRight: '4px' }}/> {activeSub.endDate}</span>
                </div>
              </div>
            </div>
          </div>
          
          <div className="active-sub-actions">
            <button 
              className="btn btn-secondary" 
              onClick={handleCancel}
              disabled={actionLoading === 'cancel'}
              style={{ color: 'var(--color-error)', borderColor: 'var(--color-error)' }}
              id="cancel-sub-btn"
            >
              {actionLoading === 'cancel' ? <div className="spinner" /> : <><XCircle size={16}/> Cancel Plan</>}
            </button>
          </div>
        </div>
      )}

      <div className="plans-section">
        <h3>Available Plans</h3>
        <div className="plans-grid">
          {plans.map(plan => {
            const isCurrent = activeSub?.subscriptionPlanId === plan.id;
            
            return (
              <div key={plan.id} className={`plan-card ${isCurrent ? 'current' : ''}`} id={`plan-${plan.id}`}>
                <div className="plan-header">
                  <h4 className="plan-name">{plan.name}</h4>
                  <div className="plan-price">
                    <span className="currency">$</span>
                    {Number(plan.price).toFixed(2)}
                  </div>
                  <div className="plan-duration">/{plan.durationDays} days</div>
                </div>

                <div className="plan-features">
                  <div className="plan-feature">
                    <CheckCircle size={18} className="feature-icon" />
                    Borrow up to {plan.maxBooksAllowed} books at once
                  </div>
                  <div className="plan-feature">
                    <CheckCircle size={18} className="feature-icon" />
                    Keep books for {plan.maxDaysPerBook} days
                  </div>
                  <div className="plan-feature">
                    <CheckCircle size={18} className="feature-icon" />
                    Priority reservations
                  </div>
                  <div className="plan-feature">
                    <CheckCircle size={18} className="feature-icon" />
                    Ad-free reading experience
                  </div>
                </div>

                <button 
                  className={`btn plan-action ${isCurrent ? 'btn-secondary' : 'btn-primary'}`}
                  disabled={isCurrent || activeSub || actionLoading === plan.id}
                  onClick={() => handleSubscribe(plan)}
                  id={`subscribe-btn-${plan.id}`}
                >
                  {actionLoading === plan.id ? (
                    <div className="spinner" />
                  ) : isCurrent ? (
                    'Current Plan'
                  ) : activeSub ? (
                    'Upgrade Plan'
                  ) : (
                    <>Subscribe Now <ArrowRight size={16} /></>
                  )}
                </button>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default SubscriptionsPage;
