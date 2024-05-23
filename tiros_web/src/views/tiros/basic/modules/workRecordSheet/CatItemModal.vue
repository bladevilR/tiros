<template>
  <a-drawer :title="title" :maskClosable="true" :closable="true" :width="600" :visible="visible" @close="handleCancel">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联规程项">
          <a-select
            placeholder="请选择规程项"
            :open="false"
            :showArrow="true"
            v-model="ReguName"
            @focus="openModal"
            ref="mySelect"
          >
            <a-icon slot="suffixIcon" type="ellipsis"/>
          </a-select>
        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="项目序号">
          <a-input-number
            :max="2147483647"
            id="inputNumber"
            v-decorator.trim="['recIndex', validatorRules.recIndex ]"
            style="width: 100%"
          />

        </a-form-item>
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="项目名称">
          <a-input v-decorator.trim="['reguTitle', validatorRules.reguTitle]" @change="onChange"/>
        </a-form-item>


      </a-form>
    </a-spin>
    <div class="drawer-bootom-button">
      <!-- <a-popconfirm title="确定放弃编辑？" @confirm="handleCancel" okText="确定" cancelText="取消"> -->
      <a-button style="margin-right: 0.8rem" @click="handleCancel">取消</a-button>
      <!-- </a-popconfirm> -->
      <a-button @click="handleSubmit" type="primary" :loading="confirmLoading">提交</a-button>
    </div>
    <regulation-list ref="modalForm" :multiple="false" :onlyProject="false" @ok="selectRegulation"></regulation-list>
  </a-drawer>
</template>

<script>
import pick from 'lodash.pick'
import { saveOrUpdateOldWorkRecordCategory } from '@/api/tirosApi'
import RegulationList from '../../../common/selectModules/RegulationList'

export default {
  name: 'CatItemModal',
  components: { RegulationList },
  props: ['recordId'],
  data() {
    return {
      value: 1,
      title: '操作',
      status: 1,
      switchCheck: true,
      ReguName: '',
      queryParam: {
        workRecId: '',
        reguDetailId: ''
      },
      visible: false,
      regu: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        recIndex: { rules: [{ required: true, message: '请输入项目序号!' }] },
        reguTitle: { rules: [{ required: true, message: '请输入项目名称!' }, { max: 64, message: '输入长度不能超过64字符!' }] }
      }
    }
  },
  methods: {
    openModal() {
      this.$refs.modalForm.showModal()
      this.$refs.mySelect.blur()
    },
    add() {
      // this.queryParam.workRecId = this.recordId
      this.edit({})
    },
    edit(record) {
      console.log(this.queryParam)
      this.queryParam.workRecId = this.recordId
      this.queryParam.reguDetailId = ''
      this.ReguName = ''
      if (record.id) {
        this.ReguName = record.reguName
      }
      this.form.resetFields()
      this.model = Object.assign({}, record)
      // this.model.status = this.status
      this.visible = true
      console.log(pick(this.model, 'recIndex', 'reguTitle'))
      this.$nextTick(() => {
        this.form.setFieldsValue(pick(this.model, 'recIndex', 'reguTitle'))
      })
    },
    onChange(value) {
      console.log(value)
      this.regu = true
      if (value.data == null) {
        this.regu = false
      }
      console.log(this.regu)
    },
    switchChange(checked) {
      if (checked) {
        this.status = 1
        this.switchCheck = true
      } else {
        this.status = 0
        this.switchCheck = false
      }
    },
    selectRegulation(data) {
      this.ReguName = data[0].title
      this.queryParam.reguDetailId = data[0].id
      if (!this.regu) {
        this.form.setFieldsValue({
          reguTitle: data[0].title
        })
      }
    },
    // 确定
    handleSubmit() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          // values.remark = (values.remark || '').trim()
          let formData = Object.assign(that.model, values, this.queryParam)
          saveOrUpdateOldWorkRecordCategory(formData)
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
              } else {
                that.$message.warning(res.message)
              }
            })
            .finally(() => {
              that.confirmLoading = false
              that.close()
            })
        }
      })
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    }
  }
}
</script>
<style scoped>
.drawer-bootom-button {
  position: absolute;
  bottom: 0;
  width: 100%;
  border-top: 1px solid #e8e8e8;
  padding: 10px 16px;
  text-align: right;
  left: 0;
  background: #fff;
  border-radius: 0 0 2px 2px;
}
</style>