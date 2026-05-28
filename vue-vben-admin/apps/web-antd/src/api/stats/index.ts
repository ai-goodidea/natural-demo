import { requestClient } from '#/api/request';

export interface SupplierStats {
  supplierName: string;
  batchCount: number;
  passedCount: number;
  rectifiedCount: number;
  returnedCount: number;
  passedRate: number;
  rectifyRate: number;
  returnRate: number;
}

export interface DeviceTypeStats {
  deviceType: string;
  totalQty: number;
  qualifiedQty: number;
  qualifiedRate: number;
}

export function fetchSupplierStats() {
  return requestClient.get<SupplierStats[]>('/business/stats/supplier');
}

export function fetchDeviceTypeStats() {
  return requestClient.get<DeviceTypeStats[]>('/business/stats/device-type');
}
