<template>
  <a-modal
    :title="title"
    width="90%"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    centered
    :bodyStyle="{height:'60vh'}"
    :destroyOnClose="true"
  >
    <a-form-model ref="form" :model="planInfo" :rules="formRules" :labelCol="labelCol" :wrapperCol="wrapperCol">
      <a-row style="width: 100%;">
        <a-col :span="24/3">
          <a-form-model-item label="车辆段" prop="depotId">
            <j-dict-select-tag
              placeholder="请选择"
              v-model="planInfo.depotId"
              dictCode="bu_mtr_depot,name,id"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item label="车间" prop="workshopId">
            <j-dict-select-tag
              placeholder="请选择"
              v-model="planInfo.workshopId"
              dictCode="bu_mtr_workshop,name,id"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item label="所属线路" prop="lineId">
            <j-dict-select-tag
              placeholder="请选择"
              v-model="planInfo.lineId"
              dictCode="bu_mtr_line,line_name,line_id"
              triggerChange
              @change="lineChange"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item label="维修车辆" prop="exchangeId">
            <a-row :gutter="10">
              <a-select style="width:calc(100% - 186px);margin-right:10px" v-model="planInfo.exchangeId" allowClear @change="handleExchange">
                <a-select-option v-for="detail in exChanges" :key="detail.id" :value="detail.id">
                  第{{ detail.trainIndex }}列-{{ detail.trainNo }}
                </a-select-option>
              </a-select>
              <span>如果没有车辆，请先接车</span>
            </a-row>

          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item label="计划模版" prop="planTemplateId">
            <a-select v-model="planInfo.planTemplateId" @change="handleTempChange" allowClear>
              <a-select-option v-for="tp in templates" :key="tp.id">
                {{ tp.tpName }}-{{ tp.duration }}天
              </a-select-option>
            </a-select>
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item label="修程类型" prop="repairProgramId" >
            <j-dict-select-tag
              ref="repairProgram"
              disabled
              v-model="planInfo.repairProgramId"
              dictCode="bu_repair_program,name,id"/>
          </a-form-model-item>
        </a-col>

        <!-- <a-col :span="24/3">
           <a-form-model-item label="运行里程" prop="mileage">
             <a-input-number :min="0" style="width: 100%" v-model="planInfo.mileage" />
           </a-form-model-item>
         </a-col>-->

        <a-col :span="24/3">
          <a-form-model-item label="关联规程" prop="planTemplateId">
            <j-dict-select-tag
              disabled
              v-model="planInfo.reguId"
              triggerChange
              dictCode="bu_repair_regu_info,name,id,status=1"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item label="计划开始" prop="startDate">
            <!-- :disabled="planInfo.id!=undefined" -->
            <a-date-picker
              style="width: 100%"
              valueFormat="YYYY-MM-DD"
              v-model="planInfo.startDate"
              @change="changeStartDate"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item label="计划结束" prop="finishDate" title="仅做参考">
            <a-date-picker
              disabled
              valueFormat="YYYY-MM-DD"
              style="width: 100%"
              v-model="planInfo.finishDate"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item label="计划工期" prop="duration">
            <a-input-number disabled :min="0" style="width: 100%" v-model="planInfo.duration"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="24">
          <a-form-model-item label="计划名称" :labelCol="{span:2}" :wrapperCol="{span:22}" prop="planName">
            <a-input :maxLength="33" v-model="planInfo.planName"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item label="财务项目" prop="fdProject">
            <!-- <a-input :maxLength="65" v-model="planInfo.fdProject"/> -->
            <FinanceSelect type="project" @change="planInfo.fdTask = undefined" v-model="planInfo.fdProject"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="24/3">
          <a-form-model-item label="财务任务" prop="fdTask">
            <!-- <a-input :maxLength="65" v-model="planInfo.fdTask"/> -->
            <FinanceSelect :id="planInfo.fdProject" type="task" v-model="planInfo.fdTask"/>

          </a-form-model-item>
        </a-col>
        <!-- <a-col :span="24/3">
          <a-form-model-item label="开支编码" prop="fdCostType">
            <a-input :maxLength="65" v-model="planInfo.fdCostType"/>
          </a-form-model-item>
        </a-col> -->
        <a-col :span="24">
          <a-form-model-item label="备注" :labelCol="{span:2}" :wrapperCol="{span:22}" prop="remark">
            <a-textarea :maxLength="201" placeholder="请输入内容" v-model="planInfo.remark"/>
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model>
  </a-modal>
