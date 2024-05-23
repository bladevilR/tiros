<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="角色">
              <j-dict-select-tag
                :triggerChange="true"
                v-model="queryParam.roleId"
                v-decorator.trim="['roleId']"
                dictCode="sys_role,role_name,id"
                placeholder="选择角色"
              />
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="24">
            <a-form-item label="人员">
              <a-input placeholder="请输入人员姓名或工号" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button :disabled="records.length != 1" @click="handleEdit(records[0])">编辑</a-button>
                <a-button @click="handleSkillCompare">技能项对比</a-button>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div>
      <vxe-table
        border
        max-height="86%"
        style="height: calc(100vh - 290px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="workNo" title="工号" width="100"></vxe-table-column>
        <vxe-table-column field="username" title="账号" width="100"></vxe-table-column>
        <vxe-table-column field="realname" align="left" header-align="center" title="姓名" width="100">
          <template v-slot="{ row }">
            <a @click.stop="handleDetail(row)">{{ row.realname }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="groupName" align="left" header-align="center" title="班组" width="120"></vxe-table-column>
        <vxe-table-column field="roleNames" align="left" header-align="center" title="角色" min-width="180"></vxe-table-column>
        <!--        <vxe-table-column field="positionName" title="岗位" width="120"></vxe-table-column>-->
        <vxe-table-column field="sex_dictText" title="性别" width="60"></vxe-table-column>
        <vxe-table-column field="phone" title="手机" width="120"></vxe-table-column>
        <vxe-table-column field="telephone" title="固定电话" width="120" />
        <vxe-table-column
          field="tags"
          title="技能标签"
          align="left"
          header-align="center"
          min-width="160"
        ></vxe-table-column>
        <vxe-table-column
          field="certs"
          title="证书情况"
          align="left"
          header-align="center"
          min-width="160"
        ></vxe-table-column>
      </vxe-table>
    </div>
    <vxe-pager
      perfect
      :current-page.sync="queryParam.currentPage"
      :page-size.sync="queryParam.pageSize"
      :total="totalResult"
      :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
      @page-change="handlePageChange"
    ></vxe-pager>
    <staff-info ref="staffInfoModel" @ok="findList()"></staff-info>
    <radar-skill ref="radarSkill"></radar-skill>
  </div>
</template>

<script>
import StaffInfo from '../../basic/modules/orgStaff/StaffInfo'
import { pageUser } from '@api/tirosApi'
import Vue from 'vue'
import { USER_INFO } from '@/store/mutation-types'
import RadarSkill from '@views/tiros/group/information/RadarSkill'

export default {
  name: 'PersonInfoPage',
  components: { StaffInfo, RadarSkill },
  data() {
    return {
      records:[],
      queryParam: {
        roleId: '',
        searchText: '',
        assetCode: '',
        status: '',
        lineId: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      userInfo: Vue.ls.get(USER_INFO),
    }
  },
  created() {
    this.findList()
  },
  methods: {
    checkboxChange(e){
      this.records = e.records;
    },
    handleSkillCompare() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let idsStr = selectRecords.map((obj) => obj.id).join(',')
        this.$refs.radarSkill.show(idsStr)
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    findList() {
      this.queryParam.groupId = this.userInfo.departId
      pageUser(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.records = []
        this.tableData = res.result.records
      })
    },
    searchReset() {},
    selectAllEvent({ checked, records }) {
      console.log(checked ? '所有勾选事件' : '所有取消事件', records)
    },
    selectChangeEvent({ checked, records }) {
      console.log(checked ? '勾选事件' : '取消事件', records)
    },
    getSelectEvent() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      this.$XModal.alert(selectRecords.length)
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            this.records = []
            this.tableData = this.tableData.filter((item) => {
              let idList = selectRecords.map((s) => s.id)
              console.log(idList, item.id, idList.includes(item.id))
              return !idList.includes(item.id)
            })
            console.log(this.tableData)
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    // handleAdd() {
    //   this.$refs.staffInfoModel.add()
    // },
    handleEdit(record) {
      this.$refs.staffInfoModel.edit(record)
      this.$refs.staffInfoModel.title = '编辑'
    },
    handleDetail(row) {
      this.$refs.staffInfoModel.edit(row)
      this.$refs.staffInfoModel.title = '详情'
      this.$refs.staffInfoModel.isDisable = true
    },
  },
}
</script>

<style scoped>
</style>