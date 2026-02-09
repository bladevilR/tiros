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
            <a-form-model-item label="BOM编码" prop="bomCode">
              <a-input v-model="model.bomCode" placeholder="请输入BOM编码" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="BOM名称" prop="bomName">
              <a-input v-model="model.bomName" placeholder="请输入BOM名称" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="车型">
              <a-input v-model="model.trainType" placeholder="请输入车型" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="线别">
              <a-input v-model="model.line" placeholder="请输入线别" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="位置">
              <a-input v-model="model.position" placeholder="请输入位置" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="系统">
              <a-input v-model="model.system" placeholder="请输入系统" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="二级模块">
              <a-input v-model="model.moduleLevel2" placeholder="请输入二级模块" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="三级模块">
              <a-input v-model="model.moduleLevel3" placeholder="请输入三级模块" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="部件明细(JSON)" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-textarea v-model="model.partDetails" :rows="3" placeholder="请输入部件明细JSON" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="爆炸图URL">
              <a-input v-model="model.explosionDiagram" placeholder="请输入爆炸图URL" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="关联物资(JSON)">
              <a-input v-model="model.materialLinks" placeholder="请输入关联物资JSON" />
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
import { saveQuotaBom, updateQuotaBom } from '@/api/tirosApi'

export default {
  name: 'QuotaBomModal',
  data() {
    return {
      title: '',
      visible: false,
      confirmLoading: false,
      model: {},
      validatorRules: {
        bomCode: [{ required: true, message: '请输入BOM编码', trigger: 'blur' }],
        bomName: [{ required: true, message: '请输入BOM名称', trigger: 'blur' }]
      }
    }
  },
  methods: {
    add() {
      this.title = '新增定额BOM'
      this.visible = true
      this.model = {}
    },
    edit(record) {
      this.title = '编辑定额BOM'
      this.visible = true
      this.model = Object.assign({}, record)
    },
    handleOk() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.confirmLoading = true
          const req = this.model.id ? updateQuotaBom(this.model) : saveQuotaBom(this.model)
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
