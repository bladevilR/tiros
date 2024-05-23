<template>
  <div class="ScreenBox">
    <div class="centerBox">
      <!-- 标题 -->
      <div class="topTitle">
        <div class="title">
          <img src="~@assets/tiros/images/monitor/title_bg.png" alt="" />
          <div class="textBox">
            <span>{{$store.state.user.info.departName || '班组'}}监控大屏</span>
          </div>
        </div>
      </div>
      <!-- 内容 -->
      <div class="contentBox">
        <div class="topBox">
          <div class="left">
            <box title="班组进度">
              <div class="topLeftBox">
                <!-- <div class="title_s">班组进度</div> -->
                <div class="progressBox">
                  <div class="div">
                    <span class="title">任务进度：{{ successNum }}/{{ taskNum }}</span>
                    <div class="warpBox">
                      <a-progress
                        :stroke-color="{
                          from: '#faad14',
                          to: '#87d068',
                        }"
                        :percent="successRatio"
                        status="active"
                      />
                      <span class="text">{{ successRatio }}%</span>
                    </div>
                  </div>
                  <div class="div">
                    <span class="title">延期占比：{{ delayNum }}/{{ taskNum }}</span>
                    <div class="warpBox">
                      <a-progress
                        :stroke-color="{
                          from: '#faad14',
                          to: '#87d068',
                        }"
                        :percent="delayRatio"
                        status="active"
                      />
                      <span class="text">{{ delayRatio }}%</span>
                    </div>
                  </div>

                  <div class="div">
                    <span class="title">暂停工单：{{ stopNum }}/{{ taskNum }}</span>
                    <div class="warpBox">
                      <a-progress
                        :stroke-color="{
                          from: '#faad14',
                          to: '#87d068',
                        }"
                        :percent="stopRatio"
                        status="active"
                      />
                      <span class="text">{{ stopRatio }}%</span>
                    </div>
                  </div>
                </div>
                <div class="title_s" style="margin-top: 0.8vw">任务明细</div>
                <div class="table_box1 tableStyleCss">
                  <vxe-table height="auto" border="outer" :data="tableData1">
                    <vxe-table-column
                      field="orderCode"
                      title="工单号"
                      width="16%"
                    ></vxe-table-column>
                    <vxe-table-column
                      field="orderName"
                      title="工单名称"
                      width="20%"
                    ></vxe-table-column>
                    <vxe-table-column
                      field="startTime"
                      title="计划开始"
                      width="16%"
                    ></vxe-table-column>
                    <vxe-table-column
                      field="finishTime"
                      title="计划结束"
                      width="16%"
                    ></vxe-table-column>
                    <vxe-table-column
                      field="orderStatus_dictText"
                      width="16%"
                      title="工单状态"
                    >
                      <template v-slot="{ row }">
                        <div
                          :style="{
                            backgroundColor: orderStatusColor[row.orderStatus + ''],
                            textAlign: 'center',
                            borderRadius: '4px',
                          }"
                        >
                          {{ row.orderStatus_dictText }}
                        </div>
                      </template>
                    </vxe-table-column>
                    <vxe-table-column width="16%" field="workStatus" title="作业状态">
                      <template v-slot="{ row }">
                        <div
                          :style="{
                            backgroundColor: workStatusColor[row.workStatus + ''],
                            textAlign: 'center',
                            borderRadius: '4px',
                          }"
                        >
                          {{ row.workStatus_dictText }}
                        </div>
                      </template>
                    </vxe-table-column>
                  </vxe-table>
                </div>
              </div>
            </box>
          </div>
          <div class="right">
            <box title="贡献排名">
              <div class="table_box2 tableStyleCss">
                <vxe-table
                  height="auto"
                  :sort-config="{
                    trigger: 'cell',
                    defaultSort: { field: 'faultAmount', order: 'desc' },
                    orders: ['desc', 'asc'],
                  }"
                  border="outer"
                  :data="tableData2"
                >
                  <vxe-table-column
                    width="20%"
                    field="sortNo"
                    title="序号"
                  ></vxe-table-column>
                  <vxe-table-column
                    width="20%"
                    field="workNo"
                    title="工号"
                  ></vxe-table-column>
                  <vxe-table-column
                    width="20%"
                    field="userRealName"
                    title="姓名"
                  ></vxe-table-column>
                  <vxe-table-column
                    field="faultAmount"
                    title="故障数"
                    width="20%"
                    sortable
                  ></vxe-table-column>
                  <vxe-table-column width="20%" field="workTime" title="工时" sortable>
                  </vxe-table-column>
                </vxe-table>
              </div>
            </box>
          </div>
        </div>
        <div class="bottomBox">
          <div class="left">
            <box title="物料预警">
              <div class="table_box3 tableStyleCss">
                <vxe-table height="auto" border="outer" :data="tableData3">
                  <vxe-table-column
                    field="materialTypeCode"
                    title="物料编码"
                    width="16%"
                  ></vxe-table-column>
                  <vxe-table-column
                    field="materialTypeName"
                    title="物料名称"
                    width="20%"
                  ></vxe-table-column>
                  <vxe-table-column
                    field="workshopStockAmount"
                    title="车间库存"
                    width="16%"
                  ></vxe-table-column>
                  <vxe-table-column
                    field="groupStockAmount"
                    title="班组库存"
                    width="16%"
                  ></vxe-table-column>
                  <vxe-table-column
                    field="needAmount"
                    title="需求数量"
                    width="16%"
                  ></vxe-table-column>
                  width="16%"
                  <vxe-table-column
                    field="diffAmount"
                    width="16%"
                    title="差额"
                  ></vxe-table-column>
                </vxe-table>
              </div>
            </box>
          </div>
          <div class="right">
            <box title="工器具预警">
              <div class="table_box3 tableStyleCss">
                <vxe-table height="auto" border="outer" :data="tableData4">
                  <vxe-table-column
                    field="materialTypeCode"
                    width="16%"
                    title="物资编码"
                  ></vxe-table-column>
                  <vxe-table-column
                    field="assetCode"
                    width="16%"
                    title="工器具资产编码"
                  ></vxe-table-column>
                  <vxe-table-column
                    width="20%"
                    field="toolName"
                    title="名称"
                  ></vxe-table-column>
                  <vxe-table-column
                    width="16%"
                    field="toolModel"
                    title="规格"
                  ></vxe-table-column>
                  <vxe-table-column
                    field="nextCheckTime"
                    width="16%"
                    title="下次送检"
                  ></vxe-table-column>
                  <vxe-table-column field="overDays" width="16%" title="逾期天数">
                    <template v-slot="{ row }">
                      <div
                        :style="{
                          backgroundColor: '#DC143C',
                          textAlign: 'center',
                          borderRadius: '4px',
                        }"
                      >
                        {{ row.overDays >= 0 ? row.overDays : "" }}
                      </div>
                    </template>
                  </vxe-table-column>
                </vxe-table>
              </div>
            </box>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getBoardOrder,
  getBoardPerson,
  getBoardTool,
  getBoardMaterial,
  getBoardToday,
} from "@api/tirosKanbanApi";
import box from "./box/box.vue";
export default {
  components: { box },
  data() {
    return {
      taskNum: 0,
      successNum: 0,
      successRatio: 0,
      delayRatio: 0,
      delayNum: 0,
      stopNum: 0,
      stopRatio: 0,
      tableData1: [],
      tableData2: [],
      tableData3: [],
      tableData4: [],
      timer: null,
      orderStatusColor: {
        0: "#dedede",
        1: "#b9caa7",
        2: "#bad795",
        3: "#eaaca5",
        4: "#b1cac0",
        5: "#e2b38c",
      },
      workStatusColor: {
        0: "#dedede",
        1: "#d4efba",
        2: "#bbdeec",
      },
    };
  },
  destroyed() {
    console.log("清楚定时器");
    clearInterval(this.timer);
  },
  mounted() {
    this.initRequest();
    this.timer = setInterval(this.initRequest, 60000);
  },
  methods: {
    initRequest() {
      console.log("请求数据");
      this.getBoardOrder();
      this.getBoardPerson();
      this.getBoardToday();
      this.getBoardMaterial();
      this.getBoardTool();
    },
    // 工器具预警
    getBoardTool() {
      getBoardTool().then((res) => {
        if (res.success) {
          this.tableData4 = res.result;
        }
      });
    },
    // 物料预警
    getBoardMaterial() {
      getBoardMaterial().then((res) => {
        if (res.success) {
          this.tableData3 = res.result;
        }
      });
    },
    // 任务明细
    getBoardToday() {
      getBoardToday().then((res) => {
        if (res.success) {
          this.tableData1 = res.result;
        }
      });
    },
    // 人员排名
    getBoardPerson() {
      getBoardPerson().then((res) => {
        if (res.success) {
          this.tableData2 = res.result;
        }
      });
    },
    // 进度
    getBoardOrder() {
      getBoardOrder().then((res) => {
        console.log(res);
        if (res.success) {
          this.taskNum = res.result.total;
          this.successNum = res.result.finish;
          this.delayNum = res.result.delay;
          this.stopNum = res.result.suspend;
          // 
          this.successRatio =
            !this.successNum && !this.taskNum
              ? 0
              : Number(((this.successNum / this.taskNum) * 100).toFixed(1));
          //  
          this.delayRatio =
            !this.delayNum && !this.taskNum
              ? 0
              : Number(((this.delayNum / this.taskNum) * 100).toFixed(1));
          // 
          this.stopRatio = 
            !this.stopNum && !this.taskNum
                ? 0
                : Number(((this.stopNum / this.taskNum) * 100).toFixed(1))
        }
      });
    },
  },
};
</script>

