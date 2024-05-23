<template>
  <div style="padding: 5px">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="输入作业检查表名称" v-model="queryParam.name" allow-clear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="修程">
              <j-dict-select-tag @change="handleProgram" triggerChange v-model="queryParam.repairProId" dictCode="bu_repair_program,name,id" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="规程">
              <j-dict-select-tag
                triggerChange
                v-model="queryParam.reguId"
                :dictCode="dicStr"
                @focus="handleFocusRegu"
              />
            </a-form-item>
          </a-col>
          
          <a-col :md="6" :sm="24">
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col>
            <span class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button :disabled="records.length != 1" @click="handleEdit">编辑</a-button>
                <a-button :disabled="records.length < 1" @click="deleteRecord">删除</a-button>
                <a-button @click="importData">导入</a-button>
                <a-button :disabled="records.length != 1" @click="print">导出打印</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 272px)">
      <vxe-table
        border
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        max-height="100%"
        auto-resize
        style="height: calc(100vh - 272px)"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="title" title="检查表名称" align="left" header-align="center">
          <template v-slot="{row}">
            <div>
              <a @click.stop="handleSeeing(row)">{{row.title}}</a>
            </div>
          </template>
        </vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="160"></vxe-table-column>
        <vxe-table-column field="repairProName" title="修程" width="200"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="100"></vxe-table-column>
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
    <!--  -->
    <job-check-item-modal ref="modalForm" @ok="loadData"></job-check-item-modal>
    <JobCheckTableView ref="jobCheckTableView"></JobCheckTableView>
    <!-- <JobCheckTablePrint ref="tablePrintRef"></JobCheckTablePrint> -->
    <JobCheckTableExport ref="tablePrintRef"></JobCheckTableExport>
    <ImportCheck ref="importForm" @ok="loadData"></ImportCheck>
  </div>
</template>

<script>
import { getWorkcheck } from '@/api/tirosQualityApi'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import jobCheckItemModal from '../modules/jobCheckSheet/jobCheckItemModal'
import JobCheckTableView from '../modules/jobCheckSheet/JobCheckTableView'
import ImportCheck from '../modules/jobCheckSheet/components/ImportCheck'
// import JobCheckTablePrint from '../modules/jobCheckSheet/components/JobCheckTablePrint'
import JobCheckTableExport from '../modules/jobCheckSheet/components/JobCheckTableExport'
import { everythingIsEmpty } from '@/utils/util'

import { getWorkCheckList, delWorkCheckItem } from '@/api/tirosApi'
export default {
  components: { LineSelectList, jobCheckItemModal, JobCheckTableView, ImportCheck, JobCheckTableExport },
  name: 'List',
  data() {
    return {
      records:[],
      dicStr: '',
      queryParam: {
        name: '',
        repairProId: '',
        reguId: '',
        lineId: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
    }
  },
  created() {
    this.findList()
  },
  methods: {
    handleFocusRegu () {
      let programId = this.queryParam.repairProId
      if (everythingIsEmpty(programId)) this.$message.warn('请先选择修程类型')
    },
    handleProgram (data) {
      if (data) {
        this.dicStr = `bu_repair_regu_info,name,id,status=1 and repair_pro_id='${data}'`
      } else {
        this.queryParam.reguId = '';
        this.dicStr = ''
      }
    },
    checkboxChange(e){
      this.records = e.records;
    },
    openModal() {
      this.$refs.stationModalForm.showModal()
    },
    findList() {
      this.loading = true
      getWorkCheckList(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.records = [];
          this.tableData = res.result.records
        }
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
        this.$message.error('请选择一条数据进行编辑！')
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
          content: `是否删除选中${selectRecords.length}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delWorkCheckItem(`ids=${idsStr}`).then((res) => {
              if (res.success) {
                this.findList()
                this.$message.success(res.message)
              } else {
                this.$message.error(res.message)
              }
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    importData() {
      this.$refs.importForm.show()
    },
    loadData() {
      this.findList()
    },
    handleSeeing(row) {
      getWorkcheck({
        id: row.id,
      }).then((res) => {
        if (res.success && res.result) {
          let formData = res.result;
          this.$refs.jobCheckTableView.show(formData)
        } else {
          this.$message.error('加载记录数据异常')
          console.error('加载记录数据失败', res.message)
        }
      })
    },
    print(){
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      this.$refs.tablePrintRef.printById(selectRecords[0].id)
    }
  },
}
</script>

<style lang="less">
#recordSheetContent {
  border: none;
  .ant-card-body {
    padding: 0px 15px 0px 0px;
    overflow-y: hidden;
  }
  .tableHeight {
    height: calc(100vh - 230px);
    // overflow-y: auto;
  }
}
</style>