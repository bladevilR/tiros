<template>
  <div>
    <a-modal
      :title="title"
      centered
      width="80%"
      :visible="visible"
      :confirmLoading="confirmLoading"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭"
      :destroyOnClose="true"
    >
      <a-spin :spinning="confirmLoading">
        <div style="max-height:80vh">
          <a-form :form="form">
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="任务类型">
                  <j-dict-select-tag
                    ref="taskTypeSelect"
                    :triggerChange="true"
                    v-decorator.trim="['taskType', validatorRules.taskType]"
                    dictCode="bu_task_type"
                    @change="onTaskTypeChange"
                    :disabled="disableTaskType"
                  />
                </a-form-item>
              </a-col>
              <!-- 占位 -->
              <a-col :span="12" style="height: 50px;" v-if="taskType == 5 || taskType == -1 || isNaN(taskType)"></a-col>
              <!--  -->
              <a-col :span="12" v-if="taskType===1">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="计划任务选择">
                  <a-select
                    placeholder="请选择计划任务"
                    allow-clear
                    :open="false"
                    :showArrow="true"
                    ref="planTaskSelect"
                    v-model="taskName"
                    @focus="selectPlanTask"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12" v-if="taskType===2">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="故障选择">
                  <a-select
                    placeholder="请选择故障记录"
                    allow-clear
                    :open="false"
                    :showArrow="true"
                    ref="faultSelect"
                    v-model="taskName"
                    @focus="selectFault"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12" v-if="taskType===3">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="整改选择">
                  <a-select
                    placeholder="请选择整改记录"
                    allow-clear
                    :open="false"
                    :showArrow="true"
                    ref="rectifySelect"
                    v-model="taskName"
                    @focus="selectRectify"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12" v-if="taskType===4">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业项选择">
                  <a-select
                    placeholder="请选择作业项"
                    allow-clear
                    :open="false"
                    :showArrow="true"
                    ref="regulationSelect"
                    v-model="taskName"
                    @focus="selectRegulation"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12" v-if="taskType===6">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="送修设备选择">
                  <div @click="selectStructure">
                  <a-select
                    placeholder="请选择送修设备"
                    allow-clear
                    :open="false"
                    :showArrow="true"
                    ref="structureSelect"
                    v-model="trainAssetName"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12" v-if="taskType===7">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="接收设备选择">
                  <div @click="selectStructure">
                  <a-select
                    placeholder="请选择接收设备"
                    allow-clear
                    :open="false"
                    :showArrow="true"
                    ref="structureSelect"
                    v-model="trainAssetName"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12" v-if="taskType===8">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="监修设备选择">
                  <div @click="selectStructure">
                    <a-select
                      placeholder="请选择监修设备"
                      allow-clear
                      :open="false"
                      :showArrow="true"
                      ref="structureSelect"
                      v-model="trainAssetName"
                    >
                      <a-icon slot="suffixIcon" type="ellipsis" />
                    </a-select>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="任务名称">
                  <a-input placeholder="请输入内容" v-decorator.trim="['taskName', validatorRules.taskName]" />
                </a-form-item>
              </a-col>
              <!-- <a-col :span="12">
                <a-form-item v-if="taskType<6" :labelCol="labelCol" :wrapperCol="wrapperCol" label="目标设备">
                  <div @click="openAssetSelectModal">
                    <a-select
                      ref="trainAssetSelect"
                      placeholder="请选择作业目标设备"
                      v-decorator.trim="['trainAssetName', validatorRules.trainAssetName]"
                      :open="false"
                      style="width: 100%"
                    >
                      <a-icon slot="suffixIcon" type="ellipsis" />
                    </a-select>
                  </div>
                </a-form-item>
              </a-col> -->
              <a-col :span="12">
                <a-form-item  :labelCol="labelCol" :wrapperCol="wrapperCol" label="维护手段">
                  <j-dict-select-tag
                    ref="methodSelect"
                    v-decorator.trim="['method', validatorRules.method]"
                    dictCode="bu_regu_method"
                    :triggerChange="true"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="预计工时">
                  <a-input-number placeholder="请输入内容" :max="99999999" :min="0" v-decorator.trim="['workTime', validatorRules.workTime]" style="width: 100%" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="作业工位">
                  <div @click="showStationList">
                  <a-select
                    placeholder="请选择工位"
                    allow-clear
                    :open="false"
                    :showArrow="true"
                    ref="stationName"
                    v-decorator.trim="['stationName', validatorRules.stationName]"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="任务要求">
                  <a-textarea placeholder="请输入内容" v-decorator.trim="['taskContent', validatorRules.taskContent]" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
                  <a-textarea placeholder="请输入内容" v-decorator="['remark',validatorRules.remark]" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item  :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否重要">
                  <!-- <a-space> -->
                    <a-switch @change="onImportantChange"  v-decorator.trim="['importantV', validatorRules.importantV]" />
                  <!-- </a-space> -->
                </a-form-item>
              </a-col>
            </a-row>
            <a-row v-if="operator == 1">
              <a-col :span="12">
                <a-form-item  :labelCol="labelCol" :wrapperCol="wrapperCol" label="安全预想">
                  <a-button type="primary" @click="setSafeNptice">设置安全预想</a-button>
                </a-form-item>
              </a-col>
            
            </a-row>
          </a-form>
          <br>
          <!-- 目标设备 -->
          <div class="info-wrapper info-top-wrapper">
            <h4>目标设备</h4>
            <!--  -->
            
            <TargetDeviceList :orderTaskId="model.id" :equipments.sync="equipments" :trainNo="trainNo" :lineId="lineId"/>
          </div>
        </div>
      </a-spin>
      <work-station-list ref="modalForm" @ok="onStationSelected"></work-station-list>
      <regulation-list ref="regulationModal" :onlyProject="true" @ok="onSelectRegulation"></regulation-list>
      <train-plan-task-list ref="planTaskModal" @ok="onSelectPlanTask"></train-plan-task-list>
      <fault-select-list ref="faultSelectModal" @ok="onSelectFault"></fault-select-list>
      <rectify-select ref="rectifySelectModal" @ok="onSelectRectify"></rectify-select>
      <train-asset-type ref="structureSelectModal"  @ok="onSelectStructure" :can-select-type="[2,3,4]" :multiple="false"></train-asset-type>
  <!--    <train-structure-list
        ref="trainAssetSelectModal"
        :multiple="false"
        :canSelectType="[3, 4]"
        @ok="onTrainAssetSelect"
        @cancel="onCancelTrainAssetSelect"
      ></train-structure-list>-->
      <!-- <CarDeviceSelectNew
        ref="trainAssetSelectModal"
        :trainNo="trainNo"
        :lineId="lineId"
        :multiple="true"
        :canSelectType="[2,3,4]"
        @ok="onTrainAssetSelect"
        @cancel="onCancelTrainAssetSelect"
      >
      </CarDeviceSelectNew> -->
    </a-modal>
    <a-modal
      title="设置安全预想"
      centered
      width="80%"
      :visible="visible2"
      @ok="handleOk2"
      @cancel="handleCancel2"
      cancelText="关闭"
      :destroyOnClose="true"
    >
      <j-editor
        plugins="lists link table textcolor wordcount contextmenu fullscreen"
        v-if="visible2"
        v-model="safeNotice"
        @change="(e)=>{safeNotice = e}"
        triggerChange
      ></j-editor>
    </a-modal>
  </div>
