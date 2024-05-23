<template>
  <div style="max-height: calc(80vh - 5px); overflow-y: auto">
    <div class="info-wrapper info-top-wrapper descriptionBox" style="margin-top: 10px">
      <h4>基本信息</h4>
      <a-descriptions bordered :column="4">
        <a-descriptions-item label="标题">
          {{ detail.title }}
        </a-descriptions-item>
        <a-descriptions-item label="计划年份">
          {{ detail.year }}
        </a-descriptions-item>
        <a-descriptions-item label="首列时间">
          {{ detail.firstTime }}
        </a-descriptions-item>
        <a-descriptions-item label="车辆段">
          {{ detail.depotName }}
        </a-descriptions-item>
        <a-descriptions-item label="架修数">
          {{ detail.middleAmount }}
        </a-descriptions-item>
        <a-descriptions-item label="架修模板">
          {{ detail.middlePlanTemplateName }}
        </a-descriptions-item>
        <a-descriptions-item label="大修数">
          {{ detail.hightAmount }}
        </a-descriptions-item>
        <a-descriptions-item label="大修模板">
          {{ detail.hightPlanTemplateName }}
        </a-descriptions-item>
        <a-descriptions-item label="车间">
          {{ detail.workshopName }}
        </a-descriptions-item>
        <a-descriptions-item label="完成进度">
          <div v-if="detail.finishPercent != null">
            <a-badge status="processing" :text="detail.finishPercent + ''" />
          </div>
        </a-descriptions-item>
        <a-descriptions-item label="备注" :span="2">
          {{ detail.remark }}
        </a-descriptions-item>
      </a-descriptions>
    </div>
    <div class="info-wrapper info-top-wrapper">
      <h4>明细信息</h4>

      <div class="table-page-body">
        <vxe-table
          show-overflow="tooltip"
          :print-config="{}"
          border
          max-height="100%"
          align="center"
          :data="detailList"
          style="height: calc(80vh - 330px)"
        >
<!--          <vxe-table-column width="80px" title="序号" type="seq"></vxe-table-column>-->
          <vxe-table-column
            width="120px"
            title="线路"
            field="lineName"
          ></vxe-table-column>
          <vxe-table-column
            width="100px"
            title="总列次"
            field="trainIndex"
            align="right"
            header-align="center"
          ></vxe-table-column>
<!--          <vxe-table-column
            width="80px"
            title="数量"
            field="amount"
            align="right"
            header-align="center"
          ></vxe-table-column>-->
          <vxe-table-column
            width="100px"
            title="修程次数"
            field="programIndex"
            align="right"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column
            width="200px"
            title="检修修程"
            field="programName"
          ></vxe-table-column>
          <vxe-table-column
            width="200px"
            title="开始时间"
            field="startDate"
          ></vxe-table-column>
          <vxe-table-column
            width="200px"
            title="完成时间"
            field="finishDate"
          ></vxe-table-column>
          <vxe-table-column
            width="120px"
            title="状态"
            field="status_dictText"
          ></vxe-table-column>
          <vxe-table-column
            title="备注"
            field="remark"
            align="left"
            header-align="center"
          ></vxe-table-column>
        </vxe-table>
      </div>
    </div>
    <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
  </div>
</template>

<script>
import { everythingIsEmpty } from "@/utils/util";
import { getYearPlanDetail } from "@/api/tirosDispatchApi";
import DocPreviewModal from "@views/tiros/common/doc/DocPreviewModal";

export default {
  name: "YearPlanDetailModal",
  components: { DocPreviewModal },
  props: {
    businessKey: {
      type: String,
      default: null,
    },
    isReadonly: {
      type: Boolean,
      default: true,
    },
    fromFlow: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      detail: {},
      detailList: [],
      handleModel: {},
      fileName: "",
    }
  },
  mounted() {
    if (this.businessKey) {
      this.show(this.businessKey);
    }
  },
  methods: {
    show(value) {
      this.planId = value;
      if (!everythingIsEmpty(this.planId)) {
        getYearPlanDetail({ id: value }).then((res) => {
          if (res.success) {
            this.detail = res.result
            this.detailList = res.result.detailList || []
          }
          this.$emit('loaded')
        })
      } else {
        this.$emit('loaded')
      }
    },
    handleSeeing(data) {
      this.fileName = data.fileName;
      this.$refs.docPreview.handleFilePath(data.fileSavePath);
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
    width: 15%;
  }
}
</style>
