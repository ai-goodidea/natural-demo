import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'mdi:truck-outline',
      order: 2,
      title: '供应商管理',
    },
    name: 'Supplier',
    path: '/supplier',
    children: [
      {
        name: 'SupplierList',
        path: '/supplier/list',
        component: () => import('#/views/supplier/list.vue'),
        meta: {
          icon: 'mdi:format-list-bulleted',
          title: '供应商列表',
        },
      },
    ],
  },
];

export default routes;
