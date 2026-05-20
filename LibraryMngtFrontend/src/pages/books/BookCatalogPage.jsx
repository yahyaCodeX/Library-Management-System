import { useState, useEffect, useCallback } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { bookAPI, genreAPI } from '../../services/api';
import {
  Search, Grid3X3, List, BookOpen, Filter,
  ChevronLeft, ChevronRight, SlidersHorizontal, X, Tag
} from 'lucide-react';
import './Books.css';

// ─── Book Card Component ───────────────────────────────────────────────────
const BookCard = ({ book }) => (
  <Link to={`/books/${book.id}`} className="book-card" id={`book-card-${book.id}`}>
    <div className="book-card-cover">
      {book.coverImageUrl ? (
        <img src={book.coverImageUrl} alt={book.title} loading="lazy" />
      ) : (
        <div className="book-cover-placeholder">
          <BookOpen size={36} />
          <span>No Cover</span>
        </div>
      )}
      <div className="book-card-availability">
        <span className={`badge ${book.availableCopies > 0 ? 'badge-success' : 'badge-error'}`}>
          {book.availableCopies > 0 ? 'Available' : 'Unavailable'}
        </span>
      </div>
    </div>
    <div className="book-card-body">
      {book.genreName && <div className="book-card-genre">{book.genreName}</div>}
      <h3 className="book-card-title">{book.title}</h3>
      <p className="book-card-author">by {book.author}</p>
      <div className="book-card-footer">
        {book.price ? (
          <span className="book-card-price">
            <span className="currency">$</span>{Number(book.price).toFixed(2)}
          </span>
        ) : (
          <span className="book-card-price" style={{ color: 'var(--color-success)' }}>Free</span>
        )}
        <span className="book-card-copies">{book.availableCopies}/{book.totalCopies} copies</span>
      </div>
    </div>
  </Link>
);

// ─── Book List Item Component ──────────────────────────────────────────────
const BookListItem = ({ book }) => (
  <Link to={`/books/${book.id}`} className="book-list-item" id={`book-list-${book.id}`}>
    <div className="book-list-cover">
      {book.coverImageUrl ? (
        <img src={book.coverImageUrl} alt={book.title} loading="lazy" />
      ) : (
        <BookOpen size={28} style={{ color: 'var(--text-muted)' }} />
      )}
    </div>
    <div className="book-list-info">
      {book.genreName && <div className="book-card-genre">{book.genreName}</div>}
      <h3 className="book-card-title">{book.title}</h3>
      <p className="book-card-author">by {book.author}</p>
      {book.description && <p className="book-list-desc">{book.description}</p>}
      <div className="book-list-meta">
        {book.publisher && <span>📕 {book.publisher}</span>}
        {book.pages && <span>📄 {book.pages} pages</span>}
        {book.language && <span>🌐 {book.language}</span>}
      </div>
    </div>
    <div className="book-list-right">
      <span className={`badge ${book.availableCopies > 0 ? 'badge-success' : 'badge-error'}`}>
        {book.availableCopies > 0 ? 'Available' : 'Unavailable'}
      </span>
      {book.price ? (
        <span className="book-card-price">
          <span className="currency">$</span>{Number(book.price).toFixed(2)}
        </span>
      ) : (
        <span className="book-card-price" style={{ color: 'var(--color-success)' }}>Free</span>
      )}
    </div>
  </Link>
);

// ─── Skeleton Loader ───────────────────────────────────────────────────────
const BookSkeleton = () => (
  <div className="book-skeleton-card">
    <div className="book-skeleton-cover skeleton" />
    <div className="book-skeleton-body">
      <div className="skeleton" style={{ width: '60%', height: '12px' }} />
      <div className="skeleton" style={{ width: '90%', height: '16px' }} />
      <div className="skeleton" style={{ width: '50%', height: '12px' }} />
      <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '8px' }}>
        <div className="skeleton" style={{ width: '60px', height: '20px' }} />
        <div className="skeleton" style={{ width: '70px', height: '14px' }} />
      </div>
    </div>
  </div>
);

