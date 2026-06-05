export const BASE_URL = 'http://localhost:8080/api';

export const API_URLS = {
  // Auth
  REGISTER: `${BASE_URL}/register`,
  LOGIN: `${BASE_URL}/login`,

  //Admin
  CREATE_ADMIN: `${BASE_URL}/admin/create`,
  GET_ADMIN_ORDERS: `${BASE_URL}/orders/admin`,

  // Users
  REGISTER_USER: `${BASE_URL}/register-user`,
  GET_USER: (id: number) => `${BASE_URL}/user/${id}`,
  GET_USERS: `${BASE_URL}/users`,
  UPDATE_USER: (id: number) => `${BASE_URL}/update-user/${id}`,
  DELETE_USER: (id: number) => `${BASE_URL}/user/${id}`,

  // Products
  CREATE_PRODUCT: `${BASE_URL}/create-product`,
  GET_PRODUCT: (id: number) => `${BASE_URL}/product/${id}`,
  GET_PRODUCTS_BY_VENDOR: (id: number) => `${BASE_URL}/vendor-product/${id}`,
  GET_PRODUCTS: `${BASE_URL}/products`,
  GET_RECOMMENDED_PRODUCTS: `${BASE_URL}/products/recommended`,
  UPDATE_PRODUCT: (id: number) => `${BASE_URL}/update-product/${id}`,
  DELETE_PRODUCT: (id: number) => `${BASE_URL}/product/${id}`,
  SEARCH_PRODUCTS: (keyword: string) =>
    `${BASE_URL}/search-products/${encodeURIComponent(keyword)}`,

  // Categories
  CREATE_CATEGORY: `${BASE_URL}/category`,
  GET_CATEGORY: (id: number) => `${BASE_URL}/category/${id}`,
  GET_CATEGORIES: `${BASE_URL}/categories`,
  UPDATE_CATEGORY: (id: number) => `${BASE_URL}/category/${id}`,
  DELETE_CATEGORY: (id: number) => `${BASE_URL}/category/${id}`,

  // Vendors
  GET_VENDORS: `${BASE_URL}/vendors`,
  GET_VENDOR: (id: number) => `${BASE_URL}/vendor/${id}`,
  CREATE_VENDOR: `${BASE_URL}/vendor/create`,
  UPDATE_VENDOR: (id: number) => `${BASE_URL}/vendor/update/${id}`,
  APPROVE_VENDOR: (id: number) => `${BASE_URL}/vendor/approval/${id}`,
  DELETE_VENDOR: (id: number) => `${BASE_URL}/vendor/delete/${id}`,

  // Cart
  CART: `${BASE_URL}/cart`,
  CART_ITEMS: `${BASE_URL}/cart/items`,
  CART_ITEM: (productId: number) => `${BASE_URL}/cart/items/${productId}`,

  // Orders
  CHECKOUT: `${BASE_URL}/orders/checkout`,
  GET_ORDER: (id: number) => `${BASE_URL}/orders/${id}`,
  GET_VENDOR_ORDERS: (id: number) => `${BASE_URL}/orders/vendor/${id}`,
  GET_ORDERS: `${BASE_URL}/orders`,
  UPDATE_ORDER_STATUS: (id: number) => `${BASE_URL}/orders/${id}/status`,
  CANCEL_ORDER: (id: number) => `${BASE_URL}/orders/${id}/cancel`,

  //Payment
  CREATE_PAYMENT: `${BASE_URL}/payment/create`,
  CONFIRM_CASH_PAYMENT: `${BASE_URL}/payment/cash/confirm`,
  GET_PAYMENT: (id: number) => `${BASE_URL}/payment/${id}`,
  GET_PAYMENTS: `${BASE_URL}/payments`,

  // Notifications
  GET_NOTIFICATIONS: `${BASE_URL}/notifications`,
  GET_UNREAD_COUNT: `${BASE_URL}/notifications/unread-count`,
  MARK_AS_READ: (id: number) => `${BASE_URL}/notifications/${id}/read`,
  MARK_ALL_READ: `${BASE_URL}/notifications/read-all`,

  // Addresses
  GET_ADDRESS: (id: number) => `${BASE_URL}/address/${id}`,
  GET_ADDRESSES: `${BASE_URL}/addresses`,

  //Points
  MY_POINTS: `${BASE_URL}/points`,
};
