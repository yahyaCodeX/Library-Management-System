import { useState, useEffect } from 'react';
import { fineAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Search, ShieldAlert, X } from 'lucide-react';
import './AdminShared.css';

const AdminFinesPage = () => {
  const [fines, setFines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  const [waiveModalOpen, setWaiveModalOpen] = useState(false);
  const [selectedFineId, setSelectedFineId] = useState(null);
  const [waiveReason, setWaiveReason] = useState('');
  const [waiveLoading, setWaiveLoading] = useState(false);

  useEffect(() => {
    fetchFines();
  }, []);

  const fetchFines = async () => {
    setLoading(true);
    try {
      const res = await fineAPI.getAll({ page: 0, size: 50 });
      setFines(res.data?.content || []);
    } catch (err) {
      toast.error('Failed to load fines');
    } finally {
      setLoading(false);
    }
  };

  const openWaiveModal = (id) => {
    setSelectedFineId(id);
    setWaiveReason('');
    setWaiveModalOpen(true);
  };

  const handleWaive = async () => {
    if (!waiveReason.trim()) {
      toast.error('Reason is required');
      return;
    }
    setWaiveLoading(true);
    try {
      await fineAPI.waive({ fineId: selectedFineId, reason: waiveReason });
      toast.success('Fine waived successfully');
      setWaiveModalOpen(false);
      fetchFines();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to waive fine');
    } finally {
      setWaiveLoading(false);
    }
  };

  const filteredFines = fines.filter(f => {
    const matchesSearch = 
      f.userName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      f.userEmail?.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || f.fineStatus === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="admin-page animate-fadeIn" id="admin-fines-page">
      <div className="admin-header">
        <div>
          <h1 className="page-title">Manage Fines</h1>
          <p className="page-subtitle">View user fines and process waivers</p>
        </div>
      </div>

      <div className="admin-table-container">
        <div className="admin-table-toolbar" style={{ justifyContent: 'flex-start' }}>
          <div className="admin-table-search">
            <Search size={16} />
            <input 
              type="text" 
              placeholder="Search user..." 
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
            <option value="PENDING">Pending (Unpaid)</option>
            <option value="PAID">Paid</option>
            <option value="WAIVED">Waived</option>
          </select>
          <div style={{ marginLeft: 'auto', fontSize: '13px', color: 'var(--text-secondary)' }}>
            Total: {filteredFines.length} fines
          </div>
        </div>

        <div className="table-responsive">
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID / Issued</th>
                <th>User</th>
                <th>Amount</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', padding: '40px' }}>
                    <div className="spinner" style={{ margin: '0 auto' }}></div>
                  </td>
                </tr>
              ) : filteredFines.length > 0 ? (
                filteredFines.map(fine => (
                  <tr key={fine.id}>
                    <td>
                      <div style={{ fontWeight: 600 }}>#{fine.id}</div>
                      <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>
                        {new Date(fine.createdAt).toLocaleDateString()}
                      </div>
                    </td>
                    <td>
                      <div style={{ fontWeight: 600, color: 'var(--text-primary)', marginBottom: '4px' }}>{fine.userName}</div>
                      <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>{fine.userEmail}</div>
                    </td>
                    <td style={{ fontWeight: 700, color: 'var(--accent-primary-hover)' }}>
                      ${(fine.amount || 0).toFixed(2)}
                    </td>
                    <td>
                      <span className={`badge ${
                        fine.fineStatus === 'PAID' ? 'badge-success' : 
                        fine.fineStatus === 'WAIVED' ? 'badge-neutral' : 'badge-error'
                      }`}>
                        {fine.fineStatus}
                      </span>
                    </td>
                    <td>
                      {fine.fineStatus === 'PENDING' ? (
                        <button 
                          className="btn btn-secondary btn-sm"
                          onClick={() => openWaiveModal(fine.id)}
                          style={{ color: 'var(--color-warning)', borderColor: 'var(--color-warning)' }}
                        >
                          <ShieldAlert size={14} /> Waive Fine
                        </button>
                      ) : (
                        <span style={{ color: 'var(--text-muted)', fontSize: '13px' }}>-</span>
                      )}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                    No fines found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Waive Modal */}
      {waiveModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h2 style={{ fontSize: '20px', fontWeight: 'bold' }}>Waive Fine</h2>
              <button onClick={() => setWaiveModalOpen(false)} style={{ background: 'none', color: 'var(--text-secondary)' }}>
                <X size={24} />
              </button>
            </div>
            
            <div className="form-group">
              <label className="form-label">Reason for Waiving *</label>
              <textarea 
                className="form-input" 
                rows="3" 
                value={waiveReason}
                onChange={e => setWaiveReason(e.target.value)}
                placeholder="E.g., System error, Manager approval..."
              />
            </div>
            
            <div style={{ display: 'flex', gap: '12px', marginTop: '24px', justifyContent: 'flex-end' }}>
              <button className="btn btn-secondary" onClick={() => setWaiveModalOpen(false)}>Cancel</button>
              <button className="btn btn-primary" onClick={handleWaive} disabled={waiveLoading}>
                {waiveLoading ? <div className="spinner" /> : 'Confirm Waiver'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminFinesPage;
