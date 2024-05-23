<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="车辆">
              <a-input placeholder="请输入车号" v-model="queryParam.trainNo" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="修程">
              <j-dict-select-tag
                v-model="queryParam.programId"
                placeholder="请选择"
                dictCode="bu_repair_program,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="交车日期">
              <a-date-picker class="datePaddingRight" v-model="date" />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择"
                dictCode="bu_repair_exchange_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="2" :sm="8">
            <a-button @click="findList">查询</a-button>
          </a-col>
          <a-col :md="24" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button @click="handleEdit" :disabled="!btnStatus.edit">编辑</a-button>
                <a-button @click="deleteRecord" :disabled="!btnStatus.del">删除</a-button>
                <a-button v-has="'receiv:train:editremark'" @click='handleEditRemark' :disabled='!(selectedRow && selectedRow.id)'>修改备注</a-button>
                <ProcessButtons
                  ref="wfButtons"
                  v-if="selectedRow && 0 === selectedRow.historyData"
                  v-has="'trans:train:workflow'"
                  @StartSuccess="onStartSuccess"
                  @StartFailure="onStartFailure"
                  @handleSuccess="onHandleSuccess"
                  @cancelSuccess="refreshList"
                  :solution-code="'WF_TRANSFER_TRAIN'"
                  :business-key="selectedRow.id"
                  :business-title="selectedRow.lineName + selectedRow.trainNo + '交车'"
                  :process-instance-id="selectedRow.processInstanceId"
                  :variables="variables"
                  :title="selectedRow.wfStatus"
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
        <vxe-table-column field="lineName" title="线路" width="80"></vxe-table-column>
        <vxe-table-column field="trainNo" title="车辆" width="100">
          <template v-slot="{ row }">
            <a @click.stop="openView(row)">{{ row.trainNo }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="trainIndex"
          align="right"
          header-align="center"
          title="总列次"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="itemNo"
          align="right"
          header-align="center"
          title="修程次数"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="programName"
          align="left"
          header-align="center"
          title="修程"
          width="80"
        ></vxe-table-column>
        <vxe-table-column
          field="sendDeptName"
          align="left"
          header-align="center"
          title="交付部门"
          min-width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="sendUserName"
          align="left"
          header-align="center"
          title="交付人"
          width="100"
        ></vxe-table-column>
        <!--        <vxe-table-column field="acceptDeptName" title="接修部门" min-width="100"></vxe-table-column>-->
        <!--        <vxe-table-column field="acceptUserName" title="接修人" width="100"></vxe-table-column>-->
        <vxe-table-column field="planFinishDate" title="计划交车日期" width="140"></vxe-table-column>
        <vxe-table-column field="sendDate" title="交车日期" width="100"></vxe-table-column>
        <vxe-table-column
          field="wfStatus"
          align="left"
          header-align="center"
          title="当前审批"
          width="80"
        ></vxe-table-column>
        <vxe-table-column field="processCandidate" title="当前处理人" align="left" header-align="center" width="160">
        </vxe-table-column>
        <vxe-table-column
          field="status_dictText"
          align="left"
          header-align="center"
          title="状态"
          width="80"
        ></vxe-table-column>
        <vxe-table-column
          field="remark"
          title="备注"
          min-width="120"
          align="left"
          header-align="center"
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
    <deliver-car-modal ref="deliverCarModal" :isReadonly="false" @ok="loadData()"></deliver-car-modal>
    <deliver-car-modal ref="deliverCarViewModal" :isReadonly="true"></deliver-car-modal>
    <a-modal
      :title='getEditExchangeRemarkTitle'
      :visible='editExchangeRemarkVisible'
      @ok='editExchangeRemark'
      @cancel='editExchangeRemarkVisible = false'
      cancelText='关闭'
      :destroyOnClose='true'
    >
      <div style='padding: 18px'>
        <a-form-model ref='exchangeRemarkForm' :model='exchangeRemarkObj'>
          <a-form-model-item :labelCol='{ span: 2 }' :wrapperCol='{ span: 22 }' label='备注' prop='remark'>
            <a-textarea v-model='exchangeRemarkObj.remark' :auto-size='{ minRows: 3, maxRows: 6 }'
                        placeholder='请输入备注信息'>
            </a-textarea>
          </a-form-model-item>
        </a-form-model>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { getExchangeList, delExchange, editExchangeRemark } from '@/api/tirosDispatchApi'
import moment from 'moment'
import DeliverCarModal from '@views/tiros/dispatch/delivercar/DeliverCarModal'
import ProcessButtons from '@views/workflow/ProcessButtons'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'DeliverCarPage',
  components: { DeliverCarModal, ProcessButtons },
  data() {
    return {
      btnStatus: new TableBtnUtil(this, 'listTable', {
        attrs: [
          {
            key: 'edit',
            judge: (e) => e.wfStatus === '未发起',
          },
        ],
      }),
      selectedRow: null,
      variables: {},
      date: null,
      queryParam: {
        trainNo: '',
        programId: '',
        sendDate: null,
        status: '',
        tradeType: 1,
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      count: '',
      editExchangeRemarkVisible: false,
      exchangeRemarkObj: {
        id: '',
        remark: ''
      }
    }
  },
  created() {
    this.findList()
  },
  methods: {
    getEditExchangeRemarkTitle() {
      if (this.selectedRow) {
        return '修改交车备注：' + this.selectedRow.trainNo + ' ' + this.selectedRow.programName
      } else {
        return ''
      }
    },
    findList() {
      this.loading = true
      if (this.date) {
        this.queryParam.sendDate = moment(this.date).format('YYYY-MM-DD')
      } else {
        this.queryParam.sendDate = ''
      }

      getExchangeList(this.queryParam).then((res) => {
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
    },
    rowSelectChange() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords && selectRecords.length == 1) {
        this.selectedRow = selectRecords[0]
      } else {
        this.selectedRow = null
      }
      this.btnStatus.update()
    },
    handleAdd() {
      //this.$router.push({ path: `/tiros/dispatch/delivercar/modal` })
      this.$refs.deliverCarModal.add()
      this.$refs.deliverCarModal.title = '新增'
    },
    handleEdit() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        this.$refs.deliverCarModal.edit(selectRecords[0])
        this.$refs.deliverCarModal.title = '编辑'
      } else {
        this.$message.warn('请选择一项数据！')
      }
    },
    deleteRecord() {
      this.selectedRow = null
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (
          selectRecords.filter((r) => {
            return r.wfStatus != '未发起'
          }).length > 0
        ) {
          this.$message.warn('审批中或已审批的数据不能删除')
          return
        }

        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delExchange('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    // 流程启动成功回调
    onStartSuccess(data) {
      this.refreshList()
    },
    // 流程启动失败回调
    onStartFailure(data) {
      console.error('流程启动失败:', data)
    },
    // 流程处理成功
    onHandleSuccess(data) {
      this.refreshList()
    },
    refreshList() {
      this.findList()
      this.selectedRow = null
    },
    openView(record) {
      this.itemView = true
      this.$nextTick(() => {
        this.$refs.deliverCarViewModal.edit(record)
        this.$refs.deliverCarViewModal.title = '查看'
      })
    },
    handleEditRemark() {
      this.exchangeRemarkObj = {
        id: this.selectedRow.id,
        remark: this.selectedRow.remark
      }
      this.editExchangeRemarkVisible = true

      console.log('this.exchangeRemarkObj:', this.exchangeRemarkObj)
    },
    editExchangeRemark() {
      console.log('this.exchangeRemarkObj:', this.exchangeRemarkObj)
      editExchangeRemark(this.exchangeRemarkObj).then(res => {
        if (res.success) {
          this.$message.success(res.message)
          this.editExchangeRemarkVisible = false
        } else {
          this.$message.error(res.message)
        }

        this.refreshList()
      })
    }
  },
}
</script>

<style scoped>
</style>