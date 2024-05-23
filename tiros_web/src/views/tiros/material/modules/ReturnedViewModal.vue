<template>
  <a-modal
    title="退料单查看"
    :width="'80%'"
    centered
    :visible="visible"
    :footer="null"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <div style="height: calc(80vh - 5px); overflow-y: auto">
      <div class="info-wrapper info-top-wrapper descriptionBox" style="margin-top: 10px">
        <h4>基本信息</h4>
        <a-descriptions bordered :column="6">
          <a-descriptions-item label="所属工单" :span="2">
            {{ detail.workOrderName }}
          </a-descriptions-item>
          <a-descriptions-item label="退料日期" :span="2">
            {{ detail.billDate }}
          </a-descriptions-item>
          <a-descriptions-item label="办理人员" :span="2">
            {{ detail.handleUserName }}
          </a-descriptions-item>
          <a-descriptions-item label="备注" :span="6">
            {{ detail.remark }}
          </a-descriptions-item>
        </a-descriptions>
      </div>
      <div class="info-wrapper info-top-wrapper">
        <h4>明细信息</h4>

        <div class="table-page-body">
          <vxe-table
            border
            ref="listTable"
            auto-resize
            max-height="100%"
            :data="detailList"
            align="center"
            style="height: calc(80vh - 264px)"
            show-overflow="tooltip"
          >
            <vxe-table-column
              field="materialTypeCode"
              title="物资编码"
            ></vxe-table-column>
            <vxe-table-column
              header-align="center"
              align="left"
              field="materialTypeSpec"
              width="320"
              title="物资描述"
            ></vxe-table-column>
            <vxe-table-column
              field="applyAmount"
              width="120"
              title="领用数量"
            ></vxe-table-column>
            <vxe-table-column field="returnAmount" title="退回数量"></vxe-table-column>
            <vxe-table-column
              field="sourceLocationName"
              title="退回库位"
            ></vxe-table-column>
          </vxe-table>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script>
import { getReturnedItem, getDetailListByOrderId } from "@/api/tirosMaterialApi";

export default {
  name: "YearPlanDetailModal",
  data() {
    return {
      detail: {},
      detailList: [],
      visible: false,
      handleModel: {},
      fileName: "",
    };
  },
  created() {},
  methods: {
    show(value) {
      this.visible = true;
      this.detail = value;
      getReturnedItem({
        id: value.id,
      }).then((res) => {
        if (res.success) {
          if (res.result.detailList.length) {
            this.detailList = res.result.detailList;
          }
        }
      });
    },
    handleSeeing(data) {
      this.fileName = data.fileName;
      this.$refs.docPreview.handleFilePath(data.fileSavePath);
    },

    // 关闭
    handleCancel() {
      this.close();
    },
    close() {
      this.$emit("close");
      this.visible = false;
      this.handleModel = {};
    },
  },
};
</script>

<style lang="less" scoped>
.info-wrapper {
  border: 1px solid #eee;
  position: relative;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 20px;
}

.info-wrapper h4 {
  position: absolute;
  top: -14px;
  padding: 1px 8px;
  margin-left: 16px;
  color: #777;
  border-radius: 2px 2px 0 0;
  background: #fff;
  font-size: 14px;
  width: auto;
}

li {
  width: 100%;
  list-style: none;
  line-height: 2rem;
  color: black;
  transition: background-color 1s linear, color 1s linear;
  -webkit-transition: background-color 1s linear, color 1s linear;
  -moz-transition: background-color 1s linear, color 1s linear;
  -o-transition: background-color 1s linear, color 1s linear;
}

li:hover {
  background-color: #ddeeff;
  color: #19aaff;
}
.descriptionBox {
  /deep/.ant-descriptions-item-label {
    width: 10%;
    text-align: right;
  }
  /deep/.ant-descriptions-item-content {
    width: 20%;
  }
}
</style>
