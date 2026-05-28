import { requestClient } from '#/api/request';

export interface Supplier {
  id?: number;
  code: string;
  name: string;
  contact?: string;
  phone?: string;
  address?: string;
  creditScore?: number | string;
  /** 1=合作中 0=停用 */
  status?: number;
  createTime?: string;
  updateTime?: string;
}

export interface SupplierPageResp {
  records: Supplier[];
  total: number;
  size: number;
  current: number;
}

export function pageSupplier(params: {
  current?: number;
  size?: number;
  keyword?: string;
}) {
  return requestClient.get<SupplierPageResp>('/supplier/page', { params });
}

export function getSupplier(id: number) {
  return requestClient.get<Supplier>(`/supplier/${id}`);
}

export function createSupplier(payload: Supplier) {
  return requestClient.post<Supplier>('/supplier', payload);
}

export function updateSupplier(payload: Supplier) {
  return requestClient.put<Supplier>('/supplier', payload);
}

export function deleteSupplier(id: number) {
  return requestClient.delete<boolean>(`/supplier/${id}`);
}
