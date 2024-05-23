<template>
  <a-modal
    :title="title"
    centered
    :width="'100%'"
    dialogClass="fullDialog no-footer"
    :visible="visible"
    :confirmLoading="loading"
    :footer="null"
    @ok="close"
    @cancel="close"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-button na-btn-type="print" type="primary" @click="print">导出</a-button>
    <RecordSheetTable ref="recordTable" v-show="recordSheet"></RecordSheetTable>
    <div style="height: 20px;"></div>
    <!-- <TablePrint ref="tablePrintRef"></TablePrint> -->
    <TableExport ref="tablePrintRef" />
  </a-modal>
</template>

<script>
import { getOldWorkRecord } from '@/api/tirosApi'
import RecordSheetTable from '@views/tiros/common/recordtable/RecordSheetTable'
// import TablePrint from '@views/tiros/common/recordtable/TablePrint'
import TableExport from '@views/tiros/common/recordtable/TableExport.vue'

export default {
  name: 'RecordTableView',
  components:{ RecordSheetTable, TableExport},
  props: {
  },
  data() {
    return {
      title: '记录表查看',
      visible: false,
      loading: false,
      recordId: '',
      recordSheet:{}
    }
  },
  mounted () {
  },
  methods: {
    show (id) {
      this.recordId=id
      this.loadData();
      this.visible = true
    },
    // 关闭
    close() {
      this.$emit('close')
      this.visible = false
    },
    loadData () {
      getOldWorkRecord({ id: this.recordId}).then(res=>{
        if(res.success) {
          this.recordSheet = res.result
          console.log(this.recordSheet)
          this.$refs.recordTable.loadData(this.recordSheet)
        } else {
          this.$message.error('获取记录表详情失败')
          console.error('获取记录表详情失败', res.message)
        }
      }).catch(err=>{
        this.$message.error('获取记录表详情异常')
        console.error('获取记录表详情异常：', res.message)
      })
    },
    print(){
      this.$refs.tablePrintRef.printById(this.recordId)
    }
  }
}
</script>

<style scoped lang="less">

</style>