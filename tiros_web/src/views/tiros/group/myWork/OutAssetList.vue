<template>
<!--  委外设备选择 -->
  <a-modal
    width="90%"
    title="委外设备选择"
    :visible="visible"
    :confirmLoading="loading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
    centered
    :bodyStyle="{ height: '80vh' }"
  >
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="7" :sm="24">
            <a-form-item label="设备名称">
              <a-input placeholder="请输入工器具名称或编码" v-model="queryParam.assetName" allowClear></a-input>
            </a-form-item>
          </a-col>
<!--          <a-col :md="7" :sm="24">
            <a-form-item label="工器具分类">
              <j-dict-select-tag v-model="queryParam.toolType" dictCode="bu_tools_type"   />
            </a-form-item>
          </a-col>-->
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
        show-overflow="ellipsis"
        :radio-config="!multiple ? {trigger: 'row'} : {}"
        :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true } : {}"
        :edit-config="{trigger: 'click', mode: 'cell',  showIcon:'true'}"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
        <vxe-table-column field="assetNo" title="部件编号" ></vxe-table-column>
        <vxe-table-column field="assetName" title="部件名称" ></vxe-table-column>
        <vxe-table-column field="lineName" title="所属线路" ></vxe-table-column>
        <vxe-table-column field="trainNo" title="所属车辆" ></vxe-table-column>
        <vxe-table-column field="outinNo" title="出厂单编号" ></vxe-table-column>
        <vxe-table-column field="outinName" title="出厂单编号" ></vxe-table-column>
        <vxe-table-column field="transferDate" title="移交日期" ></vxe-table-column>
        <vxe-table-column field="contractNo" title="所属合同编号" ></vxe-table-column>
        <vxe-table-column field="contractName" title="所属合同名称" ></vxe-table-column>
        <vxe-table-column field="remark" title="出厂备注" ></vxe-table-column>
      </vxe-table>
    </div>
  </a-modal>
</template>

<script>
import { getOutAssetList } from '@api/tirosGroupApi'

export default {
  name: 'OutAssetList',
  props: {
    multiple: {
      type: Boolean,
      default: false,
    },
  },
  data () {
    return {
      visible: false,
      loading: false,
      queryParam: {
        assetTypeId: '',
        trainNo: '',
        assetName: ''
      },
      allAlign: 'center',
      totalResult: 0,
      tableData: []
    }
  },
  methods: {
    showModal(trainNo) {
      this.queryParam.trainNo = trainNo
      this.visible = true
      this.findList()
    },
    findList() {
      this.loading = true

      getOutAssetList(this.queryParam).then((res) => {
        if(res.success) {
          this.loading = false
          this.tableData = res.result
        } else {
          this.$message.error('查询设备失败')
          console.error('查询出厂设备失败：',res.message)
        }
      })
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
    }
  }
}
</script>

<style scoped>

</style>