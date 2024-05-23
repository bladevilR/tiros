<template>
  <div class="progress_list_cont">
    <div class="start_step"> 开始 </div>
    <a-row type="flex" style="width: 100%">
      <a-col v-for="(item, index) in data" :key="index" class="item_step">
        <p
          :class="
            item.progressStatus === 5
              ? 'label green'
              : (item.progressStatus === 2 || item.progressStatus === 4)
              ? 'label red'
              : (item.progressStatus === 1 || item.progressStatus === 3)
              ? 'label blue'
              : 'label'
          "
        >
          {{ item.no }}、{{ item.title }}
        </p>
      <p class="value">{{ item.progress < 0 ? '无计划任务' : item.progress + '%' }}</p>
      </a-col>
    </a-row>
    <!-- <div v-for="(item, index) in data" :key="index" class="item_step">
      <p
        :class="
          item.progressStatus === 5
            ? 'label green'
            : (item.progressStatus === 2 || item.progressStatus === 4)
            ? 'label red'
            : (item.progressStatus === 1 || item.progressStatus === 3)
            ? 'label blue'
            : 'label'
        "
      >
        {{ item.no }}、{{ item.title }}
      </p>
      <p class="value">{{ item.progress < 0 ? '无计划任务' : item.progress + '%' }}</p>
    </div> -->
    <div class="end_step"> 结束 </div>
  </div>
</template>

<script>
export default {
  name: 'VehicleStructureTable',
  props: {
    data: {
      type: Array,
      default: []
    }
  },
  data() {
    return {}
  },
  methods: {}
}
</script>


<style scoped lang="less">
.progress_list_cont {
  overflow: hidden;
  // padding: 30px 100px;
  display: flex;
  flex-direction: column;
  align-items: center;

  .start_step {
    float: left;
    width: 50px;
    height: 50px;
    line-height: 46px;
    text-align: center;
    background: #70b602;
    color: #fff;
    font-weight: 900;
    border-radius: 50%;
    border: 2px solid #5a9203;
    margin-top: 6px;
    margin-bottom: 20px;
  }

  .end_step {
    float: left;
    width: 50px;
    height: 50px;
    line-height: 46px;
    text-align: center;
    background: #929292;
    color: #fff;
    font-weight: 900;
    border-radius: 50%;
    border: 2px solid #737373;
    margin-top: 20px;
    // margin-left: 15px;
  }

  .item_step {
    float: left;
    margin: 5px 10px;
    border: 1px solid #d7d7d7;
    width: calc(20% - 20px);

    p {
      margin: 0;
      padding: 4px 20px;

      &.label {
        background: #e0e0e0;
        padding: 6px 18px;
        color: #333;
        font-size: 14px;
        border-radius: 3px;

        &.green {
          background: #70b602;
          color: #fff;
          font-weight: 900;
        }

        &.red {
          background: #f11433;
          color: #fff;
          font-weight: 900;
        }

        &.yellow {
          background: #efbe0b;
          color: #fff;
          font-weight: 900;
        }
      }

      &.value {
        text-align: center;
      }
    }
  }
}
</style>