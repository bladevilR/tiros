<template>
  <!-- 人员选择弹窗 -->
  <a-modal
    width="85%"
    title="人员选择"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :bodyStyle="{ height: '70vh' }"
    :destroyOnClose="true"
  >
    <!-- <a-card :bordered="false"> -->
    <a-row>
      <a-col :md="4" :sm="24">
        <div style="height: calc(70vh - 20px); border-right: 1px solid #e3e1e1; margin-right: 15px">
          <a-tree
            :defaultSelectedKeys="[queryParam.depId]"
            :defaultExpandAll="true"
            v-if="treeData && treeData.length > 0"
            :tree-data="treeData"
            @select="onSelect"
            class="limitHeight"
          />
        </div>
      </a-col>
      <a-col :md="20" :sm="24">
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="findList">
            <a-row :gutter="24">
              <a-col :md="5" :sm="24">
                <a-form-item label="账号">
                  <a-input placeholder="请输入用户账号" v-model="queryParam.username"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="24">
                <a-form-item label="真实名字">
                  <a-input placeholder="请输入真实名字" v-model="queryParam.realname"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="手机号码">
                  <a-input placeholder="请输入手机号码" v-model="queryParam.phone"></a-input>
                </a-form-item>
              </a-col> 
              <a-col :md="6" :sm="24">
                <a-form-item label="角色">
                  <j-dict-select-tag
                    :triggerChange="true"
                    v-model="queryParam.roleId"
                    dictCode="sys_role,role_name,id"
                    placeholder="选择角色"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="2" :sm="8">
                <a-button @click="findList">查询</a-button>
              </a-col>
            </a-row>
          </a-form>
        </div>
        <div style="height: calc(70vh - 130px)">
          <vxe-table
            border
            ref="listTable"
            :align="allAlign"
            :data="tableData"
            max-height="100%"
            style="height: calc(70vh - 130px)"
            show-overflow="tooltip"
            :radio-config="!multiple ? { trigger: 'row' } : {}"
            :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true } : {}"
            :edit-config="{ trigger: 'manual', mode: 'row' }"
          >
            <vxe-table-column v-if="multiple" type="checkbox" width="5%"></vxe-table-column>
            <vxe-table-column v-else type="radio" width="5%"></vxe-table-column>
            <vxe-table-column
              field="workNo"
              title="工号"
              width="100"
              align="center"
            ></vxe-table-column>
            <vxe-table-column
              field="username"
              title="用户账号"
              width="150"
              align="left"
              header-align="center"
            ></vxe-table-column>
            <vxe-table-column
              field="realname"
              title="用户名字"
              width="100"
              align="left"
              header-align="center"
            ></vxe-table-column>
            <vxe-table-column
              field="roleNames"
              title="角色"
              min-width="180"
              align="left"
              header-align="center"
            ></vxe-table-column>
            <vxe-table-column field="sex_dictText" title="性别" width="80"></vxe-table-column>
            <vxe-table-column field="phone" title="手机号码" width="120"></vxe-table-column>
            <vxe-table-column
              field="orgCode"
              title="部门"
              width="120"
              align="left"
              header-align="center"
            ></vxe-table-column>
            <vxe-table-column field="status_dictText" title="状态" width="80"></vxe-table-column>
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
    <!-- </a-card> -->
  </a-modal>
</template>

<script>
import { getDepartTree, getUserList } from '@/api/tirosApi'
export default {
  name: 'UserList',
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
    depId: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      queryParam: {
        username: '',
        realname: '',
        phone: '',
        orgCode: '',
        roleId: undefined,
        pageNo: 1,
        pageSize: 10,
      },
      allAlign: 'center',
      totalResult: 0,
      tableData: [],
      visible: false,
      confirmLoading: false,
      treeData: [],
    }
  },
  watch: {
    depId(val) {
      this.$set(this.queryParam, 'depId', val || '')
    },
  },
  mounted(){
    this.$set(this.queryParam, 'depId', this.depId || '')
  },
  methods: {
    showModal() {
      this.visible = true
      this.findList()
      this.getTreeList()
    },
    getTreeList() {
      getDepartTree().then((res) => {
        if (res.success) {
          this.treeData = res.result
        }
      })
    },
    onSelect(selectedKeys, e) {
      console.log(selectedKeys,e)
      if (selectedKeys.length) {
        let nodeData = e.node.dataRef
        this.queryParam.depId = nodeData.id
        this.findList()
      } else {
        this.queryParam.orgCode = ''
        this.findList()
      }
    },
    findList() {
      this.loading = true
      getUserList(this.queryParam)
        .then((res) => {
          this.loading = false
          if (res.success) {
            this.totalResult = res.result.total
            this.tableData = res.result.records
          } else {
            this.$message.error('加载用户数据失败')
            console.error('加载用户数据失败:', res.message)
          }
        })
        .catch((err) => {
          this.$message.error('加载用户数据异常')
          console.error('加载用户数据异常:', err)
        })
        .finally(() => {})
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
      console.log(selectRecords)
      this.$emit('ok', selectRecords)
      this.visible = false
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false;
      Object.assign(this.$data,this.$options.data());
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
  height: calc(70vh - 60px);
  overflow-y: auto;
}
</style>