import { useState, useEffect, useCallback } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { wishlistAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Heart, Trash2, BookOpen } from 'lucide-react';
import './MyWishlist.css';

const MyWishlistPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [wishlist, setWishlist] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalElements, setTotalElements] = useState(0);

  const page = parseInt(searchParams.get('page') || '0');

  const fetchWishlist = useCallback(async () => {
    setLoading(true);
    try {
      const res = await wishlistAPI.getMy({ page, size: 12 });
      setWishlist(res.data?.content || []);
      setTotalElements(res.data?.totalElements || 0);
    } catch (err) {
      console.error('Failed to load wishlist:', err);
      toast.error('Failed to load your wishlist');
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    fetchWishlist();
  }, [fetchWishlist]);

  const handleRemove = async (bookId, e) => {
    e.preventDefault(); // Prevent navigating to book details
    e.stopPropagation();
    try {
      await wishlistAPI.remove(bookId);
      toast.success('Removed from wishlist');
      fetchWishlist();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to remove from wishlist');
    }
  };

  return (
    <div className="wishlist-page animate-fadeIn" id="my-wishlist-page">
      <div className="page-header">
        <h1 className="page-title">My Wishlist</h1>
        <p className="page-subtitle">Books you&apos;ve saved for later</p>
      </div>

      {loading ? (
        <div className="wishlist-grid">
          {[1, 2, 3, 4].map(i => (
            <div key={i} className="wishlist-card skeleton" style={{ height: '360px' }} />
          ))}
        </div>
      ) : wishlist.length > 0 ? (
        <div>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
            Showing {wishlist.length} of {totalElements} saved books
          </p>
          <div className="wishlist-grid">
            {wishlist.map(item => (
              <Link to={`/books/${item.book?.id}`} key={item.id} className="wishlist-card" id={`wishlist-${item.id}`}>
                <button 
                  className="remove-wishlist-btn"
                  onClick={(e) => handleRemove(item.book?.id, e)}
                  title="Remove from Wishlist"
                >
                  <Trash2 size={16} />
                </button>
                
                <div className="wishlist-cover">
                  {item.book?.coverImageUrl ? (
                    <img src={item.book.coverImageUrl} alt={item.book.title} />
                  ) : (
                    <BookOpen size={48} style={{ color: 'var(--text-muted)' }} />
                  )}
                  <div style={{ position: 'absolute', bottom: '10px', left: '10px' }}>
                    <span className={`badge ${item.book?.availableCopies > 0 ? 'badge-success' : 'badge-error'}`}>
                      {item.book?.availableCopies > 0 ? 'Available' : 'Unavailable'}
                    </span>
                  </div>
                </div>
                
                <div className="wishlist-info">
                  <h3 className="wishlist-title">{item.book?.title}</h3>
                  <p className="wishlist-author">by {item.book?.author}</p>
                  
                  <div className="wishlist-footer">
                    {item.book?.price ? (
                      <span style={{ fontWeight: 'bold', color: 'var(--accent-primary-hover)' }}>
                        ${Number(item.book.price).toFixed(2)}
                      </span>
                    ) : (
                      <span style={{ color: 'var(--color-success)', fontWeight: 'bold' }}>Free</span>
                    )}
                    <span className="wishlist-date">
                      Added {new Date(item.addedAt).toLocaleDateString()}
                    </span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </div>
      ) : (
        <div className="empty-state">
          <Heart size={48} className="empty-state-icon" />
          <h3 className="empty-state-title">Your Wishlist is Empty</h3>
          <p className="empty-state-text">Explore the catalog and save books you want to read later.</p>
          <Link to="/books" className="btn btn-primary" style={{ marginTop: '16px' }}>
            Browse Books
          </Link>
        </div>
      )}
    </div>
  );
};

export default MyWishlistPage;
