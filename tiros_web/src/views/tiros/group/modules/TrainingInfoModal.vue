<template>
  <a-modal
    :title="title"
    :width="600"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="培训名称">
              <a-input placeholder="请输入培训名称"  v-decorator.trim="[ 'title', validatorRules.title ]"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="培训类型">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="[ 'trainType', validatorRules.trainType ]"
                dictCode="bu_train_type"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="培训地点">
              <a-input placeholder="请输入培训地点"  v-decorator.trim="[ 'address', validatorRules.address ]"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="培训师">
              <a-input placeholder="请输入培训师"  v-decorator.trim="[ 'teacher', validatorRules.teacher ]"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="培训时间">
              <a-date-picker  style="width: 100%" placeholder="请选择培训时间"  format="YYYY-MM-DD" v-decorator.trim="[ 'trainTime', validatorRules.trainTime ]"></a-date-picker>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="课时">
              <a-input-number style="width: 100%"   :min=0 placeholder="请输入课时"  v-decorator.trim="[ 'timeLength', validatorRules.timeLength ]"></a-input-number>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
              <a-textarea v-decorator.trim="[ 'remark', validatorRules.remark ]" ></a-textarea>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
  </a-modal>
</template>

<script>
import moment from 'moment'

export default {
  name: 'TrainingInfoModal',
  data () {
    return {
      title: '操作',
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 10 }
      },
      isClose: false,
      confirmLoading: true,
      form: this.$form.createForm(this),
      validatorRules: {
        title: { rules: [{ required: true, message: '请输入培训名称!' }] },
        trainType: { rules: [{ required: true, message: '请选择培训类型!' }]},
        remark: { rules: [{ max:255,message:'输入长度不能超过255字符!'}] },
        address: {rules: [{ required: true, message: '请输入培训地点!' }] },
        trainTime: {rules: [{ required: true, message: '请选择培训时间!' }] },
        timeLength: {rules: [{ required: true, message: '请输入课时!' }] },
        teacher: {rules: [] },
      },
    }
  },
  methods:{
    add(){
      this.edit({})
    },
    edit(record){
      this.visible=true
      this.form.resetFields()
      if(record){
       this.model= Object.assign({},record)
        this.$nextTick(()=>{
          this.form.setFieldsValue({
            title: this.model.title,
            trainType: this.model.trainType,
            remark: this.model.remark,
            address: this.model.address,
            trainTime: moment(this.model.trainTime || new Date(), 'YYYY-MM-DD'),
            timeLength: this.model.timeLength,
            teacher: this.model.teacher
          })
        })
      }
    },

    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values)
          formData['trainTime'] =moment(values.trainTime).format('YYYY-MM-DD')
          this.visible = false
          this.$emit('setting',formData)
        }
      })
    },

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

</style>