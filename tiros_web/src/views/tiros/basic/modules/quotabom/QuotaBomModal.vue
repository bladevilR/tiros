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
              <a-input v-model="model.trainType" placeholder="请输入车型，默认电客车" />
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="线别(可多选)">
              <a-select mode="multiple" v-model="lineValues" placeholder="请选择线别" allowClear>
                <a-select-option v-for="item in lineOptions" :key="item" :value="item">{{ item }}</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item label="位置(可多选)">
              <a-select mode="multiple" v-model="positionValues" placeholder="请选择位置" allowClear>
                <a-select-option v-for="item in positionOptions" :key="item" :value="item">{{ item }}</a-select-option>
              </a-select>
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
              <a-textarea v-model="model.partDetails" :rows="5" placeholder='示例：[{
"partNo":"P001","name":"制动缸","spec":"X1","drawingQty":1,
"trainQty":2,"materialCode":"M001","maintReq":"一架"}]' />
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
      lineValues: [],
      positionValues: [],
      lineOptions: ['1号线', '1号线增购', '2号线', '2号线增购', '2号线延线', '3号线', '4号线', '4号线增购', '5号线', '6号线', '7号线', '11号线'],
      positionOptions: ['TC1', 'MP1', 'M1', 'M2', 'MP2', 'TC2', 'ALL'],
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
      this.model = { trainType: '电客车' }
      this.lineValues = []
      this.positionValues = []
    },
    edit(record) {
      this.title = '编辑定额BOM'
      this.visible = true
      this.model = Object.assign({}, record)
      this.lineValues = this.splitCsv(record.line)
      this.positionValues = this.splitCsv(record.position)
    },
    handleOk() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.model.line = this.joinCsv(this.lineValues)
          this.model.position = this.joinCsv(this.positionValues)
          if (!this.model.trainType) {
            this.model.trainType = '电客车'
          }
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
    },
    splitCsv(value) {
      if (!value) {
        return []
      }
      return String(value)
        .split(',')
        .map(item => item.trim())
        .filter(Boolean)
    },
    joinCsv(values) {
      return (values || []).map(item => String(item).trim()).filter(Boolean).join(',')
    }
  }
}
</script>
