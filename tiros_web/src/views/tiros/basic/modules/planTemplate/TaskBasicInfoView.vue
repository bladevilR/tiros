<template>
  <div id="planTaskInfoView">
    <a-descriptions bordered>
      <a-descriptions-item label="上级任务">
        {{ this.taskInfo.ParentName }}
      </a-descriptions-item>
      <a-descriptions-item label="任务名称" :span="2">
        {{ this.taskInfo.taskName }}
      </a-descriptions-item>
      <a-descriptions-item label="作业手段">
        {{ this.taskInfo.method_dictText }}
      </a-descriptions-item>
      <a-descriptions-item label="车辆停靠">
        {{ this.taskInfo.trackName }}
      </a-descriptions-item>
      <!-- <a-descriptions-item label="关联系统">
        {{ this.taskInfo.systemName }}
      </a-descriptions-item>
      <a-descriptions-item label="关联设备">
        <span>{{ this.taskInfo.assetTypeName }}</span>
      </a-descriptions-item> -->
      <a-descriptions-item label="所需工时">
        {{ this.taskInfo.workTime }}
      </a-descriptions-item>
      <a-descriptions-item label="开始日期">
        {{ taskInfo.startTime ? taskInfo.startTime.format('YYYY-MM-DD') : '' }}
      </a-descriptions-item>
      <a-descriptions-item label="结束日期">
        {{ taskInfo.finishTime ? taskInfo.finishTime.format('YYYY-MM-DD') : '' }}
      </a-descriptions-item>
      <a-descriptions-item label="工期">
        {{ this.taskInfo.taskDuration }}
      </a-descriptions-item>
      <a-descriptions-item label="第几天"> 第{{ this.taskInfo.dayIndex }}天 </a-descriptions-item>
      <a-descriptions-item label="作业工班">
        {{ this.taskInfo.workGroupName }}
      </a-descriptions-item>
      <a-descriptions-item label="作业工位" :span="2">
        {{ workStationName }}
      </a-descriptions-item>
      <a-descriptions-item label="是否生成工单">
        {{ this.taskInfo.genOrder ? '是' : '否' }}
      </a-descriptions-item>
      <a-descriptions-item label="是否委外">
        {{ this.taskInfo.outsource ? '是' : '否' }}
      </a-descriptions-item>
      <a-descriptions-item label="重要任务">
        {{ this.taskInfo.important ? '是' : '否' }}
      </a-descriptions-item>
      <a-descriptions-item label="任务描述" :span="3">
        {{ this.taskInfo.remark }}
      </a-descriptions-item>
      <a-descriptions-item label="安全预想" :span="3">
        <div v-html="taskInfo.safeNotice"></div>
      </a-descriptions-item>
    </a-descriptions>
    <!-- <train-asset-type ref="assetTypeModal" title="设备选择" :trainTypeId="trainTypeId" :parent-id="taskInfo.systemId" :multiple="false" :can-select-type="[2,3,4]" @ok="onAssetTypeSelect" @cancel="onCancelAssetTypeSelect"></train-asset-type> -->
  </div>
</template>

<script>
import moment from 'moment'
import { getStationByGroupId, getStationList } from '@/api/tirosApi'
import TrainAssetType from '../../../common/selectModules/TrainAssetType'

export default {
  name: 'TaskBasicInfoView',
  components: { TrainAssetType },
  props: {
    trainTypeId: {
      type: String,
      default: '',
    },
    baseDate: {
      type: String,
      default: '',
    },
    task: {
      type: Object,
      default: null,
    },
    tasks: {
      type: Array,
      default: null,
    },
  },
  data() {
    return {
      selectValue: 2,
      workStationName: '',
      systemDictCode: "bu_train_asset_type,name,id,train_type_id='" + this.trainTypeId + "' AND struct_type=1",
      taskInfo: {
        UID: '',
        ParentId: '',
        children: null,
        taskName: '',
        genOrder: true,
        trackId: '',
        outsource: false,
        important: false,
        method: '',
        method_dictText: '',
        workTime: 0,
        systemId: '',
        assetTypeId: '',
        dayIndex: 0,
        taskDuration: 0,
        startTime: '',
        finishTime: '',
        workGroupId: '',
        workstations: [],
        planId: '',
        remark: '',
      },
      replaceFields: {
        children: 'children',
        title: 'Name',
        key: 'ID',
        value: 'UID',
      },
      formRules: {
        taskName: [{ required: true, message: '请输入任务名称', trigger: 'change' }],
        method: [{ required: true, message: '请选择作业手段', trigger: 'change' }],
        systemId: [{ required: true, message: '请选择关联系统', trigger: 'change' }],
        assetTypeId: [{ required: true, message: '请选择关联设备', trigger: 'change' }],
        startTime: [{ required: true, message: '请选择任务开始日期', trigger: 'change' }],
        finishTime: [{ required: true, message: '请选择任务结束日期', trigger: 'change' }],
      },
      // 基准日期
      baseDateMoment: moment(this.baseDate),
      workStationList: [],

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
      labelCol2: {
        xs: { span: 24 },
        sm: { span: 3 },
      },
      wrapperCol2: {
        xs: { span: 24 },
        sm: { span: 21 },
      },
    }
  },
  computed: {
    canSelectAsset() {
      if (this.taskInfo && this.taskInfo.systemId) {
        return true
      } else {
        return false
      }
    },
  },
  mounted() {
    if (this.task) {
      Object.assign(this.taskInfo, this.task)
      if (this.task.startTime) {
        this.taskInfo.startTime = new moment(this.task.startTime)
      }
      if (this.task.finishTime) {
        this.taskInfo.finishTime = new moment(this.task.finishTime)
      }
      this.taskInfo.genOrder = this.taskInfo.genOrder === 1 || this.taskInfo.genOrder === true
      this.taskInfo.outsource = this.taskInfo.outsource === 1 || this.taskInfo.outsource === true
      this.taskInfo.important = this.taskInfo.important === 1 || this.taskInfo.important === true

      // 转换任务的工位
      if (this.taskInfo.workstations && this.taskInfo.workstations.length > 0) {
        let workStations = []
        this.taskInfo.workstations.map((w) => {
          workStations.push(w.name)
        })
        this.workStationName = workStations.toString()
      }
    }
  },
  methods: {},
}
</script>

<style lang="less">
#planTaskInfoView {
  .ant-descriptions-item-label,
  .ant-descriptions-item-content {
    min-width: 110px !important;
    // padding: 15px !important;
  }
}
</style>