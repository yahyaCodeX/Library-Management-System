import { useState, useEffect, useCallback } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { reservationAPI } from '../../services/api';
import toast from 'react-hot-toast';
import {
  Clock, Calendar, XCircle, BookOpen, AlertCircle
} from 'lucide-react';
import './MyReservations.css';

const ReservationStatusBadge = ({ status }) => {
  const statusConfig = {
    PENDING:    { label: 'Pending',   className: 'badge-warning' },
    AVAILABLE:  { label: 'Available', className: 'badge-success' },
    FULLFILLED: { label: 'Fulfilled', className: 'badge-info'    },
    CANCELLED:  { label: 'Cancelled', className: 'badge-neutral' }
  };
  const config = statusConfig[status] || { label: status, className: 'badge-neutral' };
  return (
    <span className={`badge ${config.className}`}>
      {config.label}
    </span>
  );
};

const MyReservationsPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);

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

  const fetchReservations = useCallback(async () => {
    setLoading(true);
    try {
      const params = { page, size: 10, sortBy: 'reservedAt', sortDirection: 'DESC' };
      if (statusFilter !== 'ALL') params.reservationStatus = statusFilter;
      
      const res = await reservationAPI.getMy(params);
      setReservations(res.data?.content || []);
      setTotalElements(res.data?.totalElements || 0);
    } catch (err) {
      console.error('Failed to load reservations:', err);
      toast.error('Failed to load your reservations');
    } finally {
      setLoading(false);
    }
  }, [page, statusFilter]);

  useEffect(() => {
    fetchReservations();
  }, [fetchReservations]);

  const handleCancel = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this reservation?')) return;
    try {
      await reservationAPI.cancel(id);
      toast.success('Reservation cancelled');
      fetchReservations();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to cancel reservation');
    }
  };

  return (
    <div className="reservations-page animate-fadeIn" id="my-reservations-page">
      <div className="page-header">
        <h1 className="page-title">My Reservations</h1>
        <p className="page-subtitle">Track books you are waiting for</p>
      </div>

      <div className="status-filter" style={{ marginBottom: '24px', width: 'fit-content' }}>
        {['ALL', 'PENDING', 'AVAILABLE', 'FULLFILLED', 'CANCELLED'].map(status => (
          <button
            key={status}
            className={`status-btn ${statusFilter === status ? 'active' : ''}`}
            onClick={() => updateParams({ status, page: 0 })}
          >
            {status === 'ALL' ? 'All' : status}
          </button>
        ))}
      </div>

      {loading ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {[1, 2, 3].map(i => <div key={i} className="reservation-card skeleton" style={{ height: '140px' }} />)}
        </div>
      ) : reservations.length > 0 ? (
        <div>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', marginBottom: '16px' }}>
            Showing {reservations.length} of {totalElements} reservations
          </p>
          {reservations.map(res => (
            <div key={res.id} className="reservation-card" id={`reservation-${res.id}`}>
              <div className="reservation-info">
                <div className="reservation-header">
                  <div>
                    <h3 className="reservation-title">
                      <Link to={`/books/${res.bookId}`} className="hover-link">{res.bookTitle}</Link>
                    </h3>
                    <p style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>by {res.bookAuthor}</p>
                  </div>
                  <ReservationStatusBadge status={res.reservationStatus} />
                </div>

                {res.reservationStatus === 'PENDING' && res.queuePosition != null && (
                  <div className="queue-badge">
                    <Clock size={16} />
                    Queue Position: #{res.queuePosition}
                  </div>
                )}

                <div className="reservation-details">
                  <div className="res-detail-item">
                    <Calendar size={16} />
                    <span>Reserved: <strong>
                      {res.reservedAt ? new Date(res.reservedAt).toLocaleDateString() : '-'}
                    </strong></span>
                  </div>
                  {res.reservationStatus === 'AVAILABLE' && (
                    <div className="res-detail-item">
                      <AlertCircle size={16} style={{ color: 'var(--color-success)' }} />
                      <span style={{ color: 'var(--color-success)', fontWeight: 'bold' }}>Ready for Pickup!</span>
                    </div>
                  )}
                  {res.canBeCancelled && (
                    <button 
                      className="btn btn-secondary btn-sm"
                      onClick={() => handleCancel(res.id)}
                      style={{ marginLeft: 'auto', color: 'var(--color-error)', borderColor: 'var(--color-error)' }}
                    >
                      <XCircle size={14} /> Cancel
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="empty-state">
          <Clock size={48} className="empty-state-icon" />
          <h3 className="empty-state-title">No Reservations</h3>
          <p className="empty-state-text">You don&apos;t have any reservations with this status.</p>
          <Link to="/books" className="btn btn-primary" style={{ marginTop: '16px' }}>
            Browse Books
          </Link>
        </div>
      )}
    </div>
  );
};

export default MyReservationsPage;
