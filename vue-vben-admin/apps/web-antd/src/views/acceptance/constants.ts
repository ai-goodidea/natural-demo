export const STATUS_OPTIONS = [
  { value: 'PENDING', label: '待验收', color: 'default' },
  { value: 'IN_PROGRESS', label: '验收中', color: 'processing' },
  { value: 'PASSED', label: '验收通过', color: 'success' },
  { value: 'RETURNED', label: '退货', color: 'error' },
  { value: 'RECTIFYING', label: '整改中', color: 'warning' },
  { value: 'RECTIFIED', label: '整改完成', color: 'cyan' },
] as const;

export const RESULT_OPTIONS = [
  { value: 'QUALIFIED', label: '合格', color: 'success' },
  { value: 'CONCESSION', label: '让步接收', color: 'warning' },
  { value: 'RETURNED', label: '退货', color: 'error' },
] as const;

export function statusLabel(s?: string) {
  return STATUS_OPTIONS.find((it) => it.value === s)?.label ?? s ?? '-';
}
export function statusColor(s?: string) {
  return STATUS_OPTIONS.find((it) => it.value === s)?.color ?? 'default';
}
export function resultLabel(s?: string) {
  return RESULT_OPTIONS.find((it) => it.value === s)?.label ?? s ?? '-';
}
export function resultColor(s?: string) {
  return RESULT_OPTIONS.find((it) => it.value === s)?.color ?? 'default';
}

export function isFinalStatus(s?: string) {
  return s === 'PASSED' || s === 'RETURNED';
}

/** 各状态允许流转到的状态 */
export const ALLOWED_TRANSITIONS: Record<string, string[]> = {
  PENDING: ['IN_PROGRESS'],
  IN_PROGRESS: ['PASSED', 'RETURNED', 'RECTIFYING'],
  RECTIFYING: ['RECTIFIED'],
  RECTIFIED: ['PASSED', 'RETURNED'],
  PASSED: [],
  RETURNED: [],
};
