<template>
  <a-modal
    :title='title'
    centered
    :visible='visible'
    width='100%'
    :dialogClass="operator === 6 ? 'fullDialog' : 'fullDialog no-footer'"
    @cancel='visible = false'
    :destroyOnClose='true'
  >
    <!--    <a-button na-btn-type="print" type="primary" @click="print">打印</a-button>-->
    <a-button na-btn-type='print' type='primary' @click='exportPrint'>导出打印</a-button>
    <div class='table-page-body' style='height: 100%; overflow-y: auto' v-if='formType === 3 && workRecordType === 1'>
      <RecordSheetTable ref='recordTable' v-show='curRecordData'></RecordSheetTable>
    </div>
    <div class='table-page-body' style='height: 100%; overflow-y: auto' v-if='formType === 4'>
      <JobCheckSheetTable ref='jobCheckTable' :data='sheetData'></JobCheckSheetTable>
    </div>
    <div class='table-page-body' style='height: 100%' v-if='formType === 1 || (formType === 3 && workRecordType === 2)'>
      <div id='luckysheet'></div>
    </div>
    <template slot='footer' v-if='operator === 6'>
      <!-- v-has="'quailty:check9'"  -->
      <a-button type='primary' @click='confirmForm'> 检查确认</a-button>
      <a-button @click='visible = false'> 关闭</a-button>
    </template>
    <ConfirmForm ref='confirmForm' @ok='handleOk'></ConfirmForm>
    <WorkRecordPrint :luckySheetId="'luckysheet'" ref='workRecordPrint'
                     @sheetPrintOk='onSheetPrintOk'></WorkRecordPrint>
    <JobCheckTableExport ref='tablePrintRef'></JobCheckTableExport>
    <TableExport ref='tablePrintRef2' />
  </a-modal>
</template>

<script>

import { getWorkcheck } from '@/api/tirosQualityApi'
import {
  getTrainPlanCheckRecordForm,
  getTrainPlanOnlineFormById,
  getTrainPlanRecordFormById,
  getWorkRecordExcelData,
  saveWorkRecordExcelData
} from '@/api/tirosGroupApi'
import RecordSheetTable from '@views/tiros/common/recordtable/RecordSheetTable'
import JobCheckSheetTable from '@views/tiros/basic/modules/jobCheckSheet/components/JobCheckSheetTable'
import JobCheckTableExport from '@views/tiros/basic/modules/jobCheckSheet/components/JobCheckTableExport'
import TableExport from '@views/tiros/common/recordtable/TableExport.vue'
import { getFormContent } from '@api/tirosApi'
import luckyexcelUtil from '@views/tiros/util/LuckyexcelUtil'
import ConfirmForm from './ConfirmForm'
import WorkRecordPrint from '@views/tiros/quality/workrecord/WorkRecordPrint'
import { getOldWorkRecord } from '@/api/tirosApi'
import { exportExcel } from '@views/tiros/util/export'

