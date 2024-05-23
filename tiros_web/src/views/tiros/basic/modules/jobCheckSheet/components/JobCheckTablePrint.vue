<template>
  <div ref="tablePrint" id="tablePrintMain">
    <table class="table" style="margin: 2px">
      <thead></thead>
      <tr>
        <th class="tg-d1f0" colspan="10">车辆连挂作业过程检查项点</th>
      </tr>
      <tbody>
        <tr>
          <td class="tg-ld8d" colspan="3">
            <span style="font-weight: bold; color: #000">车号：{{ sheetData.trainNo }}</span>
          </td>
          <td class="tg-ld8d" colspan="3">
            <span style="font-weight: bold; color: #000">部件名称：{{ sheetData.assetName }}</span>
          </td>
          <td class="tg-ld8d" colspan="4">作业周期：{{ sheetData.period }}</td>
        </tr>
        <tr>
          <td class="tg-ld8d" colspan="3">
            <span style="font-weight: bold; color: #000">部件编号：{{ sheetData.assetNo }}</span>
          </td>
          <td class="tg-ld8d" colspan="3">
            <span style="font-weight: bold; color: #000">作业位置：{{ sheetData.location }}</span>
          </td>
          <td class="tg-ld8d" colspan="4">作业班组：{{ sheetData.groupName }}</td>
        </tr>
        <tr>
          <td class="tg-87ij" style="width: 6%"><span style="font-weight: bold; color: #000">序号</span></td>
          <td class="tg-87ij" style="width: 12%"><span style="font-weight: bold; color: #000">项点</span></td>
          <td class="tg-87ij" style="width: 18%"><span style="font-weight: bold; color: #000">检查内容</span></td>
          <td class="tg-87ij" style="width: 16%"><span style="font-weight: bold; color: #000">等级</span></td>
          <td class="tg-87ij" style="width: 8%"><span style="font-weight: bold; color: #000">检查情况</span></td>
          <td class="tg-87ij" style="width: 8%"><span style="font-weight: bold; color: #000">结果</span></td>
          <td class="tg-87ij" style="width: 8%"><span style="font-weight: bold; color: #000">作业时间</span></td>
          <td class="tg-87ij" style="width: 8%"><span style="font-weight: bold; color: #000">检查方式</span></td>
          <td class="tg-87ij" style="width: 8%"><span style="font-weight: bold; color: #000">检查人</span></td>
          <td class="tg-87ij" style="width: 8%"><span style="font-weight: bold; color: #000">检查时间</span></td>
        </tr>
        <tr v-for="(item, index) in sheetData.itemList" :key="index">
          <td class="tg-0pky tg-9wq8">{{ item.sortNo }}</td>
          <td class="tg-0pky">{{ item.title }}</td>
          <td class="tg-0pky">{{ item.content }}</td>
          <td class="tg-0pky tg-9wq8">
            <a-rate disabled v-model="item.checkLevel" count="3"  />
          </td>
          <td class="tg-0pky">{{ item.checkDesc }}</td>
          <td class="tg-0pky tg-9wq8">{{ item.checkResult_dictText }}</td>
          <td class="tg-0pky tg-9wq8">{{ item.workTime }}</td>
          <td class="tg-0pky tg-9wq8">{{ item.checkMethod_dictText }}</td>
          <td class="tg-0pky tg-9wq8">{{ item.checkUserName }}</td>
          <td class="tg-0pky tg-9wq8">{{ item.checkTime }}</td>
        </tr>
        <tr>
          <td class="tg-0pky" colspan="10"></td>
        </tr>
        <tr>
          <td class="tg-9wq8" colspan="2">参考工艺文件</td>
          <td class="tg-0pky" colspan="8">
            <p
              style="margin: 0; font-size: 12px; line-height: 18px"
              v-for="(item, index) in sheetData.techLinkList"
              :key="index"
            >
              {{ item.teckBookName }}
            </p>
          </td>
        </tr>
        <tr>
          <td class="tg-0pky" colspan="10"></td>
        </tr>
        <tr>
          <td class="tg-87ij" colspan="7"><span style="font-weight: bold; color: #000">不合格项汇总</span></td>
          <td class="tg-87ij"><span style="font-weight: bold; color: #000">整改情况</span></td>
          <td class="tg-87ij"><span style="font-weight: bold; color: #000">复查时间</span></td>
          <td class="tg-87ij"><span style="font-weight: bold; color: #000">复查人</span></td>
        </tr>
        <tr v-for="(item, index) in sheetData.rectifyList" :key="index">
          <td class="tg-0pky" colspan="7">{{ item.itemInfo }}</td>
          <td class="tg-0pky">{{ item.rectifyDesc }}</td>
          <td class="tg-0pky">{{ item.reviewTime }}</td>
          <td class="tg-0pky">{{ item.reviewUserName }}</td>
        </tr>
        <tr>
          <td class="tg-0pky" colspan="10"></td>
        </tr>
        <tr>
          <td class="tg-9wq8" colspan="2">评定：</td>
          <td class="tg-0pky" colspan="8">
            <div class="qualityBox" v-for="judgeItem in sheetData.judgeList" :key="judgeItem.id">
              <div :style="qualityEvaluation && 'padding-right: 20px;'" class="flex1">
                <span>{{ `评定人：${judgeItem.jdUserName}` }}</span>
                <span>{{ `评定时间：${judgeItem.jdTime}` }}</span>
                <span>{{ `评定内容：${judgeItem.jdContent}` }}</span>
              </div>
              <!-- <div :style="qualityEvaluation && 'padding-right: 20px;'" class="flex1">{{ sheetData.judge }} </div> -->
              <!-- <div v-if="qualityEvaluation">
              <a-button @click="openJudgeModal">质量评定</a-button>
            </div> -->
            </div>
          </td>
        </tr>
        <tr>
          <td class="tg-0pky" colspan="10"></td>
        </tr>
        <tr>
          <td class="tg-9wq8" colspan="2">说明：{{ sheetData.remark }}</td>
          <td class="tg-0pky" colspan="8">
            1、表中只列出关键项点，检查人员在检查时参照工艺文件标准执行。<br />
            2、检查人员必须对当天作业内容进行检查。<br />
            3、表中所有内容全部现场检查时填写完成，并悬挂于部件上，随部件流转。<br />
            4、作业时间由作业班组填写，其余由检查人员填写。“检查情况”一栏详细记录检查情况；“结果”一栏对检查合格的画“√”不合格的画“×”；“检查方式”一栏写明检查手段，如：目视、测量、操作等。<br />
            5、检查人员对不合格项视情节严重情况开具整改单，及时提出整改意见，并督促整改，整改完成后及时进行复查。<br />
            6、由检查人员、质量工程师及专业工程师对整体作业质量做出最终判定。
          </td>
        </tr>
      </tbody>
    </table>
    <!-- <iframe id="printIframe"></iframe> -->
  </div>
