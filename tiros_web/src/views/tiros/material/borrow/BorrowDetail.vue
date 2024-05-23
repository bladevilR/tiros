<template>
  <NaCardContent title="借用明细" height="100%">
    <a-row :gutter="24">
      <a-col :span="24" style="margin-bottom: 12px">
        <a-space>
          <!-- <a-button @click="findList()">刷新</a-button> -->
          <a-button type="primary" v-if="!isSaveData" @click="handleAdd">新增</a-button>
          <a-button type="dashed" v-if="isCheckRow && !isSaveData" @click="handleEdit">编辑</a-button>
          <a-button type="dashed" v-if="isCheckRows && !isSaveData" @click="handleDel">删除</a-button>
          <a-button type="primary" v-if="isSaveData" @click="saveRowData">保存</a-button>
          <a-button v-if="isSaveData" @click="cencelRowData">取消</a-button>
        </a-space>
      </a-col>
    </a-row>
    <div class="table-page-body-wrapper" style="height: 100%">
      <div class="table-body-context">
        <vxe-table
          border
          ref="listTable"
          align="center"
          height="auto"
          :keep-source="true"
          :edit-config="{ trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
          :data="tableData"
          :edit-rules="validatorRules"
          :checkbox-config="{trigger: 'row', highlight: true, range: true }"
          @checkbox-change="rangeChange"
          @checkbox-all="rangeChange"
        >
          <vxe-table-column type="checkbox" width="60px" />
          <vxe-table-column field="materialTypeCode" title="物资编码" />
          <vxe-table-column field="materialTypeName" title="物资名称" />
          <vxe-table-column field="materialTypeSpec" title="物资描述" />
          <vxe-table-column field="materialTypeUnit" title="单位" />
          <vxe-table-column field="amount" title="借用数量" :edit-render="{ name: 'input' }">
            <template v-slot:edit="{ row }">
              <a-input-number v-model="row.amount" :defaultValue="1" :min="1" :max="10000000" style="width: 100%" />
            </template>
          </vxe-table-column>
          <vxe-table-column field="reason" title="借用理由" :edit-render="{ name: 'input' }" />
          <vxe-table-column field="warehouseName" title="库位" />
          <vxe-table-column field="returnStatus_dictText" title="归还状态" :edit-render="{ name: 'input' }">
            <template v-slot:edit="{ row }">
              <j-dict-select-tag
                v-model="row.returnStatus"
                :dictCode="dictReturnStatusStr"
                style="width: 100%"
                @select="onReturnStatusChange"
              />
            </template>
          </vxe-table-column>
          <vxe-table-column field="returnDate" title="归还日期" :edit-render="{ name: 'input' }">
            <template v-slot:edit="{}">
              <a-date-picker v-model="pickDate" format="YYYY-MM-DD" style="width: 100%" />
            </template>
          </vxe-table-column>
        </vxe-table>
      </div>
    </div>
    <material-list ref="materialModal" :multiple="false" @ok="onSelectMaterial"></material-list>
  </NaCardContent>
</template>

<script>
import NaCardContent from '@comp/tiros/NaCardContent'
import WorkSopDetailModal from '@views/tiros/basic/modules/worksop/WorkSopDetailModal'
import { getBorrowDetail } from '@/api/tirosMaterialApi'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'

