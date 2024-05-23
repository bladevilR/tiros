<template>
  <div id="luckySheetPringContent">
    <div class="table-page-body" style="position: relative">
      <div ref="printDom" class="ls-print" :id="luckySheetId" :style="{ height: lHeight }"></div>
    </div>
  </div>
</template>

<script>
import { getFormContent } from '@/api/tirosApi'
import clone from 'clone'
import luckyexcelUtil from '@views/tiros/util/LuckyexcelUtil'

export default {
  name: 'LuckySheetPrint',
  props:{
    luckySheetId:{
      type: String,
      default: 'luckySheetPring'
    }
  },
  data() {
    return {
      recordSheet: {},
      lHeight: 'calc(100% - 0)',
    }
  },
  mounted() {},
  methods: {
    printById(id) {
      getFormContent
        .call(this, id)
        .then((res) => {
          if (res.success) {
            this.printBySheet(JSON.parse(res.result))
          }
        })
        .catch((err) => {
          console.error('加载表单内容异常：', err)
          this.$message.error('加载表单内容异常')
        })
    },
    printByData(records) {
      this.recordSheet = records
      this.$nextTick(() => {
        this.print()
      })
    },
    printBySheet(sheet) {
      const op = Object.assign({}, luckyexcelUtil.getDefaultOptions())
      op.container = this.luckySheetId
      let maxCol = 0
      let maxRow = 0
      let maxRowlen = 0
      sheet.celldata.forEach((e) => {
        if (e.c > maxCol) {
          maxCol = e.c
        }
        if (e.r > maxRow) {
          maxRow = e.r
        }
      })
      maxCol++
      maxRow++
      maxRowlen = sheet.visibledatarow[sheet.visibledatarow.length - 1]
      op.column = maxCol
      op.row = maxRow
      delete sheet.luckysheet_select_save
      op.data = [sheet]
      this.lHeight = `${maxRowlen + 40}px`
      luckysheet.destroy()
      luckysheet.create(op)
      setTimeout(() => {
        let contentDom = document.getElementById('luckysheet-sheettable_0')
        let canvasDom = document.getElementById('luckysheetTableContent')
        const imageData = canvasDom
          .getContext('2d')
          .getImageData(42, 17, contentDom.offsetWidth, contentDom.offsetHeight + 10)
        const clipCanvas = document.createElement('canvas')
        clipCanvas.width = contentDom.offsetWidth
        clipCanvas.height = contentDom.offsetHeight
        const clipContext = clipCanvas.getContext('2d')
        clipContext.putImageData(imageData, 0, 0)
        const itemBase64 = clipCanvas.toDataURL('image/png')
        console.log('base64:', itemBase64)

        this.print(`<img id="printImage" src="${itemBase64}">`)
      }, 500)
    },
    print(html) {
      let iframe = document.getElementById('printDom') || document.createElement("iframe");
      iframe.id = 'printDom'
      document.body.appendChild(iframe)
      this.$nextTick(() => {
        let dom = document.getElementById('printDom')
        // dom.contentDocument.head.innerHTML = JSON.stringify(document.head.innerHTML)
        dom.contentDocument.write(html)
        dom.contentDocument.getElementById('printImage').onload = () => {
          dom.contentWindow.print()
          luckysheet.destroy()
          this.$emit('sheetPrintOk')
          document.body.removeChild(iframe)
        }
      })
    },
  },
}
</script>

<style scoped lang="less">
.innerTable {
  height: 100%;
  width: 100%;
  border-collapse: collapse;
  border: 0;
  td {
    border: 0;
  }
  .bottom {
    border-bottom: 1px solid #2d2a2a;
  }
}

#printIframe {
  display: none;
}

#luckySheetPringContent {
  position: fixed;
  top: 120%;
  width: 100%;
  opacity: 0;
}

.ls-print {
  // height: calc(100% - 0px);
  width: 100%;
}

.printHideCss {
  visibility: hidden !important;
}
</style>