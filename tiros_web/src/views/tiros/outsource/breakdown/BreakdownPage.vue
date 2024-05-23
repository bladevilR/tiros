<template>
  <div id='errorMonitorContent'>
    <div class='table-page-search-wrapper' v-if='searchFormShow'>
      <a-form layout='inline' @keyup.enter.native='findList'>
        <a-row :gutter='24'>
          <a-col :md='3' :sm='24'>
            <a-form-item label='来源'>
              <j-dict-select-tag
                v-model="queryParam.fromType"
                placeholder="请选择"
                dictCode="fault_from_type"
              />
            </a-form-item>
          </a-col>
          <a-col :md='3' :sm='24'>
            <a-form-item label='线路'>
              <line-select-list v-model="lineId" @changeName="changeLineName"> </line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md='3' :sm='24'>
            <a-form-item label='车辆'>
              <j-dict-select-seach-tag
                v-model='queryParam.trainNo'
                :dictCode='dictTrainStr'
                :trigger-change="true"
                @focus='handleTrainFocus'
                @change='handleTrainNo'
              />
            </a-form-item>
          </a-col>
          <a-col :md='3' :sm='12'>
            <a-form-item label='时间'>
              <j-dict-select-tag
                v-model='queryParam.dateType'
                placeholder='请选择'
                dictCode='bu_date_type'
              />
            </a-form-item>
          </a-col>
          <a-col :md='3' :sm='12'
                 v-if="(queryParam.dateType==='1'||queryParam.dateType===1||queryParam.dateType==='2')">
            <a-form-item>
              <a-date-picker
                mode='year'
                placeholder='请选择年份'
                format='YYYY'
                v-model='year'
                :open='yearPickShow'
                @panelChange='handlePanelChange'
                @openChange='handleOpenChange'
                style='width:100%'
              />
            </a-form-item>
          </a-col>
          <a-col :md='3' :sm='12' v-if="queryParam.dateType==='2'">
            <a-form-item>
              <j-dict-select-tag
                v-model='queryParam.quarter'
                placeholder='请选择'
                dictCode='bu_quarter_type'
              />
            </a-form-item>
          </a-col>
          <a-col :md='3' :sm='12' v-if="queryParam.dateType==='3'">
            <a-form-item>
              <a-month-picker placeholder='选择月份' v-model='queryParam.month'>
              </a-month-picker>
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='12' v-if="queryParam.dateType==='4'">
            <a-form-item>
              <a-range-picker v-model='rangeDate' :defaultPickerValue="defaultDateRange" >
              </a-range-picker>
            </a-form-item>
          </a-col>
          <a-col :md='7' :sm='8'>
            <span style='float: left;overflow: hidden;' class='table-page-search-submitButtons'>
              <a-space>
                 <a-button @click='findList'>查询</a-button>
               <a-button @click='resetSearch'>重置</a-button>
            <a-button><a :style="{fontSize: '12px'}" @click='toggle'>
              更多条件 <a-icon :type="expand ? 'up' : 'down'" />
            </a></a-button>
              </a-space>
          </span>
          </a-col>
        </a-row>
        <a-row :gutter='24'
               :style="{ display: expand ? 'block' : 'none' }"
        >
          <a-col :md='5' :sm='24'>
            <a-form-item label='工班'>
              <j-dict-select-tag
                v-model='queryParam.reportGroupId'
                placeholder='请选择'
                :dictCode='dictGroupStr'
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
<!--          <a-col :md='5' :sm='24'>
            <a-form-item label='工位'>
              <j-dict-select-tag
                v-model='queryParam.workstationId'
                placeholder='请选择'
                :dictCode='dictCodeStrWork'
              />
            </a-form-item>
          </a-col>-->
          <a-col :md='5' :sm='24'>
            <a-form-item label='位置'>
              <!--              <j-dict-select-tag
                              v-model="queryParam.placeId"
                              placeholder="请选择"
                              dictCode="bu_train_place,place_name,id"
                            />-->
              <a-input v-model='queryParam.placeId' placeholder='请输入'></a-input>
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='等级'>
              <j-dict-select-tag
                v-model='queryParam.level'
                placeholder='请选择'
                dictCode='bu_fault_level'
              />
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='完成'>
              <j-dict-select-tag
                v-model='queryParam.status'
                placeholder='请选择'
                dictCode='bu_fault_status'
              />
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='阶段'>
              <j-dict-select-tag
                v-model='queryParam.phase'
                placeholder='请选择'
                dictCode='bu_fault_phase'
              />
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='正线'>
              <!--              <a-switch v-model="online" dictCode="bu_state" @change="handleState1"/>-->
              <j-dict-select-tag
                v-model='queryParam.online'
                placeholder='请选择'
                dictCode='bu_state'
              />
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='有责'>
              <!--              <a-switch v-model="hasDuty" dictCode="bu_state" @change="handleState2"/>-->
              <j-dict-select-tag
                v-model='queryParam.hasDuty'
                placeholder='请选择'
                dictCode='bu_state'
              />
            </a-form-item>
          </a-col>
