<template>
  <a-modal
    :title="title"
    :width="900"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false"
  >
    <a-spin :spinning="confirmLoading">
      <a-form-model
        ref="form"
        :model="model"
        :rules="validatorRules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="通知单号" prop="noticeNo">
              <a-input v-model="model.noticeNo" placeholder="请输入通知单号" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="通知单标题" prop="title">
              <a-input v-model="model.title" placeholder="请输入通知单标题" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="通知类型" prop="noticeType">
              <a-select v-model="model.noticeType" placeholder="请选择通知类型">
                <a-select-option value="1">技术类</a-select-option>
                <a-select-option value="2">变更类</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="状态">
              <a-input :value="statusText" disabled />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="适用车型" prop="trainType">
              <a-input v-model="model.trainType" placeholder="请输入车型" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="适用线别" prop="line">
              <a-input v-model="model.line" placeholder="请输入线别" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="涉及列车总数">
              <a-input-number v-model="model.totalTrains" :min="0" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="已完成列车数">
              <a-input-number v-model="model.completedTrains" :min="0" style="width: 100%" :disabled="true" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="作业范围" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-textarea v-model="model.scope" :rows="2" placeholder="请输入作业范围描述" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="通知内容" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-textarea v-model="model.content" :rows="4" placeholder="请输入通知内容" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="编制人">
              <a-input v-model="model.creator" placeholder="请输入编制人" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="核对人">
              <a-input v-model="model.checker" placeholder="请输入核对人" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="备注" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-textarea v-model="model.remark" :rows="2" placeholder="请输入备注" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row v-if="showProgressHint">
          <a-col :span="24">
            <a-form-model-item label="执行提示" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-alert
                type="info"
                show-icon
                :message="'技术类生产通知单发布后，将通过关联工单自动统计完成进度（已完成列车数/总列车数）'"
              />
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-spin>
  </a-modal>
</template>

<script>
import { saveProductionNotice, updateProductionNotice } from '@/api/tirosApi'

export default {
  name: 'ProductionNoticeModal',
  computed: {
    statusText() {
      const map = {
        '0': '草稿',
        '1': '审核中',
        '2': '已发布',
        '3': '已关闭'
      }
      return map[this.model.status] || '草稿'
    },
    showProgressHint() {
      return this.model.noticeType === '1'
    }
  },
  data() {
    return {
      title: '',
      visible: false,
      confirmLoading: false,
      model: {},
      validatorRules: {
        noticeNo: [{ required: true, message: '请输入通知单号', trigger: 'blur' }],
        title: [{ required: true, message: '请输入通知单标题', trigger: 'blur' }],
        noticeType: [{ required: true, message: '请选择通知类型', trigger: 'change' }]
      }
    }
  },
  methods: {
    add() {
      this.title = '新增生产通知单'
      this.visible = true
      this.model = {
        status: '0',
        totalTrains: 0,
        completedTrains: 0,
        progress: '0%'
      }
    },
    edit(record) {
      this.title = '编辑生产通知单'
      this.visible = true
      this.model = Object.assign({}, record)
    },
    handleOk() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.confirmLoading = true
          const promise = this.model.id ? updateProductionNotice(this.model) : saveProductionNotice(this.model)
          promise.then(res => {
            if (res.success) {
              this.$message.success(this.model.id ? '修改成功' : '新增成功')
              this.visible = false
              this.$emit('ok')
            } else {
              this.$message.error(res.message || '操作失败')
            }
          }).finally(() => {
            this.confirmLoading = false
          })
        }
      })
    },
    handleCancel() {
      this.visible = false
      this.$refs.form.resetFields()
    }
  }
}
</script>
