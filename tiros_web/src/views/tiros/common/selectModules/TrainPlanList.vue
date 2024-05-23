<template>
<!-- 列计划选择弹窗 -->
  <a-modal
    width="80%"
    title="列计划选择"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    centered
    :destroyOnClose="true"
    :bodyStyle="{ height: '70vh' }"
  >
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="7" :sm="24">
            <a-form-item label="计划名称">
              <a-input placeholder="请输入计划名称或列车序号" v-model="queryParam.searchText"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="线路">
              <j-dict-select-tag
                style="width:100%"
                :triggerChange="true"
                v-decorator="['lineId']"
                v-model="queryParam.lineId"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-space>
              <a-button @click='findList'>查询</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100% - 55px)">
    <vxe-table
      border
      ref="listTable"
      :align="allAlign"
      :data="tableData"
      max-height="100%"
      style="height: calc(100% - 55px)"
      show-overflow="tooltip"
      :radio-config="!multiple ? {trigger: 'row'} : {}"
      :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true } : {}"
      :edit-config="{trigger: 'manual', mode: 'row'}"
    >
      <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
      <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
      <vxe-table-column field="planName" title="计划名称" align="left" ></vxe-table-column>
      <vxe-table-column field="lineName" title="所属线路" align="left" ></vxe-table-column>
      <vxe-table-column field="trainNo" title="车号" align="left" ></vxe-table-column>
      <vxe-table-column field="startDate" title="开始日期" align="left" ></vxe-table-column>
      <vxe-table-column field="finishDate" title="完成日期" align="left" ></vxe-table-column>
      <vxe-table-column field="progressStatus_dictText" title="进度状态" align="left" ></vxe-table-column>
      <vxe-table-column field="wfStatus" title="审批状态" align="left" ></vxe-table-column>
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
  </a-modal>
</template>

<script>
import { getSerialList } from '@/api/tirosGroupApi'

export default {
  name: 'TrainPlanList',
  props: {
    multiple: {
      type: Boolean,
      default: false,
    },
    trainNo: {
      type: String,
      default: ''
    },
    progressStatusList:{
      type: Array,
      default(){
        return []
      },
    },
  },
  data() {
    return {
      queryParam: {
        searchText: '',
        lineId:'',
        trainNo: '',
        // 0 草稿 1 审批中 2 已审批
        status:'2',
        pageNo: 1,
        pageSize: 10,
      },
      allAlign: 'center',
      totalResult: 0,
      tableData: [],
      visible: false,
      confirmLoading: false,
      stationData: [],
    }
  },
  methods: {
    showModal() {
      this.visible = true
      this.findList()
    },
    findList() {
      this.loading = true
      if(this.progressStatusList.length){
        this.queryParam.progressStatusList = this.progressStatusList;
      }
      if (this.trainNo){
        this.queryParam.trainNo = this.trainNo
      }
      getSerialList(this.queryParam).then((res) => {
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
        if(this.$refs.listTable.getRadioRecord()) {
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
    },
  },
}
</script>

<style>
</style>