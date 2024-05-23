<template>
  <div class="bodyWrapper">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="整改">
              <a-input placeholder="请输入名称或编码" v-model="queryParam.searchText"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag v-model="queryParam.status" placeholder="请选择" dictCode="bu_work_rectify_status" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="下发日期">
              <a-date-picker v-model="date" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="类型">
              <j-dict-select-tag
                v-model="queryParam.rectifyType"
                placeholder="请选择"
                dictCode="bu_work_rectify_type"
              />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-button @click="findList">查询</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-spin :spinning="loading">
      <div style="height: calc(100vh - 250px)">
        <vxe-table
          border
          max-height="90%"
          style="height: calc(100vh - 298px)"
          ref="listTable"
          :align="allAlign"
          :data="tableData"
          show-overflow="tooltip"
          :edit-config="{ trigger: 'manual', mode: 'row' }"
        >
          <vxe-table-column field="rectifyNo" title="编号" width="8%"></vxe-table-column>
          <vxe-table-column field="lineName" title="所属线路" width="8%"></vxe-table-column>
          <vxe-table-column field="trainNo" title="所属车辆" width="10%"></vxe-table-column>
          <vxe-table-column field="title" title="任务名称" align="left" header-align="center" width="15%">
          </vxe-table-column>
          <vxe-table-column field="rectifyType_dictText" title="整改类型" width="10%"></vxe-table-column>
          <vxe-table-column
            field="groupName"
            align="left"
            header-align="center"
            title="责任工班"
            width="10%"
          ></vxe-table-column>
          <!-- <vxe-table-column field="dutyUserName" title="责任人" width="8%"></vxe-table-column> -->

          <vxe-table-column field="sendDate" title="下达日期" width="8%"></vxe-table-column>
          <vxe-table-column field="finishDate" title="完成时间" width="8%"></vxe-table-column>
          <vxe-table-column field="status_dictText" title="整改状态" width="8%"></vxe-table-column>
          <vxe-table-column title="附件描述" width="15%" align="left" header-align="center">
            <template v-slot="{ row }">
              <a v-for="(item, index) in row.annexList" :key="index" @click.stop="handleSeeing(item)">{{
                item.title
              }}</a>
            </template>
          </vxe-table-column>
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
      <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
    </a-spin>
  </div>
</template>

<script>
import { getRectifyRecord } from '@api/tirosProductionApi'
import moment from 'moment'
import { isPrivilege } from '@/api/tirosApi'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'

export default {
  name: 'RectifyRecord',
  components: { DocPreviewModal },
  props: {
    trainNo: {
      type: String,
      default: '',
    },
    structureDetailId: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      date: null,
      queryParam: {
        sendDate: '',
        searchText: '',
        rectifyType: '',
        trainNo: this.trainNo,
        status: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      loading: false,
      filePath: '',
      fileName: '',
      status: false,
    }
  },
  watch: {
    trainNo: {
      immediate: true,
      handler(id) {
        this.queryParam.trainNo = id
        this.findList()
      },
    },
  },
  methods: {
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
        this.fileName = data.title
        this.$refs.docPreview.handleFilePath(data.address)
      }
    },
    findList() {
      this.loading = true
      if (this.date) {
        this.queryParam.sendDate = moment(this.date).format('YYYY-MM-DD')
      } else {
        this.queryParam.sendDate = ''
      }
      getRectifyRecord(this.queryParam)
        .then((res) => {
          this.totalResult = res.result.total
          this.tableData = res.result.records
        })
        .finally(() => (this.loading = false))
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
  },
}
</script>

<style scoped>
</style>