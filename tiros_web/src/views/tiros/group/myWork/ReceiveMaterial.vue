<template>
  <div class="test-class" :style="`height: calc(100% - ${getButtonShow() ? '47px' : '4px'});`">
    <div na-flex-height-full v-if="operator !== 2 && operator !== 3">
      <a-form layout="inline" v-if="getButtonShow()">
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item>
              <a-space>
                <a-button type="dashed" class="primary-color" @click="handleAdd">添加</a-button>
                <a-button type="dashed" @click="handleDel">删除</a-button>
                <a-button type="primary" v-if="opType === 2" @click="handleSubmit">提交领料申请</a-button>
                <span style="color: #dbaaaa" v-if="opType === 2">&lt;- 提交领料申请后，将暂停作业填报，工单重新流转到物料员进行发料</span>
                <span style="color: #dbaaaa" v-if="opType === 3">&lt;- 添加的退回物料，在工班长提交工单后，统一生成退料单提交给物料员</span>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <div na-flex-height-full>
        <!-- 临时领料 -->
        <vxe-table
          v-if="opType === 2"
          border
          ref="listTable"
          align="center"
          max-height="auto"
          :data.sync="taskMaterials"
          :edit-rules="validRules"
          :keep-source="true"
          :checkbox-config="{ highlight: true, range: true }"
          :row-style="checkMaterialShow"
          :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
          @blur="$refs.listTable.clearValidate()"
        >
          <vxe-table-column type="checkbox" width="40"></vxe-table-column>
          <vxe-table-column field="code" title="物料编码" :edit-render="{ name: 'input' }">
            <template v-slot:edit="{ row }">
              <a-select
                ref="formSelect"
                v-model="row.code"
                placeholder="请选择物料"
                :open="false"
                style="width: 100%"
                @dropdownVisibleChange="openMaterialSelectModal(row)"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="name"
            title="物料名称"
            :edit-render="{ name: 'input' }"
            align="left"
            header-align="center"
          >
            <template v-slot:edit="{ row }">
              {{ row.name }}
            </template>
          </vxe-table-column>
          <vxe-table-column field="useCategory_dictText" title="类别" :edit-render="{ name: 'input' }">
            <template v-slot:edit="{ row }">
              <j-dict-select-tag
                v-model="row.useCategory"
                dictCode="bu_material_type"
                style="width: 100%"
                @select="onCategoryChange"
                @blur="$refs.listTable.clearValidate()"
              />
            </template>
          </vxe-table-column>
          <vxe-table-column field="amount" title="所需数量" :edit-render="{ name: 'input' }">
            <template v-slot:edit="{ row }">
              <a-input-number v-model="row.amount" :defaultValue="1" :min="1" :max="999999" style="width: 100%" />
            </template>
          </vxe-table-column>
          <vxe-table-column field="actAmount" title="实际消耗"></vxe-table-column>
          <vxe-table-column field="unit" title="单位" :edit-render="{ name: 'input' }">
            <template v-slot:edit="{ row }">
              {{ row.unit }}
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="remark"
            title="消耗备注"
            align="left"
            :edit-render="{ name: '$input', props: { type: 'text' } }"
          ></vxe-table-column>
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
              </template>
            </template>
          </vxe-table-column>
        </vxe-table>
        <!-- 退料 -->
        <vxe-table
          v-if="opType === 3"
          border
          ref="listTable"
          align="center"
          max-height="auto"
          :data.sync="taskMaterials"
          :edit-rules="validRules"
          :keep-source="true"
          :checkbox-config="{ highlight: true, range: true }"
          :row-style="checkMaterialShow2"
          :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
          @blur="$refs.listTable.clearValidate()"
        >
          <vxe-table-column type="checkbox" width="40"></vxe-table-column>
          <vxe-table-column field="code" title="物料编码" :edit-render="{ name: 'input' }">
            <template v-slot:edit="{ row }">
              <a-select
                ref="formSelect"
                v-model="row.code"
                placeholder="请选择物料"
                :open="false"
                style="width: 100%"
                @dropdownVisibleChange="openMaterialSelectModal(row)"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="name"
            title="物料名称"
            :edit-render="{ name: 'input' }"
            align="left"
            header-align="center"
          >
            <template v-slot:edit="{ row }">
              {{ row.name }}
            </template>
          </vxe-table-column>
          <vxe-table-column field="amount" title="退料数量" :edit-render="{ name: 'input' }">
            <template v-slot:edit="{ row }">
              <a-input-number v-model="row.amount" :defaultValue="1" :min="1" :max="10000" style="width: 100%" />
            </template>
          </vxe-table-column>
          <vxe-table-column field="unit" title="单位" :edit-render="{ name: 'input' }">
            <template v-slot:edit="{ row }">
              {{ row.unit }}
            </template>
          </vxe-table-column>
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
              </template>
            </template>
          </vxe-table-column>
        </vxe-table>
      </div>
    </div>
    <material-list ref="modalForm" :multiple="false" :mode="[1,2]"  :show-type-code="true" :typeCodeList="[2,3,-1]" @ok="onSelectMaterial"></material-list>
    <ReturnMaterialModal ref="returnMaterialModal" @ok="onSelectReturnMaterial"></ReturnMaterialModal>
    <task-select-modal ref="taskSelectModal" @ok="onSelectTask"></task-select-modal>
  </div>
