export interface Category {
  id: string;
  code: string;
  name: string;
  description: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCategoryRequest {
  code: string;
  name: string;
  description: string;
}

export interface UpdateCategoryRequest {
  name: string;
  description: string;
}
