<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Page } from '@vben/common-ui';

import {
  Button,
  Card,
  Col,
  DatePicker,
  Descriptions,
  DescriptionsItem,
  Drawer,
  Form,
  FormItem,
  Input,
  InputNumber,
  message,
  Modal,
  Row,
  Select,
  Space,
  Spin,
  Table,
  Tabs,
  TabPane,
  Tag,
  Textarea,
  Timeline,
  TimelineItem,
} from 'ant-design-vue';

import {
  createAcceptance,
  deleteAcceptance,
  exportAcceptanceExcelUrl,
  getAcceptance,
  getAcceptanceLogs,
  pageAcceptance,
} from '#/api';
import {
  isFinalStatus,
  RESULT_OPTIONS,
  resultColor,
  resultLabel,
  STATUS_OPTIONS,
  statusColor,
  statusLabel,
} from './constants';
import EditDrawer from './edit-drawer.vue';

defineOptions({ name: 'AcceptanceList' });

const router = useRouter();
const loading = ref(false);
const dataSource = ref<any[]>([]);
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
});
const query = reactive({
  orderNo: '',
  supplierName: '',
  status: undefined as string | undefined,
});

const columns = [
  { title: '采购订单号', dataIndex: 'orderNo', width: 180 },
  { title: '供应商', dataIndex: 'supplierName', width: 220 },
  { title: '到货日期', dataIndex: 'arrivalDate', width: 120 },
  { title: '验收人', dataIndex: 'inspectorName', width: 100 },
  { title: '状态', dataIndex: 'status', width: 110 },
  { title: '操作', dataIndex: 'op', width: 200, fixed: 'right' as const },
];

async function load() {
  loading.value = true;
  try {
    const res = await pageAcceptance({
      current: pagination.current,
      size: pagination.pageSize,
      orderNo: query.orderNo || undefined,
      supplierName: query.supplierName || undefined,
      status: query.status,
    });
    dataSource.value = res.records ?? [];
    pagination.total = res.total ?? 0;
  } finally {
    loading.value = false;
  }
}

onMounted(load);

function reset() {
  query.orderNo = '';
  query.supplierName = '';
  query.status = undefined;
  pagination.current = 1;
  load();
}

function onSearch() {
  pagination.current = 1;
  load();
}

function onPageChange(p: any) {
  pagination.current = p.current;
  pagination.pageSize = p.pageSize;
  load();
}

function gotoDetail(id: number) {
  router.push({ path: `/acceptance/detail/${id}` });
}

// ---- 编辑抽屉 ----
const editDrawerVisible = ref(false);
const editingId = ref<number | undefined>(undefined);
function openEdit(row: any) {
  editingId.value = row.id;
  editDrawerVisible.value = true;
}
function onEditSaved() {
  load();
}

// ---- 查看抽屉（只读） ----
const viewDrawerVisible = ref(false);
const viewLoading = ref(false);
const viewHead = ref<any>({});
const viewItems = ref<any[]>([]);
const viewRectifications = ref<any[]>([]);
const viewLogs = ref<any[]>([]);
const viewSummary = ref<{
  conclusion?: string;
  conclusionText?: string;
  deviceSummary?: string;
  inspectionOverview?: string;
  issues?: string;
} | null>(null);

async function openView(row: any) {
  viewDrawerVisible.value = true;
  viewLoading.value = true;
  viewHead.value = {};
  viewItems.value = [];
  viewRectifications.value = [];
  viewLogs.value = [];
  viewSummary.value = null;
  try {
    const data = await getAcceptance(row.id);
    viewHead.value = data.head ?? {};
    viewItems.value = data.items ?? [];
    viewRectifications.value = data.rectifications ?? [];
    if (data.head?.summaryJson) {
      try {
        viewSummary.value = JSON.parse(data.head.summaryJson);
      } catch {
        /* ignore */
      }
    }
    viewLogs.value = await getAcceptanceLogs(row.id);
  } finally {
    viewLoading.value = false;
  }
}

