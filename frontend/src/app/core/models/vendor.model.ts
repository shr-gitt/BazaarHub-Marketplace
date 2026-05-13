import { AddressRequest, AddressResponseDto } from './address.model';

export interface Vendor {
  addressResponseDto: AddressRequest;
  id: number;
  userId: number;
  shopName: string;
  panCardNo: string;
  registrationNo: string;
  addressRequestDto: AddressRequest;
  businessEmail: string;
  businessPhone: string;
  approvalStatus: 'PENDING' | 'APPROVED' | 'REJECTED';
  approvedBy?: number;
  createdAt?: string;
  modifiedAt?: string;
}

export interface VendorRequest {
  shopName: string;
  businessEmail: string;
  businessPhone: string;
  panCardNo: string;
  registrationNo: string;
  addressRequestDto: AddressRequest;
}

export interface VendorResponse{
  shopName: string;
  businessEmail: string;
  businessPhone: string;
  panCardNo: string;
  registrationNo: string;
  addressResponseDto: AddressResponseDto;
}

export interface ApprovalRequest {
  approvalStatus: 'APPROVED' | 'REJECTED';
  rejectionReason?: string;
}
