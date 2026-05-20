import { useState, useEffect } from 'react';
import { reservationAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Search, CheckCircle } from 'lucide-react';
import './AdminShared.css';

const AdminReservationsPage = () => {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    fetchReservations();
  }, []);

  const fetchReservations = async () => {
    setLoading(true);
    try {
      const res = await reservationAPI.search({ page: 0, size: 50 });
      setReservations(res.data?.content || []);
    } catch (err) {
      toast.error('Failed to load reservations');
    } finally {
      setLoading(false);
    }
  };

  const handleFulfill = async (id) => {
    if (!window.confirm('Mark this reservation as fulfilled (book provided to user)?')) return;
    try {
      await reservationAPI.fulfill(id);
      toast.success('Reservation fulfilled');
      fetchReservations();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to fulfill reservation');
    }
  };

  const filteredReservations = reservations.filter(r => {
    const matchesSearch = 
      r.bookTitle?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.userName?.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || r.reservationStatus === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div className="admin-page animate-fadeIn" id="admin-reservations-page">
      <div className="admin-header">
        <div>
          <h1 className="page-title">Manage Reservations</h1>
          <p className="page-subtitle">Process pending book requests</p>
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
            <option value="PENDING">Pending</option>
            <option value="AVAILABLE">Available</option>
            <option value="FULLFILLED">Fulfilled</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
          <div style={{ marginLeft: 'auto', fontSize: '13px', color: 'var(--text-secondary)' }}>
            Total: {filteredReservations.length} reservations
          </div>
        </div>

        <div className="table-responsive">
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Book</th>
                <th>User</th>
                <th>Queue Pos.</th>
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
              ) : filteredReservations.length > 0 ? (
                 filteredReservations.map(res => (
                  <tr key={res.id}>
                    <td>#{res.id}</td>
                    <td><div style={{ fontWeight: 600 }}>{res.bookTitle}</div></td>
                    <td>
                      <div style={{ fontWeight: 600 }}>{res.userName}</div>
                      <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>{res.userEmail}</div>
                    </td>
                    <td>
                      {res.reservationStatus === 'PENDING' && res.queuePosition != null ? (
                        <span className="badge badge-warning">#{res.queuePosition}</span>
                      ) : '-'}
                    </td>
                    <td>
                      <span className={`badge ${
                        res.reservationStatus === 'PENDING'    ? 'badge-warning'  : 
                        res.reservationStatus === 'AVAILABLE'  ? 'badge-success'  : 
                        res.reservationStatus === 'FULLFILLED' ? 'badge-info'     : 'badge-neutral'
                      }`}>
                        {res.reservationStatus}
                      </span>
                    </td>
                    <td>
                      {res.reservationStatus === 'PENDING' || res.reservationStatus === 'AVAILABLE' ? (
                        <button 
                          className="btn btn-primary btn-sm"
                          onClick={() => handleFulfill(res.id)}
                        >
                          <CheckCircle size={14} /> Fulfill
                        </button>
                      ) : (
                        <span style={{ color: 'var(--text-muted)', fontSize: '13px' }}>-</span>
                      )}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                    No reservations found.
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

export default AdminReservationsPage;
