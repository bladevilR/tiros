<template>
  <div class='bodyWrapper'>
    <div class='table-page-search-wrapper'>
      <a-form layout='inline'>
        <a-row :gutter='24'>
          <a-col :md='5' :sm='24'>
            <a-form-item label='编码或名称'>
              <a-input v-model='queryParam.searchText' placeholder='请输入编码或名称' allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='类型'>
              <j-dict-select-tag v-model='queryParam.attributeType' placeholder='请选择类型' dictCode='repair_attribute_type' />
            </a-form-item>
          </a-col>
          <a-col :md='11'>
            <span class='table-page-search-submitButtons'>
              <a-space>
                <a-button @click='findList'>查询</a-button>
                <a-button type='primary' @click='handleAdd'>新增</a-button>
                <a-button :disabled='!isCheckRow' @click='handleEdit'>编辑</a-button>
                <a-button :disabled='!isCheckRows' @click='handleDel'>删除</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div class='table-page-body-wrapper' style='height: calc(100% - 52px)'>
      <div class='table-body-context'>
        <vxe-table
          border
          ref='listTable'
          align='center'
          height='auto'
          auto-resize
          :data='tableData'
          :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          @checkbox-change='rangeChange'
          @checkbox-all='rangeChange'
        >
          <vxe-table-column type='checkbox' width='60px' />
          <vxe-table-column field='attributeType_dictText' title='类型' />
          <vxe-table-column field='attributeCode' title='编码' />
          <vxe-table-column field='attributeName' title='名称' />
          <vxe-table-column field='parentType_dictText' title='上级类型' />
          <vxe-table-column field='parentName' title='上级' />
          <vxe-table-column field='programNames' title='关联修程' >
            <template v-slot="{ row }">
              <div :style="{ color: '#1890ff', borderRadius: '4px' }">
                {{ row.programNames }}
              </div>
            </template>
          </vxe-table-column>
          <vxe-table-column field='remark' title='备注' />
        </vxe-table>
      </div>
      <vxe-pager
        perfect
        :current-page.sync='queryParam.pageNo'
        :page-size.sync='queryParam.pageSize'
        :total='page.totalResult'
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change='handlePageChange'
      ></vxe-pager>
    </div>
    <RepairAttrModal ref='repairAttrModal' @ok='findList'></RepairAttrModal>
  </div>
</template>
<script>

import { pageRepairAttribute, deleteRepairAttribute } from '@/api/tirosApi'
import RepairAttrModal from '@views/tiros/basic/modules/repairAttr/RepairAttrModal'

export default {
  name: 'RepairAttr',
  components: { RepairAttrModal },
  data() {
    return {
      isCheckRow: false,
      isCheckRows: false,
      tableData: [],
      queryParam: {
        pageNo: 1,
        pageSize: 10,
        searchText: '',
        attributeType: null
      },
      page: {
        totalResult: 1
      }
    }
  },
  mounted() {
    this.findList()
  },
  methods: {
    findList() {
      pageRepairAttribute(this.queryParam).then((res) => {
        if (res.success) {
          this.page.totalResult = res.result.total
          this.tableData = res.result.records
        } else {
          this.$message.error('获取数据失败')
        }
      })
      this.isCheckRow = false
      this.isCheckRows = false
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleAdd() {
      this.$refs.repairAttrModal.add()
    },
    handleEdit() {
      let record = this.$refs.listTable.getCheckboxRecords()[0]
      this.$refs.repairAttrModal.edit(record)
    },
    handleDel() {
      let records = this.$refs.listTable.getCheckboxRecords()
      this.$confirm({
        content: `是否删除选中${records.length}条数据？`,
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          let delList = records.map((item) => {
            return item.id
          })
          let formData = new FormData()
          formData.append('ids', delList.join(','))
          deleteRepairAttribute(formData).then((res) => {
            if (res.success) {
              this.$message.success('删除成功')
              this.findList()
            } else {
              this.$message.error(res.message)
            }
          })
        }
      })
    },
    // 用户选择记录触发
    rangeChange({ records }) {
      this.isCheckRow = records.length === 1
      this.isCheckRows = records.length > 0
    }
  }
}
</script>
