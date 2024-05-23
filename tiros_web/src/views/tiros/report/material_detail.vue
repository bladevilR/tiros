<template>
  <div class="bodyWrapper" style="padding: 8px">
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="loadData">
        <a-row :gutter="24" type="flex">
          <a-col :md='4' :sm='24'>
            <a-form-item label='月份'>
              <a-range-picker
                :placeholder="['开始月份', '结束月份']"
                format='YYYY-MM'
                :value='monthVal'
                @panelChange='handlePanelMonthChange'
                :mode='mode'
              />
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-form-item label="车辆段:">
              <j-dict-select-tag v-model="queryParam.depotId" dictCode="bu_mtr_depot,name,id" />
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-form-item label="线路:    ">
              <line-select-list v-model="queryParam.lineId" @change="onLineChange"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="车辆:    ">
              <j-dict-select-seach-tag @focus="handleSysFocus" v-model="queryParam.trainNo" :dictCode="dictTrainStr" />
            </a-form-item>
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
        <a-row :gutter="24" type="flex" v-if="moreVisible">
<!--          <a-col flex="420px">-->
<!--            <na-select-date @change="dateChange" :naStyle="'margin-left: 15px'"></na-select-date>-->
<!--          </a-col>-->
          <!-- <a-col :md="4" :sm="24">
            <a-form-item label="物资分类:">
              <j-dict-select-tag v-model="queryParam.category1" placeholder="请选择" dictCode="bu_material_type" />
            </a-form-item>
          </a-col> -->
          <a-col :md="4" :sm="24">
            <a-form-item label="物资属性:">
              <j-dict-select-tag v-model="queryParam.category2" placeholder="请选择" dictCode="bu_material_attr" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="table-page-body" :class="{ 'more-visible': moreVisible }">
      <div id="luckysheet" style="height: calc(100% - 0px); width: 100%"></div>
    </div>
  </div>
</template>

<script>
import luckyexcelUtil from '../util/LuckyexcelUtil'
import { exportExcel } from '@views/tiros/util/export'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import NaSelectDate from '@comp/tiros/Na-select-date'
import { getTrainMaterialDetail, getTrainMaterialMustDetail } from '@api/tirosReportApi'

