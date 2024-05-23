<template>
  <div>
    <table class="table">
      <thead>
        <!-- 标题行 -->
        <tr>
          <td colspan="4" rowspan="2" style="padding: 0">
            <span v-if="!recordSheet.assetTypeId" style="padding-left: 5px">车号：{{ recordSheet.trainNo }}</span>
            <table v-if="recordSheet.assetTypeId && recordSheet.updown === 0" class="innerTable">
              <tr>
                <td class="bottom">车号：{{ recordSheet.trainNo }}</td>
              </tr>
              <tr>
                <td>部件号：{{ recordSheet.manufNo }}</td>
              </tr>
            </table>
            <table v-if="recordSheet.assetTypeId && recordSheet.updown === 1" class="innerTable">
              <tr>
                <td class="bottom">车号：{{ recordSheet.trainNo }}</td>
              </tr>
              <tr>
                <td class="bottom">原部件序列号：{{ recordSheet.manufNo }}</td>
              </tr>
              <tr>
                <td>新部件序列号：{{ recordSheet.manufNoUp }}</td>
              </tr>
            </table>
          </td>
          <td colspan="3" rowspan="2" style="text-align: center; font-weight: bold">
            {{ recordSheet.title || recordSheet.workRecName }}
          </td>
          <td colspan="3">作业日期：{{ recordSheet.workDate }} 完工日期: {{ recordSheet.finishDate }}</td>
        </tr>
        <tr>
          <td colspan="3">作业班组：{{ recordSheet.workGroupName }}</td>
        </tr>
        <!--  数据行 -->
        <tr>
          <th style="width: 60px">序号</th>
          <th style="width: 100px">项目</th>
          <th style="width: 200px">作业内容</th>
          <th style="width: 60px">控制点</th>
          <th style="width: 200px">技术要求</th>
          <th style="width: 200px">作业情况</th>
          <th style="width: 80px">结果</th>
          <th style="width: 60px">自检</th>
          <th style="width: 60px">互检</th>
          <th style="width: 60px">专检</th>
        </tr>
      </thead>
      <tbody v-for="category in recordSheet.categoryList" :key="category.id">
        <tr v-for="(detail, index) in category.detailList" :key="detail.id">
          <td v-if="index === 0" :rowspan="category.detailList.length">
            {{ category.recIndex }}
          </td>
          <td v-if="index === 0" :rowspan="category.detailList.length">
            {{ category.reguTitle }}
          </td>
          <td>
            {{ detail.workContent }}
          </td>
          <td style="text-align: center">
            {{ detail.checkLevel_dictText }}
          </td>
          <td>
            {{ detail.techRequire }}
          </td>
          <td>
            {{ detail.isIgnore == 0 ? detail.workInfo : detail.ignoreDesc }}
          </td>
          <td style="text-align: center">
            <a-checkbox checked v-if="detail.result === 1" disabled></a-checkbox>
            <a-checkbox v-if="detail.result === 0" disabled></a-checkbox>
          </td>
          <td>
            {{ detail.selfCheck }}
          </td>
          <td>
            {{ detail.guarderCheck }}
          </td>
          <td>
            {{ detail.monitorCheck }}
          </td>
        </tr>
      </tbody>
      <!--  工器具行 -->
      <tr v-for="(tool, index) in recordSheet.measureToolList" :key="index">
        <td v-if="index === 0" colspan="2" :rowspan="recordSheet.measureToolList.length">计量器具使用</td>
        <td colspan="2">名称: {{ tool.name }}</td>
        <td>规格型号：{{ tool.model }}</td>
        <td>出厂编号：{{ tool.serialNo }}</td>
        <td colspan="2">上次检验时间：{{ tool.lastCheckTime }}</td>
        <td colspan="2">下次检验时间：{{ tool.nextCheckTime }}</td>
      </tr>
      <tr>
        <td colspan="10" style="height: 80px">情况说明：{{ recordSheet.remark }}</td>
      </tr>
      <tr>
        <td colspan="10" style="height: 80px">
          <span style="margin-left: 5%">检查结果：{{ recordSheet.checkResult === 0 ? '未通过' : '通过' }} </span>
          <span style="margin-left: 30%">检查日期：{{ recordSheet.checkDate }} </span>
          <span style="margin-left: 30%">质量负责人：{{ recordSheet.checkUserName }} </span>
          <div style="padding-top: 15px">
            说明：<br />
            1.
            控制点分为H点和W点，“H”点为该项工序操作过程必须经过专业工程师现场检查确认的工序，“W”点为班组长可在工序完工后确认的工序。<br />
            2.
            “自检”栏为作业者本人进行签字；“互检”栏由作业监护人员进行检查签字；“专检”栏由班组长进行检查签字。“质量负责人”由相关专业工程师进行签字。<br />
            3.
            “作业情况”需根据实际情况填写。若该项工序无异常则由作业者在框中填入√；若该项工序存在异常，则将实际故障情况填入框内。<br />
            4.
            “结果”栏方框在完成该项作业及检查后，由工班长进行填写。若无异常则在方框内填入√，若有异常，工班长需对故障情况进行确认，在方框内填入×，并将故障在“备注”栏进行汇总。<br />
            5. 作业内容中有数据要求的一并填入“作业情况”栏 。
          </div>
        </td>
      </tr>
    </table>
  </div>
</template>

<script>

export default {
  name: 'RecordSheetTable',
  data() {
    return {
      recordSheet: {},
    }
  },
  methods: {
    loadData(table) {
      console.log('table Data:', table)
      
      this.recordSheet = table
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
</style>