<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="12">
            <a-form-item label="合同">
              <a-input placeholder="输入编码或者名称" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="12">
            <a-form-item label="线路">
              <line-select-list :trigger-change="true" v-model="queryParam.lineId"> </line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="12">
            <a-form-item label="厂商" class="a-form-item-width-gy">
              <a-input placeholder="输入厂商名称" v-model="queryParam.supplier"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="12">
            <a-form-item label="时间">
              <a-range-picker format="YYYY-MM-DD" :placeholder="['开始时间', '结束时间']" @change="onDateChange" :defaultPickerValue="defaultDateRange"  />
            </a-form-item>
          </a-col>
          <a-col :md="2" :sm="24"> <a-button @click="findList">查询</a-button> </a-col>
          <a-col :md="24" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button @click="handleEdit" :disabled="!btnStatus.edit">编辑</a-button>
                <a-button @click="deleteRecord" :disabled="!btnStatus.del">删除</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 258px)">
      <vxe-table
        border
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 258px)"
        :data="tableData"
        :align="allAlign"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        @checkbox-change="rowSelectChange"
        @checkbox-all="btnStatus.update()"
      >
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column field="contractNo" header-align="center" title="合同编号" width="150"></vxe-table-column>
        <vxe-table-column field="contractName" header-align="center" title="合同名称" width="220"></vxe-table-column>
        <vxe-table-column field="supplierName" header-align="center" title="培训厂商" width="150"></vxe-table-column>
        <vxe-table-column
          field="title"
          align="left"
          header-align="center"
          title="培训标题"
          width="220"
        ></vxe-table-column>
        <vxe-table-column field="startDate" title="培训开始日期" width="150"></vxe-table-column>
        <vxe-table-column field="days" header-align="center" title="培训天数" width="100"></vxe-table-column>
        <vxe-table-column field="manTimes" header-align="center" title="培训人次" width="100"></vxe-table-column>
        <vxe-table-column
          field="trainingUsers"
          align="left"
          header-align="center"
          title="培训名单"
          width="200"
        ></vxe-table-column>
        <vxe-table-column field="remark" align="left" header-align="center" title="备注" width="200"></vxe-table-column>
        <vxe-table-column field="fujian" align="left" header-align="center" title="附件" width="200">
          <template v-slot="{ row }">
            <div v-if="row.trainingAnnexList">
              <span v-for="(item, index) in row.trainingAnnexList" :key="index">
                <a style="font-size: 12px" href="javascript:;" @click.stop="handleSeeing(item)">{{ item.name }}</a>
                <i v-if="index < row.trainingAnnexList.length - 1">，</i>
              </span>
            </div>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <vxe-pager
      perfect
      :current-page.sync="queryParam.pageNo"
      :page-size.sync="queryParam.pageSize"
      :total="totalResult"
      :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
      @page-change="handlePageChange"
    ></vxe-pager>
    <TrainingRecordItemModel ref="modalForm" @ok="loadData()"></TrainingRecordItemModel>
    <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
  </div>
</template>


<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { getTrainingRecordList, delTrainingRecordItem } from '@api/tirosOutsourceApi'
import TrainingRecordItemModel from '../modules/TrainingRecordItemModel'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import { isPrivilege } from '@/api/tirosApi'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'List',
  components: { LineSelectList, TrainingRecordItemModel, DocPreviewModal },
  data() {
    return {
      btnStatus: new TableBtnUtil(this, 'listTable', {}),
      queryParam: {
        endTime: '',
        startTime: '',
        searchText: '',
        lineId: '',
        supplier: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      tableHeight: {
        top: 'height: calc(100vh - 280px)',
        bottom: 'height: calc(100vh - 225px)',
        size: '100%',
      },
      filePath: '',
      fileName: '',
      status: false,
    }
  },

  created() {
    this.findList()
  },
  methods: {
    rowSelectChange() {
      this.btnStatus.update()
    },
    onDateChange(value, dateString) {
      this.queryParam.startTime = dateString[0]
      this.queryParam.endTime = dateString[1]
    },
    findList() {
      getTrainingRecordList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
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
    handleEdit() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        this.$refs.modalForm.edit(selectRecords[0])
        this.$refs.modalForm.title = '编辑'
      } else {
        this.$message.error('请选中一项数据!')
      }
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
            delTrainingRecordItem('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    async handlePrivilege(id, operation) {
      let param = { id: id, operation: operation }
      await isPrivilege(param).then((res) => {
        this.status = res.result
      })
    },
    async handleSeeing(data) {
      await this.handlePrivilege(data.id, 2)
      if (!this.status) {
        this.$message.error('您没有权限!')
      } else {
        this.fileName = data.name
        // this.filePath = window._CONFIG['onlinePreviewDomainURL'] + '?url=' + encodeURIComponent(data.savepath)
        this.$refs.docPreview.handleFilePath(data.savepath)
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