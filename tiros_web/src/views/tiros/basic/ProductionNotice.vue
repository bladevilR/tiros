<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="handleSearch">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="通知单号">
              <a-input placeholder="请输入通知单号" v-model="queryParam.noticeNo" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="状态">
              <a-select
                placeholder="请选择状态"
                v-model="queryParam.status"
                allowClear
                @change="handleQueryChange"
              >
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="0">草稿</a-select-option>
                <a-select-option value="1">审核中</a-select-option>
                <a-select-option value="2">已发布</a-select-option>
                <a-select-option value="3">已关闭</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <template v-if="advanced">
            <a-col :md="6" :sm="24">
              <a-form-item label="标题">
                <a-input placeholder="请输入标题" v-model="queryParam.title" allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="类型">
                <a-select
                  placeholder="请选择类型"
                  v-model="queryParam.noticeType"
                  allowClear
                >
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="1">技术类</a-select-option>
                  <a-select-option value="2">变更类</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="车型">
                <a-input placeholder="请输入车型" v-model="queryParam.trainType" allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="线别">
                <a-input placeholder="请输入线别" v-model="queryParam.line" allowClear></a-input>
              </a-form-item>
            </a-col>
          </template>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleEdit(selectRows[0])">编辑</a-button>
                <a-button :disabled="selectRows.length < 1" @click="handleDelete">删除</a-button>
                <a-button @click="handleExport">导出</a-button>
                <a-upload
                  name="file"
                  :showUploadList="false"
                  :multiple="false"
                  :headers="tokenHeader"
                  :action="importExcelUrl"
                  @change="handleImportExcel"
                >
                  <a-button>导入</a-button>
                </a-upload>
                <a-button @click="handleSearch">查询</a-button>
                <a @click="advanced = !advanced">
                  {{ advanced ? '收起' : '展开' }}
                  <a-icon :type="advanced ? 'up' : 'down'" />
                </a>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div style="height: calc(100vh - 225px)">
      <vxe-table
        border
        auto-resize
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 225px)"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="noticeNo" title="通知单号" width="15%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="title" title="通知单标题" width="25%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="status" title="状态" width="12%" header-align="center" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.status === '0'">草稿</span>
            <span v-else-if="scope.row.status === '1'">审核中</span>
            <span v-else-if="scope.row.status === '2'">已发布</span>
            <span v-else-if="scope.row.status === '3'">已关闭</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="progress" title="进度" width="12%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="createTime" title="创建时间" width="18%" :formatter="formatDate"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" width="15%" header-align="center" align="left"></vxe-table-column>
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

    <production-notice-modal ref="noticeModal" @ok="loadData()"></production-notice-modal>
  </div>
</template>

<script>
import moment from 'moment'
import 'moment/locale/zh-cn'
import ProductionNoticeModal from './modules/productionnotice/ProductionNoticeModal'
import { pageProductionNotice, deleteProductionNotice } from '@/api/tirosApi'
import { downFile } from '@/api/manage'
import Vue from 'vue'
import { ACCESS_TOKEN } from '@/store/mutation-types'

export default {
  name: 'ProductionNotice',
  components: { ProductionNoticeModal },
  computed: {
    importExcelUrl() {
      return `${window._CONFIG['domianURL']}/base/production-notice/importExcel`
    }
  },
  data() {
    return {
      selectRows: [],
      advanced: false,
      tokenHeader: { 'X-Access-Token': Vue.ls.get(ACCESS_TOKEN) },
      queryParam: {
        noticeNo: '',
        status: '',
        title: '',
        noticeType: '',
        trainType: '',
        line: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      loading: false
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    checkboxChange(e) {
      this.selectRows = e.records
    },
    loadData() {
      this.loading = true
      pageProductionNotice(this.queryParam).then(res => {
        if (res.success) {
          this.tableData = res.result.records || []
          this.totalResult = res.result.total || 0
        }
      }).finally(() => {
        this.loading = false
      })
    },
    handleSearch() {
      this.queryParam.pageNo = 1
      this.loadData()
    },
    handleQueryChange() {
      this.handleSearch()
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.loadData()
    },
    handleAdd() {
      this.$refs.noticeModal.add()
    },
    handleEdit(record) {
      this.$refs.noticeModal.edit(record)
    },
    handleDelete() {
      if (this.selectRows.length === 0) {
        this.$message.warning('请先选择要删除的记录')
        return
      }
      this.$confirm({
        title: '删除确认',
        content: '确认删除选中的生产通知单吗？',
        onOk: () => {
          const ids = this.selectRows.map(row => row.id).join(',')
          deleteProductionNotice({ ids }).then(res => {
            if (res.success) {
              this.$message.success('删除成功')
              this.selectRows = []
              this.loadData()
            } else {
              this.$message.error(res.message || '删除失败')
            }
          })
        }
      })
    },
    handleExport() {
      const params = { ...this.queryParam }
      downFile('/base/production-notice/exportXls', params).then((data) => {
        if (!data) {
          this.$message.warning('文件下载失败')
          return
        }
        const blob = new Blob([data], { type: 'application/vnd.ms-excel' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        link.setAttribute('download', '生产通知单.xls')
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      })
    },
    handleImportExcel(info) {
      if (info.file.status === 'done') {
        if (info.file.response && info.file.response.success) {
          this.$message.success(info.file.response.message || `${info.file.name} 导入成功`)
          this.loadData()
        } else {
          this.$message.error(info.file.response ? info.file.response.message : '导入失败')
        }
      } else if (info.file.status === 'error') {
        this.$message.error('文件上传失败')
      }
    },
    formatDate(row) {
      if (row.createTime) {
        return moment(row.createTime).format('YYYY-MM-DD HH:mm:ss')
      }
      return ''
    }
  }
}
</script>

<style scoped>
.table-page-search-wrapper {
  padding: 12px;
  background-color: #f5f5f5;
  border-radius: 2px;
  margin-bottom: 12px;
}

.table-page-search-submitButtons {
  margin-left: 8px;
}
</style>
