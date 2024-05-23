<template>
  <div class='bodyWrapper'>
    <!-- 查询区域 -->
    <div class='table-page-search-wrapper'>
      <a-form layout='inline'>
        <a-row :gutter='24'>
          <a-col :md='5' :sm='24'>
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
          <a-col :md='3' :sm='24'>
            <a-form-item label='线路'>
              <line-select-list v-model='lineId'></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md='3' :sm='24'>
            <a-form-item label='车辆'>
              <j-dict-select-seach-tag @focus='handleSysFocus' v-model='queryParam.trainNo' :dictCode='dictCodeStr'
                                       placeholder='车辆' />
            </a-form-item>
          </a-col>
          <!--          <a-col :md="5" :sm="24">
                      <a-form-item label="时间">
                        <a-range-picker format="YYYY-MM-DD" :placeholder="['开始时间', '结束时间']" @change="onDateChange"  :defaultPickerValue="defaultDateRange" />
                      </a-form-item>
                    </a-col>-->
          <a-col :md='3' :sm='24'>
            <a-form-item label='工班'>
              <j-dict-select-tag
                v-model='queryParam.groupId'
                placeholder='请选择'
                :dictCode='dictGroupStr'
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :md='4' :sm='24'>
            <a-form-item label='物资'>
              <a-input placeholder='物资编号或名称' v-model='queryParam.materialSearchText' allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='批次'>
              <a-input placeholder='批次号' v-model='queryParam.tradeNo' allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='工单'>
              <a-input placeholder='工单编号或名称' v-model='queryParam.orderSearchText' allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='全部消耗成功'>
              <j-dict-select-tag
                v-model='queryParam.syncResultSuccess'
                placeholder='请选择'
                dictCode='bu_state'
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :md='3' :sm='24'>
            <span style='float: left; overflow: hidden' class='table-page-search-submitButtons'>
              <a-space>
                <a-button @click='findList'>查询</a-button>
                <a-button :loading='exportLoading' @click='exportEXFile()'>导出</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div :style='tableHeight.top'>
      <vxe-table
        border
        :align='allAlign'
        ref='listTable'
        :max-height='tableHeight.size'
        :style='tableHeight.bottom'
        :data='tableData'
        show-overflow='tooltip'
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column type='checkbox' width='40' fixed='left'></vxe-table-column>
        <vxe-table-column field='lineName' title='线路' width='100' header-align='center' align='center' />
        <vxe-table-column field='trainNo' title='车辆' width='100' header-align='center' align='center' />
        <vxe-table-column field='orderCode' title='工单号' width='120'/>
        <vxe-table-column
          field='orderName'
          title='工单名称'
          width='200'
          header-align='center'
          align='left'
        >
          <template v-slot='{ row }'>
            <!-- :disabled="row.orderStatus !== 0" -->
            <a-button type='link' @click.stop='viewOrder(row)'>{{ row.orderName }}</a-button>
          </template>
        </vxe-table-column>
        <vxe-table-column field='syncTime' title='消耗发送时间' width='180'/>
        <vxe-table-column field='syncResult' title='消耗状态' width='120'/>
        <vxe-table-column field='syncResultTime' title='消耗状态时间' width='180'/>
        <vxe-table-column field='groupName' align='left' header-align='center' title='班组' width='100'/>
        <vxe-table-column field='materialTypeCode' title='物料编号' width='120'/>
        <vxe-table-column field='materialTypeName' align='left' header-align='center' title='物资名称' width='180'/>
        <vxe-table-column field='materialTypeSpec' align='left' header-align='center' title='物资描述' width='200'/>
        <vxe-table-column field='applyAmount' title='申请数量' align='right' header-align='center' width='100'/>
        <vxe-table-column field='receiveAmount' title='发放数量' align='right' header-align='center' width='100'/>
        <vxe-table-column field='amount' title='该条分配数量' align='right' header-align='center' width='140'/>
        <vxe-table-column field='useCategory_dictText' title='物资类别' width='100'/>
        <vxe-table-column field='warehouseName' title='所属组织' width='120'/>
        <vxe-table-column field='locationName' title='库位' width='120' align='left'header-align='center'/>
        <vxe-table-column field='tradeNo' title='批次' width='180' align='left'header-align='center'/>
        <vxe-table-column field='palletName' title='存放托盘' min-width='100' header-align='center' align='left' />
        <vxe-table-column field='sendUserName' title='发料人' min-width='100' header-align='center' align='left' />
        <vxe-table-column field='confirmUserName' title='领料人' min-width='100' header-align='center' align='center' />
        <vxe-table-column field='confirmTime' title='领料时间' width='180' header-align='center' align='left' />
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync='queryParam.pageNo'
        :page-size.sync='queryParam.pageSize'
        :total='totalResult'
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change='handlePageChange'
      ></vxe-pager>
      <TrainPlanList ref='trainPlanModal' @ok='onSelectPlan'></TrainPlanList>
      <view-work-order-modal ref='viewOrderModal'></view-work-order-modal>
    </div>
  </div>
