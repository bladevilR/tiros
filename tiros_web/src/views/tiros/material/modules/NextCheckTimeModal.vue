<template>
  <a-modal
    :title="title"
    :width="500"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" :label="labelText">
              <a-date-picker v-decorator="[ 'nextCheckTime', validatorRules.nextCheckTime]" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>

  </a-modal>
</template>

<script>
  import moment from 'moment'
  import { setLastSelfCheckTime, setNextCheckTime } from '@/api/tirosMaterialApi'

  export default {
    name: 'NextCheckTimeModal',
    data() {
      return {
        // dictCodeStr:'bu_mtr_workshop_group,group_name,id',
        value: 1,
        title: '操作',
        labelText:'下次送检日期',
        visible: false,
        model: {},
        Nextid:'',
        labelCol: {
          xs: { span: 24 },
          sm: { span: 8 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 15 }
        },

        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {
          nextCheckTime: { rules: [{ required: true, message: '请选择送检时间!' }] },
        }
      }
    },

    created() {
    },
    methods: {
      next(record) {
        console.log(record)
        this.form.resetFields()
        this.Nextid = record.ids
        this.model = Object.assign( record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue({
            nextCheckTime:moment(this.model.type === 1 ? (this.model.lastSelfCheckTime || new Date()) : (this.model.nextCheckTime || new Date()), 'YYYY-MM-DD'),
          })
        })
      },

      // 确定
      handleOk() {
        const that = this
        that.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
            let formData = Object.assign(that.model, values,{
              nextCheckTime: moment(values.nextCheckTime).format('YYYY-MM-DD'),
            })

            let obj
            if(that.model.type === 1){
              obj = setLastSelfCheckTime('ids='+this.Nextid+'&'+'lastSelfCheckTime='+formData.nextCheckTime)
            }else {
              obj = setNextCheckTime('ids='+this.Nextid+'&'+'nextCheckTime='+formData.nextCheckTime)
            }
            obj
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
            this.visible = false
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

</style>