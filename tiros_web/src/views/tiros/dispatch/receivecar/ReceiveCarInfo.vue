<template>
  <a-form :form="form">
    <a-row :gutter="24">
      <a-row>
      <a-col style="padding:0 12px" :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
          <line-select-list :disabled="isReadonly" v-decorator="['lineId', validatorRules.lineId]" @change="changeLine"> </line-select-list>
        </a-form-item>
      </a-col>
      </a-row>
      <a-col :span="24 / 2" >
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆">
          <j-dict-select-seach-tag
            :disabled="isReadonly"
            :triggerChange="true"
            v-decorator.trim="['trainNo', validatorRules.trainNo]"
            :dictCode="dictCodeStr"
            @change="handleChangeTrainNo"
          />
        </a-form-item>
      </a-col>
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="走行公里数">
          <a-input-number
            :disabled="isReadonly"
            @change="acceptMileageChange"
            :min="0"
            :max="99999999"
            style="width: 100%"
            v-decorator.trim="['acceptMileage', validatorRules.acceptMileage]"
          />
        </a-form-item>
      </a-col>
      <a-col :span="24 / 2">
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="计划模版" prop="planTempId">
          <a-select style="width: 100%" v-decorator.trim="['planTempId', validatorRules.planTempId]"
            :disabled="isReadonly"
            @change="handleTempChange"
            :allowClear="true"
          >
            <a-select-option v-for="tp in templates" :key="tp.id">
              {{ tp.tpName }}-{{ tp.duration }}天
            </a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="修程">
          <j-dict-select-tag
            :disabled="isReadonly"
            :triggerChange="true"
            v-decorator.trim="['programId', validatorRules.programId]"
            dictCode="bu_repair_program,name,id"
            @change="onProgramChange"
          />
        </a-form-item>
      </a-col>
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="接车日期">
          <a-date-picker :disabled="isReadonly" @change="acceptDateChange" style="width: 100%" v-decorator.trim="['acceptDate', validatorRules.acceptDate]" />
        </a-form-item>
      </a-col>
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="计划交车日期">
          <a-date-picker :disabled="isReadonly" style="width: 100%" v-decorator.trim="['planFinishDate']" />
        </a-form-item>
      </a-col>
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="交付部门">
          <a-select
            :disabled="isReadonly"
            placeholder="请选择"
            v-decorator.trim="['sendDeptName', validatorRules.sendDeptId]"
            :open="false"
            :showArrow="true"
            @focus="showGroupModal"
            ref="groupSelect"
            ><a-icon slot="suffixIcon" type="ellipsis" />
          </a-select>
        </a-form-item>
      </a-col>
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="交付人">
          <a-select
            :disabled="isReadonly"
            placeholder="请选择交付人"
            v-decorator.trim="['sendUserName', validatorRules.sendUserId]"
            :open="false"
            @focus="showUserModal"
            ref="tmyuserSelect"
          >
            <a-icon slot="suffixIcon" type="ellipsis" />
          </a-select>
        </a-form-item>
      </a-col>

    </a-row>
    <!-- 年计划年份 -->
    <a-row>
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属计划年份">
          <a-date-picker
            mode="year"
            placeholder="请选择年份"
            format="YYYY"
            v-model="yearDate"
            style="width: 100%"
            :open="yearPickShow"
            @panelChange="yearPanelChange"
            @openChange="yearOpenChange"
          />
        </a-form-item>
      </a-col>
    </a-row>
    <!-- 年计划明细 -->
    <a-row>
      <a-col>
        <a-form-item :labelCol="{ xs: { span: 24 }, sm: { span: 3 } }" :wrapperCol="{ xs: { span: 24 }, sm: { span: 20 } }" label="年计划明细">
          <div style="height:calc(100vh - 600px);padding-right:2.5%" v-decorator.trim="['yearDetailId', validatorRules.yearDetailId]">
            <vxe-table
              ref="listTables"
              highlight-current-row
              show-overflow="tooltip"
              :radio-config="{trigger: 'row'}"
              height="auto"
              border
              @radio-change="radioChangeEvent"
              :data.sync="detailList"
            >
              <vxe-table-column type="radio" width="40"></vxe-table-column>
              <vxe-table-column
                width="120px"
                title="线路"
                field="lineName"
              ></vxe-table-column>
              <vxe-table-column
                width="100px"
                title="总列次"
                field="trainIndex"
                align="right"
                header-align="center"
              ></vxe-table-column>
              <vxe-table-column
                width="100px"
                title="修程次数"
                field="programIndex"
                align="right"
                header-align="center"
              ></vxe-table-column>
              <vxe-table-column
                width="200px"
                title="检修修程"
                field="programName"
              ></vxe-table-column>
              <vxe-table-column
                width="200px"
                title="开始时间"
                field="startDate"
              ></vxe-table-column>
              <vxe-table-column
                width="200px"
                title="完成时间"
                field="finishDate"
              ></vxe-table-column>
              <vxe-table-column
                width="120px"
                title="状态"
                field="status_dictText"
              ></vxe-table-column>
              <vxe-table-column
                title="备注"
                field="remark"
                align="left"
                header-align="center"
              ></vxe-table-column>
            </vxe-table>
          </div>
        </a-form-item>
      </a-col>
    </a-row>
    <a-row>
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="总列次">
          <a-input-number
            :disabled="isReadonly"
            style="width: 100%"
            id="inputNumber"
            placeholder="第几列车？"
            v-decorator.trim="['trainIndex', validatorRules.trainIndex]"
            :min="1"
            :max="99999"
          />
        </a-form-item>
      </a-col>
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="修程次数">
          <a-input-number
            :disabled="isReadonly"
            style="width: 100%"
            id="itemNo"
            v-decorator.trim="['itemNo', validatorRules.itemNo]"
            :min="1"
            :max="99999"
          />
        </a-form-item>
      </a-col>
    </a-row>

    <a-row :gutter="24">
      <a-col style="padding-bottom:50px">
        <a-form-item
          :labelCol="{ xs: { span: 24 }, sm: { span: 3 } }"
          :wrapperCol="{ xs: { span: 24 }, sm: { span: 20 } }"
          label="备注"
        >
          <a-textarea :disabled="isReadonly" :auto-size="{ minRows: 3 }" placeholder="" v-decorator.trim="['remark', validatorRules.remark]" />
        </a-form-item>
      </a-col>
    </a-row>
    <depart-window ref="departWindow" :radio="true" @ok="modalFormOk"></depart-window>
    <user-list ref="tUserModalForm" :multiple="false" @ok="onUserSelect"></user-list>
  </a-form>
