<template>
  <div class="url-picture-upload">
    <a-input-group compact>
      <a-input
        v-model:value="fileUrl"
        style="width: calc(100% - 120px)"
        placeholder="请输入图片地址"
      />
      <a-button type="primary" style="width: 120px" :loading="loading" @click="handleUpload"
        >确认</a-button
      >
    </a-input-group>
    <div class="img-wrapper">
      <img v-if="picture?.url" :src="picture?.url" alt="avatar" />
    </div>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { uploadPictureByUrlUsingPost } from '@/api/pictureController.ts'

interface Props {
  picture?: API.PictureVo
  spaceId?: number
  onSuccess?: (newPicture: API.PictureVo) => void
}

const props = defineProps<Props>()
const fileUrl = ref<string>()
const loading = ref<boolean>(false)

const handleUpload = async () => {
  loading.value = true
  try {
    const param: API.PictureUploadRequest = { fileUrl: fileUrl.value }
    param.spaceId = props.spaceId
    if (props.picture) {
      param.id = props.picture.id
    }
    const res = await uploadPictureByUrlUsingPost(param)
    if (res.data.code === 0 && res.data.data) {
      message.success('上传图片成功')
      // 将上传图片成功的图片信息返回给父组件
      props?.onSuccess(res.data.data)
    } else {
      message.error('上传图片失败')
    }
  } catch (error) {
    console.log('图片上传失败', error)
    message.error('上传图片失败', error.message)
  }
  loading.value = false
}
</script>
<style scoped>
.url-picture-upload {
  margin-bottom: 16px;
}

.url-picture-upload img {
  max-height: 480px;
  max-width: 100%;
  text-align: center;
}

.url-picture-upload .img-wrapper {
  margin-top: 16px;
  text-align: center;
}
</style>
