<template>
  <!-- 故障选择弹窗-->
  <a-modal
    width='90%'
    title='故障记录选择'
    centered
    :visible='visible'
    :bodyStyle="{ height: '80vh' }"
    @ok='handleOk'
    @cancel='handleCancel'
    cancelText='关闭'
    :destroyOnClose='true'
  >
    <!-- 查询区域 -->
    <div class='table-page-search-wrapper'>
      <a-form layout='inline' :form='form'>
        <a-row :gutter='24'>
          <a-col :md='3' :sm='24'>
            <a-form-item label='来源'>
              <j-dict-select-tag
                v-model='queryParam.fromType'
                placeholder='请选择'
                dictCode='fault_from_type'
              />
            </a-form-item>
          </a-col>
          <a-col :md='3' :sm='24'>
            <a-form-item label='线路'>
              <!--              <j-dict-select-tag
                              v-model="lineId"
                              dictCode="bu_mtr_line,line_name,line_id"
                            />-->
              <line-select-list v-model='lineId'></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md='3' :sm='24'>
            <a-form-item label='车辆'>
              <j-dict-select-seach-tag
                v-model='queryParam.trainNo'
                :dictCode='dictCodeStr'
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
              <a-range-picker v-model='rangeDate' :defaultPickerValue='defaultDateRange'>
              </a-range-picker>
            </a-form-item>
          </a-col>
          <a-col :md='7' :sm='8'>
            <span style='float: left;overflow: hidden;' class='table-page-search-submitButtons'>
              <a-space>
             <a-button @click='findList'>查询</a-button>
             <a-button><a :style="{fontSize: '12px'}" @click='toggle'>
              更多条件 <a-icon :type="expand ? 'up' : 'down'" />
            </a></a-button>
              </a-space>
          </span>
          </a-col>

        </a-row>
        <a-row :gutter='24' :style="{ display: expand ? 'block' : 'none' }"
        >
          <a-col :md='5' :sm='24'>
            <a-form-item label='工班'>
              <j-dict-select-tag
                v-model='reportGroupId'
                placeholder='请选择'
                :dictCode='dictGroupStr'
              />
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='系统'>
              <j-dict-select-tag
                v-model='queryParam.sysId'
                placeholder='请选择'
                dictCode='bu_train_asset_type,name,id,struct_type=1 and parent_id is null'
              />
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='部件'>
              <j-dict-select-tag
                v-model='queryParam.faultAssetId'
                placeholder='请选择'
                dictCode='bu_tools_status'
              />
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='工位'>
              <j-dict-select-tag
                v-model='queryParam.workstationId'
                placeholder='请选择'
                :dictCode='dictCodeStrWork'
              />
            </a-form-item>
          </a-col>
          <a-col :md='5' :sm='24'>
            <a-form-item label='位置'>
              <j-dict-select-tag
                v-model='queryParam.placeId'
                placeholder='请选择'
                dictCode='bu_train_place,place_name,id'
              />
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
          <a-col :md='9' :sm='24'>
            <a-col :md='8' :sm='24'>
              <a-form-item label='是否正线'>
                <a-switch v-model='online' dictCode='bu_state' @change='handleState1' />
              </a-form-item>
            </a-col>
            <a-col :md='8' :sm='24'>
              <a-form-item label='是否有责'>
                <a-switch v-model='hasDuty' dictCode='bu_state' @change='handleState2' />
              </a-form-item>
            </a-col>
            <a-col :md='8' :sm='24'>
              <a-form-item label='是否委外'>
                <a-switch v-model='outsource' dictCode='bu_state' @change='handleState3' />
              </a-form-item>
            </a-col>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- 列表区域 -->
    <div style='height: calc(80vh - 130px)'>
      <vxe-table
        border
        max-height='100%'
        style='height: calc(80vh - 130px)'
        ref='listTable'
        :align='allAlign'
        :data='tableData'
        show-overflow='tooltip'
        :radio-config="!multiple ? {trigger: 'row'} : {}"
        :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true } : {}"
        :edit-config="{trigger: 'click', mode: 'cell', showIcon:'true'}"
      >
        <vxe-table-column v-if='multiple' type='checkbox' width='40'></vxe-table-column>
        <vxe-table-column v-else type='radio' width='40'></vxe-table-column>
        <vxe-table-column field='faultSn' title='故障编号' width='140'></vxe-table-column>
        <vxe-table-column field='fromType_dictText' title='来源' width='120'></vxe-table-column>
        <vxe-table-column field='lineName' title='线路' width='100'></vxe-table-column>
        <vxe-table-column field='trainNo' title='车号' width='80'></vxe-table-column>
        <vxe-table-column field='sysName' title='故障系统' width='180'></vxe-table-column>
        <vxe-table-column field='faultAssetName' title='故障部件' width='180'></vxe-table-column>
        <vxe-table-column field='faultDesc' title='故障描述' align='left'></vxe-table-column>
        <vxe-table-column field='happenTime' title='发现时间' width='120'></vxe-table-column>
        <vxe-table-column field='reportUserName' title='提报人员' width='100'></vxe-table-column>
        <vxe-table-column field='reportTime' title='提报时间' width='120'></vxe-table-column>
        <vxe-table-column field='status_dictText' title='状态' width='100'></vxe-table-column>
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
  </a-modal>
