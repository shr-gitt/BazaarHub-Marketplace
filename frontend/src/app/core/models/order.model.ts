import { OrderStatus, PaymentStatus } from './enums.model';

export interface OrderRequest {
  shippingAddress: string;
  contactNumber: string;
  remark?: string;
}

export interface OrderStatusUpdateRequest {
  orderStatus: OrderStatus;
}

export interface OrderItemResponse {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  pricePerUnit: number;
  totalPrice: number;
}

export interface OrderResponse {
  id: number;
  userId: number;
  items: OrderItemResponse[];
  totalAmount: number;
  orderStatus: OrderStatus;
  paymentStatus: PaymentStatus;
  shippingAddress: string;
  contactNumber: string;
  remarks: string;
  createdAt: string;
  modifiedAt: string;
}
