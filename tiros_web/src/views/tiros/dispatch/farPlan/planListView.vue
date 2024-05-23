<template>
  <div>
    <a-card id="farPlanItemContent">
      <div class="info-wrapper info-top-wrapper">
        <h4>基本信息</h4>
        <div class="descriptionBox">
          <a-descriptions bordered :column="3">
            <a-descriptions-item :span="1" label="标题">{{
              formCopy.title
            }}</a-descriptions-item>
            <a-descriptions-item :span="1" label="车辆段">{{
              formCopy.depotName
            }}</a-descriptions-item>
            <a-descriptions-item :span="1" label="车间">{{
              formCopy.workshopName
            }}</a-descriptions-item>
            <a-descriptions-item :span="1" label="开始年份">{{
              formCopy.startYear
            }}</a-descriptions-item>
            <a-descriptions-item :span="1" label="结束年份">{{
              formCopy.endYear
            }}</a-descriptions-item>
            <a-descriptions-item :span="1" label="备注">{{
              formCopy.remark
            }}</a-descriptions-item>
          </a-descriptions>
        </div>
      </div>

      <div class="info-wrapper info-top-wrapper">
        <h4>计划明细信息</h4>
        <vxe-table
          show-overflow="tooltip"
          :print-config="{}"
          border
          align="center"
          :data="tableDatas"
        >
          <vxe-table-column field="title" title="明细标题"></vxe-table-column>
          <vxe-table-column field="lineName" title="线路"></vxe-table-column>
          <vxe-table-column field="year" title="年份"></vxe-table-column>
          <vxe-table-colgroup title="架修">
            <vxe-table-column field="middleRepair_type" title="类型"></vxe-table-column>
            <vxe-table-column field="middleRepair_num" title="数量"></vxe-table-column>
          </vxe-table-colgroup>
          <vxe-table-colgroup title="大修">
            <vxe-table-column field="hightRepair_type" title="类型"></vxe-table-column>
            <vxe-table-column field="hightRepair_num" title="数量"></vxe-table-column>
          </vxe-table-colgroup>
        </vxe-table>
      </div>
    </a-card>
  </div>
</template>

