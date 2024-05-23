<template>
  <!-- 角色选择弹窗 -->
  <a-modal
    width="90%"
    title="角色选择"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
    :bodyStyle="{height:'60vh'}"
  >
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="findList">
          <a-row :gutter="24">
            <a-col :md="7" :sm="24">
              <a-form-item label="角色名称">
                <a-input placeholder="请输入角色名称" v-model="queryParam.roleName"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="3" :sm="8">
              <a-button @click="findList">查询</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
    <div class="limitHeight">
      <vxe-table
        border
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="10%"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="10%"></vxe-table-column>
        <vxe-table-column field="roleCode" title="角色编码" width="30%"></vxe-table-column>
        <vxe-table-column field="roleName" title="角色名称" width="30%"></vxe-table-column>
        <vxe-table-column field="createTime" title="创建时间" width="30%"></vxe-table-column>
      </vxe-table>
    </div>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
  </a-modal>
</template>

<script>
import { getRoleList } from '@/api/tirosApi'
export default {
  name: 'RoleList',
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      queryParam: {
        roleName: '',
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
      getRoleList(this.queryParam).then((res) => {
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

<style scoped>
.limitTitle {
  font-size: 20px;
  font-weight: 600;
  height: 40px;
}
.limitHeight {
  height: calc(60vh - 170px);
  overflow-y: auto;
}
</style>