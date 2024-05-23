<template>
  <div class="bodyWrapper" style="padding: 8px">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="loadData">
        <a-row :gutter="24" type="flex">
          <a-col :md="6" :sm="24">
            <a-form-item label="列计划">
              <a-select
                v-model="planName"
                placeholder="请选择列计划"
                :open="false"
                :showArrow="true"
                @focus="openTrainPlanModal"
                ref="planSelect"
              >
                <div slot="suffixIcon">
                  <a-icon
                    v-if="queryParam.planId"
                    @click.stop="clearValue"
                    type="close-circle"
                    style="padding-right: 3px"
                  />
                  <a-icon v-else type="ellipsis" />
                </div>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="24">
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId" @change="onLineChange"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="24">
            <a-form-item label="车辆">
              <j-dict-select-seach-tag @focus="handleSysFocus" v-model="queryParam.trainNo" :dictCode="dictTrainStr" />
            </a-form-item>
          </a-col>
          <a-col :md='7' :sm='24'>
            <na-select-date :defaultType="1" @change="dateChange"></na-select-date>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-space>
              <a-button @click="init_sheet">查询</a-button>
              <a-button @click="downloadExcel">导出</a-button>
              <a-button>
                <a :style="{ fontSize: '12px' }" @click="toggle">
                  更多条件 <a-icon :type="moreVisible ? 'up' : 'down'" /> </a
              ></a-button>
            </a-space>
          </a-col>
        </a-row>
        <a-row :gutter="24" :style="{ display: moreVisible ? 'block' : 'none' }">
          <a-col :md="5" :sm="24">
            <a-form-item label="工班">
              <j-dict-select-tag v-model="queryParam.reportGroupId" placeholder="请选择" :dictCode="dictGroupStr" />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="系统">
              <j-dict-select-tag
                :triggerChange="true"
                v-model="queryParam.sysId"
                placeholder="请选择"
                :dictCode="dictSysIdStr"
              />
              <!--                @change="handleDicTrainAsset"
                @focus="handleFocusAssetType"-->
            </a-form-item>
          </a-col>
          <!--          <a-col :md="5" :sm="24">
            <a-form-item label="部件">
              <a-select v-if="queryParam.sysId" v-model="queryParam.faultAssetId" allowClear>
                <a-select-option v-for="item in assetTypePart" :key="item.id" :value="item.id">
                  {{ item.assetName }}
                </a-select-option>
              </a-select>
              <j-dict-select-tag v-else
                                 v-model="queryParam.faultAssetId"
                                 placeholder="请选择"
                                 :dictCode="dicTrainAsset"
                                 @focus="handleFocusSysId"
              />
            </a-form-item>
          </a-col>-->
          <a-col :md="5" :sm="24">
            <a-form-model-item label="部件">
              <a-select
                placeholder="请选择"
                v-model="faultAssetName"
                :open="false"
                :showArrow="true"
                @change="allowClearCHange"
                @dropdownVisibleChange="showAssetModal"
                ref="faultAssetSelect"
                allowClear
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="工位">
              <j-dict-select-tag v-model="queryParam.workstationId" placeholder="请选择" :dictCode="dictCodeStrWork" />
            </a-form-item>
          </a-col>
          <!-- <a-col :md="5" :sm="24">
            <a-form-item label="位置">
              <j-dict-select-tag
                v-model="queryParam.placeId"
                placeholder="请选择"
                dictCode="bu_train_place,place_name,id"
              />
            </a-form-item>
          </a-col> -->
          <a-col :md="5" :sm="24">
            <a-form-item label="等级">
              <j-dict-select-tag v-model="queryParam.level" placeholder="请选择" dictCode="bu_fault_level" />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="阶段">
              <j-dict-select-tag v-model="queryParam.phase" placeholder="请选择" dictCode="bu_fault_phase" />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag v-model="queryParam.status" placeholder="请选择" dictCode="bu_fault_status" />
            </a-form-item>
          </a-col>
          <!-- <a-col :md="5" :sm="24">
            <a-form-item label="是否正线">
              <j-switch v-model="queryParam.online" :options="['1', '0']"></j-switch>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="是否有责">
              <j-switch v-model="queryParam.hasDuty" :options="['1', '0']"></j-switch>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="是否委外">
              <j-switch v-model="queryParam.outsource" :options="['1', '0']"></j-switch>
            </a-form-item>
          </a-col> -->
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
          <a-col :md='5' :sm='24'>
            <a-form-item label='委外'>
              <!--              <a-switch v-model="outsource" dictCode="bu_state" @change="handleState3"/>-->
              <j-dict-select-tag
                v-model='queryParam.outsource'
                placeholder='请选择'
                dictCode='bu_state'
              />
            </a-form-item>
          </a-col>

          <!-- <a-col :md='5' :sm='24'>
            <a-form-item label='取消'>
              <j-dict-select-tag
                v-model='queryParam.canceled'
                placeholder='请选择'
                dictCode='bu_state'
              />
            </a-form-item>
          </a-col> -->
        </a-row>
      </a-form>
    </div>
    <!-- <train-structure-list
      ref="assetSelect"
      :multiple="false"
      :canSelectType="[3, 4]"
      @ok="addFaultAsset"
    ></train-structure-list> -->
    <!-- <CarDeviceSelect
      :canSelectType="[3, 4]"
      :trainNo="queryParam.trainNo"
      ref="assetSelect"
      :multiple="false"
      @ok="addFaultAsset"
    /> -->
    <CarDeviceSelectNew
      :canSelectType="[3, 4]"
      :trainNo="queryParam.trainNo"
      :lineId="queryParam.lineId"
      ref="assetSelect"
      :multiple="false"
      @ok="addFaultAsset"
    />
    <TrainPlanList ref="trainPlanModal" @ok="onSelectPlan"></TrainPlanList>
    <div class="table-page-body" :class="{ 'more-visible': moreVisible }">
      <div id="luckysheet"></div>
    </div>
  </div>
