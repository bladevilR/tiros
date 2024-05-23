<template>
  <!-- 工单选择弹窗 -->
  <a-modal
    width='80%'
    title='工单选择'
    :visible='visible'
    :confirmLoading='confirmLoading'
    @ok='handleOk'
    @cancel='handleCancel'
    cancelText='关闭'
    centered
    :bodyStyle="{ height: '70vh' }"
  >
    <div class='table-page-search-wrapper'>
      <a-form layout='inline' @keyup.enter.native='findList'>
        <a-row :gutter='24'>
          <a-col :md='7' :sm='24'>
            <a-form-item label='工单搜索'>
              <a-input placeholder='请输入工单名称或编码' v-model='queryParam.searchText'></a-input>
            </a-form-item>
          </a-col>
          <!--          <a-col :md="7" :sm="24">
                      <a-form-item label="工单日期">
                        <a-range-picker v-model="dateRange" @change="onDateChange" :defaultPickerValue="defaultDateRange"  />
                      </a-form-item>
                    </a-col>-->
          <a-col :md='5' :sm='24'>
            <a-form-item label='工单类型'>
              <j-dict-select-tag v-model='queryParam.orderType' placeholder='请选择工单类型' dictCode='bu_order_type' />
            </a-form-item>
          </a-col>
          <a-col :md='9' :sm='24'>
            <a-form-item label='工单状态'>
              <div class='autoWidth'>
                <j-multi-select-tag v-model='queryParam.status' placeholder='请选择工单状态' dictCode='bu_order_status' />
              </div>
            </a-form-item>
          </a-col>
          <a-col :md='3' :sm='24'>
            <a-form-item>
              <a-button type='primary' @click='findList'>查询</a-button>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style='height: calc(70vh - 172px)'>
      <vxe-table
        border
        ref='listTable'
        :align='allAlign'
        :data='tableData'
        max-height='100%'
        style='height: calc(70vh - 172px)'
        show-overflow='tooltip'
        :radio-config="!multiple ? {trigger: 'row', checkMethod: checkRadioMethod} : {}"
        :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true,checkMethod: checkRadioMethod } : {}"
        :edit-config="{trigger: 'manual', mode: 'row'}"

      >
        <vxe-table-column v-if='multiple' type='checkbox' width='40'></vxe-table-column>
        <vxe-table-column v-else type='radio' width='40'></vxe-table-column>
        <vxe-table-column field='trainNo' title='作业车号' width='100'></vxe-table-column>
        <vxe-table-column field='groupName' title='所属班组' width='100'></vxe-table-column>
        <vxe-table-column field='orderCode' title='工单编号' width='150'></vxe-table-column>
        <vxe-table-column field='orderName' title='工单名称' align='left' min-width='150'></vxe-table-column>
        <vxe-table-column field='startTime' title='计划开始' width='150'></vxe-table-column>
        <vxe-table-column field='finishTime' title='计划结束' width='150'></vxe-table-column>
        <vxe-table-column field='orderStatus_dictText' title='工单状态' width='150'></vxe-table-column>
        <vxe-table-column field='orderType_dictText' title='工单类型' width='150'></vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync='queryParam.pageNo'
        :page-size.sync='queryParam.pageSize'
        :total='totalResult'
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change='handlePageChange'
      ></vxe-pager>
    </div>
  </a-modal>
</template>

<script>
import JMultiSelectTag from '@/components/dict/JMultiSelectTag'
import { getWorkOrderList } from '@api/tirosGroupApi'
import moment from 'moment'

export default {
  name: 'WorkOrderSelect',
  components: { JMultiSelectTag },
  props: {
    multiple: {
      type: Boolean,
      default: false
    },
    workGroupId: {
      type: String,
      default: ''
    },
    planId: {
      type: String,
      default: ''
    },
    orderType: {
      type: Number,
      default: null
    },
    // 查询条件默认选中， 0 未下达 1 已下达(未接受) 2 已下达(已接受) 3 已提交(工班长) 4 已关闭(调度) 5 已暂停 6 已发料 7 已领料 8 填报中
    orderStatus: {
      type: Array,
      default: () => [3, 4, 8]
    },
    // 只能选择什么状态的工单  不传则都可以选中
    selectStatus: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      dateRange: [moment(new Date(), 'YYYY-MM-DD'), moment(new Date(), 'YYYY-MM-DD')],
      queryParam: {
        searchText: '',
        startDate: '',
        endDate: '',
        orderType: '',
        status: this.orderStatus.join(','),
        pageNo: 1,
        pageSize: 10,
        workGroupId: this.workGroupId,
        planId: this.planId
      },
      allAlign: 'center',
      totalResult: 0,
      tableData: [],
      visible: false,
      confirmLoading: false,
      stationData: []
    }
  },
  methods: {
    //
    checkRadioMethod({ row }) {
      if (this.selectStatus && this.selectStatus.length > 0) {
        if (this.selectStatus.indexOf(row.orderStatus) > -1) {
          return true
        } else {
          return false
        }
      } else {
        return true
      }
    },
    onDateChange() {
      if (this.dateRange[0]) {
        this.queryParam.startDate = this.dateRange[0].format('YYYY-MM-DD')
      } else {
        this.queryParam.startDate = null
      }
      if (this.dateRange[1]) {
        this.queryParam.endDate = this.dateRange[1].format('YYYY-MM-DD')
      } else {
        this.queryParam.endDate = null
      }
    },
    showModal() {
      this.visible = true
      console.log("this.workGroupId="+this.workGroupId)
      console.log("this.planId="+this.planId)
      this.queryParam.planId = this.planId
      this.queryParam.workGroupId = this.workGroupId
      this.queryParam.orderType = this.orderType
      console.log(this.queryParam)
      this.findList()
    },
    findList() {
      this.loading = true
      getWorkOrderList(this.queryParam).then((res) => {
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
    handleOk() {
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords()
      } else {
        if (this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      this.$emit('ok', selectRecords)
      this.visible = false
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    }
  }
}
</script>

<style scope>
.autoWidth {
  display: inline-block;
  min-width: 22.6% !important;
}
</style>