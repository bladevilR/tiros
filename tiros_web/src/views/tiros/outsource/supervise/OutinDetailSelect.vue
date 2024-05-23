<template>
  <a-modal
    width="80%"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
    style="max-height: 100%"
  >
    <a-card :bordered="false">
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="findList">
          <a-row :gutter="24">
            <a-col :md="6" :sm="24">
              <a-form-item label="交接">
                <a-input placeholder="请输入部件名或者编号" v-model="queryParam.searchText"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="所属线路">
                <!--                <j-dict-select-tag
                  v-model="queryParam.lineId"
                  placeholder="请选择"
                  dictCode="bu_mtr_line,line_name,line_id"
                />-->
                <line-select-list v-model="queryParam.lineId"></line-select-list>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="车辆">
                <j-dict-select-seach-tag
                  v-model="queryParam.trainId"
                  placeholder="请选择"
                  dictCode=" bu_train_info,train_no,id"
                />
              </a-form-item>
            </a-col>
            <a-col :md="3" :sm="8">
              <a-button @click="findList">查询</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <div style="height: calc(100vh - 380px)">
        <vxe-table
          border
          ref="listTable"
          style="height: calc(100vh - 400px)"
          max-height="90%"
          :align="allAlign"
          :data="tableData"
          show-overflow="tooltip"
          :radio-config="{ trigger: 'row' }"
          :edit-config="{ trigger: 'row', mode: 'cell', showIcon: 'true' }"
        >
          <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
          <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
          <vxe-table-column field="lineName" title="线路" width="150"></vxe-table-column>
          <vxe-table-column field="trainNo" title="车号" width="100"></vxe-table-column>
          <vxe-table-column field="outinNo" title="单号" width="150"></vxe-table-column>
          <vxe-table-column field="assetName" title="委外部件"></vxe-table-column>
          <vxe-table-column field="assetNo" title="部件编号" width="150"></vxe-table-column>
          <vxe-table-column field="contractNo" title="合同编号" width="200"></vxe-table-column>
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
    </a-card>
  </a-modal>
</template>

<script>
import { getOutinDetailList } from '@api/tirosOutsourceApi'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
export default {
  name: 'OutinDetailSelect',
  components: { LineSelectList },
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
    billType: {
      type: Number,
      default: null,
    },
  },
  data() {
    return {
      queryParam: {
        searchText: '',
        lineId: '',
        trainId: '',
        pageNo: 1,
        pageSize: 10,
      },
      allAlign: 'center',
      totalResult: 0,
      tableData: [],
      visible: false,
      confirmLoading: false,
    }
  },
  methods: {
    showModal() {
      this.visible = true
      this.findList()
    },
    findList() {
      this.loading = true
      this.queryParam.billType = this.billType
      getOutinDetailList(this.queryParam).then((res) => {
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
    },
  },
}
</script>

<style>
</style>