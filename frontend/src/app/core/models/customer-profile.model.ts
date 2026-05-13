import { AddressResponseDto } from './address.model';

export interface CustomerProfileRequestDto {
  dateOfBirth: string;
  preferences: number[];
  addressRequestDto: {
    province: string;
    district: string;
    municipality: string;
    wardNo: number;
    street: string;
    postalCode: string;
  };
}

export interface CustomerProfileResponseDto {
  id: number;
  dateOfBirth: string;
  preferences: number[];
  profileImageUrl?: string;
  addressResponseDto: AddressResponseDto;
}