</template>

<script>
import { addTrainPlan, getTrainPlanTemplates, getTrainPlanTrains,getExchangeDetails, updateTrainPlan } from '@api/tirosDispatchApi'

import FinanceSelect from '@/views/tiros/common/finance/index.vue'

export default {
  name: 'edit',
  props: ['visible', 'plan','title'],
  components:{FinanceSelect},
  data() {
    return {
      oldValue:false,
      confirmLoading: false,
      formRules: {
        planName: [
          { required: true, message: '请输入计划名称', trigger: 'change' },
          { min: 0, max: 32, message: '输入长度不能超过32字符!' }
        ],
        depotId: [
          { required: true, message: '请选择车辆段', trigger: 'change' }
        ],
        workshopId: [
          { required: true, message: '请选择车间', trigger: 'change' }
        ],
        lineId: [
          { required: true, message: '请选择线路', trigger: 'change' }
        ],
        repairProgramId: [
          { required: true, message: '请选修程类型', trigger: 'change' }
        ],
        exchangeId: [
          { required: true, message: '请选维修车辆', trigger: 'change' }
        ],
        planTemplateId: [
          { required: true, message: '请选计划模版', trigger: 'change' }
        ],
        startDate: [
          { required: true, message: '请选择开始日期', trigger: 'change' }
        ],
        finishDate: [
          { required: true, message: '请选择结束日期', trigger: 'change' }
        ],
        duration: [
          { required: true, message: '请设置计划工期', trigger: 'change' }
        ],
        fdProject: [
          { required: false, message: '请输入所属财务项目', trigger: 'change' },
          { min: 0, max: 64, message: '输入长度不能超过64字符!' }
        ],
        fdTask: [
          { required: false, message: '请输入所属财务任务', trigger: 'change' },
          { min: 0, max: 64, message: '输入长度不能超过64字符!' }
        ],
        fdCostType: [
          { required: false, message: '请输入所属开支编码', trigger: 'change' },
          { min: 0, max: 64, message: '输入长度不能超过64字符!' }
        ],
        remark:[{ min: 0, max: 200, message: '输入长度不能超过200字符!' }],
      },
      exChanges: [],
      templates: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
      },
      planInfo: {
        depotId:undefined,
        workshopId: undefined,
        lineId:undefined,
        exchangeId:undefined,
        planTemplateId:undefined,
        remark:undefined,
        fdCostType:undefined,
        repairProgramId :undefined,
        reguId:undefined,
        startDate:undefined,
        finishDate:undefined,
        duration:undefined,
        planName:undefined,
        fdProject:undefined,
        fdTask:undefined,
      },
      exchangeName:'',//车辆段名称缓存
      repairProgramName:'', // 修程类型名称缓存
    }
  },
  beforeMount() {
    this.loadExChange()
    this.loadTemplates()
    const _plan = JSON.parse(JSON.stringify(this.plan))
    if (_plan && _plan.id) {
      this.oldValue = {
        exchangeId : _plan.exchangeId,
        planTemplateId: _plan.planTemplateId,
        startDate: this.$moment(_plan.startDate).format('YYYY-MM-DD')
      }
      this.planInfo = Object.assign({}, _plan)
    }
  },
  methods: {
    loadExChange() {
      let params = {}
      if (!!this.planInfo.lineId) {
        params = { lineId: this.planInfo.lineId }
      }
      getTrainPlanTrains({ lineId: this.planInfo.lineId }).then(res => {
        if (res.success) {
          this.exChanges = res.result
        }
      })
    },
    loadTemplates() {
      getTrainPlanTemplates().then(res => {
        if (res.success) {
          this.templates = res.result
        }
      })
    },
    handleOk() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.confirmLoading = true
          if (this.planInfo.id) {
            const oldValue = this.oldValue;
            let isEqual = true;
            for(let i in oldValue){  // 遍历循环
              if(oldValue[i] !== this.planInfo[i]){
                isEqual = false;
                break;
              }
            }
            if(isEqual){
              this.editorAction();
            }else{
              let that = this;
              this.$confirm({
                content: '修改列计划的车辆、模板、开始时间将重新生成列计划，将导致原有的数据将删除，是否继续？',
                onOk() {
                  return that.editorAction();
                },
                onCancel() {
                  that.confirmLoading = false
                },
              });
            }

          } else {
            addTrainPlan(this.planInfo).then(res => {
              this.confirmLoading = false
              if (res.success) {
                this.$emit('save_success')
                this.$message.success('保存成功')
                this.$emit('update:visible', false)
              } else {
                this.$message.error(res.message)
              }
            }).catch(err => {
              this.$message.error('保存异常')
              console.error('保存列计划异常：', err)
            })
          }
        }
      })
    },
    editorAction(){
      return updateTrainPlan(this.planInfo).then(res => {
        if (res.success) {
            this.$message.success('保存成功')
            this.$emit('save_success')
            this.$emit('update:visible', false)
        } else {
          this.$message.error(res.message)
        }
        this.confirmLoading = false
      }).catch(err => {
        this.$message.error('保存异常')
        console.error('保存列计划异常：', err)
      })
    },
    handleCancel() {
      this.$emit('update:visible', false)
    },
    getTemplateDate(e){
      if(e){
        getExchangeDetails({id:e}).then((res)=>{
          if (res.success) {
            this.planInfo.planTemplateId = res.result.planTempId;
            this.handleTempChange(res.result.planTempId);
            this.planInfo.startDate = res.result.acceptDate;
            this.changeStartDate(res.result.acceptDate)
          } else {
            this.$message.error(res.message)
          }
        })
      }
    },
    handleExchange(e) {
      this.getTemplateDate(e);
      this.exChanges.map(item => {
        if (item.id === e) {
          if(!this.plan.id){ // 新增时
            this.exchangeName = `第${ item.trainIndex }列-${ item.trainNo }车`;
            this.$set(this.planInfo, 'planName', this.exchangeName + this.repairProgramName)
          }
          this.planInfo.mileage = item.acceptMileage
          this.planInfo.trainIndex = item.trainIndex
        }
      })
    },
    changeStartDate(value) {
      console.log(value)
      if (value && this.planInfo.duration) {
        this.planInfo.finishDate = this.$moment(this.planInfo.startDate).clone()
        this.planInfo.finishDate.add(this.planInfo.duration, 'd')
        this.planInfo.finishDate = this.planInfo.finishDate.format('YYYY-MM-DD')
        console.log(this.planInfo.finishDate)
      }
    },
    handleTempChange(e) {
      // console.log('templates:', this.templates)
      this.templates.map(temp => {
        if (temp.id === e) {
          this.planInfo.reguId = temp.reguId
          this.planInfo.duration = temp.duration
          this.planInfo.repairProgramId = temp.repairProgramId
          this.$set(this.planInfo, 'fdProject', temp.fdProject)
          this.$set(this.planInfo, 'fdTask', temp.fdTask)
          this.$set(this.planInfo, 'fdCostType', temp.fdCostType)

          // 设置结束日期
          if (this.planInfo.startDate) {
            // 增加 moment 格式化  解决字符串类型下 .clone 报错
            this.planInfo.finishDate = this.$moment(this.planInfo.startDate).clone()
            this.planInfo.finishDate.add(this.planInfo.duration, 'd')
          }

         this.$nextTick(()=>{
           let proItem = this.$refs.repairProgram.getSelectItem(this.planInfo.repairProgramId)
           if(proItem) {
             this.repairProgramName = proItem.text + '计划';
             this.$set(this.planInfo, 'planName', this.exchangeName + this.repairProgramName)
             this.$forceUpdate()
           }
         })
        }
      })
    },
    lineChange() {
      this.loadExChange()
    }
  }
}
</script>

<style scoped>

</style>