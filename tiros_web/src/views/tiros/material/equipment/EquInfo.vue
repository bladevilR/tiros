<template>
  <a-card>
    <a-form :form="form">
      <a-row :gutter="24">
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工装名称">
            <a-input placeholder="请输入"  v-decorator="[ 'name' ]" disabled />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资编码">
            <a-input placeholder="请输入"  v-decorator="[ 'code' ]" disabled/>
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="生产厂家">
            <j-dict-select-tag
              disabled
              :triggerChange="true"
              v-decorator="['supplierId' ]"
              dictCode="bu_base_supplier,name,id"
            />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="入场使用日期">
            <a-date-picker v-decorator="[ 'entraceDate']" disabled />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="使用寿命">
            <a-input placeholder="请输入"  v-decorator="[ 'lifetime']" disabled/>
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="入库日期">
            <a-date-picker v-decorator="[ 'entryDate']" disabled />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属工班">
            <j-dict-select-tag
              disabled
              :triggerChange="true"
              v-decorator="[ 'groupId']"
              :dictCode="dictGroupStr"
            />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属仓库">
            <j-dict-select-tag
              disabled
              :triggerChange="true"
              v-decorator="[ 'warehouseId']"
              dictCode="bu_mtr_warehouse,name,id"
            />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态">
            <j-dict-select-tag
              disabled
              :triggerChange="true"
              v-decorator="[ 'status' ]"
              dictCode="bu_tools_status"
            />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="下次送检日期">
            <a-date-picker v-decorator="[ 'nextCheckTime']" disabled />
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="资产编码">
            <a-input placeholder="请输入"  v-decorator="[ 'assetCode']" disabled/>
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="类别">
            <j-dict-select-tag
              disabled
              :triggerChange="true"
              v-decorator="[ 'toolType' ]"
              dictCode="bu_tools_type"
            />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="24" :sm="24">
          <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="规格">
            <a-input placeholder="请输入" v-decorator.trim="[ 'model']" disabled />
          </a-form-item>
        </a-col>

      </a-row>
    </a-form>

  </a-card>
</template>

<script>
  import moment from 'moment'
  import { getlistNeedCheck } from '@/api/tirosMaterialApi'
  export default {
    name: 'EquInfo',
    data() {
      return {
        dictGroupStr: 'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
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
        tableData: [],
        form: this.$form.createForm(this)
      }
    },
    created() {
      this.findList()
      // console(id);
    },
    methods: {
      findList() {
        this.loading = true
        getlistNeedCheck(this.queryParam).then((res) => {
          this.loading = false
          this.tableData = res.result
          this.form.setFieldsValue({
            name:this.tableData.name,
            code:this.tableData.code,
            assetCode:this.tableData.assetCode,
            supplierId:this.tableData.supplierId,
            groupId:this.tableData.groupId,
            entraceDate:moment(this.tableData.entraceDate || new Date(), 'YYYY-MM-DD'),
            lifetime:this.tableData.lifetime,
            entryDate:moment(this.tableData.entryDate || new Date(), 'YYYY-MM-DD'),
            warehouseId:this.tableData.warehouseId,
            nextCheckTime:moment(this.tableData.nextCheckTime || new Date(), 'YYYY-MM-DD'),
            toolType:this.tableData.toolType,
            serviceInterval:this.tableData.serviceInterval,
            status:this.tableData.status,
            model:this.tableData.model,
          })
        })

      }
    }
  }
</script>

<style scoped>

</style>