export default {
  name: 'material_detail',
  components: { LineSelectList, NaSelectDate },
  data() {
    return {
      moreVisible: false,
      dictTrainStr: '',
      queryParam: {
        category1: '',
        category2: '',
        lineId: null,
        trainNo: '',
        depotId: '',
        // year: this.$moment(new Date()).format('YYYY'),
        // quarter: '',
        // month: '',
        // endDate: '',
        // startDate: '',
        // dateType: 1,
      },
      mode: ['month', 'month'],
      monthVal: [this.$moment(new Date()).format('YYYY-MM'), this.$moment(new Date()).format('YYYY-MM')]
    }
  },
  mounted() {
    // this.init_sheet()
  },
  beforeDestroy() {
    // 离开销毁Luckysheet
    luckysheet.destroy()
  },
  methods: {
    handlePanelMonthChange(value, mode) {
      this.monthVal = value
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
    onLineChange(val, option) {
      this.dictTrainStr = "bu_train_info,train_no,train_no,status=1 and line_id='" + val + "'"
    },
    init_sheet() {
      const op = Object.assign({}, luckyexcelUtil.getDefaultOptions())
      op.showsheetbar = true
      op.showsheetbarConfig = {
        add: false, //新增sheet
        menu: false, //sheet管理菜单
      }
      op.data = []
      op.forceCalculation = false

      let param = {
        ...this.queryParam,
        startMonth: this.$moment(this.monthVal[0]).format('YYYY-MM'),
        endMonth: this.$moment(this.monthVal[1]).format('YYYY-MM')
      }

      getTrainMaterialMustDetail(param).then((res) => {
        if (res.success) {
          this.pushSheetData('备件',res, op, 0)

          getTrainMaterialDetail(param).then((res2) => {
            if (res2.success) {
              this.pushSheetData('耗材', res2, op, 1)
              luckysheet.destroy()
              luckysheet.create(op)
            }
          })
        }
      })

    },

    pushSheetData(title, res, op, index) {
      let sysItemStartIndex = 0
      let worksItemStartIndex = 0
      let itemCount = 0
      let worksCount = 0
      op.data.push(JSON.parse(faultDetailSheet))
      op.data[index].index = index
      op.data[index].name = title
      op.data[index].order = index
      let cellData = []
      cellData.push(...op.data[index].celldata)

      // 开启公式刷新
      res.result.systemList.forEach((eSys, indexSys) => {
        // 用于统计该系统下物资总价格
        let sysSum=0
        // 系统名称
        luckyexcelUtil.setCellValueByArrayData(cellData, itemCount + 1, 'A', eSys.sysName)
        eSys.workstationList.forEach((eWordStation, indexWorkstation) => {
          let countSum = 0

          // 工号
          luckyexcelUtil.setCellValueByArrayData(cellData, itemCount + 1, 'B', eWordStation.workstationNo)
          // 名称
          luckyexcelUtil.setCellValueByArrayData(cellData, itemCount + 1, 'C', eWordStation.workstationName)
          worksCount++
          // let subtotal = 0;
          eWordStation.materialList.forEach((eMaterial, indexMaterial) => {
            // 物资编码
            luckyexcelUtil.setCellValueByArrayData(cellData, itemCount + 1, 'D', eMaterial.materialTypeCode, {
              ct: { fa: '@', t: 's' },
            })
            // 物料描述
            luckyexcelUtil.setCellValueByArrayData(cellData, itemCount + 1, 'E', eMaterial.materialTypeSpec)
            // 单位
            luckyexcelUtil.setCellValueByArrayData(cellData, itemCount + 1, 'F', eMaterial.materialTypeUnit)
            // 必换件/故障件
            luckyexcelUtil.setCellValueByArrayData(cellData, itemCount + 1, 'G', eMaterial.useCategory_dictText)
            // 属性
            luckyexcelUtil.setCellValueByArrayData(cellData, itemCount + 1, 'H', eMaterial.category2_dictText)
            // 数量
            luckyexcelUtil.setCellValueByArrayData(cellData, itemCount + 1, 'I', eMaterial.consumeAmount, {
              ct: { fa: '@', t: 's' },
            })
            // 单价
            luckyexcelUtil.setCellValueByArrayData(cellData, itemCount + 1, 'J', eMaterial.unitPrice)
            // 总价
            luckyexcelUtil.setCellValueByArrayData(cellData, itemCount + 1, 'K', eMaterial.consumeTotalPrice)
            countSum += Number(eMaterial.consumeTotalPrice)
            sysSum += Number(eMaterial.consumeTotalPrice)
            // 备注
            luckyexcelUtil.setCellValueByArrayData(cellData, itemCount + 1, 'N', eMaterial.remark)
            // subtotal += Number(eMaterial.totalPrice)
            itemCount++
          })
          // 导入公式 （小计），jakgong注释，小计合并错误了，不是按工位合同，应该是按系统合并
         /* luckyexcelUtil.setCellValueByArrayData(cellData, worksItemStartIndex + 1, 'L', countSum, {
            f: `=SUM(K${worksItemStartIndex + 2}:K${itemCount + 1})`,
          })
          console.log('公式传值', {
            f: `=SUM(K${worksItemStartIndex + 2}:K${itemCount + 1})`,
            v: countSum,
            m: countSum
          });*/
          // 导入公式需要关联数据
          ;(op.data[index]['calcChain'] || (op.data[index]['calcChain'] = [])).push({
            r: worksItemStartIndex + 1,
            c: luckyexcelUtil.char2Index('L'),
            index: 0,
          })
          //合并小计, jakgong注释，小计合并错误了，不是按工位合同，应该是按系统合并
          // luckyexcelUtil.setCellMergeByValue(op.data[index], `L${worksItemStartIndex + 2}`, `L${itemCount + 1}`)
          // 合并工号
          luckyexcelUtil.setCellMergeByValue(op.data[index], `B${worksItemStartIndex + 2}`, `B${itemCount + 1}`)
          // 合并姓名
          luckyexcelUtil.setCellMergeByValue(op.data[index], `C${worksItemStartIndex + 2}`, `C${itemCount + 1}`)
          worksItemStartIndex = itemCount
        })
        // 合并系统
        luckyexcelUtil.setCellMergeByValue(op.data[index], `A${sysItemStartIndex + 2}`, `A${itemCount + 1}`)

        // 写入小计公式 add by jakgong
        luckyexcelUtil.setCellValueByArrayData(cellData, sysItemStartIndex + 1, 'L', sysSum, {
          f: `=SUM(K${sysItemStartIndex + 2}:K${itemCount + 1})`,
        })
        // 合并小计 add by jakgong
        luckyexcelUtil.setCellMergeByValue(op.data[index], `L${sysItemStartIndex + 2}`, `L${itemCount + 1}`)

        sysItemStartIndex = itemCount
      })
      op.data[index].config.borderInfo[0].range[0].row = [0, itemCount]
      //合并合计
      luckyexcelUtil.setCellMergeByValue(op.data[index], `M2`, `M${itemCount + 1}`)
      //总计
      luckyexcelUtil.setCellValueByArrayData(cellData, 1, 'M', res.result.totalCost)

      op.data[index].celldata = cellData
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
      exportExcel(luckysheet.getAllSheets(), '物料消耗明细')
    },
  },
}

const faultDetailSheet = `{"name":"物料消耗明细","color":"","status":"1","order":"0","data":[],"config":{"borderInfo":[{"rangeType":"range","borderType":"border-all","color":"#000","style":"1",
"range":[{"left":0,"width":73,"top":0,"height":19,"left_move":0,"width_move":1035,"top_move":0,"height_move":19,"row":[0,0],"column":[0,13],"row_focus":0,"column_focus":0}]}],"rowlen":{"0":19.5},"customHeight":{},
"columnlen":{"0":95,"1":81,"2":107,"3":113,"4":189,"6":113,"8":80,"9":80,"10":91,"11":87,"12":89,"13":81},"customWidth":{"0":1,"1":1,"2":1,"3":1,"4":1,"6":1,"8":1,"9":1,"10":1,"11":1,"12":1,"13":1}},"index":0,
"visibledatarow":[21],"visibledatacolumn":[96,178,286,400,590,664,778,852,933,1014,1106,1194,1284,1366],"ch_width":1486,"rh_height":101,"luckysheet_select_save":[{"left":778,"width":73,"top":0,"height":20,"left_move":778,
"width_move":73,"top_move":0,"height_move":20,"row":[0,0],"column":[7,7],"row_focus":0,"column_focus":7,"column_select":true}],"luckysheet_selection_range":[],"zoomRatio":1,"images":{},"frozen":{"type":"row"},
"celldata":[{"r":0,"c":0,"v":{"v":"系统","ct":{"fa":"General","t":"g"},"m":"系统","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":1,"v":{"v":"工位号","ct":{"fa":"General","t":"g"},"m":"工位号","fs":"11","bl":1,"ht":"0","vt":"0"}},
{"r":0,"c":2,"v":{"v":"工位名称","ct":{"fa":"General","t":"g"},"m":"工位名称","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":3,"v":{"v":"资产编码","ct":{"fa":"General","t":"g"},"m":"资产编码","fs":"11","bl":1,"ht":"0","vt":"0"}},
{"r":0,"c":4,"v":{"v":"物料描述","ct":{"fa":"General","t":"g"},"m":"物料描述","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":5,"v":{"v":"单位","ct":{"fa":"General","t":"g"},"m":"单位","fs":"11","bl":1,"ht":"0","vt":"0"}},
{"r":0,"c":6,"v":{"v":"必换件/故障件","ct":{"fa":"General","t":"g"},"m":"必换件/故障件","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":7,"v":{"v":"属性","ct":{"fa":"General","t":"g"},"m":"属性","fs":"11","bl":1,"ht":"0","vt":"0"}},
{"r":0,"c":8,"v":{"v":"数量","ct":{"fa":"General","t":"g"},"m":"数量","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":9,"v":{"v":"单价（元）","ct":{"fa":"General","t":"g"},"m":"单价（元）","fs":"11","bl":1,"ht":"0","vt":"0"}},
{"r":0,"c":10,"v":{"v":"总价（元）","ct":{"fa":"General","t":"g"},"m":"总价（元）","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":11,"v":{"m":"小计（元）","ct":{"fa":"General","t":"g"},"v":"小计（元）","fs":"11","bl":1,"ht":"0","vt":"0"}},
{"r":0,"c":12,"v":{"m":"合计（元）","ct":{"fa":"General","t":"g"},"v":"合计（元）","fs":"11","bl":1,"ht":"0","vt":"0"}},{"r":0,"c":13,"v":{"m":"备注","ct":{"fa":"General","t":"g"},"v":"备注","fs":"11","bl":1,"ht":"0","vt":"0"}}]}`
</script>

<style lang="less" scoped>
.bodyWrapper {
  .table-page-body {
    &.more-visible {
      height: calc(100% - 106px) !important;
    }
  }
}
</style>