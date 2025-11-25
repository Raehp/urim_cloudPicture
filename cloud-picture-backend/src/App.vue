<template>
  <div id="app">
    <BasicLayout />
  </div>

</template>

<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue';
// 已经改为再权限校验逻辑中获取
// import { userLoginUserStore } from "@/stores/useLoginUserStore.ts";
//
// const loginUserStore = userLoginUserStore();
// loginUserStore.fetchLoginUser();

</script>

<style scoped>

</style>
