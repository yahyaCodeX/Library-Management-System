import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { bookAPI, wishlistAPI, bookLoanAPI, reservationAPI, reviewAPI } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import toast from 'react-hot-toast';
import {
  ArrowLeft, BookOpen, Heart, HeartOff, BookMarked, Calendar,
  Globe, FileText, Hash, Building, CheckCircle, XCircle,
  Clock, Star, Bookmark
} from 'lucide-react';
import './BookDetail.css';

const BookDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState('');
  const [reviews, setReviews] = useState([]);
  const [reviewsLoading, setReviewsLoading] = useState(true);

  useEffect(() => {
    const fetchBook = async () => {
      try {
        const res = await bookAPI.getBookById(id);
        setBook(res.data);
      } catch (err) {
        console.error('Failed to load book:', err);
        toast.error('Failed to load book details');
        navigate('/books');
      } finally {
        setLoading(false);
      }
    };
    fetchBook();
  }, [id, navigate]);

  // Fetch reviews
  useEffect(() => {
    const fetchReviews = async () => {
      try {
        const res = await reviewAPI.getByBook(id, { page: 0, size: 10 });
        setReviews(res.data?.content || []);
      } catch (err) {
        console.error('Failed to load reviews:', err);
      } finally {
        setReviewsLoading(false);
      }
    };
    if (id) fetchReviews();
  }, [id]);

  const handleCheckout = async () => {
    setActionLoading('checkout');
    try {
      await bookLoanAPI.checkout({ bookId: parseInt(id) });
      toast.success('Book checked out successfully! 📚');
      // Refresh book data
      const res = await bookAPI.getBookById(id);
      setBook(res.data);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to checkout book');
    } finally {
      setActionLoading('');
    }
  };

  const handleReserve = async () => {
    setActionLoading('reserve');
    try {
      await reservationAPI.create({ bookId: parseInt(id) });
      toast.success('Book reserved successfully! 📌');
      const res = await bookAPI.getBookById(id);
      setBook(res.data);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to reserve book');
    } finally {
      setActionLoading('');
    }
  };

  const handleWishlist = async () => {
    setActionLoading('wishlist');
    try {
      await wishlistAPI.add(parseInt(id));
      toast.success('Added to wishlist! 💝');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to add to wishlist');
    } finally {
      setActionLoading('');
    }
  };

  if (loading) {
    return (
      <div className="book-detail-page">
        <div className="back-link">
          <ArrowLeft size={16} />
          <span>Back to Catalog</span>
        </div>
        <div className="book-detail-hero">
          <div className="skeleton" style={{ borderRadius: '24px', aspectRatio: '2/3' }} />
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div className="skeleton" style={{ width: '100px', height: '28px', borderRadius: '20px' }} />
            <div className="skeleton" style={{ width: '80%', height: '36px' }} />
            <div className="skeleton" style={{ width: '40%', height: '20px' }} />
            <div className="skeleton" style={{ width: '100%', height: '60px', borderRadius: '10px' }} />
            <div className="skeleton" style={{ width: '200px', height: '36px' }} />
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              {[1, 2, 3, 4].map(i => <div key={i} className="skeleton" style={{ height: '56px', borderRadius: '10px' }} />)}
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!book) return null;

  const isAvailable = book.availableCopies > 0;

  return (
    <div className="book-detail-page" id="book-detail-page">
      <Link to="/books" className="back-link" id="back-to-catalog">
        <ArrowLeft size={16} />
        Back to Catalog
      </Link>

      <div className="book-detail-hero">
        {/* Cover Image */}
        <div className="book-detail-cover">
          {book.coverImageUrl ? (
            <img src={book.coverImageUrl} alt={book.title} />
          ) : (
            <div className="book-detail-cover-placeholder">
              <BookOpen size={56} />
              <span>No Cover Available</span>
            </div>
          )}
        </div>

        {/* Book Information */}
        <div className="book-detail-info">
          {book.genreName && (
            <span className="book-detail-genre-badge" id="book-genre-badge">
              <Bookmark size={12} />
              {book.genreName}
            </span>
          )}

          <h1 className="book-detail-title" id="book-title">{book.title}</h1>
          <p className="book-detail-author" id="book-author">
            by <strong>{book.author}</strong>
          </p>

          {/* Availability Banner */}
          <div className={`availability-banner ${isAvailable ? 'available' : 'unavailable'}`} id="availability-banner">
            <div className="availability-icon">
              {isAvailable ? <CheckCircle size={20} /> : <XCircle size={20} />}
            </div>
            <div className="availability-text">
              <h4>{isAvailable ? 'Available for Checkout' : 'Currently Unavailable'}</h4>
              <p>{book.availableCopies} of {book.totalCopies} copies available</p>
            </div>
          </div>

          {/* Price */}
          {book.price && (
            <div className="book-detail-price-section">
              <span className="book-detail-price" id="book-price">
                <span className="currency">$</span>{Number(book.price).toFixed(2)}
              </span>
            </div>
          )}

          {/* Action Buttons */}
          <div className="book-detail-actions">
            {isAvailable && !book.alreadyHaveLoan && (
              <button
                className="btn btn-primary"
                onClick={handleCheckout}
                disabled={actionLoading === 'checkout'}
                id="checkout-btn"
              >
                {actionLoading === 'checkout' ? (
                  <div className="spinner" />
                ) : (
                  <>
                    <BookMarked size={16} />
                    Checkout Book
                  </>
                )}
              </button>
            )}

            {book.alreadyHaveLoan && (
              <span className="badge badge-info" style={{ padding: '10px 16px', fontSize: '13px' }}>
                <BookMarked size={14} />
                Already Checked Out
              </span>
            )}

            {!isAvailable && !book.alreadyHaveReservation && (
              <button
                className="btn btn-secondary"
                onClick={handleReserve}
                disabled={actionLoading === 'reserve'}
                id="reserve-btn"
              >
                {actionLoading === 'reserve' ? (
                  <div className="spinner" />
                ) : (
                  <>
                    <Clock size={16} />
                    Reserve Book
                  </>
                )}
              </button>
            )}

            {book.alreadyHaveReservation && (
              <span className="badge badge-warning" style={{ padding: '10px 16px', fontSize: '13px' }}>
                <Clock size={14} />
                Already Reserved
              </span>
            )}

            <button
              className="btn btn-secondary"
              onClick={handleWishlist}
              disabled={actionLoading === 'wishlist'}
              id="wishlist-btn"
            >
              {actionLoading === 'wishlist' ? (
                <div className="spinner" />
              ) : (
                <>
                  <Heart size={16} />
                  Add to Wishlist
                </>
              )}
            </button>
          </div>

          {/* Meta Information Grid */}
          <div className="book-meta-grid">
            {book.isbn && (
              <div className="meta-item">
                <div className="meta-item-icon"><Hash size={16} /></div>
                <div>
                  <div className="meta-item-label">ISBN</div>
                  <div className="meta-item-value">{book.isbn}</div>
                </div>
              </div>
            )}
            {book.publisher && (
              <div className="meta-item">
                <div className="meta-item-icon"><Building size={16} /></div>
                <div>
                  <div className="meta-item-label">Publisher</div>
                  <div className="meta-item-value">{book.publisher}</div>
                </div>
              </div>
            )}
            {book.publishedDate && (
              <div className="meta-item">
                <div className="meta-item-icon"><Calendar size={16} /></div>
                <div>
                  <div className="meta-item-label">Published</div>
                  <div className="meta-item-value">{book.publishedDate}</div>
                </div>
              </div>
            )}
            {book.language && (
              <div className="meta-item">
                <div className="meta-item-icon"><Globe size={16} /></div>
                <div>
                  <div className="meta-item-label">Language</div>
                  <div className="meta-item-value">{book.language}</div>
                </div>
              </div>
            )}
            {book.pages && (
              <div className="meta-item">
                <div className="meta-item-icon"><FileText size={16} /></div>
                <div>
                  <div className="meta-item-label">Pages</div>
                  <div className="meta-item-value">{book.pages}</div>
                </div>
              </div>
            )}
            <div className="meta-item">
              <div className="meta-item-icon"><BookOpen size={16} /></div>
              <div>
                <div className="meta-item-label">Total Copies</div>
                <div className="meta-item-value">{book.totalCopies}</div>
              </div>
            </div>
          </div>

          {/* Description */}
          {book.description && (
            <div className="book-description" id="book-description">
              <h3>About This Book</h3>
              <p>{book.description}</p>
            </div>
          )}
        </div>
      </div>

      {/* Reviews Section */}
      <div className="book-reviews-section" id="book-reviews-section">
        <h2>
          <Star size={20} style={{ display: 'inline', marginRight: '8px', verticalAlign: 'middle' }} />
          Reviews ({reviews.length})
        </h2>

        {reviewsLoading ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {[1, 2, 3].map(i => <div key={i} className="skeleton" style={{ height: '80px', borderRadius: '12px' }} />)}
          </div>
        ) : reviews.length > 0 ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {reviews.map((review) => (
              <div key={review.id} className="card" id={`review-${review.id}`}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
                  <div>
                    <div style={{ fontWeight: 600, fontSize: '14px', color: 'var(--text-primary)' }}>
                      {review.userName || 'Anonymous'}
                    </div>
                    {review.title && (
                      <div style={{ fontSize: '13px', color: 'var(--text-secondary)', marginTop: '2px' }}>
                        {review.title}
                      </div>
                    )}
                  </div>
                  <div style={{ display: 'flex', gap: '2px' }}>
                    {[1, 2, 3, 4, 5].map(s => (
                      <Star
                        key={s}
                        size={14}
                        fill={s <= review.rating ? '#f59e0b' : 'transparent'}
                        color={s <= review.rating ? '#f59e0b' : 'var(--text-muted)'}
                      />
                    ))}
                  </div>
                </div>
                <p style={{ fontSize: '14px', color: 'var(--text-secondary)', lineHeight: '1.6' }}>
                  {review.reviewText}
                </p>
                <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginTop: '8px' }}>
                  {review.createdAt && new Date(review.createdAt).toLocaleDateString('en-US', {
                    year: 'numeric', month: 'long', day: 'numeric'
                  })}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="card" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-tertiary)' }}>
            <Star size={32} style={{ marginBottom: '12px', opacity: 0.3 }} />
            <p>No reviews yet. Be the first to review this book!</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default BookDetailPage;
