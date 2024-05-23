<template>
  <!-- 工器具类型选择（物资类型) -->
  <a-modal
    width="90%"
    :title="title"
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
          <a-col :md="7" :sm="24">
            <a-form-item label="搜索">
              <a-input placeholder="请输入工器具名称或编码" v-model="queryParam.searchText"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="24">
            <a-form-item label="分类">
              <j-dict-select-tag v-model="queryParam.category1" dictCode="bu_tools_type" :disabledArray="canSelectType" />
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
        max-height="100%"
        style="height: calc(80vh - 130px);"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :radio-config="!multiple ? {trigger: 'row', checkMethod: checkRadioMethod} : {}"
        :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true , checkMethod: checkRadioMethod} : {}"
        :edit-config="{trigger: 'click', mode: 'cell',  showIcon:'true'}"
        @cell-click="onCellClick"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
        <vxe-table-column field="code" title="编码" width="150"></vxe-table-column>
        <vxe-table-column field="name" title="名称" align="left" header-align="center"></vxe-table-column>
   <!--     <vxe-table-column field="warehouse" title="库位" width="10%"></vxe-table-column>-->
        <vxe-table-column field="category1_dictText" title="类别" width="150"></vxe-table-column>
        <!-- 类别字段？？？？？？？？？？？？ -->
       <!-- <vxe-table-column field="extAttr" title="属性" width="10%"></vxe-table-column>-->
        <vxe-table-column field="unit" title="单位" width="150"></vxe-table-column>
        <!--<vxe-table-column field="price" title="单价" width="5%"></vxe-table-column>-->
       <!-- <vxe-table-column field="amount" title="当前库存" width="200"></vxe-table-column>-->
      <vxe-table-column
        v-if="batch"
        field="num"
        title="所需数量"
        width="200"
        :edit-render="{name: '$input', immediate: true, props: {type: 'number',Min:0}}"
      >
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
import { getToolTypeList } from '@/api/tirosMaterialApi'
export default {
  name: 'ToolTypeSelect',
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
    canSelectType: {
      type: Array,
      default: function () {
        return [4,5,6]
      }
    },
    batch: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      title:'工器具类型选择',
      queryParam: {
        type: '2',
        searchText: '',
        category1: null,
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
    showModal() {
      this.visible = true
      if (this.canSelectType && this.canSelectType.length > 0) {
        this.queryParam.category1 = this.canSelectType[0]
      }
      this.findList()
    },
    findList() {
      this.loading = true
      getToolTypeList(this.queryParam).then((res) => {
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
      this.visible = false
    },
    checkRadioMethod ({ row }) {
      if (!this.canSelectType) {
        return true
      }
      return this.canSelectType.indexOf(row.category1) !== -1;
    },
    // fix 点击数量编辑时取消选择问题
    onCellClick({row, column}){
      if (column.property === 'num') {
        this.$refs.listTable.setCheckboxRow([row], true)
      }
    }
  },
}
</script>

<style scoped>
.limitTitle {
  font-size: 20px;
  font-weight: 600;
  height: 40px;
}
.limitHeight {
  height: calc(80vh - 110px);
  overflow-y: auto;
}
</style>