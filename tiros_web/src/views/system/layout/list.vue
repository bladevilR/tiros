<template>
<div>
  <!-- 查询区域 -->
  <div class="table-page-search-wrapper">
    <a-form layout="inline" @keyup.enter.native="findList">
      <a-row :gutter="24">
        <a-col :md="4" :sm="24">
          <a-form-item label="名称">
            <a-input placeholder="布局名称" v-model="queryParam.layoutName"></a-input>
          </a-form-item>
        </a-col>
        <a-col :md="3" :sm="24">
          <a-form-item label="状态">
            <j-dict-select-tag v-model="queryParam.status" dictCode="bu_valid_status" />
          </a-form-item>
        </a-col>
        <a-col :md="4" :sm="24">
          <a-form-item label="所属人员">
            <div @click="showUserModal()">
            <a-select ref="userSelect" v-model="queryParam.userName" placeholder="请选择人员" :open="false">
              <a-icon slot="suffixIcon" type="ellipsis" @click="showUserModal()"/>
            </a-select>
            </div>
          </a-form-item>
        </a-col>
        <a-col :md="10" :sm="8">
          <a-space>
            <a-button @click="findList">查询</a-button>
            <a-button type="primary" @click="handleAdd">新增</a-button>
            <!-- <a-button>修改</a-button> -->
            <a-button @click="deleteRecord">删除</a-button>
             <a-button @click="layoutSetting">布局设置</a-button>
          </a-space>
        </a-col>
      </a-row>
    </a-form>
  </div>
  <div style="height: calc(100vh - 225px)">
    <vxe-table
      border
      ref="listTable"
      align="center"
      :data="tableData"
      show-overflow="tooltip"
      max-height="100%"
      style="height: calc(100vh - 225px)"
      :checkbox-config="{trigger: 'row', highlight: true, range: true}"
      :edit-config="{ trigger: 'manual', mode: 'row' }"
    >
      <vxe-table-column type="checkbox" width="40"></vxe-table-column>
      <vxe-table-column field="layoutCode" title="布局编码" width="150" align="left"></vxe-table-column>
      <vxe-table-column field="layoutName" title="布局名称" min-width="200" align="left"></vxe-table-column>
      <vxe-table-column field="isMain_dictText" title="是否主面板" width="100"></vxe-table-column>
      <vxe-table-column field="layoutScope_dictText" title="使用范围" width="100"></vxe-table-column>
      <vxe-table-column field="byUserName" title="所属人员" width="150"></vxe-table-column>
      <vxe-table-column field="status_dictText" title="状态" width="100"></vxe-table-column>
      <vxe-table-column field="layoutDesc" title="描述" width="180" align="left"></vxe-table-column>
      <vxe-table-column title="操作" width="150">
        <template v-slot="{ row }">
          <a-space>
            <a-button type="link" @click.stop="handleEdit(row)">编辑</a-button>
          </a-space>
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
  <layout-edit ref="editModal" @ok="findList"></layout-edit>
  <layout-setting ref="settingModal"></layout-setting>
  <user-list ref="userSelectModal" :multiple="false" @ok="onUserSelect"></user-list>
</div>
</template>

<script>
import { getLayouts, deleteLayout } from '@api/tirosLayoutApi'
import UserList from '@views/tiros/common/selectModules/UserList'
import layoutSetting from '@views/system/layout/layoutSetting'
import layoutEdit from '@views/system/layout/edit'

export default {
  name: 'list',
  components: { UserList, layoutEdit, layoutSetting },
  data () {
    return {
      tableData: [],
      queryParam: {
        layoutName: null,
        status: '',
        userId: null,
        userName: null,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0
    }
  },
  mounted () {
    this.findList()
  },
  methods: {
    findList () {
      this.loading = true
      getLayouts(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
        }
      })
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleAdd () {
      this.$refs.editModal.showModal()
    },
    handleEdit (record) {
      this.$refs.editModal.showModal(record)
    },
    layoutSetting () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.length > 1) {
          this.$message.warning('一次只能对一条记录进行设置')
        }
        this.$refs.settingModal.showModal(selectRecords[0].id)
      } else {
        this.$message.warning('请选择要设置的记录')
      }
    },
    deleteRecord () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      let defaultBoards=selectRecords.filter(r=>{
        return r.layoutCode === 'default-dashboard'
      })
      if (defaultBoards.length > 0) {
        this.$message.warn("默认首页面板不能删除的哦!!!")
        return
      }
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords.map(function (obj, index) {
                return obj.id
              }).join(',')
            deleteLayout({ ids: idsStr }).then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                this.findList()
              } else {
                this.$message.warning(res.message)
              }
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    showUserModal () {
      this.$refs.userSelectModal.showModal()
    },
    onUserSelect (data) {
      if (data.length) {
        this.queryParam.userId = data[0].id
        this.queryParam.userName = data[0].realname
      } else {
        this.queryParam.userId = null
        this.queryParam.userName = null
      }
    }
  }
}
</script>

<style scoped>

</style>