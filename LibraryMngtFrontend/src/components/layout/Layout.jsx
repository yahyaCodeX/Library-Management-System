import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  BookOpen, LayoutDashboard, Library, BookMarked, Clock,
  CreditCard, AlertTriangle, Star, Heart, User, LogOut,
  Menu, X, Shield, ChevronDown, Bell
} from 'lucide-react';
import './Layout.css';

const Navbar = ({ onToggleSidebar, sidebarOpen }) => {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [dropdownOpen, setDropdownOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/auth/login');
  };

  const getInitials = (name) => {
    if (!name) return '?';
    return name.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2);
  };

  return (
    <nav className="navbar" id="main-navbar">
      <div className="navbar-left">
        <button
          className="navbar-toggle"
          onClick={onToggleSidebar}
          aria-label="Toggle sidebar"
          id="sidebar-toggle"
        >
          {sidebarOpen ? <X size={20} /> : <Menu size={20} />}
        </button>
        <Link to="/dashboard" className="navbar-brand" id="navbar-brand">
          <div className="brand-icon">
            <BookOpen size={22} />
          </div>
          <span className="brand-text">LibraryMS</span>
        </Link>
      </div>

      <div className="navbar-right">
        {isAdmin() && (
          <span className="admin-badge" id="admin-role-badge">
            <Shield size={12} />
            Admin
          </span>
        )}

        <button className="navbar-icon-btn" id="notification-btn">
          <Bell size={18} />
        </button>

        <div className="navbar-user" id="user-menu">
          <button
            className="user-trigger"
            onClick={() => setDropdownOpen(!dropdownOpen)}
            id="user-menu-trigger"
          >
            <div className="user-avatar">{getInitials(user?.fullName)}</div>
            <div className="user-info">
              <span className="user-name">{user?.fullName || 'User'}</span>
              <span className="user-email">{user?.email}</span>
            </div>
            <ChevronDown size={14} className={`chevron ${dropdownOpen ? 'open' : ''}`} />
          </button>

          {dropdownOpen && (
            <>
              <div className="dropdown-backdrop" onClick={() => setDropdownOpen(false)} />
              <div className="user-dropdown animate-scaleIn" id="user-dropdown">
                <Link
                  to="/profile"
                  className="dropdown-item"
                  onClick={() => setDropdownOpen(false)}
                  id="profile-link"
                >
                  <User size={16} />
                  My Profile
                </Link>
                <div className="divider" style={{ margin: '4px 0' }} />
                <button className="dropdown-item danger" onClick={handleLogout} id="logout-btn">
                  <LogOut size={16} />
                  Sign Out
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

const sidebarLinks = {
  main: [
    { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
    { to: '/books', icon: Library, label: 'Book Catalog' },
  ],
  user: [
    { to: '/my-loans', icon: BookMarked, label: 'My Loans' },
    { to: '/my-reservations', icon: Clock, label: 'Reservations' },
    { to: '/my-wishlist', icon: Heart, label: 'Wishlist' },
    { to: '/my-fines', icon: AlertTriangle, label: 'My Fines' },
    { to: '/subscriptions', icon: CreditCard, label: 'Subscriptions' },
  ],
  admin: [
    { to: '/admin/dashboard', icon: LayoutDashboard, label: 'Admin Dashboard' },
    { to: '/admin/books', icon: BookOpen, label: 'Manage Books' },
    { to: '/admin/genres', icon: Star, label: 'Manage Genres' },
    { to: '/admin/loans', icon: BookMarked, label: 'All Loans' },
    { to: '/admin/reservations', icon: Clock, label: 'All Reservations' },
    { to: '/admin/fines', icon: AlertTriangle, label: 'All Fines' },
    { to: '/admin/payments', icon: CreditCard, label: 'All Payments' },
    { to: '/admin/subscriptions', icon: CreditCard, label: 'Subscriptions' },
    { to: '/admin/plans', icon: Star, label: 'Sub Plans' },
    { to: '/admin/users', icon: User, label: 'Users' },
  ],
};

const Sidebar = ({ isOpen }) => {
  const { isAdmin } = useAuth();
  const location = useLocation();

  const NavItem = ({ to, icon: Icon, label }) => {
    const active = location.pathname === to;
    return (
      <Link to={to} className={`sidebar-link ${active ? 'active' : ''}`} id={`nav-${to.replace(/\//g, '-').slice(1)}`}>
        <Icon size={18} />
        <span>{label}</span>
        {active && <div className="active-indicator" />}
      </Link>
    );
  };

  return (
    <aside className={`sidebar ${isOpen ? 'open' : 'closed'}`} id="main-sidebar">
      <div className="sidebar-content">
        <div className="sidebar-section">
          <span className="sidebar-section-title">Main</span>
          {sidebarLinks.main.map(link => <NavItem key={link.to} {...link} />)}
        </div>

        <div className="sidebar-section">
          <span className="sidebar-section-title">Library</span>
          {sidebarLinks.user.map(link => <NavItem key={link.to} {...link} />)}
        </div>

        {isAdmin() && (
          <div className="sidebar-section">
            <span className="sidebar-section-title">Administration</span>
            {sidebarLinks.admin.map(link => <NavItem key={link.to} {...link} />)}
          </div>
        )}
      </div>

      <div className="sidebar-footer">
        <div className="sidebar-version">v1.0.0</div>
      </div>
    </aside>
  );
};

const Layout = ({ children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(true);

  return (
    <div className="app-layout">
      <Navbar onToggleSidebar={() => setSidebarOpen(!sidebarOpen)} sidebarOpen={sidebarOpen} />
      <div className="layout-body">
        <Sidebar isOpen={sidebarOpen} />
        <main className={`main-content ${sidebarOpen ? '' : 'expanded'}`} id="main-content">
          {children}
        </main>
      </div>
    </div>
  );
};

export default Layout;
