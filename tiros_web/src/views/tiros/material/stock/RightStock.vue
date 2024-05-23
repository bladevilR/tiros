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
              <j-dict-select-tag v-model="queryParam.materialType" placeholder="请选择" dictCode="bu_material_type" />
            </a-form-item>
          </a-col>

          <!--          <a-col :md="6" :sm="24">
            <a-form-item label="物资属性">
              <j-dict-select-tag
                v-model="queryParam.materialAttr"
                placeholder="请选择"
                dictCode="bu_material_attr"
              />
            </a-form-item>
          </a-col>-->
          <a-col :md="6" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-upload
                  style="margin-left: 8px"
                  name="file"
                  :multiple="false"
                  :customRequest="customRequest"
                  :showUploadList="false"
                >
                  <a-button :loading="loading" v-has="'storage:import4level'">导入4级库位</a-button>
                </a-upload>
               <a-button @click="handleStockAttr">批次属性设置</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 250px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(100vh - 250px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        header-align="center"
      >
        <vxe-table-column type="checkbox" width="40" align="center"></vxe-table-column>
        <vxe-table-column field="materialTypeCode" title="物资编码" width="120">
          <template v-slot="{ row }">
            <a @click.stop="stockDetail(row)">{{ row.materialTypeCode }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="materialTypeName" title="物资名称" min-width="140" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="warehouseName" title="库位" width="100" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="warehouseLevel" title="库位级别" width="80" :formatter="formatLevel"></vxe-table-column>
        <vxe-table-column field="tradeNo" title="所属批次" width="160"></vxe-table-column>
        <vxe-table-column field="amount" title="当前库存" width="80" header-align="center" align="right" ></vxe-table-column>
        <vxe-table-column field="usableAmount" title="可用库存" width="80" header-align="center" align="right" ></vxe-table-column>
        <vxe-table-column field="usedDetailInfo" title="占用详情" width="160" header-align="center" align="left" ></vxe-table-column>
        <vxe-table-column field="price" title="库存单价" width="80" header-align="center" align="right" ></vxe-table-column>
        <vxe-table-column field="materialTypePrice" title="物资单价" width="80" header-align="center" align="right" ></vxe-table-column>
        <vxe-table-column field="unit" title="单位" width="60"></vxe-table-column>
        <vxe-table-column field="materialType_dictText" title="物资类别" width="80"></vxe-table-column>
        <vxe-table-column field="materialAttr_dictText" title="物资属性" width="100"></vxe-table-column>
        <vxe-table-column field="spec" title="物料描述" min-width="180" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="canReplace" title="可替换物资" width="100"></vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
      <!-- <stock-item-modal ref="modalForm" @ok="loadData()"></stock-item-modal> -->
      <stock-item-modal2 ref="modalForm" @ok="loadData()"></stock-item-modal2>
      <stock-detail-modal ref="stockDetailForm"></stock-detail-modal>
    </div>
  </a-card>
</template>
<script>
import { pageStock,importStock } from '@api/tirosMaterialApi'
// import StockItemModal from '../modules/StocktemModal'
import StockItemModal2 from '../modules/StocktemModal2'
import StockDetailModal from '../modules/StockDetailModal'

export default {
  name: 'RightStock',
  components: { StockItemModal2, StockDetailModal },
  props: ['value'],
  data() {
    return {
      loading: false,
      selectRow: null,
      queryParam: {
        searchText: '',
        materialType: null,
        materialAttr: null,
        warehouseId: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      cardStyle: {
        padding: '10px',
        height: 'calc(100vh - 120px)',
      },
    }
  },
  created() {
    this.findList()
  },
  watch: {
    value: {
      immediate: true,
      handler(id) {
        this.queryParam.warehouseId = id
        this.tableData = []
        this.findList()
      },
    },
  },
  methods: {
    findList() {
      this.loading = true
      pageStock(this.queryParam).then((res) => {
        // console.log('res:', res)

        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
      })
    },
    formatLevel ({cellValue}) {
      if (cellValue) {
        return parseInt(cellValue) - 1
      }
      return ''

    },
    customRequest(data) {
      // 上传提交
      this.loading=true
      const formData = new FormData()
      formData.append('excelFile', data.file)
      this.saveFile(formData)
    },
    saveFile(formData) {
      importStock(formData).then((res) => {
        this.loading=false
        if (res.success) {
          this.$message.success(res.message)
          this.findList()
          this.$emit('load')
        } else {
          this.$message.error(res.message)
        }
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleStockAttr() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0 && selectRecords.length === 1) {
        const materialTypeId = selectRecords[0].materialTypeId
        const tradeNo = selectRecords[0].tradeNo
        this.$refs.modalForm.edit(materialTypeId,tradeNo)
        this.$refs.modalForm.title = '设置属性'
      } else if (selectRecords.length > 1) {
        this.$message.error('只能选择一条数据!')
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    loadData() {
      this.findList()
    },
    stockDetail(row) {
      // console.log(row)
      this.$refs.stockDetailForm.detail(row)
    },
    clearRadioRow() {
      this.selectRow = null
      this.$refs.listTable.clearRadioRow()
    },
    radioChange({ row }) {
      this.selectRow = row
    },
  },
}
</script>