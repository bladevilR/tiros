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
    <a-button na-btn-type="print" type="primary" @click="print">导出打印</a-button>
    <JobCheckSheetTable @brush="$emit('brush')" @judgeChange="onJudgeChange" :fromData="fromData" :qualityEvaluation="qualityEvaluation" ref="jobCheckTable" :data="sheetData"></JobCheckSheetTable>
    <!-- <JobCheckTablePrint ref="tablePrintRef"></JobCheckTablePrint> -->
    <JobCheckTableExport ref="tablePrintRef"></JobCheckTableExport>
    <div style="height: 20px"></div>
  </a-modal>
</template>

<script>
import JobCheckSheetTable from './components/JobCheckSheetTable'
// import JobCheckTablePrint from '@views/tiros/basic/modules/jobCheckSheet/components/JobCheckTablePrint'
import JobCheckTableExport from '@views/tiros/basic/modules/jobCheckSheet/components/JobCheckTableExport'

export default {
  name: 'JobCheckTableView',
  components: { JobCheckSheetTable, JobCheckTableExport },
  props: {
    qualityEvaluation:{
      type: Boolean,
      default: false,
    },
    fromData:{
      type: Object,
      default: ()=>{},
    }
  },
  data() {
    return {
      title: '作业检查表查看',
      visible: false,
      loading: false,
      sheetData: {},
    }
  },
  mounted() {},
  methods: {
    show(row) {
      this.visible = true
      this.sheetData = row
      // this.$refs.jobCheckTable.loadData(this.sheetData)
    },
    // 关闭
    close() {
      this.$emit('close')
      this.visible = false
    },
    print(){
      this.$refs.tablePrintRef.printByRow(this.sheetData)
    },
    onJudgeChange(sheetData){
      this.sheetData = sheetData
    }
  },
}
</script>

<style scoped lang="less">

</style>