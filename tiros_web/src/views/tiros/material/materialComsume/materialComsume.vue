<template>
  <div class='bodyWrapper' style='padding: 8px; display: flex; flex-direction: column'>
    <div class='table-page-search-wrapper'>
      <a-form layout='inline' @keyup.enter.native='findList'>
        <a-row :gutter='24' type='flex'>
          <!-- <a-col :md="4">
            <a-form-item label="车辆段:">
              <j-dict-select-tag v-model="queryParam.depotId" dictCode="bu_mtr_depot,name,id" />
            </a-form-item>
          </a-col> -->

          <a-col :md='6' :sm='24'>
            <a-form-item label='列计划:' :labelCol='{span:5}' :wrapperCol='{span: 18}'>
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
          <!--          <a-col :md='3'>-->
          <!--            <a-form-item label='线路:'>-->
          <!--              <line-select-list-->
          <!--                placeholder='请选择'-->
          <!--                v-model='queryParam.lineId'-->
          <!--                @change='onLineChange'-->
          <!--              ></line-select-list>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
          <!--          <a-col :md='3' :sm='24'>-->
          <!--            <a-form-item label='车辆:'>-->
          <!--              <j-dict-select-seach-tag placeholder='请选择' @focus='handleSysFocus' v-model='queryParam.trainNo'-->
          <!--                                       :dictCode='dictTrainStr' />-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
          <a-col :md='4' :sm='24'>
            <a-form-item label='班组:'>
              <j-dict-select-tag
                v-model='queryParam.groupId'
                placeholder='请选择'
                :dictCode="'bu_mtr_workshop_group,group_name,id'"
              />
            </a-form-item>
          </a-col>

          <a-col :md='3' :sm='24'>
            <a-form-item label='差异:'>
              <j-dict-select-tag
                allowClear
                v-model='queryParam.amountIsDiff'
                placeholder='请选择'
                dictCode='bu_state'
              />
            </a-form-item>
          </a-col>
          <a-col :md='3' :sm='24'>
            <a-form-item label='必换件清单:'>
              <j-dict-select-tag
                allowClear
                v-model='queryParam.amountIsDiff'
                placeholder='请选择'
                dictCode='bu_state'
              />
            </a-form-item>
          </a-col>
          <a-col :md='6'>
            <a-form-item label='物资编码或名称:'>
              <a-input v-model='queryParam.materialSearchText' allowClear placeholder='请输入物资编码或名称' />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter='24' :style="{ display: expand ? 'block' : 'none' }">
          <a-col :md='4' :sm='24'>
            <a-form-item label='物资类型:'>
              <j-dict-select-tag
                v-model='queryParam.useCategory'
                placeholder='请选择'
                dictCode='bu_material_type'
              />
            </a-form-item>
          </a-col>
          <!--          <a-col :md='8' :sm='24'>-->
          <!--            <a-form-item label='月份'>-->
          <!--              &lt;!&ndash; <a-date-picker v-model="yearValue" @change="onDateChange" /> &ndash;&gt;-->
          <!--              <a-range-picker v-model='dateTime' @change='onDateChange' :defaultPickerValue='defaultDateRange' />-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
        </a-row>
        <a-row>
          <a-col>
            <span class='table-page-search-submitButtons'>
              <a-space>
                <a-button @click='findList'>查询</a-button>
                  <a-button :disabled='selecteList.length !== 1'
                            type='primary'
                            @click='$refs.materialConsumeModal.show(selecteList[0])'
                            v-has="'material:consume'">核实</a-button>
                <!--                <a-button :disabled='selecteList.length !== 1' -->
                <!--                          type='primary' -->
                <!--                          @click='verifyCheck(selecteList[0])'-->
                <!--                          v-has="'material:consume'">核实</a-button>-->
                <!--                <a-button :disabled='selecteList.length < 1' type='primary' @click='batchCheck'-->
                <!--                          v-has="'material:consume'">批量核实</a-button>-->
                <a-button :loading='exportLoading' @click='exportList'>导出</a-button>
                <!--                <a-button><a :style="{fontSize: '12px'}" @click='expand = !expand'>-->
                <!--                更多条件 <a-icon :type="expand ? 'up' : 'down'" />-->
                <!--                </a></a-button>-->
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <!-- <div class="table-page-body" :class="{ 'more-visible': moreVisible }">
      <div id="luckysheet" style="height: calc(100% - 0px); width: 100%"></div>
    </div> -->
    <div class='table-page-body-wrapper' style='height: calc(100vh - 242px)'>
      <span class='table-page-search-submitButtons'>
        <p style='text-align: left'>
          <a style='color: #ffa30f; font-weight: bold'>数据信息： {{ compareInfo }} </a>
        </p>
        <p style='text-align: left'>
          <a style='color: #ffa30f; font-weight: bold'>消耗工单信息： {{ consumeOrderInfo }} </a>
        </p>
      </span>
      <div class='table-body-context'>
        <vxe-table
          border
          ref='listTable'
          align='center'
          height='auto'
          :data='tableData'
          :cell-style='cellStyle'
          show-overflow='tooltip'
          @checkbox-all='selectChangeEvent'
          @checkbox-change='selectChangeEvent'
          :sort-config="{trigger: 'cell'}"
          :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        >
          <!-- :sort-config="{trigger: 'cell', defaultSort: {field: 'groupName', order: 'desc'}}"-->
          <!-- @cell-click='cellClickEvent'-->
          <vxe-table-column type='seq' width='60'></vxe-table-column>
          <vxe-table-column type='checkbox' width='40'></vxe-table-column>
