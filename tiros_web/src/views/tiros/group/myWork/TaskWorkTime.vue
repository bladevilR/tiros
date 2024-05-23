<template>
  <div na-flex-height-full>
    <a-form layout="inline" v-if="operator === 4 && !readOnly">
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
    <div :style="`height: calc(100% - ${(operator === 4 && !readOnly) ? '45px' : '4px'})`">
      <vxe-table
        border
        ref="listTable"
        align="center"
        max-height="auto"
        auto-resize
        :data.sync="staffArranges"
        :keep-source="true"
        show-overflow="ellipsis"
        :edit-rules="validRules"
        :checkbox-config="{ highlight: true, range: true }"
        :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
      >
        <vxe-table-column type="seq" width="40"></vxe-table-column>
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <!-- <vxe-table-column field="taskName" title="工单任务" width="250" align="left" v-if="selectTask">
        <template v-slot="{}">
          {{ selectTask.taskName }}
        </template>
      </vxe-table-column> -->
        <vxe-table-column
          v-if="tasks && tasks.length>1"
          title="工单任务"
          width="250"
          align="left"
          :edit-render="{ name: 'input', enabled: operator === 4 && curEditMode === 1 && tasks.length > 0 }"
        >
          <template v-slot="{ row }">
            {{ tasks.length > 0 ? row.orderTaskName : selectTask.taskName }}
          </template>
          <template v-slot:edit="{ row }">
            <a-select v-model="row.orderTaskId" style="width: 100%" @change="taskSelectChange">
              <a-select-option v-for="(task, index) in tasks" :value="task.id" :key="index">
                {{ task.taskName }}
              </a-select-option>
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="workstationName"
          width="250"
          title="工位"
          align="left"
          :edit-render="{ name: 'input', enabled: !readOnly && operator === 4 && curEditMode === 1 }"
        >
          <template v-slot:edit="{ row }">
            <a-select
              ref="stationSelect"
              v-model="row.workstationName"
              placeholder="请选择工位"
              :open="false"
              style="width: 100%"
              @focus="openStationSelectModal(row)"
            >
              <a-icon slot="suffixIcon" type="ellipsis" />
            </a-select>
          </template>
        </vxe-table-column>
        <!-- 安排人员 NoEdit -->
        <vxe-table-column
          field="userName"
          title="安排人员"
          align="left"
          :edit-render="{ name: 'input', enabled: !readOnly && operator === 4 && curEditMode === 1 }"
        >
          <template v-slot:edit="{ row }">
            <a-select
              ref="userSelect"
              v-model="row.userName"
              placeholder="请选择人员"
              :open="false"
              style="width: 100%"
              @focus="openSelectUser(row)"
            >
              <a-icon slot="suffixIcon" type="ellipsis" />
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="planWorkTime"
          title="计划工时"
          align="center"
          :edit-render="{ name: 'input', enabled: !readOnly && operator === 4 && curEditMode === 1 }"
        >
          <template v-slot="{ row }">
            {{ row.planWorkTime || '' }}
          </template>
          <template v-slot:edit="{ row }">
            <a-input-number
              v-model="row.planWorkTime"
              :defaultValue="1"
              :min="0"
              :max="10000"
              style="width: 100%"
              :disabled="!isInputPlanTime"
            />
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="workTime"
          title="实际工时"
          align="center"
          :edit-render="{ name: 'input', enabled: !readOnly && operator === 4 }"
        >
          <template v-slot="{ row }">
            {{ row.workTime || '' }}
          </template>
          <template v-slot:edit="{ row }">
            <a-input-number v-model="row.workTime" :defaultValue="1" :min="0" :max="10000" style="width: 100%" />
          </template>
        </vxe-table-column>

        <vxe-table-column title="操作" width="120" v-if="!readOnly">
          <template v-slot="{ row }" v-if="operator === 4">
            <template v-if="$refs.listTable.isActiveByRow(row)">
              <a-space>
                <a-button type="dashed" size="small" @click.stop="saveMaterialRowEvent(row)">保存</a-button>
                <a-button type="dashed" size="small" @click.stop="cancelMaterialRowEvent(row)">取消</a-button>
              </a-space>
            </template>
            <template v-else>
              <a-button type="dashed" :disabled="false" size="small" @click.stop="editMaterialRowEvent(row)"
                >填写</a-button
              >
            </template>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>

    <work-station-list ref="stationSelectModal" @ok="onSelectStation"></work-station-list>
    <user-list ref="userSelectModal" :depId="groupId" :multiple="false" @ok="onSelectUser"></user-list>
  </div>
