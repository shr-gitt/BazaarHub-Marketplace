import { AddressRequest, AddressResponseDto } from './address.model';

export interface VendorRequest {
  shopName: string;
  businessEmail: string;
  businessPhone: string;
  panCardNo: string;
  registrationNo: string;
  addressRequestDto: AddressRequest;
}

export interface VendorResponse {
  id: number;
  userId?: number;
  shopName: string;
  businessEmail: string;
  businessPhone: string;
  panCardNo: string;
  registrationNo: string;
  address?: AddressResponseDto;
  approvalStatus: 'PENDING' | 'APPROVED' | 'REJECTED';
  approvedBy?: number;
  createdAt?: string;
  modifiedAt?: string;
}

export interface ApprovalRequest {
  approvalStatus: 'APPROVED' | 'REJECTED' | 'PENDING';
  rejectionReason?: string;
}