<!--          <vxe-table-column field='lineName' width='80' title='线路' />-->
<!--          <vxe-table-column field='trainNo' width='80' title='车辆' />-->
          <vxe-table-column field='groupName' sortable align='left' width='130' header-align='center' title='所属工班' />
          <vxe-table-column field='materialCode' sortable width='130' title='物资编码' />
          <vxe-table-column field='materialName' width='150' align='left' header-align='center' title='物资名称' />
          <!--          <vxe-table-column field='materialSpec' min-width='200' align='left' header-align='center' title='物资描述' />-->
          <vxe-table-column field='useCategory_dictText' sortable width='80' title='类别' />
          <vxe-table-column field='isMustList_dictText' sortable width='140' title='必换件清单？' />
          <!--          <vxe-table-column field='mustListAmount' width='140' title='必换件清单数量' />-->
          <vxe-table-column field='requireSystemWorkstationInfo' width='280' title='必换件清单系统工位' />
          <vxe-table-column field='consumeSystemWorkstationInfo' width='280' title='消耗系统工位' />
          <!--          <vxe-table-column field='unitPrices' align='right' width='80' title='价格'>-->
          <!--            <template v-slot='{row}'>-->
          <!--              <div>-->
          <!--                {{ Number(Number(row.unitPrices).toFixed(2)) }}-->
          <!--              </div>-->
          <!--            </template>-->
          <!--          </vxe-table-column>-->
          <vxe-table-column field='needAmount' align='right' width='80' header-align='center' title='额定数量' />
          <vxe-table-column field='receiveAmount' align='right' width='80' header-align='center' title='领用数量' />
          <vxe-table-column field='consumeAmount' align='right' width='80' header-align='center' title='实际消耗' />
          <vxe-table-column field='amountIsDiff_dictText' sortable width='100' title='是否差异' />
          <!--          <vxe-table-column field='isVerify_dictText' width='80' title='已核实？'>-->
          <!-- <template v-slot="{row}">
            <div class="pointBox">
              <span>{{row.isVerify_dictText}}</span>
            </div>
          </template> -->
          <!--          </vxe-table-column>-->
          <!--          <vxe-table-column field='tradeNos' width='200' title='批次' />-->
          <!--            <vxe-table-column field="orderName" align="left" width="180" header-align="center" title="所属工单" />
                    <vxe-table-column field="receiveDate" width="140" title="领用日期" />-->
        </vxe-table>
      </div>
    </div>
    <TrainPlanList ref='trainPlanModal' @ok='onSelectPlan' />
    <!--    <VerifyCheckModal ref='verifyCheckModal' @ok='findList' />-->
    <MaterialConsumeModal ref='materialConsumeModal' @ok='verifyOrderMaterialListSuccess' />
  </div>
</template>

<script>
// import VerifyCheckModal from './modules/VerifyCheckModal'
import MaterialConsumeModal from './modules/MaterialConsumeModal'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import TrainPlanList from '@views/tiros/common/selectModules/TrainPlanList'
import NaSelectDate from '@comp/tiros/Na-select-date'
import {
  listMaterialCostCompare,
  materialCostCompareExport,
  verifyBatch
} from '@api/tirosMaterialApi'
import { getSerialList } from '@/api/tirosGroupApi'

