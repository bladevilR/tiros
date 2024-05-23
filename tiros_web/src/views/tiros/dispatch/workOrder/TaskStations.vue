<template>
  <div :style="`height: calc(100% - ${getButtonShow() ? '47px': '4px'});`">
    <a-form layout="inline" v-if="getButtonShow()">
      <a-row :gutter="24">
        <a-col :md="6" :sm="8">
          <a-form-item>
            <a-space>
              <!-- 分配人员按钮去掉，单元格直接可以分配  -->
<!--              <a-button type="dashed" class="primary-color" @click="handleArrange" v-if="operator === 1">分配人员</a-button>-->
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
        align="center"
        ref="listTable"
        max-height="auto"
        :keep-source="true"
        :data.sync="stations"
        :edit-rules="validRules"
        show-overflow="ellipsis"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <!--    <vxe-table-column type="seq" title="序号" width="100"></vxe-table-column>-->
        <vxe-table-column v-if="tasks && tasks.length>1" field="taskName" title="工单任务" width="250" align="left" :edit-render="{ name: 'input' }">
          <template v-slot:edit="{ row }">
            <a-select v-model="row.orderTaskId" style="width: 100%" @change="taskSelectChange" @blur="$refs.listTable.clearValidate()">
              <a-select-option v-for="(task, index) in tasks" :value="task.id" :key="index">
                {{ task.taskName }}
              </a-select-option>
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column field="workstationNo" width="120" title="工位号" :edit-render="{ name: 'input' }">
          <template v-slot:edit="{ row }">
            <a-select
              ref="stationSelect"
              v-model="row.workstationNo"
              placeholder="请选择工位"
              :open="false"
              style="width: 100%"
              @focus="openStationSelectModal(row)"
              @blur="$refs.listTable.clearValidate()"
            >
              <a-icon slot="suffixIcon" type="ellipsis" />
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column field="workstationName" width="250" title="工位名称" align="left">
          <template v-slot:edit="{ row }">
            {{ row.workstationName }}
          </template>
        </vxe-table-column>
        <vxe-table-column field="personNames" title="安排人员" align="left" :edit-render="{ name: 'input' }">
          <template v-slot:edit="{ row }">
            <a-select
              ref="userSelect"
              v-if="operator === 1"
              v-model="row.personNames"
              placeholder="请选择人员"
              :open="false"
              style="width: 100%"
              @dropdownVisibleChange="openUserModal(row)"
              @blur="$refs.listTable.clearValidate()"
            >
              <a-icon slot="suffixIcon" type="ellipsis" />
            </a-select>
            <span v-else>{{ row.personNames }}</span>
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
    </div> operator
    <task-select-modal ref="taskSelectModal" @ok="onSelectTask"></task-select-modal>
    <work-station-list ref="stationSelectModal" :groupId="operator == 1 && groupId" :multiple="curEditMode === 1" @ok="onSelectStation"></work-station-list>
    <user-list ref="userSelectModal" :depId="groupId" @ok="onSelectUser"></user-list>
  </div>
</template>