// ─── Genre Sidebar ─────────────────────────────────────────────────────────
const GenreSidebar = ({ genres, selectedGenre, onSelectGenre, loading }) => (
  <div className="genre-sidebar">
    <div className="genre-sidebar-card">
      <h3 className="genre-sidebar-title">
        <Tag size={16} />
        Genres
      </h3>
      {loading ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
          {[1, 2, 3, 4, 5, 6].map(i => (
            <div key={i} className="skeleton" style={{ height: '32px', borderRadius: '6px' }} />
          ))}
        </div>
      ) : (
        <div className="genre-list">
          <button
            className={`genre-item ${!selectedGenre ? 'active' : ''}`}
            onClick={() => onSelectGenre(null)}
            id="genre-all"
          >
            <span>All Genres</span>
          </button>
          {genres.map((genre) => (
            <button
              key={genre.id}
              className={`genre-item ${selectedGenre === genre.id ? 'active' : ''}`}
              onClick={() => onSelectGenre(genre.id)}
              id={`genre-${genre.id}`}
            >
              <span>{genre.name}</span>
              {genre.bookCount != null && (
                <span className="genre-count">{genre.bookCount}</span>
              )}
            </button>
          ))}
        </div>
      )}
    </div>
  </div>
);

// ─── Pagination Component ──────────────────────────────────────────────────
const Pagination = ({ page, totalPages, onPageChange }) => {
  if (totalPages <= 1) return null;

  const getPages = () => {
    const pages = [];
    const maxVisible = 5;
    let start = Math.max(0, page - Math.floor(maxVisible / 2));
    let end = Math.min(totalPages, start + maxVisible);
    if (end - start < maxVisible) start = Math.max(0, end - maxVisible);

    for (let i = start; i < end; i++) pages.push(i);
    return pages;
  };

  return (
    <div className="pagination" id="pagination">
      <button
        className="pagination-btn"
        onClick={() => onPageChange(page - 1)}
        disabled={page === 0}
        id="pagination-prev"
      >
        <ChevronLeft size={16} />
      </button>
      {getPages().map((p) => (
        <button
          key={p}
          className={`pagination-btn ${p === page ? 'active' : ''}`}
          onClick={() => onPageChange(p)}
          id={`pagination-page-${p}`}
        >
          {p + 1}
        </button>
      ))}
      <button
        className="pagination-btn"
        onClick={() => onPageChange(page + 1)}
        disabled={page >= totalPages - 1}
        id="pagination-next"
      >
        <ChevronRight size={16} />
      </button>
    </div>
  );
};

