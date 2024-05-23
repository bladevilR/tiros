<template >
  <div class="kpi-main" >
    <!-- 查询条件目录 -->
    <div :style="{ position: 'absolute', top: 1, left: 15, zIndex: 999, cursor: 'pointer' }">
      <a-dropdown :trigger="['click']">
        <!--                <a-icon class="switch_btn" type="setting" @click="(e) => e.preventDefault()" />-->
        <img src="~@/assets/tiros/images/magic.svg" @click="(e) => e.preventDefault()" />
        <a-menu slot="overlay" style="min-width: 110px">
          <a-sub-menu v-for="(lineItem, index) in plansList" :key="index" :title="lineItem.depotName">
            <a-menu-item v-for="(planItem, j) in lineItem.plans" :key="j" @click="selectPlanInfo(planItem)"
              >{{ planItem.planName }}
            </a-menu-item>
          </a-sub-menu>
        </a-menu>
      </a-dropdown>
    </div>
    <!-- Header -->
    <div>
      <div class="kpi-table-header" flex>
        <div class="kpi-column" style="width: 180px" column-padding>
          <div class="kpi-cell">{{ `当前状态：${planStatu}` }}</div>
        </div>
        <div style="width: 100%" border>
          <div class="kpi-column">
            <div class="kpi-cell">{{ planTitle }}</div>
          </div>
          <div flex>
            <div class="kpi-column" column-padding style="width: 200px; position: relative">
              <div class="kpi-cell">
                <span class="label" style="position: relative; z-index: 1">
                  当前架修进度：{{ planInfo.progress || 0 }}%
                </span>
                <div class="kpi-progress">
                  <div class="progress-bg" :style="`width: ${planInfo.progress || 0}%`"></div>
                </div>
              </div>
            </div>
            <div class="kpi-column" column-padding>
              <div class="kpi-cell">当前工期第： {{ planInfo.currentDay || 0 }} 天</div>
            </div>
            <div class="kpi-column" column-padding>
              <div class="kpi-cell">计划完工： {{ planInfo.finishDate || '暂无数据' }}</div>
            </div>
            <div class="kpi-column" style="width: 100%" column-padding align="left">
              <div class="kpi-cell"  style="width: 100%;">
                <a-row type="flex">
                  <a-col flex="210px">
                    <a-row type="flex" style="height: 32px">
                      <a-col :span="24">
                        <a-radio-group
                          name="radioGroup"
                          v-model="kpiType"
                          @change="changeKpiType"
                          style="height: 100%; display: flex; align-items: center"
                        >
                          <a-radio :value="1"> 员工贡献</a-radio>
                          <a-radio :value="2"> 班组贡献</a-radio>
                        </a-radio-group>
                      </a-col>
                    </a-row>
                  </a-col>
                  <a-col v-if="kpiType == 2">
                    <span>检修班组：&nbsp;</span>
                    <j-dict-select-tag
                      v-model="workGroup"
                      dictCode="bu_mtr_workshop_group,group_name,id"
                      @select="selectWrokGroup"
                      style="width: 150px"
                    />
                  </a-col>
                </a-row>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Body -->
    <div class="kpi-body">
      <div
        class="panel-left"
        :class="{ 'panel-width-full': showPanelType === 2, 'panel-width-empty': showPanelType == 0 }"
      >
        <vxe-table border ref="kpiTableLeft" :data="kpiTimeList" auto-resize height="auto" align="center">
          <vxe-table-colgroup title="工时贡献度">
            <vxe-table-column title="姓名" field="userName"></vxe-table-column>
            <vxe-table-column title="工号" field="workNo"></vxe-table-column>
            <vxe-table-column title="班组" field="groupName"></vxe-table-column>
            <!-- <vxe-table-column title="工位"></vxe-table-column> -->
            <vxe-table-column title="检修工时" field="repairTime"></vxe-table-column>
            <vxe-table-column title="处理故障工时" field="faultTime"></vxe-table-column>
            <vxe-table-column title="总工时" field="totalTime"></vxe-table-column>
            <vxe-table-column title="排名" field="sortNo"></vxe-table-column>
          </vxe-table-colgroup>
        </vxe-table>
      </div>
      <div class="panel-middle">
        <div v-if="showPanelType !== 0" class="panel-btn-left" @click="panelToLeft()"></div>
        <div v-if="showPanelType !== 2" class="panel-btn-right" @click="panelToRight()" style="margin-top: 10px"></div>
      </div>
      <div
        class="panel-right"
        :class="{ 'panel-width-full': showPanelType === 0, 'panel-width-empty': showPanelType == 2 }"
      >
        <vxe-table border ref="kpiTableRight" :data="kpiFaultList" auto-resize height="auto" align="center">
          <vxe-table-colgroup title="故障贡献度">
            <vxe-table-column title="姓名" field="userName"></vxe-table-column>
            <vxe-table-column title="工号" field="workNo"></vxe-table-column>
            <vxe-table-column title="班组" field="groupName"></vxe-table-column>
            <!-- <vxe-table-column title="工位"></vxe-table-column> -->
            <vxe-table-column title="发现故障次数" field="faultAmount"></vxe-table-column>
            <vxe-table-column title="处理故障次数" field="handleAmount"></vxe-table-column>
            <vxe-table-column title="故障次数总计" field="totalHappen"></vxe-table-column>
          </vxe-table-colgroup>
        </vxe-table>
      </div>
    </div>
  </div>
