<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="4" :sm="12">
            <a-form-item label="物资">
              <a-input placeholder="物资编码或者名称" v-model="queryParam.searchText"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="12">
            <a-form-item label="借用">
              <j-dict-select-tag v-model="queryParam.dateType" placeholder="请选择" dictCode="bu_date_type" />
            </a-form-item>
          </a-col>
          <a-col
            :md="3"
            :sm="12"
            v-if="queryParam.dateType === '1' || queryParam.dateType === 1 || queryParam.dateType === '2'"
          >
            <a-form-item>
              <a-date-picker
                mode="year"
                placeholder="请选择年份"
                format="YYYY"
                v-model="year"
                :open="yearPickShow"
                @panelChange="handlePanelChange"
                @openChange="handleOpenChange"
                @change="handleChanYearChange"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="12" v-if="queryParam.dateType === '2'">
            <a-form-item>
              <j-dict-select-tag v-model="queryParam.quarter" placeholder="请选择" dictCode="bu_quarter_type" />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="12" v-if="queryParam.dateType === '3'">
            <a-form-item>
              <a-month-picker placeholder="选择月份" v-model="month" @change="handleMonthChange"> </a-month-picker>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="12" v-if="queryParam.dateType === '4'">
            <a-form-item>
              <a-range-picker v-model="rangeDate"> </a-range-picker>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :md="4" :sm="12">
              <a-form-item label="归还类型">
                <j-dict-select-tag
                  v-model="queryParam.returnType"
                  placeholder="请选择"
                  dictCode="bu_material_return_type"
                />
              </a-form-item>
            </a-col>
            <a-col :md="4" :sm="12">
              <a-form-item label="归还状态">
                <j-dict-select-tag
                  v-model="queryParam.returnStatus"
                  placeholder="请选择"
                  dictCode="bu_material_return_status"
                />
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="8" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button @click="deleteRecord">删除</a-button>
                <a-button @click="handlerReturn">已归还</a-button>
                <a @click.stop="handleToggleSearch">
                  {{ toggleSearchStatus ? '收起' : '更多' }}
                  <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
                </a>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div :style="tableHeight.top">
      <vxe-table
        border
        :align="allAlign"
        ref="listTable"
        :max-height="tableHeight.size"
        :style="tableHeight.bottom"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column field="materialTypeCode" title="物资编码" width="120">
          <template v-slot="{ row }">
            <a @click.stop="borrowDetail(row)">{{ row.materialTypeCode }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="materialTypeName"
          title="物资名称"
          width="150"
          header-align="center"
          align="left"
        ></vxe-table-column>
        <vxe-table-column
          field="materialTypeSpec"
          title="物资描述"
          width="200"
          header-align="center"
          align="left"
        ></vxe-table-column>
        <vxe-table-column
          field="amount"
          title="借用数量"
          width="100"
          header-align="center"
          align="right"
        ></vxe-table-column>
        <vxe-table-column field="materialTypeUnit" title="物资单位" width="100"></vxe-table-column>
        <vxe-table-column field="warehouseName" title="所属仓库" width="120"></vxe-table-column>
        <vxe-table-column field="deptName" title="借用组织" width="120"></vxe-table-column>
        <vxe-table-column field="borrowUserName" title="借用人员" width="100"></vxe-table-column>
        <vxe-table-column field="borrowDate" title="借用日期" width="100"></vxe-table-column>
        <vxe-table-column field="returnType_dictText" title="归还类型" width="120"></vxe-table-column>
        <vxe-table-column field="returnStatus_dictText" title="归还状态" width="120"></vxe-table-column>
        <vxe-table-column
          field="remark"
          title="备注"
          min-width="100"
          header-align="center"
          align="left"
        ></vxe-table-column>
        <vxe-table-column title="操作" width="80" fixed="right">
          <template v-slot="{ row }">
            <a @click.stop="handleEdit(row)">编辑</a>
          </template>
        </vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
    <borrow-item-modal ref="modalForm" @ok="loadData()"></borrow-item-modal>
    <borrow-detail-modal ref="detailForm"></borrow-detail-modal>
  </div>
</template>

<script>
import { confirmBorrowReturn, delBorrow, getBorrow } from '../../../../api/tirosMaterialApi'
import BorrowItemModal from '../modules/BorrowItemModal'
import BorrowDetailModal from '../modules/BorrowDetailModal'
import moment from 'moment'
import 'moment/locale/zh-cn'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'Borrow',
  components: { BorrowItemModal, BorrowDetailModal },
  data() {
    return {
      year: null,
      month: null,
      component: null,
      rangeDate: [],
      toggleSearchStatus: false,
      queryParam: {
        searchText: '',
        returnStatus: '',
        returnType: '',
        quarter: '',
        year: '',
        month: '',
        endDate: '',
        startDate: '',
        dateType: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      yearPick: null, //年选择器的值
      yearPickShow: false, //年选择器的显示隐藏
      tableHeight: {
        top: 'height: calc(100vh - 225px)',
        bottom: 'height: calc(100vh - 225px)',
        size: '100%',
      },
    }
  },

  created() {
    this.findList()
  },

  watch: {
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
        this.changeHeight()
      },
    },
  },
  methods: {
    findList() {
      let { dateType, year, quarter, month } = this.queryParam
      switch (dateType) {
        case '1':
          if (everythingIsEmpty(year)) {
            this.$message.warning('请选择年份！')
            return
          }
          break
        case '2':
          if (everythingIsEmpty(year)) {
            this.$message.warning('请选择年份！')
            return
          }
          if (everythingIsEmpty(quarter)) {
            this.$message.warning('请选择季度！')
            return
          }
          break
        case '3':
          if (everythingIsEmpty(this.month)) {
            this.$message.warning('请选择月份！')
            return
          }
          break
        case '4':
          if (everythingIsEmpty(this.rangeDate)) {
            this.$message.warning('请选择时间段！')
            return
          }
          break
        default:
          break
      }
      if (!everythingIsEmpty(this.rangeDate) && dateType === '4') {
        this.queryParam.startDate = moment(this.rangeDate[0]).format('YYYY-MM-DD')
        this.queryParam.endDate = moment(this.rangeDate[1]).format('YYYY-MM-DD')
      }
      if (!everythingIsEmpty(this.month) && dateType === '3') {
        this.queryParam.year = moment(this.month).year()
        this.queryParam.month = moment(this.month).month() + 1
      }
      getBorrow(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    changeHeight() {
      if (this.toggleSearchStatus && !everythingIsEmpty(this.queryParam.dateType)) {
        this.tableHeight = {
          top: 'height: calc(100vh - 265px)',
          bottom: 'height: calc(100vh - 265px)',
          size: '100%',
        }
      } else {
        this.tableHeight = {
          top: 'height: calc(100vh - 225px)',
          bottom: 'height: calc(100vh - 225px)',
          size: '100%',
        }
      }
    },
    loadData() {
      this.findList()
      this.$emit('load')
    },
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit(record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delBorrow('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handlerReturn() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否设置选中${selectRecords.length}数据为已归还？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            confirmBorrowReturn('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    borrowDetail(row) {
      this.$refs.detailForm.detail(row)
    },
    handlerReadyMaterial() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.length > 1) {
          this.$message.warning('只能选中一条数据!')
        } else {
          this.$refs.readyMaterial.edit(row.id)
        }
      } else {
        this.$message.error('尚未选中任何数据!')
      }
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
    handleChanYearChange(value) {
      if (everythingIsEmpty(value)) this.queryParam.year = ''
    },
    handleMonthChange(value) {
      if (everythingIsEmpty(value)) {
        this.queryParam.year = ''
        this.queryParam.month = ''
      }
    },
    handleToggleSearch() {
      this.toggleSearchStatus = !this.toggleSearchStatus
      this.changeHeight()
    },
  },
}
</script>