</template>

<script>
import { getWorkCheckForm, getWorkcheck } from '@api/tirosQualityApi'

export default {
  name: 'JobCheckTablePrint',
  data() {
    return {
      jdContentText: '',
      form: this.$form.createForm(this),
      validatorRules: {
        jdContent: { rules: [{ required: true, message: '请输入评定内容!' }] },
      },
      judgeQueryParam: {
        id: null,
        checkInstId: '',
        checkInstName: '',
        jdContent: '',
        jdTime: null,
        jdUserId: this.$store.getters.userInfo.id,
        jdUserName: this.$store.getters.userInfo.username,
      },
      visible: false,
      sheetData: {},
    }
  },
  props: {
    data: {
      type: Object,
      default() {
        return {}
      },
    },
    qualityEvaluation: {
      type: Boolean,
      default: false,
    },
    fromData: {
      type: Object,
      default: () => {},
    },
  },
  created() {
    // this.sheetData = Object.assign({}, this.data)
    // this.sheetData.itemList = this.sortArray(this.sheetData.itemList)
  },
  mounted() {
    // console.log(this.fromData)
  },
  methods: {
    getWorkCheckForm() {
      let row = this.fromData
      getWorkCheckForm({
        task2InstId: row.task2InstId,
        formInstId: row.id,
      }).then((res) => {
        this.sheetData = res.result
      })
    },
    printById(id) {
      getWorkcheck({
        id: id,
      }).then((res) => {
        if (res.success) {
          res.result.itemList = this.sortArray(res.result.itemList)
          this.sheetData = res.result
          this.$nextTick(() => {
            this.print()
          })
        }
      })
    },
    printByRow(row) {
      console.log(row)
      row.itemList = this.sortArray(row.itemList)
      this.sheetData = row
      this.$nextTick(() => {
        this.print()
      })
    },
    print() {
      // setTimeout(() => {
      //   let html = this.$refs.tablePrint.innerHTML
      //   const document = window.document
      //   const iframe = window.frames[0]
      //   iframe.document.head.innerHTML = document.head.innerHTML
      //   iframe.document.body.innerHTML = html
      //   iframe.window.print()
      // }, 100)
      let html = this.$refs.tablePrint.innerHTML
      let iframe = document.getElementById('printDom') || document.createElement('iframe')
      iframe.id = 'printDom'
      document.body.appendChild(iframe)
      this.$nextTick(() => {
        let dom = document.getElementById('printDom')
        dom.contentDocument.write(`<style>body{ padding: 2px !important}</style>` + html)
        // var styleList = document.getElementsByTagName('style')
        var print_head = dom.contentDocument.getElementsByTagName('head').item(0)

        var thestyle = document.createElement('style')
        thestyle.innerHTML = printStyle
        print_head.appendChild(thestyle)

        // for (var i = 0, max = styleList.length; i < max; i++) {
        //     var thestyle = document.createElement('style');
        //     thestyle.innerHTML = styleList[i].innerHTML
        //     print_head.appendChild(thestyle);
        // }
        // var linkList = document.getElementsByTagName('link');
        // for (var i = 0, max = linkList.length; i < max; i++) {
        //   if (linkList[i].rel && /.css$/g.test(linkList[i].href)) {
        //     var thestyle = document.createElement('link');
        //     var attrib = linkList[i].attributes;
        //     for (var j = 0, attribmax = attrib.length; j < attribmax; j++) {
        //       thestyle.setAttribute(attrib[j].nodeName, attrib[j].nodeValue);
        //     }
        //     print_head.appendChild(thestyle);
        //   }
        // }
        dom.contentWindow.print()
        // document.body.removeChild(iframe)
      })
    },
    sortArray(array) {
      array.sort(this.compare('sortNo'))
      return array
    },
    compare(property) {
      return function (a, b) {
        return a[property] - b[property]
      }
    },
  },
}

