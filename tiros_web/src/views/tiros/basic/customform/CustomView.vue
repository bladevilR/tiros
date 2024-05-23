<template>
  <div style="width: 100%; overflow-x: hidden">
    <div id="luckysheet" style="margin: 0px; padding: 0px; width: 100%; height: calc(100vh - 85px)"></div>
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
      "
    >
      <div style="position: relative; top: 45%; width: 100%">
        <div class="luckysheetLoaderGif"></div>
        <span>加载中...</span>
      </div>
    </div>
  </div>
</template>

<script>
import { getFormContent } from '@api/tirosApi'
import clone from 'clone'

export default {
  name: 'view',
  props: ['customId'],
  data() {
    return {
      showLoading: false,
      defaultOptions: {
        container: 'luckysheet', //luckysheet为容器id
        title: 'sheet',
        column: 26, // 列数
        row: 50, // 行数
        lang: 'zh', // 设定表格语言
        allowEdit: true, // 允许编辑
        showinfobar: false, // 名称栏
        sheetFormulaBar: false,
        showsheetbar: false, // 底部sheet页
        showstatisticBar: true, // 底部计数栏
        enableAddRow: false, // 允许添加行
        enableAddCol: false, // 允许添加列
        showtoolbar: true, // 是否第二列显示工具栏
      },
    }
  },
  beforeDestroy() {
    // 离开销毁Luckysheet
    luckysheet.destroy()
  },
  mounted() {
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

    if (this.customId) {
      getFormContent(this.customId)
        .then((res) => {
          if (res.success) {
            const sheet = JSON.parse(res.result)
            // 设置工作表保护, 设置可以编辑的单元格
            /*if(!sheet.config.authority || sheet.config.authority.allowRangeList.length<1) {
            sheet.config.authority = {
              sheet: 1,
              hintText: '禁止编辑',
              allowRangeList: []
            }
            sheet.celldata.map((col, cindex, cols) => {
              if ( col.v && (col.v.v || (col.v.ct && col.v.ct.s))) { // 列存在值，则设置为只读和灰色背景
                col.v['ae'] = 0
                col.v['bg'] = '#eae9e9'
              } else {
                if (!col.v.mc) {
                  const colName = this.colNames[col.c]
                  // const allCol = '$' + colName + '$' + col.r+':$'+colName+'$'+ (col.r + 1)
                  const allCol = '$' + colName + '$' + (col.r + 1)
                  const name = 'area_' + colName + col.r
                  sheet.config.authority.allowRangeList.push({
                    name: name, //名称
                    hintText: "", //提示文字
                    algorithmName: "None",//加密方案：MD2,MD4,MD5,RIPEMD-128,RIPEMD-160,SHA-1,SHA-256,SHA-384,SHA-512,WHIRLPOOL
                    saltValue: null, //密码解密的盐参数，为一个自己定的随机数值
                    sqref: allCol //区域范围
                  })
                }
              }
              // col.v["ht"] = 0; //水平居中
              col.v["vt"] = 0; //垂直居中
            });
          }*/

            // 任何情况都不能编辑
            op.showtoolbarConfig = {}
            sheet.config.authority = {
              sheet: 1,
              hintText: '禁止编辑',
              allowRangeList: [],
            }

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
}
</script>

<style scoped>
</style>