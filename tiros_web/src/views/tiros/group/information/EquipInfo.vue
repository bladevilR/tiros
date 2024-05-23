<template>
  <div>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="7" :sm="24">
            <a-form-item label="工装名称">
              <a-input placeholder="请输入名称或者编码" v-model="queryParam.searchText"></a-input>
            </a-form-item>
          </a-col>
<!--          <a-col :md="7" :sm="24">
            <a-form-item label="工装编码">
              <a-input placeholder="请输入工装编码" v-model="queryParam.code"></a-input>
            </a-form-item>
          </a-col>-->
          <a-col :md="7" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button @click="findList">查询</a-button>
              <a-button type="primary" @click="handleAdd" style="margin-left: 8px" v-has="'group:info:box:add'">添加工装</a-button>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 215px)">
      <vxe-table
        border
        max-height="86%"
        style="height: calc(100vh - 290px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="code" title="工装编码" width="200"></vxe-table-column>
        <vxe-table-column field="name" title="工装名称" width="200"></vxe-table-column>
        <vxe-table-column field="amount" title="数量" width="150" header-align="center" align="right"></vxe-table-column>
        <vxe-table-column field="model" title="规格" min-width="120" header-align="center" align="left"></vxe-table-column>
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
    <tools-list ref="tools" :multiple="true" :canSelectType="[5]" @ok="addTarget"></tools-list>
  </div>
</template>

<script>
import { addTools, getToolEquipmentList } from '@api/tirosGroupApi'
import Vue from 'vue'
import { USER_INFO } from '@/store/mutation-types'
import { everythingIsEmpty } from '@/utils/util'
import ToolsList from '@views/tiros/common/selectModules/ToolsList'

export default {
  name: 'EquipInfo',
  components: {ToolsList},
  data() {
    return {
      queryParam: {
        searchText: '',
        groupId: '',
        pageNo: 1,
        pageSize: 10
      },
      Group: '',
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      count: ''
    }
  },
  created() {
    this.findList()
  },
  methods: {
    findList() {
      this.loading = true
      const userInfo = Vue.ls.get(USER_INFO)
      this.queryParam.groupId = userInfo.departId
      getToolEquipmentList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
      })
    },
    handleAdd() {
      this.$refs.tools.title='工装选择'
      this.$refs.tools.showModal()
    },
    addTarget(data){
      if(!everythingIsEmpty(data)){
        const userInfo = Vue.ls.get(USER_INFO)
        let toolsId=[]
        data.forEach((item) => {
          toolsId.push(item.id)
        })
        let param={
          toolsId:toolsId,
          groupId: userInfo.departId
        }
        addTools(param).then((res)=>{
          if(res.success){
            this.findList()
            this.$message.success(res.message)
          }else {
            this.$message.error(res.message)
          }
        })
      }
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData() {
      this.findList()
    }
  }
}
</script>

<style scoped>

</style>