</template>

<script>
import JEditor from '@/components/jeecg/JEditor'
import { randomString } from '@/components/_util/RandomString'
import WorkStationList from '@views/tiros/common/selectModules/WorkStationList'
import RegulationList from '@views/tiros/common/selectModules/RegulationList'
import TrainPlanTaskList from '@views/tiros/common/selectModules/TrainPlanTaskList'
import FaultSelectList from '@views/tiros/common/selectModules/FaultSelectList'
import RectifySelect from '@views/tiros/dispatch/workOrder/RectifySelect'
// import CarDeviceSelectNew from '@views/tiros/common/selectModules/CarDeviceSelectNew'
// import TrainStructureList from '@views/tiros/common/selectModules/TrainStructureList'
import TrainAssetType from '@views/tiros/common/selectModules/TrainAssetType'
import TargetDeviceList from '@/views/tiros/dispatch/workOrder/TargetDeviceList.vue'

export default {
  name: 'TaskItemModal',
  components: { WorkStationList, JEditor ,TargetDeviceList ,RegulationList, TrainPlanTaskList, FaultSelectList, RectifySelect, TrainAssetType},
  props: ['lineId', 'trainNo', 'groupId', 'structureId', 'orderType','operator'],
  data() {
    return {
      safeNotice:'',
      taskType: -1,
      value: 1,
      title: '操作',
      visible: false,
      visible2: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      confirmLoading: false,
      stationDetail: null,
      form: this.$form.createForm(this),
      validatorRules: {
        taskName: { rules: [{ required: true, message: '请输入任务名称!' },{max:50,message: '输入长度不能超过50字符!'}] },
        workTime: { rules: [{ required: true, message: '请输入预计工时!' }] },
        taskType: { rules: [{ required: true, message: '请选择任务类型!' }] },
        taskContent: { rules: [{ required: true, message: '请输入任务要求!' },{max:255,message: '输入长度不能超过255字符!'}] },
        stationName: { rules: [{ required: false, message: '请选择工位!' }] },
        remark: { rules: [{max:255,message: '输入长度不能超过255字符!'}] },
        importantV: {valuePropName: 'checked'},
      },
      taskObjId: '',
      taskName: '',
      assetTypeId: '',
      structDetailId: '',
      trainAssetId: '',
      trainAssetName: '',
      disableTaskType: false,
      equipments:[],
    }
  },
  methods: {
    setSafeNptice(){
      this.safeNotice = ( this.model.safeNotice ? JSON.parse(JSON.stringify(this.model.safeNotice)) : '');
      this.$nextTick(()=>{
        this.visible2 = true
      })
    },
    add() {
      this.model = {
        id : randomString()
      };
      this.edit({ important: false},true)
    },
    edit(record,isAdd = false) {
      this.equipments = record.equipments || []
      console.log(this.equipments)

      this.form.resetFields()
      this.clearData()
      Object.assign(this.model , record, {isAdd: isAdd})
      console.log(this.model.safeNotice)
      this.visible = true
      this.taskName=record.taskName;
      this.taskObjId=record.taskObjId
      this.taskType = parseInt(record.taskType)
      this.disableTaskType = false

      this.assetTypeId = record.assetTypeId
      this.structDetailId = record.structDetailId
      this.trainAssetId = record.trainAssetId
      this.trainAssetName = record.trainAssetName || record.assetTypeName

      switch (this.orderType+'') {
        case '1':
          this.taskType = 1
          this.model.taskType = 1+''
          this.disableTaskType=true
          break
        case  '2':
          this.taskType = 2
          this.model.taskType = 2+''
          this.disableTaskType=true
          break
      }
      if (record.stationDetail) {
        this.stationDetail = record.stationDetail
      }
      this.$nextTick(() => {
        this.form.setFieldsValue({
          stationName: this.stationDetail ? this.stationDetail.name : '',
          taskName: this.model.taskName,
          workTime: this.model.workTime,
          taskType: this.model.taskType,
          taskContent: this.model.taskContent,
          remark: this.model.remark,
          method: this.model.method,
          importantV: this.model.important === 1,
          trainAssetName:  this.trainAssetName
        })
      })
    },
    onImportantChange (value) {
      if (value) {
        this.model.important = 1
      } else {
        this.model.important = 0
      }
    },
    clearData () {
      this.stationDetail=null
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          let outTask=0
          if(this.taskType===6){
            outTask=1
          }else if(this.taskType===7){
            outTask=2
          }
          that.confirmLoading = true
          if (!this.model.taskType_dictText) {
            let select = this.$refs.taskTypeSelect.getSelectItem()
            if(select){
              this.model.taskType_dictText=select.text
            }
          }

          let formData = Object.assign(that.model, values)
          formData.stationDetail = that.stationDetail
          Object.assign(formData, {
            taskObjId:this.taskObjId,
            assetTypeId: this.assetTypeId,
            structDetailId: this.structDetailId,
            trainAssetId: this.trainAssetId,
            trainAssetName: this.trainAssetName,
            important: this.model.important===true?1:this.model.important===1?1:0,
            outTask:outTask
          })

          formData.important_dictText = formData.important === 1 ? '是' : '否'
          formData.method_dictText = this.$refs.methodSelect.getSelectItemText()
          // 目标设备
          formData.equipments = this.equipments;
          console.log(formData);
          if (that.model.isAdd) {
            // Object.assign(formData, {
            //   id: randomString()
            // })
            that.$emit('addTask', formData)
          } else {
            that.$emit('editTask', formData)
          }
          if(that.stationDetail) {
            that.stationDetail.orderTaskId = formData.id
            that.stationDetail.taskName = formData.taskName
            that.$emit('addStation', that.stationDetail)
          }
          // that.$emit('ok', formData)
          that.confirmLoading = false
          that.close()
        }
      })
    },
    handleOk2() {
      this.model.safeNotice = JSON.parse(JSON.stringify(this.safeNotice));
      this.visible2 = false;
      this.safeNotice = '';
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    handleCancel2(){
      this.visible2 = false;
      this.safeNotice = '';
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    showStationList () {
      this.$refs.modalForm.showModal()
      this.$refs.stationName.blur()
    },
    onStationSelected (data) {
      if (data && data.length > 0) {
        this.form.setFieldsValue({
          stationName: data[0].name
        })
        this.stationDetail = data[0]
      } else {
        this.stationDetail = null
        this.form.setFieldsValue({
          stationName: ''
        })
      }
    },
    onTaskTypeChange (value,dataItem) {
      console.log(value,dataItem)
      
      this.taskObjId = ''
      this.taskName = ''
      this.assetTypeId = ''
      this.structDetailId = ''
      this.trainAssetId = '' // 具体车辆设备
      this.trainAssetName = ''
      if(dataItem){
        this.taskType = parseInt(value)
        this.model.taskType_dictText=dataItem.text
      }else{
        this.taskType = -1
        this.model.taskType_dictText = ''
      }
    },
    selectRegulation () {
      this.$refs.regulationModal.showModal()
      this.$refs.regulationSelect.blur();
    },
    onSelectRegulation (records) {
      if (records && records.length > 0) {
        let record = records[0]
        this.taskObjId = record.id
        this.taskName = record.title
        this.$nextTick(() => {
          this.form.setFieldsValue({
            taskName: record.title,
            workTime: record.workTime,
            taskContent: record.requirement,
            importantV: record.important === 1 ? true : false
          })
          this.model.important = record.important

        })
      }
    },
    selectPlanTask () {
      this.$refs.planTaskModal.showModal(this.lineId)
      this.$refs.planTaskSelect.blur();
    },
    onSelectPlanTask (task) {
      if (task) {
        this.taskObjId = task.UID
        this.taskName = task.Name
       /* this.assetTypeId = task.assetTypeId
        this.structDetailId = ''
        this.trainAssetId = '' // 具体车辆设备
        this.trainAssetName = ''*/
        this.$nextTick(() => {
          this.form.setFieldsValue({
            taskName: task.Name,
          })
        })
      }
    },
    selectFault () {
      this.$refs.faultSelectModal.showModal(this.lineId, this.trainNo, this.groupId)
      this.$refs.faultSelect.blur();
    },
    onSelectFault (faults) {
      if (faults && faults.length > 0) {
        let fault = faults[0]
        this.taskObjId = fault.id
        this.taskName = fault.faultDesc
        this.assetTypeId = ''
        this.structDetailId = fault.trainStructureId
        this.trainAssetId = fault.faultAssetId // 具体车辆设备
        this.trainAssetName = fault.faultAssetName
        this.$nextTick(() => {
          this.form.setFieldsValue({
            taskName: fault.faultDesc,
          })
        })
      }
    },
    selectRectify () {
      this.$refs.rectifySelectModal.showModal()
      this.$refs.rectifySelect.blur();
    },
    onSelectRectify (rectifys) {
      if (rectifys && rectifys.length > 0) {
        let rectify = rectifys[0]
        this.taskObjId = rectify.id
        this.taskName = rectify.title
        this.$nextTick(() => {
          this.form.setFieldsValue({
            taskName: rectify.title
          })
        })
      }
    },
    selectStructure () {
      this.$refs.structureSelectModal.showModal(this.structureId)
      this.$refs.structureSelect.blur();
    },
    onSelectStructure (structures) {
      if (structures && structures.length > 0) {
        let structure = structures[0]
        this.assetTypeId = structure.id
        this.structDetailId = ''
        this.trainAssetId = '' // 具体车辆设备
        this.trainAssetName = structure.name
        let typeName = this.taskType == 6 ? '送修' : '接收'
        if (this.taskType == 8) {
           typeName='监修'
        }
        this.$nextTick(() => {
          this.form.setFieldsValue({
            taskName: this.trainAssetName + '--' + typeName
          })
        })
      }
    },
    // 弹出设备选择界面
    openAssetSelectModal() {
      this.$refs.trainAssetSelectModal.showModal()
    },
    // 设备选择回调
    onTrainAssetSelect(data) {
      
    },
    // 设备选择取消回调
    onCancelTrainAssetSelect() {}
  }
}
</script>

<style lang="less" scope>
.info-wrapper {
  border: 1px solid #eee;
  position: relative;
  border-radius: 8px;
  padding: 10px;
  padding-top:15px;
  margin-bottom: 20px;
}

.info-wrapper h4 {
  position: absolute;
  top: -12px;
  padding: 1px 8px;
  margin-left: 16px;
  color: #777;
  border-radius: 2px 2px 0 0;
  background: #fff;
  font-size: 14px;
  width: auto;
}
</style>