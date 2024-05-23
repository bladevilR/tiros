<template>
  <a-card>
    <a-form :form="form">
      <a-row :gutter="24">
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工装名称">
            <a-input placeholder="请输入" v-decorator.trim="[ 'name' ]" disabled/>
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资编码">
            <a-input placeholder="请输入" v-decorator.trim="[ 'code' ]" disabled/>
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="生产厂家">
            <!--            <j-dict-select-tag-->
            <!--              disabled-->
            <!--              :triggerChange="true"-->
            <!--              v-decorator.trim="['supplierId' ]"-->
            <!--              dictCode="bu_base_supplier,name,id"-->
            <!--            />-->
            <a-input placeholder="请输入" v-decorator.trim="[ 'supplierName']" disabled/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="入场使用日期">
            <a-date-picker v-decorator.trim="[ 'entraceDate']" disabled/>
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="使用寿命">
            <a-input placeholder="请输入" v-decorator.trim="[ 'lifetime']" disabled/>
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="入库日期">
            <a-date-picker v-decorator.trim="[ 'entryDate']" disabled/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属工班">
            <!--            <j-dict-select-tag-->
            <!--              disabled-->
            <!--              :triggerChange="true"-->
            <!--              v-decorator.trim="[ 'groupId']"-->
            <!--              :dictCode="dictGroupStr"-->
            <!--            />-->
            <a-input placeholder="请输入" v-decorator.trim="[ 'groupName']" disabled/>
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属仓库">
            <!--            <j-dict-select-tag-->
            <!--              disabled-->
            <!--              :triggerChange="true"-->
            <!--              v-decorator.trim="[ 'warehouseId']"-->
            <!--              dictCode="bu_mtr_warehouse,name,id"-->
            <!--            />-->
            <a-input placeholder="请输入" v-decorator.trim="[ 'warehouseName']" disabled/>
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
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="下次送检日期">
            <a-date-picker v-decorator.trim="[ 'nextCheckTime']" disabled/>
          </a-form-item>
        </a-col>
        <a-col :md="8" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="资产编码">
            <a-input placeholder="请输入" v-decorator.trim="[ 'assetCode']" disabled/>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="24" :sm="24">
          <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="规格">
            <a-input placeholder="请输入" v-decorator.trim="[ 'model']" disabled/>
          </a-form-item>
        </a-col>

      </a-row>
    </a-form>

  </a-card>
</template>

<script>
  import moment from 'moment'

  export default {
    name: 'EquManageInfo',
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

        allAlign: 'center',
        toolData: {},
        form: this.$form.createForm(this)
      }
    },
    created() {
      // this.showToolData(toolId)
    },
    methods: {
      showToolData(record) {
        this.toolData = Object.assign({}, record)
        this.form.setFieldsValue({
          name: this.toolData.name,
          code: this.toolData.code,
          assetCode: this.toolData.assetCode,
          // supplierId: this.toolData.supplierId,
          supplierName: this.toolData.supplierName,
          // groupId: this.toolData.groupId,
          groupName: this.toolData.groupName,
          entraceDate: moment(this.toolData.entraceDate || new Date(), 'YYYY-MM-DD'),
          lifetime: this.toolData.lifetime,
          entryDate: moment(this.toolData.entryDate || new Date(), 'YYYY-MM-DD'),
          // warehouseId: this.toolData.warehouseId,
          warehouseName: this.toolData.warehouseName,
          nextCheckTime: moment(this.toolData.nextCheckTime || new Date(), 'YYYY-MM-DD'),
          // toolType: this.toolData.toolType,
          serviceInterval: this.toolData.serviceInterval,
          status: this.toolData.status,
          model: this.toolData.model
        })
      }
    }
  }
</script>

<style scoped>

</style>