<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  Button,
  Card,
  Drawer,
  Form,
  FormItem,
  Input,
  InputNumber,
  message,
  Modal,
  Select,
  Space,
  Table,
  Tag,
} from 'ant-design-vue';

import {
  createSupplier,
  deleteSupplier,
  pageSupplier,
  type Supplier,
  updateSupplier,
} from '#/api';

defineOptions({ name: 'SupplierList' });

const loading = ref(false);
const dataSource = ref<Supplier[]>([]);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
});
const query = reactive({
  keyword: '',
});

const columns = [
  { title: '编码', dataIndex: 'code', width: 140 },
  { title: '名称', dataIndex: 'name', width: 220 },
  { title: '联系人', dataIndex: 'contact', width: 100 },
  { title: '电话', dataIndex: 'phone', width: 140 },
  { title: '地址', dataIndex: 'address' },
  { title: '信用分', dataIndex: 'creditScore', width: 90 },
  { title: '状态', dataIndex: 'status', width: 100 },
  { title: '操作', dataIndex: 'op', width: 160, fixed: 'right' as const },
];

const STATUS_OPTIONS = [
  { value: 1, label: '合作中', color: 'success' },
  { value: 0, label: '停用', color: 'default' },
];

function statusTag(s?: number) {
  return (
    STATUS_OPTIONS.find((it) => it.value === s) ?? {
      value: 1,
      label: '合作中',
      color: 'success',
    }
  );
}

async function load() {
  loading.value = true;
  try {
    const res = await pageSupplier({
      current: pagination.current,
      size: pagination.pageSize,
      keyword: query.keyword || undefined,
    });
    dataSource.value = res.records ?? [];
    pagination.total = res.total ?? 0;
  } finally {
    loading.value = false;
  }
}

onMounted(load);

function onSearch() {
  pagination.current = 1;
  load();
}
function reset() {
  query.keyword = '';
  pagination.current = 1;
  load();
}
function onPageChange(p: any) {
  pagination.current = p.current;
  pagination.pageSize = p.pageSize;
  load();
}

const drawerVisible = ref(false);
const drawerMode = ref<'create' | 'edit'>('create');
const submitting = ref(false);
const formState = reactive<Supplier>({
  id: undefined,
  code: '',
  name: '',
  contact: '',
  phone: '',
  address: '',
  creditScore: undefined,
  status: 1,
});

function openCreate() {
  drawerMode.value = 'create';
  Object.assign(formState, {
    id: undefined,
    code: '',
    name: '',
    contact: '',
    phone: '',
    address: '',
    creditScore: undefined,
    status: 1,
  });
  drawerVisible.value = true;
}

function openEdit(row: any) {
  drawerMode.value = 'edit';
  Object.assign(formState, {
    id: row.id,
    code: row.code,
    name: row.name,
    contact: row.contact ?? '',
    phone: row.phone ?? '',
    address: row.address ?? '',
    creditScore: row.creditScore as any,
    status: row.status ?? 1,
  });
  drawerVisible.value = true;
}

async function onSubmit() {
  if (!formState.code) {
    message.error('请填写供应商编码');
    return;
  }
  if (!formState.name) {
    message.error('请填写供应商名称');
    return;
  }
  submitting.value = true;
  try {
    const payload = { ...formState } as Supplier;
    if (drawerMode.value === 'create') {
      await createSupplier(payload);
      message.success('新增成功');
    } else {
      await updateSupplier(payload);
      message.success('更新成功');
    }
    drawerVisible.value = false;
    load();
  } finally {
    submitting.value = false;
  }
}

function confirmDelete(row: any) {
  Modal.confirm({
    title: '删除供应商',
    content: `确认删除 ${row.name} (${row.code}) 吗？`,
    onOk: async () => {
      const ok = await deleteSupplier(row.id!);
      if (ok === false) {
        message.error('删除失败，记录可能已不存在');
      } else {
        message.success('已删除');
      }
      load();
    },
  });
}
</script>

<template>
  <Page>
    <Card title="供应商管理">
      <template #extra>
        <Button type="primary" @click="openCreate">新增供应商</Button>
      </template>

      <Form layout="inline" class="mb-4">
        <FormItem label="关键字">
          <Input
            v-model:value="query.keyword"
            allow-clear
            placeholder="编码 / 名称 / 联系人"
            style="width: 240px"
            @press-enter="onSearch"
          />
        </FormItem>
        <FormItem>
          <Space>
            <Button type="primary" @click="onSearch">查询</Button>
            <Button @click="reset">重置</Button>
          </Space>
        </FormItem>
      </Form>

      <Table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="pagination"
        :scroll="{ x: 1100 }"
        row-key="id"
        @change="onPageChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'status'">
            <Tag :color="statusTag(record.status).color">
              {{ statusTag(record.status).label }}
            </Tag>
          </template>
          <template v-else-if="column.dataIndex === 'op'">
            <Space>
              <a class="vben-link" @click="openEdit(record)">编辑</a>
              <a
                class="text-destructive hover:text-destructive cursor-pointer"
                @click="confirmDelete(record)"
              >删除</a>
            </Space>
          </template>
        </template>
      </Table>
    </Card>

    <Drawer
      v-model:open="drawerVisible"
      :title="drawerMode === 'create' ? '新增供应商' : '编辑供应商'"
      :width="520"
      :mask-closable="false"
      destroy-on-close
    >
      <Form layout="vertical">
        <FormItem label="供应商编码" required>
          <Input
            v-model:value="formState.code"
            :disabled="drawerMode === 'edit'"
            placeholder="例：SUP-0001"
          />
        </FormItem>
        <FormItem label="供应商名称" required>
          <Input v-model:value="formState.name" placeholder="公司全称" />
        </FormItem>
        <FormItem label="联系人">
          <Input v-model:value="formState.contact" />
        </FormItem>
        <FormItem label="联系电话">
          <Input v-model:value="formState.phone" />
        </FormItem>
        <FormItem label="地址">
          <Input v-model:value="formState.address" />
        </FormItem>
        <FormItem label="信用分">
          <InputNumber
            v-model:value="formState.creditScore"
            :min="0"
            :max="100"
            :precision="1"
            style="width: 100%"
          />
        </FormItem>
        <FormItem label="状态">
          <Select
            v-model:value="formState.status"
            :options="STATUS_OPTIONS.map((s) => ({ value: s.value, label: s.label }))"
          />
        </FormItem>
      </Form>

      <template #footer>
        <Space style="float: right">
          <Button @click="drawerVisible = false">取消</Button>
          <Button type="primary" :loading="submitting" @click="onSubmit">
            保存
          </Button>
        </Space>
      </template>
    </Drawer>
  </Page>
</template>