</template>

<script>
import { exportExcel } from '@views/tiros/util/export'
import JSwitch from '@comp/jeecg/JSwitch'
import NaSelectDate from '@comp/tiros/Na-select-date'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import luckyexcelUtil from '../util/LuckyexcelUtil'
import { getTrainFaultList } from '@api/tirosReportApi'
import TrainPlanList from '@views/tiros/common/selectModules/TrainPlanList'
import { everythingIsEmpty } from '@/utils/util'
// import TrainStructureList from '@views/tiros/common/selectModules/TrainStructureList'
// import CarDeviceSelect from '@views/tiros/common/selectModules/CarDeviceSelect'
import CarDeviceSelectNew from '@views/tiros/common/selectModules/CarDeviceSelectNew'

export default {
  name: 'fault_detail',
  components: { JSwitch, LineSelectList, NaSelectDate,CarDeviceSelectNew, TrainPlanList },
  data() {
    return {
      moreVisible: false,
      yearOpen: false,
      yearValue: this.$moment(new Date()),
      faultAssetName: '',
      dictTrainStr: '',
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" + this.$store.getters.userInfo.workshopId + "'|workshop_id",
      dictCodeStr: 'bu_train_info,train_no,train_no,status=1',
      dictCodeStrWork:
        "bu_mtr_workstation,name,id,workshop_id='" + this.$store.getters.userInfo.workshopId + "'|workshop_id",
      dictSysIdStr: 'bu_train_asset_type,name,id,struct_type=1 and parent_id is null',
      dicTrainAsset: 'bu_train_asset,asset_name,id,(struct_type!=1 or struct_type!=2)',
      planName:'',
      queryParam: {
        year: this.$moment(new Date()).format('YYYY'),
        trainNo: '',
        depotId: this.$store.getters.userInfo.depotId,
        lineId: this.getInitLineId(),
        workshopId: this.$store.getters.userInfo.workshopId,
        faultAssetId: '',
        reportGroupId: '',
        planId:'',
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
        month: '',
        endDate: '',
        startDate: '',
        dateType: '',
        workstationId: '',
      },
    }
  },
  mounted() {
    // this.$set(this.queryParam, 'lineId', this.$store.getters.userInfo.lineId)
    this.onLineChange(this.queryParam.lineId)
    // this.init_sheet()
  },
  beforeDestroy() {
    // 离开销毁Luckysheet
    luckysheet.destroy()
  },
  methods: {
    onSelectPlan(data) {
      data.forEach((element) => {
        console.log(element)
        this.queryParam.trainNo = element.trainNo;
        this.queryParam.lineId = element.lineId;
        this.lineId = element.lineId;
        this.queryParam.planId = element.id
        this.planName = element.planName
      })
    },
    openTrainPlanModal() {
      this.$refs.trainPlanModal.showModal()
      this.$refs.planSelect.blur()
    },
    clearValue() {
      this.queryParam.planId = ''
      this.planName = ''
    },
    handleSysFocus() {
      if (!this.queryParam.lineId) this.$message.warn('请先选择线路!')
    },
    toggle() {
      this.moreVisible = !this.moreVisible
      setTimeout(() => {
        if (luckysheet.getAllSheets().length > 0) {
          luckysheet.resize()
        }
      }, 300)
    },
    check() {
      if (!this.queryParam.workshopId) {
        this.$message.warn('当前人员没有车间')
        return false
      }
      return true
    },
    changeYear(value, mode) {
      this.yearValue = value
      this.queryParam.year = value.format('YYYY')
      this.yearOpen = false
    },
    getInitLineId() {
      if (this.$store.getters.userInfo.lineId.indexOf(',') > -1) {
        return this.$store.getters.userInfo.lineId.split(',')[0]
      } else {
        return this.$store.getters.userInfo.lineId
      }
    },
    dateChange(value) {
      this.queryParam.dateType = value.dateType
      this.queryParam.year = value.year
      this.queryParam.quarter = value.quarter
      this.queryParam.month = value.month
      this.queryParam.startDate = value.dateRef[0]
      this.queryParam.endDate = value.dateRef[1]
    },
    onLineChange(val, option) {
      let id = ''
      if (val) {
        id = val
      } else {
        id = ''
      }
      this.dictTrainStr = "bu_train_info,train_no,train_no,status=1 and line_id='" + id + "'"
      this.faultAssetName = ''
      this.queryParam.faultAssetId = ''
    },
    init_sheet() {
      getTrainFaultList(this.queryParam).then((res) => {
        if (res.success) {
          const op = Object.assign({}, luckyexcelUtil.getDefaultOptions())
          const len = res.result.length
          op.data = [JSON.parse(faultDetailSheet)]
          let cellData = []
          cellData.push(...op.data[0].celldata)

          // op.data[0].row = (len + 1) < 42 ? 42 : (len + 1)
          op.data[0].config.borderInfo[0].range[0].row = [0, len]
          // res.result.forEach((e, index) => {
          //   //序号
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'A', Number(index) + 1)
          //   //故障分类
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'B', e.phase_dictText)
          //   //发生日期
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'C', e.happenTime, {
          //     ct: { fa: '@', t: 's' },
          //   })
          //   //车号
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'D', e.trainNo, {
          //     ct: { fa: '@', t: 's' },
          //   })
          //   //所属系统
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'E', e.sysName)
          //   //所属工位
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'F', e.workstationName)
          //   //故障位置
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'G', e.trainStructureName)
          //   //车辆故障详细描述
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'H', e.faultDesc)
          //   //车辆故障详细处理措施
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'I', e.handleDesc)
          //   //故障等级
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'J', e.faultLevel_dictText)
          //   //故障影响
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'K', '')
          //   //完成情况
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'L', e.status_dictText)
          //   //故障类别
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'M', e.categoryCodeDes)
          //   //关闭日期
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'N', e.closeTime, {
          //     ct: { fa: '@', t: 's' },
          //   })
          //   //处理人
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'O', e.handlePerson)
          //   //更换固件名称
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'P', '')//e.faultAssetName
          //   //是否有责
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'Q', e.hasDuty_dictText)
          //   //是否委外件故障
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'R', e.outsource_dictText)
          //   //规程内要求情况
          //   luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'S', '')
          // })
          res.result.forEach((e, index) => {
            //序号
            luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'A', Number(index) + 1)
            //故障分类
            luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'B', e.phase_dictText)
            //发生日期
            luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'C', e.happenTime, {
              ct: { fa: '@', t: 's' },
            })
            //车号
            luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'D', e.trainNo, {
              ct: { fa: '@', t: 's' },
            })
            //所属系统
            luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'E', e.sysName)
            //所属工位
            luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'F', e.workstationName)
            //车辆故障详细描述
            luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'G', e.faultDesc)
            //车辆故障详细处理措施
            luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'H', e.handleDesc)
            //故障等级
            luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'I', e.faultLevel_dictText)
            //处理情况：故障状态=已关闭或已处理时，统一显示已处理
            luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'J', (e.status_dictText === '已处理' || e.status_dictText === '已关闭') ? '已处理' : e.status_dictText)
            //处理日期
            luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'K', e.handleTime, {
              ct: { fa: '@', t: 's' },
            })
            //处理人
            luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'L', e.handlePerson)
            // //是否委外件故障
            // luckyexcelUtil.setCellValueByArrayData(cellData, index + 1, 'M', e.outsource_dictText)
          })
          op.data[0].celldata = cellData
          luckysheet.destroy()
          luckysheet.create(op)
        } else {
          this.$message.error('查询故障明细错误：', res.message)
        }
      })
    },
    loadData() {
      if (this.check()) {
        this.init_sheet()
      }
    },
    downloadExcel() {
      exportExcel(luckysheet.getAllSheets(), this.queryParam.year + '年' + this.queryParam.trainNo + '车故障明细')
    },
    //故障部件选择框弹出
    showAssetModal(e) {
      if (!this.queryParam.trainNo) {
        this.$message.warn('请先选择车辆!')
      } else {
        this.$refs.assetSelect.showModal()
        this.$refs.faultAssetSelect.blur()
      }
    },

    allowClearCHange(e){
      if(!e){
        // 清空
        this.queryParam.faultAssetId = undefined
        this.faultAssetName = undefined
      }
    },

    //故障部件选择后的回调方法
    addFaultAsset(data) {
      if (!everythingIsEmpty(data)) {
        // this.queryParam.faultAssetId = data[0].structDetailId
        // this.faultAssetName = data[0].structDetailName
        this.queryParam.faultAssetId = data[0].id
        this.faultAssetName = data[0].assetName
      }
    },
  },
}

