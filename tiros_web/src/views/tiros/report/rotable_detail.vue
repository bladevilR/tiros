<template>
  <div class="bodyWrapper" style="padding: 8px">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="loadData">
        <a-row :gutter="24" type="flex">
          <a-col :md="4">
            <a-form-item label="线路:">
              <line-select-list v-model="queryParam.lineId" @change="onLineChange"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="车辆:">
              <j-dict-select-seach-tag @focus="handleSysFocus" v-model="queryParam.trainNo" :dictCode="dictTrainStr" />
            </a-form-item>
          </a-col>
          <a-col flex="420px">
            <!-- <a-form-item label="日期:"> -->
            <na-select-date @change="dateChange"></na-select-date>
            <!-- </a-form-item> -->
          </a-col>
          <a-col :md="4" :sm="24">
            <a-space>
              <a-button @click="init_sheet">查询</a-button>
              <a-button @click="downloadExcel">导出</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="table-page-body">
      <div id="luckysheet" style="height: calc(100% - 0px); width: 100%"></div>
    </div>
  </div>
</template>

<script>
import luckyexcelUtil from '../util/LuckyexcelUtil'
import { exportExcel } from '@views/tiros/util/export'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import NaSelectDate from '@comp/tiros/Na-select-date'
import { getTrainRotableDetail } from '@api/tirosReportApi'

export default {
  name: 'rotable_detail',
  components: { LineSelectList, NaSelectDate },
  data() {
    return {
      excelLoad:false,
      moreVisible: false,
      yearOpen: false,
      yearValue: this.$moment(new Date()),
      dictTrainStr: '',
      queryParam: {
        category1: '',
        category2: '',
        lineId: null,
        trainNo: '',
        depotId: '',
        year: this.$moment(new Date()).format('YYYY'),
        quarter: '',
        month: '',
        endDate: '',
        startDate: '',
        dateType: '',
      },
    }
  },
  beforeDestroy() {
    // 离开销毁Luckysheet
    luckysheet.destroy()
  },
  methods: {
    handleSysFocus() {
      if (!this.queryParam.lineId) this.$message.warn('请先选择线路!')
    },
    toggle() {
      this.moreVisible = !this.moreVisible
    },
    changeYear(value, mode) {
      this.yearValue = value
      this.queryParam.year = value.format('YYYY')
      this.yearOpen = false
    },
    onLineChange(val, option) {
      this.dictTrainStr = "bu_train_info,train_no,train_no,status=1 and line_id='" + val + "'"
    },
    check() {
      if (!this.queryParam.lineId) {
        this.$message.warn('请先选择线路!')
        return false
      }
      if (!this.queryParam.trainNo) {
        this.$message.warn('请先选择车辆!')
        return false
      }
      return true
    },
    init_sheet() {
      if (!this.check()) return
      this.excelLoad = false;
      getTrainRotableDetail(this.queryParam).then((res) => {
        if (res.success) {
          const op = Object.assign({}, luckyexcelUtil.getDefaultOptions())
          op.data = [JSON.parse(faultDetailSheet)]
          let cellData = []
          cellData.push(...op.data[0].celldata)

          if (res.result.records) {
            res.result.records.forEach((e, index) => {
              luckyexcelUtil.setCellValueByArrayData(op.data[0], index + 2, 'A', e.index)
              luckyexcelUtil.setCellValueByArrayData(op.data[0], index + 2, 'B', e.assetName)
              luckyexcelUtil.setCellValueByArrayData(op.data[0], index + 2, 'C', e.sysName)
              luckyexcelUtil.setCellValueByArrayData(op.data[0], index + 2, 'D', e.downAssetSn)
              luckyexcelUtil.setCellValueByArrayData(op.data[0], index + 2, 'E', e.downAssetLocation)
              luckyexcelUtil.setCellValueByArrayData(op.data[0], index + 2, 'F', e.downAssetDutyUser)
              luckyexcelUtil.setCellValueByArrayData(op.data[0], index + 2, 'G', e.downAssetTime, {
                ct: { fa: '@', t: 's' },
              })
              luckyexcelUtil.setCellValueByArrayData(op.data[0], index + 2, 'H', e.upAssetSn)
              luckyexcelUtil.setCellValueByArrayData(op.data[0], index + 2, 'I', e.upAssetTrainNo)
              luckyexcelUtil.setCellValueByArrayData(op.data[0], index + 2, 'J', e.upAssetLocation)
              luckyexcelUtil.setCellValueByArrayData(op.data[0], index + 2, 'K', e.upAssetDutyUser)
              luckyexcelUtil.setCellValueByArrayData(op.data[0], index + 2, 'L', e.upAssetTime, { ct: { fa: '@', t: 's' } })
              luckyexcelUtil.setCellValueByArrayData(op.data[0], index + 2, 'M', e.remark)
            })
            op.data[0].celldata[0]['v']['m'] = `${this.queryParam.trainNo}车架修周转件信息表`
            op.data[0].celldata[0]['v']['v'] = `${this.queryParam.trainNo}车架修周转件信息表`
            op.name = `${this.queryParam.trainNo}车架修周转件信息表`
          }
          // 添加网格
          op.data[0].config.borderInfo[0].range[0].row = [0, res.result.records.length + 1]
          op.data[0].celldata = cellData
          luckysheet.destroy()
          luckysheet.create(op)
          this.excelLoad = true;
        }
      })
    },
    loadData() {},
    dateChange(value) {
      this.queryParam.dateType = value.dateType
      this.queryParam.year = value.year
      this.queryParam.quarter = value.quarter
      this.queryParam.month = value.month
      this.queryParam.startDate = value.dateRef[0]
      this.queryParam.endDate = value.dateRef[1]
    },
    downloadExcel() {
      if(!this.check()) return;
      if(!this.excelLoad) return this.$message.warning('请先点击查询按钮加载表格数据再进行导出');
      exportExcel(luckysheet.getAllSheets(), this.queryParam.trainNo + '车架修周转件信息表')
    },
  },
}

