import { requestClient } from '#/api/request';

export interface AcceptanceItem {
  id?: number;
  acceptanceId?: number;
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
  id?: number;
  orderNo: string;
  supplierId?: number;
  supplierName?: string;
  arrivalDate?: string;
  inspectorId?: number;
  inspectorName?: string;
  status?: string;
  remark?: string;
  summaryJson?: string;
  createTime?: string;
  updateTime?: string;
}

export interface Rectification {
  id?: number;
  acceptanceId: number;
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

export function getAcceptance(id: number) {
  return requestClient.get<AcceptanceVO>(`/business/acceptance/${id}`);
}

export function createAcceptance(payload: any) {
  return requestClient.post<AcceptanceVO>('/business/acceptance', payload);
}

export function updateAcceptance(payload: any) {
  return requestClient.put<AcceptanceVO>('/business/acceptance', payload);
}

export function deleteAcceptance(id: number) {
  return requestClient.delete<void>(`/business/acceptance/${id}`);
}

export function changeAcceptanceStatus(id: number, payload: {
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

export function aiSummarize(id: number) {
  return requestClient.post<AcceptanceSummary>(
    `/business/acceptance/${id}/ai-summary`,
  );
}

export function saveSummary(id: number, payload: AcceptanceSummary) {
  return requestClient.post<AcceptanceVO>(
    `/business/acceptance/${id}/save-summary`,
    payload,
  );
}

export function getAcceptanceLogs(id: number) {
  return requestClient.get<any[]>(`/business/acceptance/${id}/logs`);
}

export function exportAcceptanceExcelUrl() {
  // 直接通过 location 触发下载即可；需 token 时改用 fetch + blob
  return '/api/business/acceptance/export';
}
