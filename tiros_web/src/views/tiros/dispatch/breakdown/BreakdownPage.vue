<template>
  <div class='bodyWrapper'>
    <!-- 查询区域 -->
    <div class='table-page-search-wrapper'>
      <a-form layout='inline' :form='form'>
        <a-row :gutter='24'>
          <a-col :md='4' :sm='24'>
            <a-form-item label='来源'>
              <j-dict-select-tag
                v-model='queryParam.fromType'
                placeholder='请选择'
                dictCode='fault_from_type'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='线路'>
              <line-select-list v-model='lineId'></line-select-list>
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
            <a-form-item label='处理'>
              <j-dict-select-tag
                v-model='queryParam.status'
                placeholder='请选择'
                dictCode='bu_fault_status'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='12'>
            <a-form-item label='时间'>
              <j-dict-select-tag
                v-model='queryParam.dateType'
                placeholder='请选择'
                dictCode='bu_date_type'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='12'
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
          <a-col :md='4' :sm='12' v-if="queryParam.dateType==='2'">
            <a-form-item>
              <j-dict-select-tag
                v-model='queryParam.quarter'
                placeholder='请选择'
                dictCode='bu_quarter_type'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='12' v-if="queryParam.dateType==='3'">
            <a-form-item>
              <a-month-picker placeholder='选择月份' v-model='month'>
              </a-month-picker>
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='12' v-if="queryParam.dateType==='4'">
            <a-form-item>
              <a-range-picker v-model='rangeDate' :defaultPickerValue='defaultDateRange'>
              </a-range-picker>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter='24' :style="{ display: expand ? 'block' : 'none' }">
          <a-col :md='4' :sm='24'>
            <a-form-item label='工班'>
              <j-dict-select-tag
                v-model='reportGroupId'
                placeholder='请选择'
                :dictCode='dictGroupStr'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-model-item label='部件'>
              <a-select
                style='width:100%'
                allowClear
                placeholder='请选择'
                v-model='faultAssetName'
                :open='false'
                :showArrow='true'
                @change='allowClearCHange'
                @dropdownVisibleChange='showAssetModal'
                ref='faultAssetSelect'
              >
                <a-icon slot='suffixIcon' type='ellipsis' />
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='工位'>
              <j-dict-select-tag
                v-model='queryParam.workstationId'
                placeholder='请选择'
                :dictCode='dictCodeStrWork'
              />
            </a-form-item>
          </a-col>
          <!--<a-col :md='4' :sm='24'>
            <a-form-item label='位置'>
              &lt;!&ndash;              <j-dict-select-tag
                              v-model="queryParam.placeId"
                              placeholder="请选择"
                              dictCode="bu_train_place,place_name,id"
                            />&ndash;&gt;
              <a-input v-model='queryParam.placeId' placeholder='请输入'></a-input>
            </a-form-item>
          </a-col>-->
          <a-col :md='4' :sm='24'>
            <a-form-item label='等级'>
              <j-dict-select-tag
                v-model='queryParam.level'
                placeholder='请选择'
                dictCode='bu_fault_level'
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter='24' :style="{ display: expand ? 'block' : 'none' }">
          <!--          <a-col :md="9" :sm="24">-->
          <a-col :md='4' :sm='24'>
            <a-form-item label='正线'>
              <!--              <a-switch v-model="online" dictCode="bu_state" @change="handleState1"/>-->
              <j-dict-select-tag
                v-model='queryParam.online'
                placeholder='请选择'
                dictCode='bu_state'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='有责'>
              <!--              <a-switch v-model="hasDuty" dictCode="bu_state" @change="handleState2"/>-->
              <j-dict-select-tag
                v-model='queryParam.hasDuty'
                placeholder='请选择'
                dictCode='bu_state'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='委外'>
              <!--              <a-switch v-model="outsource" dictCode="bu_state" @change="handleState3"/>-->
              <j-dict-select-tag
                v-model='queryParam.outsource'
                placeholder='请选择'
                dictCode='bu_state'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
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
        <a-row :gutter='24' :style="{ display: expand ? 'block' : 'none' }">
          <a-col :md='4' :sm='24'>
            <a-form-item label='阶段'>
              <j-dict-select-tag
                v-model='queryParam.phase'
                placeholder='请选择'
                dictCode='bu_fault_phase'
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='系统'>
              <j-dict-select-tag
                v-model='queryParam.sysId'
                placeholder='请选择'
                dictCode='bu_train_asset_type,name,id,struct_type=1 and parent_id is null'
              />
            </a-form-item>
          </a-col>
          <a-col :md='8' :sm='24'>
            <a-form-item label='列计划'>
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
        </a-row>
        <a-row :gutter='24' :style="{ display: expand ? 'block' : 'none' }">
          <a-col :md='4' :sm='24'>
            <a-form-item label='当前环节'>
              <a-select
                show-search
                allowClear
                placeholder='请选择当前环节'
                v-model='queryParam.wfStatus'
              >
                <a-select-option :value='undefined'> 请选择</a-select-option>
                <a-select-option :value='item' v-for='(item, k) in faultWfStatusData' :key='k'>
                  {{ item }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col>
            <span class='table-page-search-submitButtons'>
              <a-space>
                <a-button @click='findList'>查询</a-button>
                <a-button @click='resetSearch'>重置</a-button>
                <a-button type='primary' @click='handleAdd'>新增</a-button>
                <a-button v-has="'dispatch:list:import'" @click='$refs.importModal.show()'>导入</a-button>
                <a-button v-has="'dispatch:wf:sync'" @click='syncFaultFromWorkshopDb'
                          :loading='syncFaultFromWorkshopDbLoading'>同步历史质保</a-button>
                <a-button v-has="'dispatch:list:edit'" :disabled='records.length !== 1'
                          @click.stop='handleEdit(records[0])'>编辑</a-button>
                <!--              <a-button :disabled="records.length != 1" @click="addWorkOrder">安排工单</a-button>-->
                <!--              <a-button :disabled="records.length < 1" @click="closeWorkOrder">关闭故障</a-button>-->
                <a-button v-has="'dispatch:wf:sync'" :disabled='!(records.length === 1 && records[0].fromType === 3)'
                          @click='deleteRecord'>删除</a-button>
                <!-- 取消 故障暂时去掉，因为取消后停止流程的功能还没有 -->
                <!--              <a-button :disabled="records.length < 1" @click='cancelRecord'>取消故障</a-button>-->
                <ProcessButtons
                  ref='WfButtons'
                  :can-start='true'
                  :solution-code="'WF_FAULT_HANDLE'"
                  v-if='selectedRow && 1 === selectedRow.fromType'
                  @StartSuccess='onStartSuccess'
                  @StartFailure='onStartFailure'
                  @handleSuccess='onHandleSuccess'
                  @cancelSuccess='findList'
                  :business-key='selectedRow.id'
                  :business-title='selectedRow.faultDesc'
                  :process-instance-id='selectedRow.processInstanceId'
                  :variables='variables'
                  :title='selectedRow.wfStatus'
                ></ProcessButtons>
                <!--<a-button @click="">导入故障</a-button>-->
              <a-button><a :style="{fontSize: '12px'}" @click='toggle'>
              更多条件 <a-icon :type="expand ? 'up' : 'down'" />
              </a></a-button>
              </a-space>
          </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div :style='tableHeight.top'>
      <vxe-table
        border
        :max-height='tableHeight.size'
        :style='tableHeight.bottom'
        ref='listTable'
        :align='allAlign'
        :data='tableData'
        show-overflow='tooltip'
        @checkbox-change='checkboxChange'
        @checkbox-all='checkboxChange'
        :edit-config="{trigger: 'manual', mode: 'row'}"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
      >
        <vxe-table-column fixed='left' type='checkbox' width='40'></vxe-table-column>
        <vxe-table-column field='faultSn' title='故障编号' width='140'>
          <template v-slot='{ row }'>
            <a @click.stop='faultDetail(row)'>{{ row.faultSn }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field='fromType_dictText' title='来源' width='120'></vxe-table-column>
        <vxe-table-column field='lineName' title='线路' width='80'></vxe-table-column>
        <vxe-table-column field='trainNo' title='车号' width='80'></vxe-table-column>
        <vxe-table-column field='sysName' title='故障系统' width='180'></vxe-table-column>
        <vxe-table-column field='faultAssetName' align='left' header-align='center' title='故障部件'
                          min-width='100'></vxe-table-column>
        <vxe-table-column field='faultDesc' title='故障描述' align='left' min-width='180'
                          header-align='center'></vxe-table-column>
        <vxe-table-column field='happenTime' title='发现时间' width='100'></vxe-table-column>
        <vxe-table-column field='reportUserName' align='left' header-align='center' title='提报人员'
                          width='100'></vxe-table-column>
        <vxe-table-column field='reportTime' title='提报时间' width='100'></vxe-table-column>
        <vxe-table-column field='status_dictText' title='故障状态' align='left' header-align='center'
                          width='80'></vxe-table-column>
        <vxe-table-column field='handleStatus_dictText' title='处理结果' align='left' header-align='center'
                          width='80'></vxe-table-column>
        <vxe-table-column field='wfStatus' align='left' header-align='center' title='当前环节' width='120'>
        </vxe-table-column>
        <vxe-table-column
          field='processCandidate'
          title='当前处理人'
          align='left'
          header-align='center'
          width='160'
        ></vxe-table-column>
        <vxe-table-column field='workOrderId ' title='来源工单' width='100'>
          <template v-slot='{ row }'>
            <a v-if='row.workOrderId' @click.stop='preViewOrderDetail(row.workOrderId)'>查看</a>
          </template>
        </vxe-table-column>
        <vxe-table-column title='处理工单' width='100'>
          <template v-slot='{ row }'>
            <a v-if='row.handleOrderId' @click.stop='preViewOrderDetail(row.handleOrderId)'>查看</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field='handleOrderId' title='已安排?' width='80' :formatter='formatValue'>
        </vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync='queryParam.pageNo'
        :page-size.sync='queryParam.pageSize'
        :total='totalResult'
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change='handlePageChange'
      ></vxe-pager>
    </div>
    <BreakdownModal ref='breakdownModal' @ok='loadData()'></BreakdownModal>
    <breakdown-detail-modal ref='breakdownDetail'></breakdown-detail-modal>
    <TrainPlanList ref='trainPlanModal' @ok='onSelectPlan'></TrainPlanList>
    <!-- <train-asset-list ref='assetSelect'
        :multiple='false'
        :canSelectType='[3,4]'
        @ok='addFaultAsset'></train-asset-list> -->
    <!-- <CarDeviceSelect
      :canSelectType="[3, 4]"
      :trainNo="queryParam.trainNo"
      ref="assetSelect"
      :multiple="false"
      @ok="addFaultAsset"
    /> -->
    <CarDeviceSelectNew
      :canSelectType='[3, 4]'
      :trainNo='queryParam.trainNo'
      :lineId='queryParam.lineId'
      ref='assetSelect'
      :multiple='false'
      @ok='addFaultAsset'
    />
    <edit-work-order-modal ref='workOrderModal' @ok='loadData'></edit-work-order-modal>
    <view-work-order-modal ref='viewOrderModal'></view-work-order-modal>
    <DispatchImport ref='importModal' url='/import/fault/history' @ok='findList()' />
  </div>
</template>

<script>
import DispatchImport from '@views/tiros/material/modules/DispatchImport'
import moment from 'moment'
import {
  cancelBreakdown,
  closeBreakdown,
  delBreakdown,
  getBreakdownInfo,
  getBreakdownList,
  listFaultWfStatus
} from '@api/tirosDispatchApi'
import EditWorkOrderModal from '@views/tiros/dispatch/workOrder/EditWorkOrderModal'
import BreakdownModal from '@views/tiros/dispatch/breakdown/BreakdownModal'
import BreakdownDetailModal from '@views/tiros/dispatch/breakdown/BreakdownDetailModal'
import { everythingIsEmpty } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { randomString } from '@comp/_util/RandomString'
import ViewWorkOrderModal from '@views/tiros/dispatch/workOrder/ViewWorkOrderModal'
// import TrainAssetList from '@views/tiros/common/selectModules/TrainAssetList'
// import CarDeviceSelect from '@views/tiros/common/selectModules/CarDeviceSelect'
import CarDeviceSelectNew from '@views/tiros/common/selectModules/CarDeviceSelectNew'
import TrainPlanList from '@views/tiros/common/selectModules/TrainPlanList'
import ProcessButtons from '@views/workflow/ProcessButtons'
import axios from 'axios'

export default {
  name: 'BreakdownPage',
  components: {
    DispatchImport,
    CarDeviceSelectNew,
    BreakdownModal,
    ViewWorkOrderModal,
    BreakdownDetailModal,
    LineSelectList,
    EditWorkOrderModal,
    TrainPlanList,
    ProcessButtons
  },
  data() {
    return {
      planName: '',
      records: [],
      dictGroupStr: 'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
      dictCodeStrWork: 'bu_mtr_workstation,name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
      year: null,
      month: '',
      queryParam: {
        fromType: 1,
        planId: '',
        trainNo: '',
        lineId: '',
        faultAssetId: '',
        reportGroupId: '',
        placeId: '',
        online: null,
        hasDuty: null,
        outsource: null,
        canceled: null,
        phase: '',
        status: '',
        level: '',
        sysId: '',
        quarter: '',
        year: '',
        month: '',
        endDate: '',
        startDate: '',
        dateType: '',
        workstationId: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      faultWfStatusData: [],
      expand: false,
      yearPick: null, //年选择器的值
      yearPickShow: false, //年选择器的显示隐藏
      form: this.$form.createForm(this),
      lineId: '',
      reportGroupId: '',
      rangeDate: [],
      trainStructId: '',
      dictTrainStr: '',
      faultAssetName: '',
      tableHeight: {
        top: 'height: calc(100vh - 267px)',
        bottom: 'height: calc(100vh - 267px)',
        size: '100%'
      },
      selectedRow: null,
      variables: { businessCode: '' },
      syncFaultFromWorkshopDbLoading: false
    }
  },
  created() {
    if (sessionStorage.getItem('DEFAULT') === 'true') {
      this.queryParam.status = 0
      sessionStorage.removeItem('DEFAULT')
    }

    this.findList()
  },
  watch: {
    //线路
    lineId(newVal, oldVal) {
      if (newVal) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + newVal + '|train_struct_id'
      } else {
        this.dictTrainStr = ''
      }
    },
    //工班
    reportGroupId(newVal, oldVal) {
      if (newVal) {
        this.dictCodeStrWork = `bu_mtr_workstation,name,id,id in (select workstation_id from bu_group_workstation where group_id='${newVal}')`
      } else {
        this.dictCodeStrWork = 'bu_mtr_workstation,name,id'
      }
    }
  },
  methods: {
    getFaultWfStatusData() {
      listFaultWfStatus().then((res) => {
        this.faultWfStatusData = res.result
      })
    },
    openTrainPlanModal() {
      this.$refs.trainPlanModal.showModal()
      this.$refs.planSelect.blur()
    },
    preViewOrderDetail(id) {
      this.$refs.viewOrderModal.showModal(id)
    },
    clearValue() {
      this.queryParam.planId = ''
      this.planName = ''
    },
    onSelectPlan(data) {
      data.forEach((element) => {
        console.log(element)
        this.queryParam.trainNo = element.trainNo
        this.queryParam.lineId = element.lineId
        this.lineId = element.lineId
        this.queryParam.planId = element.id
        this.planName = element.planName
      })
    },
    checkboxChange(e) {
      this.records = e.records

      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords && selectRecords.length == 1) {
        this.selectedRow = selectRecords[0]
        this.variables.businessCode = this.selectedRow.faultSn
      } else {
        this.selectedRow = null
        this.variables.businessCode = ''
      }
    },
    formatValue({ cellValue }) {
      if (cellValue) {
        return '已安排'
      } else {
        return '未安排'
      }
    },
    handleTrainFocus() {
      if (!this.lineId) this.$message.warn('请先选择线路!')
    },
    handleAssetFocus() {
      if (!this.queryParam.trainNo) this.$message.warn('请先选择车辆!')
    },

    //车辆改变
    handleTrainNo(val, optionData) {
      if (val && optionData.extFields) {
        this.trainStructId = optionData.extFields[0].train_struct_id
      }
    },

    allowClearCHange(e) {
      if (!e) {
        // 清空
        this.queryParam.faultAssetId = undefined
        this.faultAssetName = undefined
      }
    },

    //故障部件选择框弹出
    showAssetModal(e) {
      if (!e) {
        return
      }
      if (!this.queryParam.trainNo) {
        this.$message.warn('请先选择车辆!')
      } else {
        this.$refs.assetSelect.showModal()
        this.$refs.faultAssetSelect.blur()
      }
    },
    //故障部件选择后的回调方法
    addFaultAsset(data) {
      if (!everythingIsEmpty(data)) {
        // this.queryParam.faultAssetId = data[0].id
        // this.faultAssetName = data[0].assetName
        this.queryParam.faultAssetId = data[0].id
        this.faultAssetName = data[0].assetName
      }
    },

    //故障详情
    faultDetail(record) {
      this.$refs.breakdownDetail.show(record.id)
    },
    findList() {
      this.getFaultWfStatusData()

      this.selectedRow = null
      this.records = []
      this.loading = true
      if (this.rangeDate.length > 0 && this.queryParam.dateType === '4') {
        this.queryParam.startDate = moment(this.rangeDate[0]).format('YYYY-MM-DD')
        this.queryParam.endDate = moment(this.rangeDate[1]).format('YYYY-MM-DD')
      }
      if (this.month && this.queryParam.dateType === '3') {
        this.queryParam.year = moment(this.month).year()
        this.queryParam.month = moment(this.month).month() + 1
      }
      this.queryParam.lineId = this.lineId
      this.queryParam.reportGroupId = this.reportGroupId
      getBreakdownList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.records = []
        this.tableData = res.result.records
      })
    },
    // 流程启动成功回调
    onStartSuccess(data) {
      this.findList()
    },
    // 流程启动失败回调
    onStartFailure(data) {
      console.log('启动失败:', data)
    },
    // 流程处理成功
    onHandleSuccess(data) {
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
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData() {
      this.findList()
    },
    handleAdd() {
      // 这里1 表示调度创建  2 工班创建
      this.$refs.breakdownModal.add('', '', 1)
    },
    // 添加故障工单
    addWorkOrder() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length === 0) {
        this.$message.warn('请选择要创建工单的故障记录')
        return
      }

      let canSubs = selectRecords.filter(r => {
        return !!r.handleOrderId
      })
      if (canSubs.length > 0) {
        this.$message.warn('已安排工单的故障不能再次安排')
        return
      }

      let cancalSub = selectRecords.filter(r => {
        return r.submitStatus == 2
      })
      if (cancalSub.length > 0) {
        this.$message.warn('已取消工单的故障不能进行安排')
        return
      }

      let allows = selectRecords.filter(r => {
        return !(r.status === 3 || r.status === 0 || (r.status === 1 && (r.handleStatus === 3 || r.handleStatus === 4)))
      })

      if (allows.length > 0) {
        this.$message.warn('只能对"临时修复"、"未修复"和"遗留故障"发起工单')
        return
      }

      let orderInfo = {
        orderName: selectRecords[0].faultSn + '故障处理',
        orderType: 2,
        lineId: selectRecords[0].lineId,
        trainNo: selectRecords[0].trainNo,
        planId: selectRecords[0].planId,
        planName: selectRecords[0].planName,
        groupId: '',
        workshopId: this.$store.getters.userInfo.workshopId,
        startTime: this.$moment().format('YYYY-MM-DD'),
        finishTime: this.$moment().format('YYYY-MM-DD')
      }

      let tasks = []
      selectRecords.forEach((r, index) => {
        let task = {
          id: randomString(),
          taskType: 2,
          taskType_dictText: '故障任务',
          taskNo: index + 1,
          taskName: r.faultSn + '[故障处理]',
          taskObjId: r.id,
          method: 3,
          assetTypeId: r.subSysId,
          structDetailId: r.trainStructureId,
          trainAssetId: r.faultAssetId,
          taskContent: '处理：' + r.faultDesc
        }
        tasks.push(task)
      })
      this.$refs.workOrderModal.createFaultOrder(orderInfo, tasks)
    },
    handleEdit(record) {
      if (!!record.handleOrderId) {
        this.$message.warn('已安排的故障不能编辑')
        return
      }
      getBreakdownInfo({ id: record.id }).then((res) => {
        let data = res.result
        data['formType'] = 1
        this.$refs.breakdownModal.edit(data)
      })

    },
    toggle() {
      this.expand = !this.expand
      if (this.expand) {
        this.tableHeight = {
          top: 'height: calc(100vh - 387px)',
          bottom: 'height: calc(100vh - 387px)',
          size: '90%'
        }
      } else {
        this.tableHeight = {
          top: 'height: calc(100vh - 267px)',
          bottom: 'height: calc(100vh - 267px)',
          size: '100%'
        }
      }
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            var idsStr = selectRecords
              .map(function(obj, index) {
                return obj.id
              })
              .join(',')
            delBreakdown('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    // 关闭故障
    closeWorkOrder() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let canSubs = selectRecords.filter(r => {
          return r.status == 2
        })
        if (canSubs.length > 0) {
          this.$message.warn('已关闭工单的故障不能关闭')
          return
        }
        canSubs = selectRecords.filter(r => {
          return !!r.handleOrderId
        })
        if (canSubs.length > 0) {
          this.$message.warn('已安排工单的故障不能关闭')
          return
        }
        this.$confirm({
          content: `是否关闭选中${selectRecords.length}故障？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function(obj, index) {
                return obj.id
              })
              .join(',')
            closeBreakdown('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          }
        })
      } else {
        this.$message.warn('尚未选中任何数据!')
      }

    },
    cancelRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let canSubs = selectRecords.filter(r => {
          return !!r.handleOrderId
        })
        if (canSubs.length > 0) {
          this.$message.warn('已安排工单的故障不能取消')
          return
        }
        canSubs = selectRecords.filter((item) => {
          return item.submitStatus == 2  //条件;
        })
        if (canSubs.length > 0) {
          this.$message.warn('已取消的故障不能再次取消')
          return
        }
        this.$confirm({
          content: `是否取消选中${selectRecords.length}数据？`,
          onOk: () => {
            var idsStr = selectRecords
              .map(function(obj, index) {
                return obj.id
              })
              .join(',')
            cancelBreakdown('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    resetSearch() {
      this.queryParam = { pageNo: 1, pageSize: 10 }
      this.faultAssetName = ''
      this.lineId = ''
      this.year = null
      this.reportGroupId = ''
      this.rangeDate = []
      this.clearValue()
    },
    syncFaultFromWorkshopDb() {
      const countUrl = window._CONFIG['syncMaximo'] + '/third/ws-fault/count/all'
      axios({
        url: countUrl,
        // headers: { "X-Access-Token": token },
        method: 'GET'
      }).then((res) => {
        this.syncFaultFromWorkshopDbLoading = false
        if (res.data.success) {
          this.$message.info('查询成功：' + res.data.result)

          this.$confirm({
            content: res.data.result + ` 同步数据耗时较长，确认同步？`,
            onOk: () => {

              this.syncFaultFromWorkshopDbLoading = true
              const syncUrl = window._CONFIG['syncMaximo'] + '/third/ws-fault/sync/all'
              axios({
                url: syncUrl,
                // headers: { "X-Access-Token": token },
                method: 'POST'
              }).then((res) => {
                this.syncFaultFromWorkshopDbLoading = false
                if (res.data.success) {
                  this.$message.info('同步成功：' + res.data.result)
                }
              }).catch((err) => {
                this.syncFaultFromWorkshopDbLoading = false
                this.$notification.error({
                  message: '服务器出错了',
                  description: 'Network Error',
                  duration: 4
                })
              })

            }
          })
        }
      }).catch((err) => {
        this.syncFaultFromWorkshopDbLoading = false
        this.$notification.error({
          message: '服务器出错了',
          description: 'Network Error',
          duration: 4
        })
      })
    }
  }
}
</script>

<style scoped>
.ant-advanced-search-form .ant-form-item {
  display: flex;
}

.ant-advanced-search-form .ant-form-item-control-wrapper {
  flex: 1;
}

#components-form-demo-advanced-search .ant-form {
  max-width: none;
}

#components-form-demo-advanced-search .search-result-list {
  margin-top: 16px;
  border: 1px dashed #e9e9e9;
  border-radius: 6px;
  background-color: #fafafa;
  min-height: 200px;
  text-align: center;
  padding-top: 80px;
}
</style>