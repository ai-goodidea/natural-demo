<script lang="ts" setup>
import { useRouter } from 'vue-router';

import { ReloadOutlined, WechatOutlined } from '@ant-design/icons-vue';

defineOptions({ name: 'QrCodeLogin' });

const router = useRouter();

// 占位二维码，可替换为后端返回的微信授权二维码
const qrcodeSrc =
  'https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=wechat-login-placeholder';

function handleRefresh() {
  // 后端接入后这里刷新二维码
  window.location.reload();
}

function handleGo(path: string) {
  router.push(path);
}
</script>

<template>
  <div>
    <div style="padding-bottom: 30px">
      <a-typography-title>扫码登录</a-typography-title>
      <a-typography-text type="secondary">
        请使用微信扫描下方二维码登录
      </a-typography-text>
    </div>

    <a-flex align="center" justify="center" vertical>
      <div
        style="
          padding: 16px;
          border: 1px solid #f0f0f0;
          border-radius: 8px;
          background: #fff;
        "
      >
        <a-image :preview="false" :src="qrcodeSrc" :width="200" />
      </div>

      <a-space style="margin-top: 16px">
        <WechatOutlined style="color: #07c160; font-size: 18px" />
        <a-typography-text type="secondary">
          打开微信，扫一扫登录
        </a-typography-text>
      </a-space>

      <a-button
        :icon="undefined"
        style="margin-top: 12px"
        type="link"
        @click="handleRefresh"
      >
        <template #icon>
          <ReloadOutlined />
        </template>
        刷新二维码
      </a-button>
    </a-flex>

    <a-divider style="font-size: small">其他登录方式</a-divider>

    <a-flex align="center" gap="small" justify="center">
      <a-button @click="handleGo('/auth/login')">账号密码登录</a-button>
      <a-button @click="handleGo('/auth/code-login')">手机号登录</a-button>
    </a-flex>
  </div>
</template>
