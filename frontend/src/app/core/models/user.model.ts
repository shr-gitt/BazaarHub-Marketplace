import { Gender, Role, UserStatus } from './enums.model';

export interface UserRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  gender: Gender;
  password: string;
  role: Role;
}

export interface UserResponse {
  id: number;
  version: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  gender: Gender;
  role: Role;
  userStatus: UserStatus;
  createdAt: string;
  modifiedDate: string;
}
