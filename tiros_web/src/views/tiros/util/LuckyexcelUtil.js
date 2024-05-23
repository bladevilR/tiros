import { getFormThresholds, getFormAlertRecords } from '@/api/tirosQualityApi'

export class LuckyexcelUtil {
  getDefaultOptions() {
    return {
      container: 'luckysheet', //luckysheet为容器id
      title: 'sheet',
      column: 26, // 列数
      row: 150, // 行数
      lang: 'zh', // 设定表格语言
      allowEdit: true, // 允许编辑
      showinfobar: false, // 名称栏
      sheetFormulaBar: false,
      showsheetbar: false, // 底部sheet页
      showstatisticBar: true, // 底部计数栏
      enableAddRow: false, // 允许添加行
      enableAddCol: false, // 允许添加列
      showtoolbar: true, // 是否第二列显示工具栏
      showtoolbarConfig: {
        undoRedo: true, //撤销重做，注意撤消重做是两个按钮，由这一个配置决定显示还是隐藏
        paintFormat: true, //格式刷
        currencyFormat: true, //货币格式
        percentageFormat: true, //百分比格式
        numberDecrease: true, // '减少小数位数'
        numberIncrease: true, // '增加小数位数
        moreFormats: true, // '更多格式'
        font: true, // '字体'
        fontSize: true, // '字号大小'
        bold: true, // '粗体 (Ctrl+B)'
        italic: true, // '斜体 (Ctrl+I)'
        strikethrough: true, // '删除线 (Alt+Shift+5)'
        underline: true, // '下划线 (Alt+Shift+6)'
        textColor: true, // '文本颜色'
        fillColor: true, // '单元格颜色'
        border: true, // '边框'
        mergeCell: true, // '合并单元格'
        horizontalAlignMode: true, // '水平对齐方式'
        verticalAlignMode: true, // '垂直对齐方式'
        textWrapMode: true, // '换行方式'
        textRotateMode: false, // '文本旋转方式'
        image:false, // '插入图片'
        link:false, // '插入链接'
        chart: false, // '图表'（图标隐藏，但是如果配置了chart插件，右击仍然可以新建图表）
        postil:  false, //'批注'
        pivotTable: false,  //'数据透视表'
        function: false, // '公式'
        frozenMode: false, // '冻结方式'
        sortAndFilter: false, // '排序和筛选'
        conditionalFormat: false, // '条件格式'
        dataVerification: false, // '数据验证'
        splitColumn: false, // '分列'
        screenshot: false, // '截图'
        findAndReplace: false, // '查找替换'
        protection:false, // '工作表保护'
        print:false, // '打印'
      },
      cellRightClickConfig:{ // 右键配置
        copy: false, // 复制
        copyAs: false, // 复制为
        paste: false, // 粘贴
        insertRow: false, // 插入行
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
        /*cellMousedown: (cell, postion, sheetFile, ctx) => {
          // console.log('cell click:', cell, postion)
          this.getRowColData(postion.r, postion.c)
        },
        workbookCreateAfter: (options) => {
          this.writeOtherExcelInfo()
        }*/
      }
    }
  }

  getDevlovperOptions() {
    return {
      container: 'luckysheet', //luckysheet is the container id
      title: 'sheet',
      column: 13, // 列数
      row: 30, // 行数
      lang: 'zh', // 设定表格语言
      /*cellEditBefore: function (r, c, v) {
        console.log('双击了：', r, c, v)
      },
      cellClicked: function (r, c, v) {
        console.log('单击了：', r, c, v)
      }*/
    }
  }

  /** 获取当前选择的区域, 返回 {} */
  getSelectRange() {
    let range = luckysheet.getRange()
    if (range && range.length > 0) {
      range = range[0]
    } else {
      return null;
    }

    const select={
      r1 : range.row[0],
      r2: range.row[1],
      c1:range.column[0],
      c2:range.column[1]
    }

    return select
  }

  // 判断对象是否存在指定属性
  checkProperty (obj, property) {
    let exist = property in obj && obj[property] != null && obj[property] != undefined
    return exist
  }

  /** 从data中获取指定行列数据*/
  getCell (r, c, data) {
    return data[r][c]
  }

