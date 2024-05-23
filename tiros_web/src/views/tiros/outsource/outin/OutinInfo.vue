<template>
  <a-form :form="form" >
      <a-row :gutter="24">
        <a-col :md="6" :sm="24" :offset="3">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="交接单号">
            <a-input placeholder="交接单号" v-decorator="['billNo']" />
          </a-form-item>
        </a-col>
        <a-col :md="6" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
<!--            <j-dict-select-tag
              :triggerChange="true"
              v-decorator="['lineId', validatorRules.lineId]"
              dictCode="bu_mtr_line,line_name,line_id"
            />-->
            <line-select-list
              v-decorator="['lineId', validatorRules.lineId]">
            </line-select-list>
          </a-form-item>
        </a-col>
        <a-col :md="6" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车号">
            <j-dict-select-seach-tag
              :triggerChange="true"
              v-decorator="['trainNo', validatorRules.trainNo]"
              dictCode="bu_train_info,train_no,id"
            />
          </a-form-item>
        </a-col>
      </a-row>
    <a-row :gutter="24">
      <a-col :md="6" :sm="24" :offset="3">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="交接单名">
          <a-input placeholder="交接单名" v-decorator="['outinName']" />
        </a-form-item>
      </a-col>
      <a-col :md="6" :sm="24">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="交接日期">
        <a-date-picker style="width: 100%" placeholder="交接日期" v-decorator="['transferDate']"></a-date-picker>
        </a-form-item>
      </a-col>
      <a-col :md="6" :sm="24">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业班组">
          <j-dict-select-tag
            :triggerChange="true"
            v-decorator="['sendGroupId', validatorRules.sendGroupId]"
            :dictCode="dictGroupStr"
          />
        </a-form-item>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :md="6" :sm="24" :offset="3" v-if="billType===1">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="移交人">
          <a-input placeholder="移交人" v-decorator="['transferUserId', validatorRules.transferUserId]" />
        </a-form-item>
      </a-col>
      <a-col :md="6" :sm="24" :offset="3" v-if="billType===0">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="移交人">
          <j-dict-select-tag
            :triggerChange="true"
            v-decorator="['transferUserId', validatorRules.transferUserId]"
            dictCode="sys_user,username,id"
          />
        </a-form-item>
      </a-col>
      <a-col :md="6" :sm="24" v-if="billType===1">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="接收人">
          <j-dict-select-tag
            :triggerChange="true"
            v-decorator="['receiveUser', validatorRules.receiveUser]"
            dictCode="bu_mtr_line,line_name,line_id"
          />
        </a-form-item>
      </a-col>
      <a-col :md="6" :sm="24" v-if="billType===0">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="接收人">
          <a-input placeholder="接收人" v-decorator="['receiveUser', validatorRules.receiveUser]" />
        </a-form-item>
      </a-col>
      <a-col :md="6" :sm="24">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工程师">
          <j-dict-select-tag
            :triggerChange="true"
            v-decorator="['engineerId', validatorRules.engineerId]"
            dictCode="sys_user,username,id"
          />
        </a-form-item>
      </a-col>
    </a-row>
  </a-form>

</template>

<script>
  import moment from 'moment'
  import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

  export default {
    name: 'OutinInfo',
    components:{LineSelectList},
    props:['outInDetail'],
    data(){
      return {
        dictGroupStr: 'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
        detail:{},
        model:{},
        billType:0,
        form: this.$form.createForm(this),
        validatorRules: {
          engineerId: { rules: [{ required: true, message: '请选择工程师!' }] },
          trainNo: { rules: [{ required: true, message: '请选择车号!' }] },
          sendGroupId: { rules: [{ required: true, message: '请选择作业班组!' }] },
          receiveUser: { rules: [{ required: true, message: '请输入接车人!' }] },
          lineId: { rules: [{ required: true, message: '请选择所属线路!' }] },
        },
        labelCol: {
          xs: { span: 24 },
          sm: { span:6},
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
      }
    },
    created() {
      if(this.outInDetail){
        this.detail=this.outInDetail
        this.billType=this.detail.billType
        this.edit(this.detail)
      }

    },
    methods:{
      edit(record) {
        this.form.resetFields()
        this.model = Object.assign({}, record)
        this.$nextTick(() => {
          this.form.setFieldsValue({
            billNo: this.model.billNo,
            lineId: this.model.lineId,
            trainNo: this.model.trainNo,
            transferDate: moment(this.model.transferDate || undefined),
            outinName: this.model.outinName,
            transferUserId: this.model.transferUserId,
            sendGroupId: this.model.sendGroupId,
            receiveUser: this.model.receiveUser,
            engineerId: this.model.engineerId,
          })
        })
      },
    }
  }
</script>

<style scoped>

</style>