import { CategoryStatus } from './enums.model';

export interface CategoryRequest {
  name: string;
  description?: string;
}

export interface CategoryResponse {
  id: number;
  version: number;
  name: string;
  description: string;
  status: CategoryStatus;
  createdAt: string;
  modifiedAt: string;
}
