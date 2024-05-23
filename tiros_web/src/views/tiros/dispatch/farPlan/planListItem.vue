<template>
  <a-card id="farPlanItemContent">
    <div class="info-wrapper info-top-wrapper">
      <h4>基本信息</h4>
      <a-form :form="form">
        <j-form-container :disabled="isReadonly">
          <a-row>
             <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="标题">
                <a-input
                  :disabled="disabled"
                  placeholder="请输入内容"
                  v-decorator.trim="['title', validatorRules.title]"
                />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆段">
                <j-dict-select-tag
                  :disabled="disabled"
                  :triggerChange="true"
                  v-decorator.trim="['depotId', validatorRules.depotId]"
                  placeholder="请选择"
                  dictCode="bu_mtr_depot,name,id"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车间">
                <j-dict-select-tag
                  :disabled="disabled"
                  :triggerChange="true"
                  v-decorator.trim="['workshopId', validatorRules.workshopId]"
                  placeholder="请选择"
                  dictCode="bu_mtr_workshop,name,id"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开始年份">
                <a-date-picker
                  :disabled="disabled"
                  mode="year"
                  placeholder="请选择年份"
                  format="YYYY"
                  style="width: 100%"
                  v-decorator.trim="['startYear', validatorRules.startYear]"
                  :open="startYearPickShow"
                  @panelChange="startPanelChange"
                  @openChange="startOpenChange"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结束年份">
                <a-date-picker
                  :disabled="disabled"
                  mode="year"
                  placeholder="请选择年份"
                  format="YYYY"
                  style="width: 100%"
                  v-decorator.trim="['endYear', validatorRules.endYear]"
                  :open="endYearPickShow"
                  @panelChange="endPanelChange"
                  @openChange="endOpenChange"
                />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row>
            <a-col :span="24">
              <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="备注">
                <a-textarea
                  :disabled="disabled"
                  v-decorator.trim="['remark', validatorRules.remark]"
                ></a-textarea>
              </a-form-item>
            </a-col>
          </a-row>
        </j-form-container>
      </a-form>
    </div>
    <!-- {{basicInfoIsOk}}======={{detailTotal}} -->
    <template v-if="basicInfoIsOk">
      <a-form :form="detailInfoForm" :disabled="isReadonly">
        <div
          class="info-wrapper info-top-wrapper"
          v-for="(val, index) in detailTotal"
          :key="index"
        >
          <h4>计划明细信息</h4>
          <!-- <a-icon class="del_btn" type="close-circle" @click.stop="delSubDetailRow(index)" /> -->
          <!--          <a-row >-->
          <!--            <a @click="deleteDetail(index)" v-if="!fromFlow">删除明细</a>-->
          <!--          </a-row>-->
          <a-row>
            <a-col :span="8">
              <a-form-item
                :labelCol="{ xs: { span: 24 }, sm: { span: 6 } }"
                :wrapperCol="wrapperCol"
                label="明细标题"
              >
                <a-input
                  v-decorator.trim="['title_' + index, validatorRules.detailTitle]"
                  :disabled="isReadonly || disabled"
                ></a-input>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
                <line-select-list
                  v-decorator.trim="['line_' + index, validatorRules.detailLineId]"
                  :disabled="isReadonly || disabled"
                >
                </line-select-list>
              </a-form-item>
            </a-col>
          </a-row>
          <div style="border-bottom: 1px solid #cccccc; margin-bottom: 25px"></div>
          <j-form-container :disabled="isReadonly || disabled">
            <table class="detailTable">
              <tr>
                <th width="80px"></th>
                <th style="text-align: center" v-for="(year, col) in yearArr" :key="col">
                  {{ year }}
                </th>
              </tr>
              <tr>
                <td :colspan="yearArr.length + 1">
                  <hr style="height: 1px; border: none; border-top: 1px solid #dcdfe6" />
                </td>
              </tr>
              <tr>
                <td>架修数</td>
                <td v-for="(year, col) in yearArr" :key="col" style="text-align: center">
                  <a-form-item>
                    <a-input-number
                      :disabled="disabled"
                      :min="0"
                      :max="99999999"
                      v-decorator.trim="[
                        getInputModel(index, year, 0),
                        validatorRules.detailMiddleRepair,
                      ]"
                    />
                  </a-form-item>
                </td>
              </tr>
              <tr>
                <td :colspan="yearArr.length + 1" style="text-align: center">
                  <hr style="height: 1px; border: none; border-top: 1px solid #dcdfe6" />
                </td>
              </tr>
              <tr>
                <td>大修数</td>
                <td v-for="(year, col) in yearArr" :key="col" style="text-align: center">
                  <a-form-item>
                    <a-input-number
                      :disabled="disabled"
                      :min="0"
                      :max="99999999"
                      v-decorator.trim="[
                        getInputModel(index, year, 1),
                        validatorRules.detailHighRepair,
                      ]"
                    />
                  </a-form-item>
                </td>
              </tr>
            </table>
          </j-form-container>
        </div>
      </a-form>
    </template>

    <div class="addDiv">
      <a-space>
        <a @click.stop="addDetail" v-if="!fromFlow && !isReadonly">添加明细</a>
        <a @click.stop="subDetail" v-if="!fromFlow && !isReadonly">减少明细</a>
      </a-space>
    </div>
    <!--    <div class="footerDIv">
          <a-button type="primary" @click="save">保存</a-button>
          <a-button style="margin-left: 8px" @click="handleBack">返回</a-button>
        </div>-->
  </a-card>
