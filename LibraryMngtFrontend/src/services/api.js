import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ─── Request Interceptor — Attach JWT ──────────────────────────────────────
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ─── Response Interceptor — Handle 401 ─────────────────────────────────────
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      // Only redirect if not already on auth pages
      if (!window.location.pathname.startsWith('/auth')) {
        window.location.href = '/auth/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;

// ─── Auth APIs ─────────────────────────────────────────────────────────────
export const authAPI = {
  signup: (data) => api.post('/auth/signup', data),
  login: (data) => api.post('/auth/login', data),
  forgotPassword: (email) => api.post('/auth/forgot-password', { email }),
  resetPassword: (data) => api.post('/auth/reset-password', data),
};

// ─── User APIs ─────────────────────────────────────────────────────────────
export const userAPI = {
  getProfile: () => api.get('/api/users/profile'),
  getAllUsers: () => api.get('/api/users/list'),
};

// ─── Book APIs ─────────────────────────────────────────────────────────────
export const bookAPI = {
  getBooks: (params) => api.get('/api/books', { params }),
  getBookById: (id) => api.get(`/api/books/${id}`),
  getBookByISBN: (isbn) => api.get(`/api/books/isbn/${isbn}`),
  advancedSearch: (data) => api.post('/api/books/search', data),
  createBook: (data) => api.post('/api/admin/books/create', data),
  createBulk: (data) => api.post('/api/books/create/bulk', data),
  updateBook: (id, data) => api.put(`/api/books/update/${id}`, data),
  deleteBook: (id) => api.delete(`/api/books/delete/${id}`),
  hardDeleteBook: (id) => api.delete(`/api/books/hard-delete/${id}`),
  getStats: () => api.get('/api/books/stats'),
};

// ─── Genre APIs ────────────────────────────────────────────────────────────
export const genreAPI = {
  getAll: () => api.get('/api/genres'),
  getById: (id) => api.get(`/api/genres/${id}`),
  create: (data) => api.post('/api/genres/create', data),
  update: (id, data) => api.put(`/api/genres/${id}`, data),
  delete: (id) => api.delete(`/api/genres/${id}`),
  hardDelete: (id) => api.delete(`/api/genres/${id}/hard`),
  getTopLevel: () => api.get('/api/genres/top-level-genres'),
  getActiveCount: () => api.get('/api/genres/active-genres'),
  getBookCount: (id) => api.get(`/api/genres/${id}/book-count`),
};

// ─── Book Loan APIs ────────────────────────────────────────────────────────
export const bookLoanAPI = {
  checkout: (data) => api.post('/api/book-loans/checkout', data),
  checkoutForUser: (userId, data) => api.post(`/api/book-loans/checkout/user/${userId}`, data),
  checkin: (data) => api.post('/api/book-loans/checkin', data),
  renew: (data) => api.post('/api/book-loans/renew', data),
  getMyLoans: (params) => api.get('/api/book-loans/MyBook-Loans', { params }),
  searchLoans: (data) => api.get('/api/book-loans/search', { params: data }),
  updateOverdue: () => api.post('/api/book-loans/admin/update-overdue'),
};

// ─── Reservation APIs ──────────────────────────────────────────────────────
export const reservationAPI = {
  create: (data) => api.post('/api/reservations', data),
  createForUser: (userId, data) => api.post(`/api/reservations/user/${userId}`, data),
  cancel: (id) => api.delete(`/api/reservations/${id}`),
  fulfill: (id) => api.post(`/api/reservations/${id}/fulfill`),
  getMy: (params) => api.get('/api/reservations/my', { params }),
  search: (params) => api.get('/api/reservations/search', { params }),
};

// ─── Fine APIs ─────────────────────────────────────────────────────────────
export const fineAPI = {
  create: (data) => api.post('/api/fines/create-fine', data),
  pay: (fineId, transactionId) => api.post(`/api/fines/${fineId}/pay`, null, { params: { transactionId } }),
  markPaid: (fineId, amount, transactionID) => api.post(`/api/fines/${fineId}/mark-paid`, amount, { params: { transactionID } }),
  waive: (data) => api.post('/api/fines/waive-fine', data),
  getMyFines: (params) => api.get('/api/fines/my-fines', { params }),
  getAll: (params) => api.get('/api/fines/all', { params }),
};

// ─── Payment APIs ──────────────────────────────────────────────────────────
export const paymentAPI = {
  initiate: (data) => api.post('/api/payments/initiate', data),
  verify: (data) => api.post('/api/payments/verify', data),
  getAll: (params) => api.get('/api/payments/all', { params }),
};

// ─── Subscription APIs ────────────────────────────────────────────────────
export const subscriptionAPI = {
  subscribe: (data) => api.post('/api/subscriptions/subscribe', data),
  getMyActive: () => api.get('/api/subscriptions/active/me'),
  getUserActive: (userId) => api.get(`/api/subscriptions/active/user/${userId}`),
  cancel: (id, data) => api.patch(`/api/subscriptions/${id}/cancel`, data),
  activate: (id, paymentId) => api.patch(`/api/subscriptions/${id}/activate`, null, { params: { paymentId } }),
  deactivateExpired: () => api.patch('/api/subscriptions/deactivate-expired'),
  getAll: (params) => api.get('/api/subscriptions/all', { params }),
};

// ─── Subscription Plan APIs ────────────────────────────────────────────────
export const subscriptionPlanAPI = {
  getAll: () => api.get('/api/subscription-plans'),
  create: (data) => api.post('/api/subscription-plans/admin/create', data),
  update: (id, data) => api.put(`/api/subscription-plans/admin/update/${id}`, data),
  delete: (id) => api.delete(`/api/subscription-plans/admin/delete/${id}`),
};

// ─── Review APIs ───────────────────────────────────────────────────────────
export const reviewAPI = {
  create: (data) => api.post('/api/reviews', data),
  update: (id, data) => api.put(`/api/reviews/${id}`, data),
  delete: (id) => api.delete(`/api/reviews/${id}`),
  getByBook: (bookId, params) => api.get(`/api/reviews/book/${bookId}`, { params }),
};

// ─── Wishlist APIs ─────────────────────────────────────────────────────────
export const wishlistAPI = {
  add: (bookId) => api.post(`/api/wishlist/add/${bookId}`),
  remove: (bookId) => api.delete(`/api/wishlist/remove/${bookId}`),
  getMy: (params) => api.get('/api/wishlist/my-wishlist', { params }),
};