</template>

<script>
import moment from 'moment'
import { getBreakdownList } from '@api/tirosDispatchApi'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'FaultSelectList',
  components: { LineSelectList },
  props: {
    multiple: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      dictGroupStr: 'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
      visible: false,
      dictCodeStr: 'bu_train_info,train_no,train_no,status=1',
      dictCodeStrWork: 'bu_mtr_workstation,name,id',
      year: null,
      queryParam: {
        fromType: 1,
        trainNo: '',
        lineId: '',
        faultAssetId: '',
        reportGroupId: '',
        placeId: '',
        online: 0,
        hasDuty: 0,
        outsource: 0,
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
      online: false,
      hasDuty: false,
      outsource: false,
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      expand: false,
      yearPick: null, //年选择器的值
      yearPickShow: false, //年选择器的显示隐藏
      form: this.$form.createForm(this),
      lineId: '',
      reportGroupId: '',
      rangeDate: []
    }
  },
  watch: {
    lineId(newVal, oldVal) {
      if (newVal) {
        this.dictCodeStr = 'bu_train_info,train_no,train_no,line_id=' + newVal
      } else {
        this.dictCodeStr = 'bu_train_info,train_no,train_no'
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
    showModal(lineId, trainNo, groupId) {
      if (lineId) {
        this.queryParam.lineId = lineId
      }
      if (trainNo) {
        this.queryParam.trainNo = trainNo
      }
      if (groupId) {
        this.queryParam.reportGroupId = groupId
      }
      this.visible = true
      this.findList()
    },
    handleOk() {
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords()
      } else {
        if (this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      this.$emit('ok', selectRecords)
      this.close()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },

    findList() {
      this.loading = true
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

      // 只有故障状态为0或者3时
      this.queryParam.status = 0,
        getBreakdownList(this.queryParam).then((res) => {
          this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result.records
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
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleState1(checked1) {
      if (checked1) {
        this.queryParam.online = 1
      } else {
        this.queryParam.online = 0
      }
    },
    handleState2(checked2) {
      if (checked2) {
        this.queryParam.hasDuty = 1
      } else {
        this.queryParam.hasDuty = 0
      }
    },
    handleState3(checked3) {
      if (checked3) {
        this.queryParam.outsource = 1
      } else {
        this.queryParam.outsource = 0
      }
    },
    toggle() {
      this.expand = !this.expand
    }
  }
}
</script>

<style scoped>

</style>