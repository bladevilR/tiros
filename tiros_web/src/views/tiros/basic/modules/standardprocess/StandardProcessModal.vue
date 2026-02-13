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
          <a-col :span="24">
            <a-form-model-item label="配置提示" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-alert
                type="info"
                show-icon
                message="支持按步骤配置图文内容；步骤可新增、删除、排序，支持本地上传图片。"
              />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="工序编码" prop="processNo">
              <a-input v-model="model.processNo" placeholder="请输入工序编码" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="工序名称" prop="processName">
              <a-input v-model="model.processName" placeholder="请输入工序名称" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="工序类型" prop="processType">
              <a-select v-model="model.processType" placeholder="请选择工序类型">
                <a-select-option value="1">拆卸</a-select-option>
                <a-select-option value="2">检修</a-select-option>
                <a-select-option value="3">安装</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="车型">
              <a-input v-model="model.trainType" placeholder="请输入车型" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="标准工时(分钟)">
              <a-input-number v-model="model.standardDuration" :min="0" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="工序图示">
              <a-input :value="stepImageSummary" disabled placeholder="根据步骤中的图片URL自动生成" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="工序步骤" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <div class="process-step-wrapper">
                <div class="process-step-scroll">
                  <div
                    v-for="(step, index) in stepRows"
                    :key="step.uid || index"
                    class="process-step-item"
                  >
                    <a-row :gutter="8">
                      <a-col :span="4">
                        <a-input-number
                          v-model="step.stepNo"
                          :min="1"
                          :precision="0"
                          style="width: 100%"
                          placeholder="序号"
                        />
                      </a-col>
                      <a-col :span="8">
                        <a-input v-model="step.name" placeholder="步骤名称（必填）" />
                      </a-col>
                      <a-col :span="8">
                        <a-space>
                          <a-input v-model="step.imageUrl" placeholder="步骤图片URL" style="width: 200px" />
                          <j-image-upload
                            text="上传"
                            :isMultiple="false"
                            :bizPath="'/standard-process/step'"
                            v-model="step.imageUrl"
                          />
                        </a-space>
                      </a-col>
                      <a-col :span="4">
                        <a-button
                          danger
                          block
                          @click="removeStepRow(index)"
                          :disabled="stepRows.length === 1"
                        >删除</a-button>
                      </a-col>
                    </a-row>
                    <a-row :gutter="8" style="margin-top: 8px">
                      <a-col :span="12">
                        <a-input v-model="step.checkPoint" placeholder="检验点（选填）" />
                      </a-col>
                      <a-col :span="12">
                        <a-input v-model="step.remark" placeholder="步骤备注（选填）" />
                      </a-col>
                    </a-row>
                    <a-row style="margin-top: 8px">
                      <a-col :span="24">
                        <a-textarea v-model="step.content" :rows="2" placeholder="步骤详细内容（可填）" />
                      </a-col>
                    </a-row>
                  </div>
                </div>
                <a-space style="margin-top: 8px">
                  <a-button size="small" type="dashed" icon="plus" @click="addStepRow">新增步骤</a-button>
                  <a-button size="small" @click="normalizeStepRows">按序号排序</a-button>
                </a-space>
              </div>
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
import { saveStandardProcess, updateStandardProcess } from '@/api/tirosApi'
import JImageUpload from '@/components/jeecg/JImageUpload'

