<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="设备">
              <a-input
                allowClear
                placeholder="设备名称或者编码"
                v-model="queryParam.searchText"
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="时间">
              <a-range-picker v-model="dateRange" :defaultPickerValue="defaultDateRange" />
            </a-form-item>
          </a-col>
          <a-col>
            <span
              style="float: left; overflow: hidden"
              class="table-page-search-submitButtons"
            >
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button type="primary" @click="$refs.editModule.show(1)">新增</a-button>
                <a-button
                  :disabled="selectList.length != 1"
                  @click="$refs.editModule.show(selectList[0])"
                  >修改</a-button
                >
                <a-button @click="deletePlan" :disabled="selectList.length < 1"
                  >删除</a-button
                >
                <a-button
                  :disabled="selectList.length != 1"
                  @click="$refs.viewModal.showModal(selectList[0])"
                  >设备日历</a-button
                >
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
        align="center"
        :data="tableData"
        show-overflow="tooltip"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column
          field="assetCode"
          align="left"
          header-align="center"
          title="设备编码"
          width="160"
        ></vxe-table-column>
        <vxe-table-column
          field="name"
          align="left"
          header-align="center"
          title="设备名称"
          width="160"
        ></vxe-table-column>
        <vxe-table-column
          field="startTime"
          title="开始日期"
          width="160"
        ></vxe-table-column>
        <vxe-table-column field="endTime" title="结束日期" width="160"></vxe-table-column>
        <vxe-table-column
          align="left"
          header-align="center"
          field="workshopName"
          title="所属车间"
          width="160"
        ></vxe-table-column>
        <vxe-table-column field="manufNo" title="出厂编码" width="160"></vxe-table-column>
        <vxe-table-column
          field="leaveFactory"
          title="出厂日期"
          width="160"
        ></vxe-table-column>
        <vxe-table-column field="useDate" title="投入日期" width="160"></vxe-table-column>
        <vxe-table-column
          align="left"
          header-align="center"
          field="dutyUserName"
          title="责任人"
          width="160"
        ></vxe-table-column>
        <vxe-table-column
          field="remark"
          align="left"
          header-align="center"
          title="备注描述"
          width="200"
        ></vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="[
          'PrevJump',
          'PrevPage',
          'Number',
          'NextPage',
          'NextJump',
          'Sizes',
          'FullJump',
          'Total',
        ]"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
    <aplayAndMaintenModule :type="1" @ok="findList" ref="editModule" />
    <SpecialMaterialView ref="viewModal"></SpecialMaterialView>
  </div>
</template>

<script>
import { getSpecassetPlanList, delSpecassetPlan } from "@/api/tirosDispatchApi";
import SpecialMaterialView from "@views/tiros/material/specialmaterial/SpecialMaterialView";
import aplayAndMaintenModule from "../modules/aplayAndMaintenModule";
export default {
  name: "deviceApply",
  components: { aplayAndMaintenModule, SpecialMaterialView },
  data() {
    return {
      dateRange: [],
      queryParam: {
        searchText: undefined,
        planType: 1,
        pageNo: 1,
        pageSize: 10,
      },
      selectList: [],
      totalResult: 0,
      tableData: [],
    };
  },
  created() {
    this.findList();
  },
  methods: {
    deletePlan() {
      this.$confirm({
        content: `是否删除选中${this.selectList.length}条数据？`,
        okText: "确定",
        cancelText: "取消",
        onOk: () => {
          let ids = this.selectList
            .map((item) => {
              return item.id; //条件;
            })
            .join(",");
          return delSpecassetPlan(`ids=${ids}`).then((res) => {
            if (res.success) {
              this.$message.success("操作成功!");
              this.findList();
            }
          });
        },
      });
    },
    findList() {
      if (this.dateRange && this.dateRange.length > 0) {
        this.queryParam.startDate =
          this.dateRange[0] && this.dateRange[0].format("YYYY-MM-DD");
        this.queryParam.endDate =
          this.dateRange[1] && this.dateRange[1].format("YYYY-MM-DD");
      } else {
        this.queryParam.startDate = undefined;
        this.queryParam.endDate = undefined;
      }
      console.log(this.queryParam);
      // return;
      getSpecassetPlanList(this.queryParam).then((res) => {
        this.totalResult = res.result.total;
        this.tableData = res.result.records;
        this.selectList = [];
      });
    },
    checkboxChange({ records }) {
      this.selectList = records;
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage;
      this.queryParam.pageSize = pageSize;
      this.findList();
    },
  },
};
</script>

<style lang="less" scoped></style>
