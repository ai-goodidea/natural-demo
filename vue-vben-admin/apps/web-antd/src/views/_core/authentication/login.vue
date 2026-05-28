<script lang="ts" setup>
import { reactive, ref } from 'vue';

import { UserOutlined } from '@ant-design/icons-vue';
import { Form } from 'ant-design-vue';

import { useAuthStore } from '#/store';

defineOptions({ name: 'Login' });

const authStore = useAuthStore();

const formRef = ref();
const useForm = Form.useForm;
const modelRef: any = reactive({
  username: 'admin',
  password: '',
});
const rulesRef = reactive({
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
});
useForm(modelRef, rulesRef);

function onSubmit() {
  formRef.value
    .validate()
    .then(() => {
      authStore.authLogin({ ...modelRef });
    })
    .catch(() => {});
}
</script>

<template>
  <div>
    <a-spin :spinning="authStore.loginLoading">
      <div style="padding-bottom: 24px">
        <a-typography-title :level="3">天然气设备验收记录系统</a-typography-title>
        <a-typography-text type="secondary">
          输入用户名即可登录（demo 不校验密码）。默认用户：admin / alice / bob
        </a-typography-text>
      </div>
      <a-form ref="formRef" :model="modelRef" :rules="rulesRef">
        <a-form-item :rules="rulesRef.username" label="" name="username">
          <a-input
            v-model:value="modelRef.username"
            placeholder="用户名（不存在会自动创建）"
            size="large"
          >
            <template #suffix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item label="" name="password">
          <a-input-password
            v-model:value="modelRef.password"
            placeholder="密码（任意，不校验）"
            size="large"
          />
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
    </a-spin>
  </div>
</template>
