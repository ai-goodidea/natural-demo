<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';

import {
  Button,
  Col,
  DatePicker,
  Drawer,
  Form,
  FormItem,
  Input,
  InputNumber,
  message,
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
  aiSummarize,
  changeAcceptanceStatus,
  getAcceptance,
  getAcceptanceLogs,
  saveSummary,
  updateAcceptance,
} from '#/api';

import {
  ALLOWED_TRANSITIONS,
  isFinalStatus,
  RESULT_OPTIONS,
  resultColor,
  resultLabel,
  statusColor,
  statusLabel,
} from './constants';
import SupplierSelect from './supplier-select.vue';

defineOptions({ name: 'AcceptanceEditDrawer' });

const props = defineProps<{
  id?: number | string;
  open: boolean;
}>();
const emit = defineEmits<{
  (e: 'update:open', v: boolean): void;
  (e: 'saved'): void;
}>();

const loading = ref(false);
const aiLoading = ref(false);
const form = reactive({
  id: undefined as number | string | undefined,
  orderNo: '',
  supplierId: undefined as number | string | undefined,
  supplierName: '',
  arrivalDate: '',
  inspectorName: '',
  status: 'PENDING',
  remark: '',
  summaryJson: '' as string | undefined,
});
const items = ref<any[]>([]);
const rectifications = ref<any[]>([]);
const logs = ref<any[]>([]);
const summary = reactive({
  deviceSummary: '',
  inspectionOverview: '',
  issues: '',
  conclusion: '' as '' | 'CONCESSION' | 'QUALIFIED' | 'RETURNED',
  conclusionText: '',
});

const editable = computed(() => !isFinalStatus(form.status));
const allowedNextStatus = computed(() => ALLOWED_TRANSITIONS[form.status] ?? []);

// 状态切换的二级弹层（用 ant Modal 嵌在 Drawer 里）
const statusChangeVisible = ref(false);
const statusChangeTarget = ref<string>('');
const statusChangeForm = reactive({
  rectificationContent: '',
  rectificationOwner: '',
  rectificationDeadline: '',
});

function resetState() {
  Object.assign(form, {
    id: undefined,
    orderNo: '',
    supplierId: undefined,
    supplierName: '',
    arrivalDate: '',
    inspectorName: '',
    status: 'PENDING',
    remark: '',
    summaryJson: '',
  });
  Object.assign(summary, {
    deviceSummary: '',
    inspectionOverview: '',
    issues: '',
    conclusion: '',
    conclusionText: '',
  });
  items.value = [];
  rectifications.value = [];
  logs.value = [];
}

async function load() {
  if (!props.id) {
    resetState();
    return;
  }
  loading.value = true;
  try {
    const data = await getAcceptance(props.id);
    Object.assign(form, data.head);
    items.value = (data.items ?? []).map((it: any) => ({ ...it }));
    rectifications.value = data.rectifications ?? [];
    if (form.summaryJson) {
      try {
        Object.assign(summary, JSON.parse(form.summaryJson));
      } catch {
        /* ignore */
      }
    }
    logs.value = await getAcceptanceLogs(props.id);
  } finally {
    loading.value = false;
  }
}

async function loadLogs() {
  if (!props.id) return;
  logs.value = await getAcceptanceLogs(props.id);
}

watch(
  () => [props.open, props.id],
  ([isOpen]) => {
    if (isOpen) {
      load();
    }
  },
);

function close() {
  emit('update:open', false);
}

function addItem() {
  items.value.push({
    deviceCode: '',
    deviceName: '',
    deviceType: '',
    spec: '',
    unit: '台',
    qty: 1,
    result: 'QUALIFIED',
    defectDesc: '',
  });
}
function removeItem(idx: number) {
  items.value.splice(idx, 1);
}

async function onSave() {
  if (!form.orderNo) {
    message.error('请填写采购订单号');
    return;
  }
  loading.value = true;
  try {
    const payload = { ...form, items: items.value };
    const res = await updateAcceptance(payload);
    Object.assign(form, res.head);
    items.value = (res.items ?? []).map((it: any) => ({ ...it }));
    rectifications.value = res.rectifications ?? [];
    message.success('保存成功');
    await loadLogs();
    emit('saved');
  } finally {
    loading.value = false;
  }
}

function openStatusChange(target: string) {
  statusChangeTarget.value = target;
  statusChangeForm.rectificationContent = '';
  statusChangeForm.rectificationOwner = '';
  statusChangeForm.rectificationDeadline = '';
  statusChangeVisible.value = true;
}

