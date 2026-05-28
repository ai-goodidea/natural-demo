import type { RouteRecordStringComponent } from '@vben/types';

import { requestClient } from '#/api/request';

/**
 * 后端目前只返回空数组，菜单走前端静态路由，本接口保留是为了和 vben 框架的菜单守卫保持兼容。
 */
export async function getAllMenusApi() {
  return requestClient.get<RouteRecordStringComponent[]>('/user/auth/menu');
}
