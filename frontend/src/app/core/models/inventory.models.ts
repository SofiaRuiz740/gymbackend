export type MovementType = 'ENTRY' | 'EXIT';

export interface StockItem {
  productId: string;
  sku: string;
  productName: string;
  categoryName: string;
  availableStock: number;
  active: boolean;
  updatedAt: string;
}

export interface InventoryMovement {
  id: string;
  productId: string;
  sku: string;
  productName: string;
  categoryName: string;
  movementType: MovementType;
  quantity: number;
  resultingStock: number;
  reference: string | null;
  notes: string | null;
  registeredBy: string;
  occurredAt: string;
}

export interface RegisterInventoryMovementRequest {
  productId: string;
  quantity: number;
  reference: string;
  notes: string;
}

export interface MovementRange {
  from?: string;
  to?: string;
}
