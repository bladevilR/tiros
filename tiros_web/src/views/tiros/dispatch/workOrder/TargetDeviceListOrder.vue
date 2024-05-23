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
        :data.sync="targetDeviceList"
        :keep-source="true"
        :checkbox-config="{ highlight: true, range: true }"
        :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
        @blur="$refs.listTable.clearValidate()"
      >
        <vxe-table-column type="seq" width="40"></vxe-table-column>
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-if="tasks && tasks.length>1" field="orderTaskName" title="工单任务" align="left" :edit-render="{ name: 'input' }">
          <template v-slot:edit="{ row }">
            <a-select v-model="row.orderTaskId" style="width: 100%" @change="taskSelectChange" @blur="$refs.listTable.clearValidate()">
              <a-select-option v-for="(task, index) in tasks" :value="task.id" :key="index">
                {{ task.taskName }}
              </a-select-option>
            </a-select>
          </template>
        </vxe-table-column>
        
        <vxe-table-column
            field="trainAssetName"
            title="名称"
            tree-node
            align="left"
            header-align="center"
        ></vxe-table-column>
        <vxe-table-column align="left" header-align="center" field="trainAssetNo" title="编码" width="12%"></vxe-table-column>
        <!-- <vxe-table-column field="status_dictText" title="状态" width="100"></vxe-table-column> -->
        <vxe-table-column field="trainAssetStructType_dictText" title="类型" width="10%"></vxe-table-column>
        <!-- <vxe-table-column
            field="assetTypeName"
            title="设备类型"
            width="13%"
            align="left"
            header-align="center"
        ></vxe-table-column> -->
        
        <vxe-table-column title="操作" width="150" v-if="getButtonShow() && tasks.length > 1">
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

    <!-- 旧的资产设备选择 -->
    <!-- <CarDeviceSelect
      ref="trainAssetSelectModal"
      :trainNo="trainNo"
      :lineId="lineId"
      :multiple="true" 
      @ok="onSelectToolMultiple"
    >
    </CarDeviceSelect> -->
    <!-- maximo资产设备选择 -->
    <CarDeviceSelectNew
      ref="trainAssetSelectModal"
      :trainNo="trainNo"
      :lineId="lineId"
      :multiple="true" 
      @ok="onSelectToolMultiple"
    >
    </CarDeviceSelectNew>
  </div>
</template>

<script>

// import { randomUUID } from '@/utils/util'
// import CarDeviceSelect from "@views/tiros/common/selectModules/CarDeviceSelect";
import CarDeviceSelectNew from "@views/tiros/common/selectModules/CarDeviceSelectNew";

export default {
  name: 'targetDeviceListOrder',
  components: { 
    // CarDeviceSelect, 
    CarDeviceSelectNew 
  },
  props: {
    lineId: {
      type: [String, Number],
      default: "",
    },
    trainNo: {
      type: [String, Number],
      default: "",
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
      targetDeviceList: [],
    }
  },
  watch:{
    tasks:{
      handler(newValue){
        if(newValue && newValue.length){
          let arr = [];
          for (let i = 0, len = newValue.length; i < len; i++) {
            newValue[i].equipments && newValue[i].equipments.forEach(element => {
              arr.push({
                ...element,
                orderTaskName: newValue[i].taskName
              })
            });
          }
          this.targetDeviceList = arr
          console.log(arr)
        }
      },
      deep:true,
      immediate:true
    }
  },
  methods: {
    updateTask(){
      let tasks = JSON.parse(JSON.stringify(this.tasks));
      console.log(tasks)
      console.log(this.targetDeviceList)
      Array.from(tasks,(item,index)=>{
        item.equipments = this.targetDeviceList.filter((item2) => {
          return  item.id == item2.orderTaskId //条件;
        });
      }) 
      // console.log(tasks)
      this.$emit('update:tasks',tasks)
    },
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
      this.curEditMode = 1
      this.$refs.trainAssetSelectModal.showModal()
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
              this.targetDeviceList.map((item2, index2) => {
                console.log(item1._XID === item2._XID)
                if (item1._XID === item2._XID) {
                  this.targetDeviceList.splice(index2, 1)
                }
              })
            })
            // 更新任务数据
            this.updateTask()
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
      this.targetDeviceList.map((item2, index2) => {
        if (row.id === item2.id) {
          this.targetDeviceList.splice(index2, 1)
        }
      })
    },
    saveRowEvent(row) {
      this.$refs.listTable.validate(row, (valid) => {
        if (!valid) {
          if (this.curEditMode === 1) {
            this.targetDeviceList.push(row)
          }
          this.$refs.listTable.clearActived()
          this.curEditMode = 0
          // 更新任务数据
          this.updateTask()
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
    onSelectToolMultiple(data){
      data.forEach((element, index) => {
          if (this.targetDeviceList.find(e => e.trainAssetId === element.id)) {
            this.$message.warn(`你选择的${element.name}已经在列表中存在`)
            return
          }else{
            console.log(element)
            let newRow ={
              trainAssetName: element.name,
              trainAssetId: element.id,
              trainAssetNo: element.code,
              trainAssetStructType_dictText: element.maximoAssetType,
            }

            if (this.tasks.length) {
              Object.assign(newRow, {
                orderTaskId: this.tasks[0].id,
                orderTaskName: this.tasks[0].taskName,
              })
            }

            // if (this.selectTask) {
            //   Object.assign(newRow, {
            //     orderTaskId: this.selectTask.id,
            //     taskName: this.selectTask.taskName,
            //   })
            // }else{
              
            // }

            // 不管是一个还是多个，都直接添加，不需要点击单元格中的保存
            this.$refs.listTable.insertAt(newRow, -1)
            this.targetDeviceList.push(newRow)
            // 更新任务数据
            this.updateTask()
          }
      })
    },
    taskSelectChange(value, option) {
      this.$refs.listTable.getActiveRecord().row.orderTaskName = this.tasks[option.key].taskName
      this.$refs.listTable.validate(this.$refs.listTable.getActiveRecord().row, (errMap) => {})
    },
    clearValidate(){
      this.$refs.listTable.clearValidate()
    }
  },
}
</script>

<style>
</style>