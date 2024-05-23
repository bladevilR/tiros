<template>
  <div>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="预警">
              <a-input placeholder="预警描述" v-model="queryParam.alertDescribe" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择"
                dictCode="bu_work_measure_alert_status"
              />
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="测量日期">
              <a-date-picker class="datePaddingRight" v-model="date" />
            </a-form-item>
          </a-col>
          <!--          <a-col :md="4" :sm="24">
            <a-form-item label="工位">
              <j-dict-select-tag
                v-model="queryParam.workstationId"
                placeholder="请选择"
                dictCode="bu_mtr_workstation,name,id"
              />
            </a-form-item>
          </a-col>-->
          <a-col :md="8" :sm="24">
            <a-space>
              <a-button @click="findList">查询</a-button>
              <a-button @click="handleClose(selectedRow)" :disabled="!selectedRow || selectedRow.status != 0"
                >修复关闭</a-button
              >
              <a-button @click="createFault(selectedRow)" :disabled="!selectedRow || selectedRow.status != 0"
                >转为故障</a-button
              >
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 225px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(100vh - 225px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        @checkbox-change="rowSelectChange"
      >
        <vxe-table-column type="checkbox" width="40" />
        <vxe-table-column title="表单查看" min-width="150" align="left" header-align="center">
          <template v-slot="{ row }">
            <a @click.stop="jumpInfo(row)">{{ row.formObjName }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="alertTime" title="测量时间" width="160" />
        <vxe-table-column field="groupName" title="作业工班" width="120" />
        <!--        <vxe-table-column
          field="workstationNames"
          title="作业工位"
          width="150"
          align="left"
          header-align="center"
        />-->
        <vxe-table-column field="taskName" title="作业工序" min-width="140" align="left" header-align="center" />
        <vxe-table-column field="status_dictText" title="状态" width="80" />
        <vxe-table-column field="alertValue" title="测量原始值" width="100" />
        <vxe-table-column field="thresholdValueDesc" title="阈值" width="120" />
        <vxe-table-column field="fixedValue" title="修复值" width="100" />
        <vxe-table-column field="fixDesc" align="left" header-align="center" title="备注" min-width="100" />
        <vxe-table-column field="alertMessage" title="预警描述" min-width="240" align="left" header-align="center" />
        <!--        <vxe-table-column title="操作" width="80" fixed="right">
          <template v-slot="{ row }">
            <a @click.stop="handleclose(row)" v-if="row.status === 0">修复关闭</a>
            <a @click.stop="createFault(row)" v-if="row.status===0">转为故障</a>
          </template>
        </vxe-table-column>-->
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
    <warning-off-modal ref="modalForm" @ok="loadData()"></warning-off-modal>
    <form-template ref="formTemplate"></form-template>
    <BreakdownModal ref="breakdownModal" @ok="loadData()"></BreakdownModal>
  </div>
</template>

<script>
import moment from 'moment'
import { getMeasurealertList } from '@/api/tirosGroupApi'
import WarningOffModal from '../modules/WarningOffModal'
import BreakdownModal from '@views/tiros/dispatch/breakdown/BreakdownModal'
import FormTemplate from '@views/tiros/common/form/FormTemplate'

export default {
  name: 'WarningOffPage',
  components: { WarningOffModal, FormTemplate, BreakdownModal },
  data() {
    return {
      date: null,
      queryParam: {
        alertDescribe: '',
        workstationId: '',
        status: '',
        alertTime: null,
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      count: '',
      selectedRow: null,
    }
  },
  created() {
    this.findList()
  },
  methods: {
    findList() {
      this.loading = true
      if (this.date) {
        this.queryParam.alertTime = moment(this.date).format('YYYY-MM-DD')
      } else {
        this.queryParam.alertTime = ''
      }
      getMeasurealertList(this.queryParam).then((res) => {
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
    },
    handleClose(record) {
      // console.log(record)
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '修复关闭'
    },
    createFault(record) {
      // 获取该值对应的设备
      this.$refs.breakdownModal.edit({
        workOrderId: record.orderId,
        orderTaskId: record.orderTaskId,
        formValueId: record.id,
        //TODO: 这里需要根据角色动态设置
        formType: 2,
        createType: 2, //  1 表示来自调度， 2 表示来自工班
        happenTime: record.alertTime,
        faultDesc: record.alertMessage,
        phase: 1,
      })
    },

    jumpInfo(row) {
      this.$refs.formTemplate.showModal2(row.formObjName,row.groupName,row.formDataRecordId,row.formObjId)
    },
    rowSelectChange() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords && selectRecords.length == 1) {
        this.selectedRow = selectRecords[0]
      } else {
        this.selectedRow = null
      }
    },
  },
}
</script>

<style scoped>
</style>