const viewItemColumns = [
  { title: '设备编码', dataIndex: 'deviceCode', width: 130 },
  { title: '设备名称', dataIndex: 'deviceName', width: 150 },
  { title: '规格', dataIndex: 'spec', width: 120 },
  { title: '单位', dataIndex: 'unit', width: 70 },
  { title: '数量', dataIndex: 'qty', width: 80 },
  { title: '结果', dataIndex: 'result', width: 110 },
  { title: '缺陷描述', dataIndex: 'defectDesc' },
];

function confirmDelete(row: any) {
  Modal.confirm({
    title: '删除验收单',
    content: `确认删除单号 ${row.orderNo} 吗？`,
    onOk: async () => {
      await deleteAcceptance(row.id);
      message.success('已删除');
      load();
    },
  });
}

function doExport() {
  const a = document.createElement('a');
  a.href = exportAcceptanceExcelUrl();
  a.target = '_blank';
  a.click();
}

// ---- 新建验收单抽屉 ----
const drawerVisible = ref(false);
const submitting = ref(false);
const createForm = reactive({
  orderNo: '',
  supplierName: '',
  arrivalDate: '',
  inspectorName: '',
  remark: '',
});
const createItems = ref<any[]>([]);

function emptyItem() {
  return {
    deviceCode: '',
    deviceName: '',
    deviceType: '',
    spec: '',
    unit: '台',
    qty: 1,
    result: 'QUALIFIED',
    defectDesc: '',
  };
}

function openCreate() {
  Object.assign(createForm, {
    orderNo: '',
    supplierName: '',
    arrivalDate: '',
    inspectorName: '',
    remark: '',
  });
  createItems.value = [emptyItem()];
  drawerVisible.value = true;
}

function addItem() {
  createItems.value.push(emptyItem());
}
function removeItem(idx: number) {
  createItems.value.splice(idx, 1);
}

const itemColumns = [
  { title: '设备编码', dataIndex: 'deviceCode', width: 130 },
  { title: '设备名称', dataIndex: 'deviceName', width: 140 },
  { title: '规格', dataIndex: 'spec', width: 120 },
  { title: '单位', dataIndex: 'unit', width: 70 },
  { title: '数量', dataIndex: 'qty', width: 80 },
  { title: '结果', dataIndex: 'result', width: 120 },
  { title: '操作', dataIndex: 'op', width: 60 },
];

