<template>
  <div id='errorMonitorContent'>
    <div class='table-page-search-wrapper' v-if='searchFormShow'>
      <a-form layout='inline' @keyup.enter.native='findList'>
        <a-row :gutter='24'>
          <a-col :md='4' :sm='24'>
            <a-form-item label='车辆段'>
              <j-dict-select-tag
                v-model='queryParam.depotId'
                placeholder='请选择'
                dictCode='bu_mtr_depot,name,id'
                @changeName='changeDepotName'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='线路'>
              <line-select-list v-model='lineId' @changeName='changeLineName'></line-select-list>
            </a-form-item>
          </a-col>

          <a-col :md='4' :sm='24'>
            <a-form-item label='车辆'>
              <j-dict-select-seach-tag
                v-model='queryParam.trainNo'
                :dictCode='dictTrainStr'
                :trigger-change='true'
                @focus='handleTrainFocus'
                @change='handleTrainNo'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='工班'>
              <j-dict-select-tag v-model='queryParam.groupId' placeholder='请选择' :dictCode='dictGroupStr' />
            </a-form-item>
          </a-col>

          <a-col :md='3' :sm='8'>
            <span style='float: left; overflow: hidden' class='table-page-search-submitButtons'>
              <a-space>
                <a-button @click='getFaultTabDetail'>查询</a-button>
                <a-button @click='resetSearch'>重置</a-button>
                <a-button>
                  <a :style="{ fontSize: '12px' }" @click='toggle'>
                    更多条件 <a-icon :type="expand ? 'up' : 'down'" />
                  </a>
                </a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
        <a-row :gutter='24' :style="{ display: expand ? 'block' : 'none' }">
          <a-col :md='5' :sm='24'>
            <a-form-item label='系统'>
              <j-dict-select-tag
                :triggerChange='true'
                v-model='queryParam.sysId'
                placeholder='请选择'
                :dictCode='dictSysIdStr'
              />
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-model-item label='部件'>
              <a-select
                placeholder='请选择'
                v-model='faultAssetName'
                :open='false'
                :showArrow='true'
                @focus='showAssetModal'
                ref='faultAssetSelect'
              >
                <a-icon slot='suffixIcon' type='ellipsis' />
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :md='4' :sm='12'>
            <a-form-item label='发现时间'>
              <j-dict-select-tag v-model='queryParam.dateType' placeholder='请选择' dictCode='bu_date_type' />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='12' v-if="queryParam.dateType === '1' || queryParam.dateType === '2'">
            <a-form-item>
              <a-date-picker
                mode='year'
                placeholder='请选择年份'
                format='YYYY'
                v-model='year'
                :open='yearPickShow'
                @panelChange='handlePanelChange'
                @openChange='handleOpenChange'
                style='width: 100%'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='12' v-if="queryParam.dateType === '2'">
            <a-form-item>
              <j-dict-select-tag v-model='queryParam.quarter' placeholder='请选择' dictCode='bu_quarter_type' />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='12' v-if="queryParam.dateType === '3'">
            <a-form-item>
              <a-month-picker placeholder='选择月份' v-model='month' @change='handleMonthChange'></a-month-picker>
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='12' v-if="queryParam.dateType === '4'">
            <a-form-item>
              <a-range-picker v-model='rangeDate' :defaultPickerValue='defaultDateRange'></a-range-picker>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style='height: calc(100vh - 350px)'>
      <a-tabs defaultActiveKey='2' size='small' @change='changeTabs'>
        <a-tab-pane key='2' tab='统计'>
          <a-row v-if='chartCountList.length'>
            <a-col :span='12'>
              <bar-horizontal :title='title' :sub-title="'故障分布情况'" :dataSource='barDataSource' />
            </a-col>
            <a-col :span='12'>
              <pie :title='title' :sub-title="'系统故障占比'" :dataSource='pieDataSource' />
            </a-col>
          </a-row>
        </a-tab-pane>
        <a-tab-pane key='1' tab='故障明细'>
          <p>
            当前总故障数： <span class='redFont'>{{ count.total }}</span> 条，其中
            <span class='redFont'>{{ count.handled }}</span> 条已处理，
            <span class='redFont'>{{ count.unhandled }}</span> 条未处理
          </p>
          <div>
            <vxe-table
              border
              resizable
              :max-height='tableHeight.size'
              :style='tableHeight.bottom'
              ref='FormsListTable'
              :align='allAlign'
              :data='tableData'
              show-overflow='tooltip'
              :edit-config="{ trigger: 'manual', mode: 'row' }"
              :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
            >
              <vxe-table-column type='checkbox' width='40'></vxe-table-column>
              <vxe-table-column field='faultSn' title='故障编号' width='140'></vxe-table-column>
              <vxe-table-column field='fromType_dictText' title='来源' width='120'></vxe-table-column>
              <vxe-table-column field='lineName' title='线路' width='80'></vxe-table-column>
              <vxe-table-column field='trainNo' title='车号' width='80'></vxe-table-column>
              <vxe-table-column field='sysName' title='故障系统' width='120'></vxe-table-column>
              <vxe-table-column
                field='faultAssetName'
                title='故障部件'
                align='left'
                header-align='center'
                width='120'
              ></vxe-table-column>
              <vxe-table-column
                field='faultDesc'
                title='故障描述'
                min-width='120'
                align='left'
                header-align='center'
              ></vxe-table-column>
              <vxe-table-column field='happenTime' title='发现时间' width='100'></vxe-table-column>
              <vxe-table-column field='reportUserName' title='提报人员' width='100'></vxe-table-column>
              <vxe-table-column field='reportTime' title='提报日期' width='100'></vxe-table-column>
              <vxe-table-column field='status_dictText' title='状态' width='80'></vxe-table-column>
            </vxe-table>
            <train-asset-list
              ref='assetSelect'
              :multiple='false'
              :canSelectType='[3, 4]'
              @ok='addFaultAsset'
            ></train-asset-list>
          </div>
          <vxe-pager
            perfect
            :current-page.sync='queryParam.pageNo'
            :page-size.sync='queryParam.pageSize'
            :total='totalResult'
            :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
            @page-change='handlePageChange'
          ></vxe-pager>
        </a-tab-pane>

        <!-- <a-tab-pane key="3" tab="比较分析">
          <compare-model></compare-model>
        </a-tab-pane> -->
      </a-tabs>
    </div>
  </div>
