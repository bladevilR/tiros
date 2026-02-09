<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="handleSearch">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="BOM编码">
              <a-input placeholder="请输入BOM编码" v-model="queryParam.bomCode" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="车型">
              <a-select
                placeholder="请选择车型"
                v-model="queryParam.trainType"
                allowClear
              >
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="电客车">电客车</a-select-option>
                <a-select-option value="电动车组">电动车组</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <template v-if="advanced">
            <a-col :md="6" :sm="24">
              <a-form-item label="BOM名称">
                <a-input placeholder="请输入BOM名称" v-model="queryParam.bomName" allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="线别">
                <a-input placeholder="请输入线别" v-model="queryParam.line" allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="位置">
                <a-input placeholder="请输入位置" v-model="queryParam.position" allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="系统">
                <a-input placeholder="请输入系统" v-model="queryParam.system" allowClear></a-input>
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

    <a-row :gutter="12">
      <a-col :span="7">
        <a-card title="BOM结构树" :bordered="false" style="height: calc(100vh - 225px); overflow: auto;">
          <a-tree
            :treeData="treeData"
            :defaultExpandAll="true"
            :replaceFields="{ children: 'children', title: 'title', key: 'key' }"
            @select="onTreeSelect"
          />
        </a-card>
      </a-col>
      <a-col :span="17">
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
        <vxe-table-column field="bomCode" title="BOM编码" width="15%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="bomName" title="BOM名称" width="20%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="trainType" title="车型" width="15%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="line" title="线别" width="10%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="position" title="位置" width="10%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="system" title="系统" width="12%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="createTime" title="创建日期" width="18%" :formatter="formatDate"></vxe-table-column>
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
      </a-col>
    </a-row>

    <quota-bom-modal ref="quotaModal" @ok="loadData()"></quota-bom-modal>
  </div>
</template>

<script>
import moment from 'moment'
import 'moment/locale/zh-cn'
import QuotaBomModal from './modules/quotabom/QuotaBomModal'
import { pageQuotaBom, deleteQuotaBom, listQuotaBomTree } from '@/api/tirosApi'
import { downFile } from '@/api/manage'
import Vue from 'vue'
import { ACCESS_TOKEN } from '@/store/mutation-types'

export default {
  name: 'QuotaBom',
  components: { QuotaBomModal },
  computed: {
    importExcelUrl() {
      return `${window._CONFIG['domianURL']}/base/quota-bom/importExcel`
    }
  },
  data () {
    return {
      selectRows: [],
      advanced: false,
      tokenHeader: { 'X-Access-Token': Vue.ls.get(ACCESS_TOKEN) },
      queryParam: {
        bomCode: '',
        bomName: '',
        trainType: '',
        line: '',
        position: '',
        system: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      loading: false
      ,
      treeData: []
    }
  },
  created () {
    this.loadData()
    this.loadTree()
  },
  methods: {
    checkboxChange (e) {
      this.selectRows = e.records
    },
    loadData () {
      this.loading = true
      pageQuotaBom(this.queryParam).then((res) => {
        if (res.success) {
          this.tableData = res.result.records || []
          this.totalResult = res.result.total || 0
        }
      }).finally(() => {
        this.loading = false
      })
    },
    loadTree () {
      listQuotaBomTree({ trainType: this.queryParam.trainType }).then((res) => {
        if (res && res.success) {
          this.treeData = res.result || []
        }
      })
    },
    onTreeSelect (keys, e) {
      const node = e && e.node && e.node.dataRef
      if (node && node.level === 6 && node.bomId) {
        const target = (this.tableData || []).find(item => item.id === node.bomId)
        this.selectRows = target ? [target] : []
        if (target) {
          this.$message.info(`已定位到BOM：${target.bomCode}`)
        }
      }
    },
    handleSearch () {
      this.queryParam.pageNo = 1
      this.loadData()
      this.loadTree()
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.loadData()
    },
    handleAdd () {
      this.$refs.quotaModal.add()
    },
    handleEdit (record) {
      this.$refs.quotaModal.edit(record)
    },
    handleDelete () {
      if (this.selectRows.length === 0) {
        this.$message.warning('请先选择要删除的记录')
        return
      }
      this.$confirm({
        title: '删除确认',
        content: '确认删除选中的定额BOM吗？',
        onOk: () => {
          const ids = this.selectRows.map(row => row.id).join(',')
          deleteQuotaBom({ ids }).then((res) => {
            if (res.success) {
              this.$message.success('删除成功')
              this.selectRows = []
              this.loadData()
              this.loadTree()
            }
          })
        }
      })
    },
    handleExport() {
      const params = { ...this.queryParam }
      downFile('/base/quota-bom/exportXls', params).then((data) => {
        if (!data) {
          this.$message.warning('文件下载失败')
          return
        }
        const blob = new Blob([data], { type: 'application/vnd.ms-excel' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        link.setAttribute('download', '定额BOM.xls')
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
    formatDate (row) {
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
</style>