async function submitCreate() {
  if (!createForm.orderNo) {
    message.error('请填写采购订单号');
    return;
  }
  submitting.value = true;
  try {
    const payload = {
      ...createForm,
      status: 'PENDING',
      items: createItems.value,
    };
    const res = await createAcceptance(payload);
    message.success('已创建验收单');
    drawerVisible.value = false;
    load();
    // 创建后可选择继续完善：跳到详情页处理状态流转 / AI 摘要
    if (res?.head?.id) {
      Modal.confirm({
        title: '继续完善验收单？',
        content: '可前往详情页处理状态流转、AI 摘要、整改等。',
        okText: '前往详情',
        cancelText: '稍后再说',
        onOk: () => gotoDetail(res.head.id!),
      });
    }
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <Page>
    <Card title="验收单管理">
      <template #extra>
        <Space>
          <Button type="primary" @click="openCreate">新建验收单</Button>
          <Button @click="doExport">导出 Excel</Button>
        </Space>
      </template>

      <Form layout="inline" class="mb-4">
        <FormItem label="采购订单号">
          <Input v-model:value="query.orderNo" allow-clear placeholder="模糊搜索" />
        </FormItem>
        <FormItem label="供应商">
          <Input v-model:value="query.supplierName" allow-clear placeholder="模糊搜索" />
        </FormItem>
        <FormItem label="状态">
          <Select
            v-model:value="query.status"
            allow-clear
            placeholder="全部"
            style="width: 140px"
            :options="STATUS_OPTIONS.map((s) => ({ value: s.value, label: s.label }))"
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
            <Tag :color="statusColor(record.status)">{{ statusLabel(record.status) }}</Tag>
          </template>
          <template v-else-if="column.dataIndex === 'op'">
            <Space>
              <a class="vben-link" @click="openView(record)">查看</a>
              <a
                v-if="!isFinalStatus(record.status)"
                class="vben-link"
                @click="openEdit(record)"
              >编辑</a>
              <a
                v-if="!isFinalStatus(record.status)"
                class="vben-link"
                style="color: var(--ant-color-error)"
                @click="confirmDelete(record)"
              >删除</a>
            </Space>
          </template>
        </template>
      </Table>
    </Card>

    <Drawer
      v-model:open="drawerVisible"
      title="新建验收单"
      :width="880"
      :mask-closable="false"
      destroy-on-close
    >
      <Form layout="vertical">
        <Row :gutter="16">
          <Col :span="8">
            <FormItem label="采购订单号" required>
              <Input v-model:value="createForm.orderNo" placeholder="例：PO20260520-001" />
            </FormItem>
          </Col>
          <Col :span="8">
            <FormItem label="供应商">
              <Input v-model:value="createForm.supplierName" placeholder="供应商名称" />
            </FormItem>
          </Col>
          <Col :span="8">
            <FormItem label="到货日期">
              <DatePicker
                v-model:value="createForm.arrivalDate"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </FormItem>
          </Col>
          <Col :span="8">
            <FormItem label="验收人">
              <Input v-model:value="createForm.inspectorName" placeholder="姓名" />
            </FormItem>
          </Col>
          <Col :span="24">
            <FormItem label="备注">
              <Textarea
                v-model:value="createForm.remark"
                :rows="2"
                placeholder="描述本次到货验收的整体情况"
              />
            </FormItem>
          </Col>
        </Row>
      </Form>

      <div style="display: flex; align-items: center; justify-content: space-between; margin: 8px 0">
        <strong>验收明细</strong>
        <Button type="dashed" size="small" @click="addItem">+ 新增明细行</Button>
      </div>
      <Table
        :columns="itemColumns"
        :data-source="createItems"
        :pagination="false"
        size="small"
        row-key="deviceCode"
        :scroll="{ x: 720 }"
      >
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.dataIndex === 'deviceCode'">
            <Input v-model:value="record.deviceCode" size="small" />
          </template>
          <template v-else-if="column.dataIndex === 'deviceName'">
            <Input v-model:value="record.deviceName" size="small" />
          </template>
          <template v-else-if="column.dataIndex === 'spec'">
            <Input v-model:value="record.spec" size="small" />
          </template>
          <template v-else-if="column.dataIndex === 'unit'">
            <Input v-model:value="record.unit" size="small" />
          </template>
          <template v-else-if="column.dataIndex === 'qty'">
            <InputNumber v-model:value="record.qty" :min="0" size="small" style="width: 100%" />
          </template>
          <template v-else-if="column.dataIndex === 'result'">
            <Select
              v-model:value="record.result"
              size="small"
              :options="RESULT_OPTIONS.map((r) => ({ value: r.value, label: r.label }))"
              style="width: 100%"
            />
          </template>
          <template v-else-if="column.dataIndex === 'op'">
            <a
              class="vben-link"
              style="color: var(--ant-color-error)"
              @click="removeItem(index)"
            >删除</a>
          </template>
        </template>
      </Table>

      <template #footer>
        <Space style="float: right">
          <Button @click="drawerVisible = false">取消</Button>
          <Button type="primary" :loading="submitting" @click="submitCreate">保存</Button>
        </Space>
      </template>
    </Drawer>

    <Drawer
      v-model:open="viewDrawerVisible"
      :title="`验收单 ${viewHead.orderNo || ''}`"
      :width="880"
      destroy-on-close
    >
      <template #extra>
        <Tag :color="statusColor(viewHead.status)">{{ statusLabel(viewHead.status) }}</Tag>
      </template>

      <Spin :spinning="viewLoading">
        <Descriptions :column="2" bordered size="small">
          <DescriptionsItem label="采购订单号">{{ viewHead.orderNo || '-' }}</DescriptionsItem>
          <DescriptionsItem label="供应商">{{ viewHead.supplierName || '-' }}</DescriptionsItem>
          <DescriptionsItem label="到货日期">{{ viewHead.arrivalDate || '-' }}</DescriptionsItem>
          <DescriptionsItem label="验收人">{{ viewHead.inspectorName || '-' }}</DescriptionsItem>
          <DescriptionsItem label="状态">
            <Tag :color="statusColor(viewHead.status)">{{ statusLabel(viewHead.status) }}</Tag>
          </DescriptionsItem>
          <DescriptionsItem label="创建时间">{{ viewHead.createTime || '-' }}</DescriptionsItem>
          <DescriptionsItem :span="2" label="备注">{{ viewHead.remark || '-' }}</DescriptionsItem>
        </Descriptions>

        <Tabs class="mt-4">
          <TabPane key="items" tab="验收明细">
            <Table
              :columns="viewItemColumns"
              :data-source="viewItems"
              :pagination="false"
              size="small"
              row-key="id"
              :scroll="{ x: 720 }"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'result'">
                  <Tag :color="resultColor(record.result)">{{ resultLabel(record.result) }}</Tag>
                </template>
              </template>
            </Table>
          </TabPane>

          <TabPane key="summary" tab="AI 验收结论">
            <template v-if="viewSummary">
              <Descriptions :column="1" bordered size="small">
                <DescriptionsItem label="设备信息汇总">{{ viewSummary.deviceSummary || '-' }}</DescriptionsItem>
                <DescriptionsItem label="验收项目概述">{{ viewSummary.inspectionOverview || '-' }}</DescriptionsItem>
                <DescriptionsItem label="发现的问题">{{ viewSummary.issues || '-' }}</DescriptionsItem>
                <DescriptionsItem label="结论">{{ viewSummary.conclusion || '-' }}</DescriptionsItem>
                <DescriptionsItem label="结论描述">{{ viewSummary.conclusionText || '-' }}</DescriptionsItem>
              </Descriptions>
            </template>
            <template v-else>
              <p style="color: #999">暂无 AI 摘要</p>
            </template>
          </TabPane>

          <TabPane key="rect" tab="整改记录">
            <Table
              :columns="[
                { title: '内容', dataIndex: 'content' },
                { title: '责任人', dataIndex: 'owner', width: 120 },
                { title: '截止', dataIndex: 'deadline', width: 120 },
                {
                  title: '状态',
                  dataIndex: 'finished',
                  width: 100,
                  customRender: ({ text }: any) => (text ? '已完成' : '未完成'),
                },
                { title: '提交时间', dataIndex: 'createTime', width: 180 },
              ]"
              :data-source="viewRectifications"
              :pagination="false"
              size="small"
              row-key="id"
            />
          </TabPane>

          <TabPane key="log" tab="操作日志">
            <Timeline>
              <TimelineItem v-for="l in viewLogs" :key="l.id">
                <div>
                  <Tag color="blue">{{ l.opType }}</Tag>
                  <strong>{{ l.userName || '匿名' }}</strong>
                  <span style="color: #999; margin-left: 8px">{{ l.opTime }}</span>
                </div>
                <div>{{ l.summary }}</div>
              </TimelineItem>
            </Timeline>
          </TabPane>
        </Tabs>
      </Spin>

      <template #footer>
        <Space style="float: right">
          <Button @click="viewDrawerVisible = false">关闭</Button>
        </Space>
      </template>
    </Drawer>

    <EditDrawer
      v-model:open="editDrawerVisible"
      :id="editingId"
      @saved="onEditSaved"
    />
  </Page>
</template>
