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
            <a-form-model-item label="项目名称" prop="projectName">
              <a-input v-model="model.projectName" placeholder="请输入项目名称" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="计划ID">
              <a-input v-model="model.planId" placeholder="请输入计划ID" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="车号">
              <a-input v-model="model.trainNo" placeholder="请输入车号" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="车型">
              <a-input v-model="model.trainType" placeholder="请输入车型" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="数据汇总" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-space>
                <a-button type="dashed" icon="sync" :loading="refreshLoading" @click="handleAutoRefresh">自动汇总</a-button>
                <span style="color: rgba(0, 0, 0, 0.45)">根据计划ID+车号自动汇总工单与质量问题数据</span>
              </a-space>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="质量等级">
              <a-select v-model="model.qualityLevel" placeholder="请选择质量等级">
                <a-select-option value="A">A</a-select-option>
                <a-select-option value="B">B</a-select-option>
                <a-select-option value="C">C</a-select-option>
                <a-select-option value="D">D</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="进度">
              <a-input v-model="model.progress" placeholder="请输入进度(%)" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="已完成工序数">
              <a-input-number v-model="model.completedProcesses" :min="0" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="总工序数">
              <a-input-number v-model="model.totalProcesses" :min="0" style="width: 100%" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="质量问题数">
              <a-input-number v-model="model.qualityIssues" :min="0" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="未关闭问题数">
              <a-input-number v-model="model.openIssues" :min="0" style="width: 100%" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="工序流程(JSON)" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-textarea v-model="model.processFlow" :rows="3" placeholder="请输入工序流程JSON" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="质检点(JSON)" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-textarea v-model="model.qualityPoints" :rows="3" placeholder="请输入质检点JSON" />
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
      </a-form-model>
    </a-spin>
  </a-modal>
</template>

<script>
import { saveQualityVisual, updateQualityVisual, refreshQualityVisual } from '@/api/tirosApi'

export default {
  name: 'QualityVisualModal',
  data() {
    return {
      title: '',
      visible: false,
      confirmLoading: false,
      refreshLoading: false,
      model: {},
      validatorRules: {
        projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }]
      }
    }
  },
  methods: {
    add() {
      this.title = '新增质量可视化'
      this.visible = true
      this.model = {
        trainType: '电客车',
        qualityLevel: 'A',
        completedProcesses: 0,
        totalProcesses: 0,
        qualityIssues: 0,
        openIssues: 0
      }
    },
    edit(record) {
      this.title = '编辑质量可视化'
      this.visible = true
      this.model = Object.assign({}, record)
    },
    handleAutoRefresh() {
      if (!this.model.planId || !this.model.trainNo) {
        this.$message.warning('请先输入计划ID和车号')
        return
      }
      this.refreshLoading = true
      refreshQualityVisual({
        planId: this.model.planId,
        trainNo: this.model.trainNo,
        trainType: this.model.trainType,
        projectName: this.model.projectName
      }).then(res => {
        if (res.success && res.result) {
          this.model = Object.assign({}, this.model, res.result)
          this.$message.success('自动汇总成功，已回填数据')
        } else {
          this.$message.error(res.message || '自动汇总失败')
        }
      }).finally(() => {
        this.refreshLoading = false
      })
    },
    handleOk() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.model.completedProcesses > this.model.totalProcesses) {
            this.$message.warning('已完成工序数不能大于总工序数')
            return
          }
          this.confirmLoading = true
          const req = this.model.id ? updateQualityVisual(this.model) : saveQualityVisual(this.model)
          req.then(res => {
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