export default {
  name: 'StandardProcessModal',
  components: { JImageUpload },
  computed: {
    stepImageSummary() {
      return (this.stepRows || []).map(item => (item.imageUrl || '').trim()).filter(Boolean).join(',')
    }
  },
  data() {
    return {
      title: '',
      visible: false,
      confirmLoading: false,
      model: {},
      stepRows: [],
      validatorRules: {
        processNo: [{ required: true, message: '请输入工序编码', trigger: 'blur' }],
        processName: [{ required: true, message: '请输入工序名称', trigger: 'blur' }]
      }
    }
  },
  methods: {
    add() {
      this.title = '新增标准工序'
      this.visible = true
      this.model = {
        processType: '1',
        standardDuration: 0
      }
      this.stepRows = [this.createEmptyStepRow(1)]
    },
    edit(record) {
      this.title = '编辑标准工序'
      this.visible = true
      this.model = Object.assign({}, record)
      this.stepRows = this.parseStepRows(record.processSteps, record.processImages)
    },
    handleOk() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.normalizeStepRows()
          const payloadSteps = this.buildPayloadSteps(this.stepRows)
          if (!payloadSteps.length) {
            this.$message.warning('请至少配置一个步骤名称')
            return
          }
          const submitModel = Object.assign({}, this.model)
          submitModel.processSteps = JSON.stringify(payloadSteps)
          submitModel.processImages = payloadSteps
            .map(item => (item.imageUrl || '').trim())
            .filter(Boolean)
            .join(',')
          this.confirmLoading = true
          const req = submitModel.id ? updateStandardProcess(submitModel) : saveStandardProcess(submitModel)
          req.then(res => {
            if (res.success) {
              this.$message.success(submitModel.id ? '修改成功' : '新增成功')
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
      this.stepRows = []
    },
    addStepRow() {
      const nextNo = (this.stepRows || []).length + 1
      this.stepRows.push(this.createEmptyStepRow(nextNo))
    },
    removeStepRow(index) {
      this.stepRows.splice(index, 1)
      if (this.stepRows.length === 0) {
        this.stepRows.push(this.createEmptyStepRow(1))
      }
      this.normalizeStepRows()
    },
    normalizeStepRows() {
      this.stepRows = (this.stepRows || []).map((item, index) => {
        const stepNo = this.toPositiveInt(item.stepNo)
        return {
          uid: item.uid || this.newUid(),
          stepNo: stepNo > 0 ? stepNo : index + 1,
          name: (item.name || '').trim(),
          content: (item.content || '').trim(),
          checkPoint: (item.checkPoint || '').trim(),
          imageUrl: (item.imageUrl || '').trim(),
          remark: (item.remark || '').trim()
        }
      }).sort((left, right) => left.stepNo - right.stepNo)
      if (this.stepRows.length === 0) {
        this.stepRows.push(this.createEmptyStepRow(1))
      }
    },
    parseStepRows(processSteps, processImages) {
      const imageList = String(processImages || '').split(',').map(item => item.trim()).filter(Boolean)
      const fallbackFromLines = () => {
        const lines = String(processSteps || '').split('\n').map(item => item.trim()).filter(Boolean)
        if (!lines.length) {
          return [this.createEmptyStepRow(1)]
        }
        return lines.map((line, index) => ({
          ...this.createEmptyStepRow(index + 1),
          name: line,
          imageUrl: imageList[index] || ''
        }))
      }
      if (!processSteps) {
        return [this.createEmptyStepRow(1)]
      }
      try {
        const list = JSON.parse(processSteps)
        if (!Array.isArray(list) || list.length === 0) {
          return fallbackFromLines()
        }
        return list.map((item, index) => ({
          uid: this.newUid(),
          stepNo: this.toPositiveInt(item.stepNo) || index + 1,
          name: (item.name || item.stepName || '').trim(),
          content: (item.content || item.desc || item.description || '').trim(),
          checkPoint: (item.checkPoint || '').trim(),
          imageUrl: (item.imageUrl || imageList[index] || '').trim(),
          remark: (item.remark || '').trim()
        }))
      } catch (e) {
        return fallbackFromLines()
      }
    },
    buildPayloadSteps(rows) {
      return (rows || [])
        .map(item => ({
          stepNo: this.toPositiveInt(item.stepNo),
          name: (item.name || '').trim(),
          content: (item.content || '').trim(),
          checkPoint: (item.checkPoint || '').trim(),
          imageUrl: (item.imageUrl || '').trim(),
          remark: (item.remark || '').trim()
        }))
        .filter(item => item.name || item.content || item.imageUrl || item.checkPoint || item.remark)
        .map((item, index) => ({
          ...item,
          stepNo: item.stepNo > 0 ? item.stepNo : index + 1
        }))
    },
    createEmptyStepRow(stepNo) {
      return {
        uid: this.newUid(),
        stepNo: stepNo || 1,
        name: '',
        content: '',
        checkPoint: '',
        imageUrl: '',
        remark: ''
      }
    },
    toPositiveInt(value) {
      const num = Number(value)
      if (Number.isNaN(num) || num <= 0) {
        return 0
      }
      return Math.floor(num)
    },
    newUid() {
      return `step_${Date.now()}_${Math.random().toString(36).slice(2, 8)}`
    }
  }
}
</script>

<style scoped>
.process-step-wrapper {
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 8px;
  background: #fafafa;
}

.process-step-scroll {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 4px;
}

.process-step-item {
  padding: 8px;
  border-radius: 4px;
  background: #fff;
  border: 1px solid #f0f0f0;
}

.process-step-item + .process-step-item {
  margin-top: 8px;
}
</style>