<script>
import moment from "moment";
import { getFarPlanDetail } from "@/api/tirosDispatchApi";
import { everythingIsEmpty } from "@/utils/util";
import LineSelectList from "@views/tiros/common/selectModules/LineSelectList";
export default {
  name: "planListView",
  components: { LineSelectList },
  data() {
    return {
      formCopy: {},
      detailInfoFormCopy: {},
      tableDatas: [],
      disabled: false,
      title: "操作",
      planId: "",
      startYear: null,
      endYear: null,
      endYearPickShow: false,
      basicInfoIsOk: false,
      detailTotal: 0,
      tempDetailList: [],
    };
  },
  props: {
    businessKey: {
      type: String,
      default: null,
    },
    isReadonly: {
      type: Boolean,
      default: true,
    },
    fromFlow: {
      type: Boolean,
      default: false,
    },
  },
  mounted() {
    if (this.businessKey) {
      this.show(this.businessKey);
    }
  },
  methods: {
    moment,
    show(value, disabled = false) {
      this.disabled = disabled;
      this.planId = value;
      if (this.form && this.detailInfoForm) {
        this.detailInfoForm.resetFields();
        this.form.resetFields();
      }
      this.startYear = null;
      this.endYear = null;

      if (!everythingIsEmpty(this.planId)) {
        getFarPlanDetail({ id: this.planId }).then((res) => {
          let detail = res.result
          const item = {
            title: detail.title,
            depotId: detail.depotId,
            startYear: moment(detail.startYear || new Date(), 'YYYY'),
            endYear: moment(detail.endYear || new Date(), 'YYYY'),
            remark: detail.remark
          }
          console.log(item)
          this.$nextTick(() => {
            item.depotName = detail.depotName
            item.workshopName = detail.workshopName
            item.startYear = moment(item.startYear).format('YYYY')
            item.endYear = moment(item.endYear).format('YYYY')
            this.formCopy = item
          })
          this.startYear = parseInt(detail.startYear)
          this.endYear = parseInt(detail.endYear)
          //处理明细列表

          if (detail.detailList.length) {
            if (this.detailInfoForm) {
              this.detailInfoForm.resetFields()
            }
            this.detailTotal = detail.detailList.length
            this.tempDetailList = detail.detailList
            this.basicInfoIsOk = true
            let inputArr = {}
            //计划明细列表遍历

            if (this.detailInfoForm) {
              detail.detailList.forEach((val, index) => {
                let jiaxiuArr = {}
                let daxiuArr = {}
                let otherArr = {}
                let jiaxiuStringArr = val.middleRepair.split('#')
                let daxiuStringArr = val.hightRepair.split('#')
                jiaxiuStringArr.forEach((val) => {
                  let refName = 'ref_' + index + '_0_' + val.split(':')[0]
                  jiaxiuArr[refName] = val.split(':')[1]
                })
                daxiuStringArr.forEach((val) => {
                  let refName = 'ref_' + index + '_1_' + val.split(':')[0]
                  daxiuArr[refName] = val.split(':')[1]
                })
                otherArr['title_' + index] = val.title
                otherArr['line_' + index] = val.lineId
                let detailArr = {}
                Object.assign(detailArr, jiaxiuArr, daxiuArr, otherArr)
                Object.assign(inputArr, detailArr)
              })
              this.$nextTick(() => {
                this.detailInfoForm.setFieldsValue(inputArr)
              })
            } else {
              let _detailList = detail.detailList
              let _table = []
              let _init = (e) => {
                let _item = e.split('#')
                for (let i = 0, len = _item.length; i < len; i++) {
                  _item[i] = _item[i].split(':')
                }
                return _item
              }
              Array.from(_detailList, (item, index) => {
                //将类数组对象或可迭代对象转化为数组。
                item.hightRepair = _init(item.hightRepair)
                item.middleRepair = _init(item.middleRepair)
                console.log(item)
                let _item = {
                  title: item.title,
                  lineName: item.lineName,
                  year: '',
                  hightRepair_type: '大修',
                  hightRepair_num: '',
                  middleRepair_type: '架修',
                  middleRepair_num: ''
                }
                Array.from(item.hightRepair, (itemH, _index) => {
                  //将类数组对象或可迭代对象转化为数组。
                  let itemM = item.middleRepair[_index]
                  console.log(itemH[0], itemM[0])
                  if (itemH[0] == itemM[0]) {
                    _item.year = itemH[0]
                    _item.hightRepair_num = itemH[1]
                    _item.middleRepair_num = itemM[1]
                    _table.push(JSON.parse(JSON.stringify(_item)))
                  }
                })
              })
              this.tableDatas = _table
              this.detailInfoFormCopy = _detailList
            }
          }

          this.$emit('loaded')
        })
      } else {
        this.$emit('loaded')
      }
    },
  },
  computed: {
    tableData() {
      let d = [];
      let jiaxiu = { name: "架修数", id: "0", num: 0 };
      let daxiu = { name: "大修数", id: "1", num: 0 };
      d.push(jiaxiu);
      d.push(daxiu);
      return d;
    },
  },
};
</script>

<style lang="less" scoped>
#farPlanItemContent {
  border: none;

  .ant-card-body {
    height: auto !important;
    overflow-y: auto;
  }

  .info-wrapper {
    border: 1px solid #eee;
    position: relative;
    border-radius: 8px;
    padding: 10px;
    margin-bottom: 20px;
  }

  .info-wrapper .del_btn {
    float: right;
    font-size: 22px;
    color: #f5222d;
    margin-top: 9px;
    margin-right: 5px;
    cursor: pointer;
    z-index: 2;
    position: relative;
  }

  .info-wrapper h4 {
    position: absolute;
    top: -14px;
    padding: 1px 8px;
    margin-left: 16px;
    color: #777;
    border-radius: 2px 2px 0 0;
    background: #fff;
    font-size: 14px;
    width: auto;
  }

  .addDiv {
    margin: 20px 20px 30px;
  }

  .footerDIv {
    padding: 10px;
    background: #fff;
    z-index: 1;
    position: fixed;
    bottom: 0;
    right: 24px;
    text-align: right;
  }

  .detailTable {
    td {
      padding: 5px;
    }

    .ant-form-item {
      margin-bottom: 0 !important;
    }
  }
}
.descriptionBox {
  /deep/.ant-descriptions-item-label {
    width: 10%;
    text-align: right;
  }
  /deep/.ant-descriptions-item-content {
    width: 20%;
  }
}
</style>
