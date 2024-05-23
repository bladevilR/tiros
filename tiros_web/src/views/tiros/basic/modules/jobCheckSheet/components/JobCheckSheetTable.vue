<template>
  <table class="table">
    <thead>
      <tr>
        <th class="tg-d1f0" colspan="10">{{ sheetData.title }}</th>
      </tr>
    </thead>
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
        <td class="tg-87ij" style="width: 8%"><span style="font-weight: bold; color: #000">等级</span></td>
        <td class="tg-87ij" style="width: 8%"><span style="font-weight: bold; color: #000">检查情况</span></td>
        <td class="tg-87ij" style="width: 8%"><span style="font-weight: bold; color: #000">结果</span></td>
        <td class="tg-87ij" style="width: 10%"><span style="font-weight: bold; color: #000">作业时间</span></td>
        <td class="tg-87ij" style="width: 10%"><span style="font-weight: bold; color: #000">检查方式</span></td>
        <td class="tg-87ij" style="width: 10%"><span style="font-weight: bold; color: #000">检查人</span></td>
        <td class="tg-87ij" style="width: 10%"><span style="font-weight: bold; color: #000">检查时间</span></td>
      </tr>
      <tr v-for="(item, index) in sheetData.itemList" :key="index">
        <td class="tg-0pky tg-9wq8">{{ item.sortNo }}</td>
        <td class="tg-0pky">{{ item.title }}</td>
        <td class="tg-0pky">{{ item.content }}</td>
        <td class="tg-0pky tg-9wq8">
          <a-rate disabled v-model="item.checkLevel" :count="3" />
        </td>
        <td class="tg-0pky">{{ item.checkDesc }}</td>
        <td class="tg-0pky tg-9wq8">{{ item.checkUserName ? item.checkResult_dictText : '' }}</td>
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
            <div v-if="qualityEvaluation && judgeItem.jdUserId === $store.getters.userInfo.id">
              <a-button @click="openJudgeModal" v-has="'jobchekc:evaluation:add'" type="link">质量评定</a-button>
              <a-button @click="delJudge(judgeItem.id)" v-has="'jobchekc:evaluation:del'" type="link">删除</a-button>
            </div>
          </div>
          <div class="qualityBox" v-if="!sheetData.judgeList || sheetData.judgeList.length === 0 || !isJudge">
            <div :style="qualityEvaluation && 'padding-right: 20px;'" class="flex1"></div>
            <div>
              <a-button @click="openJudgeModal" v-if="fromData" v-has="'jobchekc:evaluation:add'" type="link">质量评定</a-button>
            </div>
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
    <a-modal
      title="作业质量评定"
      :width="800"
      :visible="visible"
      centered
      @ok="handleOk"
      @cancel="visible = false"
      cancelText="关闭"
      :destroyOnClose="true"
    >
      <a-form :form="form" ref="initForm">
        <a-form-item>
          <a-textarea
            v-decorator="['jdContent', validatorRules.jdContent]"
            :auto-size="{ minRows: 8, maxRows: 8 }"
          ></a-textarea>
        </a-form-item>
      </a-form>
    </a-modal>
  </table>
</template>

<script>
import { getWorkCheckForm, setJudgeRecord, delJudgeRecord } from '@api/tirosQualityApi'
export default {
  name: 'JobCheckSheetTable',
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
    this.sheetData = Object.assign({}, this.data)
    this.sheetData.itemList = this.sortArray(this.sheetData.itemList)
  },
  mounted() {
    // console.log(this.fromData)
  },
  computed: {
    isJudge(){
      if (!this.sheetData || !this.sheetData.judgeList || this.sheetData.judgeList.length < 1) {
        return false
      }
      return !!this.sheetData.judgeList.find((e) => e.jdUserId === this.$store.getters.userInfo.id)
    }
  },
  methods: {
    async getWorkCheckForm() {
      let row = this.fromData
      return getWorkCheckForm({
        task2InstId: row.task2InstId,
        formInstId: row.id,
      }).then((res) => {
        if (res.success && res.result) {
          res.result.itemList = this.sortArray(res.result.itemList)
          this.sheetData = res.result
        }
        return this.sheetData
      })

      // this.$refs.jobCheckTableView.show(row)
    },
    openJudgeModal() {
      let row = this.fromData
      // console.log(row.judgeList)
      this.judgeQueryParam.checkInstId = row.id
      this.judgeQueryParam.checkInstName = row.name
      this.visible = true
      let record = row.judgeList.find((e) => e.jdUserId === this.$store.getters.userInfo.id)
      if (record) {
        this.judgeQueryParam.jdContent = record.jdContent
        this.judgeQueryParam.id = record.id
        this.form.getFieldDecorator('jdContent')
        this.$nextTick(() => {
          this.form.setFieldsValue({
            jdContent: this.jdContentText || this.judgeQueryParam.jdContent,
          })
        })
      } else {
        this.judgeQueryParam.jdContent = ''
        this.judgeQueryParam.id = null
        this.form.getFieldDecorator('jdContent')
        this.$nextTick(() => {
          this.form.setFieldsValue({
            jdContent: '',
          })
        })
      }
    },
    handleOk() {
      // setJudgeRecord
      this.form.validateFields((err, values) => {
        if (!err) {
          Object.assign(this.judgeQueryParam, values)
          setJudgeRecord(this.judgeQueryParam).then((res) => {
            if (res.success) {
              this.jdContentText = values.jdContent
              this.$message.success('保存成功')
              this.$emit('brush')
              this.getWorkCheckForm().then((res) => {
                this.$emit('judgeChange', this.sheetData)
              })
            } else {
              this.$message.error(res.message)
            }
          })
          this.visible = false
        }
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
    delJudge(id) {
      this.$confirm({
        content: `是否删除作业质量评定？`,
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          let formData = new FormData()
          formData.append('ids', id)
          delJudgeRecord(formData).then((res) => {
            if (res.success) {
              this.$message.success('执行成功')
              this.$emit('brush')
              this.getWorkCheckForm().then((res) => {
                this.$emit('judgeChange', this.sheetData)
              })
            } else {
              this.$message.error(res.message)
            }
          })
        },
      })
    },
  },
}
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

    span {
      margin-right: 15px;
    }
  }
  [type='button'] {
    margin-right: 15px;
  }
}
</style>