  // 获取单元格文本
  getCellText (cell) {
    let text=''
    if(this.checkProperty(cell,"m")){
      text=cell.m
    } else if(this.checkProperty(cell,"v")){
      text=cell.v
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
  }

  // 根据文本搜索所在单元格, 找到第一个就返回
  searchCellByText (txt,data, r=-1) {
    if (!data) {
      return null;
    }
    if(r===-1) {
      for (let i = 0; i < data.length; i++) {
        for (let j = 0; j < data[i].length; j++) {
          const cell = data[i][j]
          if (cell) {
            let cellText = this.getCellText(cell)
            if (cellText && cellText.indexOf(txt) > -1) {
              return { r: i, c: j, mc: cell.mc, cell: cell }
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
            return { r: r, c: j, mc: cell.mc, cell: cell }
          }
        }
      }
    }
    return null
  }

  /**
   * 寻找这个cell右边的一个单元的列下标
   * @param cell
   * @returns {number|*}
   */
  findCellRightIndex (cell) {
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
  }

  /**
   * 获取cell的 4 下标表示法
   * @param r 默认行
   * @param c 默认列
   * @param cell
   * @returns {{colIndexTo: *, colIndex: *, rowIndex: *, rowIndexTo: *}}
   */
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
  }

  /**
   * 将表格字母列转成序号，必须是大写（最多支持两个字母)，0 对应 A  1 对应 B ......
   * @param c
   * @returns {number}
   */
  char2Index(c) {
    let index = 0
    if (c.length < 2) {
      index = c.charCodeAt() - 65
    }
    if (c.length === 2) {
      let first = c[0]
      let sec = c[1]
      let r = first.charCodeAt() - 65
      r = (r + 1) * 25
      index = r + sec.charCodeAt() - 64
    }
    return index
  }

  /**
   * 根据列序号取对应字母（从0开始，最多支持两位字母）0 对应A  1 对应 B， 25 对应Z， 26 对应 AA ....
   * @param index
   */
  index2Chart(colIndex) {
    let index = colIndex + 1
    if (index <= 26) {
      return String.fromCharCode(64 + index)
    }
    let r = parseInt(index / 26);
    let m = parseInt(index % 26);
    if (m == 0) {
      // 刚好被整除，最后应该是z
      r = r - 1;
    }
    let firstIndex = r + 64;
    let secIndex = index - r * 26;

    let firstChar = String.fromCharCode(firstIndex);
    let secChar = index2Chart(secIndex)
    // console.log('ex:', index, first, sec);
    return firstChar + '' + secChar
  }

  mcToRange (mc) {
    if (mc) {
      /*
      * "mc": { //合并单元格必备属性
            "r": 0, //主单元格的行号
            "c": 0, //主单元格的列号
            "rs": 2, //合并单元格占的行数
            "cs": 2 //合并单元格占的列数
        }*/
      let range = ""
      if (this.checkProperty(mc, 'rs') && this.checkProperty(mc, 'cs')) {
        // 有rs,cs 表示是合并主单元格
      } else {
        // 没有则表示是辅单元格
      }
    } else {
      return ''
    }
  }

  /**
   * 设置指定行列的单元格值
   * @param row  0 开始
   * @param col  0 开始
   * @param value
   */
  setCellValue(row, col, value) {
    luckysheet.setCellValue(row, col, value, {
      success: () => {
        // console.log('成功在：%s 行 %s  列 插入: %s', row, col,value)
      }
    })
  }

  /**
   * 设置指定区域边框（默认为细线边框）
   * @param row1  行号 从0开始
   * @param col1   字母
   * @param row2 行号 从0开始
   * @param col2  字母
   */
  setBorder(row1, col1, row2, col2, color = '#000000') {
    let c1 = this.char2Index(col1)
    let c2 = this.char2Index(col2)
    for (let r = row1; r <= row2; r++) {
      for (let c = c1; c <= c2; c++) {
        luckysheet.setCellFormat(r, c, 'bd', { borderType: 'border-all', style: '1', color: color })
        this.addBorderInfo(r, c)
      }
    }
  }

  addBorderInfo(r, c) {
    // table.config.borderInfo
    let borderInfo = {
      'rangeType': 'cell',
      'value': {
        'row_index': r,
        'col_index': c,
        'l': {
          'style': 1,
          'color': '#000000'
        },
        'r': {
          'style': 1,
          'color': '#000000'
        },
        't': {
          'style': 1,
          'color': '#000000'
        },
        'b': {
          'style': 1,
          'color': '#000000'
        }
      }
    }
    luckysheet.getLuckysheetfile()[0].config.borderInfo.push(borderInfo)
  }

  /**
   * 设置区域格式
   * @param attr
   * @param value
   * @param row1 行号 从0开始
   * @param col1
   * @param row2 行号 从0开始
   * @param col2
   */
  setRangeFormat(attr, value, row1, col1, row2, col2) {
    // console.log('rang:%s%s:%s%s set: %s = %s', col1, row1, col2, row2, attr, value)
    let c1 = this.char2Index(col1)
    let c2 = this.char2Index(col2)
    for (let r = row1; r <= row2; r++) {
      for (let c = c1; c <= c2; c++) {
        try {
          luckysheet.setCellFormat(r, c, attr, value)
          // console.log('rang:%s行%s列 set: %s = %s', r, c, attr, value)
        } catch (e) {
          // 没事
        }
      }
    }
  }
  setRangeCellValue(value, row1, col1, row2, col2) {
    // console.log('rang:%s%s:%s%s set: %s = %s', col1, row1, col2, row2, attr, value)
    let c1 = this.char2Index(col1)
    let c2 = this.char2Index(col2)
    for (let r = row1; r <= row2; r++) {
      for (let c = c1; c <= c2; c++) {
        try {
          luckysheet.setCellValue(r, c, value)
        } catch (e) {
          // 没事
        }
      }
    }
  }

  /**
   *  批量设置值，并替换值得行列，多用于批量设置计算公式
   * @param sources 要复制刷新的单元格数组  [{row: 从0开始的数字，col: 字母}]
   * @param template, 要设置成的值模板（中间的key被替换掉）如：SUM(${COL}1:${COL}8), SUM(D${ROW}:I${ROW})
   * @param replace 从值中查找替换的关键字 ${COL}
   * @param type 替换行或者列 1 行，2 列
   */
  setCellValueByTemplate(sources, template, replace, type = 1) {
    sources.forEach(c => {
      if (type === 1) {
        let value = template.replaceAll(replace, c.row + 1) // 在计算公式中，行号从1开始
        this.setCellValue(c.row, this.char2Index(c.col), value)
      }
      if (type === 2) {
        let value = template.replaceAll(replace, c.col)
        this.setCellValue(c.row, this.char2Index(c.col), value)
      }
    })
  }

  /**
   * 批量设置单元格值，并使用repalce替换 sources 中的 value[attr] 中的占位符
   * @param sources
   * @param replace
   * @param type
   */
  setCellValueByObjectValue(sources, attr, replace, type = 1) {
    sources.forEach(c => {
      if (type === 1) {
        c.value[attr] = c.value[attr].replaceAll(replace, c.row + 1) // 在计算公式中，行号从1开始
        this.setCellValue(c.row, this.char2Index(c.col), c.value)
      }
      if (type === 2) {
        c.value[attr] = c.value[attr].replaceAll(replace, c.col)
        this.setCellValue(c.row, this.char2Index(c.col), c.value)
      }
    })
  }

  /**
   * 数据以数组方式组合(提高渲染效率)
   * @param {*} data LuckySheet的Sheet数据的 data 参数
   * @param {*} r  row
   * @param {*} cChar col的Char字符（AA-ZZ）
   * @param {*} value
   * @param {*} setting 单元格格式参数 默认{ ct: {fa: "General",t: "n"}, fs: '10', ht: '0', vt: 0 }
   * @returns
   */
  setCellValueByArrayData(data, r, cChar, value, setting = {}) {
    const cellV = {}
    for (const key in setting) {
      cellV[key] = setting[key]
    }
    if (!setting['ct']) {
      cellV['ct'] = { fa: "General", t: "n" }
    }
    if (!setting['fs']) {
      cellV['fs'] = '10'
    }
    if (!setting['ht']) {
      cellV['ht'] = 0
    }
    if (!setting['vt']) {
      cellV['vt'] = 0
    }
    cellV['m'] = value
    cellV['v'] = value
    return data.push({ r: r, c: this.char2Index(cChar), v: cellV })
  }

  /**
   * 合并单元格（填入数据后渲染方式）
   * @param {*} data LuckySheet的Sheet数据的 data 参数
   * @param {*} cCharStart 开始单元格（A1）
   * @param {*} cCharEnd 结束单元格(A10)
   */
  setCellMergeByValue(data, cCharStart, cCharEnd) {
    const rIndex = Number(/[0-9].*/.exec(cCharStart)[0]);
    const cIndex = this.char2Index(/^[A-Z]{1,2}/.exec(cCharStart)[0]);
    const rIndex2 = /[0-9].*/.exec(cCharEnd)[0];
    const cIndex2 = this.char2Index(/^[A-Z]{1,2}/.exec(cCharEnd)[0]);

    (data.config['merge'] || (data.config['merge'] = {}))[`${rIndex - 1}_${cIndex}`] = {
      r: rIndex - 1,
      c: cIndex,
      rs: (rIndex2 - rIndex) + 1,
      cs: (cIndex2 - cIndex) + 1
    }
  }

  //单元格设定条件变红
  async setRangeConditionalOncell(formId, formInstId = '') {
    let dataList = []
    try {
      getFormThresholds(formId).then(res => {
        if (res.success) {
          let settingList = res.result
          if (settingList && settingList.length > 0) {
            settingList.forEach((element) => {
              if (element.operator) {
                formatConditional(element, element.operator, element.threshold)
              }
              if (element.operator2) {
                formatConditional(element, element.operator2, element.threshold2)
              }
            })
            // 检查修复值并设置样式
            getFormAlertRecords(formInstId).then(res => {
              res.result.forEach(record => {
                if (record.fixedValue) {
                  let range = record.linkDomain.split('$')
                  let row = range[1]
                  let cell = range[2]
                  luckysheet.setCellFormat(row, cell, 'bg', '#ffff00')
                  luckysheet.setCellFormat(row, cell, 'ps', {
                    "left": 92,
                    "top": 0,
                    "width": 91,
                    "height": 12,
                    "value": `修复前预警值：${record.alertValue}`,
                    "isshow": false
                  })
                }
              });
            })
          }
          // console.log('输出');
          // console.log(dataList);
        }
      })
      function formatConditional(recordData, operator, threshold) {
        //type 来源bu_operator
        let operatorList = []
/*        console.log('获取值');
        console.log(recordData);
        console.log(threshold);*/
        switch (operator) {
          case 1:
            // 等于
            operatorList = ['equal']
            setFormatValue(recordData, operatorList, threshold)
            break
          case 2:
            // 小于
            // operatorList = ['lessThan']
            operatorList = ['greaterThan', 'equal']
            setFormatValue(recordData, operatorList, threshold)
            break
          case 3:
            // 小于等于
            // operatorList = ['lessThan', 'equal']
            operatorList = ['greaterThan']
            setFormatValue(recordData, operatorList, threshold)
            break
          case 4:
            // 大于
            // operatorList = ['greaterThan']
            operatorList = ['lessThan', 'equal']
            setFormatValue(recordData, operatorList, threshold)
            break
          case 5:
            // 大于等于
            // operatorList = ['greaterThan', 'equal']
            operatorList = ['lessThan']
            setFormatValue(recordData, operatorList, threshold)
            break
          case 6:
            // 不等于
            luckysheet.setCellFormat(recordData.row1, recordData.col1, 'bg', '#ff0000')
            luckysheet.setRangeConditionalFormatDefault('equal', [threshold], {
              format: { cellColor: '#ffffff', textColor: null },
              cellrange: { row: [recordData.row1, recordData.row2], column: [recordData.col1, recordData.col2] },
            })
            break
        }
      }
      function setFormatValue(recordData, operatorList, threshold) {
        operatorList.forEach(element => {
          luckysheet.setRangeConditionalFormatDefault(element, [threshold], {
            format: { cellColor: '#ff0000', textColor: null },
            cellrange: { row: [recordData.row1, recordData.row2], column: [recordData.col1, recordData.col2] },
            success: function (data) {
   /*           console.log('设定结果');
              console.log(data);*/
            }
          })
        })
      }
    } catch (error) {
      console.log(error)
    }
  }

  /**
   * 获取中文项对应的属性key
   * @param txtName
   * @returns {string}
   */
  getAttrKeyByName (txtName) {
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
  }
}

export default new LuckyexcelUtil()