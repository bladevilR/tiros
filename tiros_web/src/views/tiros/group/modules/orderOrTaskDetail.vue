<template>
    <div class="taskDetailsPana" style="padding: 20px">
        <div class="info-wrapper info-top-wrapper">
            <h4>工单信息</h4>
            <a-descriptions title bordered>
                <a-descriptions-item label="工单编号">
                  {{ workOrderInfo.orderCode }}
                </a-descriptions-item>
                <a-descriptions-item label="工单类型">
                  {{ workOrderInfo.orderType_dictText }}
                </a-descriptions-item>
                <a-descriptions-item label="工单名称">
                  {{ workOrderInfo.orderName }}
                </a-descriptions-item>
                <a-descriptions-item label="作业班组">
                  {{ workOrderInfo.groupName }}
                </a-descriptions-item>
                <a-descriptions-item label="线路">
                  {{ workOrderInfo.lineName }}
                </a-descriptions-item>
                <a-descriptions-item label="车号">
                  {{ workOrderInfo.trainNo }}
                </a-descriptions-item>
                <a-descriptions-item label="开始日期">
                  {{ workOrderInfo.startTime }}
                </a-descriptions-item>
                <a-descriptions-item label="结束日期">
                  {{ workOrderInfo.finishTime }}
                </a-descriptions-item>
            </a-descriptions>
        </div>
        <div class="info-wrapper info-top-wrapper">
            <h4>任务信息</h4>
            <a-descriptions title bordered>
                <a-descriptions-item v-if="type === 'outsources'" label="任务编码">
                  {{ taskInfo.taskNo }}
                </a-descriptions-item>
                <a-descriptions-item v-else label="任务序号">
                  {{ taskInfo.taskNo }}
                </a-descriptions-item>

                <a-descriptions-item label="重要工序">
                  {{ taskInfo.important === 1 ? '是' : '否' }}
                </a-descriptions-item>

                <a-descriptions-item v-if="type === 'outsources'" label="是否委外">
                  {{ taskInfo.outTask === 0 ? '否' : taskInfo.outTask === 1 ? '委外送修' : '委外接收' }}
                </a-descriptions-item>
                <a-descriptions-item v-else label="是否委外">
                  {{ taskInfo.outsource === 1 ? '是' : '否' }}
                </a-descriptions-item>

                <a-descriptions-item label="任务名称">
                  {{ taskInfo.taskName }}
                </a-descriptions-item>
                <a-descriptions-item label="作业手段">
                  {{ taskInfo.method_dictText }}
                </a-descriptions-item>
                <a-descriptions-item label="作业部件">
                  {{ taskInfo.assetTypeName }}
                </a-descriptions-item>
                <a-descriptions-item label="预计工时">
                  {{ taskInfo.workTime }}
                </a-descriptions-item>

                <a-descriptions-item v-if="type === 'outsources'" label="任务描述">
                  {{ taskInfo.taskContent }}
                </a-descriptions-item>
                <a-descriptions-item v-else label="任务描述">
                  {{ taskInfo.remark }}
                </a-descriptions-item>
                
            </a-descriptions>
        </div>
    </div>
</template>

<script>
export default {
  props:{
    workOrderInfo:{
      type:Object,
      default:{}
    },
    taskInfo:{
      type:Object,
      default:{}
    },
    type:String,
  },
  name: 'orderOrTaskDetail', // 作业填报  - 任务信息工单信息查看组件
}
</script>

<style lang="less" scoped>
.taskDetailsPana {
    .info-top-wrapper {
        /deep/.ant-descriptions-item-label {
            width: 15%;
            text-align: right;
        }
        /deep/.ant-descriptions-item-content {
            width: 15%;
        }
    }
    .info-wrapper {
      border: 1px solid #eee;
      position: relative;
      border-radius: 8px;
      padding: 15px;
      padding-top: 20px;
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
} 
</style>
