<template>
  <a-modal
    v-model="visible"
    title="审阅作业指导书"
    width="600px"
    :maskClosable="false"
    @ok="handleOk"
    @cancel="handleCancel"
    :confirmLoading="loading"
  >
    <a-form layout="vertical">
      <a-form-item label="审阅结论">
        <a-radio-group v-model="reviewStatus">
          <a-radio :value="2">通过</a-radio>
          <a-radio :value="3">驳回</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label="审阅意见">
        <a-textarea v-model="reviewComment" :rows="4" placeholder="请输入审阅意见"></a-textarea>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import { decisionSopReview } from '@/api/tirosApi'

export default {
  name: 'TechBookReviewModal',
  data () {
    return {
      visible: false,
      loading: false,
      record: {},
      reviewStatus: 2,
      reviewComment: ''
    }
  },
  methods: {
    open (record) {
      this.record = record || {}
      this.reviewStatus = 2
      this.reviewComment = (record && record.reviewComment) || ''
      this.visible = true
    },
    handleOk () {
      if (!this.record.id) {
        this.$message.warning('未选择指导书')
        return
      }
      if (this.record.reviewStatus !== 1) {
        this.$message.warning('仅待审状态可进行审阅')
        return
      }
      const comment = (this.reviewComment || '').trim()
      if (!comment) {
        this.$message.warning('请填写审阅意见')
        return
      }
      this.loading = true
      decisionSopReview({
        id: this.record.id,
        reviewStatus: this.reviewStatus,
        reviewComment: comment
      }).then(res => {
        if (res.success) {
          this.$message.success('审阅完成')
          this.$emit('ok')
          this.handleCancel()
        } else {
          this.$message.error(res.message || '审阅失败')
        }
      }).finally(() => {
        this.loading = false
      })
    },
    handleCancel () {
      this.visible = false
      this.record = {}
      this.reviewComment = ''
    }
  }
}
</script>
