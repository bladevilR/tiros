<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="物资">
              <a-input placeholder="物资编号或名称" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="物资类别">
              <j-dict-select-tag v-model="queryParam.materialCategory" dictCode="bu_material_type" />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="是否确认">
              <j-dict-select-tag v-model="queryParam.confirm" dictCode="bu_state" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col>
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button @click="handleEdit" :disabled="!btnStatus.edit">修改</a-button>
                <a-button @click="deleteRecord" :disabled="!btnStatus.delJudge">删除</a-button>
<!--     暂时去掉吧，因为自动生成了入库记录           <a-button @click="$refs.importModal.show()">导入</a-button>-->
                <a-button @click="handlePutStock" :disabled="!btnStatus.edit">确认已入库</a-button>
                <a-button @click="viewPutStockDetail" :disabled="!btnStatus.look">查看入库信息</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div :style="tableHeight.top">
      <vxe-table
        border
        :align="allAlign"
        ref="listTable"
        :max-height="tableHeight.size"
        :style="tableHeight.bottom"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        @checkbox-change="btnStatus.update()"
        @checkbox-all="btnStatus.update()"
      >
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column
          field="materialTypeCode"
          title="物资编码"
          width="150"
          header-align="center"
          align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="materialTypeName"
          title="物资名称"
          width="200"
          header-align="center"
          align="left"
        ></vxe-table-column>
        <vxe-table-column
          field="materialTypeCategory1_dictText"
          title="物资类别"
          width="100"
          header-align="center"
          align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="entryNo"
          title="入库单号"
          min-width="150"
        ></vxe-table-column>
        <vxe-table-column field="entryDate" title="入库日期" width="120"></vxe-table-column>
        <vxe-table-column field="amount" align="right" header-align="center" title="入库数量" width="100"></vxe-table-column>
        <vxe-table-column field="materialTypeUnit" title="单位" width="80"></vxe-table-column>
        <vxe-table-column field="price" align="right" header-align="center" title="单价" width="100"></vxe-table-column>
        <vxe-table-column field="totalPrice" align="right" header-align="center" title="金额" width="120"></vxe-table-column>
        <vxe-table-column align="left" header-align="center"  field="selfWarehouseName2" title="二级库" width="100"></vxe-table-column>
        <vxe-table-column align="left" header-align="center"  field="selfWarehouseName" title="存放库位" width="120"></vxe-table-column>
        <vxe-table-column field="confirm_dictText" title="已确认？" width="100"></vxe-table-column>
        <vxe-table-column
          field="confirmUserName"
          title="确认人"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="materialTypeSpec"
          title="物资描述"
          min-width="220"
          header-align="center"
          align="left"
        ></vxe-table-column>
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
    <PutStocktemModal ref="modalForms" @ok="loadData()" />
    <PutStockViewModal ref="viewDetails" />
    <j-import-modal :sum="1" ref="importModal" url="/import/materialEntryOrder" @ok="loadData()"></j-import-modal>
  </div>
</template>


<script>
import { getPutStockList, delPutStockItem } from '@/api/tirosMaterialApi'
import PutStockItemModal from '../modules/PutStockItemModal'
import PutStocktemModal from '../modules/PutStocktemModal'
import PutStockViewModal from '../modules/PutStockViewModal'


import JImportModal from '@/components/jeecg/JImportModal'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'List',
  components: { PutStockItemModal,JImportModal,PutStocktemModal,PutStockViewModal },

  data() {
    return {
      component: null,
      queryParam: {
        confirm:'',
        entryClass: '',
        materialCategory: '',
        warehouseId: '',
        searchText: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      btnStatus: new TableBtnUtil(this, 'listTable',{
        attrs:[
          {
            key: 'edit',
            judge: (e) => {
              return e.confirm !== 1
            }
          },
          {
            key: 'look',
            judge: (e) => {
              return e.confirm == 1
            }
          },
          {
            key: 'delJudge',
            isDel: true,
            judge: (e) => {
              return e.confirm !== 1
            }
          }
        ]
      }),
      tableHeight: {
        top: 'height: calc(100vh - 267px)',
        bottom: 'height: calc(100vh - 267px)',
        size: '100%',
      },
    }
  },
  created() {
    this.findList()
  },
  methods: {
    viewPutStockDetail(){
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if(selectRecords && selectRecords.length){
        let records = selectRecords[0]
        this.$refs.viewDetails.showModal(records)
      }
    },
    handlePutStock () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length === 1) {
        let records = selectRecords[0]
        if(selectRecords[0].confirm===1){
          this.$message.warn('该物资已入库,不可重复确认入库!')
          return
        }
        this.$refs.modalForms.edit(records)
        this.$refs.modalForms.title = '确认入库'
      } else if ((selectRecords.length > 1)) {
        this.$message.error('只能选择一条数据!')
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    findList() {
      this.queryParam.lineId = this.lineId
      getPutStockList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
        this.btnStatus.update()
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData() {
      this.findList()
      this.$emit('load')
    },
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit(row) {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if(selectRecords[0].confirm===1){
        this.$message.warn('该物资已入库,不可修改!')
        return
      }
      if (row.id) {
        this.$refs.modalForm.edit(row)
        this.$refs.modalForm.title = '编辑'
      } else if (selectRecords.length == 1) {
        this.$refs.modalForm.edit(selectRecords[0])
        this.$refs.modalForm.title = '编辑'
      } else {
        this.$message.error('请选中一项数据!')
      }
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let record = selectRecords.filter((item)=> item.confirm === 1 )
        if(record.length>0){
          this.$message.warn('已确认入库的物资不能进行删除')
          return;
        }
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delPutStockItem('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
  },
}
</script>