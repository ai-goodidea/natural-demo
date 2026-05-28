<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

import { Select } from 'ant-design-vue';

import { pageSupplier, type Supplier } from '#/api';

defineOptions({ name: 'SupplierSelect' });

const props = withDefaults(
  defineProps<{
    /** 当前选中的供应商 ID（v-model:value） */
    value?: number | string;
    /** 当前选中的供应商名称（v-model:name） */
    name?: string;
    disabled?: boolean;
    placeholder?: string;
    /** 一次拉取多少条候选 */
    pageSize?: number;
  }>(),
  {
    pageSize: 50,
    placeholder: '输入名称/编码/联系人搜索供应商',
  },
);

const emit = defineEmits<{
  (e: 'update:value', v: number | string | undefined): void;
  (e: 'update:name', v: string | undefined): void;
  (e: 'change', supplier: Supplier | undefined): void;
}>();

const loading = ref(false);
const records = ref<Supplier[]>([]);
let timer: ReturnType<typeof setTimeout> | undefined;

async function fetchSuppliers(keyword?: string) {
  loading.value = true;
  try {
    const res = await pageSupplier({
      current: 1,
      size: props.pageSize,
      keyword,
    });
    records.value = res.records ?? [];
  } finally {
    loading.value = false;
  }
}

onMounted(() => fetchSuppliers());

function onSearch(kw: string) {
  if (timer) clearTimeout(timer);
  timer = setTimeout(() => fetchSuppliers(kw?.trim() || undefined), 300);
}

/**
 * 当前 value 但不在 records 里时（编辑回显场景，候选不命中），补一条占位。
 * 用 String() 兼容 number / string id。
 */
const options = computed(() => {
  const list = records.value.map((s) => ({
    value: s.id,
    label: s.code ? `${s.name}（${s.code}）` : (s.name ?? ''),
    name: s.name,
    raw: s,
  }));
  if (
    props.value !== undefined &&
    props.value !== null &&
    !list.some((o) => String(o.value) === String(props.value))
  ) {
    list.unshift({
      value: props.value,
      label: props.name ?? String(props.value),
      name: props.name ?? String(props.value),
      raw: {
        id: props.value,
        code: '',
        name: props.name ?? String(props.value),
      } as Supplier,
    });
  }
  return list;
});

function onChange(v: number | string | undefined) {
  const hit = options.value.find((o) => String(o.value) === String(v));
  emit('update:value', v);
  emit('update:name', hit?.name);
  emit('change', hit?.raw);
}
</script>

<template>
  <Select
    :value="value"
    :loading="loading"
    :disabled="disabled"
    :placeholder="placeholder"
    :options="options"
    :field-names="{ label: 'label', value: 'value' }"
    show-search
    allow-clear
    :filter-option="false"
    :default-active-first-option="false"
    style="width: 100%"
    @search="onSearch"
    @change="onChange"
  />
</template>
