<template>
  <div>
    <a-modal
      title="入库明细查看"
      :width="1100"
      @cancel="visible = false"
      :visible="visible"
      :destroyOnClose="true"
    >
      <a-spin :spinning="confirmLoading">
        <div class="descriptionBox">
          <a-descriptions bordered :column="6">
            <a-descriptions-item label="物资编码" :span="2">{{detailData.materialTypeCode}}</a-descriptions-item>
            <a-descriptions-item label="物资名称" :span="2">{{detailData.materialTypeName}}</a-descriptions-item>
            <a-descriptions-item label="入库数量" :span="2">{{detailData.amount}}</a-descriptions-item>
            <a-descriptions-item label="二级库位" :span="2">{{detailData.selfWarehouseName}}</a-descriptions-item>
            <a-descriptions-item label="生产日期" :span="2">{{detailData.productionDate}}</a-descriptions-item>
            <a-descriptions-item label="失效日期" :span="2">{{detailData.expirDate}}</a-descriptions-item>
            <a-descriptions-item label="有效期" :span="2">{{detailData.expirDay}}</a-descriptions-item>
            <a-descriptions-item label="物资属性" :span="2">{{detailData.materialAttr}}</a-descriptions-item>
          </a-descriptions>
        </div>
        <!-- 明细 -->
        <div>
          <a-divider orientation="left"> 入库明细 </a-divider>
          <div style="height: 200px">
            <vxe-table
              ref="listTable"
              height="auto"
              align="center"
              border
              :data="detailList"
              :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
              show-overflow="tooltip"
            >
              <vxe-table-column type="seq" width="80" fixed="left"></vxe-table-column>
              <vxe-table-column field="amount" title="入库数量" width="200">
              </vxe-table-column>
              <vxe-table-column field="selfWarehouseName" title="移入四级库">
              </vxe-table-column>
            </vxe-table>
          </div>
        </div>
      </a-spin>
    </a-modal>
  </div>
</template>

<script>
import { entryViewStock } from "@api/tirosMaterialApi";
export default {
  name: "PutStockViewModal",
  data() {
    return {
      visible: false,
      confirmLoading: false,
      detailData: {},
      detailList: [],
    };
  },
  created() {},
  methods: {
    showModal(record) {
      console.log(record);
      this.visible = true;
      this.confirmLoading = true;
      entryViewStock({ entryDetailId: record.id })
        .then((res) => {
          if (res.success) {
            this.detailData = res.result;
            this.detailList = res.result.levelFourDetail;
          }
          this.confirmLoading = false;
        })
        .catch((err) => {
          this.confirmLoading = false;
        });
    },
  },
};
</script>

<style lang="less" scoped>
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
