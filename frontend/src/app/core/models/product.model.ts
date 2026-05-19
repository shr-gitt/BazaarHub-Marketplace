import { ProductStatus } from './enums.model';

export interface ProductRequest {
  name: string;
  description: string;
  price: number;
  discountPrice?: number | null;
  stockQuantity: number;
  categoryId: number;
  vendorId: number;
}

export interface ProductResponse {
  id: number;
  name: string;
  description: string;
  price: number;
  discountPrice?: number;
  stockQuantity: number;
  imageUrl?: string;

  categoryId: number;
  category: string;
  status: ProductStatus;
  vendorName: string;
  createdAt: string;
  modifiedAt: string;
}
