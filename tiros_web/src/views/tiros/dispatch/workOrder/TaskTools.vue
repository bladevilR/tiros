<template>
  <div :style="`height: calc(100% - ${getButtonShow() ? '47px': '4px'});`">
    <a-form layout="inline" v-if="getButtonShow()">
      <a-row :gutter="24">
        <a-col :md="6" :sm="8">
          <a-form-item>
            <a-space>
              <a-button type="dashed" class="primary-color" @click="handleAdd">添加</a-button>
              <a-button type="dashed" @click="handleDel">删除</a-button>
            </a-space>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <div na-flex-height-full>
      <vxe-table
        border
        ref="listTable"
        :align="'center'"
        max-height="auto"
        auto-resize
        :data.sync="taskTools"
        :keep-source="true"
        :edit-rules="validRules"
        :checkbox-config="{ highlight: true, range: true }"
        :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
        @blur="$refs.listTable.clearValidate()"
      >
        <vxe-table-column type="seq" width="40"></vxe-table-column>
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-if="tasks && tasks.length>1" field="taskName" title="工单任务" align="left" :edit-render="{ name: 'input' }">
          <template v-slot:edit="{ row }">
            <!--          <a-select ref="taskSelect" v-model="row.taskName" placeholder="请选择任务" :open="false" style="width: 100%" @focus="openTaskSelectModal(row)">
                      <a-icon slot="suffixIcon" type="ellipsis"  />
                    </a-select>-->
            <a-select v-model="row.orderTaskId" style="width: 100%" @change="taskSelectChange" @blur="$refs.listTable.clearValidate()">
              <a-select-option v-for="(task, index) in tasks" :value="task.id" :key="index">
                {{ task.taskName }}
              </a-select-option>
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column field="code" title="工器具类型编码" :edit-render="{ name: 'input' }">
          <template v-slot:edit="{ row }">
            <a-select
              ref="toolSelect"
              v-model="row.code"
              placeholder="请选择工器具"
              :open="false"
              style="width: 100%"
              @focus="openToolSelectModal(row)"
            >
              <a-icon slot="suffixIcon" type="ellipsis" />
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="name"
          title="工器具名称"
          :edit-render="{ name: 'input' }"
          align="left"
          header-align="center"
        >
          <template v-slot:edit="{ row }">
            {{ row.name }}
          </template>
        </vxe-table-column>
        <vxe-table-column field="category1_dictText" title="类别" :edit-render="{ name: 'input' }">
          <template v-slot:edit="{ row }">
            {{ row.category1_dictText }}
          </template>
        </vxe-table-column>
        <vxe-table-column field="amount" title="所需数量" :edit-render="{ name: 'input' }">
          <template v-slot:edit="{ row }">
            <a-input-number v-model="row.amount" :defaultValue="1" :min="1" :max="999999" style="width: 100%" />
          </template>
        </vxe-table-column>
        <vxe-table-column field="unit" title="单位" :edit-render="{ name: 'input' }">
          <template v-slot:edit="{ row }">
            {{ row.unit }}
          </template>
        </vxe-table-column>
        <!-- 2021-05-11 工单提交阶段新增实际工具填报 -->
        <vxe-table-column
          v-if="operator === 4"
          field="assetCode"
          title="工器具资产编码"
          align="left"
          :edit-render="{ name: 'input' }"
          :formatter="serialNoFormatter"
        >
          <template v-slot:edit="{ row }">
            <div @click.stop="openToolRealitySelectModal(row)">
              <a-select
                ref="toolRealitySelect"
                v-model="row.assetCode"
                placeholder="请选择工器具"
                :open="false"
                style="width: 100%"
              >
                <a-icon slot="suffixIcon" type="ellipsis" @click.stop="openToolRealitySelectModal(row)" />
              </a-select>
            </div>
          </template>
        </vxe-table-column>
        <!--      <vxe-table-column field="workRecordInstName" title="关联表单" :edit-render="{name: 'input'}">
              <template v-slot:edit="{row}">
                <a-select v-model="row.workRecordInstId" style="width: 100%"  @change="formSelectChange">
                  <a-select-option v-for="(form,index) in forms" :value="form.id" :key="index">
                    {{ form.formName }}
                  </a-select-option>
                </a-select>
              </template>
            </vxe-table-column>-->
        <vxe-table-column title="操作" width="150" v-if="getButtonShow()">
          <template v-slot="{ row }">
            <template v-if="$refs.listTable.isActiveByRow(row)">
              <a-space>
                <a-button type="dashed" size="small" @click.stop="saveRowEvent(row)">保存</a-button>
                <a-button type="dashed" size="small" @click.stop="cancelRowEvent(row)">取消</a-button>
              </a-space>
            </template>
            <template v-else>
              <a-button type="dashed" size="small" @click.stop="editRowEvent(row)">编辑</a-button>
              <!-- <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
            </template>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>

    <ToolTypeSelect ref="modalForm" :multiple="false" :batch="true" @ok="onSelectTool"></ToolTypeSelect>
    <ToolTypeSelect ref="modalFormMultiple" :multiple="true" :batch="true" @ok="onSelectToolMultiple"></ToolTypeSelect>
    <ToolsList ref="modalRealityForm" :multiple="false" @ok="onRealitySelectTool"></ToolsList>
    <task-select-modal ref="taskSelectModal" @ok="onSelectTask"></task-select-modal>
  </div>