</template>

<script>
import moment from "moment";
import { addFarPlan, editFarPlan, getFarPlanDetail } from "@/api/tirosDispatchApi";
import { everythingIsEmpty } from "@/utils/util";
import LineSelectList from "@views/tiros/common/selectModules/LineSelectList";
export default {
  name: "planListItem",
  components: { LineSelectList },
  data() {
    return {
      form: this.$form.createForm(this, { onValuesChange: this.changeField }),
      detailInfoForm: this.$form.createForm(this, { name: "detailInfoForm" }),
      disabled: false,
      title: "操作",
      confirmLoading: false,
      planId: "",
      model: {},
      startYear: null,
      endYear: null,
      startYearPickShow: false,
      endYearPickShow: false,
      basicInfoIsOk: false,
      detailTotal: 0,
      tempDetailList: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 4 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 2 },
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 20 },
      },
      validatorRules: {
        title: {
          rules: [
            { required: true, message: "请输入标题!" },
            { max: 32, message: "输入长度不能超过32字符!" },
          ],
        },
        depotId: { rules: [{ required: true, message: "请选择车辆段!" }] },
        workshopId: { rules: [{ required: true, message: "请选择车间!" }] },
        startYear: { rules: [{ required: true, message: "请选择开始年份!" }] },
        endYear: { rules: [{ required: true, message: "请选择结束年份!" }] },
        remark: { rules: [{ max: 255, message: "输入长度不能超过255字符!" }] },
        detailTitle: {
          rules: [
            { required: true, message: "请输入明细标题!" },
            { max: 32, message: "输入长度不能超过32字符!" },
          ],
        },
        detailLineId: { rules: [{ required: true, message: "请选择线路!" }] },
        detailMiddleRepair: { rules: [{ required: true, message: "请输入架修数量!" }] },
        detailHighRepair: { rules: [{ required: true, message: "请输入大修数量!" }] },
      },
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
            workshopId: detail.workshopId,
            startYear: moment(detail.startYear || new Date(), 'YYYY'),
            endYear: moment(detail.endYear || new Date(), 'YYYY'),
            remark: detail.remark
          }
          console.log(item)
          this.$nextTick(() => {
            this.form.setFieldsValue(item)
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
    save(opts) {
      this.form.validateFields((err, values) => {
        if (err) {
          return;
        }
        let arr = [];
        // 明细数量
        for (let index = 0; index < this.detailTotal; index++) {
          let tempArrItem = {
            title: this.detailInfoForm.getFieldValue("title_" + index),
            lineId: this.detailInfoForm.getFieldValue("line_" + index),
          };
          //0架修1大修
          for (let i = 0; i <= 1; i++) {
            let tempTypeString = "";
            if (i == "0") {
              //年份
              for (let year of this.yearArr) {
                let refName = "ref_" + index + "_" + i + "_" + year;
                if (
                  this.detailInfoForm.getFieldValue(refName) >= 0 &&
                  this.detailInfoForm.getFieldValue("title_" + index) &&
                  this.detailInfoForm.getFieldValue("line_" + index)
                ) {
                  tempTypeString =
                    tempTypeString +
                    year +
                    ":" +
                    this.detailInfoForm.getFieldValue(refName) +
                    "#";
                } else {
                  this.$message.warning("请填写完整计划明细信息");
                  return false;
                }
              }
              tempArrItem.middleRepair = tempTypeString.slice(
                0,
                tempTypeString.length - 1
              );
            } else if (i == "1") {
              for (let year of this.yearArr) {
                let refName = "ref_" + index + "_" + i + "_" + year;
                if (this.detailInfoForm.getFieldValue(refName) >= 0) {
                  tempTypeString =
                    tempTypeString +
                    year +
                    ":" +
                    this.detailInfoForm.getFieldValue(refName) +
                    "#";
                } else {
                  this.$message.warning("请填写完整计划明细信息");
                  return false;
                }
              }
              tempArrItem.hightRepair = tempTypeString.slice(
                0,
                tempTypeString.length - 1
              );
            }
          }
          arr.push(tempArrItem);
          // this.tempDetailList.push(tempArrItem)
          console.log(arr);
        }
        let formData = {};
        Object.assign(formData, values, {
          endYear: moment(values.endYear).format("YYYY"),
          startYear: moment(values.startYear).format("YYYY"),
          detailList: arr,
          // detailList: this.tempDetailList
        });
        if (!everythingIsEmpty(this.planId)) {
          Object.assign(formData, { id: this.planId });
          editFarPlan(formData).then((res) => {
            if (res.success) {
              this.close();
              if(this.fromFlow) {
                opts.res = res
                this.$emit('ok', opts)
              } else {
                this.$emit('ok')
              }
            } else {
              // this.$message.warning('保存失败')
              if(this.fromFlow) {
                this.$emit('fail', opts)
              } else {
                this.$emit('fail')
              }
              console.error("保存远期规划失败：", res.message);
            }
          });
        } else {
          addFarPlan(formData).then((res) => {
            if (res.success) {
              this.close();
              if(this.fromFlow) {
                opts.res = res
                this.$emit('ok', opts)
              } else {
                this.$emit('ok')
              }
            } else {
              opts.res = res
              if(this.fromFlow) {
                this.$emit('fail', opts)
              } else {
                this.$emit('fail')
              }
              console.error("保存远期规划失败：", res.message);
            }
          });
        }
      });
    },
    getInputModel(index, year, row) {
      // 第几条明细_架修/大修_年份，0代表架修，1代表大修
      // this.detailInfoForm.getFieldValue(refName)
      let name = "ref_" + index + "_" + row + "_" + year;

      return name;
    },
    addDetail() {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.basicInfoIsOk = true;
          this.detailTotal++;
        } else {
          this.basicInfoIsOk = false;
        }
      });
    },
    subDetail() {
      if (this.detailTotal > 0) {
        this.detailTotal--;
      }
    },
    delSubDetailRow(index) {
      console.log(this.detailTotal);
      // this.detailTotal.splice(index, 1)
    },
    // deleteDetail(index) {
    //   console.log('要删除的下标index = '+ index)
    //   this.tempDetailList.splice(index, 1)
    //   this.detailTotal--
    // },
    //表单域发生变化
    changeField(props, values) {
      let key = Object.keys(values)[0];
      if (key == "startYear" || key == "endYear") {
        this.detailTotal = 0;
        this[key] = parseInt(moment(values[key]).format("YYYY"));
      }
    },
    // 弹出日历和关闭日历的回调
    startOpenChange(status) {
      this.startYearPickShow = status;
    },
    // 得到年份选择器的值
    startPanelChange(value) {
      this.form.setFieldsValue({
        startYear: value,
      });
      this.startYearPickShow = false;
    },
    endOpenChange(status) {
      this.endYearPickShow = status;
    },
    endPanelChange(value) {
      this.form.setFieldsValue({
        endYear: value,
      });
      this.endYearPickShow = false;
    },

    close() {
      /*this.$emit('close')
      this.visible = false
      this.basicInfoIsOk = false*/
    },
  },
  computed: {
    yearArr() {
      let arr = [];
      if (this.startYear && this.startYear && this.endYear >= this.startYear) {
        for (let i = this.startYear; i <= this.endYear; i++) {
          arr.push(i);
        }
      }
      return arr;
    },
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

<style lang="less" scope>
#farPlanItemContent {
  border: none;

  .ant-card-body {
    height: calc(80vh - 150px);
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
</style>