</template>

<script>
import { getFaultCount, getFaultList, getFaultStatistics } from '@/api/tirosProductionApi'
// import BarHorizontal from '@/components/chart/BarHorizontal'
import BarHorizontal from '@/components/echart/BarHorizontal'
// import Pie from '@/components/chart/Pie'
import Pie from '@/components/echart/Pie'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import moment from 'moment'
import CompareModel from './compareModal'
import { everythingIsEmpty } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import TrainAssetList from '@views/tiros/common/selectModules/TrainAssetList'

export default {
  mixins: [JeecgListMixin],
  components: { CompareModel, BarHorizontal, Pie, LineSelectList, TrainAssetList },
  data() {
    return {
      dictTrainStr: '',
      dictGroupStr:
        'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
      dictSysIdStr: 'bu_train_asset_type,name,id,struct_type=1 and parent_id is null',
      queryParam: {
        depotId: '',
        lineId: '',
        trainNo: '',
        groupId: '',
        sysId: '',
        assetId: '',
        quarter: '',
        year: '',
        month: '',
        endDate: '',
        startDate: '',
        dateType: '',
        pageNo: 1,
        pageSize: 10
      },
      yearPick: null, //年选择器的值
      yearPickShow: false, //年选择器的显示隐藏
      year: null,
      month: null,
      url: {},
      depotName: '',
      lineName: '',
      lineId: '',
      chartCountList: [],
      totalResult: 0,
      tableData: [],
      rangeDate: [],
      allAlign: 'center',
      expand: false,
      count: {
        total: null,
        handled: null,
        unhandled: null
      },
      trainStructId: '',
      faultAssetName: '',
      formLeft: this.$form.createForm(this),
      formRight: this.$form.createForm(this),
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 19 }
      },
      selectedTabKey: '1',
      searchFormShow: true,
      tableHeight: {
        top: 'height: calc(100vh - 260px)',
        bottom: 'height: calc(100vh - 310px)',
        size: '100%'
      }
    }
  },
  created() {
    this.getFaultStatisticsDetail()
  },
  watch: {
    lineId(newVal, oldVal) {
      if (newVal) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + newVal + '|train_struct_id'
      } else {
        this.dictTrainStr = ''
      }
    },
    'queryParam.dateType': {
      immediate: true,
      deep: true,
      handler(value) {
        this.queryParam.quarter = ''
        this.queryParam.year = ''
        this.queryParam.month = ''
        this.queryParam.endDate = ''
        this.queryParam.startDate = ''
        this.year = null
        this.month = null
      }
    }
  },
  methods: {
    handleTrainFocus() {
      if (!this.lineId) this.$message.warn('请先选择线路!')
    },
    handleAssetFocus() {
      if (!this.queryParam.trainNo) this.$message.warn('请先选择车辆!')
    },
    //车辆改变
    handleTrainNo(val, optionData) {
      if (val && optionData.extFields && optionData.extFields.length) {
        this.trainStructId = optionData.extFields[0].train_struct_id
      }
    },
    //故障部件选择框弹出
    showAssetModal(e) {
      if (!this.queryParam.trainNo) {
        this.$message.warn('请先选择车辆!')
      } else {
        this.$refs.assetSelect.showModal(this.queryParam.trainNo, this.trainStructId)
        this.$refs.faultAssetSelect.blur()
      }
    },
    //故障部件选择后的回调方法
    addFaultAsset(data) {
      console.log(data)
      if (!everythingIsEmpty(data)) {
        this.queryParam.assetId = data[0].id
        this.faultAssetName = data[0].assetName
      }
    },
    getFaultTabDetail() {
      if (this.selectedTabKey === '1') {
        this.getFaultDetail()
      } else if (this.selectedTabKey === '2') {
        this.getFaultStatisticsDetail()
      }
    },
    getFaultDetail() {
      this.getTotalCount()
      this.findList()
    },
    setQueryParam() {
      if (this.rangeDate.length > 0 && this.queryParam.dateType === '4') {
        this.queryParam.startDate = moment(this.rangeDate[0]).format('YYYY-MM-DD')
        this.queryParam.endDate = moment(this.rangeDate[1]).format('YYYY-MM-DD')
      }
      if (this.month && this.queryParam.dateType === '3') {
        this.queryParam.year = moment(this.month).year()
        this.queryParam.month = moment(this.month).month() + 1
      }
      this.queryParam.lineId = this.lineId
    },
    getTotalCount() {
      this.setQueryParam()
      getFaultCount(this.queryParam).then((res) => {
        if (res.success) {
          this.count = res.result
        }
      })
    },
    findList() {
      this.setQueryParam()
      getFaultList(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
        }
      })
    },
    toggle() {
      this.expand = !this.expand
      if (this.expand) {
        this.tableHeight = {
          top: 'height: calc(100vh - 300px)',
          bottom: 'height: calc(100vh - 350px)',
          size: '90%'
        }
      } else {
        this.tableHeight = {
          top: 'height: calc(100vh - 260px)',
          bottom: 'height: calc(100vh - 310px)',
          size: '100%'
        }
      }
    },
    changeDateType(type) {
    },
    changeDepotName(name) {
      this.depotName = name
    },
    changeLineName(name) {
      this.lineName = name
    },
    changeTabs(key) {
      this.selectedTabKey = key
      if (key === '1') {
        this.searchFormShow = true

        this.getFaultDetail()
      } else if (key === '2') {
        this.searchFormShow = true

        this.getFaultStatisticsDetail()
      } else if (key === '3') {
        this.searchFormShow = false

        // this.sysDataList = []
      }
    },
    getFaultStatisticsDetail() {
      this.setQueryParam()
      getFaultStatistics(this.queryParam).then((res) => {
        this.chartCountList = res.result.systemItemList
        this.totalCount = res.result.total
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    // 弹出日历和关闭日历的回调
    handleOpenChange(status) {
      this.yearPickShow = status
    },
    // 得到年份选择器的值
    handlePanelChange(value) {
      this.year = value
      this.queryParam.year = moment(value).format('YYYY')
      this.yearPickShow = false
    },
    resetSearch() {
      this.queryParam = { pageNo: 1, pageSize: 10 }
      this.faultAssetName = ''
      this.lineId = ''
      this.year = null
      this.month = null
      this.rangeDate = []
    },
    handleMonthChange(value) {
      if (everythingIsEmpty(value)) {
        this.queryParam.year = ''
        this.queryParam.month = ''
      }
    }
  },
  computed: {
    title() {
      return this.depotName + this.lineName + (this.queryParam.trainNo ? this.queryParam.trainNo + '车' : '') || ''
    },
    barDataSource() {
      return this.chartCountList.map((item) => {
        return {
          item: item.systemName,
          count: item.faultCount
        }
      })
    },
    pieDataSource() {
      return this.chartCountList.map((item) => {
        return {
          item: item.systemName,
          // percent: Math.round((item.faultCount / this.totalCount) * 10000) / 100
          count: item.faultCount
        }
      })
    }
  }
}
</script>

<style lang='less'>
</style>
