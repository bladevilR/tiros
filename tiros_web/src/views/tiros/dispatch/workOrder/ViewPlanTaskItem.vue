<template>
  <j-modal
    :title="title"
    centered
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    cancelText="关闭"
    fullscreen
    :destroyOnClose="true"
    :footer="null"
  >
    <a-spin :spinning="confirmLoading">
      <a-form-model ref="form" v-if="taskInfo" :model="taskInfo">
        <a-row style="width: 100%">
          <!-- <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="任务编码">
            <a-input placeholder="任务编码" v-decorator.trim="[ 'taskWbs', validatorRules.taskWbs]" />
          </a-form-model-item>
        </a-col>-->
          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="上级任务" prop="ParentId">
              <a-input disabled v-model="taskInfo.ParentName" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否生成工单" prop="genOrder">
              <a-switch :value="taskInfo.genOrder ? true : false" disabled />
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否委外" prop="outsource">
              <a-switch :value="taskInfo.outsource ? true : false" disabled />
            </a-form-model-item>
          </a-col>
          <a-col :span="4">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="重要任务" prop="important">
              <a-switch :value="taskInfo.important ? true : false" disabled />
            </a-form-model-item>
          </a-col>
          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="任务名称" prop="taskName">
              <a-input v-model="taskInfo.taskName" disabled />
            </a-form-model-item>
          </a-col>
          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业手段" prop="method">
              <j-dict-select-tag v-model="taskInfo.method" dictCode="bu_regu_method" disabled />
            </a-form-model-item>
          </a-col>

          <!-- <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联规程" prop="linkRegular">
            <a-select v-model="taskInfo.linkRegular" placeholder="请选择关联规程" :open="false">
              <a-icon slot="suffixIcon" type="ellipsis" />
            </a-select>
          </a-form-model-item>
        </a-col>-->
          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆停靠" prop="trackId">
              <j-dict-select-tag dictCode="bu_mtr_track,name,id,status=1" v-model="taskInfo.trackId" disabled />
            </a-form-model-item>
          </a-col>
          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联系统" prop="systemId">
              <a-input v-model="taskInfo.systemName" disabled />
            </a-form-model-item>
          </a-col>
          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联设备" prop="assetTypeId">
              <a-select
                ref="assetType"
                v-model="taskInfo.assetTypeName"
                placeholder="请选择关联设备"
                :open="false"
                disabled
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所需工时" prop="workTime">
              <a-input-number :min="0" style="width: 100%" v-model="taskInfo.workTime" disabled />
            </a-form-model-item>
          </a-col>
          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开始日期" prop="startTime">
              <a-input style="width: 100%" v-model="taskInfo.startTime" disabled />
            </a-form-model-item>
          </a-col>
          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结束日期" prop="finishTime">
              <a-input style="width: 100%" v-model="taskInfo.finishTime" disabled />
            </a-form-model-item>
          </a-col>

          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工期" prop="taskDuration">
              <a-input-number :disabled="true" :min="0" style="width: 100%" v-model="taskInfo.taskDuration" />
            </a-form-model-item>
          </a-col>
          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业工班" prop="workGroupId">
              <j-dict-select-tag
                dictCode="sys_depart,depart_name,id,org_category=4"
                v-model="taskInfo.workGroupId"
                :triggerChange="true"
                disabled
              />
            </a-form-model-item>
          </a-col>
          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业工位">
              <a-select mode="multiple" style="width: 100%" labelInValue v-model="workStation" disabled>
                <a-select-option v-for="i in workStationList" :title="i.name" :key="i.id">{{ i.name }}</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="24 / 3">
            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="第几天" prop="dayIndex">
              <a-input-number :disabled="true" :min="0" style="width: 100%" v-model="taskInfo.dayIndex" />
            </a-form-model-item>
          </a-col>

          <a-col :span="24">
            <a-form-model-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="任务描述" prop="remark">
              <a-textarea placeholder="请输入内容" v-model="taskInfo.remark" disabled />
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="安全预想" prop="remark">
              <div v-html="taskInfo.safeNotice"></div>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-spin>
  </j-modal>
</template>

<script>
import { getTrainTaskDetail } from '@api/tirosProductionApi'
export default {
  name: 'ViewPlanTaskItemModal',
  components: {},
  data() {
    return {
      title: '操作',
      visible: false,
      taskInfo: {},
      workStationList: [],
      workStation: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 },
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 2 },
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 22 },
      },
      confirmLoading: false,
    }
  },
  methods: {
    view(record) {
      console.log(record)
      let that = this
      this.visible = true
      this.confirmLoading = true
// safeNotice
      getTrainTaskDetail(record.taskObjId).then((res) => {
        this.workStation = []
        if (res.success) {
          this.taskInfo = res.result
          this.taskInfo.safeNotice = record.safeNotice
          console.log(this.taskInfo)
          this.workStationList = res.result.workstations
          this.workStationList.forEach((item) => {
            that.workStation.push({
              key: item.id,
              label: item.name,
            })
            console.log(that.workStation)
          })
        }
        this.confirmLoading = false
      })
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
  },
}
</script>
