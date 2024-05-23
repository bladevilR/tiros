<template>
  <div>
    <a-space>
      <a-button @click="openFile" class="tab-button">
        导入
        <input id="Luckyexcel-File" ref="file" type="file" style="display: none" @change="loadFile" />
      </a-button>
      <a-button class="tab-button" v-has="'demo:excel:export'"> 导出 </a-button>
      <a-button @click="insertRow">插入一行</a-button>
      <a-button @click="getRowData">获取行数据</a-button>
      <a-button @click="setSelectValue">设置数据</a-button>
      <a-button @click="getSheetData">获取表格数据</a-button>
      <a-button @click="setCheck('自检')">自检</a-button>
      <a-button @click="setCheck('互检')">互检</a-button>
      <a-button @click="setCheck('专检')">专检</a-button>
      <a-button @click="setQualityCheck('质量检查')">质量检查</a-button>
      <a-button @click="setTools">工器具设置</a-button>
    </a-space>

    <div id="luckysheet" style="height: calc(100vh - 150px); width: 100%"></div>
  </div>
</template>
<!-- 这个页面不能和甘特图页面一起打开，一次只能打开其中一个 -->
<script>
export default {
  name: 'excel',
  data() {
    return {
      defaultOptions: {
        container: 'luckysheet', //luckysheet is the container id
        title: 'sheet',
        column: 26, // 列数
        row: 50, // 行数
        lang: 'zh', // 设定表格语言
        allowEdit: true, // 允许编辑
        showtoolbar: true, // 显示工具栏
        showtoolbarConfig: {
          undoRedo: true, //撤销重做，注意撤消重做是两个按钮，由这一个配置决定显示还是隐藏
          paintFormat: true, //格式刷
          currencyFormat: false, //货币格式
          percentageFormat: false, //百分比格式
          numberDecrease: false, // '减少小数位数'
          numberIncrease: false, // '增加小数位数
          moreFormats: false, // '更多格式'
          font: false, // '字体'
          fontSize: false, // '字号大小'
          bold: false, // '粗体 (Ctrl+B)'
          italic: false, // '斜体 (Ctrl+I)'
          strikethrough: false, // '删除线 (Alt+Shift+5)'
          underline: false, // '下划线 (Alt+Shift+6)'
          textColor: false, // '文本颜色'
          fillColor: false, // '单元格颜色'
          border: true, // '边框'
          mergeCell: true, // '合并单元格'
          horizontalAlignMode: true, // '水平对齐方式'
          verticalAlignMode: true, // '垂直对齐方式'
          textWrapMode: true, // '换行方式'
          textRotateMode: false, // '文本旋转方式'
          image:true, // '插入图片'
          link:false, // '插入链接'
          chart: false, // '图表'（图标隐藏，但是如果配置了chart插件，右击仍然可以新建图表）
          postil:  false, //'批注'
          pivotTable: false,  //'数据透视表'
          function: false, // '公式'
          frozenMode: false, // '冻结方式'
          sortAndFilter: false, // '排序和筛选'
          conditionalFormat: false, // '条件格式'
          dataVerification: true, // '数据验证'
          splitColumn: false, // '分列'
          screenshot: false, // '截图'
          findAndReplace: true, // '查找替换'
          protection:true, // '工作表保护'
          print:false, // '打印'
        },
        showinfobar: false, // 信息栏(标题)
        showsheetbar: false, // 底部sheet页
        showstatisticBar: false, // 底部计数栏
        enableAddRow: false, // 允许添加行
        enableAddCol: false, // 允许添加列
        functionButton:'<button id="" class="btn btn-primary" style="padding:3px 6px;font-size: 12px;margin-right: 10px;">下载</button>',
        cellRightClickConfig: {
          copy: true, // 复制
          copyAs: false, // 复制为
          paste: false, // 粘贴
          insertRow: true, // 插入行
          insertColumn: false, // 插入列
          deleteRow: false, // 删除选中行
          deleteColumn: false, // 删除选中列
          deleteCell: false, // 删除单元格
          hideRow: false, // 隐藏选中行和显示选中行
          hideColumn: false, // 隐藏选中列和显示选中列
          rowHeight: false, // 行高
          columnWidth: false, // 列宽
          clear: false, // 清除内容
          matrix: false, // 矩阵操作选区
          sort: false, // 排序选区
          filter: false, // 筛选选区
          chart: false, // 图表生成
          image: false, // 插入图片
          link: false, // 插入链接
          data: false, // 数据验证
          cellFormat: false // 设置单元格格式
        },
        hook: {
          sheetMouseup: (cell, postion, sheetFile, ctx) => {
            this.getSelectRange()
          },
          cellEditBefore: function (r, c, v) {
            console.log('cellEditBefore：', r, c, v)
          },
          cellUpdateBefore: function (r, c, v) {
            console.log('cellUpdateBefore：', r, c, v)
          },
          cellUpdated (r, c, ov, nv, isrefresh) {
            console.log('cellUpdateBefore：', r, c, ov,nv)
          },
          cellMousedown: function (cell,postion) {
            console.log('cellMousedown：', cell,postion)
          },
        },

      },
      selectRange: null,
      sheetMetaData: null
    }
  },
  mounted() {
    const sheet=window.demoData;
    this.defaultOptions.data=[sheet]
    luckysheet.create(this.defaultOptions)
    this.loadDemoData(sheet)
  },
  beforeDestroy() {
    // 离开销毁Luckysheet
    luckysheet.destroy()
  },
  methods: {
    loadDemoData (sheet) {
      const data= luckysheet.transToData(sheet.celldata)
      const metaData = {
        checkDetail: {
          id:'checkDetail',
          name: '作业记录检查明细',
          type: 'zone',
          rowIndex:-1,
          colIndex: 0,
          rowIndexTo: -1,
          colIndexTo: -1,
          rows: [
          ]
        }
      }
      let dataText = [] // 单元格文本, 这里只是为了显示
      for (let i = 0; i < data.length; i++) {
        const row = data[i]
        let newRow=[];
        dataText.push(newRow)
        for (let j = 0; j < row.length; j++) {
          const cell = row[j]
          if(cell) {
            let cellText = this.getCellText(cell)
            if (!cellText) {
              // 当前是合并单元格
              if(this.checkProperty(cell,"mc")){
                const mc=cell.mc
                if(this.checkProperty(mc,"r") && this.checkProperty(mc,"c") && this.checkProperty(mc,"rs") && this.checkProperty(mc,"cs")){
                  // 主单元格
                } else if(this.checkProperty(mc,"r") && this.checkProperty(mc,"c")){
                  // 辅单元格，判断是
                  let txt = dataText[mc.r][mc.c] // 找主单元格的文本
                  if (txt) {
                    //newRow.push(txt)
                    cellText = txt
                  } else {
                    //newRow.push('')
                    cellText = ''
                  }
                }
              }
            }
            if(cellText){
              newRow.push(cellText)
            } else {
              newRow.push('')
            }
          } else {
            newRow.push('')
          }
        }
        // 查找明细行标题所在行
        if (newRow.includes('序号') && newRow.includes('项目') && newRow.includes('自检')) {
          let detailTitle = {
            id:'detailTitle',name:'明细标题行',type: 'row', rowIndex:i, rowIndexTo: i ,
            cells: {}
          }
          metaData.checkDetail.rows.push(detailTitle)
          metaData.checkDetail.rowIndex = i // 开始行

          // 找出所有标题
          for (let j = 0; j < row.length; j++) {
            const cell=row[j]
            if (cell) {
              metaData.checkDetail.colIndexTo = j
              let cellText = this.getCellText(cell)
              if (cellText) {
                let location = this.getCellLocation(i, j, cell)
                switch (cellText) {
                case '序号':
                  detailTitle.cells.xh = {
                    id: 'xh',
                    name: '序号',
                    type: 'cell',
                    cellType: 'title',
                    rowIndex: location.rowIndex,
                    rowIndexTo: location.rowIndexTo,
                    colIndex: location.colIndex,
                    colIndexTo: location.colIndexTo
                  }
                case '项目':
                  detailTitle.cells.xm = {
                    id: 'xm',
                    name: '项目',
                    type: 'cell',
                    cellType: 'title',
                    rowIndex: location.rowIndex,
                    rowIndexTo: location.rowIndexTo,
                    colIndex: location.colIndex,
                    colIndexTo: location.colIndexTo
                  }
                case '作业内容':
                  detailTitle.cells.zynr = {
                    id: 'zynr',
                    name: '作业内容',
                    type: 'cell',
                    cellType: 'title',
                    rowIndex: location.rowIndex,
                    rowIndexTo: location.rowIndexTo,
                    colIndex: location.colIndex,
                    colIndexTo: location.colIndexTo
                  }
                case '控制点':
                  detailTitle.cells.kzd = {
                    id: 'kzd',
                    name: '控制点',
                    type: 'cell',
                    cellType: 'title',
                    rowIndex: location.rowIndex,
                    rowIndexTo: location.rowIndexTo,
                    colIndex: location.colIndex,
                    colIndexTo: location.colIndexTo
                  }
                case '技术要求':
                  detailTitle.cells.jsyq ={
                    id: 'jsyq',
                    name: '技术要求',
                    type: 'cell',
                    cellType: 'title',
                    rowIndex: location.rowIndex,
                    rowIndexTo: location.rowIndexTo,
                    colIndex: location.colIndex,
                    colIndexTo: location.colIndexTo
                  }
                case '作业情况':
                  detailTitle.cells.zyqk = {
                    id: 'zyqk',
                    name: '作业情况',
                    type: 'cell',
                    cellType: 'title',
                    rowIndex: location.rowIndex,
                    rowIndexTo: location.rowIndexTo,
                    colIndex: location.colIndex,
                    colIndexTo: location.colIndexTo
                  }
                case '结果':
                  detailTitle.cells.jg = {
                    id: 'jg',
                    name: '结果',
                    type: 'cell',
                    cellType: 'title',
                    rowIndex: location.rowIndex,
                    rowIndexTo: location.rowIndexTo,
                    colIndex: location.colIndex,
                    colIndexTo: location.colIndexTo
                  }
                case '自检':
                  detailTitle.cells.zijian = {
                    id: 'zijian',
                    name: '自检',
                    type: 'cell',
                    cellType: 'title',
                    rowIndex: location.rowIndex,
                    rowIndexTo: location.rowIndexTo,
                    colIndex: location.colIndex,
                    colIndexTo: location.colIndexTo
                  }
                case '互检':
                  detailTitle.cells.hujian = {
                    id: 'hujian',
                    name: '互检',
                    type: 'cell',
                    cellType: 'title',
                    rowIndex: location.rowIndex,
                    rowIndexTo: location.rowIndexTo,
                    colIndex: location.colIndex,
                    colIndexTo: location.colIndexTo
                  }
                case '专检':
                  detailTitle.cells.zhuanjian = {
                    id: 'zhuanjian',
                    name: '专检',
                    type: 'cell',
                    cellType: 'title',
                    rowIndex: location.rowIndex,
                    rowIndexTo: location.rowIndexTo,
                    colIndex: location.colIndex,
                    colIndexTo: location.colIndexTo
                  }
                }
              }
            }
          }
        }
        // 判断是否检查项目结束行： 主要判断序号列是否有值且大于0
        if (metaData.checkDetail.rowIndex > -1 && metaData.checkDetail.rows.length > 0) {
          let titleMeta=metaData.checkDetail.rows[0].cells // 默认第一行为标题
          const xh = newRow[titleMeta.xh.colIndex]
          if (xh && parseInt(xh) > 0) {
            metaData.checkDetail.rowIndexTo = i
            metaData.checkDetail.rows.push(
              {
                xh: {
                  id: 'xh',
                  name: newRow[titleMeta.xh.colIndex],
                  type: 'cell',
                  cellType: 'title',
                  rowIndex: i,
                  rowIndexTo: i,
                  colIndex: titleMeta.xh.colIndex,
                  colIndexTo: titleMeta.xh.colIndexTo
                },
                xm: {
                  id: 'xm',
                  name: newRow[titleMeta.xm.colIndex],
                  type: 'cell',
                  cellType: 'title',
                  rowIndex: i,
                  rowIndexTo: i,
                  colIndex: titleMeta.xm.colIndex,
                  colIndexTo: titleMeta.xm.colIndexTo
                },
                zynr: {
                  id: 'zynr',
                  name: newRow[titleMeta.zynr.colIndex],
                  type: 'cell',
                  cellType: 'title',
                  rowIndex: i,
                  rowIndexTo: i,
                  colIndex: titleMeta.zynr.colIndex,
                  colIndexTo: titleMeta.zynr.colIndexTo
                },
                kzd: {
                  id: 'kzd',
                  name: newRow[titleMeta.kzd.colIndex],
                  type: 'cell',
                  cellType: 'title',
                  rowIndex: i,
                  rowIndexTo: i,
                  colIndex: titleMeta.kzd.colIndex,
                  colIndexTo: titleMeta.kzd.colIndexTo
                },
                jsyq: {
                  id: 'jsyq',
                  name: newRow[titleMeta.jsyq.colIndex],
                  type: 'cell',
                  cellType: 'title',
                  rowIndex: i,
                  rowIndexTo: i,
                  colIndex: titleMeta.jsyq.colIndex,
                  colIndexTo: titleMeta.jsyq.colIndexTo
                }
              }
            )
          }
        }
      }

      console.log(metaData)
      this.sheetMetaData = metaData
    },
    // 判断对象是否存在指定属性
    checkProperty (obj, property) {
      return property in obj
    },
    // 获取单元格文本
    getCellText (cell) {
      let text=''
      if(this.checkProperty(cell,"m")){
        text=cell.m
      } else if(this.checkProperty(cell,"v")){
        text=cell.m
      } else if(this.checkProperty(cell,"ct") && this.checkProperty(cell.ct,"s")){
        let sArr=cell.ct.s
        sArr.forEach((s,index)=>{
          if(this.checkProperty(s,"m")){
            text+=s.m
          } else if(this.checkProperty(s,"v")){
            text+=s.v
          }
        });
      }
      return text
    },
    // 获取当前选择的单元格
    getSelectRange() {
      const range = luckysheet.getRange()[0]

      const select={
        r_start : range.row[0],
        r_end: range.row[1],
        c_start:range.column[0],
        c_end:range.column[1]
      }

      this.selectRange = select
    },

    // 根据文本搜索所在单元格, 找到第一个就返回
    searchCellByText (txt,data, r=-1) {
      if (!data) {
        //return null;
        data= luckysheet.getAllSheets()[0].data
      }
      if(r===-1) {
        for (let i = 0; i < data.length; i++) {
          for (let j = 0; j < data[i].length; j++) {
            const cell = data[i][j]
            if (cell) {
              let cellText = this.getCellText(cell)
              if (cellText && cellText.indexOf(txt) > -1) {
                return { r: i, c: j, mc: cell.mc }
              }
            }
          }
        }
      } else {
        for (let j = 0; j < data[r].length; j++) {
          const cell = data[r][j]
          if (cell) {
            let cellText = this.getCellText(cell)
            if (cellText && cellText.indexOf(txt) > -1) {
              return { r: r, c: j, mc: cell.mc }
            }
          }
        }
      }
    },

    getCheckKeyByName (txtName) {
      switch (txtName) {
      case '自检':
        return 'zijian'
      case '互检':
        return 'hujian'
      case '专检':
        return 'zhuanjian'
      default:
        return ''
      }
    },
    // 设置检查人员
    setCheck (type) {
      if (this.sheetMetaData && this.selectRange) {
        let checkKey = this.getCheckKeyByName(type)
        let titleMeta = this.sheetMetaData.checkDetail.rows[0].cells
        let checkIndex = titleMeta[checkKey].colIndex
        let jgIndex = titleMeta.jg.colIndex
        let qkIndex=  titleMeta.zyqk.colIndex // this.dataLocations.detailTitleRow.indexOf('作业情况')
        // 检查是否在明细区间内
        if (this.sheetMetaData.checkDetail.rowIndex < this.selectRange.r_start && this.selectRange.r_end <= this.sheetMetaData.checkDetail.rowIndexTo) {
          for (let i = this.selectRange.r_start; i <= this.selectRange.r_end; i++) {
            if (checkIndex) {
              this.setCellValue(i, checkIndex, '我检查')
            }
            if (jgIndex) {
              this.setCellValue(i, jgIndex, '√')
            }
            if (qkIndex) {
              this.setCellValue(i, qkIndex, '正常')
            }
          }
        } else {
          this.$message.error('选择区域不能填写检查信息')
        }

      }
    },
    // 根据当前单元格，寻找他右边可填写的第一个格子，如果当前单元格式合并单元格则跳过合并数量的单元格数量
    findWriteCellIndex (cell) {
      if(!this.checkProperty(cell,'mc')) {
        return cell.c + 1 // 没有合并单元格，返回当前单元格的右边一个
      } else {
        const mc = cell.mc
        if(this.checkProperty(mc,"r") && this.checkProperty(mc,"c") && this.checkProperty(mc,"rs") && this.checkProperty(mc,"cs")){
          // 主单元格
          return cell.mc.c+cell.mc.cs  // 返回合并单元格右边第一个单元格
        } else if(this.checkProperty(mc,"r") && this.checkProperty(mc,"c")) {
          // 辅单元格
          // 辅单元格什么都不做
          return -1
        }
      }
    },
    getCellLocation (r,c,cell) {
      let location = {
        rowIndex: r,
        rowIndexTo: r,
        colIndex: c,
        colIndexTo: c
      }
      if(this.checkProperty(cell,'mc')) {
        const mc = cell.mc
        if(this.checkProperty(mc,"r") && this.checkProperty(mc,"c") && this.checkProperty(mc,"rs") && this.checkProperty(mc,"cs")){
          // 主单元格
          location.rowIndex = cell.mc.r
          location.colIndex = cell.mc.c
          location.rowIndexTo = cell.mc.r + cell.mc.rs - 1
          location.colIndexTo = cell.mc.c + cell.mc.cs - 1
        }
      }
      return location
    },
    // 质量检查
    setQualityCheck () {
      let cell1 = this.searchCellByText('检查结果')
      if (cell1 && cell1.r!==this.sheetMetaData.checkDetail.rowIndex) {
         // 找到了，且不是明细标题中的
        const colIndex = this.findWriteCellIndex(cell1)
        this.setCellValue(cell1.r,colIndex, "结果不错")
      }
      cell1 = this.searchCellByText('检查日期')
      if (cell1 && cell1.r!==this.sheetMetaData.checkDetail.rowIndex) {
        // 找到了，且不是明细标题中的
        const colIndex = this.findWriteCellIndex(cell1)
        this.setCellValue(cell1.r,colIndex, "2021-09-14")
      }
      cell1 = this.searchCellByText('质量负责人')
      if (cell1 && cell1.r!==this.sheetMetaData.checkDetail.rowIndex) {
        // 找到了，且不是明细标题中的
        const colIndex = this.findWriteCellIndex(cell1)
        this.setCellValue(cell1.r,colIndex, "质检员啊")
      }

      // 作业日期
      cell1 = this.searchCellByText('作业日期')
      if (cell1 && cell1.r!==this.sheetMetaData.checkDetail.rowIndex) {
        // 找到了，且不是明细标题中的
        this.setCellValue(cell1.r,cell1.c, "作业日期：2021-09-14  完工日期: 2021-09-14")
      }
      // 作业班组
      cell1 = this.searchCellByText('作业班组')
      if (cell1 && cell1.r!==this.sheetMetaData.checkDetail.rowIndex) {
        // 找到了，且不是明细标题中的
        this.setCellValue(cell1.r,cell1.c, "作业班组：调试工班")
      }
      // 车号
      cell1 = this.searchCellByText('车号')
      if (cell1 && cell1.r!==this.sheetMetaData.checkDetail.rowIndex) {
        // 找到了，且不是明细标题中的
        this.setCellValue(cell1.r,cell1.c, "车号：0105")
      }
    },
    // 填充计量器具
    setTools () {
      // 1. 先找到“计量器具”列，判断是否合并单元格，如果是，则找到开始行和结束行
      // 2. 寻找指定行的指定文本列
      const cell = this.searchCellByText('计量器具')
      if (cell) {
        const startRowindex=cell.mc.r
        const endRowindex = cell.mc.r + cell.mc.rs
        for(let i=startRowindex;i<=endRowindex;i++){
          let writeCell = this.searchCellByText('名称', i)
          if (writeCell) {
            this.setCellValue(writeCell.r, writeCell.c, '名称：可爱哇')
          }
          writeCell = this.searchCellByText('规格型号', i)
          if (writeCell) {
            this.setCellValue(writeCell.r, writeCell.c, '规格型号：可爱哇')
          }
          writeCell = this.searchCellByText('出厂编号', i)
          if (writeCell) {
            this.setCellValue(writeCell.r, writeCell.c, '出厂编号：可爱哇')
          }
          writeCell = this.searchCellByText('使用时间', i)
          if (writeCell) {
            this.setCellValue(writeCell.r, writeCell.c, '使用时间：可爱哇')
          }
          writeCell = this.searchCellByText('下次检验时间', i)
          if (writeCell) {
            this.setCellValue(writeCell.r, writeCell.c, '下次检验时间：可爱哇')
          }
        }
        console.log('tool:', cell)
      }
    },

    setCellValue (r, c, value) {
      luckysheet.setCellValue(r, c, value, {
        success: () => {
        }
      })
    },
    // 点击打开上传文件
    openFile() {
      this.$refs.file.dispatchEvent(new MouseEvent('click'))
    },
    // 从选择的文件中加载
    loadFile() {
      const selectedFile = this.$refs.file.files[0]
      // 后缀获取
      let suffix = ''
      try {
        const fileArr = selectedFile.name.split('.')
        suffix = fileArr[fileArr.length - 1]
      } catch (err) {
        suffix = ''
      }
      if (suffix === '' || (suffix !== 'xls' && suffix !== 'xlsx')) {
        alert('不是有效的EXCEL文件！')
        return
      }
      LuckyExcel.transformExcelToLucky(selectedFile, (importDataJson) => {
        luckysheet.destroy()
        // console.log("the json data:", importDataJson.sheets);
        let op = Object.assign({}, this.defaultOptions)
        // 设置颜色和只读
        /*importDataJson.sheets.map(function (sheet, sindex, sheets) {
          sheet.celldata.map(function (col, cindex, cols) {
            if (col.v.v) { // 列存在值，则设置为只读和灰色背景
              col.v["ae"] = 0;
              col.v["bg"] = '#cccccc';
            }
            // col.v["ht"] = 0; //水平居中
            col.v["vt"] = 0; //垂直居中
          });
        });*/
        importDataJson.sheets[0].config.authority={
          sheet: 1,
          hintText: '不能编辑',
        }
        op = Object.assign(op, {
          data: importDataJson.sheets,
          title: importDataJson.info.name,
          userInfo: importDataJson.info.name.creator,
        })
        luckysheet.create(op)
        // console.log("luckysheet:", luckysheet);
      })
    },
    insertRow() {
      let select = luckysheet.getRange()
      if (select && select.length > 0) {
        let row = select[0].row[0]
        console.log('插入：', row)
        luckysheet.insertRow(row + 1, {
          success: () => {
            console.log('插入成功。。。')
          },
        })
      }
    },
    getRowData() {
      let data = luckysheet.getLuckysheetfile()[0].data

      let select = luckysheet.getRange()
      if (select && select.length > 0) {
        // 获取当前选中行
        let rowIndex = select[0].row[0]
        console.log('复制行号：', rowIndex)
        let newRow = JSON.parse(JSON.stringify(data[rowIndex]))
        // 处理合并列的行标
        newRow.map((cell) => {
          if (cell.mc) {
          }
        })

        console.log('new row:', newRow)
        data.splice(rowIndex, 0, newRow)
      }

      /*
      let data = luckysheet.getRangeValue()
      if (data && data.length > 0) {
        console.log('the data:', data[0])
      }*/
    },
    setSelectValue() {
      let select = luckysheet.getRange()
      if (select && select.length > 0) {
        console.log('插入：', select[0])

        let row = select[0].row[0]
        let col = select[0].column[0]

        luckysheet.setCellValue(row, col, '插入', {
          success: () => {
            console.log('成功在行：%s 列: %s 插入', row, col)
          },
        })
      }
    },
    getSheetData() {
      let data = JSON.stringify(luckysheet.getLuckysheetfile()[0])
      console.log(data)
    },
  },
}
</script>

<style>
.luckysheet-stat-area {
  background-color: transparent !important;
}
</style>