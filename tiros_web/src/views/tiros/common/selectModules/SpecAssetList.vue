<template>
  <a-modal
    width="80%"
    title="特种设备选择"
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
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item label="设备">
              <a-input v-model="queryParam.searchText" placeholder="名称或编码"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择"
                dictCode="bu_tools_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
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
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
        <vxe-table-column field="assetCode" title="设备编码" />
        <vxe-table-column field="name" title="设备名称1">
          <template v-slot="{ row }">
            <a @click.stop="showView(row)">{{ row.name }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" />
        <vxe-table-column field="workshopName" title="所属车间" />
        <vxe-table-column field="supplierName" title="厂商" />
        <vxe-table-column field="manufNo" title="出厂编码" />
        <vxe-table-column field="materialCode" title="物资编码" />
        <vxe-table-column field="brand" title="品种" />
        <vxe-table-column field="model" title="规格" />
        <vxe-table-column field="leaveFactory" title="出厂日期" />
        <vxe-table-column field="useDate" title="投入日期" />
        <vxe-table-column field="dutyUserName" title="责任人" />
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
    <SpecialMaterialView ref="viewModal"></SpecialMaterialView>
  </a-modal>
</template>

<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import SpecialMaterialView from '@views/tiros/material/specialmaterial/SpecialMaterialView'

import { getSpecassetList } from '@/api/tirosMaterialApi'

// import { getSopPage } from '@api/tirosApi'
export default {
  name: 'SpecAssetList',
  components: { LineSelectList, SpecialMaterialView },
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      queryParam: {
        pageNo: 1,
        pageSize: 10,
        searchText: '',
        status: null,
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
  methods: {
    showModal() {
      this.visible = true
      this.findList()
    },
    findList() {
      this.loading = true
      getSpecassetList(this.queryParam).then((res) => {
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
      this.queryParam.formName = ''
    },
    showView(row) {
      this.$refs.viewModal.showModal(row)
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