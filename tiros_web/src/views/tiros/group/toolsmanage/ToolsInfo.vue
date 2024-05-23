<template>
  <a-card>
    <a-form :form="form">
      <a-row :gutter="24">
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工具名称">
            <a-input   v-decorator.trim="[ 'name' ]" disabled/>
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资编码">
            <a-input   v-decorator.trim="[ 'code' ]" disabled/>
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="生产厂家">
            <j-dict-select-tag
              disabled
              :triggerChange="true"
              v-decorator.trim="['supplierId' ]"
              dictCode="bu_base_supplier,name,id"
            />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="入场使用日期">
            <a-date-picker placeholder="" v-decorator.trim="[ 'entraceDate']" disabled  style="width: 100%" />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="使用寿命">
            <a-input   v-decorator.trim="[ 'lifetime']" disabled />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="入库日期">
            <a-date-picker placeholder="" v-decorator.trim="[ 'entryDate']" disabled  style="width: 100%"/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="数量">
            <a-input   v-decorator.trim="[ 'amount']" disabled />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属仓库">
            <j-dict-select-tag
              disabled
              :triggerChange="true"
              v-decorator.trim="[ 'warehouseId']"
              dictCode="bu_mtr_warehouse,name,id"
            />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态">
            <j-dict-select-tag
              disabled
              :triggerChange="true"
              v-decorator.trim="[ 'status' ]"
              dictCode="bu_tools_status"
            />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
<!--        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否系统内">
            <j-dict-select-tag
              disabled
              :triggerChange="true"
              v-decorator.trim="[ 'status1' ]"
              dictCode="bu_tools_status"
            />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否固资">
            <j-dict-select-tag
              disabled
              :triggerChange="true"
              v-decorator.trim="[ 'status2' ]"
              dictCode="bu_tools_status"
            />
          </a-form-item>
        </a-col>-->
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="送检周期">
            <a-input   v-decorator.trim="[ 'serviceInterval']" disabled />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="上次送检">
            <a-input   v-decorator.trim="[ 'lastCheckTime']" disabled />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="下次送检">
            <a-input   v-decorator.trim="[ 'nextCheckTime']" disabled />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="24" :sm="24">
          <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="描述">
            <a-textarea  v-decorator.trim="[ 'remark']" disabled />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>

  </a-card>
</template>

<script>
  import { getToolInfo } from '@api/tirosGroupApi'
  import moment from 'moment'

  export default {
    name: 'ToolsInfo',
    data() {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 15 }
        },
        labelCol1: {
          xs: { span: 24 },
          sm: { span: 2 }
        },
        wrapperCol1: {
          xs: { span: 24 },
          sm: { span: 21 }
        },
        queryParam: {
          id: this.$route.params.id
        },
        allAlign: 'center',
        tableData: {},
        form: this.$form.createForm(this)
      }
    },
    methods: {
      findList(data) {
        this.loading = true
        getToolInfo(data).then((res) => {
          this.loading = false
          this.tableData = res.result
          this.$nextTick(()=>{
            this.form.setFieldsValue({
              name:this.tableData.name,
              code:this.tableData.code,
              supplierId:this.tableData.supplierId,
              entraceDate: this.tableData.entraceDate ? moment(this.tableData.entraceDate) : '',
              lifetime:this.tableData.lifetime,
              entryDate: this.tableData.entryDate ? moment(this.tableData.entryDate) : '',
              amount:this.tableData.amount,
              warehouseId:this.tableData.warehouseId,
              model:this.tableData.model,
              serviceInterval:this.tableData.serviceInterval,
              status:this.tableData.status,
              lastCheckTime: this.tableData.lastCheckTime,
              nextCheckTime: this.tableData.nextCheckTime,
              remark: this.tableData.remark
            })
          })
        })

      }
    }
  }
</script>

<style scoped>

</style>