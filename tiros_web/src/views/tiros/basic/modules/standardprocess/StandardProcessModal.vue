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
              <a-input v-model="model.processImages" placeholder="图片URL，多个逗号分隔" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="工序步骤" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-textarea v-model="model.processSteps" :rows="4" placeholder="请输入工序步骤" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="紧固顺序">
              <a-input v-model="model.fastenSequence" placeholder="请输入紧固顺序" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="紧固示意图">
              <a-input v-model="model.fastenDiagram" placeholder="示意图URL" />
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

export default {
  name: 'StandardProcessModal',
  data() {
    return {
      title: '',
      visible: false,
      confirmLoading: false,
      model: {},
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
    },
    edit(record) {
      this.title = '编辑标准工序'
      this.visible = true
      this.model = Object.assign({}, record)
    },
    handleOk() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.confirmLoading = true
          const req = this.model.id ? updateStandardProcess(this.model) : saveStandardProcess(this.model)
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
