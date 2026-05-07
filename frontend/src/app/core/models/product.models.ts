export interface Product {
  id: string;
  sku: string;
  name: string;
  description: string | null;
  categoryId: string;
  categoryName: string;
  unitPrice: number;
  brand: string;
  productType: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateProductRequest {
  sku: string;
  name: string;
  description: string;
  categoryId: string;
  unitPrice: number;
  brand: string;
  productType: string;
}

export interface UpdateProductRequest {
  name: string;
  description: string;
  categoryId: string;
  unitPrice: number;
  brand: string;
  productType: string;
}
