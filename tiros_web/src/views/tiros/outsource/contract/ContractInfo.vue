<template>
  <div class="bodyWrapper">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="合同">
              <a-input placeholder="合同编码或名称" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="厂商" class="a-form-item-width-gy">
              <a-select
                v-model="supplierName"
                placeholder="请选择厂商"
                :open="false"
                :showArrow="true"
                @focus="openSupplierModal"
                ref="mySupplierSelect"
              >
                <div slot="suffixIcon">
                  <a-icon
                    v-if="queryParam.supplierId"
                    @click.stop="clearValue"
                    type="close-circle"
                    style="padding-right: 3px"
                  />
                  <a-icon v-else type="ellipsis" />
                </div>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag v-model="queryParam.status" dictCode="bu_contract_status" />
            </a-form-item>
          </a-col>
          <a-col :md="2" :sm="24">
            <a-button @click="findList">查询</a-button>
          </a-col>
          <a-col :md="24" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button @click="handleEdit" :disabled="!btnStatus.edit">编辑</a-button>
                <a-button @click="deleteRecord" :disabled="!btnStatus.del">删除</a-button>
                <a-button @click="handlePay" :disabled="!btnStatus.pay">支付管理</a-button>
                <a-button @click="viewPay" :disabled="!btnStatus.del">价格查看</a-button>
                <a-button @click="handleDoc" :disabled="!btnStatus.file">文档资料</a-button>
                <a-button @click="handleRate" :disabled="!btnStatus.score">评分管理</a-button>
              </a-space>
              <!--            <na-buttons style="width: 100%">

              <a-button @click="deleteRecord">删除</a-button>
              <a-button @click="reset">重置</a-button>
              <a-button @click="handlePay">支付管理</a-button>
