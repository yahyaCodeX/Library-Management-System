import { useState, useEffect } from 'react';
import { userAPI } from '../../services/api';
import toast from 'react-hot-toast';
import { Search, UserCheck, Shield } from 'lucide-react';
import './AdminShared.css';

const AdminUsersPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const res = await userAPI.getAllUsers();
      // the backend returns a List<UserDto> or Page<UserDto>
      const userData = Array.isArray(res.data) ? res.data : (res.data?.content || []);
      setUsers(userData);
    } catch (err) {
      toast.error('Failed to load users');
    } finally {
      setLoading(false);
    }
  };

  const filteredUsers = users.filter(u => 
    u.name?.toLowerCase().includes(searchQuery.toLowerCase()) || 
    u.email?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="admin-page animate-fadeIn" id="admin-users-page">
      <div className="admin-header">
        <div>
          <h1 className="page-title">Manage Users</h1>
          <p className="page-subtitle">View and monitor registered members</p>
        </div>
      </div>

      <div className="admin-table-container">
        <div className="admin-table-toolbar">
          <div className="admin-table-search">
            <Search size={16} />
            <input 
              type="text" 
              placeholder="Search by name or email..." 
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
            Total: {filteredUsers.length} users
          </div>
        </div>

        <div className="table-responsive">
          <table className="admin-table">
            <thead>
              <tr>
                <th>User</th>
                <th>Role</th>
                <th>Status</th>
                <th>Joined Date</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center', padding: '40px' }}>
                    <div className="spinner" style={{ margin: '0 auto' }}></div>
                  </td>
                </tr>
              ) : filteredUsers.length > 0 ? (
                filteredUsers.map(user => (
                  <tr key={user.id}>
                    <td>
                      <div style={{ fontWeight: 600, color: 'var(--text-primary)', marginBottom: '4px' }}>{user.name || 'Anonymous User'}</div>
                      <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>{user.email}</div>
                    </td>
                    <td>
                      {user.role === 'ADMIN' ? (
                        <span className="badge badge-warning"><Shield size={12}/> Admin</span>
                      ) : (
                        <span className="badge badge-neutral"><UserCheck size={12}/> Member</span>
                      )}
                    </td>
                    <td>
                      {user.accountStatus === 'ACTIVE' || !user.accountStatus ? (
                        <span className="badge badge-success">Active</span>
                      ) : (
                        <span className="badge badge-error">{user.accountStatus}</span>
                      )}
                    </td>
                    <td>
                      {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : '-'}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                    No users found matching your search.
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

export default AdminUsersPage;
