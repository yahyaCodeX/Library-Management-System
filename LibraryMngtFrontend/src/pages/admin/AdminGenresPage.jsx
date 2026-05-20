import { useState, useEffect } from 'react';
import { genreAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Plus, Edit2, Trash2, X, Search } from 'lucide-react';
import './AdminShared.css';

const AdminGenresPage = () => {
  const [genres, setGenres] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingGenre, setEditingGenre] = useState(null);
  const [formData, setFormData] = useState({ name: '', description: '' });
  const [formLoading, setFormLoading] = useState(false);

  useEffect(() => {
    fetchGenres();
  }, []);

  const fetchGenres = async () => {
    setLoading(true);
    try {
      const res = await genreAPI.getAll();
      setGenres(res.data || []);
    } catch (err) {
      toast.error('Failed to load genres');
    } finally {
      setLoading(false);
    }
  };

  const openModal = (genre = null) => {
    if (genre) {
      setEditingGenre(genre);
      setFormData({ name: genre.name, description: genre.description || '' });
    } else {
      setEditingGenre(null);
      setFormData({ name: '', description: '' });
    }
    setIsModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFormLoading(true);
    try {
      if (editingGenre) {
        await genreAPI.update(editingGenre.id, formData);
        toast.success('Genre updated');
      } else {
        await genreAPI.create(formData);
        toast.success('Genre created');
      }
      setIsModalOpen(false);
      fetchGenres();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to save genre');
    } finally {
      setFormLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this genre?')) return;
    try {
      await genreAPI.delete(id);
      toast.success('Genre deleted');
      fetchGenres();
    } catch (err) {
      toast.error('Failed to delete genre. It might be linked to existing books.');
    }
  };

  const filteredGenres = genres.filter(g => g.name?.toLowerCase().includes(searchQuery.toLowerCase()));

  return (
    <div className="admin-page animate-fadeIn" id="admin-genres-page">
      <div className="admin-header">
        <div>
          <h1 className="page-title">Manage Genres</h1>
          <p className="page-subtitle">Organize your library catalog categories</p>
        </div>
        <button className="btn btn-primary" onClick={() => openModal()}>
          <Plus size={16} /> Add Genre
        </button>
      </div>

      <div className="admin-table-container">
        <div className="admin-table-toolbar">
          <div className="admin-table-search">
            <Search size={16} />
            <input 
              type="text" 
              placeholder="Search genres..." 
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
            Total: {filteredGenres.length} genres
          </div>
        </div>

        <div className="table-responsive">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Genre Name</th>
                <th>Description</th>
                <th>Books Count</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center', padding: '40px' }}>
                    <div className="spinner" style={{ margin: '0 auto' }}></div>
                  </td>
                </tr>
              ) : filteredGenres.length > 0 ? (
                filteredGenres.map(genre => (
                  <tr key={genre.id}>
                    <td><div style={{ fontWeight: 600 }}>{genre.name}</div></td>
                    <td style={{ color: 'var(--text-secondary)', maxWidth: '400px', whiteSpace: 'normal' }}>
                      {genre.description || '-'}
                    </td>
                    <td>
                      <span className="badge badge-info">{genre.bookCount || 0} books</span>
                    </td>
                    <td>
                      <div className="table-actions">
                        <button className="btn-icon edit" onClick={() => openModal(genre)} title="Edit">
                          <Edit2 size={16} />
                        </button>
                        <button className="btn-icon delete" onClick={() => handleDelete(genre.id)} title="Delete">
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                    No genres found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Genre Modal */}
      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content" style={{ maxWidth: '500px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h2 style={{ fontSize: '20px', fontWeight: 'bold' }}>
                {editingGenre ? 'Edit Genre' : 'Add Genre'}
              </h2>
              <button onClick={() => setIsModalOpen(false)} style={{ background: 'none', color: 'var(--text-secondary)' }}>
                <X size={24} />
              </button>
            </div>
            
            <form onSubmit={handleSubmit} className="admin-form-grid">
              <div className="form-group admin-form-full">
                <label className="form-label">Genre Name *</label>
                <input required type="text" className="form-input" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} />
              </div>
              <div className="form-group admin-form-full">
                <label className="form-label">Description</label>
                <textarea className="form-input" rows="3" value={formData.description} onChange={e => setFormData({...formData, description: e.target.value})} />
              </div>
              
              <div className="admin-form-full" style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '16px' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setIsModalOpen(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={formLoading}>
                  {formLoading ? <div className="spinner" /> : 'Save Genre'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminGenresPage;