export default {
  name: 'BorrowDetail',
  components: { NaCardContent, WorkSopDetailModal, MaterialList },
  props: {
    tableData: {
      type: Array,
      default: [],
    },
    borrowInfo: {
      type: Object,
      default: {},
    },
  },
  data() {
    return {
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      isCheckRow: false,
      isCheckRows: false,
      isSaveData: false,
      visible: false,
      dictReturnStatusStr: 'bu_material_return_status',
      confirmLoading: false,
      pickDate: null,
      queryParam: {
        id: '',
      },
      validatorRules: {
        reason: [{ max:64, message: '输入长度不能超过64个字符!'} ]
      },
    }
  },
  created() {},
  methods: {
    initStatus() {
      this.isSaveData = false
      this.isCheckRow = false
      this.isCheckRows = false
      this.updateDict(this.borrowInfo.returnType)
    },
    updateDict(type) {
      if (type == 1) {
        this.dictReturnStatusStr = 'bu_material_return_status'
      } else if (type == 2) {
        this.dictReturnStatusStr = 'bu_material_transfers_status'
      }
    },
    // 保存数据
    save() {
      this.isSaveData = false
    },
    //保存Row数据
    saveRowData() {
      let record = this.$refs.listTable.getActiveRecord()
      if (!record) {
        return
      }
      this.$refs.listTable.validate(record.row, (valid) => {
        if (!valid) {
          if (this.pickDate) {
            record.row.returnDate = this.pickDate.format('YYYY-MM-DD')
          }
          if (!this.tableData.find((e) => e === record.row)) {
            this.tableData.push(Object.assign({}, record.row))
          }
          this.isSaveData = false
          this.curEditMode = 0
          this.$refs.listTable.clearActived()
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
      this.rangeChange()
    },
    // 取消编辑或保存
    cencelRowData() {
      let record = this.$refs.listTable.getActiveRecord()
      if (record) {
        if (record.row.id || this.curEditMode === 2) {
          // 还原行数据
          this.$refs.listTable.revertData(record.row)
        } else {
          // 新增,点击取消
          this.$refs.listTable.remove(record.row)
        }
      }
      this.$refs.listTable.clearActived()
      this.isSaveData = false
      this.curEditMode = 0
    },
    // 用户选择记录触发
    rangeChange() {
      let records = this.$refs.listTable.getCheckboxRecords()
      this.isCheckRow = records.length === 1
      this.isCheckRows = records.length > 0
    },
    handleAdd() {
      this.curEditMode = 1
      this.$refs.materialModal.showModal()
    },
    handleEdit() {
      this.curEditMode = 2
      if (this.$refs.listTable.getActiveRecord()) {
        this.$message.warn('有未保存数据')
        return
      }
      let record = this.$refs.listTable.getCheckboxRecords()[0]
      this.$refs.listTable.setActiveRow(record)
      if (record.returnDate) {
        this.pickDate = this.$moment(record.returnDate)
      }else{
        this.pickDate = null
      }
      this.isSaveData = true
    },
    handleDel() {
      let records = this.$refs.listTable.getCheckboxRecords()
      this.$confirm({
        content: `是否删除选中${records.length}条数据？`,
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          records.forEach((record) => {
            this.tableData.splice(
              this.tableData.findIndex((e) => e === record),
              1
            )
            this.isCheckRow = false
            this.isCheckRows = false
          })
        },
      })
    },
    onReturnStatusChange(value, option) {
      let record = this.$refs.listTable.getActiveRecord()
      record.row.returnStatus_dictText = option.title
    },
    // 选择物料
    onSelectMaterial(data) {
      console.log(data);
      data.forEach((element) => {
        if (this.tableData.find((e) => e.materialTypeId === element.id)) {
          this.$message.warn(`${element.name}物料已经存在列表中`)
          return
        }
        let item = {
          amount: Number(element.num),
          borrowId: this.borrowInfo.borrowId,
          id: null,
          materialTypeCode: element.code,
          materialTypeId: element.id,
          materialTypeName: element.name,
          materialTypeSpec: element.spec,
          materialTypeUnit: element.unit,
          reason: '',
          remark: '',
          returnDate: null,
          returnStatus: 0,
          returnStatus_dictText: '未归还',
          warehouseLocationId: this.borrowInfo.warehouseLocationId,
          warehouseName: this.borrowInfo.warehouseName,
        }
        this.pickDate = null
        this.isSaveData = true
        this.$refs.listTable.insertAt.call(this, item, -1).then(({ row }) => {
          this.isSaveData = true
          this.$refs.listTable.setActiveCell(row, 'amount')
        })
      })
    },
  },
}
</script>