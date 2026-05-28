<script lang="ts" setup>
import { onBeforeUnmount, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

import { MobileOutlined, SafetyOutlined } from '@ant-design/icons-vue';
import { Form, message } from 'ant-design-vue';

import { useAuthStore } from '#/store';

defineOptions({ name: 'CodeLogin' });

const router = useRouter();
const authStore = useAuthStore();

const formRef = ref();

const useForm = Form.useForm;
const modelRef: any = reactive({
  mobile: '',
  code: '',
});
const rulesRef = reactive({
  mobile: [
    { required: true, message: '手机号不能为空', trigger: 'blur' },
    {
      pattern: /^1\d{10}$/,
      message: '请输入正确的手机号',
      trigger: 'blur',
    },
  ],
  code: [{ required: true, message: '验证码不能为空', trigger: 'blur' }],
});
useForm(modelRef, rulesRef);

const COUNTDOWN_SECONDS = 60;
const countdown = ref(0);
let timer: null | ReturnType<typeof setInterval> = null;

function startCountdown() {
  countdown.value = COUNTDOWN_SECONDS;
  timer = setInterval(() => {
    countdown.value -= 1;
    if (countdown.value <= 0 && timer) {
      clearInterval(timer);
      timer = null;
    }
  }, 1000);
}

onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
});

function handleSendCode() {
  if (countdown.value > 0) return;
  formRef.value
    .validate(['mobile'])
    .then(() => {
      message.success(`验证码已发送至 ${modelRef.mobile}`);
      startCountdown();
    })
    .catch(() => {});
}

function onSubmit() {
  formRef.value
    .validate()
    .then(() => {
      const data = { ...modelRef };
      authStore.authLogin(data);
    })
    .catch(() => {});
}

function handleGo(path: string) {
  router.push(path);
}
</script>

<template>
  <div>
    <a-spin :spinning="authStore.loginLoading">
      <div style="padding-bottom: 30px">
        <a-typography-title>手机号登录</a-typography-title>
        <a-typography-text type="secondary">
          输入手机号和验证码登录系统
        </a-typography-text>
      </div>
      <div>
        <a-form ref="formRef" :model="modelRef" :rules="rulesRef">
          <a-form-item label="" name="mobile">
            <a-input
              v-model:value="modelRef.mobile"
              placeholder="手机号"
              size="large"
            >
              <template #suffix>
                <MobileOutlined />
              </template>
            </a-input>
          </a-form-item>
          <a-form-item label="" name="code">
            <a-input
              v-model:value="modelRef.code"
              placeholder="验证码"
              size="large"
            >
              <template #suffix>
                <SafetyOutlined />
              </template>
              <template #addonAfter>
                <a-button
                  :disabled="countdown > 0"
                  type="link"
                  @click="handleSendCode"
                >
                  {{ countdown > 0 ? `${countdown}s 后重发` : '发送验证码' }}
                </a-button>
              </template>
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button
              html-type="submit"
              size="large"
              style="width: 100%"
              type="primary"
              @click.prevent="onSubmit"
            >
              登录
            </a-button>
          </a-form-item>
        </a-form>
        <a-flex align="center" justify="center">
          <a-button type="link" @click="handleGo('/auth/login')">
            返回账号密码登录
          </a-button>
        </a-flex>
      </div>
    </a-spin>
  </div>
</template>