<script>
import { randomUUID } from '@/utils/util'
import TaskSelectModal from '@views/tiros/dispatch/workOrder/TaskSelectModal'
import WorkStationList from '@views/tiros/common/selectModules/WorkStationList'
import UserList from '@views/tiros/common/selectModules/UserList'
export default {
  name: 'TaskStations',
  components: { TaskSelectModal, WorkStationList, UserList },
  props: {
    stations: {
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
    groupId:{
      type: String,
      default: null,
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
        // taskName: [{ required: true, message: '请选工单任务', trigger: 'manual' }],
        workstationNo: [{ required: true, message: '请选择工位', trigger: 'manual' }],
      },
    }
  },
  methods: {
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
      if (this.$refs.listTable.getActiveRecord()) {
        return
      }
      this.curEditMode = 1
      let station = {
        id: randomUUID(),
      }
      if (this.selectTask) {
        Object.assign(station, {
          orderTaskId: this.selectTask.id,
          taskName: this.selectTask.taskName,
        })
      }else{
        if (this.tasks.length) {
          Object.assign(station, {
            orderTaskId: this.tasks[0].id,
            taskName: this.tasks[0].taskName,
          })
        }
      }

      this.$refs.listTable.insertAt(station, -1).then(({ row }) => {
        this.$refs.listTable.setActiveCell(row, 'workstationNo')
      })
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
              this.stations.map((item2, index2) => {
                if (item1.id === item2.id) {
                  this.stations.splice(index2, 1)
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
      this.stations.map((item2, index2) => {
        if (row.id === item2.id) {
          this.stations.splice(index2, 1)
        }
      })
    },
    saveRowEvent(row) {
      this.$refs.listTable.validate(row, (valid) => {
        if (!valid) {
          if (this.curEditMode === 1) {
            this.stations.push(row)
          }
          this.$refs.listTable.clearActived()
          this.curEditMode = 0
          this.$emit('selectUser')
          this.$forceUpdate()
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
    openStationSelectModal(row) {
      this.curRow = row
      this.$refs.stationSelectModal.showModal()
      this.$refs.stationSelect.blur()
      this.$refs.listTable.clearValidate()
    },
    onSelectStation(data) {
      if (data && data.length > 0) {
        let station = data[0]

        data.forEach((element, index) => {
          if (this.stations.find(e => e.workstationId === element.id && this.curRow.orderTaskId === e.orderTaskId)) {
            this.$message.warn(`存在同样的任务及工位数据，无需重复设置`)
            return
          }
          if (index > 0) {
            let newRow ={
              id: randomUUID(),
              orderTaskId: this.curRow.orderTaskId,
              taskName: this.curRow.taskName,

              workstationId: element.id,
              workstationName: element.name,
              workstationNo: element.stationNo,
              remark: element.remark
            }
            this.stations.push(newRow)
          }else {
            this.curRow.workstationId = station.id
            this.curRow.workstationName = station.name
            this.curRow.workstationNo = station.stationNo
            this.curRow.remark = station.remark
            if (data.length > 1) {
              if (this.curEditMode === 1) {
                this.stations.push(this.curRow)
              }
              this.curEditMode = 0
            }
          }
        })
      } else {
        this.curRow.workstationId = ''
        this.curRow.workstationName = ''
        this.curRow.workstationNo = ''
        this.curRow.remark = ''
      }
      this.$refs.listTable.validate(this.$refs.listTable.getActiveRecord().row, (errMap) => {})
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
    handleArrange() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$refs.userSelectModal.showModal()
      } else {
        this.$message.error('尚未选中要安排人员的工位!')
      }
    },
    openUserModal(row) {
      // let selectRecords = this.$refs.listTable.getCheckboxRecords()
      // if (selectRecords.length > 0) {
        this.$refs.userSelectModal.showModal()
      // } else {
      //   this.$message.error('尚未选中要安排人员的工位!')
      // }
    },
    onSelectUser(data) {
      let users = []
      if (data && data.length > 0) {
        data.forEach((user) => {
          users.push({ userId: user.id, realName: user.realname })
        })
      }
      if (this.curEditMode === 0) {
        let selectRecords = this.$refs.listTable.getCheckboxRecords()
        selectRecords.forEach((station) => {
          station.staffUserIds = users.map((u) => u.userId)
          station.personNames = users.map((u) => u.realName).join(',')
        })
      } else {
        let record = this.$refs.listTable.getActiveRecord().row
        record.staffUserIds = users.map((u) => u.userId)
        record.personNames = users.map((u) => u.realName).join(',')
      }
    },
    taskSelectChange(value, option) {
      this.$refs.listTable.getActiveRecord().row.taskName = this.tasks[option.key].taskName
      this.$refs.listTable.validate(this.$refs.listTable.getActiveRecord().row, (errMap) => {})
    },
    clearValidate(){
      this.$refs.listTable.clearValidate()
    }
  },
}
</script>

<style scoped>
</style>