var printStyle = `.table {
  border-collapse: collapse;
  border-spacing: 0;
}
.table td {
  border-color: black;
  border-style: solid;
  border-width: 1px;
  font-family: Arial, sans-serif;
  font-size: 14px;
  overflow: hidden;
  padding: 10px 5px;
  word-break: normal;
}
.table th {
  border-color: black;
  border-style: solid;
  border-width: 1px;
  font-family: Arial, sans-serif;
  font-size: 14px;
  font-weight: normal;
  overflow: hidden;
  padding: 10px 5px;
  word-break: normal;
}
.table .tg-ld8d {
  background-color: #f1f1f1;
  border-color: inherit;
  font-weight: bold;
  text-align: left;
  vertical-align: middle;
}
.table .tg-9wq8 {
  border-color: inherit !important;
  text-align: center !important;
  vertical-align: middle !important;
}
.table .tg-87ij {
  background-color: #f1f1f1;
  border-color: inherit;
  font-weight: bold;
  text-align: center;
  vertical-align: middle;
}
.table .tg-d1f0 {
  background-color: #f1f1f1;
  border-color: inherit;
  font-size: 18px;
  text-align: center;
  vertical-align: top;
}
.table .tg-0pky {
  border-color: inherit;
  text-align: left;
  vertical-align: top;
}
.qualityBox {
  display: -ms-flexbox;
  display: flex;
  -ms-flex-align: end;
      align-items: flex-end;
}
.qualityBox .flex1 {
  -ms-flex: 1;
      flex: 1;
}
.qualityBox .flex1 span {
  margin-right: 15px;
}
.qualityBox [type='button'] {
  margin-right: 15px;
}
.ant-rate {
    box-sizing: border-box;
    color: rgba(0, 0, 0, 0.65);
    font-size: 14px;
    font-variant: tabular-nums;
    line-height: 1.5;
    font-feature-settings: 'tnum';
    display: inline-block;
    margin: 0;
    padding: 0;
    color: #fadb14;
    font-size: 20px;
    line-height: unset;
    list-style: none;
    outline: none;
}

.ant-rate-star:not(:last-child) {
    margin-right: 8px;
}

.ant-rate-star-first {
    position: absolute;
    top: 0;
    left: 0;
    width: 50%;
    height: 100%;
    overflow: hidden;
    opacity: 0;
}

.ant-rate {
    box-sizing: border-box;
    color: rgba(0, 0, 0, 0.65);
    font-size: 14px;
    font-variant: tabular-nums;
    line-height: 1.5;
    font-feature-settings: 'tnum';
    display: inline-block;
    margin: 0;
    padding: 0;
    color: #fadb14;
    font-size: 20px;
    line-height: unset;
    list-style: none;
    outline: none;
}

.ant-rate-star {
    position: relative;
    display: inline-block;
    margin: 0;
    padding: 0;
    color: inherit;
    cursor: pointer;
    transition: all 0.3s;
}

.ant-rate-star-first {
    position: absolute;
    top: 0;
    left: 0;
    width: 50%;
    height: 100%;
    overflow: hidden;
    opacity: 0;
}
.ant-rate-star-first, .ant-rate-star-second {
    color: #e8e8e8;
    transition: all 0.3s;
    -webkit-user-select: none;
    -ms-user-select: none;
    user-select: none;
}

.anticon {
    display: inline-block;
    color: inherit;
    font-style: normal;
    line-height: 0;
    text-align: center;
    text-transform: none;
    vertical-align: -0.125em;
    text-rendering: optimizeLegibility;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
}

.ant-rate-star-first .anticon, .ant-rate-star-second .anticon {
    vertical-align: middle;
}

.ant-rate-star-half .ant-rate-star-first, .ant-rate-star-full .ant-rate-star-second {
    color: inherit;
}

.ant-rate-star-first, .ant-rate-star-second {
    color: #e8e8e8;
    transition: all 0.3s;
    -webkit-user-select: none;
    -ms-user-select: none;
    user-select: none;
}`
</script>

