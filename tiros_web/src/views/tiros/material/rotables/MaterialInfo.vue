<template>
  <a-card>
    <a-form :form="form">
      <a-row :gutter="24">
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件编码">
            <a-input placeholder="请输入"  v-decorator.trim="[ 'materialCode']" disabled />
          </a-form-item>
        </a-col>
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件名称">
            <a-input placeholder="请输入" v-decorator.trim="[ 'name']" disabled  />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="上级设备">
            <a-input placeholder="请输入"  v-decorator.trim="[ '']" disabled  />
          </a-form-item>
        </a-col>
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件类型">
            <a-input placeholder="请输入" v-decorator.trim="[ 'assetTypeId']" disabled  />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件品牌">
            <a-input placeholder="请输入"  v-decorator.trim="[ 'brand']" disabled  />
          </a-form-item>
        </a-col>
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="规格型号">
            <a-input placeholder="请输入" v-decorator.trim="[ 'model']" disabled  />
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
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="出厂编号">
            <a-input placeholder="请输入" v-decorator.trim="[ 'manufNo']"  disabled />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属位置">
            <a-input placeholder="请输入"  v-decorator.trim="[ '']" disabled  />
          </a-form-item>
        </a-col>
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="位置描述">
            <a-input placeholder="请输入" v-decorator.trim="[ 'currentLocation']" disabled  />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col :md="10" :sm="24">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="位置描述">
            <a-textarea v-decorator="[ '']" disabled  />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>

  </a-card>
</template>

<script>
  import { getRotablesInfo } from '@/api/tirosMaterialApi'

  export default {
    name: 'MaterialInfo',
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
        getRotablesInfo(this.queryParam).then((res) => {
          this.loading = false
          this.tableData = res.result
          this.form.setFieldsValue({
            assetCode:this.tableData.assetCode,
            name:this.tableData.name,
            assetTypeId:this.tableData.assetTypeId,
            brand:this.tableData.brand,
            model:this.tableData.model,
            supplierId:this.tableData.supplierId,
            manufNo:this.tableData.manufNo,
            currentLocation:this.tableData.currentLocation,
          })
        })

      }
    }
  }
</script>

<style scoped>

</style>