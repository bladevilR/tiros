<template>
<a-modal
  title="领用物资明细"
  :width="'80%'"
  :bodyStyle="{height:'70vh'}"
  :visible="visible"
  :spinning="confirmLoading"
  :closable="true"
  :destroyOnClose="true"
  @cancel="handleCancel"
  :footer="null">
        <a-spin :spinning="confirmLoading">
        <vxe-table
          border
          ref="listTable"
          :align="allAlign"
          :data="tableData"
          show-overflow="ellipsis">
          <vxe-table-column field="materialTypeCode" title="物资编码" width="150"></vxe-table-column>
          <vxe-table-column field="materialTypeName" title="物资名称" width="180" align="left"></vxe-table-column>
          <vxe-table-column field="materialTypeSpec" title="规格型号" min-width="180" align="left"></vxe-table-column>
          <vxe-table-column field="materialTypeUnit" title="单位" width="100"></vxe-table-column>
          <vxe-table-column field="amount" title="领用数量" width="100" align="right"></vxe-table-column>
          <vxe-table-column field="receive" title="发料数量" width="100" align="right"></vxe-table-column>
          <vxe-table-column field="warehouseName" title="库位" width="120"></vxe-table-column>
          <vxe-table-column field="palletName" title="存放托盘" width="150"></vxe-table-column>
          <vxe-table-column field="remark" title="备注" width="150"></vxe-table-column>
        </vxe-table>
        </a-spin>
  </a-modal>
</template>
<script>
  import { getApplyInfo, getApplyList } from '../../../../api/tirosMaterialApi'

  export default {
    name:'ApplyDetail',
    data(){
      return{
        confirmLoading:false,
        tableData:[],
        allAlign: 'center',
        visible:false,
        queryParam:{
          applyId:''
        }
      }
    },
    created() {
    },
    methods:{
      detail(id){
        this.visible=true
        this.queryParam.applyId=id;
        this.findList();
      },
      findList(){
        this.confirmLoading = true
        getApplyInfo(this.queryParam).then((res) => {
          this.confirmLoading = false
          this.tableData = res.result
        })
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