import { useState, useEffect } from 'react';
import { subscriptionPlanAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Plus, Edit2, Trash2, X } from 'lucide-react';
import './AdminShared.css';

const AdminPlansPage = () => {
  const [plans, setPlans] = useState([]);
  const [loading, setLoading] = useState(true);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingPlan, setEditingPlan] = useState(null);
  const [formData, setFormData] = useState({
    name: '', price: 0, durationDays: 30, maxBooksAllowed: 1, maxDaysPerBook: 7, isActive: true
  });
  const [formLoading, setFormLoading] = useState(false);

  useEffect(() => {
    fetchPlans();
  }, []);

  const fetchPlans = async () => {
    setLoading(true);
    try {
      const res = await subscriptionPlanAPI.getAll();
      setPlans(res.data || []);
    } catch (err) {
      toast.error('Failed to load plans');
    } finally {
      setLoading(false);
    }
  };

  const openModal = (plan = null) => {
    if (plan) {
      setEditingPlan(plan);
      setFormData({
        name: plan.name, price: plan.price, durationDays: plan.durationDays,
        maxBooksAllowed: plan.maxBooksAllowed, maxDaysPerBook: plan.maxDaysPerBook,
        isActive: plan.isActive
      });
    } else {
      setEditingPlan(null);
      setFormData({
        name: '', price: 0, durationDays: 30, maxBooksAllowed: 1, maxDaysPerBook: 7, isActive: true
      });
    }
    setIsModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormLoading(true);
    try {
      if (editingPlan) {
        await subscriptionPlanAPI.update(editingPlan.id, formData);
        toast.success('Plan updated');
      } else {
        await subscriptionPlanAPI.create(formData);
        toast.success('Plan created');
      }
      setIsModalOpen(false);
      fetchPlans();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save plan');
    } finally {
      setFormLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this plan?')) return;
    try {
      await subscriptionPlanAPI.delete(id);
      toast.success('Plan deleted');
      fetchPlans();
    } catch (err) {
      toast.error('Failed to delete plan');
    }
  };

  return (
    <div className="admin-page animate-fadeIn" id="admin-plans-page">
      <div className="admin-header">
        <div>
          <h1 className="page-title">Subscription Plans</h1>
          <p className="page-subtitle">Create and manage premium subscription tiers</p>
        </div>
        <button className="btn btn-primary" onClick={() => openModal()}>
          <Plus size={16} /> Create Plan
        </button>
      </div>

      <div className="admin-table-container">
        <div className="table-responsive">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Plan Name</th>
                <th>Price</th>
                <th>Duration (Days)</th>
                <th>Limits</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '40px' }}>
                    <div className="spinner" style={{ margin: '0 auto' }}></div>
                  </td>
                </tr>
              ) : plans.length > 0 ? (
                plans.map(plan => (
                  <tr key={plan.id}>
                    <td><div style={{ fontWeight: 600 }}>{plan.name}</div></td>
                    <td style={{ fontWeight: 700, color: 'var(--accent-primary-hover)' }}>
                      ${Number(plan.price).toFixed(2)}
                    </td>
                    <td>{plan.durationDays} Days</td>
                    <td>
                      <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>
                        {plan.maxBooksAllowed} Books / {plan.maxDaysPerBook} Days
                      </div>
                    </td>
                    <td>
                      <span className={`badge ${plan.isActive ? 'badge-success' : 'badge-neutral'}`}>
                        {plan.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td>
                      <div className="table-actions">
                        <button className="btn-icon edit" onClick={() => openModal(plan)} title="Edit">
                          <Edit2 size={16} />
                        </button>
                        <button className="btn-icon delete" onClick={() => handleDelete(plan.id)} title="Delete">
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                    No plans available. Create one to get started.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Plan Form Modal */}
      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: '500px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h2 style={{ fontSize: '20px', fontWeight: 'bold' }}>
                {editingPlan ? 'Edit Plan' : 'Create Plan'}
              </h2>
              <button onClick={() => setIsModalOpen(false)} style={{ background: 'none', color: 'var(--text-secondary)' }}>
                <X size={24} />
              </button>
            </div>
            
            <form onSubmit={handleSubmit} className="admin-form-grid">
              <div className="form-group admin-form-full">
                <label className="form-label">Plan Name *</label>
                <input required type="text" className="form-input" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} />
              </div>

              <div className="form-group">
                <label className="form-label">Price ($) *</label>
                <input required type="number" step="0.01" min="0" className="form-input" value={formData.price} onChange={e => setFormData({...formData, price: parseFloat(e.target.value)})} />
              </div>
              <div className="form-group">
                <label className="form-label">Duration (Days) *</label>
                <input required type="number" min="1" className="form-input" value={formData.durationDays} onChange={e => setFormData({...formData, durationDays: parseInt(e.target.value)})} />
              </div>

              <div className="form-group">
                <label className="form-label">Max Books Allowed *</label>
                <input required type="number" min="1" className="form-input" value={formData.maxBooksAllowed} onChange={e => setFormData({...formData, maxBooksAllowed: parseInt(e.target.value)})} />
              </div>
              <div className="form-group">
                <label className="form-label">Max Days / Book *</label>
                <input required type="number" min="1" className="form-input" value={formData.maxDaysPerBook} onChange={e => setFormData({...formData, maxDaysPerBook: parseInt(e.target.value)})} />
              </div>

              <div className="form-group admin-form-full" style={{ display: 'flex', alignItems: 'center', gap: '8px', marginTop: '8px' }}>
                <input type="checkbox" id="isActive" checked={formData.isActive} onChange={e => setFormData({...formData, isActive: e.target.checked})} style={{ width: '16px', height: '16px' }} />
                <label htmlFor="isActive" className="form-label" style={{ marginBottom: 0 }}>Active (Visible to users)</label>
              </div>

              <div className="admin-form-full" style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '16px' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setIsModalOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={formLoading}>
                  {formLoading ? <div className="spinner" /> : 'Save Plan'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminPlansPage;
