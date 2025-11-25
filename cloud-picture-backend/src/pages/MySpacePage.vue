<template>
  <div id="mySpacePage"></div>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'
import { userLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { listSpaceVoByPageUsingPost } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import { onMounted } from 'vue'
import { SPACE_TYPE_ENUM } from '@/constants/space.ts'

const router = useRouter()
const loginUserStore = userLoginUserStore()

// 检查用户是否有个人空间
const checkUserSpace = async () => {
  // 判断用户是否登录
  const loginUser = loginUserStore.loginUser
  if (!loginUser.id) {
    router.replace('/user/login')
    return
  }
  const res = await listSpaceVoByPageUsingPost({
    userId: loginUser.id,
    current: 1,
    pageSize: 1,
    spaceType: SPACE_TYPE_ENUM.PRIVATE,
  })
  if (res.data.code === 0) {
    // 如果有则跳转到第一个空间
    if (res.data.data?.records?.length > 0) {
      const space = res.data.data.records[0]
      // 跳转到个人空间页面
      router.replace(`/space/${space.id}`)
    } else {
      // 如果没有则跳转到创建空间页面
      router.replace('/add_space')
      message.warn('请先创建空间')
    }
  } else {
    message.error('加载我的空间失败' + res.data.message)
  }
}

onMounted(() => {
  checkUserSpace()
})
</script>

<style scoped>
#mySpacePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
