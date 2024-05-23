<template>
  <div>
    <a-modal
      :title="title"
      :width="'80%'"
      :visible="visible"
      :confirmLoading="confirmLoading"
      :bodyStyle="{ minHeight: '450px' }"
      @ok="handleOk"
      @cancel="handleCancel"
      :destroyOnClose="true"
      cancelText="关闭"
    >
      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-row :gutter="24">
            <a-col :md="9" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属工单">
                <div @click="openOrderModel">
                  <a-input
                    placeholder="请选择工单"
                    v-decorator.trim="['workOrderName', validatorRules.workOrderName]"
                    ref="myOrderSelect"
                  >
                    <a-icon slot="suffix" type="ellipsis" />
                  </a-input>
                </div>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="退料日期">
                <a-date-picker
                  style="width: 100%"
                  v-decorator.trim="['billDate', validatorRules.billDate]"
                />
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="办理人员">
                <a-select
                  v-decorator="['handleUserName', validatorRules.handleUserName]"
                  placeholder="请选择办理人员"
                  :open="false"
                  :showArrow="true"
                  @focus="openUserSelectModal"
                  ref="myUserSelect"
                >
                  <a-icon slot="suffixIcon" type="ellipsis" />
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24">
            <a-col :md="24" :sm="24">
              <a-form-item
                class="fixedWidth"
                :labelCol="labelCol1"
                :wrapperCol="wrapperCol1"
                label="备注"
              >
                <a-textarea
                  placeholder="请输入备注"
                  :maxLength="201"
                  :auto-size="{ minRows: 3, maxRows: 5 }"
                  v-decorator.trim="['remark', validatorRules.remark]"
                />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row>
            <a-form-item>
              <a-col>
                <a-space>
                  <a-button
                    :disabled="!workOrderId"
                    type="primary"
                    @click="returnVisible = true"
                    >新增</a-button
                  >
                </a-space>
              </a-col>
            </a-form-item>
          </a-row>
          <a-row :gutter="24" style="height: calc(100vh - 450px); padding: 0 12px">
            <vxe-table height="auto" border :data="detailList" show-overflow="tooltip">
              <vxe-table-column
                field="materialTypeCode"
                title="物资编码"
              ></vxe-table-column>
              <vxe-table-column
                field="materialTypeSpec"
                width="320"
                header-align="center"
                align="left"
                title="物资描述"
              ></vxe-table-column>
              <vxe-table-column
                field="applyAmount"
                width="120"
                title="领用数量"
              ></vxe-table-column>
              <vxe-table-column field="returnAmount" title="退回数量">
                <template #default="{ row }">
                  <a-input-number
                    style="width: 100%"
                    v-model="row.returnAmount"
                    placeholder="输入退回数量"
                    :min="1"
                    :max="row.applyAmount"
                  ></a-input-number>
                </template>
              </vxe-table-column>
              <vxe-table-column field="sourceLocationName" title="退回库位">
                <template #default="{ row }">
                  <a-input
                    ref="locationSelect"
                    placeholder="选择库位"
                    v-model="row.sourceLocationName"
                    style="width: 100%"
                    @click="openlocationModal(row)"
                  >
                    <a-icon slot="suffix" type="ellipsis" />
                  </a-input>
                </template>
              </vxe-table-column>
            </vxe-table>
          </a-row>
        </a-form>
      </a-spin>
      <!-- 7 已领料  3 已提交  4 已关闭  -->
      <work-order-select
        :workGroupId="curDepartId"
        :order-status="[7, 3, 4]"
        :select-status="[7, 3, 4]"
        ref="workOrderSelect"
        @ok="onSelectOrder"
      ></work-order-select>
      <user-list
        ref="userSelectModal"
        :dep-id="dispatchGroupId"
        :multiple="false"
        @ok="userSelect"
      ></user-list>
      <LocationList ref="locationModal" @ok="onSelectLocation"></LocationList>
    </a-modal>
    <!-- 选择物料订单 -->
    <a-modal
      title="选择退料物资"
      :width="'80%'"
      :visible="returnVisible"
      :bodyStyle="{ minHeight: '450px' }"
      @ok="returnAddOk"
      :destroyOnClose="true"
      @cancel="returnVisible = false"
    >
      <a-row :gutter="24" style="height: calc(100vh - 250px); padding: 0 12px">
        <vxe-table
          :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          ref="tableList"
          height="auto"
          border
          :data="selectData"
          show-overflow="tooltip"
        >
          <vxe-table-column
            align="center"
            type="checkbox"
            width="40"
            fixed="left"
          ></vxe-table-column>
          <vxe-table-column field="materialTypeCode" title="物资编码"></vxe-table-column>
          <vxe-table-column
            field="materialTypeSpec"
            width="320"
            header-align="center"
            align="left"
            title="物资描述"
          ></vxe-table-column>
          <vxe-table-column
            field="applyAmount"
            width="120"
            title="领用数量"
          ></vxe-table-column>
        </vxe-table>
      </a-row>
    </a-modal>
  </div>
