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
          <a-col :md='6' :sm='12' v-if="queryParam.dateType==='4'">
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
          <!-- <a-col :md="4" :sm="24">
            <a-form-item label="位置">
              <a-input  v-model="queryParam.placeId"  placeholder="请输入"></a-input>
            </a-form-item>
          </a-col> -->
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
          <a-col :md='4' :sm='24'>
            <a-form-item label='阶段'>
              <j-dict-select-tag
                v-model='queryParam.phase'
                placeholder='请选择'
                dictCode='bu_fault_phase'
              />
            </a-form-item>
          </a-col>
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
          <!--          </a-col>-->
        </a-row>
        <a-row :gutter='24' :style="{ display: expand ? 'block' : 'none' }">
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
        <a-row>
          <a-col>
            <span class='table-page-search-submitButtons'>
              <a-space>
              <a-button @click='findList'>查询</a-button>
                <a-button @click='resetSearch'>重置</a-button>
              <a-button :disabled='!(records.length ==1 && records[0].status!==2)' @click.stop='handleEdit(records[0])'>编辑</a-button>
              <a-button :disabled='records.length !== 1' @click='createExceptionTransfer'>例外转序</a-button>
              <a-button :disabled='records.length < 1' @click='deleteRecord'>取消</a-button>
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
        <vxe-table-column type='checkbox' width='40'></vxe-table-column>
        <vxe-table-column field='faultSn' title='故障编号' width='140'>
          <template v-slot='{ row }'>
            <a @click.stop='faultDetail(row)'>{{ row.faultSn }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field='fromType_dictText' title='来源' width='120'></vxe-table-column>
        <vxe-table-column field='lineName' title='线路' width='80'></vxe-table-column>
        <vxe-table-column field='trainNo' title='车号' width='80'></vxe-table-column>
        <vxe-table-column field='sysName' title='故障系统' min-width='100'></vxe-table-column>
        <vxe-table-column field='faultAssetName' align='left' header-align='center' title='故障部件'
                          min-width='100'></vxe-table-column>
        <vxe-table-column field='faultDesc' title='故障描述' min-width='160' align='left'
                          header-align='center'></vxe-table-column>
        <vxe-table-column field='happenTime' title='发现时间' width='100'></vxe-table-column>
        <vxe-table-column field='reportUserName' align='left' header-align='center' title='提报人员'
                          width='100'></vxe-table-column>
        <vxe-table-column field='reportTime' title='提报日期' width='100'></vxe-table-column>
        <vxe-table-column field='status_dictText' align='left' header-align='center' title='故障状态'
                          width='80'></vxe-table-column>
        <vxe-table-column field='submitStatus_dictText' title='提交状态' width='80'></vxe-table-column>
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
    <!--    <safe-anticipate-modal ref="modalForm" @ok="loadData()"></safe-anticipate-modal>-->
    <BreakdownModal ref='breakdownModal' @ok='loadData()'></BreakdownModal>
    <breakdown-detail-modal ref='breakdownDetail'></breakdown-detail-modal>
    <TrainPlanList ref='trainPlanModal' @ok='onSelectPlan'></TrainPlanList>
    <!-- <train-asset-list ref='assetSelect' :multiple='false' :canSelectType='[3,4]'
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
  </div>
</template>

<script>
import moment from 'moment'
import { cancelBreakdown, getBreakdownInfo } from '@api/tirosDispatchApi'
import BreakdownModal from '@views/tiros/dispatch/breakdown/BreakdownModal'
import BreakdownDetailModal from '@views/tiros/dispatch/breakdown/BreakdownDetailModal'

import { everythingIsEmpty } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { getFaultPage } from '@api/tirosOutsourceApi'
import TrainAssetList from '@views/tiros/common/selectModules/TrainAssetList'
// import CarDeviceSelect from '@views/tiros/common/selectModules/CarDeviceSelect'
import CarDeviceSelectNew from '@views/tiros/common/selectModules/CarDeviceSelectNew'
import TrainPlanList from '@views/tiros/common/selectModules/TrainPlanList'

export default {
  name: 'BreakDownPage',
  components: {
    BreakdownModal,
    BreakdownDetailModal,
    TrainAssetList,
    LineSelectList,
    CarDeviceSelectNew,
    TrainPlanList
  },
  data() {
    return {
      month: undefined,
      planName: '',
      records: [],
      date: null,
      dictGroupStr: 'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
      dictCodeStrWork: 'bu_mtr_workstation,name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
      year: null,
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
      count: '',
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
      }
    }
  },
  watch: {
    lineId(newVal, oldVal) {
      if (newVal) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + newVal + '|train_struct_id'
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
  created() {
    this.findList()
  },
  methods: {
    openTrainPlanModal() {
      this.$refs.trainPlanModal.showModal()
      this.$refs.planSelect.blur()
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
      console.log(e)
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
        this.queryParam.faultAssetId = data[0].id
        this.faultAssetName = data[0].assetName
      }
    },
    faultDetail(record) {
      this.$refs.breakdownDetail.show(record.id)
      // getBreakdownInfo({ id: record.id }).then((res) => {
      //   let data = res.result
      //   this.$refs.breakdownDetail.show(data)
      // })
    },
    findList() {
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
      getFaultPage(this.queryParam).then((res) => {
        if (res.result) {
          this.totalResult = res.result.total
          this.records = []
          this.tableData = res.result.records
        }
        this.loading = false
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData() {
      this.findList()
    },

    handleEdit(record) {
      getBreakdownInfo({ id: record.id }).then((res) => {
        let data = res.result
        data['formType'] = 1
        this.$refs.breakdownModal.edit(data)
      })
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let temp = selectRecords.filter((item) => item.status !== 0)
        if (temp.length > 0) {
          this.$message.warn('已关闭或者已处理的故障不可以取消')
          return
        }
        let isCancel = selectRecords.filter((item) => item.submitStatus === 2)
        if (isCancel.length > 0) {
          this.$message.warn('所选数据存在故障已经取消,无需再取消!')
          return
        }
        this.$confirm({
          content: `是否取消选中${selectRecords.length}数据？`,
          onOk: () => {
            let idsStr = selectRecords
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
    createExceptionTransfer() {
      const selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (!selectRecords || selectRecords.length !== 1) {
        this.$message.warning('请选择一条故障记录')
        return
      }
      const fault = selectRecords[0]
      if (!fault.workOrderId) {
        this.$message.warning('所选故障未关联工单，无法发起例外转序')
        return
      }
      this.$router.push({
        path: '/tiros/quality/exception-transfer',
        query: {
          create: '1',
          faultId: fault.id || '',
          faultSn: fault.faultSn || '',
          orderId: fault.workOrderId || '',
          orderTaskId: fault.orderTaskId || '',
          processName: fault.sysName || '',
          transferDesc: fault.faultDesc || ''
        }
      })
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
    resetSearch() {
      this.queryParam = { pageNo: 1, pageSize: 10 }
      this.faultAssetName = ''
      this.lineId = ''
      this.year = null
      this.rangeDate = []
      this.clearValue()
    }
  }
}
</script>

<style scoped>

</style>
