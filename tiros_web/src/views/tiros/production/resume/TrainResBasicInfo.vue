<template>
    <a-card :body-style="cardStyle">
        <a-form :form="form">
          <a-row :gutter="24">
            <a-col :md="10" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属路线">
                <j-dict-select-tag
                  disabled
                  :triggerChange="true"
                  v-decorator.trim="['line' ]"
                />
              </a-form-item>
            </a-col>
            <a-col :md="10" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆编码">
                <a-input  v-decorator.trim="[ 'trainNo' ]" disabled/>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col :md="10" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车型">
                <j-dict-select-tag
                  disabled
                  :triggerChange="true"
                  v-decorator.trim="['trainType' ]"
                />
              </a-form-item>
            </a-col>
            <a-col :md="10" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编组数">
                <a-input v-decorator.trim="[ 'groupNum' ]" disabled/>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col :md="10" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆状态">
                <j-dict-select-tag
                  disabled
                  :triggerChange="true"
                  v-decorator.trim="['status_dictText' ]"
                />
              </a-form-item>
            </a-col>
            <a-col :md="10" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="行走里程">
                <a-input   v-decorator.trim="[ 'mileage' ]" disabled/>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col :md="10" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="投入运营">
                <a-input   v-decorator.trim="[ 'useDate' ]" disabled/>
              </a-form-item>
            </a-col>
            <a-col :md="10" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="质保有效期">
                <a-input   v-decorator.trim="[ 'warrantyDate' ]" disabled/>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col :md="10" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="停放股道">
                <j-dict-select-tag
                  disabled
                  :triggerChange="true"
                  v-decorator.trim="['track' ]"
                />
              </a-form-item>
            </a-col>
            <a-col :md="10" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属厂商">
                <j-dict-select-tag
                  disabled
                  :triggerChange="true"
                  v-decorator.trim="['supplier' ]"
                />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col :md="10" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="检修系统编码">
                <j-dict-select-tag
                  disabled
                  :triggerChange="true"
                  v-decorator.trim="['maximoTrainCode' ]"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      <a-row style="margin-top: 40px">
        <a-col :md="20" :sm="24">
<!--          <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="架大修时间">-->
<!--            <a-steps progress-dot :current="trainPeriodList.length - 1">-->
<!--&lt;!&ndash;              <a-popover slot="progressDot" slot-scope="{ index, status, prefixCls }">&ndash;&gt;-->
<!--&lt;!&ndash;                <template slot="content">&ndash;&gt;-->
<!--&lt;!&ndash;                  <span>step {{ index }} status: {{ status }}</span>&ndash;&gt;-->
<!--&lt;!&ndash;                </template>&ndash;&gt;-->
<!--&lt;!&ndash;                <span :class="`${prefixCls}-icon-dot`" />&ndash;&gt;-->
<!--&lt;!&ndash;              </a-popover>&ndash;&gt;-->
<!--              <p v-for='item in trainPeriodList' :key='item.id'>-->
<!--                <a-step :key='item.id' :title="item.startTime" :description="item.repairProgramName" />-->
<!--              </p>-->
<!--            </a-steps>-->
<!--          </a-form-item>-->
          <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="架大修时间">
            <a-steps progress-dot :current="currentStepIndex">
              <a-popover slot="progressDot" slot-scope="{ index, status, prefixCls }">
                <template slot="content">
                  <span>step {{ index }} status: {{ status }}</span>
                </template>
                <span :class="`${prefixCls}-icon-dot`" />
              </a-popover>
              <a-step v-for='(item,index) in trainPeriodList' :key='index' :title="item.date" :description="item.description" />
            </a-steps>
          </a-form-item>
        </a-col>
      </a-row>
    </a-card>
</template>

<script>
import { getTrainInfo, getTrainPeriod } from '@api/tirosProductionApi'

  export default {
    name: 'TrainResBasicInfo',
    props: ['structureDetail','trainNo'],
    data() {
      return {
        cardStyle: {
          'padding': '10px',
          'height': 'calc(100vh - 185px)'
        },
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
          sm: { span: 3 }
        },
        wrapperCol1: {
          xs: { span: 24 },
          sm: { span: 21 }
        },

        queryParam: {
          trainNo: ''
        },
        allAlign: 'center',
        tableData: [],
        form: this.$form.createForm(this),
        trainPeriodList:[],
        currentStepIndex: 0
      }
    },
    watch: {
      structureDetail: {
        immediate: true,
        handler(id) {
          this.queryParam.trainNo = id
          this.findList()
        },
      },
    },
    methods: {
      findList() {
        this.loading = true
        getTrainInfo(this.queryParam).then((res) => {
          this.loading = false
          this.tableData = res.result
          this.form.setFieldsValue({
            line:this.tableData.lineName,
            trainNo:this.tableData.trainNo,
            trainType:this.tableData.trainTypeName,
            groupNum:this.tableData.groupNum,
            status_dictText:this.tableData.status_dictText,
            mileage:this.tableData.mileage,
            useDate:this.tableData.useDate,
            warrantyDate:this.tableData.warrantyDate,
            track:this.tableData.trackName,
            supplier:this.tableData.supplierName,
            maximoTrainCode:this.tableData.id,
          })
        })
        this.findTrainPeriod()
      },
      findTrainPeriod(){
       getTrainPeriod({'trainNo':this.trainNo}).then((res)=>{
         this.trainPeriodList = res.result
         if(this.trainPeriodList.length > 0){
           this.trainPeriodList.map((item, index) => {
             if (item.isCurrent && item.isCurrent === true) {
               this.currentStepIndex = index
             }
           })
         }
       })
      },
    }
  }
</script>

<style scoped>

</style>