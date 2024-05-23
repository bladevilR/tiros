<template>
  <a-card>
    <a-form :form="form">
      <a-row :gutter="24">
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件编码">
            <a-input   v-decorator.trim="[ 'materialCode']" disabled />
          </a-form-item>
        </a-col>
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件名称">
            <a-input v-decorator.trim="[ 'name']" disabled  />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件类型">
            <a-input v-decorator.trim="[ 'assetTypeName']" disabled  />
          </a-form-item>
        </a-col>
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件品牌">
            <a-input  v-decorator.trim="[ 'brand']" disabled  />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件厂商">
            <j-dict-select-tag
              :triggerChange="true"
              v-decorator="[ 'supplierId']"
              dictCode="bu_base_supplier,name,id"
              disabled
            />
          </a-form-item>
        </a-col>
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="规格型号">
            <a-input  v-decorator.trim="[ 'model']" disabled  />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="出厂编号">
            <a-input v-decorator.trim="[ 'manufNo']"  disabled />
          </a-form-item>
        </a-col>
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属位置">
            <a-input   v-decorator.trim="[ 'currentLocation']" disabled  />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属工班">
            <a-input   v-decorator.trim="[ 'groupName']" disabled  />
          </a-form-item>
        </a-col>
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属系统">
            <a-input   v-decorator.trim="[ 'systemName']" disabled  />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属线路">
            <a-input   v-decorator.trim="[ 'lineName']" disabled  />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>

  </a-card>
</template>

<script>
  import { getRotableManageInfo } from '@api/tirosGroupApi'

  export default {
    name: 'MaterialManageInfo',
    props:['assetId'],
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

        queryParam: {
          id: this.assetId
        },
        allAlign: 'center',
        tableData: [],
        form: this.$form.createForm(this)
      }
    },
    created() {
      this.findList()
    },
    methods: {
      findList() {
        this.loading = true
        getRotableManageInfo(this.queryParam).then((res) => {
          this.loading = false
          this.tableData = res.result
          this.form.setFieldsValue({
            materialCode:this.tableData.materialCode,
            name:this.tableData.name,
            assetTypeName:this.tableData.assetTypeName,
            brand:this.tableData.brand,
            model:this.tableData.model,
            supplierId:this.tableData.supplierId,
            manufNo:this.tableData.manufNo,
            currentLocation:this.tableData.currentLocation,
            groupName:this.tableData.groupName,
            systemName:this.tableData.systemName,
            lineName:this.tableData.lineName,
          })
        })

      }
    }
  }
</script>

<style scoped>

</style>