async function confirmStatusChange() {
  if (
    statusChangeTarget.value === 'RECTIFYING' &&
    !statusChangeForm.rectificationContent
  ) {
    message.error('进入整改中必须填写整改内容');
    return;
  }
  if (!props.id) {
    message.error('请先保存再切换状态');
    return;
  }
  const res = await changeAcceptanceStatus(props.id, {
    newStatus: statusChangeTarget.value,
    ...statusChangeForm,
  });
  Object.assign(form, res.head);
  rectifications.value = res.rectifications ?? [];
  statusChangeVisible.value = false;
  message.success('状态已更新');
  await loadLogs();
  emit('saved');
}

async function genSummary() {
  if (!props.id) {
    message.error('请先保存验收单');
    return;
  }
  aiLoading.value = true;
  try {
    const r = await aiSummarize(props.id);
    Object.assign(summary, r);
    message.success('已生成 AI 摘要，可继续编辑');
  } catch (e: any) {
    message.error(e?.message || 'AI 生成失败');
  } finally {
    aiLoading.value = false;
  }
}

async function persistSummary() {
  if (!props.id) {
    message.error('请先保存验收单');
    return;
  }
  await saveSummary(props.id, {
    deviceSummary: summary.deviceSummary,
    inspectionOverview: summary.inspectionOverview,
    issues: summary.issues,
    conclusion: summary.conclusion as any,
    conclusionText: summary.conclusionText,
  });
  message.success('已保存验收摘要');
  await loadLogs();
}

const itemColumns = [
  { title: '设备编码', dataIndex: 'deviceCode', width: 130 },
  { title: '设备名称', dataIndex: 'deviceName', width: 140 },
  { title: '设备类型', dataIndex: 'deviceType', width: 110 },
  { title: '规格型号', dataIndex: 'spec', width: 130 },
  { title: '单位', dataIndex: 'unit', width: 70 },
  { title: '数量', dataIndex: 'qty', width: 80 },
  { title: '验收结果', dataIndex: 'result', width: 120 },
  { title: '缺陷描述', dataIndex: 'defectDesc' },
  { title: '操作', dataIndex: 'op', width: 60, fixed: 'right' as const },
];
</script>

