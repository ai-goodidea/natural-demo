import { useAccessStore } from '@vben/stores';

import { requestClient } from '#/api/request';

export interface AcceptanceItem {
  id?: number | string;
  acceptanceId?: number | string;
  deviceCode: string;
  deviceName: string;
  deviceType?: string;
  spec?: string;
  unit?: string;
  qty: number;
  result: 'CONCESSION' | 'QUALIFIED' | 'RETURNED';
  defectDesc?: string;
}

export interface AcceptanceHead {
  id?: number | string;
  orderNo: string;
  supplierId?: number | string;
  supplierName?: string;
  arrivalDate?: string;
  inspectorId?: number | string;
  inspectorName?: string;
  status?: string;
  remark?: string;
  summaryJson?: string;
  createTime?: string;
  updateTime?: string;
}

export interface Rectification {
  id?: number | string;
  acceptanceId: number | string;
  content: string;
  owner?: string;
  deadline?: string;
  finished: number;
  createTime?: string;
}

export interface AcceptanceVO {
  head: AcceptanceHead;
  items: AcceptanceItem[];
  rectifications: Rectification[];
}

export interface AcceptanceSummary {
  deviceSummary: string;
  inspectionOverview: string;
  issues: string;
  conclusion: 'CONCESSION' | 'QUALIFIED' | 'RETURNED';
  conclusionText: string;
}

export interface PageResp<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
}

export function pageAcceptance(params: {
  current?: number;
  size?: number;
  orderNo?: string;
  supplierName?: string;
  status?: string;
}) {
  return requestClient.get<PageResp<AcceptanceHead>>(
    '/business/acceptance/page',
    { params },
  );
}

export function getAcceptance(id: number | string) {
  return requestClient.get<AcceptanceVO>(`/business/acceptance/${id}`);
}

export function createAcceptance(payload: any) {
  return requestClient.post<AcceptanceVO>('/business/acceptance', payload);
}

export function updateAcceptance(payload: any) {
  return requestClient.put<AcceptanceVO>('/business/acceptance', payload);
}

export function deleteAcceptance(id: number | string) {
  return requestClient.delete<void>(`/business/acceptance/${id}`);
}

export function changeAcceptanceStatus(id: number | string, payload: {
  newStatus: string;
  rectificationContent?: string;
  rectificationOwner?: string;
  rectificationDeadline?: string;
}) {
  return requestClient.request<AcceptanceVO>(
    `/business/acceptance/${id}/status`,
    { method: 'PATCH', data: payload },
  );
}

export function aiSummarize(id: number | string) {
  return requestClient.post<AcceptanceSummary>(
    `/business/acceptance/${id}/ai-summary`,
  );
}

export function saveSummary(id: number | string, payload: AcceptanceSummary) {
  return requestClient.post<AcceptanceVO>(
    `/business/acceptance/${id}/save-summary`,
    payload,
  );
}

export function getAcceptanceLogs(id: number | string) {
  return requestClient.get<any[]>(`/business/acceptance/${id}/logs`);
}

export interface AcceptanceExportQuery {
  orderNo?: string;
  supplierName?: string;
  status?: string;
}

/**
 * 导出验收单 Excel（含验收单头信息与明细行）。
 * 使用 fetch 携带 Bearer Token，与列表查询条件一致；返回 Blob 以便前端触发下载。
 */
export async function exportAcceptanceExcel(
  params: AcceptanceExportQuery = {},
): Promise<Blob> {
  const accessStore = useAccessStore();
  const token = accessStore.accessToken;
  const baseURL =
    (import.meta.env.VITE_GLOB_API_URL as string | undefined) ?? '/api';
  const search = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') {
      search.append(k, String(v));
    }
  });
  const qs = search.toString();
  const url = `${baseURL.replace(/\/$/, '')}/business/acceptance/export${
    qs ? `?${qs}` : ''
  }`;

  const resp = await fetch(url, {
    method: 'GET',
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
  if (!resp.ok) {
    throw new Error(`导出失败：HTTP ${resp.status}`);
  }
  return resp.blob();
}

/**
 * 把后端响应的 Blob 触发为浏览器下载。
 */
export function triggerDownload(blob: Blob, filename: string) {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.append(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
}
