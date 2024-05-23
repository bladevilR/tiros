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
    <vxe-table
      border
      align="center"
      ref="listTable"
      :data.sync="files"
      :edit-rules="validRules"
      :keep-source="true"
      show-overflow="ellipsis"
      :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
      :edit-config="{key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true}"
    >
      <vxe-table-column type="checkbox" width="40"></vxe-table-column>
      <!--    <vxe-table-column type="seq" title="序号" width="100"></vxe-table-column>-->
      <vxe-table-column field="taskName" title="工单任务" align="left" :edit-render="{name: 'input'}">
        <template v-slot:edit="{row}">
          <a-select v-model="row.orderTaskId" style="width: 100%" @change="taskSelectChange">
            <a-select-option v-for="(task,index) in tasks" :value="task.id" :key="index">
              {{ task.taskName }}
            </a-select-option>
          </a-select>
        </template>
      </vxe-table-column>
      <vxe-table-column field="fileName" title="文件名称" :edit-render="{name: 'input'}">
        <template v-slot:edit="{row}">
          <a-select ref="fileSelect" v-model="row.fileName" placeholder="请选择文件" :open="false" style="width: 100%"
                    @focus="openFileSelectModal(row)">
            <a-icon slot="suffixIcon" type="ellipsis" />
          </a-select>
        </template>
      </vxe-table-column>
      <vxe-table-column field="fileType" title="文件类型">
        <template v-slot:edit="{row}">
          {{ row.fileType }}
        </template>
      </vxe-table-column>
      <vxe-table-column field="fileSize" title="文件大小" align="right">
        <template v-slot:edit="{row}">
          {{ row.fileSize }}
        </template>
      </vxe-table-column>
      <!--    <vxe-table-column field="remark" title="备注" align="left">
            <template v-slot:edit="{row}">
              <a-input placeholder="请输入备注" v-model="row.remark" />
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
    <task-select-modal ref="taskSelectModal" @ok="onSelectTask"></task-select-modal>
    <tech-file-list ref="fileSelectModal" :id="'4'" :multiple="false" @ok="onFileSelect"></tech-file-list>
  </div>
</template>

<script>
import { randomUUID } from '@/utils/util'
import TaskSelectModal from '@views/tiros/dispatch/workOrder/TaskSelectModal'
import TechFileList from '@views/tiros/common/selectModules/TechFileList'

export default {
  name: 'TaskTechFiles',
  components: { TaskSelectModal, TechFileList },
  props: {
    files: {
      type: Array,
      default: () => {
        return []
      }
    },
    tasks: {
      type: Array,
      default: () => {
        return []
      }
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    selectTask: {
      type: Object,
      default: null
    },
    operator: {
      type: Number,
      default: 0
    }
  },
  data () {
    return {
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      validRules: {
        fileName: [
          { required: true, message: '请选择文件' }
        ]
      }
    }
  },
  methods: {
    getButtonShow () {
      if (this.readOnly) {
        return false
      }
      if (this.operator === 0 || this.operator === 1) {
        return true
      } else {
        return false
      }
    },
    handleAdd () {
      if (this.$refs.listTable.getActiveRecord()) {
        return
      }
      this.curEditMode = 1
      let file = {
        id: randomUUID()
      }
      if (this.selectTask) {
        Object.assign(file, {
          orderTaskId: this.selectTask.id,
          taskName: this.selectTask.taskName
        })
      }
      this.$refs.listTable.insertAt(file, -1)
        .then(({ row }) => {
          this.$refs.listTable.setActiveCell(row, 'taskName')
        })
    },
    handleDel () {
      let m = this.$refs.listTable.getCheckboxRecords()
      if (m.length > 0) {
        this.$confirm({
          content: `是否删除选中${m.length}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            m.map((item1) => {
              this.files.map((item2, index2) => {
                if (item1.id === item2.id) {
                  this.files.splice(index2, 1)
                }
              })
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    editRowEvent (row) {
      this.curEditMode = 2
      this.$refs.listTable.setActiveRow(row)
    },
    delRowEvent (row) {
      this.files.map((item2, index2) => {
        if (row.id === item2.id) {
          this.files.splice(index2, 1)
        }
      })
    },
    saveRowEvent (row) {
      this.$refs.listTable.validate(row, valid => {
        if (!valid) {
          if (this.curEditMode === 1) {
            this.files.push(row)
          }
          this.$refs.listTable.clearActived()
          this.curEditMode = 0
        } else {
          for (const validKey in valid) {
            let vals = valid[validKey]
            vals.forEach(item => {
              if (item.rule) {
                this.$message.error(item.rule.message)
              }
            })
          }
        }
      })
    },
    cancelRowEvent (row) {
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
    openFileSelectModal (row) {
      this.curRow = row
      this.$refs.fileSelectModal.showModal()
      this.$refs.fileSelect.blur()
    },
    onFileSelect (data) {
      if (data && data.length > 0) {
        let file = data[0]
        this.curRow.fileId = file.id
        this.curRow.fileName = file.name
        this.curRow.fileType = file.type
        this.curRow.fileSize = file.size
      } else {
        this.curRow.fileId = ''
        this.curRow.fileName = ''
        this.curRow.fileType = ''
        this.curRow.fileSize = ''
      }
      this.$refs.listTable.validate(this.$refs.listTable.getActiveRecord().row, (errMap) => {
      })
    },

    openTaskSelectModal (row) {
      this.curRow = row
      this.$refs.taskSelectModal.showModal(this.tasks)
      this.$refs.taskSelect.blur()
    },
    onSelectTask (tasks) {
      if (tasks && tasks.length > 0) {
        let task = tasks[0]
        this.curRow.orderTaskId = task.id
        this.curRow.taskName = task.taskName
      } else {
        this.curRow.orderTaskId = ''
        this.curRow.taskName = ''
      }
    },
    taskSelectChange (value, option) {
      this.$refs.listTable.getActiveRecord().row.taskName = this.tasks[option.key].taskName
      this.$refs.listTable.validate(this.$refs.listTable.getActiveRecord().row, (errMap) => {
      })
    }
  }
}
</script>

<style scoped>

</style>