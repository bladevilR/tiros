<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="任务名称">
              <a-input placeholder="请输入内容" v-model="queryParam.name" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="线路">
              <!--              <j-dict-select-tag
                v-model="queryParam.lineId"
                placeholder="请选择"
                dictCode="bu_mtr_line,line_name,line_id"
              />-->
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="审批状态">
              <j-dict-select-tag v-model="queryParam.status" placeholder="请选择" dictCode="bu_supervise_status" />
            </a-form-item>
          </a-col>
          <a-col :md="2" :sm="8">
            <a-button @click="findList">查询</a-button>
          </a-col>
          <a-col :md="24" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd">申请</a-button>
                <a-button @click="handleEdit" :disabled="!btnStatus.edit">编辑</a-button>
                <a-button @click="deleteRecord" :disabled="!btnStatus.delJudge">删除</a-button>
                <a-button @click="uploadData" :disabled="!btnStatus.upload">上传资料</a-button>
                <ProcessButtons
                  ref="bts"
                  v-if="selectedRow"
                  @StartSuccess="onStartSuccess"
                  @StartFailure="onStartFailure"
                  @handleSuccess="onHandleSuccess"
                  @cancelSuccess="refreshList"
                  :solution-code="'WF_OUT_SUPERVISE'"
                  :business-key="selectedRow.id"
                  :business-title="selectedRow.planName"
                  :process-instance-id="selectedRow.processInstanceId"
                  :variables="variables"
                ></ProcessButtons>
              </a-space>
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
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="taskContent" title="任务名称" min-width="120" header-align="center" align="left">
          <template v-slot="{ row }">
            <a @click.stop="handleDetail(row)">{{ row.taskContent }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="lineName" title="所属线路" width="100"></vxe-table-column>
        <vxe-table-column
          field="dispatchGroupName"
          align="left"
          header-align="center"
          title="派遣工班"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="dispatchUserName"
          align="left"
          header-align="center"
          title="派遣人员"
          width="100"
        ></vxe-table-column>
        <vxe-table-column field="userPhone" title="联系方式" min-width="120"></vxe-table-column>
        <vxe-table-column
          field="supplierName"
          title="派往公司"
          min-width="140"
          header-align="center"
          align="left"
        ></vxe-table-column>
        <vxe-table-column field="supplierPhone" title="联系方式" min-width="120"></vxe-table-column>
        <vxe-table-column
          field="supplierAddress"
          title="联系地址"
          min-width="140"
          header-align="center"
          align="left"
        ></vxe-table-column>
        <vxe-table-column field="dispatchDate" title="出派时间" width="100"></vxe-table-column>
        <vxe-table-column field="returnDate" title="返回时间" width="100"></vxe-table-column>
        <vxe-table-column
          field="contractNo"
          title="所属合同"
          min-width="120"
          header-align="center"
          align="left"
        >
          <template v-slot="{ row }">
            <a @click.stop="contractInfoDetail(row)">{{ row.contractNo }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="assetTypeName" title="设备项目" min-width="120"></vxe-table-column>
        <vxe-table-column field="createTime" title="申请时间" width="100"></vxe-table-column>
        <vxe-table-column
          field="wfStatus"
          title="当前审批"
          align="center"
          header-align="center"
          width="8%"
        ></vxe-table-column>
        <vxe-table-column
          field="processCandidate"
          title="当前处理人"
          align="left"
          header-align="center"
          width="160"
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
      <supervise-item-module ref="modalForm" @ok="loadData()"></supervise-item-module>
      <supervise-upload-module ref="modalUpload" @ok="loadData()"></supervise-upload-module>
      <supervise-item-detail-modal ref="itemDetail"></supervise-item-detail-modal>
      <contract-info-detail ref="detailModal" @ok="loadData()"></contract-info-detail>
    </div>
  </div>
</template>

<script>
import {
  delSupervise,
  delSupplier,
  getContractInfoList,
  getSuperviseList,
  getSupplierList
} from '@api/tirosOutsourceApi'
import SuperviseItemModule from '../modules/SuperviseItemModule'
import SuperviseUploadModule from '../modules/SupplierUploadModule'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import ProcessButtons from '@views/workflow/ProcessButtons'
import SuperviseItemDetailModal from '@views/tiros/outsource/modules/SuperviseItemDetailModal'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'
import ContractInfoDetail from '@views/tiros/outsource/contract/ContractInfoDetail'

export default {
  name: 'Supervise',
  components: {
    SuperviseItemModule,
    SuperviseUploadModule,
    LineSelectList,
    ProcessButtons,
    SuperviseItemDetailModal,
    ContractInfoDetail
  },
  data () {
    return {
      btnStatus: new TableBtnUtil(this, 'listTable', {
        attrs: [
          {
            key: 'edit',
            judge: (e) => e.wfStatus === '未发起'
          },
          {
            key: 'upload',
            judge: (e) => e.wfStatus !== '已结束'
          },
          {
            key: 'delJudge',
            isDel: true,
            judge: (e) => e.wfStatus === '未发起'
          }
        ]
      }),
      queryParam: {
        name: '',
        lineId: '',
        status: null,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      selectedRow: null,
      variables: {},
      wfStatus: false//是否审批结束
    }
  },
  created () {
    this.findList()
  },
  methods: {
    handleDetail (record) {
      this.$refs.itemDetail.show(record)
    },
    findList () {
      this.selectedRow = null
      getSuperviseList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
        this.btnStatus.update()
      })
    },
    rowSelectChange () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords && selectRecords.length == 1) {
        this.selectedRow = selectRecords[0]
        this.wfStatus = this.selectedRow.wfStatus === '已结束'
      } else {
        if (selectRecords) {
          selectRecords.map(item => {
            if (item.wfStatus === '已结束') this.wfStatus = true
          })
        }
        this.selectedRow = null
      }
      this.btnStatus.update()
    },
    deleteRecord () {
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
            delSupervise('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    uploadData () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.length === 1) {
          this.$refs.modalUpload.handleAdd(selectRecords[0])
        } else {
          this.$message.error('只能选择一条数据!')
        }
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },

    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData () {
      this.findList()
      this.$emit('load')
    },
    handleAdd () {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        this.$refs.modalForm.edit(selectRecords[0])
        this.$refs.modalForm.title = '编辑'
      } else {
        this.$message.warn('请选择一项数据！')
      }
    },
    onStartSuccess (data) {
      this.refreshList()
    },
    onStartFailure (data) {
      console.log('启动失败:', data)
    },
    onHandleSuccess (data) {
      this.refreshList()
    },
    refreshList () {
      this.findList()
    },
    contractInfoDetail (record) {
      let queryParam = {
          id: record.contractId,
          pageNo: 1,
          pageSize: 10
        }
      getContractInfoList(queryParam).then((res) => {
        let data=res.result.records;
        if(data.length>0) {
          this.$refs.detailModal.show(data[0])
        }else{
          this.$refs.detailModal.show(record)
        }
      })
    }
  }
}
</script>

<style scoped>
</style>