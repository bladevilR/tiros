<template>
  <!-- 实际的工器具选择 -->
  <a-modal
    width="90%"
    title="设备选择"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    centered
    :destroyOnClose="true"
    :bodyStyle="{ height: '80vh' }"
  >
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
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择"
                dictCode="bu_tools_status"
              />
            </a-form-item>
          </a-col>
          <a-col>
            <span
              style="float: left; overflow: hidden"
              class="table-page-search-submitButtons"
            >
              <a-space>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(80vh - 130px)">
      <vxe-table
        border
        ref="listTable"
        style="height: calc(80vh - 130px)"
        max-height="100%"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :radio-config="!multiple ? { trigger: 'row' } : {}"
        :checkbox-config="
          multiple
            ? {
                trigger: 'row',
                highlight: true,
                range: true,
              }
            : {}
        "
        :edit-config="{ trigger: 'click', mode: 'cell', showIcon: 'true' }"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
        <vxe-table-column field="assetCode" title="设备编码"></vxe-table-column>
        <vxe-table-column field="name" title="设备名称"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态"></vxe-table-column>
        <vxe-table-column field="workshopName" title="所属车间"></vxe-table-column>
        <vxe-table-column field="supplierName" title="厂商"></vxe-table-column>
        <vxe-table-column field="manufNo" title="出厂编码"></vxe-table-column>
        <vxe-table-column field="materialCode" title="物资编码"></vxe-table-column>
        <vxe-table-column title="品牌&规格">
          <template v-slot="{row}">
            <div>
              {{row.brand}} <span v-if="row.brand && row.model">-</span> {{row.model}}
            </div>
          </template>
        </vxe-table-column>
        <vxe-table-column field="leaveFactory" title="出厂日期"></vxe-table-column>
        <vxe-table-column field="useDate" title="投入日期"></vxe-table-column>
        <vxe-table-column field="dutyUserName" title="责任人"></vxe-table-column>
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
  </a-modal>
</template>

<script>
import { getSpecasset } from "@/api/tirosDispatchApi";
export default {
  name: "ToolsList",
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      queryParam: {
        searchText:undefined,
        status:undefined,
        pageNo: 1,
        pageSize: 10,
      },
      allAlign: "center",
      totalResult: 0,
      tableData: [],
      visible: false,
      confirmLoading: false,
    };
  },
  methods: {
    showModal() {
      this.visible = true;
      this.findList();
    },
    findList() {
      this.loading = true;
      getSpecasset(this.queryParam).then((res) => {
        this.totalResult = res.result.total;
        this.loading = false;
        this.tableData = res.result.records;
      });
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage;
      this.queryParam.pageSize = pageSize;
      this.findList();
    },
    handleOk() {
      let selectRecords = [];
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords();
      } else {
        if (this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord());
        }
      }
      this.$emit("ok", selectRecords);
      this.visible = false;
    },
    // 关闭
    handleCancel() {
      this.close();
    },
    close() {
      this.$emit("close");
      this.visible = false;
    },
  },
};
</script>

<style scoped>
.limitTitle {
  font-size: 20px;
  font-weight: 600;
  height: 40px;
}
</style>
