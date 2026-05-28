import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'mdi:clipboard-check-outline',
      order: 1,
      title: '验收管理',
    },
    name: 'Acceptance',
    path: '/acceptance',
    children: [
      {
        name: 'AcceptanceList',
        path: '/acceptance/list',
        component: () => import('#/views/acceptance/list.vue'),
        meta: {
          icon: 'mdi:format-list-bulleted',
          title: '验收单列表',
        },
      },
      {
        name: 'AcceptanceDetail',
        path: '/acceptance/detail/:id?',
        component: () => import('#/views/acceptance/detail.vue'),
        meta: {
          icon: 'mdi:file-document-edit-outline',
          title: '验收单详情',
          hideInMenu: true,
          activePath: '/acceptance/list',
        },
      },
    ],
  },
];

export default routes;
