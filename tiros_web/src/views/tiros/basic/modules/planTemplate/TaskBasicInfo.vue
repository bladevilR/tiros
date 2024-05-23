<template>
  <div style="padding-bottom: 50px;">
    <a-form-model ref="form" :model="taskInfo" :rules="formRules">
      <a-row style="width: 100%;">
        <!-- <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="任务编码">
            <a-input placeholder="任务编码" v-decorator.trim="[ 'taskWbs', validatorRules.taskWbs]" />
          </a-form-model-item>
        </a-col>-->
        <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="上级任务" prop="ParentId">
            <a-input disabled v-model="taskInfo.ParentName" />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否生成工单" prop="genOrder">
            <a-switch v-model="taskInfo.genOrder" />
          </a-form-model-item>
        </a-col>
        <a-col :span="4">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否委外" prop="outsource">
            <a-switch v-model="taskInfo.outsource" />
          </a-form-model-item>
        </a-col>
        <a-col :span="4">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="重要任务" prop="important">
            <a-switch v-model="taskInfo.important" />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="任务名称" prop="taskName">
            <a-input :maxLength="51" v-model="taskInfo.taskName" />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业手段" prop="method">
            <j-dict-select-tag
              v-model="taskInfo.method"
              dictCode="bu_regu_method"
              @blur="onMethodBlur"
            />
          </a-form-model-item>
        </a-col>

       <!-- <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联规程" prop="linkRegular">
            <a-select v-model="taskInfo.linkRegular" placeholder="请选择关联规程" :open="false">
              <a-icon slot="suffixIcon" type="ellipsis" />
            </a-select>
          </a-form-model-item>
        </a-col>-->
        <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆停靠" prop="trackId">
            <j-dict-select-tag
              dictCode="bu_mtr_track,name,id,status=1"
              v-model="taskInfo.trackId"
            />
          </a-form-model-item>
        </a-col>
        <!-- <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联系统" prop="systemId">
            <j-dict-select-tag
              :dictCode="systemDictCode"
              v-model="taskInfo.systemId"
              @blur="onAssetSystemBlur"
              @change="onSystemChange"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联设备" prop="assetTypeId" >
            <a-select ref="assetType" :disabled="!canSelectAsset" v-model="taskInfo.assetTypeName" placeholder="请选择关联设备" :open="false" @focus="openAssetTypeModal">
              <a-icon slot="suffixIcon" type="ellipsis"  @click="openAssetTypeModal" />
            </a-select>
          </a-form-model-item>
        </a-col> -->
        <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所需工时" prop="workTime">
            <a-input-number :min="0" :max="99999" style="width: 100%" v-model="taskInfo.workTime" />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="第几天" prop="dayIndex">
            <a-input-number @change="dayIndexChange" :disabled="!!parentTask" :min="0" style="width: 100%" v-model="taskInfo.dayIndex" />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工期" prop="taskDuration">
            <a-input-number @change="taskDurationChange" :min="0" style="width: 100%" v-model="taskInfo.taskDuration" />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开始日期" prop="startTime" >
            <a-date-picker
              style="width: 100%"
              v-model="taskInfo.startTime"
              :disabled-date="disabledStartDate"
              @change="changeStartDate"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业工班" prop="workGroupId">
            <j-dict-select-tag
              dictCode="sys_depart,depart_name,id,org_category=4"
              v-model="taskInfo.workGroupId"
              :triggerChange="true"
              @change="changeWorkGroup"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业工位" prop="workstations">
            <a-select
              mode="multiple"
              style="width: 100%"
              labelInValue
              @select="workStationSelect"
              @deselect="workStationDeselect"
              v-model="workStations"
            >
              <a-select-option v-for="i in workStationList" :title="i.name" :key="i.id">{{ i.name}}</a-select-option>
            </a-select>
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结束日期" prop="finishTime">
            <a-date-picker
              style="width: 100%"
              v-model="taskInfo.finishTime"
              :disabled-date="disabledEndDate"
              @change="changeEndDate"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="24">
          <a-form-model-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="任务描述" prop="remark">
            <a-textarea :maxLength="65" placeholder="请输入内容" v-model="taskInfo.remark" />
          </a-form-model-item>
        </a-col>
        <a-col :span="24">
          <a-form-model-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="安全预想" prop="remark">
            <j-editor
              plugins="lists link table textcolor wordcount contextmenu fullscreen"
              v-model="taskInfo.safeNotice"
              @change="(e)=>{taskInfo.safeNotice = e}"
              triggerChange
            ></j-editor>
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model>
    <train-asset-type ref="assetTypeModal" title="设备选择" :trainTypeId="trainTypeId" :parent-id="taskInfo.systemId" :multiple="false" :can-select-type="[2,3,4]" @ok="onAssetTypeSelect" @cancel="onCancelAssetTypeSelect"></train-asset-type>
  </div>