</template>

<script>
import { everythingIsEmpty } from "@/utils/util";

import LineSelectList from "@views/tiros/common/selectModules/LineSelectList";
import {
  saverReturnedItem,
  getReturnedItem,
  getDetailListByOrderId,
} from "@/api/tirosMaterialApi";
import JTreeSelect from "@/components/jeecg/JTreeSelect";
import moment from "moment";
import JSelectDepart from "@/components/jeecgbiz/JSelectDepart";
import UserList from "@views/tiros/common/selectModules/UserList";
import LocationList from "@views/tiros/common/selectModules/LocationList";
import WorkOrderSelect from "@views/tiros/common/selectModules/WorkOrderSelect";

export default {
  name: "WillChangeItemModal",
  components: {
    JTreeSelect,
    UserList,
    JSelectDepart,
    LineSelectList,
    LocationList,
    WorkOrderSelect,
  },
  data() {
    return {
      selectData: [],
      title: "操作",
      visible: false,
      returnVisible: false,
      model: {},
      curDepartId: "",
      workOrderId: "",
      handleUserId: "",
      dispatchGroupId: "",
      userList: [],
      detailList: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 3 },
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 20 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      deptId: "",
      validatorRules: {
        workOrderName: { rules: [{ required: true, message: "请选择工单!" }] },
        billDate: { rules: [{ required: true, message: "请选择退料日期!" }] },
        handleUserName: { rules: [{ required: false, message: "请选择办理人员!" }] },
        remark: {
          rules: [{ required: false, max: 255, message: "输入长度不能超过255个字符!" }],
        },
      },
    };
  },
  computed: {
    billName() {
      return `${this.form.getFieldValue("workOrderName")}【退料】`;
    },
  },
  created() {},
  methods: {
    returnAddOk() {
      const records = this.$refs.tableList.getCheckboxRecords();
      console.log(records);
      records.map((item) => {
        if (
          this.detailList.find((item2) => item2.materialTypeId === item.materialTypeId)
        ) {
          this.$message.warning(`物资${item.materialTypeCode}列表中已存在`);
        } else {
          // this.$set(this.selectRow, "locationWbs", records[0].locationWbs);
          // this.$set(this.selectRow, "locationName", records[0].locationName);
          // this.$set(this.selectRow, "sourceLocationName", records[0].sourceLocationName);
          let s_item;
          try {
            s_item = {
              ...item,
              returnAmount: 1,
              tradeNo: item.assignDetailList[0].tradeNo,
              locationWbs: item.assignDetailList[0].locationWbs || item.assignDetailList[0].warehouseWbs,
              locationName: item.assignDetailList[0].locationName || item.assignDetailList[0].warehouseName,
              sourceLocationName: item.assignDetailList[0].sourceLocationName,
            };
          } catch (error) {
            console.log(error);
            s_item = { ...item, returnAmount: 1 };
          }
          delete s_item.assignDetailList;
          console.log(s_item,11111111);
          this.detailList.push(s_item);
        }
      });
      this.returnVisible = false;
    },
    openOrderModel() {
      this.$refs.workOrderSelect.showModal();
    },
    onSelectOrder(data) {
      //  选择工单
      this.form.setFieldsValue({
        workOrderName: data[0].orderName,
      });
      this.workOrderId = data[0].id;
      console.log(this.workOrderId);
      if (data) {
        // 获取工单对应的已领料的物资
        getDetailListByOrderId({
          orderId: this.workOrderId,
          status: [2, 3],
        }).then((res) => {
          this.selectData = [];
          this.detailList = [];
          if (res.success && res.result.length) {
            console.log(res.result);
            res.result.forEach((item) => {
              this.selectData.push({
                assignDetailList: item.assignDetailList,
                materialTypeId: item.materialTypeId,
                returnAmount: "",
                locationWbs: "",
                locationName: "",
                sourceLocationName: "",
                materialTypeCode: item.materialTypeCode,
                materialTypeSpec: item.materialTypeSpec,
                orderMaterialId: item.id,
                applyAmount: item.amount,
              });
            });
          }
        });
      } else {
        this.selectData = [];
        this.detailList = [];
      }
      this.$refs.myOrderSelect.blur();
    },
    openUserSelectModal() {
      this.$refs.userSelectModal.showModal();
      this.$refs.myUserSelect.blur();
    },
    userSelect(data) {
      if (!everythingIsEmpty(data)) {
        this.handleUserId = data[0].id;
        this.form.setFieldsValue({
          handleUserName: data[0].realname,
        });
      }
    },
    // 打开库位选择
    openlocationModal(row) {
      this.selectRow = row;
      this.$refs.locationModal.showModal(this.selectRow.materialTypeId, null, null);
    },
    //库位选择
    onSelectLocation(records) {
      console.log(records);
      if (records.length == 1) {
        this.$set(this.selectRow, "locationWbs", records[0].warehouseWbs);
        this.$set(this.selectRow, "locationName", records[0].warehouseName);
        this.$set(this.selectRow, "sourceLocationName", records[0].sourceLocationName);
      }
    },
    add() {
      this.visible = true;
      this.$nextTick(() => {
        this.form.setFieldsValue({
          billDate: moment().format("YYYY-MM-DD"),
        });
      });
      this.edit({});
    },
    edit(record) {
      this.visible = true;
      this.form.resetFields();
      this.model = Object.assign({}, record);
      this.detailList = [];
      if (record.id) {
        this.$nextTick(() => {
          this.form.setFieldsValue({
            workOrderName: this.model.workOrderName,
            billCode: this.model.billCode,
            billDate: this.model.billDate,
            handleUserName: this.model.handleUserName,
            remark: this.model.remark,
            id: this.model.id,
          });
        });
        this.handleUserId = this.model.handleUserId;
        this.workOrderId = this.model.workOrderId;

        getReturnedItem({
          id: record.id,
        }).then((res) => {
          if (res.success) {
            if (res.result.detailList.length) {
              this.detailList = res.result.detailList;
            }
          }
        });
      }
    },
    // 确定
    handleOk() {
      const that = this;
      that.form.validateFields((err, values) => {
        if (!err) {
          // 验证数据填写是否完整；
          const dataList = this.detailList;
          console.log(dataList);
          let isOk = true;
          for (let i = 0, len = dataList.length; i < len; i++) {
            const item = dataList[i];
            if (!item.returnAmount) {
              this.$message.warning(`物资${item.materialTypeCode}的退回数量不能小于1`);
              isOk = false;
              break;
            } else if (
              !item.locationWbs ||
              !item.locationName ||
              !item.sourceLocationName
            ) {
              console.log(item)
              this.$message.warning(`物资${item.materialTypeCode}的退回库位不能为空`);
              isOk = false;
              break;
            } else if (!item.tradeNo) {
              console.log(item)
              this.$message.warning(`物资${item.materialTypeCode}的退回批次不能为空`);
              isOk = false;
              break;
            }
          }
          // 根据返回值进行判断
          if (dataList.length == 0) {
            this.$message.warning("请添加退回物料");
            return;
          } else if (!isOk) {
            return;
          }

          that.confirmLoading = true;
          let formData = Object.assign(that.model, values, {
            handleUserId: this.handleUserId,
            detailList: dataList,
            billName: this.billName,
            workOrderId: this.workOrderId,
          });
          let obj;
          if (!that.model.id) {
            obj = saverReturnedItem(formData);
          } else {
            obj = saverReturnedItem(formData);
          }
          obj
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message);
                that.$emit("ok");
                that.close();
              } else {
                that.$message.warning(res.message);
              }
            })
            .finally(() => {
              that.confirmLoading = false;
            });
        }
      });
    },
    // 关闭
    handleCancel() {
      this.close();
    },
    close() {
      this.$emit("close");
      this.detailList = [];
      this.workOrderName = "";
      this.workOrderId = "";
      this.visible = false;
    },
  },
};
</script>

<style lang="less">
.fixedWidth {
  .ant-col-sm-3 {
    width: 12% !important;
  }
}
</style>
