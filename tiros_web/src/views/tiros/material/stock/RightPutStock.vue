<template>
  <a-card :body-style="cardStyle">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="物资">
              <a-input placeholder="请输入物资编码或者名称" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="物资类别">
              <j-dict-select-tag
                v-model="queryParam.materialCategory"
                placeholder="请选择"
                dictCode="bu_material_type"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="是否新增">
              <j-dict-select-tag
                v-model="queryParam.entryClass"
                placeholder="请选择"
                dictCode="bu_entryClass_type"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
              <a-button @click="findList">查询</a-button>
              <a-button type="primary" @click="handlePutStock">确认已入库</a-button>
                </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 250px)">
      <vxe-table
        border
        auto-resize
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 250px)"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type="checkbox" width="40" align="center"></vxe-table-column>
        <!-- <template v-slot:header>
           @cell-click="radioChange"
            :radio-config="{highlight: true}"
           <vxe-button type="text" @click="clearRadioRow" v-if="selectRow"><a-icon type="close" /></vxe-button>
         </template>
       </vxe-table-column>-->
        <vxe-table-column field="materialTypeCode" title="物资编码" width="12%"></vxe-table-column>
        <vxe-table-column field="materialTypeName" title="物资名称" width="12%"></vxe-table-column>
        <vxe-table-column field="selfWarehouseName" title="库位" width="10%"></vxe-table-column>
        <vxe-table-column field="materialTypeCategory1_dictText" title="类别" width="10%"></vxe-table-column>
        <vxe-table-column field="entryDate" title="入库日期" width="10%" align="right"
                          header-align="center"></vxe-table-column>
        <vxe-table-column field="entryNo" title="批次" width="10%" align="right" header-align="center"></vxe-table-column>
        <vxe-table-column field="materialTypeUnit" title="单位" width="8%"></vxe-table-column>
        <vxe-table-column field="price" title="入库单价" width="10%" align="right" header-align="center"></vxe-table-column>
        <vxe-table-column field="amount" title="入库数量" width="10%" align="right"
                          header-align="center"></vxe-table-column>
        <vxe-table-column field="entryClass_dictText" title="入库类别" width="8%"></vxe-table-column>
        <vxe-table-column field="confirm_dictText" title="确认入库？" width="10%"></vxe-table-column>
        <vxe-table-column field="materialTypeSpec" title="物料描述" width="20%"></vxe-table-column>

      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
    <put-stock-item-modal ref="modalForm" @ok="loadData()"></put-stock-item-modal>
  </a-card>
</template>
<script>
import { getPutStock } from '@api/tirosMaterialApi'
import PutStockItemModal from '../modules/PutStocktemModal'

export default {
  name: 'RightPutStock',
  components: { PutStockItemModal },
  props: ['value'],
  data () {
    return {
      selectRow: null,
      queryParam: {
        searchText: '',
        materialCategory: null,
        entryClass: null,
        warehouseId: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      cardStyle: {
        'padding': '10px',
        'height': 'calc(100vh - 120px)',
      }
    }
  },
  created () {
    this.findList()
  },
  watch: {
    value: {
      immediate: true,
      handler (id) {
        this.queryParam.warehouseId = id
        this.tableData = []
        this.findList()
      }
    }
  },
  methods: {
    findList () {
      this.loading = true
      getPutStock(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
      })
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handlePutStock () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length === 1) {
        let id = selectRecords[0].id
        if(selectRecords[0].confirm===1){
          this.$message.warn('该物资已入库,不可重复确认入库!')
          return
        }
        this.$refs.modalForm.edit(id)
        this.$refs.modalForm.title = '确认入库'
      } else if ((selectRecords.length > 1)) {
        this.$message.error('只能选择一条数据!')
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    loadData () {
      this.findList()
      this.$emit('selectTree')
    },
    clearRadioRow () {
      this.selectRow = null
      this.$refs.listTable.clearRadioRow()
    },
    radioChange ({ row }) {
      this.selectRow = row
    }
  }
}
</script>