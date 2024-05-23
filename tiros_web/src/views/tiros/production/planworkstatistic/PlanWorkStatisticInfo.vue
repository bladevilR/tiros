<template>
  <div id='MonitorContent'>
    <div class='table-page-search-wrapper'>
      <a-form layout='inline' @keyup.enter.native='findList'>
        <a-row :gutter='24' type='flex'>
          <a-col :md='3' :sm='24'>
            <a-form-model-item label='车号' prop='trainNo'>
              <j-dict-select-seach-tag
                :triggerChange='true'
                v-model='queryParam.trainNo'
                :dictCode="'bu_train_info,train_no,train_no'"
              />
            </a-form-model-item>
          </a-col>
          <a-col :md='7' :sm='24'>
            <a-form-item
              label='列计划:'
              :labelCol='{ span: 5 }'
              :wrapperCol='{ span: 18 }'
            >
              <a-select
                v-model='planName'
                placeholder='请选择列计划'
                :open='false'
                :showArrow='true'
                @focus='openTrainPlanModal'
                ref='planSelect'
              >
                <div slot='suffixIcon'>
                  <a-icon
                    v-if='queryParam.planId'
                    @click.stop='clearValue'
                    type='close-circle'
                    style='padding-right: 3px'
                  />
                  <a-icon v-else type='ellipsis' />
                </div>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='班组:'>
              <j-dict-select-tag
                v-model='queryParam.groupId'
                placeholder='请选择'
                :dictCode="'bu_mtr_workshop_group,group_name,id'"
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <span class='table-page-search-submitButtons'>
              <a-space>
                <a-button @click='findList'>查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!--    <div>-->
    <!--      <a-tabs defaultActiveKey='1' size='small' @change='changeTabs'>-->
    <!--        <a-tab-pane key='1' tab='列计划统计' style='height: calc(100vh - 218px); overflow: auto'>-->
    <div class='info-wrapper info-top-wrapper' style='margin-top: 18px'>
      <h4>列计划统计</h4>
      <vxe-table
        border
        resizable
        auto-resize
        ref='FormsListTable'
        :align='allAlign'
        :data='tableDataPlan'
        :cell-style='cellStyle'
        show-overflow='tooltip'
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
      >
        <vxe-table-column field='planName' title='列计划' min-width='180' align='left' header-align='center' />
        <vxe-table-column field='planProgressStatus_dictText' title='进度状态' width='120' align='left'
                          header-align='center' />
        <vxe-table-column field='trainNo' title='车号' width='100' />
        <vxe-table-column field='taskTotal' title='总任务数' width='120' align='right' header-align='center' />
        <vxe-table-column field='taskIssued' title='已下发' width='100' align='right' header-align='center' />
        <vxe-table-column field='taskSuspended' title='已暂停' width='100' align='right' header-align='center' />
        <vxe-table-column field='taskClosed' title='已关闭' width='100' align='right' header-align='center' />
        <vxe-table-column field='taskSubmitted' title='已提交' width='100' align='right' header-align='center' />
        <vxe-table-column field='taskFinishPercent' title='完成比率' width='100' align='right'
                          header-align='center'>
          <template v-slot='{ row }'>
            {{ row.taskFinishPercent + '%' }}
          </template>
        </vxe-table-column>
        <vxe-table-column field='faultFound' title='发现故障' width='100' align='right' header-align='center' />
        <vxe-table-column field='faultHandled' title='解决故障' width='100' align='right' header-align='center' />
      </vxe-table>
    </div>
    <!--        </a-tab-pane>-->
    <!--        <a-tab-pane key='2' tab='各班组统计' style='height: calc(100vh - 218px); overflow: auto'>-->
    <div class='info-wrapper info-top-wrapper' style='margin-top: 18px'>
      <h4>各班组统计</h4>
      <vxe-table
        border
        resizable
        auto-resize
        ref='FormsListTable'
        :align='allAlign'
        :data='tableDataGroup'
        :cell-style='cellStyle'
        show-overflow='tooltip'
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
      >
        <vxe-table-column field='planName' title='列计划' min-width='180' align='left' header-align='center' />
        <vxe-table-column field='planProgressStatus_dictText' title='进度状态' width='120' align='left'
                          header-align='center' />
        <vxe-table-column field='trainNo' title='车号' width='100' />
        <vxe-table-column field='groupName' title='班组' width='120' align='left' header-align='center' />
        <vxe-table-column field='groupTaskNeedWrite' title='应填任务' width='100' align='right'
                          header-align='center' />
        <vxe-table-column field='groupTaskSuspended' title='已暂停' width='100' align='right'
                          header-align='center' />
        <vxe-table-column field='groupTaskClosed' title='已关闭' width='100' align='right'
                          header-align='center' />
        <vxe-table-column field='groupTaskWrote' title='已填任务' width='100' align='right'
                          header-align='center' />
        <vxe-table-column field='groupTaskFinishPercent' title='完成比率' width='100' align='right'
                          header-align='center'>
          <template v-slot='{ row }'>
            {{ row.groupTaskFinishPercent + '%' }}
          </template>
        </vxe-table-column>
        <vxe-table-column field='groupFaultFound' title='发现故障' width='100' align='right'
                          header-align='center' />
        <vxe-table-column field='groupFaultHandled' title='解决故障' width='100' align='right'
                          header-align='center' />
      </vxe-table>
    </div>
    <!--        </a-tab-pane>-->
    <!--      </a-tabs>-->
    <!--    </div>-->
    <TrainPlanList ref='trainPlanModal' :train-no='queryParam.trainNo' @ok='onSelectPlan'></TrainPlanList>
  </div>
