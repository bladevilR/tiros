<template>
  <div style="overflow-x: hidden">
    <div class="table-page-search-wrapper">
      <a-form-model ref="form" layout="inline" :model="form" :rules="rules">
        <a-row :gutter="24">
          <a-col :span="3">
            <a-form-model-item prop="code" label="编码">
              <a-input v-model="form.code" placeholder="表单编码"></a-input>
            </a-form-model-item>
          </a-col>
           <a-col :span="3">
            <a-form-model-item prop="code" label="分类">
              <j-dict-select-tag
                :triggerChange="true"
                v-model="form.category"
                placeholder="请选择"
                dictCode="sys_category,name,id,id!='1328995025744318466'"
              />
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-form-model-item prop="name" label="名称">
              <a-input v-model="form.name" placeholder="表单名称"></a-input>
            </a-form-model-item>
          </a-col>

          <a-col :span="5">
            <a-form-model-item class="a-form-item-width-gy" prop="reguId" label="技术规程">
              <a-select
                allowClear
                v-model="form.reguId"
                @change="reguDetaiChange"
              >
                <a-select-option v-for="(item,k) in treeDataSource" :key="k" :value="item.id">
                  {{item.name + (item.version ? `(${item.version})` : '') }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>

          <a-col :span="4">
            <a-form-model-item prop="lineId" label="所属线路">
              <j-dict-select-tag
                :triggerChange="true"
                v-model="form.lineId"
                placeholder="请选择"
                dictCode="bu_mtr_line,line_name,line_id|train_type_id"
              />
            </a-form-model-item>
          </a-col>
          <a-col :span="3">
            <a-form-model-item prop="repairProgramId" label="所属修程">
              <j-dict-select-tag
                ref="lineSelect"
                :triggerChange="true"
                v-model="form.repairProgramId"
                placeholder="请选择"
                dictCode="bu_repair_program,name,id"
              />
            </a-form-model-item>
          </a-col>

        </a-row>
        <a-row>
          <a-col :span="4">
            <a-form-model-item label="当前选中">
              {{ curRange }} <span v-if="curRange && !manyCell">[{{ curIsAllow ? '可编辑' : '不可编辑' }}]</span>
              <!--<a-checkbox @change="onAllowChange" v-model="curIsAllow" style="margin-left: 15px;">
                可编辑
              </a-checkbox>-->
            </a-form-model-item>
          </a-col>
          <a-col :span="5">
            <a-space>
              <a-button type="dashed" @click="setCellEditAble(null,true)">可编辑</a-button>
              <a-button type="dashed" v-if="form.category !== '1328995639249358850'" @click="allEditAble(true)">全部可编辑</a-button>
              <a-button type="dashed" @click="setCellEditAble(null,false)">不可编辑</a-button>
            </a-space>
          </a-col>
          <a-col :span="5" class="space-btn">
            <a-button type="primary" @click="onSave">保存</a-button>
            <a-button @click="openFile">
              导入表格
              <input ref="file" type="file" style="display: none" @change="uploadExcel" />
            </a-button>
          </a-col>
        </a-row>
      </a-form-model>
    </div>
    <div id="luckysheet" style="margin: 0px; padding: 0px; width: 100%; height: calc(100vh - 127px)"></div>
    <div
      v-if="showLoading"
      style="
        width: 100%;
        text-align: center;
        position: absolute;
        top: 0px;
        height: 100%;
        font-size: 16px;
        z-index: 1000000000;
        background: #fff;
      ">
      <div style="position: relative; top: 45%; width: 100%">
        <div class="luckysheetLoaderGif"></div>
        <span>加载中...</span>
      </div>
    </div>
  </div>
</template>

<script>
import { addCustomForm, updateCustomForm, getFormContent,getReguList } from '@api/tirosApi'
import { randomNumber } from '@/utils/util'
import clone from 'clone'
import luckysheetUtil from '@/views/tiros/util/LuckyexcelUtil'

export default {
  name: 'edit',
  props: ['formInfo'],
  data() {
    return {
      treeDataSource: [],
      showLoading: false,
      form: {
        code: '',
        name: '',
        category: '',
        content: '',
        type: 1,
        lineId: '',
        repairProgramId: '',
        createGroupId: '',
        reguId:'',
        datajson: ''
      },
      rules: {
        code: [
          { required: true, message: '请输入表单编码', trigger: 'blur' },
          { max: 7, message: '输入长度不能超过7字符!', trigger: 'blur' },
        ],
        name: [
          { required: true, message: '请输入表单名称', trigger: 'blur' },
          { max: 100, message: '输入长度不能超过100字符!', trigger: 'blur' },
        ],
        lineId: [{ required: true, message: '请选择线路', trigger: 'blur' }],
        repairProgramId: [{ required: true, message: '请选择修程', trigger: 'blur' }],
      },
      curRange: '',
      curIsAllow: false,
      manyCell: false,
      allowRange: '',
      colNames: [],
      defaultOptions: {
        container: 'luckysheet', //luckysheet为容器id
        title: 'sheet',
        column: 26, // 列数
        row: 50, // 行数
        lang: 'zh', // 设定表格语言
        allowEdit: true, // 允许编辑
        showinfobar: false, // 名称栏
        sheetFormulaBar: true,
        showsheetbar: false, // 底部sheet页
        showstatisticBar: true, // 底部计数栏
        enableAddRow: false, // 允许添加行
        enableAddCol: false, // 允许添加列
        showtoolbar: true, // 是否第二列显示工具栏
        hook: {
          sheetMouseup: (cell, postion, sheetFile, ctx) => {
            this.onSelectRange()
          },
          workbookCreateAfter: (options)=>{
            if (this.form.category === '1328995639249358850') {
              luckysheet.getLuckysheetfile().forEach((sheet, index) => {
                // 设置默认可写状态
                this.setDefaultAllowEdit(sheet)
              })
            }
          }
        },
      },
    }
  },
  beforeDestroy() {
    // 离开销毁Luckysheet
    luckysheet.destroy()
  },
  mounted() {
    this.queryTreeData();
   /* try {
      luckysheet.destroy()
    } catch (e) {

    }*/
    for (let i = 65; i < 91; i++) {
      this.colNames.push(String.fromCharCode(i))
    }

    if (this.formInfo) {
      Object.assign(this.form, this.formInfo)
    }
    const op = clone(this.defaultOptions)
    op.data = [
      {
        name: 'Sheet1',
        color: '',
        status: '1',
        order: '0',
        index: 0,
        ch_width: 2044,
        rh_height: 1080,
      },
    ]

    if (this.form.id) {
      getFormContent(this.form.id)
        .then((res) => {
          if (res.success) {
            const sheet = JSON.parse(res.result)
            // 设置工作表保护, 设置可以编辑的单元格
            op.data = [sheet]
            luckysheet.destroy()
            luckysheet.create(op)
          }
        })
        .catch((err) => {
          console.error('加载表单内容异常：', err)
          this.$message.error('加载表单内容异常')
          luckysheet.destroy()
          luckysheet.create(op)
        })
    } else {
      luckysheet.destroy()
      luckysheet.create(op)
    }
  },
  methods: {
    reguDetaiChange(e){
      if(e){
        let arr = this.treeDataSource.filter((item,index)=>{
          return item.id === e;
        })
        if(arr.length>0){
          arr = arr[0];
          this.form.lineId = arr.lineId;
          this.form.repairProgramId = arr.repairProId;
          this.$forceUpdate()
        }
      }
    },
    queryTreeData() {
      getReguList()
        .then((res) => {
          if (res.success) {
            this.treeDataSource = res.result
          }
        })
    },
    onSave() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // console.log('save sheet:', luckysheet.getAllSheets()[0])
          const saveSheet = luckysheet.getAllSheets()[0]

          if (this.form.category === '1328995639249358850') {
            // 如果当前分类是作业记录表
            try {
              let metaData = this.getMetadata(saveSheet,true)
              if (metaData) {
                this.form.datajson = JSON.stringify(metaData)
              } else {
                console.error('获取描述数据失败：', metaData)
              }
            } catch (e) {
              console.error('获取描述数据异常：', e)
            }
          }

          saveSheet.scrollLeft=0
          saveSheet.scrollTop = 0
          saveSheet.data=[] // 将data清空，只保留celldata进行初始化
          saveSheet.luckysheet_select_save=[] // 清空选择区域
          this.form.content = JSON.stringify(saveSheet)
          if (!this.form.createGroupId) {
            this.form.createGroupId = this.$store.getters.userInfo.departId
          }
          if (!this.form.id) {
            addCustomForm(this.form)
              .then((res) => {
                if (res.success) {
                  this.$message.success('保存成功')
                  this.$emit('saveSuccess')
                  this.closeWindow()
                } else {
                  console.error('保存自定义表单失败:', res.message)
                  this.$message.error('保存失败')
                }
              })
              .catch((err) => {
                console.error('保存自定义表单异常：', err)
                this.$message.error('保存自定义表单异常')
              })
          } else {
            updateCustomForm(this.form)
              .then((res) => {
                if (res.success) {
                  this.$message.success('保存成功')
                  this.$emit('saveSuccess')
                  this.closeWindow()
                } else {
                  console.error('保存自定义表单失败:', res.message)
                  this.$message.error('保存失败')
                }
              })
              .catch((err) => {
                console.error('保存自定义表单异常：', err)
                this.$message.error('保存自定义表单异常')
              })
          }
        } else {
          console.error('输入内容验证失败!!')
          return false
        }
      })
    },
    closeWindow() {
      luckysheet.destroy()
      this.$emit('update:visible', false)
    },
    // 点击打开上传文件
    openFile() {
      this.$refs.file.dispatchEvent(new MouseEvent('click'))
    },
    uploadExcel(e) {
      this.showLoading = true
      const files = e.target.files
      if (files == null || files.length === 0) {
        this.showLoading = false
        return
      }

      // 后缀获取
      let suffix = ''
      try {
        const fileArr = files[0].name.split('.')
        suffix = fileArr[fileArr.length - 1]
      } catch (err) {
        suffix = ''
      }
      if (suffix === '' || suffix !== 'xlsx') {
        this.$message.error('不是有效的EXCEL文件,只支持xlsx格式的EXCEL文件')
        this.showLoading = false
        return
      }
      LuckyExcel.transformExcelToLucky(files[0], (exportJson, luckysheetfile) => {
        if (exportJson.sheets == null || exportJson.sheets.length === 0) {
          this.$message.error('读取EXCEL文件失败, 当前不支持xls格式的excel文件!')
          this.showLoading = false
          return
        }

        let op = clone(this.defaultOptions)
        // 设置全部可编辑
        exportJson.sheets.forEach((sheet) => {
          sheet.config.authority = {
            sheet: 0, // 开启保护
            hintText: '禁止编辑',
            allowRangeList: [],
          }
        })

        op.data = exportJson.sheets
        luckysheet.destroy()
        this.showLoading = false
        luckysheet.create(op)
        this.form.name = exportJson.info.name.replace('.xlsx', '')
        this.form.remark = exportJson.info.name
        this.form.code = randomNumber(6) + ''
        this.$forceUpdate()

        setTimeout(()=>{
          // 如果当前分类是作业记录表，进行特殊处理
          if (this.form.category === '1328995639249358850') {
            try {
              const saveSheet = luckysheet.getLuckysheetfile()[0]
              // 设置默认可写状态
              //this.setDefaultAllowEdit(saveSheet)

              let metaData = this.getMetadata(saveSheet, true)

              if (metaData) {
                this.form.datajson = JSON.stringify(metaData)
              } else {
                console.error('获取描述数据失败：', metaData)
              }
            } catch (e) {
              console.error('获取描述数据异常：', e)
            }
          }
        },2000)
      })
    },

    // 设置默认可编辑
    setDefaultAllowEdit (sheet) {
      if (!luckysheetUtil.checkProperty(sheet.config, 'authority')) {
        // 没有配置就不搞了
        return
      }

      if (sheet.config.authority.allowRangeList && sheet.config.authority.allowRangeList.length > 0) {
        // 说明已经有了，不需要初始化了
        return
      }
      sheet.config.authority.sheet = 1
      // 默认设置所有的单元格为可写
      let ranges = []
      sheet.celldata.forEach(cd => {
        ranges.push({r: cd.r, c: cd.c})
      })
      let allowCells = this.getSelectCells(sheet, ranges)
      this.setCellEditAble(sheet, true, allowCells)

      // console.log('the default edit:', sheet)
    },
    getShowRowIndex (rowIndex) {
      return rowIndex + 1
    },
    // 获取当前选择
    onSelectRange() {
      const sheet = luckysheet.getLuckysheetfile()[0]
      const range = luckysheet.getRange()[0]
      this.curRange =  this.colNames[range.column[0]] + this.getShowRowIndex(range.row[0]) + ':' + this.colNames[range.column[1]] +  this.getShowRowIndex(range.row[1])
      if(sheet.config.authority && sheet.config.authority.allowRangeList){
        this.curIsAllow = !!this.searchAllowRange(range, sheet.config.authority.allowRangeList)
        this.manyCell = range.column[0] !== range.column[1] || range.row[0] !== range.row[1]
      }
    },
    // 检查当前单元格是否在被允许编辑列表中
    searchAllowRange (range, allowRangeList) {
      let isAllow = false
      let allowRange = null
      // console.log('allows2:',luckysheet.getAllSheets()[0].config.authority.allowRangeList)
      if (!allowRangeList || allowRangeList.length < 1) {
        // 没有配置任何，说明都不允许
        return null
      }

      let rg = '$' + this.colNames[range.column[0]] + '$' + this.getShowRowIndex(range.row[0])

      // 是否非单个单元格
      let searchSingleCell = range.column[0] === range.column[1] && range.row[0] === range.row[1]
      if (!searchSingleCell) {
        rg += ':$' + this.colNames[range.column[1]] + '$' + this.getShowRowIndex(range.row[1])
      }

      allowRangeList.forEach((allow, index) => {
        // console.log('allow:', allow)
        if (allow.sqref === rg) {
          isAllow = true
          allowRange = allow
          allowRange['index'] = index
          return true
        }
      })
      return allowRange
      //return isAllow
    },
    // 设置单元格是否可编辑
    setCellEditAble (sheet, able, cells = null) {
      if (sheet == null) {
        sheet = luckysheet.getLuckysheetfile()[0]
      }
      if (!cells) {
        // 获取所有被选中的单元格
        cells = this.getSelectCells(sheet);
      }

      if (cells.length < 1) {
        // 没有任何单元格
        return
      }

      cells.forEach(cell => {
        let cellRange = null

        if (luckysheetUtil.checkProperty(cell,'mc')) {
          // 合并单元格
          if (luckysheetUtil.checkProperty(cell.mc,'rs') && luckysheetUtil.checkProperty(cell.mc,'cs')) {
            // 主单元格才操作
            cellRange = {
              row: [cell.mc.r, cell.mc.r + cell.mc.rs - 1],
              column: [cell.mc.c, cell.mc.c + cell.mc.cs - 1]
            }
          } else {
            // 合并单元格的非主单元格不操作
            return
          }
        } else {
          cellRange = {
            row: [cell.r, cell.r],
            column: [cell.c, cell.c],
          }
        }

        const ableEditCell = this.searchAllowRange(cellRange,sheet.config.authority.allowRangeList)
        //console.log('存在的设置:', ableEditCell)

        if (!!ableEditCell) {
          // 有在允许编辑列表中
          if (!able) {
            // 删除这个允许，变成不可 编辑
            sheet.config.authority.allowRangeList.splice(ableEditCell.index, 1)
          }
        } else {
          // 没有在允许列表中，要添加到允许编辑列表
          if (able) {
            let rg = '$' + this.colNames[cellRange.column[0]] + '$' + this.getShowRowIndex(cellRange.row[0])
            // 要查找的是单个单元格
            let searchSingleCell = cellRange.column[0] === cellRange.column[1] && cellRange.row[0] === cellRange.row[1]
            if (!searchSingleCell) {
              rg += ':$' + this.colNames[cellRange.column[1]] + '$' + this.getShowRowIndex(cellRange.row[1])
            }
            const name = 'area_' + rg
            const allow = this.buildAllowRange(name, rg)

            sheet.config.authority.allowRangeList.push(allow)
          }
        }
      })

      // this.$message.success("设置成功，注意保存设置！")
    },
    // 设置全部可编辑
    allEditAble (able) {
      const sheet = luckysheet.getLuckysheetfile()[0]
      if (able) {
        sheet.config.authority.sheet = 0
      } else {
        sheet.config.authority.sheet = 1
        sheet.config.authority.allowRangeList = []
      }

      this.$message.success("设置成功，注意保存设置！")
    },
    // 获取当前选择的单元格(或者指定区域包括的）
    getSelectCells (sheet, ranges = null) {
      if (!ranges) {
        ranges = luckysheet.getRangeWithFlatten()
      }
      let cells = []
      ranges.forEach(range => {
        let cell = sheet.data[range.r][range.c]
        if (cell) {
          cells.push(Object.assign({
            r: range.r,
            c: range.c
          }, cell))
        } else {
          // 没有找到这样的cell, 表示为null
          cells.push(
            {
              r: range.r,
              c: range.c
            }
          )
        }
      })
      return cells
    },
    // 构造可编辑区
    buildAllowRange (name,addCol) {
      return {
        name: name, //名称
        hintText: '', //提示文字
        algorithmName: 'None', //加密方案：MD2,MD4,MD5,RIPEMD-128,RIPEMD-160,SHA-1,SHA-256,SHA-384,SHA-512,WHIRLPOOL
        saltValue: null, //密码解密的盐参数，为一个自己定的随机数值
        sqref: addCol, //区域范围, 行号是从1开始算的哦
      }
    },

    // 将区域变化成单个的单元格一维数组
    getRangeWithFlatten (range) {
      let cells = []
      for (let r = range.row[0]; r <= range.row[1]; r++) {
        for (let c = range.column[0]; c <= range.column[1]; c++) {
          cells.push({
            r: r,
            c: c
          })
        }
      }
      return cells
    },

    // 作业记录表相关操作
    getMetadata (sheet, showMsg = true) {
      const data = luckysheet.transToData(sheet.celldata)
      let metaData = {
        checkDetail: {
          id: 'checkDetail',
          name: '作业记录表作业项明细',
          type: 'zone',
          rowIndex: -1,
          colIndex: 0,
          rowIndexTo: -1,
          colIndexTo: -1,
          rows: []
        }
      }

      //--------------------------作业项 明细处理-------------------------------------
      let dataText = [] // excel单元格文本, 这里只是为了后面好进行逻辑处理
      for (let i = 0; i < data.length; i++) {
        const row = data[i]
        let newRow = []
        dataText.push(newRow)
        // 循环该行所有列，填充每个单元格文本
        for (let j = 0; j < row.length; j++) {
          const cell = row[j]
          if (cell) {
            let cellText = luckysheetUtil.getCellText(cell)
            if (!cellText) {
              // 当前是合并单元格
              if (luckysheetUtil.checkProperty(cell, 'mc')) {
                const mc = cell.mc
                if (luckysheetUtil.checkProperty(mc, 'r') && luckysheetUtil.checkProperty(mc, 'c') && luckysheetUtil.checkProperty(mc, 'rs') && luckysheetUtil.checkProperty(mc, 'cs')) {
                  // 主单元格
                } else if (luckysheetUtil.checkProperty(mc, 'r') && luckysheetUtil.checkProperty(mc, 'c')) {
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
            if (cellText) {
              newRow.push(cellText)
            } else {
              newRow.push('')
            }
          } else {
            newRow.push('')
          }
        }

        // 查找作业项的标题所在行
        if (newRow.includes('结果') && newRow.includes('自检') && newRow.includes('互检')) {
          let detailTitle = {
            id: 'detailTitle', name: '明细标题行', type: 'row', rowIndex: i, rowIndexTo: i,
            cells: {}
          }
          metaData.checkDetail.rows.push(detailTitle)
          metaData.checkDetail.rowIndex = i // 开始行

          // 找出所有标题
          for (let j = 0; j < row.length; j++) {
            const cell = row[j]
            if (cell) {
              metaData.checkDetail.colIndexTo = j
              let cellText = luckysheetUtil.getCellText(cell)
              if (cellText) {
                let location = luckysheetUtil.getCellLocation(i, j, cell)
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
                  break;
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
                  break;
                case  '位置':
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
                  break;
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
                  break;
                case '技术要求':
                  detailTitle.cells.jsyq = {
                    id: 'jsyq',
                    name: '技术要求',
                    type: 'cell',
                    cellType: 'title',
                    rowIndex: location.rowIndex,
                    rowIndexTo: location.rowIndexTo,
                    colIndex: location.colIndex,
                    colIndexTo: location.colIndexTo
                  }
                  break;
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
                  break;
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
                  break;
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
                  break;
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
                  break;
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
                default:

                }
              }
            }
          }
        }

        // 判断是否检查项目结束行： 主要判断序号列是否有值且大于0， 如果是检查项，则添加数据描述
        if (metaData.checkDetail.rowIndex > -1 && metaData.checkDetail.rows.length > 0) {
          let titleMeta = metaData.checkDetail.rows[0].cells // 默认第一行为标题
          const xh = newRow[titleMeta.xh.colIndex]
          if (xh && parseInt(xh) > 0) {
            // 更新明细数据最后的行号
            metaData.checkDetail.rowIndexTo = i

            // 判断"作业内容"这列是否为合并单元格，如果是合并单元格则只有主单元格才添加
            const cell = row[titleMeta.zynr.colIndex]
            let addMetaData = false
            if (cell) {
              if (luckysheetUtil.checkProperty(cell, 'mc')) {
                const mc = cell.mc
                if (luckysheetUtil.checkProperty(mc, 'r') && luckysheetUtil.checkProperty(mc, 'c') && luckysheetUtil.checkProperty(mc, 'rs') && luckysheetUtil.checkProperty(mc, 'cs')) {
                  // 主单元格
                  addMetaData = true
                } else if (luckysheetUtil.checkProperty(mc, 'r') && luckysheetUtil.checkProperty(mc, 'c')) {
                  // 合并单元格（辅）不加
                  addMetaData = false
                }
              } else {
                addMetaData = true
              }
            }

            if (addMetaData) {
              // 添加一个检查项
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
      }

      let haveCheckDetail = true
      if (metaData.checkDetail.rows.length < 1 || metaData.checkDetail.rowIndex === -1 || metaData.checkDetail.rowIndexTo === -1) {
        haveCheckDetail = false
        if(showMsg) {
          this.$message.warn('该作业记录表不是标准格式，无法找到作业项，请知晓')
        }
      } else {
        // 如果有作业记录明细，则将该区域设置成只读
        let titleRow = metaData.checkDetail.rows[0].cells

        // let startColIndex = titleRow.zyqk.colIndex, endColIndex = titleRow.zhuanjian.colIndex
        // 将从作业情况列开始到专检列 结束的所有明细行区域设置为只读
        /*let range = {
          row: [metaData.checkDetail.rowIndex, metaData.checkDetail.rowIndexTo],
          column: [titleRow.zyqk.colIndex, titleRow.zyqk.colIndex]
        }*/

        let ableCells = []
        // 作业情况区域
        if(luckysheetUtil.checkProperty(titleRow,'zyqk')) {
          ableCells.push(...this.getRangeWithFlatten({
            row: [metaData.checkDetail.rowIndex, metaData.checkDetail.rowIndexTo],
            column: [titleRow.zyqk.colIndex, titleRow.zyqk.colIndex]
          }))
        }
        // 结果
        if(luckysheetUtil.checkProperty(titleRow,'jg')) {
          ableCells.push(...this.getRangeWithFlatten({
            row: [metaData.checkDetail.rowIndex, metaData.checkDetail.rowIndexTo],
            column: [titleRow.jg.colIndex, titleRow.jg.colIndex]
          }))
        }
        // 自检
        if(luckysheetUtil.checkProperty(titleRow,'zijian')) {
          ableCells.push(...this.getRangeWithFlatten({
            row: [metaData.checkDetail.rowIndex, metaData.checkDetail.rowIndexTo],
            column: [titleRow.zijian.colIndex, titleRow.zijian.colIndex]
          }))
        }
        // 互检
        if(luckysheetUtil.checkProperty(titleRow,'hujian')) {
          ableCells.push(...this.getRangeWithFlatten({
            row: [metaData.checkDetail.rowIndex, metaData.checkDetail.rowIndexTo],
            column: [titleRow.hujian.colIndex, titleRow.hujian.colIndex]
          }))
        }
        // 专检
        if(luckysheetUtil.checkProperty(titleRow,'zhuanjian')) {
          ableCells.push(...this.getRangeWithFlatten({
            row: [metaData.checkDetail.rowIndex, metaData.checkDetail.rowIndexTo],
            column: [titleRow.zhuanjian.colIndex, titleRow.zhuanjian.colIndex]
          }))
        }

        // 设置不可编辑区域（这里默认的都是单个单元格）
        this.setCellEditAble(sheet,false, this.getSelectCells(sheet, ableCells))
      }

      if (!haveCheckDetail) {
        // 如果没有找到检查项， 后面的不设置了？ 如果没有就全部设置为可编辑
        sheet.config.authority.sheet = 0
        return
      }

      // -------------------------------质量检查处理-----------------------------------
      /*let checkResult = luckysheetUtil.searchCellByText('检查结果', data)
      if (checkResult && checkResult.r!==metaData.checkDetail.rowIndex) {
        // 找到了，且不是明细标题中的
        const colIndex = luckysheetUtil.findCellRightIndex(checkResult)
        metaData['checkResult']= {
          id:'checkResult',
          name: '检查结果',
          type: 'cell',
          cellType: 'write',
          isJoin: false, // 内容写入方式 false 覆盖写入， true 在原有内容上拼接写入
          original: '', // 原始内容，拼接是要写入
          rowIndex: checkResult.r,
          colIndex: colIndex,
          rowIndexTo: checkResult.r,
          colIndexTo: colIndex
        }
      } else {
        this.$message.error("该作业记录表不是标准格式，无法找到 '检查结果' 输入处，请知晓")
      }

      let checkDate = luckysheetUtil.searchCellByText('检查日期', data)
      if (checkDate && checkDate.r!==metaData.checkDetail.rowIndex) {
        // 找到了，且不是明细标题中的
        const colIndex = luckysheetUtil.findCellRightIndex(checkDate)
        metaData['checkDate']= {
          id:'checkDate',
          name: '检查日期',
          type: 'cell',
          cellType: 'write',
          isJoin: false, // 内容写入方式 false 覆盖写入， true 在原有内容上拼接写入
          original: '', // 原始内容，拼接是要写入
          rowIndex: checkDate.r,
          colIndex: colIndex,
          rowIndexTo: checkDate.r,
          colIndexTo: colIndex
        }
      } else {
        this.$message.error("该作业记录表不是标准格式，无法找到 '检查日期' 输入处，请知晓")
      }

      let checkMan = luckysheetUtil.searchCellByText('质量负责人', data)
      if (checkMan && checkMan.r!==metaData.checkDetail.rowIndex) {
        // 找到了，且不是明细标题中的
        const colIndex = luckysheetUtil.findCellRightIndex(checkMan)
        metaData['checkMan']= {
          id:'checkMan',
          name: '质量负责人',
          type: 'cell',
          cellType: 'write',
          isJoin: false, // 内容写入方式 false 覆盖写入， true 在原有内容上拼接写入
          original: '', // 原始内容，拼接是要写入
          rowIndex: checkMan.r,
          colIndex: colIndex,
          rowIndexTo: checkMan.r,
          colIndexTo: colIndex
        }
      } else {
        this.$message.error("该作业记录表不是标准格式，无法找到 '质量负责人' 输入处，请知晓")
      }*/
      // 质量检查改成 整行数据覆盖写入，所有有一个就可以了
      let checkResult = luckysheetUtil.searchCellByText('检查结果', data)

      if (checkResult && checkResult.r !== metaData.checkDetail.rowIndex) {
        // 找到了，且不是明细标题中的
        //const colIndex = luckysheetUtil.findCellRightIndex(checkResult)
        metaData['checkResult'] = {
          id: 'checkResult',
          name: '检查结果',
          type: 'cell',
          cellType: 'write',
          isJoin: false, // 内容写入方式 false 覆盖写入， true 在原有内容上拼接写入
          original: '', // 原始内容，拼接是要写入
          rowIndex: checkResult.r,
          colIndex: checkResult.c,
          rowIndexTo: checkResult.r,
          colIndexTo: checkResult.c
        }

        let range = {
          r: checkResult.r,
          c: checkResult.c
        }
        if (checkResult.mc && checkResult.mc.rs && checkResult.mc.cs) {
          // 主单元格才操作
          range = {
            r: checkResult.mc.r,
            c: checkResult.mc.c
          }
        }

        // 将质量检查区域设置为不可编辑
        let ckCells = this.getSelectCells(sheet, [range])
        this.setCellEditAble(sheet,false, ckCells)
      } else {
        if(showMsg) {
          this.$message.warn('该作业记录表不是标准格式，无法找到 \'检查结果\' 输入处，请知晓')
        }
      }

      return metaData
    }
  },
}
</script>

<style>
.luckysheet-stat-area {
  background-color: transparent !important;
}
/*解决输入框被弹窗挡住的问题啊*/
.luckysheet-input-box{
  z-index: 9999;
}
</style>
<style scope>
.a-form-item-width-gy .ant-col.ant-form-item-control-wrapper{
  width: calc(100% - 77px);
}
</style>