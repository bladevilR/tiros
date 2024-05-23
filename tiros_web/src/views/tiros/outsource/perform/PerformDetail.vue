<template>
  <a-modal
    :width="'85%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    :closable="true"
    :footer="null"
    :destroyOnClose="true"
  >
    <div style="height:calc(100vh - 200px)">
      <a-tabs :active-key="activeKey" @change="handleActive">
        <a-tab-pane key="1" tab="基本信息">
          <a-form :form="form">
            <a-row :gutter="24" >
              <a-col :md="8" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
                  <a-input disabled  v-decorator.trim="[ 'lineName', validatorRules.lineName]"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车号">
                  <a-input  disabled v-decorator.trim="[ 'trainNo', validatorRules.trainNo]"></a-input>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="8" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="委外部件">
                  <a-input disabled  v-decorator.trim="[ 'assetName', validatorRules.assetName]"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="委外厂商">
                  <a-input  disabled v-decorator.trim="[ 'supplierName', validatorRules.supplierName]"></a-input>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="8" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="送检日期">
                  <a-date-picker  disabled style="width: 100%;" v-decorator.trim="[ 'transferDate', validatorRules.transferDate]"></a-date-picker>
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="返回时间">
                  <a-date-picker disabled style="width: 100%;" v-decorator.trim="[ 'returnTime', validatorRules.returnTime]"></a-date-picker>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="8" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="送检班组">
                  <a-input  disabled  v-decorator.trim="[ 'sendGroupName', validatorRules.sendGroupName]"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态">
                  <a-input  disabled  v-decorator.trim="[ 'status_dictText', validatorRules.status_dictText]"></a-input>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </a-tab-pane>
<!--        <a-tab-pane key="2" tab="开工审核情况" :forceRender="true">
          <perform-resource :outinDetailId="outinDetail.id"
                            :fileType=1
          ></perform-resource>
        </a-tab-pane>
        <a-tab-pane key="3" tab="计划实现情况">
          <perform-resource  :outinDetailId="outinDetail.id"
                             :fileType=2>

          </perform-resource>

        </a-tab-pane>
        <a-tab-pane key="4" tab="质量情况">
          <perform-resource  :outinDetailId="outinDetail.id"
                             :fileType=3></perform-resource>

        </a-tab-pane>
        <a-tab-pane key="5" tab="售后响应情况">
          <perform-resource  :outinDetailId="outinDetail.id"
                             :fileType=4></perform-resource>
        </a-tab-pane>
        <a-tab-pane key="6" tab="项点评分">
          <perform-rate :outinDetailId="outinDetail.id"></perform-rate>
        </a-tab-pane>-->
        <a-tab-pane key="7" tab="文档资料">
          <perform-other-resource :outinDetailId="outinDetail.id">
          </perform-other-resource>
        </a-tab-pane>
<!--       <a-button slot="tabBarExtraContent"
                          @click="handleCancel">返回</a-button>-->
      </a-tabs>
    </div>

  </a-modal>
</template>

<script>
  import moment from 'moment'
  import PerformResource from './PerformResource'
  import PerformRate from './PerformRate'
  import PerformOtherResource from './PerformOtherResource'
  export default {
    name: 'PerformDetail',
    components:{PerformResource,PerformRate,PerformOtherResource},
    data() {
      return {
        title:'',
        confirmLoading:false,
        visible: false,
        activeKey:'1',
        outinDetail: {},
        form: this.$form.createForm(this),
        validatorRules: {
          supplierName: { rules: [{ required: true, message: '请选择委外厂商!' }] },
          sendGroupName: { rules: [{ required: true, message: '请选择送检班组!' }] },
          returnTime: { rules: [{ required: true, message: '请选择返厂时间!' }] },
          transferDate: { rules: [{ required: true, message: '请选择送修时间!' }] },
          assetName: { rules: [{ required: true, message: '请选择部件!' }] },
          trainNo: { rules: [{ required: true, message: '请选择车号!' }] },
          lineName: { rules: [{ required: true, message: '请选择线路!' }] },
          status_dictText: { rules: [{ required: true, message: '请选择状态!' }] },
        },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 15 }
        },
      }
    },
   /* created() {
      if (this.$route.params.outinDetail) {
        this.outinDetail = this.$route.params.outinDetail
      }
      this.initFrom()
    },*/
    methods: {
      show(value){
        this.outinDetail=value
        this.visible=true
        this.initFrom()
      },
      handleBack() {
        this.$router.back()
      },
      initFrom() {
        this.$nextTick(() => {
          this.form.setFieldsValue({
            lineName: this.outinDetail.lineName,
            trainNo: this.outinDetail.trainNo,
            assetName: this.outinDetail.assetName,
            supplierName: this.outinDetail.supplierName,
            transferDate: moment(this.outinDetail.transferDate || undefined),
            returnTime: moment(this.outinDetail.returnTime || undefined),
            sendGroupName: this.outinDetail.sendGroupName,
            status_dictText: this.outinDetail.status_dictText,
          })
        })
      },
      handleActive(e){
        this.activeKey=e
      },
      // 关闭
      handleCancel () {
        this.close()
      },
      close () {
        this.$emit('close')
        this.visible = false
        this.activeKey='1'
      }
    }
    }
</script>

<style scoped>

</style>