export default {
  name: 'MaterialComsume',
  components: { LineSelectList, NaSelectDate, TrainPlanList, MaterialConsumeModal },
  data() {
    return {
      exportLoading: false,
      moreVisible: false,
      dictTrainStr: '',
      tableData: [],
      consumeOrderInfo: '',
      compareInfo: '',
      dateTime: [],
      planName: null,
      selecteList: [],
      expand: false,
      queryParam: {
        planId: null,
        groupId: null,
        amountIsDiff: undefined,
        isMustList: undefined,
        materialSearchText: null,
        useCategory: null
      }
    }
  },
  mounted() {
    getSerialList({
      status: 2,
      pageNo: 1,
      pageSize: 1
    }).then((res) => {
      if (res.success && res.result.records.length) {
        const item = res.result.records[0]
        this.queryParam.planId = item.id
        this.planName = item.planName
        // this.queryParam.lineId = item.lineId
        // this.onLineChange(item.lineId)
        // this.queryParam.trainNo = item.trainNo
        this.findList()
      }
    })
  },
  methods: {
    // handleSysFocus() {
    //   if (!this.queryParam.lineId) this.$message.warn('请先选择线路!')
    // },
    // cellClickEvent({ row, column, $event }) {
    //   if (column.property === 'isVerify_dictText' && row.isVerify === 1) {
    //     this.$refs.verifyCheckModal.show(row, true)
    //   } else if (column.property === 'isVerify_dictText' && row.isVerify === 0) {
    //     this.verifyCheck(row)
    //   }
    // },
    cellStyle({ row, rowIndex, column, columnIndex }) {
      if (['amountIsDiff_dictText'].indexOf(column.property) > -1) {
        if (row.amountIsDiff === 1) {
          return {
            backgroundColor: 'red',
            color: '#fff'
          }
        } else if (row.consumeAmount <= 0) {
          return {
            backgroundColor: '#f60',
            color: '#fff'
          }
        }
      } else if (['isVerify_dictText'].indexOf(column.property) > -1) {
        let style = {
          cursor: 'pointer'
        }
        if (row.isVerify == 1) {
          style.backgroundColor = '#12bd12'
          style.color = '#fff'
        }
        return style
      }
    },
    exportList() {
      this.exportLoading = true
      materialCostCompareExport(this.queryParam).then((data) => {
        if (!data) {
          this.$message.warning('文件下载失败')
          return
        }
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(new Blob([data], { type: 'application/vnd.ms-excel' }), fileName + '.xls')
        } else {
          let url = window.URL.createObjectURL(new Blob([data], { type: 'application/vnd.ms-excel' }))
          let link = document.createElement('a')
          link.style.display = 'none'
          link.href = url
          link.setAttribute('download', '物资消耗核实.xls')
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link) //下载完成移除元素
          window.URL.revokeObjectURL(url) //释放掉blob对象
        }
        this.exportLoading = false
      }).catch((err) => {
        this.exportLoading = false
      })
    },
    selectChangeEvent({ records }) {
      this.selecteList = JSON.parse(JSON.stringify(records))
    },
    // batchCheck() {
    //   let selectRecords = this.selecteList
    //   console.log(selectRecords)
    //   if (selectRecords.filter((item) => item.isVerify === 1).length) {
    //     this.$message.warning('已核实的物资不能再核实')
    //     return
    //   }
    //   this.$confirm({
    //     content: `是否核实选中${selectRecords.length}数据？`,
    //     onOk: () => {
    //       let orderMaterialIds = selectRecords.map(function(obj, index) {
    //         return obj.orderMaterialIds
    //       }).join(',')
    //       return verifyBatch({ orderMaterialIds: orderMaterialIds }).then((res) => {
    //         console.log(res)
    //         if (res.success) {
    //           this.$message.success('操作成功')
    //           this.findList()
    //         } else {
    //           this.$message.warning(res.message)
    //         }
    //       })
    //     }
    //   })
    // },
    // verifyCheck(record) {
    //   if (record.isVerify === 1) {
    //     this.$message.warning('该物资消耗已经核实过')
    //     return
    //   }
    //   this.$refs.verifyCheckModal.show(record)
    // },
    findList() {
      // if (!this.queryParam.lineId || !this.queryParam.trainNo) {
      //   this.$message.warning('请选择线路和车辆')
      //   return
      // }
      if (!this.queryParam.planId) {
        this.$message.warning('请选择列计划')
        return
      }
      listMaterialCostCompare(this.queryParam).then((res) => {
        if (res.success) {
          this.selecteList = []
          this.tableData = res.result.compareResultList
          this.consumeOrderInfo = res.result.consumeOrderInfo
          this.compareInfo = res.result.compareInfo
        } else {
          this.$message.error(res.message)
        }
      })
    },
    // onLineChange(val, option) {
    //   this.dictTrainStr = 'bu_train_info,train_no,train_no,status=1 and line_id=\'' + val + '\''
    // },
    // onDateChange() {
    //   if (this.dateTime[0]) {
    //     this.queryParam.startDate = this.dateTime[0].format('YYYY-MM-DD')
    //   } else {
    //     this.queryParam.startDate = null
    //   }
    //   if (this.dateTime[1]) {
    //     this.queryParam.endDate = this.dateTime[1].format('YYYY-MM-DD')
    //   } else {
    //     this.queryParam.endDate = null
    //   }
    // },
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
        this.queryParam.planId = element.id
        // this.queryParam.trainNo = element.trainNo
        // this.queryParam.lineId = element.lineId
        this.planName = element.planName
        // this.queryParam.startDate = element.startDate
        // this.queryParam.endDate = element.finishDate
        // console.log(element)
        // this.dateTime = [this.$moment(element.startDate), this.$moment(element.finishDate)]
      })
      this.findList()
    },
    verifyOrderMaterialListSuccess() {
      setTimeout(() => {
        this.findList()
      }, 1000)
    }
  }
}
</script>

<style lang='less' scoped>
.bodyWrapper {
  .table-page-body {
    &.more-visible {
      height: calc(100% - 106px) !important;
    }
  }
}
</style>