</template>

<script>
import { getKpiGroupList, getKpiUserList, getKpiPlanList, getKpiPlanInfo } from '@api/tirosProductionApi'
export default {
  name: 'GroupKpiPage',
  components: {},
  data() {
    return {
      dictTrainStr: '',
      // planStatu: '当前状态： 正常(作业中)',
      planInfo: {},
      resData: [],
      tableData: [],
      tableKpiInfo: {},
      plansList: [],
      workGroup: '',
      kpiType: 1,
      showPanelType: 1,
      activePlanIndex: 0,
      queryParam: {
        lineId: null,
        trainNo: undefined,
        depotId: undefined,
        periodType: 2,
      },
    }
  },
  computed: {
    planTitle() {
      if (this.plansList.length > 0 && this.planInfo.depotName !== '' && this.planInfo.trainNo !== '') {
        return `${this.planInfo.depotName}: ${this.planInfo.trainNo}车辆`
      }
      return ''
    },
    planStatu() {
      if (this.planInfo.hasOwnProperty('progressStatus')) {
        if (this.planInfo.progressStatus == 0) {
          return '未开始'
        } else if (this.planInfo.progressStatus == 1) {
          return '正常(作业中)'
        } else if (this.planInfo.progressStatus == 2) {
          return '逾期(作业中)'
        } else if (this.planInfo.progressStatus == 3) {
          return '正常(完工)'
        } else if (this.planInfo.progressStatus == 4) {
          return '逾期(完工)'
        } else if (this.planInfo.progressStatus == 5) {
          return '提前(完工)'
        }
      }
      return ''
    },
    kpiTimeList() {
      if (this.tableKpiInfo.timeList) {
        if (this.tableKpiInfo.timeList.length > 0) {
          return this.tableKpiInfo.timeList
        }
      }
      return []
    },
    kpiFaultList() {
      if (this.tableKpiInfo.faultList) {
        if (this.tableKpiInfo.faultList.length > 0) {
          return this.tableKpiInfo.faultList
        }
      }
      return []
    },
  },
  watch: {
    workGroup: function (val) {
      if (!val) {
        this.getKpiList()
      }
    },
  },
  mounted() {
    this.getPlanList()
    this.getKpiList()
  },
  methods: {
    getPlanList() {
      // 查询列计划列表
      getKpiPlanList()
        .then((res) => {
          if (res.success && res.result.length > 0) {
            this.plansList.length = 0
            res.result.forEach((element) => {
              element.plans.forEach((e) => {
                if (!e.depotName) {
                  e.depotName = element.depotName
                }
                if (!e.depotId) {
                  e.depotId = element.depotId
                }
              })
              this.plansList.push(JSON.parse(JSON.stringify(element)))
            })
            this.planInfo = this.plansList[0].plans[0]
            getKpiPlanInfo({
              planId: this.planInfo.id,
            }).then((res) => {
              if (res.success) {
                this.planInfo.progress = res.result.progress
                this.planInfo.currentDay = res.result.currentDay
                this.planInfo.finishDate = res.result.finishDate
              } else {
                this.$message.error(res.message)
              }
            })
            this.getKpiList()
            this.loadProject()
          }
        })
        .catch((err) => {})
    },
    /**
     * 加载计划模版详情
     * @param plan
     */
    selectPlanInfo(value) {
      this.planInfo = value
      getKpiPlanInfo({
        planId: this.planInfo.id,
      }).then((res) => {
        if (res.success) {
          this.planInfo.progress = res.result.progress
          this.planInfo.currentDay = res.result.currentDay
          this.planInfo.finishDate = res.result.finishDate
        } else {
          this.$message.error(res.message)
        }
      })
      this.getKpiList()
    },
    getKpiList() {
      if (
        this.planInfo.hasOwnProperty('depotId') &&
        this.planInfo.hasOwnProperty('trainNo') &&
        this.planInfo.hasOwnProperty('id')
      ) {
        if (this.kpiType == 1) {
          getKpiUserList({
            depotId: this.planInfo.depotId,
            lineId: this.planInfo.lineId,
            planId: this.planInfo.id,
          }).then((res) => {
            if (res.success && res.result) {
              this.tableKpiInfo = res.result
            } else {
              this.$message.warn(res.message)
            }
          })
        } else {
          getKpiGroupList({
            depotId: this.planInfo.depotId,
            groupId: this.workGroup || undefined,
            lineId: this.planInfo.lineId,
            planId: this.planInfo.id,
          }).then((res) => {
            if (res.success && res.result) {
              this.tableKpiInfo = res.result
            } else {
              this.$message.warn(res.message)
            }
          })
        }
      }
    },
    changeKpiType() {
      if (this.kpiType == 2) {
        this.$refs.kpiTableLeft.hideColumn(this.$refs.kpiTableLeft.getColumnByField('userName'))
        this.$refs.kpiTableLeft.hideColumn(this.$refs.kpiTableLeft.getColumnByField('workNo'))
        this.$refs.kpiTableRight.hideColumn(this.$refs.kpiTableRight.getColumnByField('userName'))
        this.$refs.kpiTableRight.hideColumn(this.$refs.kpiTableRight.getColumnByField('workNo'))
      } else {
        this.$refs.kpiTableLeft.showColumn(this.$refs.kpiTableLeft.getColumnByField('userName'))
        this.$refs.kpiTableLeft.showColumn(this.$refs.kpiTableLeft.getColumnByField('workNo'))
        this.$refs.kpiTableRight.showColumn(this.$refs.kpiTableRight.getColumnByField('userName'))
        this.$refs.kpiTableRight.showColumn(this.$refs.kpiTableRight.getColumnByField('workNo'))
      }
      this.getKpiList()
    },
    selectWrokGroup() {
      this.getKpiList()
    },
    panelToLeft() {
      if (this.showPanelType > 0) {
        this.showPanelType--
      }
      this.$refs.kpiTableLeft.refreshColumn()
      this.$refs.kpiTableRight.refreshColumn()
    },
    panelToRight() {
      if (this.showPanelType < 2) {
        this.showPanelType++
      }
      this.$refs.kpiTableLeft.refreshColumn()
      this.$refs.kpiTableRight.refreshColumn()
    },
  },
}
</script>
<style lang="less" scoped>
.kpi-main {
  padding: 8px;
  position: relative;
  width: 100%;
  height: calc(100vh - 106px);
  display: flex;
  flex-direction: column;

  .kpi-body {
    position: relative;
    z-index: 11;
    width: 100%;
    height: calc(100% - 106px);
    display: flex;
    padding-top: 8px;
    .panel-left {
      height: 100%;
      width: calc(50% - 5px);
    }
    .panel-right {
      height: 100%;
      width: calc(50% - 5px);
    }
    .panel-width-empty {
      width: 0 !important;
    }
    .panel-width-full {
      width: calc(100% - 10px) !important;
    }
    .panel-middle {
      width: 4px;
      margin: 0 1px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      border-top: 2px solid #e8eaec;
      border-bottom: 2px solid #e8eaec;

      .panel-btn-left,
      .panel-btn-right {
        height: 70px;
        background-color: #d1d1d1;
        position: relative;
        cursor: pointer;
      }

      .panel-btn-left {
        border-top-left-radius: 10px;
        border-bottom-left-radius: 10px;
        &::before {
          content: '';
          width: 0;
          height: 0;
          border-top: 4px solid transparent;
          border-right: 4px solid #555;
          border-bottom: 4px solid transparent;
          position: absolute;
          top: 0;
          right: 0px;
          bottom: 0;
          margin: auto;
        }
      }
      .panel-btn-right {
        border-top-right-radius: 10px;
        border-bottom-right-radius: 10px;
        &::before {
          content: '';
          width: 0;
          height: 0;
          border-top: 4px solid transparent;
          border-left: 4px solid #555;
          border-bottom: 4px solid transparent;
          position: absolute;
          top: 0;
          left: 0px;
          bottom: 0;
          margin: auto;
        }
      }
    }
  }
}

.kpi-header {
  // overflow: hidden;
  height: 100px;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    width: 100%;
    height: 50px;
    background-color: #fff;
    top: 100%;
    left: 0;
    z-index: 10;
  }
}

.kpi-progress {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;

  .progress-bg {
    width: 0;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    background-color: rgba(26, 212, 12, 0.6);
    transition: all 0.4s cubic-bezier(0.08, 0.82, 0.17, 1) 0s;
  }
}

.vxe-table--header-wrapper {
  overflow: initial;
}

.header-title {
  background: red;
}

.kpi-table-header {
  background: #f8f8f9;
  font-size: 14px;
  font-weight: bold;
  color: #606266;
  font-family: -apple-system, BlinkMacSystemFont, Segoe UI, PingFang SC, Hiragino Sans GB, Microsoft YaHei,
    Helvetica Neue, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol;

  [flex],
  &[flex] {
    display: flex;
  }

  [align="left"]{
    align-items: flex-start;
  }

  [column-padding] {
    padding-left: 18px;
    padding-right: 18px;
  }

  .kpi-column {
    border: 1px solid #e8eaec;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    min-height: 50px;
  }

  .kpi-cell {
    width: max-content;
    padding-top: 11px;
    padding-bottom: 11px;
  }
}
</style>