</template>

<script>
import moment from 'moment'
import { getStationByGroupId,getStationList } from '@/api/tirosApi'
import TrainAssetType from '../../../common/selectModules/TrainAssetType'
import JEditor from '@/components/jeecg/JEditor'

export default {
  name: 'TaskBasicInfo',
  components: { TrainAssetType, JEditor },
  props: {
    trainTypeId:{
      type: String,
      default: ''
    },
    baseDate: {
      type: String,
      default: '',
    },
    task: {
      type: Object,
      default: null
    },
    tasks: {
      type: Array,
      default: null
    },
    parentTask: {
      type: Object,
      default: null,
    },
    planInfo: {
      type: Object,
      default(){
        return {}
      },
    },
  },
  data() {
    return {
      selectValue: 2,
      workStations: [],
      systemDictCode: 'bu_train_asset_type,name,id,train_type_id=\'' + this.trainTypeId + '\' AND struct_type=1',
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
        safeNotice: '',
      },
      replaceFields: {
        children: 'children',
        title: 'Name',
        key: 'ID',
        value: 'UID'
      },
      formRules: {
        taskName: [
          { required: true, message: '请输入任务名称', trigger: 'change' },
          { max: 50, message: '输入长度不能超过50个字符', trigger: 'change'}
        ],
        remark: [
          { max: 255, message: '输入长度不能超过255个字符', trigger: 'change'}
        ],
        method: [
          { required: true, message: '请选择作业手段', trigger: 'change' }
        ],
        systemId: [
          { required: true, message: '请选择关联系统', trigger: 'change' }
        ],
        assetTypeId: [
          { required: true, message: '请选择关联设备', trigger: 'change' }
        ],
        startTime: [
          { required: true, message: '请选择任务开始日期', trigger: 'change' }
        ],
        finishTime: [
          { required: true, message: '请选择任务结束日期', trigger: 'change' }
        ]
      },
      // 基准日期
      baseDateMoment: moment(this.baseDate),
      workStationList: [],

      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 2 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 22 }
      },
      labelCol2: {
        xs: { span: 24 },
        sm: { span: 3 }
      },
      wrapperCol2: {
        xs: { span: 24 },
        sm: { span: 21 }
      }
    }
  },
  computed: {
    canSelectAsset () {
      if (this.taskInfo && this.taskInfo.systemId) {
        return true
      } else {
        return false
      }
    }
  },
  mounted () {
    if(this.parentTask){
      this.taskInfo.startTime = moment(this.parentTask.startTime)
      this.changeStartDate(this.taskInfo.startTime)
    }
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
        this.workStations = []
        this.taskInfo.workstations.map(w => {
          this.workStations.push({key:w.workstationId, label: w.workstationName})
        })
      }

      if (this.parentTask && !this.taskInfo.workGroupId) {
        this.taskInfo.workGroupId = this.parentTask.workGroupId
      }

      this.workStationList=[]
      if (this.taskInfo.workGroupId) {
        getStationByGroupId(this.taskInfo.workGroupId).then((res) => {
          if(res.success) {
            this.workStationList = res.result
          }
          if (!this.workStationList || this.workStationList.length === 0) {
            // 工班没有关联工位，加载车间所以工位
            getStationList({ id: this.$store.getters.userInfo.workshopId, pageNo: 1, pageSize: 10000 }).then(res=>{
              if (res.success) {
                this.workStationList = res.result.records
              }
            })
          }
        })
      }
    }
  },
  methods: {
    moment,
    // 计算开始日期以及结束日期
    setTimeEvent(dayIndex,taskDuration){
      if(!!this.parentTask){
        return false;
      }
      // 第一个参数 = 第几天   第二个参数 = 工期
      console.log(dayIndex,taskDuration)
      if(!!this.planInfo){
        const baseDate = this.planInfo.baseDate
        if(dayIndex && taskDuration){ // 计算两个日期
          this.taskInfo.startTime = moment(baseDate).add(dayIndex-1, 'days');
          this.taskInfo.finishTime = moment(this.taskInfo.startTime).add(taskDuration-1, 'days');
        }else if(dayIndex){ // 计算开始日期
          this.taskInfo.startTime = moment(baseDate).add(dayIndex-1, 'days');
        }
      }
    },
    dayIndexChange(data){
      if(data> 0){
        this.setTimeEvent(data,this.taskInfo.taskDuration)
      }
    },
    taskDurationChange(data){
      if(data == 0){
        this.taskInfo.finishTime = null;
      }else if(data> 0){
        if(this.taskInfo.startTime){
          this.taskInfo.finishTime = moment(this.taskInfo.startTime).add(data-1, 'days');
        }
        this.setTimeEvent(this.taskInfo.dayIndex,data)
      }
    },
    validate () {
      return new Promise((resolve,reject) => {
        this.$refs.form.validate(valid => {
          if (valid) {
            resolve(true)
          } else {
            console.error('检查失败：', valid)
            reject('基本信息输入验证失败，请检查')
          }
        });
      })
    },
    setOutsource (flag) {
      this.taskInfo.outsource = flag
    },
    setImportant (flag) {
      this.taskInfo.important = flag
    },
    getData () {
      const task = mini.clone(this.taskInfo)
      if(this.taskInfo.startTime) {
        task.startTime = this.taskInfo.startTime.format("YYYY-MM-DD HH:mm:ss")
      }
      if(this.taskInfo.finishTime) {
        task.finishTime = this.taskInfo.finishTime.format("YYYY-MM-DD HH:mm:ss")
      }

      if(this.taskInfo.Start) {
        task.Start = new moment(this.taskInfo.Start).format("YYYY-MM-DD HH:mm:ss")
      }
      if(this.taskInfo.Finish) {
        task.Finish = new moment(this.taskInfo.Finish).format("YYYY-MM-DD HH:mm:ss")
      }

      task.genOrder = task.genOrder ? 1 : 0
      task.outsource = task.outsource ? 1 : 0
      task.important = task.important ? 1 : 0

      // 转换任务工位
      task.workstations = []
      if (this.workStations.length > 0) {
        this.workStations.map(w => {
          task.workstations.push({'taskId':task.UID, 'workstationId':w.key, 'workstationName': w.label})
        })
      }

      return task;
    },
    // 设置不可选择的开始日期
    disabledStartDate(value) {
      /*let endValue = this.taskInfo.finishTime
      if (endValue) {
        return value < this.baseDateMoment || endValue < value
      } else {
        return value < this.baseDateMoment
      }*/
      if(!!this.parentTask){
        return value < moment(this.parentTask.startTime)
      }else{
        return value < this.baseDateMoment
      }

    },
    // 设置不可选的结束日期
    disabledEndDate(value) {
      let startValue = this.taskInfo.startTime
      if (!startValue || !value) {
        return false
      }
      return value < startValue
    },
    // 开始日期改变
    changeStartDate(value) {
      // this.taskInfo.startTime = value
      if(value) {
        let dayIndexNum = value.diff(this.baseDateMoment, 'day')
        // console.log(moment(this.baseDateMoment).format('YYYY-MM-DD'),dayIndexNum)
        this.taskInfo.dayIndex = dayIndexNum // 与基准日期同一天算第一天
        // 设置完成日期为同一天
        this.taskInfo.finishTime = value
      }

      // 计算工期
      let endValue = this.taskInfo.finishTime
      if (value && endValue) {
        let durationNum = moment(endValue).diff(value, 'day')
        this.taskInfo.taskDuration = durationNum + 1 // 在同一天也算一天
      }
    },
    // 结束日期改变
    changeEndDate(value) {
      let startValue = this.taskInfo.startTime
      // 计算工期
      if (value && startValue) {
        let duration = moment(value).diff(startValue, 'day')
        this.taskInfo.taskDuration = duration + 1 // 在同一天也算一天
      }
    },
    // 工班改变
    changeWorkGroup(value) {
      // console.log('班组改变了:', value)
      getStationByGroupId(value).then((res) => {
        this.workStationList = res.result
      })
    },
    // 工位选择
    workStationSelect (value) {
      // console.log('workstation select:', value)
    },
    // 工位取消选择
    workStationDeselect (value) {

    },
    // 弹出设备选择界面
    openAssetTypeModal() {
      if (!this.canSelectAsset) {
        return;
      }
      console.log('systemId:', this.taskInfo.systemId)
      this.$refs.assetTypeModal.showModal()
      // this.$refs.assetType.focus()
      this.$refs.assetType.blur()
    },
    // 设备选择回调
    onAssetTypeSelect(data) {
      if (data.length > 0) {
        const item = data[0]
        if (item) {
          this.taskInfo.assetTypeName = item.name
          this.taskInfo.assetTypeId = item.id
        } else {
          this.taskInfo.assetTypeName = ''
          this.taskInfo.assetTypeId = ''
        }
        this.$forceUpdate()
        this.$refs.form.validateField('assetType')
      }
    },
    // 设备选择取消回调
    onCancelAssetTypeSelect () {
      this.$refs.form.validateField('assetType')
    },
    onMethodBlur () {
      this.$refs.form.validateField('method')
    },
    onAssetSystemBlur () {
      this.$refs.form.validateField('assetSystem')
    },
    onSystemChange (e) {

    },
    onParentTaskChange(value) {
      this.$refs.form.validateField('parentTask')
    }
  },
}
</script>

<style>
</style>