<template>
  <Drawer
    :open="open"
    :title="`编辑验收单 ${form.orderNo || ''}`"
    :width="1080"
    :mask-closable="false"
    destroy-on-close
    @update:open="(v) => emit('update:open', v)"
  >
    <template #extra>
      <Space>
        <Tag :color="statusColor(form.status)">{{ statusLabel(form.status) }}</Tag>
        <Button v-if="editable" type="primary" @click="onSave">保存</Button>
        <Button
          v-for="s in allowedNextStatus"
          :key="s"
          :type="s === 'PASSED' ? 'primary' : 'default'"
          :danger="s === 'RETURNED'"
          @click="openStatusChange(s)"
        >
          {{ statusLabel(s) }}
        </Button>
      </Space>
    </template>

    <Spin :spinning="loading">
      <Form layout="vertical" :disabled="!editable">
        <Row :gutter="16">
          <Col :span="6">
            <FormItem label="采购订单号" required>
              <Input v-model:value="form.orderNo" placeholder="例：PO20260520-001" />
            </FormItem>
          </Col>
          <Col :span="8">
            <FormItem label="供应商">
              <SupplierSelect
                v-model:value="form.supplierId"
                v-model:name="form.supplierName"
                :disabled="!editable"
              />
            </FormItem>
          </Col>
          <Col :span="5">
            <FormItem label="到货日期">
              <DatePicker
                v-model:value="form.arrivalDate"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </FormItem>
          </Col>
          <Col :span="5">
            <FormItem label="验收人">
              <Input v-model:value="form.inspectorName" placeholder="姓名" />
            </FormItem>
          </Col>
          <Col :span="24">
            <FormItem label="验收记录（备注）">
              <Textarea
                v-model:value="form.remark"
                :rows="2"
                placeholder="描述本次到货验收的整体情况，AI 摘要会以此为依据"
              />
            </FormItem>
          </Col>
        </Row>
      </Form>

      <div style="display: flex; align-items: center; justify-content: space-between; margin: 8px 0">
        <strong>验收明细</strong>
        <Button v-if="editable" type="dashed" size="small" @click="addItem">
          + 新增明细行
        </Button>
      </div>
      <Table
        :columns="itemColumns"
        :data-source="items"
        :pagination="false"
        size="small"
        row-key="deviceCode"
        :scroll="{ x: 980 }"
      >
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.dataIndex === 'deviceCode'">
            <Input v-model:value="record.deviceCode" :disabled="!editable" size="small" />
          </template>
          <template v-else-if="column.dataIndex === 'deviceName'">
            <Input v-model:value="record.deviceName" :disabled="!editable" size="small" />
          </template>
          <template v-else-if="column.dataIndex === 'deviceType'">
            <Input v-model:value="record.deviceType" :disabled="!editable" size="small" />
          </template>
          <template v-else-if="column.dataIndex === 'spec'">
            <Input v-model:value="record.spec" :disabled="!editable" size="small" />
          </template>
          <template v-else-if="column.dataIndex === 'unit'">
            <Input v-model:value="record.unit" :disabled="!editable" size="small" />
          </template>
          <template v-else-if="column.dataIndex === 'qty'">
            <InputNumber
              v-model:value="record.qty"
              :min="0"
              :disabled="!editable"
              size="small"
              style="width: 100%"
            />
          </template>
          <template v-else-if="column.dataIndex === 'result'">
            <Select
              v-if="editable"
              v-model:value="record.result"
              size="small"
              :options="RESULT_OPTIONS.map((r) => ({ value: r.value, label: r.label }))"
              style="width: 100%"
            />
            <Tag v-else :color="resultColor(record.result)">
              {{ resultLabel(record.result) }}
            </Tag>
          </template>
          <template v-else-if="column.dataIndex === 'defectDesc'">
            <Input v-model:value="record.defectDesc" :disabled="!editable" size="small" />
          </template>
          <template v-else-if="column.dataIndex === 'op'">
            <a
              v-if="editable"
              class="text-destructive hover:text-destructive cursor-pointer"
              @click="removeItem(index)"
            >删除</a>
          </template>
        </template>
      </Table>

      <Tabs class="mt-4">
        <TabPane key="summary" tab="AI 验收结论">
          <Space class="mb-3">
            <Button
              type="primary"
              :loading="aiLoading"
              :disabled="!editable"
              @click="genSummary"
            >生成摘要（调用大模型）</Button>
            <Button
              :disabled="!summary.conclusion || !editable"
              @click="persistSummary"
            >保存摘要</Button>
          </Space>
          <Form layout="vertical" :disabled="!editable">
            <FormItem label="设备信息汇总">
              <Textarea v-model:value="summary.deviceSummary" :rows="2" />
            </FormItem>
            <FormItem label="验收项目概述">
              <Textarea v-model:value="summary.inspectionOverview" :rows="3" />
            </FormItem>
            <FormItem label="发现的问题">
              <Textarea v-model:value="summary.issues" :rows="2" />
            </FormItem>
            <Row :gutter="16">
              <Col :span="8">
                <FormItem label="结论">
                  <Select
                    v-model:value="summary.conclusion"
                    :options="[
                      { value: 'QUALIFIED', label: '合格' },
                      { value: 'CONCESSION', label: '让步接收' },
                      { value: 'RETURNED', label: '退货' },
                    ]"
                  />
                </FormItem>
              </Col>
              <Col :span="16">
                <FormItem label="结论描述">
                  <Input v-model:value="summary.conclusionText" />
                </FormItem>
              </Col>
            </Row>
          </Form>
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
            :data-source="rectifications"
            :pagination="false"
            size="small"
            row-key="id"
          />
        </TabPane>

        <TabPane key="log" tab="操作日志">
          <Timeline>
            <TimelineItem v-for="l in logs" :key="l.id">
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
        <Button @click="close">关闭</Button>
        <Button v-if="editable" type="primary" :loading="loading" @click="onSave">
          保存
        </Button>
      </Space>
    </template>

    <a-modal
      v-model:open="statusChangeVisible"
      :title="`流转到：${statusLabel(statusChangeTarget)}`"
      @ok="confirmStatusChange"
    >
      <Form layout="vertical">
        <template v-if="statusChangeTarget === 'RECTIFYING'">
          <FormItem label="整改内容" required>
            <Textarea v-model:value="statusChangeForm.rectificationContent" :rows="3" />
          </FormItem>
          <Row :gutter="16">
            <Col :span="12">
              <FormItem label="责任人">
                <Input v-model:value="statusChangeForm.rectificationOwner" />
              </FormItem>
            </Col>
            <Col :span="12">
              <FormItem label="截止日期">
                <DatePicker
                  v-model:value="statusChangeForm.rectificationDeadline"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </FormItem>
            </Col>
          </Row>
        </template>
        <template v-else>
          <p>
            确认将状态切换为
            <Tag :color="statusColor(statusChangeTarget)">
              {{ statusLabel(statusChangeTarget) }}
            </Tag>
            ？
          </p>
          <p
            v-if="statusChangeTarget === 'PASSED' || statusChangeTarget === 'RETURNED'"
            style="color: var(--ant-color-warning)"
          >
            该状态为终态，验收单不可再编辑。
          </p>
        </template>
      </Form>
    </a-modal>
  </Drawer>
</template>