<style scoped lang="less">
.table {
  border-collapse: collapse;
  border-spacing: 0;
}
.table td {
  border-color: black;
  border-style: solid;
  border-width: 1px;
  font-family: Arial, sans-serif;
  font-size: 14px;
  overflow: hidden;
  padding: 10px 5px;
  word-break: normal;
}
.table th {
  border-color: black;
  border-style: solid;
  border-width: 1px;
  font-family: Arial, sans-serif;
  font-size: 14px;
  font-weight: normal;
  overflow: hidden;
  padding: 10px 5px;
  word-break: normal;
}
.table .tg-ld8d {
  background-color: #f1f1f1;
  border-color: inherit;
  font-weight: bold;
  text-align: left;
  vertical-align: middle;
}
.table .tg-9wq8 {
  border-color: inherit !important;
  text-align: center !important;
  vertical-align: middle !important;
}
.table .tg-87ij {
  background-color: #f1f1f1;
  border-color: inherit;
  font-weight: bold;
  text-align: center;
  vertical-align: middle;
}
.table .tg-d1f0 {
  background-color: #f1f1f1;
  border-color: inherit;
  font-size: 18px;
  text-align: center;
  vertical-align: top;
}
.table .tg-0pky {
  border-color: inherit;
  text-align: left;
  vertical-align: top;
}
.qualityBox {
  display: flex;
  align-items: flex-end;
  .flex1 {
    flex: 1;
  }
}
#printIframe {
  display: none;
}

#tablePrintMain {
  position: fixed;
  top: 120%;
  opacity: 0;
}
</style>
