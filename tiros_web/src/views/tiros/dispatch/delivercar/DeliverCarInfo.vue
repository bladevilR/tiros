<template>
  <a-form :form="form">
    <a-row :gutter="24">
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
          <line-select-list :disabled="isReadonly" v-decorator="['lineId', validatorRules.lineId]" @change="changeLine"> </line-select-list>
        </a-form-item>
      </a-col>
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆">
          <!-- <j-dict-select-seach-tag
            :disabled="isReadonly"
            :triggerChange="true"
            v-decorator.trim="['trainNo', validatorRules.trainNo]"
            :dictCode="dictCodeStr"
            @change="handleTrainNo"
          /> -->
          <a-select
            title="要交车的车辆，对应的接车记录必须是‘已维修’的状态"
            show-search
            :filter-option="filterOption"
            :triggerChange="true"
            @change="handleTrainNo"
            :disabled="isReadonly"
            v-decorator.trim="['trainNo', validatorRules.trainNo]"
            allow-clear
          >
            <a-select-option :value="undefined">请选择</a-select-option>
            <a-select-option v-for="(item,k) in trainList" :key="k" :value="item.trainNo" >
              {{item.trainNo}}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="走行公里数">
          <a-input-number
            :disabled="isReadonly"
            :min="0"
            :max="99999999"
            style="width: 100%"
            @change="acceptMileageChange"
            v-decorator.trim="['acceptMileage', validatorRules.acceptMileage]"
          />
        </a-form-item>
      </a-col>
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="修程">
          <j-dict-select-tag
            disabled
            :triggerChange="true"
            v-decorator.trim="['programId', validatorRules.programId]"
            dictCode="bu_repair_program,name,id"
          />
        </a-form-item>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="接车日期">
          <a-date-picker style="width: 100%" v-decorator.trim="['acceptDate', validatorRules.acceptDate]" disabled />
        </a-form-item>
      </a-col>
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="计划交车日期">
          <a-date-picker :disabled="isReadonly" style="width: 100%" v-decorator.trim="['planFinishDate', validatorRules.planFinishDate]" />
        </a-form-item>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="交付部门">
          <!-- <j-dict-select-tag
              v-decorator.trim="[ 'sendDeptId', validatorRules.sendDeptId]"
              :triggerChange="true"
              placeholder="请选择"
              dictCode="sys_depart,depart_name,id"
              :allowClear="true"
            /> -->
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
          <!-- <j-dict-select-tag
              v-decorator.trim="[ 'sendUserId', validatorRules.sendUserId]"
              :triggerChange="true"
              placeholder="请选择"
              dictCode="sys_user,realname,id"
              :allowClear="true"
            /> -->
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
    <a-row :gutter="24">
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="总列次">
          <a-input-number
            style="width: 100%"
            id="inputNumber"
            v-decorator.trim="['trainIndex', validatorRules.trainIndex]"
            :min="1"
            :max="99999"
            disabled
          />
        </a-form-item>
      </a-col>
      <a-col :span="24 / 2">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="修程次数">
          <a-input-number
            style="width: 100%"
            id="itemNo"
            v-decorator.trim="['itemNo']"
            :min="1"
            :max="99999"
            disabled
          />
        </a-form-item>
      </a-col>
    </a-row>
    <a-row :gutter="24">
      <a-col>
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
  <!--    <span style="float: right;overflow: hidden;margin-top: 200px;" class="table-page-search-submitButtons">
      <a-space>
      <a-button type="primary" @click="handleOk">保存</a-button>
        &lt;!&ndash;      <a-button type="primary" @click="">发起流程</a-button>&ndash;&gt;
      </a-space>
    </span>-->
</template>

<script>
import all from './mixins/common.js'
export default {
  name: 'deliverCarInfo',
  mixins:[all],
  data(){
    return {
      form: this.$form.createForm(this),
    }
  },
  methods:{
    filterOption(input, option) {
      return (
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
  }
}
</script>

<style scoped>
</style>