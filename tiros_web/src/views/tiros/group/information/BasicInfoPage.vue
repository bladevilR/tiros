<template>
  <div class="info-wrapper info-top-wrapper" style="margin-top: 18px">
    <h4>基本信息</h4>
    <a-form>
      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="运营公司">
            <a-input v-decorator="['companyName']" disabled />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="中心">
            <a-input v-decorator="['depotName']" disabled />
          </a-form-item>
        </a-col>
      </a-row>
       <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车间">
            <a-input v-decorator="['workshopName']" disabled />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工班名称">
            <a-input v-decorator="['groupName']" disabled />
          </a-form-item>
        </a-col>
      </a-row>
       <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工班编号">
            <a-input v-decorator="['orgCode']" disabled />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="人数">
            <a-input v-decorator="['userCount']" disabled />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </div>
</template>

<script>
  import { getGroupBasicInfo } from '@api/tirosGroupApi'
  import moment from 'moment'

  export default {
    name: 'basicInfoPage',
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
    },
    methods: {
      findList() {
        this.loading = true
        getGroupBasicInfo(this.queryParam).then((res) => {
          this.loading = false
          this.tableData = res.result
          this.form.setFieldsValue({
            companyName:this.tableData.companyName,
            depotName:this.tableData.depotName,
            workshopName:this.tableData.workshopName,
            groupName:this.tableData.groupName,
            orgCode:this.tableData.orgCode,
            userCount:this.tableData.userCount,
          })
        })

      }
    }
  }
</script>

<style scoped>
  .info-wrapper {
    border: 1px solid #eee;
    position: relative;
    border-radius: 8px;
    padding: 10px;
    margin-bottom: 10px;
  }
  .info-wrapper h4 {
    position: absolute;
    top: -14px;
    padding: 1px 8px;
    margin-left: 16px;
    color: #777;
    border-radius: 2px 2px 0 0;
    background: #fff;
    font-size: 14px;
    width: auto;
  }
</style>