</template>

<script>
import MaterialList from '@views/tiros/common/selectModules/MaterialList'
import TaskSelectModal from '@views/tiros/dispatch/workOrder/TaskSelectModal'
import ApplyReadyComponent from '@views/tiros/material/apply/ApplyReadyComponent'
import ApplyConfirmComponent from '@views/tiros/material/apply/ApplyConfirmComponent'
import ReturnMaterialModal from '@views/tiros/group/modules/ReturnMaterialModal'
import { getDetailListByOrderId } from '@api/tirosMaterialApi'
import { commitTempOrderMaterial, delMaterial,getMaterialApplyList, saveMaterial } from '@api/tirosGroupApi'

export default {
  name: 'ReceiveMaterial',
  components: { TaskSelectModal, MaterialList, ApplyReadyComponent, ApplyConfirmComponent,ReturnMaterialModal },
  props: {
    taskMaterials: {
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
    opType: {
      type: Number,
      default: 2,
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
  data() {
    return {
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      validRules: {
        taskName: [{ required: true, message: '请选工单任务', trigger: 'manual' }],
        code: [{ required: true, message: '请选择物料', trigger: 'manual' }],
        useCategory_dictText: [{ required: true, message: '类别必须选择', trigger: 'manual' }],
        amount: [{ required: true, message: '数量必须填写', trigger: 'manual' }],
        actAmount: [{ required: this.opType === 2, message: '数量必须填写', trigger: 'manual' }],
      },
      applyMaterialList:[],
      typeCodeList: []
    }
  },
  mounted() {},
  methods: {
    onCategoryChange(value, option) {
      let record = this.$refs.listTable.getActiveRecord()
      if (option) {
        record.row.useCategory_dictText = option.title
      } else {
        record.row.useCategory_dictText = undefined
      }

      this.$forceUpdate()
    },
    save(onlySave) {},
    saveOk() {
      this.$emit('ok')
    },
    saveFail(e) {
      this.$emit('fail', e)
    },
    getButtonShow() {
      if (this.readOnly) {
        return false
      }
      if (this.operator === 0 || this.operator === 1 || this.operator === 2) {
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

      if (this.opType === 3) {
        // 不是这么做的
        // this.getAppleMaterialList()
        this.$refs.returnMaterialModal.showModal(this.selectTask.orderId)
        return
      }else{
        this.applyMaterialList = []
      }
      let material = {
        // id: randomUUID(),
        useCategory: null,
        opType: this.opType,
        isGenOrder: 0,
        opType_dictText: this.opType === 2 ? '临时新增' : '退还物资'
      }
      if (this.selectTask) {
        Object.assign(material, {
          orderId: this.selectTask.orderId,
          orderTaskId: this.selectTask.id,
          taskName: this.selectTask.taskName,
        })
      }
      this.$refs.listTable.insertAt(material, -1).then(({ row }) => {
        this.$refs.listTable.setActiveCell(row, 'code')
      })
    },
    handleDel() {
      let m = this.$refs.listTable.getCheckboxRecords()
      m = m.filter(e => e.opType === this.opType)
      if(m.find(e => e.isGenOrder === 1 && this.opType === 2)){
        this.$message.warn(`${m.find(e => e.isGenOrder === 1 && this.opType === 2).name}物料已经生成单据，不能删除`)
        return
      }
      if (m.length > 0) {
        this.$confirm({
          content: `是否删除选中${m.length}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            /*let delList = []
            m.map((item1) => {
              this.taskMaterials.map((item2, index2) => {
                if (item1.id === item2.id) {
                  delList.push(item2.id)
                  // this.staffArranges.splice(index2, 1)
                }
              })
            })*/
            let delList=m.map(item =>  item.id)
            delMaterial({ids: delList.join(',')}).then((res) => {
              if (res.success) {
                delList.forEach((item1) => {
                  this.taskMaterials.map((item2, index2) => {
                    if (item1 === item2.id) {
                      this.taskMaterials.splice(index2, 1)
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
    handleSubmit() {
      // let records = this.taskMaterials.filter(row => row.opType === 2 && (!row.isGenOrder || row.isGenOrder == 0 ))
      // 只有需要发料，且没有发放的，都提交
      let records = this.taskMaterials.filter(row => row.opType === 2 && row.needDispatchin ===1 && row.isGenOrder === 0 )
      if (!records.length) {
        this.$message.warn('没有新添加的物料明细，不能提交')
        return
      }

      this.$confirm({
          content: `确认是否发起领料申请？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let formData = new FormData()
            formData.append('orderId', this.selectTask.orderId)
            formData.append('opType', 2)
            commitTempOrderMaterial(formData).then(res =>{
              if (res.success) {
                this.$message.success('提交申请成功')
                records.forEach(record=>{
                  if (this.taskMaterials[this.taskMaterials.findIndex(e => e == record )]) {
                    this.taskMaterials[this.taskMaterials.findIndex(e => e == record )].isGenOrder = 1
                    this.taskMaterials[this.taskMaterials.findIndex(e => e == record )].isGenOrder_dictText = '是'
                  }
                })
                this.$emit('submit')
              } else {
                this.$message.error(res.message)
              }
            })
          },
        })
    },
    editRowEvent(row) {
      this.curEditMode = 2
      if (this.opType === 3) {
        this.getAppleMaterialList()
      }else{
        this.applyMaterialList = []
      }
      this.$refs.listTable.setActiveRow(row)
    },
    delRowEvent(row) {
      this.taskMaterials.map((item2, index2) => {
        if (row.id === item2.id) {
          this.taskMaterials.splice(index2, 1)
        }
      })
    },
    async saveRowEvent(row) {
      this.$refs.listTable.validate(row, (valid) => {
        if (!valid) {
          // 退料
          if (this.opType === 3) {
            if (this.applyMaterialList.find(e => e.materialTypeId === row.materialTypeId).amount < row.amount) {
              this.$message.warn('退料数量不能大于领料数量')
              return
            }
          }

          if (row.actAmount > row.amount) {
            this.$message.error('实际消耗不能大于所需数量')
            return
          }

          this.$emit(this.curEditMode === 1 ? 'add' : 'edit', row)
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
    getAppleMaterialList(){
      // 2021-06-21 修改
      this.applyMaterialList = []
      getMaterialApplyList({
        orderId: this.selectTask.orderId
      }).then(res =>{
        if (res.success) {
          res.result.forEach(apply =>{
            this.applyMaterialList = [...this.applyMaterialList, ...apply.detailList]
          })
        }else{
          this.$message.error('读取领用明细失败')
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
    openMaterialSelectModal(row) {
      this.curRow = row
      this.$refs.modalForm.showModal()
      // this.$refs.formSelect.blur()
      this.$refs.listTable.clearValidate()
    },
    onSelectMaterial(data) {
      if (data && data.length > 0) {
        const material = data[0]

        // let exists = this.taskMaterials.filter((m) => {
        //   return m.materialTypeId === material.id
        // })

        // if (exists && exists.length > 0) {
        //   this.$message.error('你选择的物料已经在列表中存在，请选择其他物料')
        //   return
        // }

        this.curRow.code = material.code
        this.curRow.materialTypeId = material.id
        this.curRow.name = material.name
        this.curRow.kind = material.kind
        this.curRow.unit = material.unit
        this.curRow.kind_dictText = material.kind_dictText
        if(this.opType===2 && material.category1!==-1) {
          this.curRow.useCategory = material.category1 ? material.category1 : null
          this.curRow.useCategory_dictText = material.category1 ? material.category1_dictText : ''
        }
        this.curRow.amount = material.num
        this.curRow.actAmount = 0
/*        this.curRow.opType= 2
        this.curRow.opType_dictText = '临时新增'*/
        this.curRow.needDispatchin = 1
        this.curRow.needDispatchin_dictText= '是'
      } else {
        this.curRow.code = ''
        this.curRow.materialTypeId = ''
        this.curRow.name = ''
        this.curRow.kind = ''
        this.curRow.kind_dictText = ''
        this.curRow.useCategory = ''
        this.curRow.useCategory_dictText = ''
        this.curRow.unit = ''
        this.curRow.amount = 0
        this.curRow.actAmount = 0
        this.curRow.actAmount = 0
/*        this.curRow.opType= 2
        this.curRow.opType_dictText = '临时新增'*/
        this.curRow.needDispatchin = 1
        this.curRow.needDispatchin_dictText= '是'
      }
    },
    onSelectReturnMaterial(records){
      let tempList = []
      records.forEach(record =>{
        if (this.taskMaterials.find(e => e.materialTypeId === record.materialTypeId && e.opType === 3)) {
          this.$message.warn(`${record.materialTypeName}物资已存在`)
          return
        }
        let material = {
          useCategory: null,
          isGenOrder: 0,
          opType: this.opType,
          opType_dictText: '退还物资',
        }
        if (this.selectTask) {
          Object.assign(material, {
            orderId: this.selectTask.orderId,
            orderTaskId: this.selectTask.id,
            taskName: this.selectTask.taskName,
          })
        }
        Object.assign(material, {
          code: record.materialTypeCode,
          materialTypeId: record.materialTypeId,
          name: record.materialTypeName,
          unit: record.materialTypeUnit,
          useCategory: record.useCategory,
          useCategory_dictText: record.useCategory_dictText,
          amount: record.num,
          actAmount: 0,
          planAmount: 0,
          needDispatchin: 0
        })
        tempList.push(saveMaterial(material).then((res) => {
          if (res.success) {
            return material
          } else {
            this.$message.error('保存错误')
            console.error('保存物料消耗错误：', res.message)
            return null
          }
        }).catch(err =>{
          this.$message.error('保存异常')
          console.error('保存物料消耗异常：', err)
          return null
        }))
      })
      if (!tempList.length) {
          return
      }
      Promise.all(tempList).then(res =>{
        this.$message.success('执行成功')
        this.$emit('addReturnMateriles', res.filter(e => e))
      })
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
      // this.$refs.listTable.validate(this.$refs.listTable.getActiveRecord().row, (errMap) => {})
      // this.$refs.listTable.clearValidate()
    },
    clearValidate() {
      this.$refs.listTable.clearValidate()
    },
    checkMaterialShow({row}){
      // 列表中不是2的不显示
      if (row.opType !== this.opType) {
        return 'display: none;'
      }
      // 不需要发料，或者已经生成过领料单的 都不在这个列表显示
      if ( row.needDispatchin ===0 || row.isGenOrder === 1) {
        return 'display: none;'
      }
    },
    checkMaterialShow2({row}){
       // 不等3都不显示
        if (row.opType !== this.opType) {
          return 'display: none;'
        }
    }
  },
}
</script>

<style scoped>
.hiddenDom{
  display: none;
}
</style>