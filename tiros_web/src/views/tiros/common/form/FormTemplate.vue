<template>
  <a-modal
    :title="'表单查看-' + trainNo + '车 ' + formName"
    centered
    :visible="visible"
    width="100%"
    :footer="null"
    dialogClass="fullDialog no-title no-footer"
    @cancel="visible = false"
    :destroyOnClose="true"
  >
    <!--    <div style="height: 120px">
      <vxe-table
        border
        max-height="100%"
        ref="listTable"
        align="center"
        :data="tableData"
        show-overflow="tooltip"
      >
        <vxe-table-column field="name" title="表单名称" align="left" header-align="center"
                          width="25%"></vxe-table-column>
        <vxe-table-column field="code" title="表单编码" align="center" header-align="center"
                          width="80"></vxe-table-column>
        <vxe-table-column field="categoryName" title="所属分类" align="center" header-align="center"
                          width="80"></vxe-table-column>
        <vxe-table-column field="type_dictText" title="表单类型" align="center" header-align="center"
                          width="80"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" align="center" header-align="center"
                          width="80"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" align="left" header-align="center"></vxe-table-column>
      </vxe-table>
    </div>-->
    <div class="table-page-body" style="height: calc(100% - 50px)">
      <div id="luckysheet"></div>
    </div>
  </a-modal>
</template>

<script>
import { getFormContent } from '@api/tirosApi'
import { getTrainPlanOnlineFormById } from '@api/tirosGroupApi'
import luckyexcelUtil from '@views/tiros/util/LuckyexcelUtil'

export default {
  name: 'FormTemplate',
  data() {
    return {
      formName: '',
      trainNo: '',
      groupName: '',
      visible: false,
      fileUrl: '',
      tableData: [],
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
        showtoolbar: false, // 是否第二列显示工具栏
      },
    }
  },
  beforeDestroy() {
    // 离开销毁Luckysheet
    luckysheet.destroy()
  },
  methods: {
    initSheet(sheetData) {
      const op = JSON.parse(JSON.stringify(this.defaultOptions))
      op.data = [sheetData]
      luckysheet.destroy()
      luckysheet.create(op)
    },
    // row.formObjId , row.formObjName, row.formDataRecordId, row.groupName
    showModal(row) {
      this.formName = row.formObjName
      this.groupName = row.groupName
      this.loadOnlineFormDetail(row.id, row.formInstId)
      this.visible = true
    },
    showModal2 (formName, groupName,formInstanceId,formObjId) {
      this.formName = formName
      this.groupName =groupName
      this.loadOnlineFormDetail('task2InstId', formInstanceId)
      this.visible = true
    },
    //  获取在线表单明细
    loadOnlineFormDetail(task2InstId, formInstanceId) {
      const params = {
        task2InstId: task2InstId,
        formInstId: formInstanceId,
        needValues: false,
      }

      getTrainPlanOnlineFormById(params)
        .then((re) => {
          if (re.success) {
            // 改成记录值里面只记录数据，excel的其他信息不记录
            let curOnlineForm = re.result
            this.trainNo = curOnlineForm.trainNo
            if (curOnlineForm && curOnlineForm.result) {
              try {
                let data = JSON.parse(curOnlineForm.result)
                getFormContent(curOnlineForm.formObjId).then((res) => {
                  if (res.success) {
                    let sheet = JSON.parse(res.result)
                    if (data) {
                      sheet.data = []
                      sheet.celldata = luckysheet.transToCellData(data)
                    }
                    this.initSheet(sheet)
                    setTimeout(() => {
                      luckyexcelUtil.setRangeConditionalOncell(curOnlineForm.formObjId, curOnlineForm.id)
                    }, 200)
                  } else {
                    this.$message.error('获取表单模板数据失败')
                    console.error('获取表单模板数据失败', res.message)
                  }
                })
              } catch (e) {
                this.$message.error('转换表单填写结果异常')
                console.error('转换表单填写结果异常', curOnlineForm.result)
                return
              }
            } else {
              this.$message.error('获取作业表单数据失败')
              console.error('获取作业表单数据失败：', re.message)
            }
          } else {
            // 没有获取到记录表的结果， 获取原始表单定义
            /* getFormContent(this.curForm.objId).then(res => {
             if (res.success) {
               this.initSheet(JSON.parse( res.result))
             }
           })*/
            this.$message.error('获取记录表内容错误')
            console.error('获取记录表内容错误', res.message)
          }
        })
        .catch((err) => {
          this.$message.error('获取记录表内容异常')
          console.error('获取记录表内容异常', err)
        })
    },
  },
}
</script>

<style lang="less" scoped>
#luckysheet {
  height: calc(100% - 0px);
  width: 100%;
}
</style>