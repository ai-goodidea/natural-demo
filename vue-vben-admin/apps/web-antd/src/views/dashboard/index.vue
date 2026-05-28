<script setup lang="ts">
import { computed, onMounted, ref, shallowRef } from 'vue';

import { Page } from '@vben/common-ui';

import { Card, Col, Row, Statistic } from 'ant-design-vue';
import * as echarts from 'echarts/core';
import { BarChart, PieChart } from 'echarts/charts';
import {
  GridComponent,
  LegendComponent,
  TitleComponent,
  TooltipComponent,
} from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

import { fetchDeviceTypeStats, fetchSupplierStats } from '#/api';
import type { DeviceTypeStats, SupplierStats } from '#/api/stats';

echarts.use([
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  CanvasRenderer,
]);

defineOptions({ name: 'AcceptanceOverview' });

const supplierStats = ref<SupplierStats[]>([]);
const deviceStats = ref<DeviceTypeStats[]>([]);

const barRef = ref<HTMLDivElement | null>(null);
const pieRef = ref<HTMLDivElement | null>(null);
const rateRef = ref<HTMLDivElement | null>(null);
const barChart = shallowRef<echarts.ECharts | null>(null);
const pieChart = shallowRef<echarts.ECharts | null>(null);
const rateChart = shallowRef<echarts.ECharts | null>(null);

const totalBatches = computed(() =>
  supplierStats.value.reduce((acc, s) => acc + s.batchCount, 0),
);
const totalPassed = computed(() =>
  supplierStats.value.reduce((acc, s) => acc + s.passedCount, 0),
);
const totalReturned = computed(() =>
  supplierStats.value.reduce((acc, s) => acc + s.returnedCount, 0),
);
const totalRectified = computed(() =>
  supplierStats.value.reduce((acc, s) => acc + s.rectifiedCount, 0),
);

async function reload() {
  const [a, b] = await Promise.all([
    fetchSupplierStats(),
    fetchDeviceTypeStats(),
  ]);
  supplierStats.value = a ?? [];
  deviceStats.value = b ?? [];
  renderCharts();
}

function renderCharts() {
  if (barRef.value && !barChart.value) {
    barChart.value = echarts.init(barRef.value);
  }
  if (pieRef.value && !pieChart.value) {
    pieChart.value = echarts.init(pieRef.value);
  }
  if (rateRef.value && !rateChart.value) {
    rateChart.value = echarts.init(rateRef.value);
  }

  barChart.value?.setOption({
    title: { text: '供应商到货批次分布', left: 'center', textStyle: { fontSize: 14 } },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { data: ['批次', '合格', '整改', '退货'], bottom: 0 },
    grid: { left: '5%', right: '4%', bottom: 60, top: 60, containLabel: true },
    xAxis: {
      type: 'category',
      data: supplierStats.value.map((s) => s.supplierName),
      axisLabel: { rotate: 20, fontSize: 11 },
    },
    yAxis: { type: 'value', name: '数量' },
    series: [
      { name: '批次', type: 'bar', data: supplierStats.value.map((s) => s.batchCount), itemStyle: { color: '#5470c6' } },
      { name: '合格', type: 'bar', data: supplierStats.value.map((s) => s.passedCount), itemStyle: { color: '#91cc75' } },
      { name: '整改', type: 'bar', data: supplierStats.value.map((s) => s.rectifiedCount), itemStyle: { color: '#fac858' } },
      { name: '退货', type: 'bar', data: supplierStats.value.map((s) => s.returnedCount), itemStyle: { color: '#ee6666' } },
    ],
  });

  rateChart.value?.setOption({
    title: { text: '供应商合格率', left: 'center', textStyle: { fontSize: 14 } },
    tooltip: { trigger: 'axis', formatter: '{b}: {c}%' },
    grid: { left: '5%', right: '4%', bottom: 60, top: 60, containLabel: true },
    xAxis: {
      type: 'category',
      data: supplierStats.value.map((s) => s.supplierName),
      axisLabel: { rotate: 20, fontSize: 11 },
    },
    yAxis: { type: 'value', name: '%', max: 100 },
    series: [
      {
        name: '合格率',
        type: 'bar',
        data: supplierStats.value.map((s) => s.passedRate),
        itemStyle: { color: '#73c0de' },
        label: { show: true, position: 'top', formatter: '{c}%' },
      },
    ],
  });

  pieChart.value?.setOption({
    title: { text: '设备类型到货数量', left: 'center', textStyle: { fontSize: 14 } },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [
      {
        name: '到货数量',
        type: 'pie',
        radius: ['40%', '70%'],
        data: deviceStats.value.map((d) => ({
          name: `${d.deviceType}（合格率${d.qualifiedRate}%）`,
          value: Number(d.totalQty),
        })),
      },
    ],
  });
}

window.addEventListener('resize', () => {
  barChart.value?.resize();
  rateChart.value?.resize();
  pieChart.value?.resize();
});

onMounted(reload);
</script>

<template>
  <Page>
    <Row :gutter="16">
      <Col :span="6">
        <Card><Statistic title="到货总批次" :value="totalBatches" /></Card>
      </Col>
      <Col :span="6">
        <Card><Statistic title="验收通过" :value="totalPassed" :value-style="{ color: '#52c41a' }" /></Card>
      </Col>
      <Col :span="6">
        <Card><Statistic title="整改批次" :value="totalRectified" :value-style="{ color: '#faad14' }" /></Card>
      </Col>
      <Col :span="6">
        <Card><Statistic title="退货批次" :value="totalReturned" :value-style="{ color: '#f5222d' }" /></Card>
      </Col>
    </Row>

    <Card class="mt-4" title="供应商维度统计">
      <Row :gutter="16">
        <Col :span="14">
          <div ref="barRef" style="height: 360px" />
        </Col>
        <Col :span="10">
          <div ref="rateRef" style="height: 360px" />
        </Col>
      </Row>
    </Card>

    <Card class="mt-4" title="设备类型维度统计">
      <div ref="pieRef" style="height: 360px" />
    </Card>
  </Page>
</template>
