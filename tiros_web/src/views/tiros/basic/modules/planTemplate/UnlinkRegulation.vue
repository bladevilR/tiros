<template>
  <a-drawer
    :title="title"
    :width="1000"
    :maskClosable="true"
    :closable="true"
    :visible="visible"
    :destroyOnClose="true"
    @close="handleCancel"
  >
    <div class="tableHeight" style="height: calc(100vh - 135px)">
      <vxe-table
        border
        row-id="id"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        max-height="100%"
        style="height: calc(100vh - 135px)"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        :checkbox-config="{trigger: 'row', highlight: true,checkStrictly: true}"
        :tree-config="{ lazy: true, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod }"
      >
<!--        <vxe-table-column type="checkbox" width="40" ></vxe-table-column>-->
        <vxe-table-column  type="seq" title="序号" width="100"></vxe-table-column>
        <vxe-table-column field="title" title="名称" tree-node align="left"  header-align="center" width="250"></vxe-table-column>
        <vxe-table-column field="type_dictText" title="类型" width="100"></vxe-table-column>
        <vxe-table-column field="method_dictText" title="维保手段" width="100"></vxe-table-column>
        <vxe-table-column field="qualityLevel_dictText" title="质保等级" width="100"></vxe-table-column>
        <vxe-table-column field="outsource_dictText" title="是否委外" width="100"></vxe-table-column>
        <vxe-table-column field="safeNotice" title="安全提示" width="200" align="left"
                          header-align="center"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" width="200" align="left" header-align="center"></vxe-table-column>
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

  </a-drawer>

</template>

<script>
import { getPlanNoRelevanceDetail, getReguDeteil } from '@api/tirosApi'

export default {
  name: 'UnlinkRegulation',
  data () {
    return {
      visible:false,
      title:'未关联的规程详情',
      tableData:[],
      allAlign:'center',
      queryParam:{
        planId:'',
        reguId:'',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,

    }
  },
  methods:{
    show(value){
      this.visible=true
      this.queryParam.planId=value.planId
      this.queryParam.reguId=value.reguId
      this.findList()
    },
    findList(){
      getPlanNoRelevanceDetail(this.queryParam).then((res)=>{
        this.totalResult = res.result.total
        this.tableData = res.result.records
      })
    },
    loadChildrenMethod({ row }) {
      let param = {
        reguId: this.queryParam.reguId,
        parentId: row.id
      }
      return new Promise((resolve) => {
        getReguDeteil(param).then((res) => {
          if (res.success) {
            resolve(res.result)
          } else {
            this.$message.error(`${res.msg}`)
          }
        })
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },

    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
  }
}
</script>

<style scoped>

</style>