// var faultDetailSheet = `{"name":"故障明细","column":19,"row":60,"data":[],"visibledatarow":[38,58,78,98,118,138,158,178,198,218,238,258,278,298,318,338,358,378,398,418,438,458,478,498,518,538,558,578,598,618,638,658,678,698,718,738,758,778,798,818,838,858,878,898,918,938,958,
// 978,998,1018,1038,1058,1078,1098,1118,1138,1158,1178,1198,1218],"visibledatacolumn":[74,148,222,296,422,496,570,756,958,1032,1106,1180,1254,1365,1455,1575,1719,1875,2017],"ch_width":2137,"rh_height":1298,"luckysheet_select_save":[{"left":0,"width":73,"top":38,"height":19,"left_move":0,"width_move":73,"top_move":38,"height_move":19,"row":[1,1],"column":[0,0],"row_focus":1,"column_focus":0}],"luckysheet_selection_range":[],"zoomRatio":1,"config":{"merge":{},"rowlen":{"0":37},"columnlen":{"4":125,"7":185,"8":201,"13":110,"14":89,"15":119,"16":143,"17":155,"18":141},"customWidth":{"4":1,"7":1,"8":1,"13":1,"14":1,"15":1,"16":1,"17":1,"18":1},"customHeight":{"0":1},"borderInfo":[{"rangeType":"range","borderType":"border-all","color":"#000","style":"1","range":[{"row":[0,0],"column":[0,19]}]}]},"luckysheet_conditionformat_save":[],"frozen":{"type":"rangeBoth","range":{"row_focus":0,"column_focus":4}},"dataVerification":{},"dynamicArray":[],"images":{},"scrollTop":0,"celldata":[{"r":0,"c":0,"v":{"v":"序号","ct":{"fa":"General","t":"g"},"m":"序号","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":1,"v":{"v":"发现阶段","ct":{"fa":"General","t":"g"},"m":"发现阶段","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":2,"v":{"v":"发生日期","ct":{"fa":"General","t":"g"},"m":"发生日期","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":3,"v":{"v":"车号","ct":{"fa":"General","t":"g"},"m":"车号","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":4,"v":{"v":"所属系统名称","ct":{"fa":"General","t":"g"},"m":"所属系统名称","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":5,"v":{"v":"所属工位","ct":{"fa":"General","t":"g"},"m":"所属工位","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":6,"v":{"v":"故障位置","ct":{"fa":"General","t":"g"},"m":"故障位置","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":7,"v":{"v":"车辆故障详细描述","ct":{"fa":"General","t":"g"},"m":"车辆故障详细描述","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":8,"v":{"v":"车辆故障详细处理措施","ct":{"fa":"General","t":"g"},"m":"车辆故障详细处理措施","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":9,"v":{"v":"故障等级","ct":{"fa":"General","t":"g"},"m":"故障等级","fs":"11","bl":1,"ht":"0","vt":"0"}},
// {"r":0,"c":10,"v":{"v":"故障影响","ct":{"fa":"General","t":"g"},"m":"故障影响","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":11,"v":{"v":"完成情况","ct":{"fa":"General","t":"g"},"m":"完成情况","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":12,"v":{"v":"故障类别","ct":{"fa":"General","t":"g"},"m":"故障类别","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":13,"v":{"v":"关闭日期","ct":{"fa":"General","t":"g"},"m":"关闭日期","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":14,"v":{"m":"处理人","ct":{"fa":"General","t":"g"},"v":"处理人","fs":"11","bl":1,"vt":"0","ht":"0"}},{"r":0,"c":15,"v":{"v":"更换部件名称","ct":{"fa":"General","t":"g"},"m":"更换部件名称","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":16,"v":{"v":"是否有责","ct":{"fa":"General","t":"g"},"m":"是否有责","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":17,"v":{"v":"是否委外件故障","ct":{"fa":"General","t":"g"},"m":"是否委外件故障","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":18,"v":{"v":"规程内要求情况","ct":{"fa":"General","t":"g"},"m":"规程内要求情况","fs":"11","bl":1,"ht":"0","vt":"0"}}]}`
// var faultDetailSheet = `{"name":"故障明细","column":13,"row":60,"data":[],"visibledatarow":[38,58,78,98,118,138,158,178,198,218,238,258,278,298,318,338,358,378,398,418,438,458,478,498,518,538,558,578,598,618,638,658,678,698,718,738,758,778,798,818,838,858,878,898,918,938,958,
// 978,998,1018,1038,1058,1078,1098,1118,1138,1158,1178,1198,1218],"visibledatacolumn":[73,146,219,292,427,500,707,930,1003,1076,1195,1290,1465,1538,1611,1684,1757,1830,1903,1976,2049,2122,2195,2268,2341,2414],"ch_width":2137,"rh_height":1298,"luckysheet_select_save":[{"left":0,"width":73,"top":38,"height":19,"left_move":0,"width_move":73,"top_move":38,"height_move":19,"row":[1,1],"column":[0,0],"row_focus":1,"column_focus":0}],"luckysheet_selection_range":[],"zoomRatio":1,"config":{"merge":{},"rowlen":{"0":37},"columnlen":{"2":125,"4":180,"5":180,"6":180,"7":180,"10":125,"11":125,"12":175},"customWidth":{"4":1,"7":1,"8":1,"13":1,"14":1,"15":1,"16":1,"17":1,"18":1},"customHeight":{"0":1},"borderInfo":[{"rangeType":"range","borderType":"border-all","color":"#000","style":"1","range":[{"row":[0,0],"column":[0,19]}]}]},"luckysheet_conditionformat_save":[],"frozen":{"type":"rangeBoth","range":{"row_focus":0,"column_focus":4}},"dataVerification":{},"dynamicArray":[],"images":{},"scrollTop":0,"celldata":[{"r":0,"c":0,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"序号"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":1,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"发现阶段"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":2,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"发生日期"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":3,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"车号"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":4,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"所属系统名称"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":5,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"所属工位"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":6,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"车辆故障详细描述"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":7,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"车辆故障详细处理措施"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":8,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"故障等级"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":9,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"完成情况"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":10,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"关闭日期"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":11,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"处理人"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":12,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"是否委外件故障"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}}]}`
var faultDetailSheet = `{"name":"故障明细","column":12,"row":60,"data":[],"visibledatarow":[38,58,78,98,118,138,158,178,198,218,238,258,278,298,318,338,358,378,398,418,438,458,478,498,518,538,558,578,598,618,638,658,678,698,718,738,758,778,798,818,838,858,878,898,918,938,958,
978,998,1018,1038,1058,1078,1098,1118,1138,1158,1178,1198,1218],"visibledatacolumn":[73,146,219,292,427,500,707,930,1003,1076,1195,1290,1465,1538,1611,1684,1757,1830,1903,1976,2049,2122,2195,2268,2341,2414],"ch_width":2137,"rh_height":1298,"luckysheet_select_save":[{"left":0,"width":73,"top":38,"height":19,"left_move":0,"width_move":73,"top_move":38,"height_move":19,"row":[1,1],"column":[0,0],"row_focus":1,"column_focus":0}],"luckysheet_selection_range":[],"zoomRatio":1,"config":{"merge":{},"rowlen":{"0":37},"columnlen":{"2":125,"4":180,"5":180,"6":180,"7":180,"10":125,"11":125,"12":175},"customWidth":{"4":1,"7":1,"8":1,"13":1,"14":1,"15":1,"16":1,"17":1,"18":1},"customHeight":{"0":1},"borderInfo":[{"rangeType":"range","borderType":"border-all","color":"#000","style":"1","range":[{"row":[0,0],"column":[0,19]}]}]},"luckysheet_conditionformat_save":[],"frozen":{"type":"rangeBoth","range":{"row_focus":0,"column_focus":4}},"dataVerification":{},"dynamicArray":[],"images":{},"scrollTop":0,"celldata":[{"r":0,"c":0,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"序号"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":1,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"发现阶段"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":2,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"发生日期"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":3,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"车号"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":4,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"所属系统名称"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":5,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"所属工位"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":6,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"车辆故障详细描述"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":7,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"车辆故障详细处理措施"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":8,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"故障等级"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":9,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"处理情况"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":10,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"处理日期"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}},{"r":0,"c":11,"v":{"ct":{"fa":"General","t":"inlineStr","s":[{"ff":"宋体","fc":"#000000","fs":11,"bl":1,"v":"处理人"}]},"fs":11,"fc":"#000000","ff":"微软雅黑","bl":1,"ht":0,"vt":0,"tb":1}}]}`
</script>

<style scoped>
#luckysheet {
  height: calc(100% - 0px);
  width: 100%;
}
.bodyWrapper .table-page-body.more-visible {
  height: calc(100% - 190px);
}
</style>