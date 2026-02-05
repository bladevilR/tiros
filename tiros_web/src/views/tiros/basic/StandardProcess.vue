<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="工序名称">
              <a-input placeholder="请输入工序名称" v-model="queryParam.name" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="类型">
              <a-select
                placeholder="请选择工序类型"
                v-model="queryParam.type"
                allowClear
              >
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="拆卸">拆卸</a-select-option>
                <a-select-option value="检修">检修</a-select-option>
                <a-select-option value="安装">安装</a-select-option>
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
        <vxe-table-column field="name" title="工序名称" width="20%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="type" title="工序类型" width="15%" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="duration" title="标准耗时(小时)" width="15%" header-align="center" align="right"></vxe-table-column>
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

export default {
  data () {
    return {
      selectRows: [],
      queryParam: {
        name: '',
        type: '',
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
    this.$message.info('标准工序功能待完善，后端接口开发中')
  },
  methods: {
    checkboxChange (e) {
      this.selectRows = e.records
    },
    loadData () {
      this.$message.warning('后端接口待实现')
    },
    handleSearch () {
      this.loadData()
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.loadData()
    },
    handleAdd () {
      this.$message.warning('功能开发中')
    },
    handleEdit (record) {
      this.$message.warning('功能开发中')
    },
    handleDelete () {
      this.$message.warning('功能开发中')
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
