<template>
  <a-modal
    :title='title'
    width='90%'
    :visible='visible'
    :confirmLoading='confirmLoading'
    @ok='handleOk'
    @cancel='handleCancel'
    cancelText='关闭'
    :destroyOnClose='true'
    centered
    :bodyStyle="{ height: '80vh' }"
  >
    <div class='table-page-search-wrapper'>
      <a-form layout='inline' @keyup.enter.native='findList'>
        <a-row :gutter='24'>
          <a-col :md='5' :sm='24'>
            <a-form-item label='表单:'>
              <a-input v-model='queryParam.searchText' placeholder='表单名称或编码' allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='表单类型:'>
              <a-select v-model='queryParam.formTypeList' allowClear>
                <a-select-option :value='3'> 作业记录表</a-select-option>
                <a-select-option :value='1'> 数据记录表</a-select-option>
                <a-select-option :value='4'> 检查记录表</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='选择工位'>
              <a-select
                allowClear
                v-model='stationName'
                placeholder='请选择工位'
                :open='false'
                style='width: 100%'
                @dropdownVisibleChange='openStationSelectModal()'
                @change='onStationChange'
              >
                <a-icon slot='suffixIcon' type='ellipsis' />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md='9' :sm='8'>
            <a-space>
              <a-button @click='findList'>查询</a-button>
              <a-button @click='openFormModal'>从模板添加</a-button>
              <a-button @click='openFormEditModal(btnStatus.editRow)' :disabled='!btnStatus.edit'>修改</a-button>
              <a-button :loading='deleteLoading' :disabled='records.length <= 0' @click='deleteForm'>删除</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style='height: calc(80vh - 90px)'>
      <vxe-table
        border
        ref='listTable'
        style='height: calc(80vh - 90px)'
        max-height='100%'
        :align="'center'"
        :data='tableData'
        :radio-config="!multiple ? { trigger: 'row' } : {}"
        :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true } : {}"
        show-overflow='tooltip'
        @checkbox-change='rowSelectChange'
        @checkbox-all='rowSelectChange'
      >
        <vxe-table-column type='seq' fixed='left' width='60'></vxe-table-column>
        <vxe-table-column v-if='multiple' type='checkbox' width='40'></vxe-table-column>
        <vxe-table-column v-else type='radio' width='40'></vxe-table-column>
        <vxe-table-column field='planName' title='列计划' align='left' header-align='center'
                          width='180'></vxe-table-column>
        <vxe-table-column field='title' title='表单名称' min-width='220' align='left' header-align='center'>
          <template v-slot='{ row }'>
            <span v-if='operator !== 1'>{{ row.title }}</span>
            <a v-else @click.stop='jumpInfo(row)'>{{ row.title }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column align='left' header-align='center' field='code' title='表单编码' width='120'></vxe-table-column>
        <!--        <vxe-table-column field='formName' title='表单名称' align='left' header-align='center'></vxe-table-column>-->
        <vxe-table-column field='formType_dictText' title='表单类型' width='120'>
          <template v-slot='{ row }'>
            {{ row.formType !== 3 ? row.formType_dictText : row.workRecordType === 1 ? '作业记录表(老)' : '作业记录表(新)' }}
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
        <vxe-table-column field='trainStructName' title='关联设备' min-width='150' align='left'
                          header-align='center'></vxe-table-column>
      </vxe-table>
      <!-- <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager> -->
    </div>
    <JobCheckTableView ref='jobCheckTableView'></JobCheckTableView>
    <RecordTableView ref='recordTableView'></RecordTableView>
    <FormViewModal ref='formViewModal'></FormViewModal>
    <ViewWorkForm ref='viewWorkForm' :operator='operator'></ViewWorkForm>
    <TaskFormModel ref='taskFormModel' :repairId='repairId' :operator='operator' :checkList='tableData'
                   :trainNo='trainNo' :plan-id='planId' :line-id='lineId' @ok='onAddForm'></TaskFormModel>
    <work-station-list ref='stationSelectModal' @ok='onSelectStation'></work-station-list>
    <task-form-edit-model ref='taskFormEditModel' :line-id='lineId' :train-no='trainNo'
                          @ok='findList'></task-form-edit-model>
  </a-modal>
</template>

<script>
import FormsList from '@views/tiros/common/selectModules/FormsList'
import CarDeviceSelect from '@views/tiros/common/selectModules/CarDeviceSelect'
import { deleteFormEntity, getFormEntityList } from '@/api/tirosApi'
import FormViewModal from '@views/tiros/basic/customform/FormViewModal'
import JobCheckTableView from '@views/tiros/basic/modules/jobCheckSheet/JobCheckTableView'
import RecordTableView from '@views/tiros/basic/modules/workRecordSheet/RecordTableView'
import ViewWorkForm from '@views/tiros/dispatch/workOrder/ViewWorkForm'
import TaskFormModel from '@views/tiros/dispatch/modules/TaskFormModel'
import WorkStationList from '@views/tiros/common/selectModules/WorkStationList'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'
import TaskFormEditModel from '@views/tiros/dispatch/modules/TaskFormEditModel'

export default {
  name: 'TaskFormSelectModel',
  components: {
    FormsList,
    CarDeviceSelect,
    TaskFormModel,
    WorkStationList,
    TaskFormEditModel,
    ViewWorkForm,
    JobCheckTableView,
    RecordTableView,
    FormViewModal
  },
  props: {
    trainNo: {
      type: String,
      default: ''
    },
    lineId: {
      type: String,
      default: ''
    },
    workOrderTaskId: {
      type: String,
      default: ''
    },
    workOrderId: {
      type: String,
      default: ''
    },
    repairId: {
      type: [Number, String],
      default: null
    },
    operator: {
      type: [String, Number],
      default: ''
    },
    checkList: {
      type: Array,
      default: () => {
        return []
      }
    },
    multiple: {
      type: Boolean,
      default: true
    }
  },

  data() {
    return {
      records: [],
      deleteLoading: false,
      title: '列计划已有表单实例',
      confirmLoading: false,
      visible: false,
      tableData: [],
      stationName: '',
      queryParam: {
        formTypeList: '',
        planId: '',
        searchText: '',
        workstationId: null
      },
      totalResult: 0,
      planId: '',
      btnStatus: new TableBtnUtil(this, 'listTable')
    }
  },
  methods: {
    jumpInfo(_row) {
      console.log('row:', _row)

      let row = {
        ..._row,
        planFormId: _row.planFormId,
        instType: _row.formType,
        formObjId: _row.formObjId,
        formInstId: _row.id,
        workRecordType: _row.workRecordType,
        formName: _row.title,
        // id: "27403e5c2614036096a901a3fda972fa", // 测试
        workOrderId: this.workOrderId,
        workOrderTaskId: this.workOrderTaskId
      }
      delete row.id
      // const instType =  row.type || row.formType;
      // return
      // if (row.isNew) {
      //   // 新增加的
      //   if (instType === 1) {
      //     this.$refs.formViewModal.showModal(row.formObjId)
      //   }
      //   if (instType === 3) {
      //     this.$refs.recordTableView.show(row.formObjId)
      //   }
      //   if (instType === 4) {
      //     getWorkcheck({
      //       id: row.formObjId,
      //     }).then((res) => {
      //       if (res.success && res.result) {
      //         let formData = res.result
      //         this.$refs.jobCheckTableView.show(formData)
      //       } else {
      //         this.$message.error('加载检查记录表明细记录数据异常')
      //         console.error('加载检查记录表明细记录数据异常', res.message)
      //       }
      //     })
      //   }
      // } else {
      this.$refs.viewWorkForm.showModal(row, this.trainNo)
      // }
    },
    deleteForm() {
      const selectRecords = this.$refs.listTable.getCheckboxRecords()
      console.log(selectRecords)
      const list = selectRecords.map((item) => {
        return {
          formType: item.formType,
          formInstId: item.id,
          planId: item.planId
        }
      })
      this.deleteLoading = true
      deleteFormEntity(list).then((res) => {
        if (res.success) {
          this.$message.success('删除成功')
          this.findList()
        } else {
          this.$message.warning(res.message)
        }
        this.deleteLoading = false
      }).catch((err) => {
        this.deleteLoading = false
      })
    },
    rowSelectChange({ records }) {
      this.records = records
      this.btnStatus.update()

    },
    findList() {
      getFormEntityList(this.queryParam).then((res) => {
        if (res.success) {
          this.tableData = res.result || []
          console.log('datta:', this.tableData)
          this.records = []
        } else {
          this.$message.warn(res.message)
          console.log(res.message)
        }
      })
    },

    showModal(planId) {
      this.queryParam.planId = planId
      this.tableData = []
      this.findList()
      this.visible = true
    },

    openFormEditModal(record) {
      this.$refs.taskFormEditModel.edit(record)
      console.log(record)
    },

    // 确定
    handleOk() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length) {
        let exists = selectRecords.filter((e) => {
          let repeat = this.checkList.find((r) => r.formInstId === e.id)
          if (repeat) {
            this.$message.warn(`${e.title}已经添加了`)
          }
          return repeat
        })
        if (exists.length > 0) {
          return
        }
        if (selectRecords.length > 0) {
          this.$emit('ok', selectRecords)
          this.visible = false
        }
      } else {
        this.visible = false
      }
      // this.$message.warn('请勾选填写的数据')
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    openFormModal() {
      this.$refs.taskFormModel.showModal(this.queryParam.planId)
    },
    onAddForm(records) {
      this.findList()
    },
    openStationSelectModal() {
      this.$refs.stationSelectModal.showModal()
    },
    onSelectStation(records) {
      if (records.length) {
        this.queryParam.workstationId = records[0].id
        this.stationName = records[0].name
      }
    },
    onStationChange(value) {
      if (!value) {
        this.queryParam.workstationId = null
      }
    }
  }
}
</script>
<style lang='less' scoped>
.QRcode {
  width: 150px;
  height: 150px;
}
</style>
