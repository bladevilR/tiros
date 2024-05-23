<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="工装名称">
              <a-input placeholder="请输入名称或编码" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button @click="handleNextCheck(btnStatus.editRow)" :disabled="!btnStatus.edit">设置下次送检时间</a-button>
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
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
        :edit-config="{trigger: 'manual', mode: 'row'}"
        @checkbox-change="btnStatus.update()"
        @checkbox-all="btnStatus.update()"
      >
        <!-- <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="code" title="装备编码" width="10%"></vxe-table-column>
        <vxe-table-column field="name" title="装备名称" width="10%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="assetCode" title="资产编号" width="10%"></vxe-table-column>
        <vxe-table-column field="lastCheckTime" title="上次送检日期" width="15%"></vxe-table-column>
        <vxe-table-column field="nextCheckTime" title="下次送检日期" width="15%"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" width="20%" header-align="center" align="left"></vxe-table-column> -->
        <!-- <vxe-table-column title="操作" width="15%" fixed="right">
          <template v-slot="{ row }">
            <a @click.stop="handleNextCheck(row)">设置下次送检时间</a>
          </template>
        </vxe-table-column> -->
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column title="装备名称" width="150" align="left" header-align="center">
          <template v-slot="{ row }">
            <a @click.stop="jumpInfo(row)">{{ row.name }}</a>
          </template>
        </vxe-table-column>

        <vxe-table-column
          field="code"
          title="物料编码"
          width="130"
          align="center"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="toolType_dictText"
          title="类别"
          width="100"
        ></vxe-table-column>
<!--        <vxe-table-column
          field="warehouseName"
          title="库位"
          width="120"
          align="left"
          header-align="center"
        ></vxe-table-column>-->
        <vxe-table-column
          field="entraceDate"
          title="入场日期"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="assetCode"
          title="资产编号"
          width="170"
        ></vxe-table-column>

        <vxe-table-column
          field="amount"
          title="数量"
          width="80"
          align="right"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="entryDate"
          title="入库日期"
          width="120"
        ></vxe-table-column>
        <vxe-table-column
          field="nextCheckTime"
          title="下次送检"
          width="120"
        ></vxe-table-column>
        <vxe-table-column field="isOverTime" title="是否逾期" width="100px">
          <template v-slot="{ row }">
            {{ getIsOverTimeStatus(row) }}
          </template>
        </vxe-table-column>
        <vxe-table-column field="isOverTimeDays" title="逾期天数" width="100">
          <template v-slot="{ row }">
            {{ getIsOverTimeDays(row) }}
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="isSelfCheck_dictText"
          title="是否自检?"
          width="90"
        ></vxe-table-column>
        <vxe-table-column
          field="lastSelfCheckTime"
          title="上次自检"
          width="120"
        ></vxe-table-column>
        <vxe-table-column
          field="status_dictText"
          title="状态"
          width="80"
        ></vxe-table-column>
        <vxe-table-column
          field="groupName"
          title="所属班组"
          width="150"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="userName"
          title="责任人"
          width="150"
          align="left"
          header-align="center"
        ></vxe-table-column>

        <vxe-table-column
          field="supplierName"
          title="生产厂家"
          width="150"
          align="left"
          header-align="center"
        ></vxe-table-column>

        <vxe-table-column
          field="lifetime"
          title="使用寿命"
          width="100"
          align="center"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="model"
          title="规格"
          min-width="140"
          align="left"
          header-align="center"
        ></vxe-table-column>


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
    <next-check-time-modal ref="nextmodalForm" @ok="loadData()"></next-check-time-modal>
    <a-modal
      title="工装器具详情"
      :width="'90%'"
      centered
      :visible="visible"
      dialogClass="fullDialog no-footer"
      @cancel="visible = false"
      :forceRender="true"
    >
      <tools-manage-info ref="toolsManageInfo"></tools-manage-info>
    </a-modal>
  </div>
</template>

<script>
  import ToolsManageInfo from "@views/tiros/group/toolsmanage/ToolsManageInfo";
  import { getlistNeedCheck } from '@/api/tirosMaterialApi'
  import NextCheckTimeModal from '../modules/NextCheckTimeModal'
  import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

  export default {
    name: 'NeedCheckList',
    components:{ NextCheckTimeModal, ToolsManageInfo },
    data() {

      return {
        visible: false,
        queryParam: {
          searchText: '',
          pageNo: 1,
          pageSize: 10,
        },
        totalResult: 0,
        allAlign: 'center',
        tableData: [],
        btnStatus: new TableBtnUtil(this, 'listTable'),
      }
    },
    created() {
      this.findList()
    },
    methods: {
      findList() {
        this.loading = true
        getlistNeedCheck(this.queryParam).then((res) => {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
          this.btnStatus.update()
        })
      },
      
      jumpInfo(row) {
        // alert(row);
        this.visible = true;
        this.$refs.toolsManageInfo.show({ id: row.id });
      },
      getIsOverTimeStatus(row) {
        if (row.nextCheckTime) {
          let dateNow = this.$moment(new Date()).format("YYYY-MM-DD");
          let diffDay = this.$moment(row.nextCheckTime).diff(dateNow, "day");
          return diffDay < 0 ? "是" : "否";
        } else {
          return "否";
        }
      },
      // 计算逾期天数
      getIsOverTimeDays(row) {
        if (row.nextCheckTime) {
          console.log(23232)
          let dateNow = this.$moment(new Date()).format("YYYY-MM-DD");
          let diffDay = this.$moment(row.nextCheckTime).diff(dateNow, "day");
          if (diffDay < 0) {
            return Math.abs(diffDay);
          }
        }
        return "";
      },

      handlePageChange({ currentPage, pageSize }) {
        this.queryParam.pageNo = currentPage
        this.queryParam.pageSize = pageSize
        this.findList()
      },
      handleNextCheck(record) {
        let recordNext = {
          type: 2,
          ids: record.id,
          nextCheckTime: record.length === 1 ? record[0].nextCheckTime : ""
        }
        this.$refs.nextmodalForm.next(recordNext)
        this.$refs.nextmodalForm.title = '设置下次送检时间'
      },
      loadData() {
        this.findList()
      },

      back() {
        this.$router.push({ path: `/tiros/material/equipment` })
      },
    }

  }
</script>

<style scoped>

</style>