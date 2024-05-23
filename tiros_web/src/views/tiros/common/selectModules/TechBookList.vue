<template>
  <a-modal
    width="80%"
    title="作业指导书选择"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    centered
    :destroyOnClose="true"
    :bodyStyle="{ height: '80vh' }"
  >
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="请输入指导书名称" v-model="queryParam.formName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="修程类型">
              <j-dict-select-tag
                v-model="queryParam.repairProgramId"
                placeholder="请选择"
                dictCode="bu_repair_program,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="所属线路">
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(80vh - 130px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(80vh - 130px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        highlight-current-row
        show-overflow="tooltip"
        :radio-config="!multiple ? { trigger: 'row' } : {}"
        :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true } : {}"
        :edit-config="{ trigger: 'click', mode: 'cell', showIcon: 'true' }"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
        <vxe-table-column field="fileNo" title="文件编号" width="180"></vxe-table-column>
        <vxe-table-column field="fileName" title="文件名称" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="fileVer" title="文件版本" width="140"></vxe-table-column>
        <vxe-table-column field="repairProgramName" title="所属修程" width="140"></vxe-table-column>
        <vxe-table-column field="lineName" title="所属线路" width="140"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="有效状态" width="80"></vxe-table-column>
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
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

import { getSopPage } from '@api/tirosApi'
export default {
  name: 'TechBookList',
  components: { LineSelectList },
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      queryParam: {
        formName: '',
        lineId: '',
        repairProgramId: '',
        pageNo: 1,
        pageSize: 10,
      },
      allAlign: 'center',
      totalResult: 0,
      tableData: [],
      visible: false,
      confirmLoading: false,
      formItemLayout: {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 18 },
        },
      },
    }
  },
  methods: {
    showModal() {
      this.visible = true
      this.findList()
    },
    findList() {
      this.loading = true
      getSopPage(this.queryParam).then((res) => {
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
      this.close()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
      this.queryParam.formName = ''
    },
  },
}
</script>
<style scoped>
.limitTitle {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
  border-bottom: 1px solid #e1e1de;
}
.limitHeight {
  height: calc(80vh - 110px);
  overflow-y: auto;
}
</style>