</template>

<script>
import {getReveiveCarTrainDetails} from '@api/tirosDispatchApi'
import all from './mixins/common.js'
export default {
  name: 'receiveCarInfo',
  mixins:[all],
  data(){
    return {
      detailList:[],
      form: this.$form.createForm(this),
      year:'',
      yearDate: null,
      yearPickShow: false,
    }
  },
  methods:{
    radioChangeEvent({row}){
      this.form.setFieldsValue({
        trainIndex: row.trainIndex,
        itemNo: row.programIndex,
        yearDetailId: row.id
      })
      this.model.yearDetailId = row.id
    },
    yearOpenChange (status) {
      this.yearPickShow = status
    },
    // 得到年份选择器的值
    yearPanelChange (value) {
      this.yearDate=value
      this.year = value.year()
      this.acceptDateChange()
      this.yearPickShow = false
    },
    acceptDateChange(data){
      if(!this.year){
        this.year=data.year()
        this.yearDate=data
      }
      if(this.year){
        getReveiveCarTrainDetails({year:this.year, status: 1}).then((res)=>{
          if(res.success){
            this.detailList = res.result
            if (this.model.yearDetailId) {
              // 设置选择
              let idx = this.detailList.findIndex(d => d.id === this.model.yearDetailId)
              if (idx > -1) {
                setTimeout(()=>{
                  this.$refs.listTables.setCurrentRow(this.detailList[idx], true)
                },500)
              }
            }
          }
        })
      }
    },
  },
}
</script>

<style scoped lang="less">
</style>