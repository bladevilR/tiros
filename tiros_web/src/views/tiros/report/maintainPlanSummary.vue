<template>
  <div>
    <a-spin :spinning="spinning" tip="数据加载中...">
      <div class="table-page-search-wrapper">
        <a-form layout="inline" @keyup.enter.native="getDataAll">
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
            <a-col>
              <span class="table-page-search-submitButtons">
                <a-space>
                  <a-button @click="getDataAll">查询</a-button>
                  <a-button @click="$refs.WordPreviewExport.exportWord()"
                    >导出Word</a-button
                  >
                </a-space>
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <div
        class="table-page-body-wrapper"
        style="height: calc(100vh - 174px); position: relative"
      >
        <WordPreviewExport v-if="!spinning" ref="WordPreviewExport" />
        <div class="maskBox" v-else></div>
      </div>
    </a-spin>
    <TrainPlanList
      :progressStatusList="[3, 4, 5]"
      ref="trainPlanModal"
      @ok="onSelectPlan"
    ></TrainPlanList>
  </div>
</template>

<script>
import TrainPlanList from "@views/tiros/common/selectModules/TrainPlanList";
import WordPreviewExport from "@/views/tiros/report/wordExport/WordPreviewExport.vue";
import { getSerialList } from "@/api/tirosGroupApi";
import {
  getSummaryCost,
  getSummaryFault,
  getSummaryDetail,
  getSummaryWorkTime,
  getSummaryPeriod,
  getSummaryOutsource,
  getSummaryProgress,
} from "@/api/tirosReportApi.js";
export default {
  components: { TrainPlanList, WordPreviewExport },
  data() {
    return {
      planName: null,
      queryParam: {
        planId: null,
      },
      spinning: true,
      WordPreviewExportData: {
        getSummaryDetail: {},
        getSummaryCost: {},
        getSummaryFault: {},
        getSummaryWorkTime: {},
        getSummaryPeriod: {},
        getSummaryOutsource: {},
        getSummaryProgress:{},
      },
    };
  },
  mounted() {
    getSerialList({
      progressStatusList: [3, 4, 5],
      pageNo: 1,
      pageSize: 1,
    }).then((res) => {
      if (res.success && res.result.records.length) {
        const item = res.result.records[0];
        this.queryParam.planId = item.id;
        this.planName = item.planName;
        this.getDataAll();
      }
    });
  },
  methods: {
    // 获取整个word数据
    getDataAll() {
      const allList = [];
      this.spinning = true;
      // 总体情况
      const request0 = getSummaryDetail({
        planId: this.queryParam.planId,
      }).then((res) => {
        console.log(res, 0);
        if (res.success) {
          this.WordPreviewExportData.getSummaryDetail = res.result;
        }
      });
      allList.push(request0);
      // 维修成本情况
      const request1 = getSummaryCost({
        planId: this.queryParam.planId,
      }).then((res) => {
        console.log(res, 1);
        if (res.success) {
          this.WordPreviewExportData.getSummaryCost = res.result;
        }
      });
      allList.push(request1);
      // 维修质量控制情况
      const request2 = getSummaryFault({
        planId: this.queryParam.planId,
      }).then((res) => {
        console.log(res, 2);
        if (res.success) {
          this.WordPreviewExportData.getSummaryFault = res.result;
        }
      });
      allList.push(request2);
      //维修作业工时控制情况
      const request3 = getSummaryWorkTime({
        planId: this.queryParam.planId,
      }).then((res) => {
        console.log(res, 3);
        if (res.success) {
          this.WordPreviewExportData.getSummaryWorkTime = res.result;
        }
      });
      allList.push(request3);
      // 维修周期控制情况
      const request4 = getSummaryPeriod({
        planId: this.queryParam.planId,
      }).then((res) => {
        console.log(res, 4);
        if (res.success) {
          this.WordPreviewExportData.getSummaryPeriod = res.result;
        }
      });
      allList.push(request4);
      // 委外维修部件维修进度控制情况
      const request5 = getSummaryOutsource({
        planId: this.queryParam.planId,
      }).then((res) => {
        console.log(res, 5);
        if (res.success) {
          this.WordPreviewExportData.getSummaryOutsource = res.result;
        }
      });
      allList.push(request5);
      // 生产进度控制情况
      const request6 = getSummaryProgress({
        planId: this.queryParam.planId,
      }).then((res) => {
        console.log(res, 6);
        if (res.success) {
          this.WordPreviewExportData.getSummaryProgress = res.result;
        }
      });
      allList.push(request6);
      //
      Promise.all(allList)
        .then((result) => {
          console.log("全部请求完成");
          this.spinning = false;
          this.$nextTick(() => {
            if(this.$refs.WordPreviewExport){
              this.$refs.WordPreviewExport.show(this.WordPreviewExportData);
            }
          });
        })
        .catch((error) => {
          console.log(error); // 失败了，打出 '失败'
        });
    },
    onSelectPlan(data) {
      data.forEach((element) => {
        this.queryParam.planId = element.id;
        this.planName = element.planName;
      });
    },
    openTrainPlanModal() {
      this.$refs.trainPlanModal.showModal();
      this.$refs.planSelect.blur();
    },
  },
};
</script>

<style lang="less" scoped>
.maskBox {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 999;
  height: 100%;
  width: 100%;
  background: #e1e1e1;
}
</style>
