<template>
  <a-modal
    width="70%"
    title="故障原因选择"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    centered
    :bodyStyle="{ height: '80vh' }"
    :destroyOnClose="true"
  >
    <!-- <a-card :bordered="false"> -->
    <div class="table-page-search-wrapper">
      <a-form
        layout="inline"
        @keyup.enter.native="findList"
        :label-col="formItemLayout.labelCol"
        :wrapper-col="formItemLayout.wrapperCol"
      >
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="">
              <a-input placeholder="请输入故障原因编码或者描述" v-model="queryParam.searchText"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="所属分类">
              <!--              <j-dict-select-tag v-model="queryParam.categoryId" dictCode="bu_fault_category,category_code,id" />
              -->
              <a-input
                readonly
                allowClear
                v-model="categoryCode"
                placeholder="请选择所属分类"
                @click="openSelectTypeModal(1)"
                ref="categoryIdSelect"
              >
                <a-icon slot="suffix" type="ellipsis" />
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="所属故障代码">
              <!--              <j-dict-select-tag v-model="queryParam.faultCodeId" dictCode="bu_fault_code,fault_code,id" />
              -->
              <a-input
                readonly
                allowClear
                v-model="faultCode"
                placeholder="请选择所属故障代码"
                @focus="openSelectTypeModal(2)"
                ref="faultCodeIdSelect"
              >
                <a-icon slot="suffix" type="ellipsis" />
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="8">
            <a-form-item>
              <a-button @click="findList">查询</a-button>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(80vh - 130px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(80vh - 130px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        highlight-current-row
        show-overflow="tooltip"
        :radio-config="!multiple ? { trigger: 'row' } : {}"
        :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true } : {}"
        :edit-config="{ trigger: 'click', mode: 'cell', showIcon: 'true' }"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
        <vxe-table-column type="seq" width="60" title="序号"></vxe-table-column>
        <vxe-table-column field="reasonCode" title="故障原因代码" width="150"></vxe-table-column>
        <vxe-table-column field="reasonDesc" title="故障原因描述" align="left" header-align="center"></vxe-table-column>
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
    <fault-category-list ref="categorySelect" :multiple="false" @ok="addCategoryTarget"></fault-category-list>
    <fault-code-list ref="codeSelect" :multiple="false" @ok="addCodeTarget"></fault-code-list>
  </a-modal>
</template>

<script>
import { getBreakdownReasonList } from '@api/tirosDispatchApi'
import FaultCategoryList from '@views/tiros/common/selectModules/FaultCategoryList'
import FaultCodeList from '@views/tiros/common/selectModules/FaultCodeList'
import { everythingIsEmpty } from '@/utils/util'
export default {
  name: 'FaultReasonList',
  components: { FaultCategoryList, FaultCodeList },
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      categoryCode: '',
      faultCode: '',
      queryParam: {
        searchText: '',
        categoryId: '',
        faultCodeId: '',
        pageNo: 1,
        pageSize: 10,
      },
      allAlign: 'center',
      totalResult: 0,
      tableData: [],
      visible: false,
      confirmLoading: false,
      formItemLayout: {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 18 },
        },
      },
    }
  },
  watch:{
    categoryCode(newValue, oldValue){
      console.log(newValue);
      console.log(oldValue);
      if (!newValue) {
        this.queryParam.categoryId = null
      }
    },
     faultCode(newValue, oldValue){
      if (!newValue) {
        this.queryParam.faultCodeId = null
      }
    },
  },
  methods: {
    showModal(id) {
      this.visible = true
      this.queryParam.faultCodeId = id
      this.findList()
    },
    findList() {
      this.loading = true
      getBreakdownReasonList(this.queryParam).then((res) => {
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
      this.categoryCode = ''
      this.faultCode = ''
      this.queryParam.searchText = ''
      this.queryParam.categoryId = ''
      this.queryParam.faultCodeId = ''
    },
    openSelectTypeModal(type) {
      switch (type) {
        case 1:
          this.$refs.categorySelect.showModal()
          this.$refs.categoryIdSelect.blur()
          break
        case 2:
          this.$refs.codeSelect.showModal()
          this.$refs.faultCodeIdSelect.blur()
          break
        default:
          break
      }
    },
    addCategoryTarget(data) {
      if (!everythingIsEmpty(data)) {
        this.queryParam.categoryId = data[0].id
        this.categoryCode = data[0].categoryCode
      } else {
        this.queryParam.categoryId = ''
      }
    },
    addCodeTarget(data) {
      if (!everythingIsEmpty(data)) {
        this.queryParam.faultCodeId = data[0].id
        this.faultCode = data[0].fltCode
      } else {
        this.queryParam.faultCodeId = ''
      }
    },
  },
}
</script>
<style scoped>
.limitTitle {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
  border-bottom: 1px solid #e1e1de;
}
.limitHeight {
  height: calc(80vh - 110px);
  overflow-y: auto;
}
</style>