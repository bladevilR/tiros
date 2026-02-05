<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
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
              >
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="未开始">未开始</a-select-option>
                <a-select-option value="进行中">进行中</a-select-option>
                <a-select-option value="已完成">已完成</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button :disabled="selectRows.length != 1" @click="handleEdit(selectRows[0])">编辑</a-button>
                <a-button :disabled="selectRows.length < 1" @click="handleDelete">删除</a-button>
                <a-button @click="handleSearch">查询</a-button>
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
        <vxe-table-column field="title" title="标题" width="20%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="status" title="状态" width="10%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="progress" title="进度" width="15%" header-align="center" align="center"></vxe-table-column>
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

    <a-empty v-if="tableData.length === 0 && !loading" description="后端接口待实现" style="margin-top: 100px;" />
  </div>
</template>

<script>
import moment from 'moment'
import 'moment/locale/zh-cn'
import { pageProductionNotice, deleteProductionNotice } from '@/api/tirosApi'

export default {
  data () {
    return {
      selectRows: [],
      queryParam: {
        noticeNo: '',
        status: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      loading: false
    }
  },
  created () {
    this.loadData()
  },
  methods: {
    checkboxChange (e) {
      this.selectRows = e.records
    },
    loadData () {
      this.loading = true
      pageProductionNotice(this.queryParam).then((res) => {
        if (res.success) {
          this.tableData = res.result.records || []
          this.totalResult = res.result.total || 0
        }
      }).finally(() => {
        this.loading = false
      })
    },
    handleSearch () {
      this.queryParam.pageNo = 1
      this.loadData()
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.loadData()
    },
    handleAdd () {
      this.$message.info('新增功能开发中，后续版本完善')
    },
    handleEdit (record) {
      this.$message.info('编辑功能开发中，后续版本完善')
    },
    handleDelete () {
      if (this.selectRows.length === 0) {
        this.$message.warning('请先选择要删除的记录')
        return
      }
      this.$confirm({
        title: '删除确认',
        content: '确认删除选中的生产通知单吗？',
        onOk: () => {
          const ids = this.selectRows.map(row => row.id).join(',')
          deleteProductionNotice({ ids }).then((res) => {
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
