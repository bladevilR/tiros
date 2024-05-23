<template>
  <div class="bodyWrapper" style="padding: 8px; display: flex; flex-direction: column">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24" type="flex">
          <a-col :md="6">
            <a-form-item label="搜索:">
              <a-input v-model="queryParam.searchText" placeholder="借用部门或借用人" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="借用类型:">
              <j-dict-select-tag v-model="queryParam.borrowType" dictCode="bu_borrow_type" placeholder="请选择" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="日期">
              <!-- <a-date-picker v-model="yearValue" @change="onDateChange" /> -->
              <a-range-picker v-model="dateTime" @change="onDateChange" style="width: 100%" :defaultPickerValue="defaultDateRange" />
            </a-form-item>
          </a-col>

          <a-col :md="4" :sm="24">
            <a-button @click="findList">查询</a-button>
          </a-col>
        </a-row>
        <a-row>
          <span class="table-page-search-submitButtons">
            <a-space>
              <a-button type="primary" @click="handleAdd">新增</a-button>
              <!--               <a-button @click="downloadExcel">导出台账</a-button>-->
              <a-button type="dashed" :disabled="!btnStatus.edit" @click="handleEdit">编辑</a-button>
              <a-button type="dashed" :disabled="!btnStatus.del" @click="handleDel">删除</a-button>

              <a-dropdown>
                <a-menu slot="overlay" @click="downloadExcel">
                  <a-menu-item key="1">
                    <div>借用台账导出</div>
                  </a-menu-item>
                  <a-menu-item key="2">
                    <div>借用单导出</div>
                  </a-menu-item>
                  <!--                  <a-menu-item key="3">-->
                  <!--                    <div>外单位物料借用单导出</div>-->
                  <!--                  </a-menu-item>-->
                </a-menu>
                <a-button>
                  物料借用导出
                  <a-icon type="down" />
                </a-button>
              </a-dropdown>
            </a-space>
          </span>
        </a-row>
      </a-form>
    </div>
    <div class="table-page-body-wrapper" style="height: calc(100vh - 222px)">
      <div class="table-body-context">
        <vxe-table
          border
          ref="listTable"
          height="100%"
          align="center"
          show-overflow="tooltip"
          :data="tableData"
          :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          @checkbox-change="rangeChange"
          @checkbox-all="rangeChange"
        >
          <vxe-table-column type="checkbox" width="60px" />
          <vxe-table-column field="borrowType_dictText" title="借用类型" />
          <vxe-table-column field="billCode" title="借用单号" />
          <vxe-table-column field="deptName" align="left" header-align="center" title="借用单位">
            <template v-slot="{ row }">
              {{ row.borrowType == 1 ? row.deptName : row.borrowDep }}
            </template>
          </vxe-table-column>
          <vxe-table-column field="borrowUserName" align="left" header-align="center" title="借用人员">
            <template v-slot="{ row }">
              {{ row.borrowType == 1 ? row.borrowUserName : row.borrowUser }}
            </template>
          </vxe-table-column>
          <vxe-table-column field="borrowDate" title="借用日期" />
          <vxe-table-column field="returnType_dictText" title="归还类型" />
          <vxe-table-column field="returnStatus_dictText" title="归还状态" />
          <vxe-table-column field="warehouseName" align="left" header-align="center" title="所属二级库" />
          <vxe-table-column field="remark" align="left" header-align="center" title="备注" />
        </vxe-table>
      </div>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="page.totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
    <BorrowModal ref="formModal" @ok="findList"></BorrowModal>
  </div>
</template>

<script>
import { downloadBorrow, delBorrow, getBorrow, borrowExport } from '@api/tirosMaterialApi'
import BorrowModal from '@views/tiros/material/borrow/BorrowModal'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'Borrow',
  components: { BorrowModal },
  data() {
    return {
      moreVisible: false,
      isCheckRow: false,
      isCheckRows: false,
      dictTrainStr: '',
      tableData: [],
      dateTime: [null, null],
      queryParam: {
        pageNo: 1,
        pageSize: 10,
        startDate: null,
        endDate: null,
        searchText: '',
        borrowType: null,
      },
      btnStatus: new TableBtnUtil(this, 'listTable'),
      page: {
        totalResult: 1,
      },
    }
  },
  mounted() {
    this.findList()
  },
  methods: {
    findList() {
      getBorrow(this.queryParam).then((res) => {
        if (res.success) {
          this.page.totalResult = res.result.total
          this.tableData = res.result.records
          this.btnStatus.update()
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
    onDateChange() {
      if (this.dateTime[0]) {
        this.queryParam.startDate = this.dateTime[0].format('YYYY-MM-DD')
      } else {
        this.queryParam.startDate = null
      }
      if (this.dateTime[1]) {
        this.queryParam.endDate = this.dateTime[1].format('YYYY-MM-DD')
      } else {
        this.queryParam.endDate = null
      }
    },
    // 用户选择记录触发
    rangeChange({ records }) {
      this.isCheckRow = records.length === 1
      this.isCheckRows = records.length > 0
      this.btnStatus.update()
    },
    handleAdd() {
      this.$refs.formModal.add()
      this.$refs.formModal.title = '新增'
    },
    handleEdit() {
      let record = this.$refs.listTable.getCheckboxRecords()[0]
      this.$refs.formModal.edit(record)
      this.$refs.formModal.title = '编辑'
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
          console.log(delList.join(','))
          let formData = new FormData()
          formData.append('ids', delList.join(','))
          delBorrow(formData).then((res) => {
            if (res.success) {
              this.$message.success('删除成功')
              this.findList()
            } else {
              this.$message.error(res.message)
            }
          })
        },
      })
    },
    downloadExcel(e) {
      if (e.key == 1) {
        downloadBorrow(`借用台账-${this.$moment(new Date()).format('YYYYMMDDhhmmss')}.xls`, {
          borrowType: this.queryParam.borrowType,
          searchText: this.queryParam.searchText,
          startDate: this.queryParam.startDate,
          endDate: this.queryParam.endDate,
        })
      } else if (e.key == 2) {
        if (
          this.$refs.listTable.getCheckboxRecords().length == 0 ||
          this.$refs.listTable.getCheckboxRecords().length >= 2
        ) {
          this.$message.error('请选择一条数据导出！')
        } else {
          let record = this.$refs.listTable.getCheckboxRecords()[0].id
          let borrowType = this.$refs.listTable.getCheckboxRecords()[0].borrowType
          let deptName = this.$refs.listTable.getCheckboxRecords()[0].borrowDep
          borrowExport(deptName + `借用单-${this.$moment(new Date()).format('YYYYMMDDhhmmss')}.xls`, {
            borrowType: borrowType,
            searchText: this.queryParam.searchText,
            startDate: this.queryParam.startDate,
            endDate: this.queryParam.endDate,
            id: record,
          })
        }
      }
    },
  },
}
</script>

<style lang="less" scoped>
.bodyWrapper {
  .table-page-body {
    &.more-visible {
      height: calc(100% - 106px) !important;
    }
  }
}
</style>