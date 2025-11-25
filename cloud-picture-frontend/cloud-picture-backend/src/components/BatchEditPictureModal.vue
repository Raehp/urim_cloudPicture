<template>
  <div class="batch-edit-picture-modal">
    <a-modal v-model:visible="visible" title="批量编辑图片" :footer="false" @close="closeModal">
      <a-typography-paragraph type="secondary"> * 只对当前页面的图片生效</a-typography-paragraph>
      <!--    批量创建表单-->
      <a-form name="dataForm" layout="vertical" :model="dataForm" @finish="handleSubmit">
        <a-form-item label="分类" name="category">
          <a-auto-complete
            v-model:value="dataForm.category"
            placeholder="请输入分类"
            allow-clear
            :options="categoryOptions"
          />
        </a-form-item>
        <a-form-item label="标签" name="tags">
          <a-select
            v-model:value="dataForm.tags"
            mode="tags"
            placeholder="请输入标签"
            allow-clear
            :options="tagOptions"
          />
        </a-form-item>
        <a-form-item name="nameRule" label="命名规则">
          <a-input
            v-model:value="dataForm.nameRule"
            placeholder="请输入命名规则：输入 {序号} 可动态生成"
            allow-clear
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" style="width: 100%">提交</a-button>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import router from '@/router'
import { editPictureByBatchUsingPost, listPictureTagCategoryUsingGet } from '@/api/pictureController.ts'

const visible = ref(false)

/**
 * 定义组件属性类型
 */
interface Props {
  pictureList: API.PictureVo[]
  spaceId: number
  onSuccess: () => void
}

/**
 * 给组件指定初始值
 */
const props = withDefaults(defineProps<Props>(), {})

// 打开弹窗
const openModal = () => {
  visible.value = true
}

// 关闭弹窗
const closeModal = () => {
  visible.value = false
}

defineExpose({
  openModal,
})

const dataForm = reactive<API.PictureEditByBatchRequest>({
  category: '',
  tags: [],
  nameRule: '',
})

// 标签和分类列表
const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

/**
 * 获取标签和分类
 */
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('获取标签和分页列表失败，' + res.data.message)
  }
}
onMounted(() => {
  getTagCategoryOptions()
})

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  if (!props.pictureList) {
    return;
  }
  const res = await editPictureByBatchUsingPost({
    pictureIdList: props.pictureList.map((picture) => picture.id),
    spaceId: props.spaceId,
    ...values,
  })
  // 操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success('操作成功')
    closeModal()
    props.onSuccess?.()
  } else {
    message.error('操作失败' + res.data.message)
  }
}
</script>