// ═══════════════════════════════════════════════════════════════════════════
// MAIN CATALOG PAGE
// ═══════════════════════════════════════════════════════════════════════════
const BookCatalogPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  // State
  const [books, setBooks] = useState([]);
  const [genres, setGenres] = useState([]);
  const [loading, setLoading] = useState(true);
  const [genresLoading, setGenresLoading] = useState(true);
  const [viewMode, setViewMode] = useState('grid');
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // Filters from URL
  const page = parseInt(searchParams.get('page') || '0');
  const selectedGenre = searchParams.get('genre') ? parseInt(searchParams.get('genre')) : null;
  const availableOnly = searchParams.get('available') === 'true';
  const sortBy = searchParams.get('sortBy') || 'createdAt';
  const sortDirection = searchParams.get('sortDir') || 'DESC';
  const searchQuery = searchParams.get('q') || '';

  // Update URL params
  const updateParams = (updates) => {
    const newParams = new URLSearchParams(searchParams);
    Object.entries(updates).forEach(([key, value]) => {
      if (value === null || value === undefined || value === '' || value === false) {
        newParams.delete(key);
      } else {
        newParams.set(key, value);
      }
    });
    // Reset to page 0 when filters change (unless page itself is being set)
    if (!('page' in updates)) {
      newParams.set('page', '0');
    }
    setSearchParams(newParams);
  };

  // Fetch genres on mount
  useEffect(() => {
    const fetchGenres = async () => {
      try {
        const res = await genreAPI.getAll();
        setGenres(res.data || []);
      } catch (err) {
        console.error('Failed to load genres:', err);
      } finally {
        setGenresLoading(false);
      }
    };
    fetchGenres();
  }, []);

  // Fetch books when filters change
  const fetchBooks = useCallback(async () => {
    setLoading(true);
    try {
      const params = {
        page,
        size: 20,
        sortBy,
        sortDirection,
      };
      if (selectedGenre) params.genreId = selectedGenre;
      if (availableOnly) params.availableOnly = true;

      let res;
      if (searchQuery.trim()) {
        // Use advanced search
        res = await bookAPI.advancedSearch({
          ...params,
          keyword: searchQuery.trim(),
        });
      } else {
        res = await bookAPI.getBooks(params);
      }

      const data = res.data;
      setBooks(data.content || []);
      setTotalElements(data.totalElements || 0);
      setTotalPages(data.totalPages || 0);
    } catch (err) {
      console.error('Failed to load books:', err);
      setBooks([]);
    } finally {
      setLoading(false);
    }
  }, [page, selectedGenre, availableOnly, sortBy, sortDirection, searchQuery]);

  useEffect(() => {
    fetchBooks();
  }, [fetchBooks]);

  // Debounced search
  const [searchInput, setSearchInput] = useState(searchQuery);
  useEffect(() => {
    const timer = setTimeout(() => {
      if (searchInput !== searchQuery) {
        updateParams({ q: searchInput || null });
      }
    }, 400);
    return () => clearTimeout(timer);
  }, [searchInput]);

  const selectedGenreName = genres.find(g => g.id === selectedGenre)?.name;

  return (
    <div className="catalog-page animate-fadeIn" id="book-catalog-page">
      {/* Header */}
      <div className="catalog-header">
        <div className="catalog-header-left">
          <h1 className="page-title">Book Catalog</h1>
          <p className="page-subtitle">Discover your next great read</p>
        </div>
        <div className="catalog-controls">
          <div className="view-toggle" id="view-toggle">
            <button
              className={`view-toggle-btn ${viewMode === 'grid' ? 'active' : ''}`}
              onClick={() => setViewMode('grid')}
              title="Grid view"
              id="view-grid-btn"
            >
              <Grid3X3 size={16} />
            </button>
            <button
              className={`view-toggle-btn ${viewMode === 'list' ? 'active' : ''}`}
              onClick={() => setViewMode('list')}
              title="List view"
              id="view-list-btn"
            >
              <List size={16} />
            </button>
          </div>
        </div>
      </div>

      {/* Search & Filter Toolbar */}
      <div className="catalog-toolbar">
        <div className="search-box">
          <Search size={16} className="search-icon" />
          <input
            type="text"
            placeholder="Search by title, author, or ISBN..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            id="book-search-input"
          />
        </div>

        <select
          className="filter-select"
          value={sortBy}
          onChange={(e) => updateParams({ sortBy: e.target.value })}
          id="sort-by-select"
        >
          <option value="createdAt">Newest First</option>
          <option value="title">Title A-Z</option>
          <option value="author">Author A-Z</option>
          <option value="price">Price</option>
          <option value="availableCopies">Availability</option>
        </select>

        <select
          className="filter-select"
          value={sortDirection}
          onChange={(e) => updateParams({ sortDir: e.target.value })}
          id="sort-dir-select"
          style={{ minWidth: '100px' }}
        >
          <option value="DESC">Desc</option>
          <option value="ASC">Asc</option>
        </select>

        <button
          className={`filter-chip ${availableOnly ? 'active' : ''}`}
          onClick={() => updateParams({ available: availableOnly ? null : 'true' })}
          id="filter-available"
        >
          <Filter size={14} />
          Available Only
        </button>
      </div>

      {/* Active Filters */}
      {(selectedGenre || searchQuery || availableOnly) && (
        <div className="active-filters" style={{ marginBottom: '16px' }}>
          {searchQuery && (
            <span className="filter-chip active">
              Search: &quot;{searchQuery}&quot;
              <X size={12} style={{ cursor: 'pointer' }} onClick={() => { setSearchInput(''); updateParams({ q: null }); }} />
            </span>
          )}
          {selectedGenreName && (
            <span className="filter-chip active">
              Genre: {selectedGenreName}
              <X size={12} style={{ cursor: 'pointer' }} onClick={() => updateParams({ genre: null })} />
            </span>
          )}
          {availableOnly && (
            <span className="filter-chip active">
              Available Only
              <X size={12} style={{ cursor: 'pointer' }} onClick={() => updateParams({ available: null })} />
            </span>
          )}
          <button
            className="clear-filters"
            onClick={() => { setSearchInput(''); setSearchParams({}); }}
            id="clear-all-filters"
          >
            Clear All
          </button>
        </div>
      )}

      {/* Layout: Sidebar + Content */}
      <div className="catalog-layout">
        <GenreSidebar
          genres={genres}
          selectedGenre={selectedGenre}
          onSelectGenre={(id) => updateParams({ genre: id })}
          loading={genresLoading}
        />

        <div className="catalog-content">
          {/* Results Info */}
          <div className="results-info">
            <span className="results-count">
              {loading ? (
                <div className="skeleton" style={{ width: '120px', height: '14px', display: 'inline-block' }} />
              ) : (
                <>Showing <strong>{books.length}</strong> of <strong>{totalElements}</strong> books</>
              )}
            </span>
          </div>

          {/* Books Display */}
          {loading ? (
            <div className={viewMode === 'grid' ? 'books-grid' : 'books-list'}>
              {Array.from({ length: 8 }).map((_, i) => (
                viewMode === 'grid' ? <BookSkeleton key={i} /> : (
                  <div key={i} className="book-list-item" style={{ opacity: 0.5 }}>
                    <div className="skeleton" style={{ width: '80px', height: '110px', borderRadius: '10px', flexShrink: 0 }} />
                    <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: '8px' }}>
                      <div className="skeleton" style={{ width: '40%', height: '12px' }} />
                      <div className="skeleton" style={{ width: '70%', height: '18px' }} />
                      <div className="skeleton" style={{ width: '30%', height: '12px' }} />
                    </div>
                  </div>
                )
              ))}
            </div>
          ) : books.length > 0 ? (
            viewMode === 'grid' ? (
              <div className="books-grid">
                {books.map((book, i) => (
                  <div key={book.id} style={{ animationDelay: `${i * 40}ms` }} className="animate-fadeIn">
                    <BookCard book={book} />
                  </div>
                ))}
              </div>
            ) : (
              <div className="books-list">
                {books.map((book, i) => (
                  <div key={book.id} style={{ animationDelay: `${i * 30}ms` }} className="animate-fadeIn">
                    <BookListItem book={book} />
                  </div>
                ))}
              </div>
            )
          ) : (
            <div className="empty-state">
              <BookOpen size={48} className="empty-state-icon" />
              <h3 className="empty-state-title">No Books Found</h3>
              <p className="empty-state-text">
                {searchQuery
                  ? `No results for "${searchQuery}". Try different keywords.`
                  : 'No books match the current filters. Try adjusting your search.'}
              </p>
              {(searchQuery || selectedGenre || availableOnly) && (
                <button
                  className="btn btn-secondary btn-sm"
                  onClick={() => { setSearchInput(''); setSearchParams({}); }}
                  style={{ marginTop: '16px' }}
                >
                  Clear Filters
                </button>
              )}
            </div>
          )}

          {/* Pagination */}
          {!loading && totalPages > 1 && (
            <Pagination
              page={page}
              totalPages={totalPages}
              onPageChange={(p) => updateParams({ page: p })}
            />
          )}
        </div>
      </div>
    </div>
  );
};

export default BookCatalogPage;