<style lang="less" scoped>
.flexCenter {
  display: flex;
  justify-content: center;
  align-items: center;
}
@fontColor: #0efcff;
.ScreenBox {
  width: 100%;
  height: 100%;
  background-image: url("~@assets/tiros/images/monitor/background.jpg");
  background-size: cover;
  display: flex;
  align-items: center;
  justify-content: center;
  .centerBox {
    width: 97%;
    height: 95%;
    display: flex;
    flex-flow: column nowrap;
    .topTitle {
      display: flex;
      justify-content: center;
      .title {
        .flexCenter;
        width: 55%;
        margin: auto;
        position: relative;
        img {
          width: 86%;
        }
        .textBox {
          .flexCenter;
          color: @fontColor;
          position: absolute;
          font-size: 1.25vw;
          font-weight: bold;
          height: 100%;
          width: 100%;
          top: 0;
          left: 0;
        }
      }
    }
    .contentBox {
      .commonStyle {
        display: flex;
        flex-flow: row nowrap;
        justify-content: space-between;
        .left,
        .right {
          height: 100%;
          width: 49.5%;
        }
      }
      margin-top: 0.6vw;
      flex: 1;
      height: 0;
      .topBox {
        height: 49%;
        box-sizing: border-box;
        padding-bottom: 0.8vw;
        .commonStyle;
        .topLeftBox {
          padding: 0.45vw;
          height: 100%;
          display: flex;
          flex-flow: column nowrap;

          .title_s {
            color: @fontColor;
            font-size: 1.15vw;
          }
          .progressBox {
            padding-top: 0.5vw;
            display: flex;
            flex-flow: row nowrap;
            justify-content: space-between;
            .div {
              width: 30%;
              .title {
                font-size: 0.8vw;
                color: #fff;
              }
              @progressWidth: 0.95vw;
              .warpBox {
                .text {
                  .title;
                  margin-left: 1vw;
                  padding-bottom: 0.17vw;
                }
                display: flex;
                justify-content: space-between;
                align-items: center;
                // width: calc(100% - 2vw);
                /deep/.ant-progress-text {
                  display: none;
                }
                /deep/.ant-progress-inner {
                  height: @progressWidth;
                  vertical-align: text-top;
                }
                /deep/.ant-progress-show-info,
                /deep/.ant-progress-outer {
                  padding-right: 0;
                }
                /deep/.ant-progress-success-bg,
                /deep/.ant-progress-bg {
                  height: @progressWidth !important;
                }
              }
            }
          }
          .table_box1 {
            flex: 1;
            height: 0;
          }
        }
        .table_box2 {
          height: 100%;
        }
      }
      .bottomBox {
        height: 49%;
        .commonStyle;
        .table_box3 {
          height: 100%;
        }
      }
    }
  }
}
/deep/.tableStyleCss {
  .vxe-table.border--default .vxe-table--header-wrapper,
  .vxe-table.border--full .vxe-table--header-wrapper,
  .vxe-table.border--outer .vxe-table--header-wrapper {
    background: transparent;
  }
  .vxe-table .vxe-table--body-wrapper,
  .vxe-table .vxe-table--footer-wrapper {
    background: transparent;
  }
  * {
    border: 0;
  }
  .vxe-table--header-border-line {
    display: none;
  }
  .vxe-table {
    color: #fff;
    font-size: 0.8vw;
  }
  .vxe-table .vxe-body--row .vxe-body--column,
  .vxe-table .vxe-body--column.col--ellipsis,
  .vxe-table.vxe-editable .vxe-body--column,
  .vxe-table .vxe-footer--column.col--ellipsis,
  .vxe-table .vxe-header--column.col--ellipsis {
    height: 30px !important;
  }
}
</style>