<!--          <a-col :md='5' :sm='24'>
            <a-form-item label='委外'>
              &lt;!&ndash;              <a-switch v-model="outsource" dictCode="bu_state" @change="handleState3"/>&ndash;&gt;
              <j-dict-select-tag
                v-model='queryParam.outsource'
                placeholder='请选择'
                dictCode='bu_state'
              />
            </a-form-item>
          </a-col>-->
          <a-col :md='5' :sm='24'>
            <a-form-item label='取消'>
              <!--              <a-switch v-model="canceled" dictCode="bu_state" @change="handleState4"/>-->
              <j-dict-select-tag
                v-model='queryParam.canceled'
                placeholder='请选择'
                dictCode='bu_state'
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div>
      <a-tabs size='small' @change='changeTabs'>
        <a-tab-pane key='1' tab='故障明细'>
          <p>
            当前总故障数： <span class='redFont'>{{ count.total }}</span> 条，其中
            <span class='redFont'>{{ count.handled }}</span> 条已处理，
            <span class='redFont'>{{ count.unhandled }}</span> 条未处理
          </p>
          <div :style='tableHeight.top'>
            <vxe-table
              border
              :max-height='tableHeight.size'
              :style='tableHeight.bottom'
              ref='listTable'
              :align='allAlign'
              :data='tableData'
              show-overflow='tooltip'
              :edit-config="{trigger: 'manual', mode: 'row'}"
              :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
            >
              <vxe-table-column type='checkbox' width='40'></vxe-table-column>
              <vxe-table-column field='faultSn' title='故障编号' width='140'></vxe-table-column>
              <vxe-table-column field='fromType_dictText' title='来源' width='120'></vxe-table-column>
              <vxe-table-column field='lineName' title='线路' width='100'></vxe-table-column>
              <vxe-table-column field='trainNo' title='车号' width='100'></vxe-table-column>
              <vxe-table-column field='sysName' title='故障系统' min-width='120'></vxe-table-column>
              <vxe-table-column field='faultAssetName' title='故障部件' min-width='120'></vxe-table-column>
              <vxe-table-column field='faultDesc' title='故障描述' min-width='140' align='left'
                                header-align='center'></vxe-table-column>
              <vxe-table-column field='happenTime' title='发现时间' width='100'></vxe-table-column>
              <vxe-table-column field='reportUserName' title='提报人员' width='100'></vxe-table-column>
              <vxe-table-column field='reportTime' title='提报日期' width='100'></vxe-table-column>
              <vxe-table-column field='status_dictText' title='状态' width='80'></vxe-table-column>
            </vxe-table>
            <vxe-pager
              perfect
              :current-page.sync='queryParam.pageNo'
              :page-size.sync='queryParam.pageSize'
              :total='totalResult'
              :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
              @page-change='handlePageChange'
            ></vxe-pager>
            <train-structure-list ref='assetSelect' :multiple='false' :canSelectType='[3,4]'
                                  @ok='addFaultAsset'></train-structure-list>
          </div>
        </a-tab-pane>
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
        <a-tab-pane key='3' tab='比较分析'>
          <compare-out-modal></compare-out-modal>
        </a-tab-pane>
      </a-tabs>
    </div>
  </div>
</template>

