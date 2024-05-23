<template>
  <div>
    <a-form v-if='!readOnly' layout='inline'>
      <a-row :gutter='24'>
        <a-col :md='6' :sm='8'>
          <a-form-item style=''>
            <a-space>
              <a-button type='dashed' class='primary-color' @click='handleAdd'>添加</a-button>
              <a-button type='dashed' @click='handleDel'>删除</a-button>
            </a-space>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <vxe-table border ref='listTable' align='center'
               :data.sync='taskForms'
               :edit-config="{key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true}"
    >
      <vxe-table-column type='checkbox' width='40'></vxe-table-column>
      <!--      <vxe-table-column field='title' title='表单名称' min-width='180' align='left' header-align='center' />-->
      <vxe-table-column
        field='formName'
        title='表单名称'
        min-width='180'
        :edit-render="{ name: 'input' }"
        align='left'
        header-align='center'
      >
        <template v-slot='{row}'>
          <a @click='handleSeeing(row)'>{{ row.title }}</a>
        </template>
        <template v-slot:edit='{ row }'>
          <a-input
            ref='formSelect'
            v-model='row.title'
            placeholder='请选择表单'
            :open='false'
            style='width: 100%'
          >
            <a-icon slot='suffix' type='ellipsis' @click='openFormSelectModal(row)' />
          </a-input>
        </template>
      </vxe-table-column>
      <vxe-table-column field='formType_dictText' title='表单类型' width='120'>
        <template v-slot="{ row }">
          {{ row.formType !== 3 ? row.formType_dictText : row.workRecordType === 1 ? '作业记录表(老)' : '作业记录表(新)'  }}
        </template>
      </vxe-table-column>
      <vxe-table-column field='reguName' title='所属规程' width='120' />
      <vxe-table-column field='reguVersion' title='规程版本' width='120' />
      <vxe-table-column field='trainStructName' width='120' title='关联设备' align='left' header-align='center' />
      <vxe-table-column field='remark' title='备注' width='120' align='left' header-align='center' />
      <vxe-table-column title='操作' width='150' v-if='!readOnly'>
        <template v-slot='{ row }'>
          <template v-if='$refs.listTable.isActiveByRow(row)'>
            <a-space>
              <a-button type='dashed' size='small' @click='saveRowEvent(row)'>保存</a-button>
              <a-button type='dashed' size='small' @click='cancelRowEvent(row)'>取消</a-button>
            </a-space>
          </template>
          <template v-else>
            <a-button type='dashed' size='small' @click='editRowEvent(row)'>编辑</a-button>
            <!--  <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
          </template>
        </template>
      </vxe-table-column>
    </vxe-table>
    <PlanFormsList :plan-type='planType' ref='planFormsList' :multiple='false' @ok='onSelectForm'></PlanFormsList>
    <FormViewModal ref='formViewModal'></FormViewModal>
    <RecordTableView ref='recordTableView'></RecordTableView>
    <JobCheckTableView ref='jobCheckTableView'></JobCheckTableView>
  </div>
</template>

<script>
import { randomUUID } from '@/utils/util'
import PlanFormsList from '@views/tiros/common/selectModules/PlanFormsList'
import FormViewModal from '@views/tiros/basic/customform/FormViewModal'
import RecordTableView from '@views/tiros/basic/modules/workRecordSheet/RecordTableView'
import JobCheckTableView from '@views/tiros/basic/modules/jobCheckSheet/JobCheckTableView'

import { getWorkcheck } from '@api/tirosQualityApi'

export default {
  name: 'TaskForms',
  components: { PlanFormsList, FormViewModal, RecordTableView, JobCheckTableView },
  props: {
    readOnly: {
      type: Boolean,
      default: false
    },
    taskId: {
      type: String,
      default: ''
    },
    taskForms: {
      type: Array,
      default: []
    },
    planInfo: {
      type: Object,
      default() {
        return {}
      }
    },
    trainTypeId: {
      type: String,
      default: ''
    },
    planType: {
      // 0表示计划模板，1表示列计划
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null
    }
  },
  methods: {
    handleAdd() {
      if (this.$refs.listTable.getActiveRecord()) {
        return
      }
      this.curEditMode = 1
      let form = {
        id: randomUUID(),
        taskId: this.taskId,
        formName: ''
      }
      this.$refs.listTable.insertAt(form, -1)
        .then(({ row }) => {
          this.$refs.listTable.setActiveCell(row, 'formName')
        })
    },
    handleDel() {
      let m = this.$refs.listTable.getCheckboxRecords()
      m.map((item1) => {
        this.taskForms.map((item2, index2) => {
          if (item1.id === item2.id) {
            this.taskForms.splice(index2, 1)
          }
        })
      })
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
      this.$refs.listTable.validate(row, valid => {
        if (!valid) {
          if (this.curEditMode === 1) {
            this.taskForms.push(row)
          }
          this.$refs.listTable.clearActived()
          this.curEditMode = 0
        }
      })
    },
    cancelRowEvent(row) {
      this.$refs.listTable.clearActived()
      if (this.curEditMode === 1) {
        // 新增,点击取消
        this.$refs.listTable.remove(row)
      }
      this.curEditMode = 0
    },
    openFormSelectModal(row) {
      this.curRow = row
      this.$refs.planFormsList.show(this.planInfo)
      this.$refs.formSelect.blur()
    },
    onSelectForm(data) {
      if (data && data.length > 0) {
        const form = data[0]

        // 判断选择的表单是否已经存在
        let flag = 0
        this.taskForms.map(f => {
          if (f.planFormId === form.id) {
            flag = 1
            return false
          }
        })

        if (flag === 1) {
          this.$message.error('你选择的表单已经在列表中存在，请选择其他表单')
          return
        }

        this.curRow.planFormId = form.id
        this.curRow.title = form.title
        this.curRow.formType = form.formType
        this.curRow.formType_dictText = form.formType_dictText
        this.curRow.objId = form.objId
        this.curRow.reguName = form.reguName
        this.curRow.reguVersion = form.reguVersion
        this.curRow.trainStructName = form.trainStructName
        this.curRow.remark = form.remark
        // this.curRow.copies = 1
        // if (form.assetTypeId) {
        //   this.curRow.assetTypeName = form.assetTypeName
        //   this.curRow.assetTypeId = form.assetTypeId
        // }
      } else {
        this.curRow.planFormId = ''
        this.curRow.title = ''
        this.curRow.formType = ''
        this.curRow.formType_dictText = ''
        this.curRow.objId = ''
        this.curRow.reguName = ''
        this.curRow.reguVersion = ''
        this.curRow.trainStructName = ''
        this.curRow.remark = ''
        // this.curRow.copies = 0
      }
    },
    handleSeeing(data) {
      if (data.formType === 1) {
        this.$refs.formViewModal.showModal(data.objId)
      }
      if (data.formType === 3) {
        if (data.workRecordType === 1) {
          this.$refs.recordTableView.show(data.objId)
        } else if (data.workRecordType === 2) {
          this.$refs.formViewModal.showModal(data.objId)
        }
      }
      if (data.formType === 4) {
        getWorkcheck({
          id: data.objId
        }).then((res) => {
          if (res.success && res.result) {
            let formData = res.result
            this.$refs.jobCheckTableView.show(formData)
          } else {
            this.$message.error('加载记录数据异常')
            console.error('加载记录数据失败', res.message)
          }
        })
      }
    }
  }
}
</script>

<style scoped>

</style>