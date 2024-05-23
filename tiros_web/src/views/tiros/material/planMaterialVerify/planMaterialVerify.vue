<template>
  <div class="bodyWrapper" style="padding: 8px; display: flex; flex-direction: column">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24" type="flex">
          <a-col :md="6" :sm="24">
            <a-form-item
              label="列计划:"
              :labelCol="{ span: 5 }"
              :wrapperCol="{ span: 18 }"
            >
              <a-select
                v-model="planName"
                placeholder="请选择列计划"
                :open="false"
                :showArrow="true"
                @focus="openTrainPlanModal"
                ref="planSelect"
              >
                <div slot="suffixIcon">
                  <a-icon
                    v-if="queryParam.planId"
                    @click.stop="clearValue"
                    type="close-circle"
                    style="padding-right: 3px"
                  />
                  <a-icon v-else type="ellipsis" />
                </div>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="3">
            <a-form-item label="线路:">
              <line-select-list
                placeholder="请选择"
                v-model="queryParam.lineId"
                @change="onLineChange"
              ></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="24">
            <a-form-item label="车辆:">
              <j-dict-select-seach-tag
                placeholder="请选择"
                @focus="handleSysFocus"
                v-model="queryParam.trainNo"
                :dictCode="dictTrainStr"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="班组:">
              <j-dict-select-tag
                v-model="queryParam.groupId"
                placeholder="请选择"
                :dictCode="'bu_mtr_workshop_group,group_name,id'"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="类别:">
              <j-dict-select-tag
                v-model="queryParam.useCategory"
                placeholder="请选择"
                dictCode="bu_material_type"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-form-item label="物资:">
              <a-input
                v-model="queryParam.materialSearchText"
                allowClear
                placeholder="请输入物资编码或名称"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col>
            <span class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button @click="$refs.planMaterialModal.show(selecteList[0])" :disabled="selecteList.length != 1" type="primary">
                  核实
                </a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="table-page-body-wrapper" style="height: calc(100vh - 280px)">
      <div class="table-body-context">
        <vxe-table
          border
          ref="listTable"
          align="center"
          height="auto"
          :data="tableData"
          show-overflow="tooltip"
          @checkbox-all="selectChangeEvent"
          @checkbox-change="selectChangeEvent"
          :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        >
          <vxe-table-column type="seq" width="60"></vxe-table-column>
          <vxe-table-column type="checkbox" width="40"></vxe-table-column>
          <vxe-table-column field="lineName" width="60" title="线路" />
          <vxe-table-column field="trainNo" width="60" title="车辆" />
          <vxe-table-column
            field="groupName"
            align="left"
            width="130"
            header-align="center"
            title="所属工班"
          />
          <vxe-table-column field="materialTypeCode" width="130" title="物资编码" />
          <vxe-table-column
            field="materialTypeName"
            width="220"
            align="left"
            header-align="center"
            title="物资名称"
          />
          <vxe-table-column
            field="materialTypeSpec"
            align="left"
            min-width="220"
            header-align="center"
            title="物资描述"
          />
          <vxe-table-column
            field="useCategory_dictText"
            width="80"
            align="left"
            header-align="center"
            title="类别"
          />
          <vxe-table-column
            field="systemName"
            width="180"
            align="left"
            header-align="center"
            title="系统"
          />
          <vxe-table-column
            field="workstationName"
            width="180"
            align="left"
            header-align="center"
            title="工位"
          />
          <vxe-table-column
            field="amount"
            align="right"
            width="80"
            header-align="center"
            title="额定数量"
          />
          <vxe-table-column
            field="actAmount"
            align="right"
            width="80"
            header-align="center"
            title="实际消耗"
          />
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
    </div>
    <planMaterialModal ref="planMaterialModal" @ok="findList" />
    <TrainPlanList ref="trainPlanModal" @ok="onSelectPlan"></TrainPlanList>
  </div>
</template>

<script>
import LineSelectList from "@views/tiros/common/selectModules/LineSelectList";
import TrainPlanList from "@views/tiros/common/selectModules/TrainPlanList";
import planMaterialModal from "@/views/tiros/material/planMaterialVerify/planMaterialModal.vue";
import { planCostVerifyPage } from "@api/tirosMaterialApi";
import { getSerialList } from "@/api/tirosGroupApi";

export default {
  name: "planMaterialVerify",
  components: { LineSelectList, TrainPlanList, planMaterialModal },
  data() {
    return {
      dictTrainStr: "",
      tableData: [],
      planName: null,
      selecteList: [],
      totalResult: 0,
      queryParam: {
        pageNo: 1,
        pageSize: 10,
        lineId: null,
        trainNo: null,
        planId: null,
        groupId: null,
        useCategory: null,
        materialSearchText: null,
      },
    };
  },
  mounted() {
    getSerialList({
      status: 2,
      pageNo: 1,
      pageSize: 1,
    }).then((res) => {
      if (res.success && res.result.records.length) {
        const item = res.result.records[0];
        this.queryParam.planId = item.id;
        this.planName = item.planName;
        this.queryParam.lineId = item.lineId;
        this.onLineChange(item.lineId);
        this.queryParam.trainNo = item.trainNo;
        this.findList();
      }
    });
  },
  methods: {
    handleSysFocus() {
      if (!this.queryParam.lineId) this.$message.warn("请先选择线路!");
    },
    selectChangeEvent({ records }) {
      this.selecteList = JSON.parse(JSON.stringify(records));
    },
    findList() {
      if (!this.queryParam.lineId || !this.queryParam.trainNo) {
        this.$message.warning("请选择线路和车辆");
        return;
      }
      planCostVerifyPage(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total;
          this.selecteList = [];
          this.tableData = res.result.records;
        } else {
          this.$message.error(res.message);
        }
      });
    },
    onLineChange(val, option) {
      this.dictTrainStr =
        "bu_train_info,train_no,train_no,status=1 and line_id='" + val + "'";
    },
    openTrainPlanModal() {
      this.$refs.trainPlanModal.showModal();
      this.$refs.planSelect.blur();
    },
    clearValue() {
      this.queryParam.planId = "";
      this.planName = "";
    },
    onSelectPlan(data) {
      data.forEach((element) => {
        this.queryParam.planId = element.id;
        this.queryParam.trainNo = element.trainNo;
        this.queryParam.lineId = element.lineId;
        this.planName = element.planName;
        console.log(element);
      });
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage;
      this.queryParam.pageSize = pageSize;
      this.findList();
    },
  },
};
</script>

<style lang="less" scoped>
.bodyWrapper {
  .table-page-body {
    &.more-visible {
      height: calc(100% - 106px) !important;
    }
  }
}
</style>
