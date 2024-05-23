<template>
  <div>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="标题">
              <a-input placeholder="请输入标题" v-model="queryParam.title" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="日期">
              <a-date-picker v-model="date" />
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button @click="handleEdit" :disabled="!btnStatus.edit">编辑</a-button>
                <a-button @click="deleteRecord" :disabled="!btnStatus.del">删除</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 225px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(100vh - 225px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        @checkbox-change="rowSelectChange"
        @checkbox-all="btnStatus.update()"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column title="安全预想标题" width="200" align="left" header-align="center">
          <template v-slot="{ row }">
            <a @click.stop="jumpInfo(row)">{{ row.title }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="readUserCount"
          align="left"
          header-align="center"
          title="已读人数"
          width="100"
        ></vxe-table-column>
        <vxe-table-column field="readDate" title="预想日期" width="150"></vxe-table-column>
        <vxe-table-column
          field="createUserName"
          align="left"
          header-align="center"
          title="创建人员"
          width="120"
        ></vxe-table-column>
        <vxe-table-column
          field="readUserListStr"
          align="left"
          header-align="center"
          title="已读人员列表"
        ></vxe-table-column>
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

    <safe-anticipate-modal ref="modalForm" @ok="loadData()"></safe-anticipate-modal>
    <safe-anticipate-info ref="safeInfo" @ok="loadData()"></safe-anticipate-info>
  </div>
</template>

<script>
import moment from 'moment'
import SafeAnticipateModal from '../modules/SafeAnticipateModal'
import { addSafeassumeRead, delSafeassume, getSafeassumeDetail, getSafeassumeList } from '@api/tirosGroupApi'
import SafeAnticipateInfo from '@views/tiros/group/safeanticipate/SafeAnticipateInfo'
import Vue from 'vue'
import { USER_INFO } from '@/store/mutation-types'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'SafeAnticipatePage',
  components: { SafeAnticipateModal, SafeAnticipateInfo },
  data() {
    return {
      btnStatus: new TableBtnUtil(this, 'listTable', {}),
      date: null,
      queryParam: {
        title: '',
        readDate: null,
        groupId: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      count: '',
    }
  },
  created() {
    this.findList()
  },
  methods: {
    findList() {
      this.dataSource = []
      this.loading = true
      if (this.date) {
        this.queryParam.readDate = moment(this.date).format('YYYY-MM-DD')
      } else {
        this.queryParam.readDate = ''
      }

      const userInfo = Vue.ls.get(USER_INFO)
      this.queryParam.groupId = userInfo.departId
      getSafeassumeList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
        this.btnStatus.update()
      })
    },
    rowSelectChange() {
      this.btnStatus.update()
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData() {
      this.findList()
    },
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit() {
      // console.log(record)
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length == 1) {
        this.$refs.modalForm.edit(selectRecords[0])
        this.$refs.modalForm.title = '编辑'
      } else {
        this.$message.warn('请选择一项数据！')
      }
    },

    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delSafeassume('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    jumpInfo(row) {
      const userInfo = Vue.ls.get(USER_INFO)
      let data = { safeAssumeId: row.id, userId: userInfo.id, readTime: moment().format('YYYY-MM-DD HH:mm:ss') }
      addSafeassumeRead(data).then((res) => {
        getSafeassumeDetail({ id: row.id }).then((res) => {
          this.$refs.safeInfo.show(res.result)
        })
      })
    },
  },
}
</script>

<style scoped>
</style>