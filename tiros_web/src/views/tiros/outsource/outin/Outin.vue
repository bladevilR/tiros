<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="交接">
              <a-input
                placeholder="请输入交接单号或者工单号"
                v-model="queryParam.billSearchText"
                allowClear
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="部件">
              <a-input
                placeholder="请输入部件名或者编号"
                v-model="queryParam.assetSearchText"
                allowClear
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="24">
            <a-form-item label="所属线路">
              <line-select-list
                v-model="queryParam.lineId"
                @change="changeLine"
              ></line-select-list>
              <!-- <j-dict-select-tag
                :triggerChange="true"
                v-model="faultModel.lineId"
                dictCode="bu_mtr_line,line_name,line_id"
                @change="changeLine"
              /> -->
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="24">
            <a-form-item label="车辆">
              <j-dict-select-seach-tag
                :triggerChange="true"
                v-model="queryParam.trainId"
                :dictCode="dictTrainStr"
                @focus="handleSysFocus"
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-space>
              <a-button @click="findList">查询</a-button>
              <a-button v-has="'outsource:outin:add'" type="primary" @click="$refs.OutinEntrance.add(1)"
                >新增入库单</a-button
              >
              <a-button v-has="'outsource:outin:add'" type="primary" @click="$refs.OutinEntrance.add(0)"
                >新增出库单</a-button
              >
              <a-button v-has="'outsource:outin:edit'" :disabled="selectRecords.length != 1" @click="editorEvent"
                >编辑</a-button
              >
              <a-button v-has="'outsource:outin:delete'" :disabled="selectRecords.length < 1" @click="deleteEvent"
                >删除</a-button
              >
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 255px)">
      <vxe-table
        border
        auto-resize
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 255px)"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        @checkbox-change="rowSelectChange"
        @checkbox-all="rowSelectChange"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="80"></vxe-table-column>
        <vxe-table-column field="trainNo" title="车号" width="80"></vxe-table-column>
        <vxe-table-column
          field="billType_dictText"
          title="类型"
          width="80"
        ></vxe-table-column>
        <vxe-table-column field="billNo" title="单号" min-width="120">
          <template v-slot="{ row }">
            <a @click.stop="outInDetail(row)">{{ row.billNo }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="contactPhone"
          title="交接单描述"
          min-width="120"
          header-align="center"
          align="left"
        ></vxe-table-column>
        <vxe-table-column
          field="sendGroupName"
          title="交接班组"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="transferUserName"
          title="移交人员"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="receiveUserName"
          title="接收人员"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="engineerName"
          title="工程师"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="transferDate"
          title="交接日期"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="workOrderNo"
          title="交接工单"
          min-width="120"
        ></vxe-table-column>
        <vxe-table-column
          field="outinName"
          title="交接工单名称"
          min-width="120"
        ></vxe-table-column>
        <vxe-table-column
          field="remark"
          title="备注"
          min-width="120"
          header-align="center"
          align="left"
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
      <outin-detail ref="outInDetail"> </outin-detail>
      <OutinEntrance @ok="findList" ref="OutinEntrance" />
    </div>
  </div>
</template>

<script>
import { getOutinList, deleteOutIn } from "@/api/tirosOutsourceApi";
import OutinDetail from "@views/tiros/outsource/outin/OutinDetail";
import OutinEntrance from "@/views/tiros/outsource/outin/OutinEntrance.vue";
import LineSelectList from "@views/tiros/common/selectModules/LineSelectList";

export default {
  name: "Outin",
  components: { OutinDetail, LineSelectList, OutinEntrance },
  data() {
    return {
      queryParam: {
        assetSearchText: "",
        billSearchText: "",
        lineId: "",
        trainId: "",
        pageNo: 1,
        pageSize: 10,
      },
      dictTrainStr: "",
      totalResult: 0,
      allAlign: "center",
      selectRecords: [],
      tableData: [],
    };
  },
  created() {
    this.findList();
  },
  methods: {
    editorEvent() {
      // console.log(this.selectRecords[0])
      this.$refs.OutinEntrance.edit(this.selectRecords[0]);
    },
    deleteEvent() {
      const ids = this.selectRecords
        .map((item) => {
          return item.id; //条件;
        })
        .join(",");
      console.log(ids);
      this.$confirm({
        content: "确定要删除选中的出入单吗？",
        onOk: () => {
          return deleteOutIn(`ids=${ids}`).then((res) => {
            if (res.success) {
              this.$message.success("删除成功");
              this.findList();
            }
          });
        },
      });
    },
    rowSelectChange() {
      this.selectRecords = this.$refs.listTable.getCheckboxRecords();
    },
    handleSysFocus() {
      if (!this.queryParam.lineId) this.$message.warn("请先选择线路!");
    },
    openSupplierModal(type) {
      if (type === 1) {
        this.supplierSelectType = 1;
        this.$refs.supplierModal.showModal();
        this.$refs.mySupplierSelectOut.blur();
      } else {
        this.supplierSelectType = 2;
        this.$refs.supplierModal.showModal();
        this.$refs.mySupplierSelectIn.blur();
      }
    },
    changeLine(data, edit) {
      this.dictTrainStr = "";
      if (data) {
        this.dictTrainStr =
          "bu_train_info,train_no,train_no,line_id=" + data + "|train_struct_id";
      }
    },
    findList() {
      getOutinList(this.queryParam).then((res) => {
        this.totalResult = res.result.total;
        this.loading = false;
        this.tableData = res.result.records;
        this.selectRecords = [];
      });
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage;
      this.queryParam.pageSize = pageSize;
      this.findList();
    },
    loadData() {
      this.findList();
      this.$emit("load");
    },
    outInDetail(value) {
      this.$refs.outInDetail.show(value);
    },
  },
};
</script>

<style scoped></style>
