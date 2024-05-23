<template>
  <div :style="`height: calc(100vh - ${getButtonShow() ? '47px' : '4px'});`">
    <a-form layout='inline' v-if='getButtonShow()'>
      <a-row :gutter='24'>
        <a-col :md='24'>
          <a-form-item label='表单名称:'>
            <a-input v-model='searchText' placeholder='表单名称' @change='sortTable' allowClear></a-input>
          </a-form-item>
          <a-form-item label='表单类型:'>
            <a-select v-model='searchCategory' style='width: 120px' @change='sortTable' allowClear>
              <a-select-option :value='3'> 作业记录表</a-select-option>
              <a-select-option :value='1'> 数据记录表</a-select-option>
              <a-select-option :value='4'> 检查记录表</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item style=''>
            <a-space>
              <!--              显示方式：  去掉，因为现在就一个任务
              <a-select v-model="displayModel" style="width: 120px" @change="handleChange">
                <a-select-option value="1">
                  合并显示
                </a-select-option>
                <a-select-option value="0">
                  展开显示
                </a-select-option>
              </a-select>-->
              <a-button type='dashed' class='primary-color' @click='handleAdd' :disabled="displayModel === '1'">关联表单
              </a-button>
              <a-button type='dashed' @click='handleDel' :disabled="displayModel === '1'">删除关联</a-button>
              <!--              <a-button class="primary-color" @click="openFormDetaile" v-if="displayModel !== '1' && operator === 1" :disabled="!btnStatus.edit">设置填写明细</a-button>-->
            </a-space>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <!--  现在就一个任务  <a-form layout="inline" v-if="!getButtonShow()">
      <a-form-item label="显示方式" >
      <a-select v-model="displayModel" style="width: 120px" @change="handleChange">
        <a-select-option value="1">
          合并显示
        </a-select-option>
        <a-select-option value="0">
          展开显示
        </a-select-option>
      </a-select>
      </a-form-item>
    </a-form>-->
    <div na-flex-height-full>
      <vxe-table
        border
        v-if="displayModel === '1'"
        ref='listTable'
        align='center'
        max-height='auto'
        :data='sumTableDate'
        :edit-rules='validRules'
        :keep-source='true'
        :filter-config='{ showIcon: false }'
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
        @checkbox-change='btnStatus.update()'
        @checkbox-all='btnStatus.update()'
      >
        <vxe-table-column type='seq' width='40'></vxe-table-column>
        <vxe-table-column type='checkbox' width='40'></vxe-table-column>
        <vxe-table-column
          v-if='tasks && tasks.length > 1'
          field='taskName'
          title='工单任务'
          align='left'
          :edit-render="{ name: 'input' }"
        >
          <template v-slot:edit='{ row }'>
            <a-select v-model='row.workOrderTaskId' style='width: 100%' @change='taskSelectChange'>
              <a-select-option v-for='(task, index) in tasks' :value='task.id' :key='index'>
                {{ task.taskName }}
              </a-select-option>
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field='title'
          title='表单名称'
          width='25%'
          :edit-render="{ name: 'input' }"
          align='left'
          :filters="[{ data: '' }]"
          :filter-method='customStringFilterMethod'
          header-align='center'
        >
          <template v-slot:edit='{ row }'>
            <a-select
              ref='formSelect'
              v-model='row.formName'
              placeholder='请选择表单'
              :open='false'
              style='width: 100%'
              @focus='openFormSelectModal(row)'
            >
              <a-icon slot='suffixIcon' type='ellipsis' @click='openFormSelectModal(row)' />
            </a-select>
          </template>
          <template v-slot='{ row }'>
            <a @click.stop='jumpInfo(row)'>{{ row.formName }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field='instType_dictText' title='表单类型' width='100' :edit-render="{ name: 'input' }">
          <template v-slot:edit='{ row }'>
            <a-input placeholder='请选择表单' disabled v-model='row.instType_dictText' />
          </template>
        </vxe-table-column>
        <vxe-table-column field='reguName' title='所属规程' width='120'>
          <template v-slot='{ row }'>
            {{ row.reguName }}
          </template>
          <template v-slot:edit='{ row }'>
            <a-input placeholder='请选择表单' disabled v-model='row.reguName' />
          </template>
        </vxe-table-column>
        <vxe-table-column field='reguVersion' title='规程版本' width='120'>
          <template v-slot='{ row }'>
            {{ row.reguVersion }}
          </template>
          <template v-slot:edit='{ row }'>
            <a-input placeholder='请选择表单' disabled v-model='row.reguVersion' />
          </template>
        </vxe-table-column>
        <vxe-table-column
          field='trainAssetName'
          title='关联设备'
          :edit-render="{ name: 'input' }"
          align='left'
          header-align='center'
          :formatter='formatterAssetName'
        >
          <template v-slot:edit='{ row }'>
            <a-select
              ref='trainAssetSelect'
              v-model='row.trainAssetName'
              placeholder='请选择关联设备'
              :open='false'
              style='width: 100%'
              @focus='openAssetSelectModal(row)'
            >
              <a-icon slot='suffixIcon' type='ellipsis' @click='openAssetSelectModal(row)' />
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column field='status_dictText' title='已填写？' width='100'></vxe-table-column>
        <vxe-table-column field='checkUserName' title='检查' width='100'></vxe-table-column>
        <vxe-table-column
          field='remark'
          title='备注'
          :edit-render="{ name: '$input', props: { placeholder: '请输入备注' } }"
          align='left'
          header-align='center'
        >
          <!-- <template v-slot:edit="{ row }">
          <a-input @change="remarkChange(row)" :maxLength="201" placeholder="请输入备注" v-model="row.remark" />
        </template> -->
        </vxe-table-column>
        <vxe-table-column title='操作' width='150' v-if='operator===6'>
          <template v-slot='{ row }'>
            <template>
              <a-button size='small' @click.stop='jumpInfo(row)' :disabled='operator!==6'>检查填写</a-button>
            </template>
          </template>
        </vxe-table-column>
      </vxe-table>
      <vxe-table
        border
        v-if="displayModel === '0'"
        ref='listTable'
        align='center'
        max-height='auto'
        :data.sync='taskForms'
        :edit-rules='validRules'
        :keep-source='true'
        :filter-config='{ showIcon: false }'
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
        @checkbox-change='btnStatus.update()'
        @checkbox-all='btnStatus.update()'
      >
        <vxe-table-column type='seq' width='40'></vxe-table-column>
        <vxe-table-column type='checkbox' width='40'></vxe-table-column>
        <vxe-table-column
          v-if='tasks && tasks.length > 1'
          field='taskName'
          title='工单任务'
          align='left'
          :edit-render="{ name: 'input' }"
        >
          <template v-slot:edit='{ row }'>
            <a-select v-model='row.workOrderTaskId' style='width: 100%' @change='taskSelectChange'>
              <a-select-option v-for='(task, index) in tasks' :value='task.id' :key='index'>
                {{ task.taskName }}
              </a-select-option>
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field='formName'
          title='表单名称'
          width='25%'
          :edit-render="{ name: 'input' }"
          align='left'
          header-align='center'
          :filters="[{ data: '' }]"
          :filter-method='customStringFilterMethod'
        >
          <template v-slot:edit='{ row }'>
            <a-select
              ref='formSelect'
              v-model='row.formName'
              placeholder='请选择表单'
              :open='false'
              style='width: 100%'
              @focus='openFormSelectModal(row)'
            >
              <a-icon slot='suffixIcon' type='ellipsis' @click='openFormSelectModal(row)' />
            </a-select>
          </template>
          <template v-slot='{ row }'>
            <a @click.stop='jumpInfo(row)'>{{ row.formName }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field='instType_dictText' title='表单类型' width='100' :edit-render="{ name: 'input' }">
          <template v-slot:edit='{ row }'>
            <a-input placeholder='请选择表单' disabled v-model='row.instType_dictText' />
          </template>
        </vxe-table-column>
        <vxe-table-column field='reguName' title='所属规程' width='120'>
          <template v-slot='{ row }'>
            {{ row.reguName }}
          </template>
          <template v-slot:edit='{ row }'>
            <a-input placeholder='请选择表单' disabled v-model='row.reguName' />
          </template>
        </vxe-table-column>
        <vxe-table-column field='reguVersion' title='规程版本' width='120'>
          <template v-slot='{ row }'>
            {{ row.reguVersion }}
          </template>
          <template v-slot:edit='{ row }'>
            <a-input placeholder='请选择表单' disabled v-model='row.reguVersion' />
          </template>
        </vxe-table-column>
        <vxe-table-column
          field='trainAssetName'
          title='关联设备'
          :edit-render="{ name: 'input' }"
          align='left'
          header-align='center'
          :formatter='formatterAssetName'
        >
          <template v-slot:edit='{ row }'>
            <a-select
              ref='trainAssetSelect'
              v-model='row.trainAssetName'
              placeholder='请选择关联设备'
              :open='false'
              style='width: 100%'
              @focus='openAssetSelectModal(row)'
            >
              <a-icon slot='suffixIcon' type='ellipsis' @click='openAssetSelectModal(row)' />
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column field='status_dictText' title='已填写？' width='100'></vxe-table-column>
        <vxe-table-column field='checkUserName' title='专验' width='100'>
          <template v-slot='{ row }'>
            <span v-if='(row.instType === 3 || row.instType === 4) && !row.checkUserName'
            >未检查</span
            >
            <span v-else>{{ row.checkUserName }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field='remark'
          title='备注'
          :edit-render="{ name: '$input', props: { placeholder: '请输入备注' } }"
          align='left'
          header-align='center'
        >
          <!-- <template v-slot:edit="{ row }">
          <a-input @change="remarkChange(row)" :maxLength="201" placeholder="请输入备注" v-model="row.remark" />
        </template> -->
        </vxe-table-column>
        <!-- <vxe-table-column title="操作" width="150" v-if="getButtonShow()">
          <template v-slot="{ row }">
            <template v-if="$refs.listTable.isActiveByRow(row)">
              <a-space>
                <a-button type="dashed" size="small" @click.stop="saveRowEvent(row)">保存</a-button>
                <a-button type="dashed" size="small" @click.stop="cancelRowEvent(row)">取消</a-button>
              </a-space>
            </template>
            <template v-else>
              <a-button type="dashed" size="small" @click.stop="editRowEvent(row)" :disabled="displayModel === '1'"
                >编辑
              </a-button>
            </template>
          </template>
        </vxe-table-column> -->
      </vxe-table>
    </div>
    <form-template ref='formTemplate'></form-template>
    <FormsList ref='modalForm' :multiple='false' @ok='onSelectForm'></FormsList>
    <FormsList ref='multipleSelect' :multiple='true' @ok='multipleSelectChange'></FormsList>
    <!--    <train-structure-list
      ref="trainAssetSelectModal"
      :multiple="false"
      :canSelectType="[3, 4]"
      @ok="onTrainAssetSelect"
      @cancel="onCancelTrainAssetSelect"
    ></train-structure-list>-->
    <CarDeviceSelect
      ref='trainAssetSelectModal'
      :train-no='trainNo'
      :multiple='false'
      :canSelectType='[3, 4]'
      @ok='onTrainAssetSelect'
      @cancel='onCancelTrainAssetSelect'
    />
    <ViewWorkForm ref='viewWorkForm' :operator='operator' @checkSuccess='onCheckSuccess'></ViewWorkForm>
    <FormViewModal ref='formViewModal'></FormViewModal>
    <RecordTableView ref='recordTableView'></RecordTableView>
    <JobCheckTableView ref='jobCheckTableView'></JobCheckTableView>
    <!-- <TaskFormModel ref="taskFormModel" :train-no="trainNo" :check-list="taskForms" :plan-id="planId" @ok="onAddForm"></TaskFormModel> -->
    <TaskFormSelectModel
      ref='formSelectModel'
      :check-list='taskForms'
      :train-no='trainNo'
      :lineId='workOrderInfo.lineId'
      :repairId='repairId'
      :operator='operator'
      :workOrderId='workOrderInfo.id || workOrderInfo.orderId'
      :workOrderTaskId='defaultTaskId'
      @ok='onAddForm'
    ></TaskFormSelectModel>
    <TaskFormDetaileModal ref='taskFormDetaileModal' :workOrderInfo='workOrderInfo' :tableForm='taskForms'
                          @ok='onSelectFormDetaile'></TaskFormDetaileModal>
  </div>
</template>

<script>
import { randomUUID } from '@/utils/util'
import FormsList from '@views/tiros/common/selectModules/FormsList'
import CarDeviceSelect from '@views/tiros/common/selectModules/CarDeviceSelect'
import TrainStructureList from '@views/tiros/common/selectModules/TrainStructureList'
import FormTemplate from '@views/tiros/common/form/FormTemplate'
import ViewWorkForm from '@views/tiros/dispatch/workOrder/ViewWorkForm'
import FormViewModal from '@views/tiros/basic/customform/FormViewModal'
import RecordTableView from '@views/tiros/basic/modules/workRecordSheet/RecordTableView'
import JobCheckTableView from '@views/tiros/basic/modules/jobCheckSheet/JobCheckTableView'
import { getWorkcheck } from '@api/tirosQualityApi'
import TaskFormSelectModel from '@views/tiros/dispatch/modules/TaskFormSelectModel'
import TaskFormDetaileModal from '@views/tiros/dispatch/modules/TaskFormDetaileModal'

import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'TaskForms',
  components: {
    FormsList,
    TrainStructureList,
    FormTemplate,
    ViewWorkForm,
    CarDeviceSelect,
    FormViewModal,
    RecordTableView,
    JobCheckTableView,
    TaskFormSelectModel,
    TaskFormDetaileModal
  },
  props: {
    taskForms: {
      type: Array,
      default: () => {
        return []
      }
    },
    workOrderInfo: {
      type: Object,
      default: () => {
        return {}
      }
    },
    tasks: {
      type: Array,
      default: () => {
        return []
      }
    },
    repairId: {
      type: [Number, String],
      default: null
    },
    trainNo: {
      type: String,
      default: ''
    },
    planId: {
      type: String,
      default: ''
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    operator: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      defaultTaskId: '',
      displayModel: '0', // 表单： 0 不合并显示  1 合并显示
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      searchText: '',
      searchCategory: null,
      validRules: {
        formName: [{ required: true, message: '表单必须选择', trigger: 'manual' }],
        copies: [{ required: true, message: '数量必须填写', trigger: 'manual' }],
        remark: [{ max: 200, type: 'string', message: '备注不能超过200个字符' }]
      },
      sumTableDate: [],
      btnStatus: new TableBtnUtil(this, 'listTable', {
        attrs: [
          {
            key: 'edit',
            judge: (e) => e.instType === 3 || e.instType === 4
          }
        ]
      })
    }
  },
  watch: {
    taskForms(v1, v2) {
      if (this.operator === 4 || this.operator === 6 || this.readOnly) {
        // this.sumTableDate = this.groupArr(this.taskForms)  任务只有一个，去掉合并
        this.sumTableDate = this.taskForms
        this.btnStatus.update()
      }
    }
  },
  mounted() {
    if (this.operator === 4 || this.operator === 6 || this.readOnly) {
      // this.sumTableDate = this.groupArr(this.taskForms) 任务只有一个，去掉合并
      this.sumTableDate = this.taskForms
      this.displayModel = '1'
    }
    if (this.tasks && this.tasks.length > 0) {
      this.defaultTaskId = this.tasks[0].id
    }
  },
  methods: {
    onCheckSuccess(formInfo, checkInfo) {
      this.taskForms.forEach(form => {
        if (form.id === formInfo.id) {
          form.checkUserName = checkInfo.checkUserName
        }
      })
      this.$forceUpdate()
    },
    remarkChange({ remark }) {
      if (remark.length > 200) {
        this.$message.error('备注不能超过200个字符')
        return false
      } else {
        return true
      }
    },
    handleChange(value) {
      if (this.displayModel === '1') {
        this.sumTableDate = this.groupArr(this.taskForms)
      }
    },
    // 将不任务与同一表单实例的记录合并
    groupArr(arr) {
      let dataMap = {}
      for (let i = 0; i < arr.length; i++) {
        let item = arr[i]
        let key = 'A' + item.formInstId
        let data = dataMap[key]
        if (!data) {
          data = {}
          data.taskName = ''
          data.instType = item.instType
          data.formName = item.formName
          data.formInstId = item.formInstId
          data.instType_dictText = item.instType_dictText
          data.trainAssetName = item.trainAssetName || item.assetTypeName
          data.remark = item.remark
          data.status_dictText = item.status_dictText
          data.checkUserName = item.checkUserName
          dataMap[key] = data
        }
        data.taskName += item.taskName
      }
      let reArr = []
      for (let attr in dataMap) {
        reArr.push(dataMap[attr])
      }
      return reArr
    },
    getButtonShow() {
      if (this.readOnly) {
        return false
      }
      if (this.operator === 0 || this.operator === 1) {
        return true
      } else {
        return false
      }
    },
    handleAdd() {
      if (!this.tasks.length) {
        this.$message.warning('请先添加任务')
        return
      } else if (this.$refs.listTable.getActiveRecord()) {
        return
      }

      // this.$refs.multipleSelect.showModal()
      // console.log('列计划ID： ' + this.planId)
      this.$refs.formSelectModel.showModal(this.planId)

      // this.curEditMode = 1
      // let form = {
      //   id: randomUUID(),
      //   planId: this.planId,
      //   workOrderTaskId: ''
      // }

      // if (this.tasks.length) {
      //   Object.assign(form, {
      //     workOrderTaskId: this.tasks[0].id,
      //     taskName: this.tasks[0].taskName,
      //   })
      // }

      // this.$refs.listTable.insertAt(form, -1).then(({ row }) => {
      //   this.$refs.listTable.setActiveCell(row, 'formName')
      // })
    },
    handleDel() {
      let m = this.$refs.listTable.getCheckboxRecords()
      if (m.length > 0) {
        this.$confirm({
          content: `是否删除选中${m.length}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            m.map((item1) => {
              this.taskForms.map((item2, index2) => {
                if (item1.id === item2.id) {
                  this.taskForms.splice(index2, 1)
                }
              })
            })
            this.btnStatus.update()
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    editRowEvent(row) {
      this.curEditMode = 2
      this.$refs.listTable.setActiveRow(row)
    },
    delRowEvent(row) {
      this.taskForms.map((item2, index2) => {
        if (row.id === item2.id) {
          this.taskForms.splice(index2, 1)
        }
      })
    },
    saveRowEvent(row) {
      this.$refs.listTable.validate(row, (valid) => {
        if (!valid) {
          if (this.curEditMode === 1) {
            this.taskForms.push(row)
          }
          this.$refs.listTable.clearActived()
          this.curEditMode = 0
        } else {
          for (const validKey in valid) {
            let vals = valid[validKey]
            vals.forEach((item) => {
              if (item.rule) {
                this.$message.error(item.rule.message)
              }
            })
          }
        }
      })
    },
    cancelRowEvent(row) {
      this.$refs.listTable.clearActived()
      if (this.curEditMode === 1) {
        // 新增,点击取消
        this.$refs.listTable.remove(row)
      } else if (this.curEditMode === 2) {
        // 还原行数据
        this.$refs.listTable.revertData(row)
      }
      this.curEditMode = 0
    },
    openFormSelectModal(row) {
      this.curRow = row
      this.$refs.modalForm.showModal()
      this.$refs.formSelect.blur()
    },
    onSelectForm(data) {
      if (data && data.forms.length > 0) {
        const form = data.forms[0]

        // 判断选择的表单是否已经存在
        let flag = 0
        for (let i = 0, len = this.taskForms.length; i < len; i++) {
          const f = this.taskForms[i]
          if (f.formObjId == form.id) {
            flag = 1
            break
          }
        }

        if (flag === 1) {
          this.$message.error('你选择的表单已经在列表中存在，请选择其他表单')
          return
        }

        this.curRow.formName = form.title
        this.curRow.formObjId = form.id
        this.curRow.formCode = form.code
        //  1表示来自列计划生成2表示来自工单添加
        this.curRow.fromBy = 2

        this.curRow.formInstId = ''
        this.curRow.instType = data.formType
        this.curRow.planFormId = ''
        this.curRow.instType_dictText = data.formTypeName
        this.curRow.copies = 1
        this.curRow.status_dictText = '未填写'
        // 1  表示 来自列计划生成， 2 表示来自工单添加
        this.curRow.fromBy = 2
        if (form.assetTypeId) {
          this.curRow.assetTypeName = form.assetTypeName
          this.curRow.assetTypeId = form.assetTypeId
        }

        this.curRow.isNew = true
      } else {
        this.curRow.formName = ''
        this.curRow.objId = ''
        this.curRow.formType = ''
        this.curRow.formType_dictText = ''
        this.curRow.copies = 0
        this.curRow.fromBy = 2
        this.curRow.isNew = true
      }
    },
    // 弹出设备选择界面
    openAssetSelectModal(row) {
      this.curRow = row
      this.$refs.trainAssetSelectModal.showModal()
      this.$refs.trainAssetSelect.blur()
    },
    // 设备选择回调
    onTrainAssetSelect(data) {
      if (data.length) {
        const item = data[0]
        if (item) {
          this.curRow.trainStructName = item.assetName
          this.curRow.trainStructId = item.structDetailId
          this.curRow.trainAssetId = item.id
          this.curRow.trainAssetName = item.assetName
          this.curRow.assetTypeId = item.assetTypeId
        } else {
          this.curRow.trainStructName = ''
          this.curRow.trainStructId = ''
          this.curRow.trainAssetId = ''
          this.curRow.trainAssetName = ''
        }
      } else {
        this.curRow.trainStructName = ''
        this.curRow.trainStructId = ''
        this.curRow.trainAssetId = ''
        this.curRow.trainAssetName = ''
      }
    },
    multipleSelectChange(data) {
      let len = data.forms.length

      for (let i = 0; i < len; i++) {
        const item = data.forms[i]
        // 判断选择的表单是否已经存在
        let flag = 0

        // 表单重复检查暂时去掉，因为同一份表单可能要填写多份
        // const taskForms = this.taskForms
        /*for (let i = 0, len = taskForms.length; i < len; i++) {
          const f = taskForms[i];
          if (f.formObjId == item.id) {
            flag = 1;
            this.$message.warn(`你选择的${item.title}已经在列表中存在`)
            break;
          }
        }*/

        if (flag !== 1) {
          let form = {
            id: randomUUID(),
            planId: this.planId,
            workOrderTaskId: '',
            formName: item.title,
            formObjId: item.id,
            formCode: item.code,
            //  1表示来自列计划生成2表示来自工单添加
            fromBy: 2,
            formInstId: '',
            instType: data.formType,
            planFormId: '',
            instType_dictText: data.formTypeName,
            copies: 1,
            status_dictText: '未填写',
            trainStructName: '',
            trainStructId: '',
            trainAssetId: '',
            trainAssetName: '',
            assetTypeId: '',
            assetTypeName: ''
          }

          if (this.tasks.length) {
            Object.assign(form, {
              workOrderTaskId: this.tasks[0].id,
              taskName: this.tasks[0].taskName
            })
          }
          if (item.assetTypeId) {
            form.assetTypeName = item.assetTypeName
            form.assetTypeId = item.assetTypeId
          }
          form.isNew = true
          //  注释 by jakgong , 添加后无需点击确定
          /* if(len == 1){
            this.curEditMode = 1;
            this.$refs.listTable.insertAt(form, -1).then(({ row }) => {
              this.$refs.listTable.setActiveCell(row, 'formName')
            })
          }else{
            this.taskForms.push(form)
            this.$refs.listTable.insertAt(form, -1)
          }*/
          this.taskForms.push(form)
          this.$refs.listTable.insertAt(form, -1)
        }
      }
    },
    // 设备选择取消回调
    onCancelTrainAssetSelect() {
    },
    taskSelectChange(value, option) {
      this.$refs.listTable.getActiveRecord().row.taskName = this.tasks[option.key].taskName
    },
    jumpInfo(row) {
      if (row.isNew) {
        // 新增加的
        if (row.instType === 1) {
          this.$refs.formViewModal.showModal(row.formObjId)
        }
        if (row.instType === 3) {
          if (row.workRecordType === 1) {
            this.$refs.recordTableView.show(row.formObjId)
          } else if (row.workRecordType === 2) {
            this.$refs.formViewModal.showModal(row.formObjId)
          }
        }
        if (row.instType === 4) {
          getWorkcheck({
            id: row.formObjId
          }).then((res) => {
            if (res.success && res.result) {
              let formData = res.result
              this.$refs.jobCheckTableView.show(formData)
            } else {
              this.$message.error('加载检查记录表明细记录数据异常')
              console.error('加载检查记录表明细记录数据异常', res.message)
            }
          })
        }
      } else {
        this.$refs.viewWorkForm.showModal(row, this.trainNo)
      }
    },
    clearValidate() {
      this.$refs.listTable.clearValidate()
    },
    sortTable() {
      // const listTable = this.$refs.listTable
      if (this.$refs.listTable) {
        const column = this.$refs.listTable.getColumnByField('formName')
        const option = column.filters[0]
        option.checked = true
        this.$refs.listTable.updateData()
      }
    },
    customStringFilterMethod({ row }) {
      const reg = new RegExp(this.searchText)
      const regName = new RegExp(this.searchText)
      return reg.test(row.formName) && (!this.searchCategory || row.instType === this.searchCategory)
    },
    onAddForm(records) {
      if (records.length) {
        records.forEach((element) => {
          this.taskForms.push({
            isNew: true,
            id: randomUUID(),
            planId: this.planId,
            formName: element.title,
            title: element.title,
            workOrderId: this.workOrderInfo.id,
            workOrderTaskId: this.tasks[0].id,
            formInstId: element.id,
            formObjId: element.formObjId,
            formCode: element.code,
            fromBy: 2,
            instType: element.type || element.formType,
            instType_dictText: element.formType_dictText,
            workRecordType: element.workRecordType,
            /*copies: 1,*/
            status_dictText: '未填写',
            recordIds: '',
            remark: '',
            assetTypeName: element.assetTypeName,
            trainStructName: element.trainStructName,
            trainAssetName: element.assetName,
            trainStructWbs: element.trainStructWbs
          })
        })
        this.btnStatus.update()
      }
    },
    openFormDetaile() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length) {
        // let ids = selectRecords.map((e) => e.id).join(',')
        this.$refs.taskFormDetaileModal.showModal(selectRecords[0])
      } else {
        this.$message.warn('请勾选填写的数据')
      }
    },
    formatterAssetName({ cellValue, row }) {
      let assetName = row.trainAssetName || row.trainStructName || row.assetTypeName
      if (row.trainStructWbs) {
        let wbs = row.trainStructWbs.split('.')
        assetName = wbs[0] + '_' + assetName
      }
      return assetName
    },
    onSelectFormDetaile(ids) {
      let row = this.$refs.listTable.getCheckboxRecords()[0]
      row.recordIds = ids || null
    }
  }
}
</script>

<style scoped>
</style>