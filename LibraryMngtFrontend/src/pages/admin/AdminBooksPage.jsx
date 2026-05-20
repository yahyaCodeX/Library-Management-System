import { useState, useEffect } from 'react';
import { bookAPI, genreAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Plus, Edit2, Trash2, Search, X } from 'lucide-react';
import './AdminShared.css';

const AdminBooksPage = () => {
  const [books, setBooks] = useState([]);
  const [genres, setGenres] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingBook, setEditingBook] = useState(null);
  const [formData, setFormData] = useState({
    title: '', author: '', isbn: '', genreId: '',
    totalCopies: 1, availableCopies: 1, price: 0,
    coverImageUrl: '', description: ''
  });
  const [formLoading, setFormLoading] = useState(false);

  useEffect(() => {
    fetchBooks();
    fetchGenres();
  }, []);

  const fetchBooks = async (query = '') => {
    setLoading(true);
    try {
      const res = query 
        ? await bookAPI.advancedSearch({ keyword: query, size: 50 })
        : await bookAPI.getBooks({ size: 50, sortBy: 'createdAt', sortDir: 'DESC' });
      setBooks(res.data?.content || []);
    } catch (err) {
      toast.error('Failed to load books');
    } finally {
      setLoading(false);
    }
  };

  const fetchGenres = async () => {
    try {
      const res = await genreAPI.getAll();
      setGenres(res.data || []);
    } catch (err) {
      console.error('Failed to load genres', err);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    fetchBooks(searchQuery);
  };

  const openModal = (book = null) => {
    if (book) {
      setEditingBook(book);
      setFormData({
        title: book.title, author: book.author, isbn: book.isbn, 
        genreId: book.genreId || '', totalCopies: book.totalCopies, 
        availableCopies: book.availableCopies, price: book.price || 0,
        coverImageUrl: book.coverImageUrl || '', description: book.description || ''
      });
    } else {
      setEditingBook(null);
      setFormData({
        title: '', author: '', isbn: '', genreId: '',
        totalCopies: 1, availableCopies: 1, price: 0,
        coverImageUrl: '', description: ''
      });
    }
    setIsModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormLoading(true);
    try {
      if (editingBook) {
        await bookAPI.updateBook(editingBook.id, formData);
        toast.success('Book updated successfully');
      } else {
        await bookAPI.createBook(formData);
        toast.success('Book added successfully');
      }
      setIsModalOpen(false);
      fetchBooks(searchQuery);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save book');
    } finally {
      setFormLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this book?')) return;
    try {
      await bookAPI.deleteBook(id);
      toast.success('Book deleted successfully');
      fetchBooks(searchQuery);
    } catch (err) {
      toast.error('Failed to delete book');
    }
  };

  return (
    <div className="admin-page animate-fadeIn" id="admin-books-page">
      <div className="admin-header">
        <div>
          <h1 className="page-title">Manage Books</h1>
          <p className="page-subtitle">Add, edit, or remove books from the catalog</p>
        </div>
        <button className="btn btn-primary" onClick={() => openModal()} id="add-book-btn">
          <Plus size={16} /> Add New Book
        </button>
      </div>

      <div className="admin-table-container">
        <div className="admin-table-toolbar">
          <form onSubmit={handleSearch} className="admin-table-search">
            <Search size={16} />
            <input 
              type="text" 
              placeholder="Search title, author, or ISBN..." 
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </form>
          <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
            Total: {books.length} books
          </div>
        </div>

        <div className="table-responsive">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Title & Author</th>
                <th>ISBN</th>
                <th>Genre</th>
                <th>Inventory</th>
                <th>Price</th>
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
              ) : books.length > 0 ? (
                books.map(book => (
                  <tr key={book.id}>
                    <td>
                      <div style={{ fontWeight: 600, color: 'var(--text-primary)', marginBottom: '4px' }}>{book.title}</div>
                      <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>{book.author}</div>
                    </td>
                    <td style={{ fontFamily: 'monospace', fontSize: '12px' }}>{book.isbn}</td>
                    <td>{book.genreName || '-'}</td>
                    <td>
                      <span className={`badge ${book.availableCopies > 0 ? 'badge-success' : 'badge-error'}`} style={{ padding: '4px 8px' }}>
                        {book.availableCopies} / {book.totalCopies}
                      </span>
                    </td>
                    <td>{book.price ? `$${Number(book.price).toFixed(2)}` : 'Free'}</td>
                    <td>
                      <div className="table-actions">
                        <button className="btn-icon edit" onClick={() => openModal(book)} title="Edit">
                          <Edit2 size={16} />
                        </button>
                        <button className="btn-icon delete" onClick={() => handleDelete(book.id)} title="Delete">
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                    No books found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Book Form Modal */}
      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: '600px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h2 style={{ fontSize: '20px', fontWeight: 'bold' }}>
                {editingBook ? 'Edit Book' : 'Add New Book'}
              </h2>
              <button onClick={() => setIsModalOpen(false)} style={{ background: 'none', color: 'var(--text-secondary)' }}>
                <X size={24} />
              </button>
            </div>
            
            <form onSubmit={handleSubmit} className="admin-form-grid">
              <div className="form-group">
                <label className="form-label">Title *</label>
                <input required type="text" className="form-input" value={formData.title} onChange={e => setFormData({...formData, title: e.target.value})} />
              </div>
              <div className="form-group">
                <label className="form-label">Author *</label>
                <input required type="text" className="form-input" value={formData.author} onChange={e => setFormData({...formData, author: e.target.value})} />
              </div>
              
              <div className="form-group">
                <label className="form-label">ISBN *</label>
                <input required type="text" className="form-input" value={formData.isbn} onChange={e => setFormData({...formData, isbn: e.target.value})} />
              </div>
              <div className="form-group">
                <label className="form-label">Genre *</label>
                <select required className="form-input" value={formData.genreId} onChange={e => setFormData({...formData, genreId: e.target.value})}>
                  <option value="">Select Genre</option>
                  {genres.map(g => <option key={g.id} value={g.id}>{g.name}</option>)}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Total Copies *</label>
                <input required type="number" min="1" className="form-input" value={formData.totalCopies} onChange={e => setFormData({...formData, totalCopies: parseInt(e.target.value)})} />
              </div>
              <div className="form-group">
                <label className="form-label">Available Copies *</label>
                <input required type="number" min="0" max={formData.totalCopies} className="form-input" value={formData.availableCopies} onChange={e => setFormData({...formData, availableCopies: parseInt(e.target.value)})} />
              </div>

              <div className="form-group">
                <label className="form-label">Price ($)</label>
                <input type="number" step="0.01" min="0" className="form-input" value={formData.price} onChange={e => setFormData({...formData, price: parseFloat(e.target.value)})} />
              </div>
              <div className="form-group">
                <label className="form-label">Cover Image URL</label>
                <input type="url" className="form-input" value={formData.coverImageUrl} onChange={e => setFormData({...formData, coverImageUrl: e.target.value})} />
              </div>

              <div className="form-group admin-form-full">
                <label className="form-label">Description</label>
                <textarea className="form-input" rows="3" value={formData.description} onChange={e => setFormData({...formData, description: e.target.value})}></textarea>
              </div>

              <div className="admin-form-full" style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '8px' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setIsModalOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={formLoading}>
                  {formLoading ? <div className="spinner" /> : 'Save Book'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminBooksPage;