<script>
import { getcountFault, getFaultPage, getcountFaultSystem } from '../../../../api/tirosOutsourceApi'
import BarHorizontal from '@/components/chart/BarHorizontal'
import Pie from '@/components/chart/Pie'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import moment from 'moment'
import CompareOutModal from './compareOutModal'
import { everythingIsEmpty } from '@/utils/util'
import TrainStructureList from '@views/tiros/common/selectModules/TrainStructureList'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'BreakdownPage',
  mixins: [JeecgListMixin],
  components: { CompareOutModal, BarHorizontal, Pie, TrainStructureList ,LineSelectList},
  data() {
    return {
      dictGroupStr: 'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
      dictCodeStrWork: 'bu_mtr_workstation,name,id',
      queryParam: {
        fromType: 1,
        lineId: '',
        trainNo: '',
        reportGroupId: '',
        sysId: '',
        faultAssetId: '',
        quarter: '',
        year: '',
        month: '',
        endDate: '',
        startDate: '',
        dateType: '',
        phase: '',
        status: '',
        level: '',
        placeId: '',
        online: null,
        hasDuty: null,
        canceled: null,
        workstationId: '',
        pageNo: 1,
        pageSize: 10
      },
      yearPick: null, //年选择器的值
      yearPickShow: false, //年选择器的显示隐藏
      year: null,
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
      searchFormShow: true,
      count: {
        total: null,
        handled: null,
        unhandled: null
      },
      trainStructId: '',
      dictTrainStr: '',
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
      tableHeight:{
        top: "height: calc(100vh - 260px)",
        bottom: "height: calc(100vh - 310px)",
        size: "90%",
      },
    }
  },
  created() {
    this.getTotalCount()
    this.findList()
  },
  watch: {
    lineId(newVal, oldVal) {
      if (newVal) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + newVal+ '|train_struct_id'
      } else {
        this.dictTrainStr = ''
      }
    },
    reportGroupId(newVal, oldVal) {
      if (newVal) {
        this.dictCodeStrWork = `bu_mtr_workstation,name,id,id in (select workstation_id from bu_group_workstation where group_id='${newVal}')`
      } else {
        this.dictCodeStrWork = 'bu_mtr_workstation,name,id'
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
      if (val&&optionData.extFields) {
        this.trainStructId = optionData.extFields[0].train_struct_id
      }
    },
    //故障部件选择框弹出
    showAssetModal(e) {
      console.log(e)
      if (!this.queryParam.trainNo) {
        this.$message.warn('请先选择车辆!')
      } else {
        this.$refs.assetSelect.showModal(this.trainStructId)
        this.$refs.faultAssetSelect.blur()
      }
    },
    //故障部件选择后的回调方法
    addFaultAsset(data) {
      if (!everythingIsEmpty(data)) {
        this.queryParam.faultAssetId = data[0].id
        this.faultAssetName = data[0].name
      }
    },
    findList() {
      if (this.rangeDate.length > 0 && this.queryParam.dateType === '4') {
        this.queryParam.startDate = moment(this.rangeDate[0]).format('YYYY-MM-DD')
        this.queryParam.endDate = moment(this.rangeDate[1]).format('YYYY-MM-DD')
      }
      if (this.queryParam.month && this.queryParam.dateType === '3') {
        this.queryParam.year = moment(this.queryParam.month).year()
        this.queryParam.month = moment(this.queryParam.month).month() + 1
      }
      this.queryParam.lineId = this.lineId
      this.queryParam.reportGroupId = this.reportGroupId
      getFaultPage(this.queryParam).then(res => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
          this.queryParam.lineId = this.lineId
          getcountFaultSystem(this.queryParam).then(res => {
            this.chartCountList = res.result.systemItemList
            this.totalCount = res.result.total

          })
        }
      })
    },
    toggle() {
      this.expand = !this.expand
      if(this.expand){
        this.tableHeight={
          top: "height: calc(100vh - 390px)",
          bottom: "height: calc(100vh - 440px)",
          size: "85%",
        }
      }else {
        this.tableHeight={
          top: "height: calc(100vh - 260px)",
          bottom: "height: calc(100vh - 310px)",
          size: "90%",
        }
      }
    },
    getTotalCount() {
      getcountFault(this.queryParam).then(res => {
        this.count = res.result
      })
    },
    changeDepotNAme(name) {
      this.depotName = name
    },
    changeLineName(name) {
      this.lineName = name
    },
    changeTabs(key) {
      if (key === '1') {
        this.searchFormShow = true
      } else if (key == '2') {
        this.searchFormShow = true
        if (this.rangeDate.length > 0 && this.queryParam.dateType === '4') {
          this.queryParam.startDate = moment(this.rangeDate[0]).format('YYYY-MM-DD')
          this.queryParam.endDate = moment(this.rangeDate[1]).format('YYYY-MM-DD')
        }
        if (this.queryParam.month && this.queryParam.dateType === '3') {
          this.queryParam.year = moment(this.queryParam.month).year()
          this.queryParam.month = moment(this.queryParam.month).month() + 1
        }
        this.queryParam.lineId = this.lineId
        getcountFaultSystem(this.queryParam).then(res => {
          this.chartCountList = res.result.systemItemList
          this.totalCount = res.result.total
        })
      } else {
        this.chartCountList = []
        this.searchFormShow = false
      }
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    // 弹出日历和关闭日历的回调
    handleOpenChange(
      status) {
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
      this.rangeDate = []
    }
  },
  computed: {
    title() {
      return this.depotName + this.lineName + (this.queryParam.trainNo ? this.queryParam.trainNo + '车' : '') || ''
    },
    barDataSource() {
      return this.chartCountList.map(item => {
        return {
          item: item.systemName,
          count: item.faultCount
        }
      })
    },
    pieDataSource() {
      return this.chartCountList.map(item => {
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

<style scoped>

</style>