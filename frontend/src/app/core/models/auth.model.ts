export enum Role {
  CUSTOMER = 'CUSTOMER',
  VENDOR = 'VENDOR',
  ADMIN = 'ADMIN',
}

export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  OTHER = 'OTHER',
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  gender: Gender;
  password: string;
  role: Role;
}

export interface AuthResponse {
  message: string;
  userId: number;
  role: Role;
  token: string;
}