</template>


<script>
import ViewWorkOrderModal from '@views/tiros/dispatch/workOrder/ViewWorkOrderModal'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { getReceiveList, exportEXFile } from '@api/tirosMaterialApi'
import { everythingIsEmpty } from '@/utils/util'
import TrainPlanList from '@views/tiros/common/selectModules/TrainPlanList'

export default {
  name: 'List',
  components: { LineSelectList, TrainPlanList, ViewWorkOrderModal },
  data() {
    return {
      planName: null,
      component: null,
      lineId: null,
      exportLoading: false,
      dictCodeStr: '',
      dictGroupStr: 'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
      queryParam: {
        planId: null,
        lineId: null,
        trainNo: null,
        groupId: null,
        materialSearchText: null,
        orderSearchText: null,
        startDate: '',
        endDate: '',
        syncResultSuccess: null,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      tableHeight: {
        top: 'height: calc(100vh - 265px)',
        bottom: 'height: calc(100vh - 265px)',
        size: '100%'
      }
    }
  },
  watch: {
    lineId(newVal, oldVal) {
      if (newVal) {
        this.dictCodeStr = 'bu_train_info,train_no,train_no,line_id=' + newVal
      } else {
        this.dictCodeStr = ''
      }
    }
  },
  created() {
    this.findList()
  },
  methods: {
    handleSysFocus() {
      if (!this.lineId) this.$message.warn('请先选择线路!')
    },
    viewOrder(row) {
      this.$refs.viewOrderModal.showModal(row.orderId)
    },
    openTrainPlanModal() {
      this.$refs.trainPlanModal.showModal()
      this.$refs.planSelect.blur()
    },
    clearValue() {
      this.queryParam.planId = null
      this.planName = null
    },
    onSelectPlan(data) {
      data.forEach((element) => {
        this.lineId = element.lineId
        this.queryParam.planId = element.id
        this.planName = element.planName
        this.$forceUpdate()

        setTimeout(() => {
          this.queryParam.trainNo = element.trainNo
        }, 500)
      })
    },
    onDateChange(value, dateString) {
      this.queryParam.startDate = dateString[0]
      this.queryParam.endDate = dateString[1]
    },
    exportEXFile() {
      let syncResultSuccess = 0
      if (this.queryParam.syncResultSuccess) {
        syncResultSuccess = this.queryParam.syncResultSuccess
      }
      if (!(syncResultSuccess && syncResultSuccess !== 1)) {
        this.$message.warning('请筛选全部消耗成功的数据')
        return
      }

      // 导入
      this.queryParam.lineId = this.lineId
      this.exportLoading = true
      exportEXFile(this.queryParam).then((data) => {
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

          link.setAttribute('download', '领料明细.xls')
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
    findList() {
      this.queryParam.lineId = this.lineId
      getReceiveList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    }
  }
}
</script>