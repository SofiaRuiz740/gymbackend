export interface ReportProduct {
  productId: string;
  sku: string;
  productName: string;
  categoryId: string;
  categoryName: string;
  currentStock: number;
  totalEntries: number;
  totalExits: number;
  active: boolean;
  updatedAt: string;
}

export interface MovementAuditRecord {
  movementId: string;
  productId: string;
  sku: string;
  productName: string;
  categoryName: string;
  movementType: string;
  quantity: number;
  resultingStock: number;
  reference: string | null;
  notes: string | null;
  registeredBy: string;
  occurredAt: string;
}

export interface MovementSummaryRecord {
  movementType: string;
  movementCount: number;
  totalUnits: number;
}
