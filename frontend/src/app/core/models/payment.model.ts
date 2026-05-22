import { PaymentStatus } from './enums.model';

export type PaymentType = 'CASH_ON_DELIVERY' | 'WALLET' | 'MOBILE_BANKING';

export interface PaymentRequest {
  orderId: number;
  paymentType: PaymentType; 
}

export interface CashPaymentConfirmRequest {
  paymentId: number;       
}

export interface PaymentResponse {
  id: number;
  orderId: number;
  userId: number;
  paymentType: PaymentType;
  paymentStatus: PaymentStatus;
  amount: number;
  createdAt: string;
  modifiedAt: string;
}