<template>
  <div>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="7" :sm="24">
            <a-form-item label="工位名称">
              <a-input placeholder="请输入名称或编码" v-model="queryParam.name" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="24">
            <a-form-item label="位置">
              <a-input placeholder="请输入位置" v-model="queryParam.position" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
              <a-button @click="findList">查询</a-button>
              <a-button type="primary" @click="handleRelation">关联工位</a-button>
              <a-button type="primary" @click="handleRelieve">解除工位</a-button>
              </a-space>
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
        <vxe-table-column field="stationNo" title="工位号" width="100"></vxe-table-column>
        <vxe-table-column field="name" title="工位名称" width="180"></vxe-table-column>
        <vxe-table-column field="location" title="位置" width="200" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="content" title="作业内容" min-width="120"  header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" min-width="100" header-align="center" align="left"></vxe-table-column>

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
    <wook-info-modal ref="modalForm" @ok="loadData()" :datagroup="queryParam.groupId"></wook-info-modal>
  </div>
</template>

<script>
import { deleteWorkstation, pageRelatedWorkstation } from '@api/tirosGroupApi'
import WookInfoModal from '../modules/WookInfoModal'
import Vue from 'vue'
import { USER_INFO } from '@/store/mutation-types'

export default {
  name: 'WookInfo',
  components: { WookInfoModal },
  data () {
    return {
      queryParam: {
        name: '',
        position: '',
        groupId: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      count: ''
    }
  },
  created () {
    this.findList()
  },
  methods: {
    findList () {
      const userInfo = Vue.ls.get(USER_INFO)
      this.queryParam.groupId = userInfo.departId
      pageRelatedWorkstation(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.tableData = res.result.records
      })
    },
    handleRelation () {
      this.$refs.modalForm.showModal()
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleRelieve () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否解除选中${selectRecords.length}数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {

                return obj.id
              })
              .join(',')
            let param = {
              workstationId: idsStr,
              groupId: this.queryParam.groupId
            }
            deleteWorkstation(param).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    loadData () {
      this.findList()
    }
  }
}
</script>

<style scoped>

</style>