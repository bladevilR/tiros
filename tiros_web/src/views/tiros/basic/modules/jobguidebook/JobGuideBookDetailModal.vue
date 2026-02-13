<template>
  <a-modal
    v-model="visible"
    :title="isEdit ? '编辑作业指导书' : '新建作业指导书'"
    width="900px"
    @ok="handleOk"
    @cancel="handleCancel"
    :confirmLoading="loading"
    :maskClosable="false"
  >
    <a-form :form="form" layout="vertical">
      <a-row :gutter="16">
        <a-col :md="24">
          <a-form-item>
            <a-checkbox v-model="reuseEnabled">从既有作业指导书复用</a-checkbox>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16" v-if="reuseEnabled && !isEdit">
        <a-col :md="24">
          <a-form-item label="复用来源" :required="true">
            <a-select
              showSearch
              optionFilterProp="children"
              placeholder="请选择要复用的作业指导书"
              v-model="reuseSourceId"
            >
              <a-select-option v-for="item in reuseOptions" :key="item.id" :value="item.id">
                {{ item.fileNo }} - {{ item.fileName }}（{{ item.fileVer }}）
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :md="12">
          <a-form-item label="文件编号" :required="true">
            <a-input
              placeholder="请输入文件编号"
              v-decorator="['fileNo', {rules: [{required: true, message: '请输入文件编号'}]}]"
            />
          </a-form-item>
        </a-col>
        <a-col :md="12">
          <a-form-item label="文件名称" :required="true">
            <a-input
              placeholder="请输入文件名称"
              v-decorator="['fileName', {rules: [{required: true, message: '请输入文件名称'}]}]"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :md="12">
          <a-form-item label="版本" :required="true">
            <a-input
              placeholder="请输入版本号，如V1.0"
              v-decorator="['fileVer', {rules: [{required: true, message: '请输入版本号'}]}]"
            />
          </a-form-item>
        </a-col>
        <a-col :md="12">
          <a-form-item label="所属线路" :required="true">
            <a-select
              showSearch
              optionFilterProp="children"
              placeholder="请选择所属线路"
              v-decorator="['lineId', {rules: [{required: true, message: '请选择所属线路'}]}]"
            >
              <a-select-option v-for="item in lineOptions" :key="item.value" :value="item.value">
                {{ item.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :md="12">
          <a-form-item label="车型" :required="true">
            <a-select
              showSearch
              optionFilterProp="children"
              placeholder="请选择车型"
              v-decorator="['trainTypeId', {rules: [{required: true, message: '请选择车型'}]}]"
            >
              <a-select-option v-for="item in trainTypeOptions" :key="item.value" :value="item.value">
                {{ item.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :md="12">
          <a-form-item label="所属修程" :required="true">
            <a-select
              showSearch
              optionFilterProp="children"
              placeholder="请选择所属修程"
              v-decorator="['repairProgramId', {rules: [{required: true, message: '请选择所属修程'}]}]"
            >
              <a-select-option v-for="item in repairProgramOptions" :key="item.value" :value="item.value">
                {{ item.label }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :md="12">
          <a-form-item label="项目">
            <a-input
              placeholder="请输入项目"
              v-decorator="['project', {rules: [{required: false}]}]"
            />
          </a-form-item>
        </a-col>
        <a-col :md="12">
          <a-form-item label="实施日期" :required="true">
            <a-date-picker
              style="width: 100%"
              valueFormat="YYYY-MM-DD"
              placeholder="请选择实施日期"
              v-decorator="['exeTime', {rules: [{required: true, message: '请选择实施日期'}]}]"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="24">
          <a-form-item label="备注">
            <a-textarea
              placeholder="请输入备注"
              :rows="3"
              v-decorator="['remark', {rules: [{required: false}]}]"
            />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
import { saveJgbRecord, saveJgbByReuse, getJgbPage } from '@/api/tirosApi'
import { ajaxGetDictItems } from '@/api/api'

export default {
  data () {
    return {
      visible: false,
      loading: false,
      form: this.$form.createForm(this),
      record: null,
      isEdit: false,
      reuseEnabled: false,
      reuseSourceId: '',
      reuseOptions: [],
      lineOptions: [],
      trainTypeOptions: [],
      repairProgramOptions: []
    }
  },
  created () {
    this.loadDictOptions()
  },
  methods: {
    loadDictOptions () {
      ajaxGetDictItems('bu_mtr_line,line_name,line_id', null).then(res => {
        const list = (res && res.success) ? (res.result || []) : []
        this.lineOptions = list.map(item => ({ label: item.text, value: item.value }))
      })
      ajaxGetDictItems('bu_train_type,name,id', null).then(res => {
        const list = (res && res.success) ? (res.result || []) : []
        this.trainTypeOptions = list.map(item => ({ label: item.text, value: item.value }))
      })
      ajaxGetDictItems('bu_repair_program,name,id', null).then(res => {
        const list = (res && res.success) ? (res.result || []) : []
        this.repairProgramOptions = list.map(item => ({ label: item.text, value: item.value }))
      })
      getJgbPage({ pageNo: 1, pageSize: 1000, templateFlag: 0 }).then(res => {
        if (res && res.success) {
          const records = (res.result && res.result.records) ? res.result.records : []
          this.reuseOptions = records.filter(item => item && item.status !== 9)
        }
      })
    },
    add () {
      this.isEdit = false
      this.record = null
      this.reuseEnabled = false
      this.reuseSourceId = ''
      this.form.resetFields()
      this.visible = true
    },
    edit (record) {
      this.isEdit = true
      this.record = record
      this.reuseEnabled = false
      this.reuseSourceId = ''
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          fileNo: record.fileNo,
          fileName: record.fileName,
          fileVer: record.fileVer,
          lineId: record.lineId,
          trainTypeId: record.trainTypeId,
          repairProgramId: record.repairProgramId,
          project: record.project,
          exeTime: record.exeTime,
          remark: record.remark
        })
      })
    },
    handleOk () {
      this.form.validateFields((err, values) => {
        if (!err) {
          if (this.isEdit && this.record && this.record.status === 1) {
            this.$message.warning('已发布作业指导书不允许修改基础信息')
            return
          }
          this.loading = true
          const params = { ...values, id: this.isEdit ? this.record.id : undefined }
          if (!this.isEdit && this.reuseEnabled && !this.reuseSourceId) {
            this.$message.warning('请选择复用来源')
            this.loading = false
            return
          }
          const req = (!this.isEdit && this.reuseEnabled)
            ? saveJgbByReuse(this.reuseSourceId, params)
            : saveJgbRecord(params)
          req.then((res) => {
            if (res.success) {
              this.$message.success(this.isEdit ? '修改成功' : '新增成功')
              this.handleCancel()
              this.$emit('ok')
            } else {
              this.$message.error(res.message || '操作失败')
            }
          }).finally(() => { this.loading = false })
        }
      })
    },
    handleCancel () {
      this.visible = false
      this.form.resetFields()
      this.record = null
      this.reuseEnabled = false
      this.reuseSourceId = ''
    }
  }
}
</script>
