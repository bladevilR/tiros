<template>
  <a-modal
    :title="title"
    :width="'85%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    :footer="null"
    :destroyOnClose="true"
  >
    <div >
      <div style="height: calc(100vh - 220px)">
        <vxe-table
          border
          style="height: calc(100vh - 220px);"
          max-height="90%"
          ref="listTable"
          :align="allAlign"
          :data="tableData"
          show-overflow="tooltip"
        >
          <vxe-table-column field="contractNo" title="合同编号" width="20%" header-align="center"
                            align="center"></vxe-table-column>
          <vxe-table-column field="contractName"  width="30%" title="合同名称" header-align="center" align="left"></vxe-table-column>
          <vxe-table-column field="trainAmount" title="车辆列数" width="10%" header-align="center" align="right"></vxe-table-column>
          <vxe-table-column field="contractAmount" title="合同总价" width="10%" header-align="center" align="right"></vxe-table-column>
          <vxe-table-column field="singlePrice" title="单列价格" width="10%" header-align="center" align="right"></vxe-table-column>
          <vxe-table-column field="partPrice" title="部件单价" width="10%" header-align="center" align="right"></vxe-table-column>
          <vxe-table-column field="signDate" title="签订日期" width="10%" align="center"></vxe-table-column>
        </vxe-table>
      </div>
    </div>
  </a-modal>
</template>

<script>
import { getContractInfoPriceList } from '@/api/tirosOutsourceApi'

export default {
  name: 'ContractViewPay',
  data () {
    return {
      visible: false,
      tableData: [],
      allAlign: 'center',
      title:'合同价格查询',
      confirmLoading: false
    }
  },
  methods: {
    show (value) {
    this.visible = true
    this.findList(value.ids)
    },
    findList (ids) {
      this.confirmLoading = true
      getContractInfoPriceList({
          ids: ids
      }).then((res) => {
        this.confirmLoading = false
        this.tableData = res.result
      })
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
    }
  }
}

</script>
<style lang="less">
.table-footer .col-red {
  background-color: #2db7f5;
  color: #fff;
}
</style>