</template>

<script>
import { saveWorkTime, delWorkArrange, getWorkOrderDetail } from '@/api/tirosGroupApi'
import WorkStationList from '@views/tiros/common/selectModules/WorkStationList'
import UserList from '@views/tiros/common/selectModules/UserList'

export default {
  name: 'TaskWorkTime',
  components: { UserList, WorkStationList },
  props: {
    staffArranges: {
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
    operator: {
      type: Number,
      default: 0,
    },
    workOrderId: {
      type: String,
      default: null,
    },
  },
  watch: {
    selectTask() {
      this.refresh()
    },
  },
  data() {
    return {
      groupId: '',
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      workStationDataList: [],
      isInputPlanTime: true,
      validRules: {
        ordTskWkStationId: [{ required: true, message: '请选择任务', trigger: 'manual' }],
        workstationName: [{ required: true, message: '请选择工位', trigger: 'manual' }],
        userName: [{ required: true, message: '请选择人员', trigger: 'manual' }],
        // planWorkTime: [{ required: true, message: '请输入计划工时' }],
        workTime: [{ required: true, message: '请输入实际工时', trigger: 'manual' }],
      },
      workStations: [],
    }
  },
  created(){
    try { // 执行块
      this.groupId = this.$store.state.user.info.departId
    } catch (error) {  // 上述代码块执行出错时执行
      console.log(error)
    }
  },
  mounted() {
    this.refresh()
  },
  methods: {
    refresh() {
      try {
        let orderId = null
        if (this.workOrderId) {
          orderId = this.workOrderId
        } else {
          orderId = this.selectTask.orderId
        }
        if (orderId) {
          getWorkOrderDetail(orderId).then((res) => {
            if (res.success) {
              this.workStations = res.result.workstations
              this.staffArranges.length = 0
              this.workStations.forEach((item) => {
                if (this.selectTask) {
                  if (item.orderTaskId === this.selectTask.id) {
                    item.staffArrangeList.forEach((item2) => {
                      this.staffArranges.push(item2)
                    })
                  }
                } else {
                  item.staffArrangeList.forEach((item2) => {
                    this.staffArranges.push(item2)
                  })
                }
              })
            }
          })
        }
      } catch (error) {
        console.log(error)
      }
    },
    checkIsAddStation(row) {
      // 检查是否新增工位
      if (this.workStations) {
        const record = this.workStations.find((e) => e.orderTaskId === row.orderTaskId)
        if (record) {
          this.isInputPlanTime = record.staffArrangeList.find((e) => e.workstationId === row.workstationId)
            ? false
            : true
          if (!this.isInputPlanTime) {
            this.$refs.listTable.getActiveRecord().row.planWorkTime = record.workTime
          }
        } else {
          this.isInputPlanTime = true
        }
      }
    },
    getWorkStationName(row) {
      let item = this.workStationDataList.find((e) => e.id === row.ordTskWkStationId)
      return item ? item.workstationName : ''
    },
    editMaterialRowEvent(row) {
      this.curEditMode = 2
      this.$refs.listTable.setActiveRow(row)
    },
    saveMaterialRowEvent(row) {
      if (this.curEditMode === 1) {
        if (
          this.staffArranges.find(
            (e) => e.workstationId === row.workstationId && e.userId === row.userId && e.orderTaskId === row.orderTaskId
          )
        ) {
          this.$message.warn('已经录入相同工位的员工工时')
          return
        }
      }

      this.$refs.listTable.validate(row, (valid) => {
        if (!valid) {
          saveWorkTime([row]).then((res) => {
            if (res.success) {
              this.refresh()
              this.$emit('ok')
              // this.$message.success('填报成功')
            } else {
              this.$refs.listTable.revertData(row)
              this.$message.error('保存失败')
            }
          })
          this.$refs.listTable.clearActived()
          this.curEditMode = 0
        }
      })
    },
    cancelMaterialRowEvent(row) {
      this.$refs.listTable.clearActived()
      if (this.curEditMode === 1) {
        this.$refs.listTable.remove(row)
      } else if (this.curEditMode === 2) {
        this.$refs.listTable.revertData(row)
      }
      this.curEditMode = 0
    },
    handleAdd() {
      this.curEditMode = 1
      this.isInputPlanTime = true
      if (this.$refs.listTable.getActiveRecord()) {
        return
      }
      this.curEditMode = 1
      // randomUUID()
      let time = {
        id: '',
      }
      if (this.selectTask) {
        Object.assign(time, {
          ordTskWkStationId: null,
          orderId: this.selectTask.orderId,
          orderTaskId: this.selectTask.id,
          userId: null,
          userName: null,
          planWorkTime: null,
          workTime: null,
          workstationId: null,
          workstationName: null,
          workstationNo: null,
        })
        this.$refs.listTable.insertAt(time, -1).then(({ row }) => {
          this.$refs.listTable.setActiveCell(row, 'userName')
        })
      } else {
        Object.assign(time, {
          ordTskWkStationId: null,
          orderId: null,
          orderTaskId: null,
          userId: null,
          userName: null,
          planWorkTime: null,
          workTime: null,
          workstationId: null,
          workstationName: null,
          workstationNo: null,
        })
        if (this.tasks.length) {
          Object.assign(time, {
            orderTaskId: this.tasks[0].id,
            taskName: this.tasks[0].taskName,
          })
        }
        this.$refs.listTable.insertAt(time, -1).then(({ row }) => {
          // this.$refs.listTable.setActiveCell(row)
          this.$refs.listTable.setActiveRow(row)
        })
      }
    },
    handleDel() {
      let m = this.$refs.listTable.getCheckboxRecords()
      if (m.length > 0) {
        this.$confirm({
          content: `是否删除选中${m.length}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let delList = []
            m.map((item1) => {
              this.staffArranges.map((item2, index2) => {
                if (item1.id === item2.id) {
                  delList.push(item2.id)
                  // this.staffArranges.splice(index2, 1)
                }
              })
            })
            let formData = new FormData()
            formData.append('ids', delList.toString())
            delWorkArrange(formData).then((res) => {
              if (res.success) {
                delList.forEach((item1) => {
                  this.staffArranges.map((item2, index2) => {
                    if (item1 === item2.id) {
                      this.staffArranges.splice(index2, 1)
                    }
                  })
                })
                this.$emit('ok')
                this.$message.success('删除成功')
              } else {
                this.$message.error('删除失败')
              }
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    taskSelectChange(value, option) {
      this.$refs.listTable.getActiveRecord().row.orderTaskId = this.tasks[option.key].id
      // this.$refs.listTable.validate(this.$refs.listTable.getActiveRecord().row, (errMap) => {})
    },
    onSelectStation(data) {
      if (data && data.length > 0) {
        let station = data[0]
        this.curRow.workstationId = station.id
        this.curRow.workstationName = station.name
        this.curRow.workstationNo = station.stationNo
      } else {
        this.curRow.workstationId = ''
        this.curRow.workstationName = ''
        this.curRow.workstationNo = ''
      }
      this.checkIsAddStation(this.curRow)
      // this.$refs.listTable.validate(this.$refs.listTable.getActiveRecord().row, (errMap) => {})
    },
    openSelectUser(row) {
      this.curRow = row
      this.$refs.userSelectModal.showModal()
      this.$refs.userSelect.blur()
    },
    onSelectUser(data) {
      if (data.length) {
        this.curRow.userId = data[0].id
        this.curRow.userName = data[0].realname
        this.$forceUpdate()
      }
    },
    openStationSelectModal(row) {
      this.curRow = row
      this.$refs.stationSelectModal.showModal()
      this.$refs.stationSelect.blur()
    },
    clearValidate(){
      this.$refs.listTable.clearValidate()
    }
  },
}
</script>

<style>
</style>