</template>

<script>
import { getLastWorkingPlan, listPlanGroupStatistic } from '@api/tirosProductionApi'
import TrainPlanList from '@views/tiros/common/selectModules/TrainPlanList'

export default {
  components: { TrainPlanList },
  data() {
    return {
      planName: null,
      queryParam: {
        planId: '',
        groupId: '',
        trainNo: ''
      },
      tableDataPlan: [],
      tableDataGroup: [],
      allAlign: 'center',
      selectedTabKey: '1',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 19 }
      },
      progressStatusColor: {
        // 未开始
        '0': {
          backgroundColor: '#A9A9A9',
          color: '#fff'
        },
        // 正常(作业中)
        '1': {
          backgroundColor: '#1E90FF',
          color: '#fff'
        },
        // 逾期(作业中)
        '2': {
          backgroundColor: '#ee8c60',
          color: '#fff'
        },
        // 正常(完工)
        '3': {
          backgroundColor: '#008000',
          color: '#fff'
        },
        // 逾期(完工)
        '4': {
          backgroundColor: '#bccb1c',
          color: '#fff'
        },
        // 提前(完工)
        '5': {
          backgroundColor: '#1cff00',
          color: '#fff'
        },
        // 暂停中
        '6': {
          backgroundColor: '#817659',
          color: '#fff'
        }
      }
    }
  },
  created() {
    this.findLastWorkingPlanList()
  },
  watch: {},
  methods: {
    cellStyle({ row, rowIndex, column, columnIndex }) {
      if (['planProgressStatus_dictText'].indexOf(column.property) > -1) {
        let style = this.progressStatusColor[row.planProgressStatus]
        return { color: style.backgroundColor }
      }
    },
    openTrainPlanModal() {
      this.$refs.trainPlanModal.showModal()
      this.$refs.planSelect.blur()
    },
    onSelectPlan(data) {
      data.forEach((element) => {
        this.queryParam.planId = element.id
        this.planName = element.planName
      })
    },
    clearValue() {
      this.queryParam.planId = ''
      this.planName = ''
    },
    findLastWorkingPlanList() {
      getLastWorkingPlan().then((res) => {
        if (res.success) {
          this.loading = false
          this.queryParam.planId = res.result.id
          this.planName = res.result.planName
          this.queryParam.trainNo = res.result.trainNo

          this.findList()
        }
      })
    },
    findList() {
      listPlanGroupStatistic(this.queryParam).then((res) => {
        if (res.success) {
          this.loading = false
          this.tableDataPlan = res.result.planList
          this.tableDataGroup = res.result.groupList
        }
      })
    },
    changeTabs(key) {
      this.selectedTabKey = key
      if (key === '1') {
        this.findList()
      } else if (key === '2') {
        this.findList()
      }
    }
  },
  computed: {}
}
</script>

<style scoped>
.info-wrapper {
  border: 1px solid #eee;
  position: relative;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 10px;
}

.info-wrapper h4 {
  position: absolute;
  top: -14px;
  padding: 1px 8px;
  margin-left: 16px;
  color: #777;
  border-radius: 2px 2px 0 0;
  background: #fff;
  font-size: 16px;
  width: auto;
}
</style>