</template>

<script>
import ToolTypeSelect from '@views/tiros/common/selectModules/ToolTypeSelect'
import TaskSelectModal from '@views/tiros/dispatch/workOrder/TaskSelectModal'
import ToolsList from '@views/tiros/common/selectModules/ToolsList'
import { randomUUID } from '@/utils/util'

export default {
  name: 'TaskTools',
  components: { ToolTypeSelect, TaskSelectModal, ToolsList },
  props: {
    taskTools: {
      type: Array,
      default: () => {
        return []
      },
    },
    tasks: {
      type: Array,
      default: () => {
        return []
      },
    },
    readOnly: {
      type: Boolean,
      default: false,
    },
    selectTask: {
      type: Object,
      default: null,
    },
    forms: {
      type: Array,
      default: () => {
        return []
      },
    },
    operator: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      validRules: {
        // taskName: [{ required: true, message: '请选择任务', trigger: 'manual' }],
        code: [{ required: true, message: '请选择工器具', trigger: 'manual' }],
        amount: [{ required: true, message: '数量必须填写', trigger: 'manual' }],
        /*formName: [{
          validator: ({ row }) => {
            if (row.category1 === '6') {
              if (!row.workRecordInstId) {
                return new Error('计量器具必须指定一个记录表')
              }
            }
          }
        }]*/
      },
      tableHeight: {
        top: 'height: calc(100vh - 160px)',
        bottom: 'height: calc(100vh - 160px)',
        size: '100%',
      },
    }
  },
  methods: {
    getButtonShow() {
      if (this.readOnly) {
        return false
      }
      //1 核实工单、4 提交工单  5 工单关闭
      if (this.operator === 0 || this.operator === 1 || this.operator === 4 ) {
        return true
      } else {
        return false
      }
    },
    handleAdd() {
      if (this.$refs.listTable.getActiveRecord()) {
        return
      }
      this.curEditMode = 1
      this.$refs.modalFormMultiple.showModal()
      // let tool = {
      //   id: randomUUID(),
      //   orderTaskId: '',
      // }
      // if (this.selectTask) {
      //   Object.assign(tool, {
      //     orderTaskId: this.selectTask.id,
      //     taskName: this.selectTask.taskName,
      //   })
      // }else{
      //   if (this.tasks.length) {
      //     Object.assign(tool, {
      //       orderTaskId: this.tasks[0].id,
      //       taskName: this.tasks[0].taskName,
      //     })
      //   }
      // }
      // this.$refs.listTable.insertAt(tool, -1).then(({ row }) => {
      //   this.$refs.listTable.setActiveCell(row, 'code')
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
              this.taskTools.map((item2, index2) => {
                if (item1.id === item2.id) {
                  this.taskTools.splice(index2, 1)
                }
              })
            })
          },
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
      this.taskTools.map((item2, index2) => {
        if (row.id === item2.id) {
          this.taskTools.splice(index2, 1)
        }
      })
    },
    saveRowEvent(row) {
      this.$refs.listTable.validate(row, (valid) => {
        if (!valid) {
          if (this.curEditMode === 1) {
            this.taskTools.push(row)
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
    openToolSelectModal(row) {
      this.curRow = row
      this.$refs.modalForm.showModal()
      this.$refs.toolSelect.blur()
      this.$refs.listTable.clearValidate()
    },
    openToolRealitySelectModal(row) {
      this.curRow = row
      this.$refs.modalRealityForm.showModal(row.toolTypeId)
      this.$refs.toolRealitySelect.blur()
    },
    onSelectToolMultiple(data){
      data.forEach((element, index) => {
          if (this.taskTools.find(e => e.toolTypeId === element.id)) {
            this.$message.warn(`你选择的${element.name}已经在列表中存在`)
            return
          }else{
            let newRow ={
              id: randomUUID(),
              code: element.code,
              toolTypeId: element.toolTypeId || element.id,
              name: element.name,
              kind: element.kind,
              unit: element.unit,
              kind_dictText: element.kind_dictText,
              category1: element.category1,
              category1_dictText: element.category1_dictText,
              amount: element.num,
              toolId: '',
              measure: element.category1 === 6 ? 1 : 0,
            }

            if (this.selectTask) {
              Object.assign(newRow, {
                orderTaskId: this.selectTask.id,
                taskName: this.selectTask.taskName,
              })
            }else{
              if (this.tasks.length) {
                Object.assign(newRow, {
                  orderTaskId: this.tasks[0].id,
                  taskName: this.tasks[0].taskName,
                })
              }
            }

            // 不管是一个还是多个，都直接添加，不需要点击单元格中的保存
            this.$refs.listTable.insertAt(newRow, -1)
            this.taskTools.push(newRow)
           /* if(data.length == 1){
              this.$refs.listTable.insertAt(newRow, -1).then(({ row }) => {
                this.$refs.listTable.setActiveCell(row, 'amount')
              })
            }else{
              this.$refs.listTable.insertAt(newRow, -1)
              this.taskTools.push(newRow)
            }*/
          }
      })
    },
    onSelectTool(data) {
      if (data && data.length > 0) {
        const tool = data[0]
        data.forEach((element, index) => {
          if (this.taskTools.find(e => e.toolTypeId === element.id)) {
            this.$message.warn(`你选择的${element.name}已经在列表中存在`)
            return
          }
          if (index > 0) {
            let newRow ={
              id: randomUUID(),
              orderTaskId: this.curRow.orderTaskId,
              taskName: this.curRow.taskName,
              code: element.code,
              toolTypeId: element.toolTypeId || element.id,
              name: element.name,
              kind: element.kind,
              unit: element.unit,
              kind_dictText: element.kind_dictText,
              category1: element.category1,
              category1_dictText: element.category1_dictText,
              amount: element.num,
              toolId: '',
              measure: element.category1 === 6 ? 1 : 0,
            }
            this.taskTools.push(newRow)
          } else {
            this.curRow.code = tool.code
            this.curRow.toolTypeId = tool.toolTypeId
            if (!tool.toolTypeId) {
              this.curRow.toolTypeId = tool.id
            }
            this.curRow.name = tool.name
            this.curRow.kind = tool.kind
            this.curRow.unit = tool.unit
            this.curRow.kind_dictText = tool.kind_dictText
            this.curRow.category1 = tool.category1
            this.curRow.category1_dictText = tool.category1_dictText
            this.curRow.amount = tool.num
            this.curRow.toolId = ''
            // 4 工器具  5工装 6 计量器具
            this.curRow.measure = tool.category1 === 6 ? 1 : 0
            if (data.length > 1) {
              this.$refs.listTable.clearActived()
             if (this.curEditMode === 1) {
                this.taskTools.push(this.curRow)
              }
              this.curEditMode = 0
            }
          }
        })
      } else {
        this.curRow.code = ''
        this.curRow.toolTypeId = ''
        this.curRow.name = ''
        this.curRow.kind = ''
        this.curRow.kind_dictText = ''
        this.curRow.category1 = ''
        this.curRow.category1_dictText = ''
        this.curRow.unit = ''
        this.curRow.amount = 0
        this.curRow.toolId = ''
        this.curRow.measure = ''
      }
      this.$refs.listTable.validate(this.$refs.listTable.getActiveRecord().row, (errMap) => {})
    },
    onRealitySelectTool(data){
      if (data && data.length > 0) {
        const tool = data[0]
        // 2021/05/07 选择计量器具时跳过判断 task #2015
        if ((this.curRow.toolTypeId !== tool.materialTypeId) && (tool.category1 !== 6)) {
          this.$message.error('你选择的工器具与需求类型不一样')
          return
        }
        this.$refs.listTable.getActiveRecord().row.toolId = tool.id
        this.$refs.listTable.getActiveRecord().row.assetCode = tool.assetCode
      }

    },
    openTaskSelectModal(row) {
      this.curRow = row
      this.$refs.taskSelectModal.showModal(this.tasks)
      this.$refs.taskSelect.blur()
    },
    onSelectTask(tasks) {
      if (tasks && tasks.length > 0) {
        let task = tasks[0]
        this.curRow.orderTaskId = task.id
        this.curRow.taskName = task.taskName
      } else {
        this.curRow.orderTaskId = ''
        this.curRow.taskName = ''
      }
    },
    taskSelectChange(value, option) {
      this.$refs.listTable.getActiveRecord().row.taskName = this.tasks[option.key].taskName
      this.$refs.listTable.validate(this.$refs.listTable.getActiveRecord().row, (errMap) => {})
    },
    formSelectChange(value, option) {
      this.$refs.listTable.getActiveRecord().row.workRecordInstName = this.forms[option.key].formName
      this.$refs.listTable.validate(this.$refs.listTable.getActiveRecord().row, (errMap) => {})
    },
    serialNoFormatter({ cellValue }) {
      if (!cellValue) {
        return '无'
      }
      return cellValue
    },
    clearValidate(){
      this.$refs.listTable.clearValidate()
    }
  },
}
</script>

<style>
</style>