&lt;!&ndash;              <a-button @click="handleBak">暂列金管理</a-button>&ndash;&gt;
              &lt;!&ndash;              <a-button>导入</a-button>&ndash;&gt;
            </na-buttons>-->
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 258px)">
      <vxe-table
        border
        auto-resize
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 258px)"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        @checkbox-change="rowSelectChange"
        @checkbox-all="btnStatus.update()"
      >
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column field="lineName" title="所属线路" width="8%" fixed="left"></vxe-table-column>
        <vxe-table-column
          field="contractNo"
          title="合同编号"
          width="12%"
          fixed="left"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="contractName"
          title="合同名称"
          width="12%"
          fixed="left"
          align="left"
          header-align="center"
        >
          <template v-slot="{ row }">
            <a @click.stop="contractInfoDetail(row)">{{ row.contractName }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="supplierName"
          title="委外厂商"
          width="16%"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column field="amount" title="合同总价" width="8%" header-align="center" align="right">
          <template v-slot="{ row }">
            <span>{{ row.amount + '万' }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="taxRate" title="合同税率" width="8%" header-align="center" align="right">
          <template v-slot="{ row }">
            <span>{{ row.taxRate + '%' }}</span>
          </template>
        </vxe-table-column>
        <!--        <vxe-table-column field="behindMoney" title="暂列金" width="8%" header-align="center" align="right">
          <template v-slot="{ row }">
            <span>{{ row.behindMoney + '万' }}</span>
          </template>
        </vxe-table-column>-->
        <vxe-table-column field="deposit" title="履约保证金" width="8%" header-align="center" align="right">
          <template v-slot="{ row }">
            <span>{{ row.deposit + '万' }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="expiration" align="right" header-align="center" title="质保期限" width="8%">
          <template v-slot="{ row }">
            <span>{{ row.expiration + '个月' }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="needDay"
          title="维修周期"
          width="8%"
          header-align="center"
          align="right"
        ></vxe-table-column>
        <vxe-table-column field="status_dictText" title="合同状态" width="8%"></vxe-table-column>
        <!-- <vxe-table-column field="progress" title="项目进度" width="12%"></vxe-table-column> -->
        <vxe-table-column
          field="finishAmount"
          align="right"
          header-align="center"
          title="第几列车"
          width="12%"
        ></vxe-table-column>
        <vxe-table-column field="curTrain" title="执行车号" width="12%"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" width="12%" align="left" header-align="center"></vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
      <contract-info-add-modal ref="modalForm" @ok="loadData()"></contract-info-add-modal>
      <contract-pay-modal ref="payModal" @ok="loadData()"></contract-pay-modal>
      <contract-bak-modal ref="bakModal" @ok="loadData()"></contract-bak-modal>
      <contract-info-detail ref="detailModal" @ok="loadData()"></contract-info-detail>
      <contract-view-pay-modal ref="viewPayModal"></contract-view-pay-modal>
      <supplier-list ref="supplierModal" :multiple="false" @ok="supplierSelect"></supplier-list>
      <contract-perform-detail ref="docModal"></contract-perform-detail>
      <perform-rate ref="rateModal"></perform-rate>
    </div>
  </div>
</template>

<script>
import { delContractInfo, getContractInfoList } from '@/api/tirosOutsourceApi'
import ContractInfoAddModal from '@views/tiros/outsource/contract/ContractInfoAdd'
import ContractPayModal from '@views/tiros/outsource/contract/ContractPay'
import ContractBakModal from '@views/tiros/outsource/contract/ContractBak'
import ContractViewPayModal from '@views/tiros/outsource/contract/ContractViewPay'
import ContractInfoDetail from '@views/tiros/outsource/contract/ContractInfoDetail'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import SupplierList from '@views/tiros/common/selectModules/SupplierList'
import { everythingIsEmpty } from '@/utils/util'
import ContractPerformDetail from '@views/tiros/outsource/contract/ContractPerformDetail'
import PerformRate from '@views/tiros/outsource/perform/PerformRate'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'ContractInfo',
  components: {
    ContractInfoAddModal,
    ContractPayModal,
    ContractBakModal,
    ContractViewPayModal,
    ContractInfoDetail,
    LineSelectList,
    SupplierList,
    ContractPerformDetail,
    PerformRate,
  },
  data() {
    return {
      btnStatus: new TableBtnUtil(this, 'listTable', {
        attrs: [
          {
            key: 'pay',
          },
          {
            key: 'payView',
          },
          {
            key: 'file',
          },
          {
            key: 'score',
          },
        ],
      }),
      tableData: [],
      allAlign: 'center',
      totalResult: 0,
      supplierName: '',
      queryParam: {
        searchText: '',
        lineId: '',
        supplierId: '',
        status: null,
        pageNo: 1,
        pageSize: 10,
      },
    }
  },
  created() {
    this.findList()
  },
  methods: {
    rowSelectChange() {
      this.btnStatus.update()
    },
    clearValue() {
      this.queryParam.supplierId = ''
      this.supplierName = ''
    },
    openSupplierModal() {
      this.$refs.supplierModal.showModal()
      this.$refs.mySupplierSelect.blur()
    },
    supplierSelect(data) {
      if (!everythingIsEmpty(data)) {
        this.supplierName = data[0].name
        this.queryParam.supplierId = data[0].id
      }
    },
    loadData() {
      this.findList()
    },
    findList() {
      this.loading = true
      getContractInfoList(this.queryParam).then((res) => {
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
    handleEdit() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        this.$refs.modalForm.edit(selectRecords[0])
        this.$refs.modalForm.title = '编辑'
      } else {
        this.$message.warn('请选择一项数据！')
      }
    },
    contractInfoDetail(record) {
      this.$refs.detailModal.show(record)
    },
    handlePay() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length === 1) {
        let id = selectRecords[0].id
        let amount = selectRecords[0].amount
        this.$refs.payModal.show({
          contractId: id,
          amount: amount,
          executeTaxRate: selectRecords[0].executeTaxRate,
          contractNo: selectRecords[0].contractNo,
        })
      } else if (selectRecords.length > 1) {
        this.$message.error('只能选中一条数据!')
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    viewPay() {
      // 价格查看
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length) {
        let idsStr = selectRecords
          .map(function (obj, index) {
            return obj.id
          })
          .join(',')
        this.$refs.viewPayModal.show({ ids: idsStr })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handleBak() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length === 1) {
        let id = selectRecords[0].id
        let behindMoney = selectRecords[0].behindMoney
        this.$refs.bakModal.show({ contractId: id, behindMoney: behindMoney })
      } else if (selectRecords.length > 1) {
        this.$message.error('只能选中一条数据!')
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delContractInfo('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handleDoc() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length === 1) {
        let id = selectRecords[0].id
        let supplierId = selectRecords[0].supplierId
        this.$refs.docModal.show(id, supplierId)
      } else if (selectRecords.length > 1) {
        this.$message.error('只能选中一条数据!')
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handleRate() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length === 1) {
        let id = selectRecords[0].id
        let supplierId = selectRecords[0].supplierId
        this.$refs.rateModal.show(id, supplierId)
      } else if (selectRecords.length > 1) {
        this.$message.error('只能选中一条数据!')
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
  },
}
</script>

<style lang="less" scope>
.a-form-item-width-gy .ant-col.ant-form-item-control-wrapper {
  width: calc(100% - 50px);
}
</style>