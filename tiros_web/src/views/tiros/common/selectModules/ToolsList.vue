<template>
  <!-- 实际的工器具选择 -->
  <a-modal
    width="90%"
    title="工器具选择"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    centered
    :destroyOnClose="true"
    :bodyStyle="{ height: '80vh' }"
  >
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="工器具名称">
              <a-input placeholder="请输入工器具名称或编码" v-model="queryParam.searchText"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="工器具分类">
              <j-dict-select-tag placeholder="请选择工器具分类" v-model="queryParam.toolType" dictCode="bu_tools_type" :disabled="canSelectType && canSelectType.length===1"  />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="工班">
              <j-dict-select-tag
                v-model="queryParam.groupId"
                dictCode="sys_depart,depart_name,id,org_category=4"
                placeholder="请选择工班"
              />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="8">
            <a-button @click="findList">查询</a-button>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(80vh - 130px);">
      <vxe-table
        border
        ref="listTable"
        style="height: calc(80vh - 130px);"
        max-height="100%"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :cell-class-name="cellClassName"
        :radio-config="!multiple ? {trigger: 'row', checkMethod: checkRadioMethod} : {}"
        :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true , checkMethod: checkRadioMethod} : {}"
        :edit-config="{trigger: 'click', mode: 'cell',  showIcon:'true'}"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
        <vxe-table-column field="code" title="工器具类型编码" ></vxe-table-column>
        <vxe-table-column field="name" title="工器具名称" ></vxe-table-column>
        <vxe-table-column field="model" title="规格型号" ></vxe-table-column>
        <vxe-table-column field="assetCode" title="资产编码" ></vxe-table-column>
        <vxe-table-column field="serialNo" title="出厂编号" ></vxe-table-column>
        <vxe-table-column field="toolType_dictText" title="类别" ></vxe-table-column>
        <vxe-table-column field="unit" title="单位" ></vxe-table-column>
        <vxe-table-column field="groupName" title="所属班组" ></vxe-table-column>
        <vxe-table-column field="nextCheckTime" title="下次送检" width="100"></vxe-table-column>
        <vxe-table-column field="isOverTime" title="是否逾期" width="100px">
          <template v-slot="{row}" >
              {{getIsOverTimeStatus(row)}}
          </template>
        </vxe-table-column>
        <vxe-table-column field="isOverTimeDays" title="逾期天数" width="100">
          <template v-slot="{ row }">
            {{ getIsOverTimeDays(row) }}
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

  </a-modal>
</template>

<script>
import { getToolsList } from '@/api/tirosMaterialApi'
export default {
  name: 'ToolsList',
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
    canSelectType: {
      type: Array,
      default: null,
    },
    groupId:{
      type: String,
      default: null
    }
  },
  data() {
    return {
      queryParam: {
        searchText: '',
        // groupId: this.$store.getters.userInfo.departId,
        toolType: '6',
        groupId:'',
        pageNo: 1,
        pageSize: 10,
      },
      allAlign: 'center',
      totalResult: 0,
      tableData: [],
      visible: false,
      confirmLoading: false
    }
  },
  methods: {
    // updata 2021/04/26 传入物资类型筛选查询 默认Null
    showModal(materialTypeId = null) {
      this.visible = true
      if (this.canSelectType && this.canSelectType.length > 0) {
        this.queryParam.toolType = this.canSelectType[0]+''
      }
      if (this.groupId) {
        this.queryParam.groupId = this.groupId
      }
      if (materialTypeId) {
        this.queryParam.materialTypeId = materialTypeId
      }
      this.findList()
    },
    findList() {
      this.loading = true
      getToolsList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
        this.tableData.forEach((item) => {
          this.$set(item, 'num', 1)
        })
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleOk() {
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords()
      } else {
        if(this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      this.$emit('ok', selectRecords)
      this.visible = false
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.queryParam.materialTypeId = this.$options.data().queryParam.materialTypeId;
      this.visible = false
    },
    checkRadioMethod ({ row }) {
      if (!this.canSelectType) {
        return true
      }
      if (this.canSelectType.indexOf(row.toolType) > -1) {
        return true
      } else {
        return false
      }
    },
    getIsOverTimeStatus(row){
      if (row.nextCheckTime) {
        let dateNow = this.$moment(new Date()).format('YYYY-MM-DD')
        let diffDay = this.$moment(row.nextCheckTime).diff(dateNow, 'day')
        return diffDay < 0 ? '是' : '否'
      }else{
        return '否'
      }
    },
    // 逾期数据单元格样式设定
    cellClassName({ row, rowIndex, column, columnIndex }) {
      let columnsArr = ['nextCheckTime', 'isOverTime', 'isOverTimeDays']
      if (columnsArr.includes(column.property)) {
        if (row.nextCheckTime) {
          let dateNow = this.$moment(new Date()).format('YYYY-MM-DD')
          let diffDay = this.$moment(row.nextCheckTime).diff(dateNow, 'day')

          if (column.property === 'nextCheckTime') {
            if (diffDay <= 0) {
              return 'table-cell-bg-red'
            }
            if (diffDay > 0 && diffDay <= 15) {
              return 'table-cell-bg-yellow'
            }
          }

          if (column.property === 'isOverTime') {
            if (diffDay < 0) {
              return 'table-cell-bg-red'
            }
          }
          if (column.property === 'isOverTimeDays') {
            if (diffDay < 0) {
              return 'table-cell-bg-red'
            }
          }
        }
      }
    },
    // 计算逾期天数
    getIsOverTimeDays(row) {
      if (row.nextCheckTime) {
        let dateNow = this.$moment(new Date()).format('YYYY-MM-DD')
        let diffDay = this.$moment(row.nextCheckTime).diff(dateNow, 'day')
        if (diffDay < 0) {
          return Math.abs(diffDay)
        }
      }
      return ''
    },
  },
}
</script>

<style scoped>
.limitTitle {
  font-size: 20px;
  font-weight: 600;
  height: 40px;
}

</style>