export default {
  name: 'ViewWorkForm',
  components: { RecordSheetTable, ConfirmForm, JobCheckSheetTable, WorkRecordPrint, JobCheckTableExport, TableExport },
  props: {
    operator: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      sheetData: {},
      formType: 1,
      workRecordType: 0,
      formName: '',
      trainNo: '',
      groupName: '',
      visible: false,
      fileUrl: '',
      tableData: [],
      curRecordData: [],
      // 当前在线表单
      curOnlineForm: null,
      formSheetId: '',
      formInfo: {},
      formInstId: '',
      onLineInstId: '',
      defaultOptions: {
        container: 'luckysheet', //luckysheet为容器id
        title: 'sheet',
        column: 26, // 列数
        row: 50, // 行数
        lang: 'zh', // 设定表格语言
        allowEdit: false, // 允许编辑
        showinfobar: false, // 名称栏
        sheetFormulaBar: false,
        showsheetbar: false, // 底部sheet页
        showstatisticBar: true, // 底部计数栏
        enableAddRow: false, // 允许添加行
        enableAddCol: false, // 允许添加列
        showtoolbar: false // 是否第二列显示工具栏
      },
      printType: 0, // 0:打印有数据的 1:打印没数据的
      allItems: true
    }
  },
  computed: {
    title() {
      return '表单查看-' + this.trainNo + '车 ' + this.formName
    }
  },
  beforeDestroy() {
    // 离开销毁Luckysheet
    luckysheet.destroy()
  },
  methods: {
    exportPrint() {
      try {
        let train = ''
        if (this.trainNo) {
          train = '-' + this.trainNo + '车'
        }
        switch (this.formType) {
          case 1:
            exportExcel(luckysheet.getAllSheets(), this.formName + train)
            break
          case 3:
            if (this.workRecordType === 1) {
              // console.log(this.curRecordData)
              this.$refs.tablePrintRef2.printByData(this.curRecordData)
              // this.loadRecordTableDetail(this.formInfo.id, this.formInfo.formInstId, allItems)
              // alert('功能暂未实现')
            } else if (this.workRecordType === 2) {
              exportExcel(luckysheet.getAllSheets(), this.formName + train)
            }
            break
          case 4:
            //this.loadWorkCheckFormDetail(this.formInfo.id, this.formInfo.formInstId, allItems)
            this.$refs.tablePrintRef.printByRow(this.sheetData)
          // alert('功能暂未实现')
        }
      } catch (error) {
        console.error(error)
      }
    },
    initSheet(sheetData) {
      sheetData.scrollLeft = 0
      sheetData.scrollTop = 0
      const op = JSON.parse(JSON.stringify(this.defaultOptions))
      op.data = [sheetData]
      luckysheet.destroy()
      luckysheet.create(op)
      // setTimeout(() => {
      //   excelUtil.setRangeConditionalOncell(this.formSheetId)
      // }, 200)
    },
    showModal(formInfo, trainNo) {
      try {
        this.formInfo = formInfo
        this.formType = formInfo.instType
        this.workRecordType = formInfo.workRecordType // 1, // 测试 
        this.trainNo = trainNo
        this.formName = formInfo.formName
        this.formInstId = formInfo.formInstId
        this.allItems = false
        this.loadFormDetail(false)
      } catch (error) {
        console.error(error)
      }
    },
    showModalAllItem(formInfo, trainNo) {
      try {
        this.formInfo = formInfo
        this.formType = formInfo.instType
        this.trainNo = trainNo
        this.formName = formInfo.formName
        this.formInstId = formInfo.formInstId
        this.allItems = true
        this.loadFormDetail(true)
      } catch (error) {
        console.error(error)
      }
    },
    loadFormDetail(allItems) {
      this.printType = 0
      try {
        console.log(this.workRecordType, JSON.stringify(this.formInfo))
        switch (this.formType) {
          case 1:
            this.formInfo.formObjId = this.formInfo.id
            this.loadOnlineFormDetail(this.formInfo.task2InstId, this.formInfo.formInstId)
            break
          case 3:
            if (this.workRecordType === 1) {
              this.loadRecordTableDetail(this.formInfo.task2InstId, this.formInfo.formInstId, allItems)
            } else if (this.workRecordType === 2) {
              //this.formInfo.formObjId = this.formInfo.id
              //this.loadOnlineFormDetail(this.formInfo.id, this.formInfo.formInstId)
              this.loadWorkRecordDetail(this.formInfo.formObjId, this.formInfo.formInstId)
            }
            break
          case 4:
            this.loadWorkCheckFormDetail(this.formInfo.task2InstId, this.formInfo.formInstId, allItems)
        }
      } catch (error) {
        console.error(error)
      }
    },
    // 获取新版的作业记录表的数据
    loadWorkRecordDetail(formObjId, formInstId, showData = true) {
      this.visible = true
      // 1. 判断是否有excel数据
      // 2. 如果咩有，则从模板中加载，包括datajson
      // 获取模板
      // console.log(formObjId, formInstId)
      getFormContent(formObjId).then((res) => {
        if (res.success) {
          let sheet = JSON.parse(res.result)
          if (!showData) {
            this.initSheet(sheet)
            return
          }
          // 获取数据
          getWorkRecordExcelData(formInstId).then(res => {
            if (res.success) {
              if (res.result) {
                let data = JSON.parse(res.result)
                sheet.data = []
                sheet.celldata = luckysheet.transToCellData(data)
              }
              this.initSheet(sheet)
            } else {
              this.initSheet(sheet)
            }
          })
        } else {
          this.$message.error('获取作业记录表模板数据失败')
          console.error('获取作业记录表模板数据失败', res.message)
        }
      })
    },
    // 检查表
    loadWorkCheckFormDetail(task2InstId, formInstanceId, allItem = false) {
      getTrainPlanCheckRecordForm({
        task2InstId: task2InstId,
        formInstId: formInstanceId,
        allItems: allItem
      }).then((res) => {
        if (res.success && res.result) {
          let formData = res.result
          this.sheetData = formData
          this.visible = true
        } else {
          this.$message.error('加载记录数据异常')
          console.error('加载记录数据失败', res.message)
        }
      })
    },
    //  获取在线表单明细（数据表）
    loadOnlineFormDetail(task2InstId, formInstanceId) {
      this.visible = true
      getTrainPlanOnlineFormById({
        task2InstId: task2InstId,
        formInstId: formInstanceId,
        needValues: false
      }).then((res) => {
        // try {
        //   if (res.success && res.result) {
        //     let curOnlineForm = res.result
        //     this.formSheetId = curOnlineForm.formObjId
        //     this.formInstId = curOnlineForm.id
        //     let data = JSON.parse(curOnlineForm.result)
        //     this.$nextTick(() => {
        //       getFormContent(curOnlineForm.formObjId).then((res2) => {
        //         if (res2.success) {
        //           let sheet = JSON.parse(res2.result)
        //           if (data) {
        //             sheet.celldata = luckysheet.transToCellData(data)
        //           }
        //           this.initSheet(sheet)
        //           setTimeout(() => {
        //             luckyexcelUtil.setRangeConditionalOncell(this.formSheetId, this.formInstId)
        //           }, 200)
        //         }
        //       })
        //     })
        //   } else {
        //     this.$message.error('加载记录数据异常')
        //     console.error('加载记录数据失败', res.message)
        //   }
        // } catch (error) {
        //   this.$message.error('加载记录数据异常')
        //   console.log(error)
        // }
        if (res.success) {
          // 改成记录值里面只记录数据，excel的其他信息不记录
          this.curOnlineForm = res.result
          let data = null
          if (this.curOnlineForm) {
            if (this.curOnlineForm.result) {
              // result 不为null 表示填写过
              try {
                data = JSON.parse(this.curOnlineForm.result)
              } catch (e) {
                this.$message.error('转换表单填写结果异常')
                console.error('转换表单填写结果异常', this.curOnlineForm.result)
                return
              }
            }

            // 获取模板
            getFormContent(this.curOnlineForm.formObjId).then((res2) => {
              if (res2.success) {
                let sheet = JSON.parse(res2.result)
                if (data) {
                  sheet.data = []
                  sheet.celldata = luckysheet.transToCellData(data)
                }
                this.initSheet(sheet)
              } else {
                this.$message.error('获取表单模板数据失败')
                console.error('获取表单模板数据失败', res2.message)
              }
            })
          } else {
            this.$message.error('获取作业表单数据失败')
            console.error('获取作业表单数据失败：', res.message)
          }
        } else {
          // 没有获取到记录表的结果， 获取原始表单定义
          this.$message.error('获取记录表内容错误')
          console.error('获取记录表内容错误', res.message)
        }
      })
    },
    // loadTaskFormDetail(planId, formId) {
    // 作业记录表
    loadRecordTableDetail(task2InstId, formInstanceId, allItem = false) {
      getTrainPlanRecordFormById({
        task2InstId: task2InstId,
        formInstId: formInstanceId,
        needChecks: true,
        needCategory: true,
        allItems: allItem
      }).then((res) => {
        if (res.success && res.result) {
          this.curRecordData = res.result
          if (this.curRecordData.categoryList) {
            this.curRecordData.categoryList.sort((a, b) => a.recIndex - b.recIndex)
          }
          this.$nextTick(() => {
            // console.log(this.curRecordData)
            this.$refs.recordTable.loadData(this.curRecordData)
          })
          this.visible = true
        } else {
          this.$message.error('加载记录数据异常')
          console.error('加载记录数据失败', res.message)
        }
      })
    },
    confirmForm() {
      // 确认
      this.$refs.confirmForm.confirm(this.formInstId)
    },
    handleOk(checkInfo) {
      // 如果当前表单是新版的，则要将结果保存到excel表中
      if (this.formType === 3 && this.workRecordType === 2) {
        // 获取检查数据所在单元格
        let cell = luckyexcelUtil.searchCellByText('检查结果', luckysheet.getAllSheets()[0].data)
        let jgIndex = cell.c
        if (cell) {
          let str = luckyexcelUtil.getCellText(cell.cell)
          let jg = checkInfo.checkResult === '1' ? '通过' : '不通过'

          str = str.replace('检查结果：', '检查结果：' + jg)
          luckyexcelUtil.setCellValue(cell.r, cell.c, str)
        }

        cell = luckyexcelUtil.searchCellByText('检查日期', luckysheet.getAllSheets()[0].data)
        let rqIndex = cell.c
        if (cell) {
          let str = luckyexcelUtil.getCellText(cell.cell)
          str = str.replace('检查日期：', '检查日期：' + checkInfo.checkDate)
          luckyexcelUtil.setCellValue(cell.r, cell.c, str)
        }

        cell = luckyexcelUtil.searchCellByText('质量负责人', luckysheet.getAllSheets()[0].data)
        let manIndex = cell.c
        if (cell) {
          let str = luckyexcelUtil.getCellText(cell.cell)
          str = str.replace('质量负责人：', '质量负责人：' + checkInfo.checkUserName)
          luckyexcelUtil.setCellValue(cell.r, cell.c, str)
        }

        if (jgIndex === rqIndex && rqIndex === manIndex) {
          let jg = checkInfo.checkResult === '1' ? '通过' : '不通过'
          let str = '检查结果：' + jg
          str += '          检查日期：' + checkInfo.checkDate
          str += '          质量负责人：' + checkInfo.checkUserName
          luckyexcelUtil.setCellValue(cell.r, cell.c, str)
        }

        this.saveWorkRecord(false)
      }
      this.$emit('checkSuccess', this.formInfo, checkInfo)

      this.visible = false
    },
    // 保存作业记录表数据（excel)
    saveWorkRecord(tips = false) {
      // 保存exceljs
      let dataJson = JSON.stringify(luckysheet.getAllSheets()[0].data)
      const saveObj = { formInstId: this.formInfo.formInstId, xlsJson: dataJson }
      saveWorkRecordExcelData(saveObj).then(res => {
        if (!res.success) {
          if (tips) {
            this.$message.error('保存作业记录表信息失败')
          }
          console.error('保存作业记录表excel信息失败', res)
        } else {
          if (tips) {
            this.$message.success('保存作业记录表信息成功')
          }
          console.log('保存作业记录表excel信息成功')
        }
      })
    },
    // 打印
    print() {
      let option = {
        id: this.formInfo.id,
        instType: this.formType,
        formInstId: this.formInfo.formInstId,
        formObjId: this.formInfo.formObjId,
        formName: this.formName,
        workRecordType: this.workRecordType
      }
      if (this.printType === 0) {
        this.$refs.workRecordPrint.print(option)
      } else if (this.printType === 1) {
        this.$refs.workRecordPrint.printNoData(option)
      }

    },
    onSheetPrintOk() {
      if (this.printType === 0) {
        this.loadFormDetail(this.allItems)
      } else if (this.printType === 1) {
        this.loadFormDetailNoData()
      }
    },
    // 查看明细列表（没有填写数据）
    showModalNoData(formInfo, trainNo) {
      this.formInfo = formInfo
      this.formType = formInfo.instType
      this.trainNo = trainNo
      this.formName = formInfo.formName
      this.formInstId = formInfo.formInstId
      this.loadFormDetailNoData()
    },
    loadFormDetailNoData() {
      this.printType = 1
      try {
        switch (this.formType) {
          case 1:
            this.formInfo.formObjId = this.formInfo.id
            this.loadOnlineDetailNoData(this.formInfo.formInstId)
            break
          case 3:
            // console.log(this.formInfo)
            if (this.workRecordType === 1) {
              this.loadRecordDetailNoData(this.formInfo.formInstId)
            } else if (this.workRecordType === 2) {
              this.loadWorkRecordDetail(this.formInfo.formObjId || this.formInfo.id, this.formInfo.formInstId, false)
            }
            break
          case 4:
            this.loadCheckDetailNoData(this.formInfo.formInstId)
        }
      } catch (error) {
        console.error(error)
      }
    },
    //作业记录表明细查询(无数据)
    loadRecordDetailNoData(formInstId) {
      getOldWorkRecord({
        id: formInstId
      }).then(res => {
        if (res.success && res.result) {
          this.curRecordData = res.result
          if (this.curRecordData.categoryList) {
            this.curRecordData.categoryList.sort((a, b) => a.recIndex - b.recIndex)
          }
          this.$nextTick(() => {
            this.$refs.recordTable.loadData(this.curRecordData)
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
        id: formInstId
      }).then((res) => {
        try {
          if (res.success && res.result) {
            let formData = res.result
            this.sheetData = formData
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
    //数据记录表明细查询(无数据)
    loadOnlineDetailNoData(formInstId) {
      this.visible = true
      getFormContent(formInstId).then((res) => {
        if (res.success) {
          let sheet = JSON.parse(res.result)
          this.$nextTick(() => {
            this.initSheet(sheet)
          })
        }
      })
    }
  }
}
</script>

<style>
.fullDialog.no-title.ViewWorkForm .ant-modal-body {
  height: calc(100% - 100px) !important;
}
</style>
<style lang='less' scoped>
#luckysheet {
  height: calc(100% - 0px);
  width: 100%;
}
</style>