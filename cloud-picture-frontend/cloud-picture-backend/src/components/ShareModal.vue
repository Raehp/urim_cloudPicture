<template>
  <div>
    <a-modal v-model:visible="visible" :title="title" :footer="false" @close="closeModal">
      <h4>复制连接分享</h4>
      <a-typography-link copyable>
        {{ link }}
      </a-typography-link>
      <div style="margin-bottom: 16px"></div>
      <h4>手机扫码查看</h4>
      <a-qrcode :value="link" />
    </a-modal>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue';

// 是否可见
const visible = ref(false)

/**
 * 定义组件属性类型
 */
interface Props {
  title: string
  link: string
}

/**
 * 给组件指定初始值
 */
const props = withDefaults(defineProps<Props>(), {
  title: () => '分享图片',
  link: () => 'https://www.baidu.com',
})

// 打开弹窗
const openModal = () => {
  visible.value = true;
};

// 关闭弹窗
const closeModal = () => {
  visible.value = false;
};

defineExpose({
  openModal,
});
</script>

