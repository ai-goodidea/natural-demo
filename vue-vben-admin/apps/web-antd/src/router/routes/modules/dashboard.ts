import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:layout-dashboard',
      order: -1,
      title: '验收统计看板',
    },
    name: 'Dashboard',
    path: '/dashboard',
    component: () => import('#/views/dashboard/index.vue'),
    children: [
      {
        name: 'AcceptanceOverview',
        path: '/dashboard',
        component: () => import('#/views/dashboard/index.vue'),
        meta: {
          affixTab: true,
          icon: 'lucide:area-chart',
          title: '验收统计看板',
          hideChildrenInMenu: true,
        },
      },
    ],
  },
];

export default routes;
