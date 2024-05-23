<template>
  <div>
    <!-- <LuckySheetPrint ref="luckySHeetPrint"></LuckySheetPrint> -->
    <TablePrint ref="tablePrint"></TablePrint>
    <LuckySheetPrint
      ref="luckySheetPrint"
      :luckySheetId="luckySheetId"
      @sheetPrintOk="$emit('sheetPrintOk')"
    ></LuckySheetPrint>
    <!-- <JobCheckTablePrint ref="jobCheckPrint"></JobCheckTablePrint> -->
    <JobCheckTableExport ref="jobCheckPrint"></JobCheckTableExport>
  </div>
</template>


<script>
import TablePrint from '@views/tiros/common/recordtable/TablePrint'
import LuckySheetPrint from '@views/tiros/common/recordtable/LuckySheetPrint'
// import JobCheckTablePrint from '@views/tiros/basic/modules/jobCheckSheet/components/JobCheckTablePrint'
import JobCheckTableExport from '@views/tiros/basic/modules/jobCheckSheet/components/JobCheckTableExport'
import { getRecordTableDetail, getWorkCheckForm, getWorkcheck } from '@/api/tirosQualityApi'
import { getTrainPlanOnlineFormById, getTrainPlanRecordFormById, getWorkRecordExcelData } from '@/api/tirosGroupApi'
import { getFormContent, getOldWorkRecord } from '@api/tirosApi'

export default {
  name: 'WorkRecordPrint',
  components: { TablePrint, JobCheckTableExport, LuckySheetPrint },
  props: {
    luckySheetId: {
      type: String,
      default: 'lsPrint',
    },
  },
  data() {
    return {}
  },

  created() {},
  methods: {
    // 打印有数据的
    print(record) {
      switch (record.instType) {
        case 1:
          this.loadOnlineFormDetail(record.id, record.formInstId)
          break
        case 3:
          if(record.workRecordType===1) {
            this.loadRecordTableDetail(record.id, record.formInstId)
          } else if (record.workRecordType === 2) {
            this.loadWorkRecordForm(record,true)
          }
          break
        case 4:
          console.log(record)
          this.loadWorkFormDetail(record.task2InstId, record.formInstId)
      }
    },
    loadWorkRecordForm (record,showData=false) {
      // 获取模板
      getFormContent(record.formObjId).then((res) => {
        if (res.success) {
          let sheet = JSON.parse(res.result)
          if (!showData) {
            //this.initSheet(sheet)
            this.$refs.luckySheetPrint.printBySheet(sheet)
            return
          }
          // 获取数据
          getWorkRecordExcelData(record.formInstId).then(res => {
            if (res.success) {
              if (res.result) {
                let data = JSON.parse(res.result)
                sheet.data = []
                sheet.celldata = luckysheet.transToCellData(data)
              }
            } else {
              console.error('获取作业记录表数据失败：', res)
            }
            this.$refs.luckySheetPrint.printBySheet(sheet)
          })
        } else {
          this.$message.error('获取作业记录表模板数据失败')
          console.error('获取作业记录表模板数据失败', res.message)
        }
      })
    },
    loadRecordTableDetail(task2InstId, formInstanceId) {
      getTrainPlanRecordFormById({
        task2InstId: task2InstId,
        formInstId: formInstanceId,
        needChecks: true,
        needCategory: true,
        allItems: true,
      }).then((res) => {
        if (res.success && res.result) {
          this.$nextTick(() => {
            if (res.result.categoryList) {
              res.result.categoryList.sort((a, b) => a.recIndex - b.recIndex)
            }
            this.$refs.tablePrint.printByData(res.result)
          })
          this.visible = true
        } else {
          this.$message.error('加载记录数据异常')
          console.error('加载记录数据失败', res.message)
        }
      })
    },
    loadWorkFormDetail(task2InstId, formInstanceId) {
      console.log(2323232)
      getWorkCheckForm({
        task2InstId: task2InstId,
        formInstId: formInstanceId,
      }).then((res) => {
        if (res.success && res.result) {
          this.$refs.jobCheckPrint.printByRow(res.result)
        } else {
          this.$message.error('加载记录数据异常')
          console.error('加载记录数据失败', res.message)
        }
      })
    },
    //  获取在线表单明细
    loadOnlineFormDetail(task2InstId, formInstanceId) {
      getTrainPlanOnlineFormById({
        task2InstId: task2InstId,
        formInstId: formInstanceId,
        needValues: false,
      }).then((res) => {
        try {
          if (res.success && res.result) {
            let curOnlineForm = res.result
            // this.formSheetId = curOnlineForm.formObjId
            // this.formInstId = curOnlineForm.id
            let data = JSON.parse(curOnlineForm.result)
            this.$nextTick(() => {
              getFormContent(curOnlineForm.formObjId).then((res2) => {
                if (res2.success) {
                  let sheet = JSON.parse(res2.result)
                  if (data) {
                    sheet.celldata = luckysheet.transToCellData(data)
                  }
                  this.$refs.luckySheetPrint.printBySheet(sheet)
                  // this.initSheet(sheet)
                  // setTimeout(() => {
                  //   excelUtil.setRangeConditionalOncell(this.formSheetId, this.formInstId)
                  // }, 200)
                }
              })
            })
          } else {
            this.$message.error('加载记录数据异常')
            console.error('加载记录数据失败', res.message)
          }
        } catch (error) {
          this.$message.error('加载记录数据异常')
          console.log(error)
        }
      })
    },
    // 打印无数据的
    printNoData(record) {
      try {
        console.log('record:', record)

        switch (record.instType) {
          case 1:
            this.loadOnlineDetailNoData(record.formInstId)
            break
          case 3:
            if(record.workRecordType===1) {
              this.loadRecordDetailNoData(record.formInstId)
            } else if (record.workRecordType === 2) {
              this.loadWorkRecordForm(record,false)
            }
            break
          case 4:
            this.loadCheckDetailNoData(record.formInstId)
        }
      } catch (error) {
        console.error(error)
      }
    },
    //作业记录表明细查询(无数据)
    loadRecordDetailNoData(formInstId) {
      getOldWorkRecord({
        id: formInstId,
      }).then((res) => {
        if (res.success && res.result) {
          this.$nextTick(() => {
            if (res.result.categoryList) {
              res.result.categoryList.sort((a, b) => a.recIndex - b.recIndex)
            }
            this.$refs.tablePrint.printByData(res.result)
          })
          this.visible = true
        } else {
          this.$message.error('加载记录数据异常')
          console.error('加载记录数据失败', res.message)
        }
      })
    },
    //检查记录表明细查询(无数据)
    loadCheckDetailNoData(formInstId) {
      getWorkcheck({
        id: formInstId,
      }).then((res) => {
        try {
          if (res.success && res.result) {
            // let formData = res.result
            // this.sheetData = formData
            this.$refs.jobCheckPrint.printByRow(res.result)
            this.visible = true
          } else {
            this.$message.error('加载记录数据异常')
            console.error('加载记录数据失败', res.message)
          }
        } catch (error) {
          this.$message.error('加载记录数据异常')
          console.log(error)
        }
      })
    },
    //检查记录表明细查询(无数据)
    loadOnlineDetailNoData(formInstId) {
      this.visible = true
      getFormContent(formInstId).then((res) => {
        if (res.success) {
          let sheet = JSON.parse(res.result)
          this.$refs.luckySheetPrint.printBySheet(sheet)
        }
      })
    },
  },
}
</script>