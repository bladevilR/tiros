<template>
  <a-descriptions bordered :column="3">
    <a-descriptions-item label="车号">
      {{ trainInfo.trainNo }}
    </a-descriptions-item>
    <a-descriptions-item label="线路">
      {{ trainInfo.lineName }}
    </a-descriptions-item>
    <a-descriptions-item label="车型名称">
      {{ trainInfo.trainTypeName }}
    </a-descriptions-item>
    <a-descriptions-item label="编组数">
      {{ trainInfo.groupNum }}
    </a-descriptions-item>
    <a-descriptions-item label="走行公里数">
      {{ acceptMileage || trainInfo.mileage }}/万公里
    </a-descriptions-item>
    <a-descriptions-item label="厂商名称">
      {{ trainInfo.supplierName }}
    </a-descriptions-item>
    <a-descriptions-item label="投入运营日期">
      {{ trainInfo.useDate }}
    </a-descriptions-item>
    <a-descriptions-item label="最新更新时间">
      {{ trainInfo.updateTime }}
    </a-descriptions-item>
    <a-descriptions-item label="合同编号">
      {{ trainInfo.contractNo }}
    </a-descriptions-item>
    <a-descriptions-item label="合同名称">
      {{ trainInfo.contractName }}
    </a-descriptions-item>
    <a-descriptions-item label="架大修时间" :span="2">
      <a-steps progress-dot :current="currentStepIndex">
        <a-popover slot="progressDot" slot-scope="{ index, status, prefixCls }">
          <template slot="content">
            <span>step {{ index }} status: {{ status }}</span>
          </template>
          <span :class="`${prefixCls}-icon-dot`" />
        </a-popover>
        <a-step v-for='(item,index) in trainPeriodList' :key='index' :title="item.date" :description="item.description" />
      </a-steps>
    </a-descriptions-item>
  </a-descriptions>
</template>

<script>
import { getTrainInfo, getTrainTime } from "@api/tirosProductionApi";

export default {
  name: "TrainResumeBasicInfo",
  props: ["trainNo", "acceptMileage"],
  data() {
    return {
      trainInfo: "",
      queryParam: {
        trainNo: this.trainNo,
      },
      trainPeriodList: [],
      currentStepIndex: 0
    };
  },
  watch: {
    trainNo: {
      immediate: false,
      handler(trainNo) {
        this.queryParam.trainNo = trainNo;
        this.findTrainInfo();
      },
    },
  },
  created() {
    this.findTrainInfo();
  },

  methods: {
    findTrainInfo() {
      getTrainInfo(this.queryParam).then((res) => {
        this.trainInfo = res.result;
        // 获取时间轴数据
        if (this.trainInfo.trainNo) {
          getTrainTime({ trainNo: this.trainInfo.trainNo }).then((res1) => {
            // console.log(res1);
            if (res1.success && res1.result) {
              this.trainPeriodList = res1.result;
              if(this.trainPeriodList.length > 0){
                this.trainPeriodList.map((item, index) => {
                  if (item.isCurrent && item.isCurrent === true) {
                    this.currentStepIndex = index
                  }
                })
              }
            }
          });
        }
      });
    },
  },
};
</script>

<style scoped></style>