const faultDetailSheet = `{"name":"周转件信息表","color":"","status":"1","order":"0","data":[],"config":{"borderInfo":[{"rangeType":"range",
"borderType":"border-all","color":"#000","style":"1","range":[{"left":0,"width":1513,"top":-1,"height":40,"left_move":0,"width_move":1513,"top_move":0,"height_move":96,"row":[0,1],"column":[0,12],"row_focus":0,"column_focus":0}]}],
"rowlen":{"0":39,"1":56},"customHeight":{"0":1,"1":1},"columnlen":{"0":95,"1":131,"2":174,"3":113,"4":96,"5":109,"6":129,"7":113,"8":144,"9":91,"10":91,"11":126,"12":89,"13":81},"customWidth":{"0":1,"1":1,"2":1,"3":1,"4":1,"5":1,"6":1,"7":1,
"8":1,"9":1,"10":1,"11":1,"12":1,"13":1},"merge":{"0_0":{"r":0,"c":0,"rs":1,"cs":13}}},"index":0,"visibledatarow":[40,97],"visibledatacolumn":[96,228,403,517,614,724,854,968,1113,1205,1297,1424,1514],"ch_width":1634,"rh_height":177,
"luckysheet_select_save":[{"left":0,"width":95,"top":40,"height":56,"left_move":0,"width_move":95,"top_move":40,"height_move":56,"row":[1,1],"column":[0,0],"row_focus":1,"column_focus":0}],"luckysheet_selection_range":[],"zoomRatio":1,
"images":{},"frozen":{"type":"rangeRow","range":{"row_focus":1,"column_focus":0}},"celldata":[{"r":0,"c":0,"v":{"mc":{"r":0,"c":0,"rs":1,"cs":13},"ht":"0","vt":"0","v":"周转件信息表","m":"周转件信息表","ct":{"fa":"General","t":"g"},"bl":1,
"fs":"14"}},{"r":0,"c":1,"v":{"mc":{"r":0,"c":0},"ht":"0","vt":"0","bl":1,"fs":"14"}},{"r":0,"c":2,"v":{"mc":{"r":0,"c":0},"ht":"0","vt":"0","bl":1,"fs":"14"}},{"r":0,"c":3,"v":{"mc":{"r":0,"c":0},"ht":"0","vt":"0","bl":1,"fs":"14"}},
{"r":0,"c":4,"v":{"mc":{"r":0,"c":0},"ht":"0","vt":"0","bl":1,"fs":"14"}},{"r":0,"c":5,"v":{"mc":{"r":0,"c":0},"ht":"0","vt":"0","bl":1,"fs":"14"}},{"r":0,"c":6,"v":{"mc":{"r":0,"c":0},"ht":"0","vt":"0","bl":1,"fs":"14"}},{"r":0,"c":7,
"v":{"mc":{"r":0,"c":0},"ht":"0","vt":"0","bl":1,"fs":"14"}},{"r":0,"c":8,"v":{"mc":{"r":0,"c":0},"ht":"0","vt":"0","bl":1,"fs":"14"}},{"r":0,"c":9,"v":{"mc":{"r":0,"c":0},"ht":"0","vt":"0","bl":1,"fs":"14"}},{"r":0,"c":10,"v":{"mc":{"r":0,
"c":0},"ht":"0","vt":"0","bl":1,"fs":"14"}},{"r":0,"c":11,"v":{"mc":{"r":0,"c":0},"ht":"0","vt":"0","bl":1,"fs":"14"}},{"r":0,"c":12,"v":{"mc":{"r":0,"c":0},"ht":"0","vt":"0","bl":1,"fs":"14"}},{"r":1,"c":0,"v":{"v":"序号",
"ct":{"fa":"General","t":"g"},"m":"序号","fs":"12","bl":0,"ht":"0","vt":"0"}},{"r":1,"c":1,"v":{"v":"部件名称","ct":{"fa":"General","t":"g"},"m":"部件名称","fs":"12","bl":0,"ht":"0","vt":"0"}},{"r":1,"c":2,
"v":{"v":"部件所属系统（按照系统清单填写）","ct":{"fa":"General","t":"g"},"m":"部件所属系统（按照系统清单填写）","fs":"12","bl":0,"ht":"0","vt":"0","tb":"2"}},{"r":1,"c":3,"v":{"v":"原部件序号","ct":{"fa":"General","t":"g"},"m":"原部件序号",
"fs":"12","bl":0,"ht":"0","vt":"0"}},{"r":1,"c":4,"v":{"m":"原部件安装位置","ct":{"fa":"General","t":"g"},"v":"原部件安装位置","ht":"0","vt":"0","fs":"12","tb":"2"}},{"r":1,"c":5,"v":{"v":"责任人","ct":{"fa":"General","t":"g"},"m":"责任人",
"fs":"12","bl":0,"ht":"0","vt":"0"}},{"r":1,"c":6,"v":{"v":"时间","ct":{"fa":"General","t":"g"},"m":"时间","fs":"12","bl":0,"ht":"0","vt":"0"}},{"r":1,"c":7,"v":{"v":"原位置新安装件序列号","ct":{"fa":"General","t":"g"},"m":"原位置新安装件序列号",
"fs":"12","bl":0,"ht":"0","vt":"0","tb":"2"}},{"r":1,"c":8,"v":{"v":"新部件原车号","ct":{"fa":"General","t":"g"},"m":"新部件原车号","fs":"12","bl":0,"ht":"0","vt":"0"}},{"r":1,"c":9,"v":{"v":"新部件原安装位置","ct":{"fa":"General","t":"g"},
"m":"新部件原安装位置","fs":"12","bl":0,"ht":"0","vt":"0","tb":"2"}},{"r":1,"c":10,"v":{"v":"责任人","ct":{"fa":"General","t":"g"},"m":"责任人","fs":"12","bl":0,"ht":"0","vt":"0"}},{"r":1,"c":11,"v":{"m":"时间","ct":{"fa":"General","t":"g"},
"v":"时间","fs":"12","bl":0,"ht":"0","vt":"0"}},{"r":1,"c":12,"v":{"m":"备注","ct":{"fa":"General","t":"g"},"v":"备注","fs":"12","bl":0,"ht":"0","vt":"0"}}],"luckysheet_conditionformat_save":[],"dataVerification":{}}`
</script>