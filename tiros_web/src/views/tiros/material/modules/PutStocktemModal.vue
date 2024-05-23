<template>
  <div>
    <a-modal
      :dialogStyle="{top:'85px'}"
      :title="title"
      :width="900"
      :visible="visible"
      :confirmLoading="confirmLoading"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭"
      :destroyOnClose="true"
    >
      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-row :gutter="24">
            <a-col :md="12" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资编码">
                <a-input-number
                  style="width: 100%"
                  :min="0"
                  disabled
                  v-decorator.trim="['materialTypeCode', validatorRules.materialTypeCode]"
                />
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资名称">
                <a-input
                  style="width: 100%"
                  :min="0"
                  disabled
                  v-decorator.trim="['materialTypeName', validatorRules.materialTypeName]"
                />
              </a-form-item>
            </a-col>
            <a-col :md="12" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="入库数量">
                <a-input-number
                  style="width: 100%"
                  :min="0"
                  disabled
                  :max="9999999"
                  v-decorator.trim="['confirmAmount', validatorRules.confirmAmount]"
                />
              </a-form-item>
            </a-col>
            <!-- </a-row>
        <a-row :gutter="24"> -->
            <a-col :md="12" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="二级库位">
                <!-- v-model="selfWarehouseId"-->
                <j-tree-select
                  disabled
                  placeholder="请选择存放位置"
                  dict="bu_mtr_warehouse,name,id"
                  pidField="parent_id"
                  v-decorator.trim="['selfWarehouseId', validatorRules.selfWarehouseId]"
                >
                </j-tree-select>
              </a-form-item>
            </a-col>
            <!-- </a-row>
        <a-row :gutter="24"> -->
            <a-col :md="12" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="生产日期">
                <a-date-picker
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  v-decorator.trim="['productionDate', validatorRules.productionDate]"
                  :disabled-date="disabledStartDate"
                  @openChange="handleStartOpenChange"
                />
              </a-form-item>
            </a-col>
            <!-- </a-row>
        <a-row :gutter="24"> -->
            <a-col :md="12" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="失效日期">
                <a-date-picker
                  style="width: 100%"
                  format="YYYY-MM-DD"
                  v-decorator.trim="['expirDate', validatorRules.expirDate]"
                  @change="handleExpirDate"
                  :disabled-date="disabledEndDate"
                  :open="endOpen"
                  @openChange="handleEndOpenChange"
                />
              </a-form-item>
            </a-col>
            <!-- </a-row>
        <a-row :gutter="24"> -->
            <a-col :md="12" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="有效期">
                <a-input-number
                  placeholder="请输入有效期"
                  style="width: 100%"
                  :min="0"
                  :max="99999999"
                  v-decorator.trim="['expirDay']"
                  @change="expirDayChange"
                />
              </a-form-item>
            </a-col>
            <!-- </a-row>
        <a-row :gutter="24"> -->
            <a-col :md="12" :sm="24">
              <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资属性">
                <j-dict-select-tag
                  placeholder="请选择物资属性"
                  dictCode="bu_material_attr"
                  :triggerChange="true"
                  v-decorator.trim="['materialAttr']"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
        <!-- 明细 -->
        <div>
          <a-divider orientation="left"> 入库明细 </a-divider>
          <a-row :gutter="24" style="margin-bottom: 12px">
            <a-col :md="8" :sm="24">
              <a-space>
                <a-button @click="addDetailItem">新增</a-button>
                <a-button @click="delDetailItem">删除</a-button>
              </a-space>
            </a-col>
          </a-row>
          <div style="height: 200px">
            <vxe-table
              ref="listTable"
              height="auto"
              align="center"
              border
              :data="detailList"
              :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
              show-overflow="tooltip"
            >
              <vxe-table-column type="seq" width="80" fixed="left"></vxe-table-column>
              <vxe-table-column
                type="checkbox"
                width="40"
                fixed="left"
              ></vxe-table-column>
              <vxe-table-column field="amount" title="入库数量" width="200">
                <template #default="{ row }">
                  <div @click.stop>
                    <a-input-number
                      style="width: 100%"
                      v-model="row.amount"
                      placeholder="输入入库数量"
                      :min="0"
                    ></a-input-number>
                  </div>
                </template>
              </vxe-table-column>
              <vxe-table-column field="selfWarehouseId" title="移入四级库">
                <template v-slot="{ row, rowIndex }">
                  <div @click.stop>
                    <a-select
                      v-model="row.selfWarehouseName"
                      placeholder="请选择物料"
                      :open="false"
                      style="width: 100%"
                      @dropdownVisibleChange="openWareHouseTree(rowIndex)"
                    >
                      <a-icon slot="suffixIcon" type="ellipsis" />
                    </a-select>
                  </div>
                </template>
              </vxe-table-column>
            </vxe-table>
          </div>
        </div>
      </a-spin>
    </a-modal>
    <WareHouseTree ref="wareHouseTree" @ok="onSelectLocation"></WareHouseTree>
  </div>
</template>

<script>
import moment from "moment";
import "moment/locale/zh-cn";
import { entryPutStock } from "@api/tirosMaterialApi";
import JTreeSelect from "@/components/jeecg/JTreeSelect";
import { everythingIsEmpty } from "@/utils/util";
import WareHouseTree from "@views/tiros/material/putStock/WareHouseTree";

export default {
  name: "PutStockItemModal",
  components: { JTreeSelect, WareHouseTree },
  data() {
    return {
      rowIndex: null,
      title: "操作",
      visible: false,
      buMaterialEntryDetailId: "",
      model: {},
      endOpen: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        materialTypeCode: { rules: [{ required: true }] },
        materialTypeName: { rules: [{ required: true }] },
        // productionDate: { rules: [{ required: true, message: '请选择生产日期!' }] },
        // expirDate: { rules: [{ required: true, message: '请选择失效日期!' }] },
        // expirDay: { rules: [{ required: true, message: '请输入有效期!' }] },
        confirmAmount: { rules: [{ required: true, message: "请输入库数量!" }] },
        selfWarehouseId: { rules: [{ required: true, message: "请选择存放位置!" }] },
      },
      detailList: [],
    };
  },
  created() {},
  methods: {
    moment,
    expirDayChange(data) {
      const productionDate = this.form.getFieldValue("productionDate");
      if (data && productionDate) {
        console.log(productionDate);
        const expirDate = moment(productionDate).add(data - 1, "days");
        this.form.setFieldsValue({
          expirDate: expirDate,
        });
      }
    },
    openWareHouseTree(rowIndex) {
      this.rowIndex = rowIndex;
      const selfWarehouseId = this.form.getFieldValue("selfWarehouseId");
      this.$refs.wareHouseTree.showModal(selfWarehouseId);
    },
    //库位选择
    onSelectLocation(records) {
      console.log(records);
      if (records.length > 0) {
        this.detailList[this.rowIndex].selfWarehouseId = records[0].id;
        this.detailList[this.rowIndex].selfWarehouseName = records[0].name;
      }
    },
    validatirEvent() {
      let result = true;
      let num = 0;
      let subData = JSON.parse(JSON.stringify(this.detailList));
      // 先判断添加明细是否都填写
      if (subData.length > 0) {
        for (let i = 0, len = subData.length; i < len; i++) {
          const item = subData[i];
          if (item.amount <= 0) {
            result = false;
            this.$message.warning(`明细序号 ${i + 1} 的入库数量不能小于1`);
            break;
          } else if (!item.selfWarehouseId) {
            result = false;
            this.$message.warning(`明细序号 ${i + 1} 的移入四级库位不能为空`);
            break;
          } else {
            delete subData[i].selfWarehouseName;
            delete subData[i]._XID;
            num += item.amount;
          }
        }
      } else {
        result = false;
        this.$message.warning("请添加入库明细");
      }
      // 在判断入库明细的数量是否符合入库数量
      const s_num = this.form.getFieldValue("confirmAmount");
      if (result && num != s_num) {
        result = false;
        this.$message.warning(`司机库位的总库存量${num > s_num ? '大' : '小'}于入库数量`);
      } else {
        // 设置提交明细
        this.model.confirmVO = subData;
      }
      return result;
    },
    addDetailItem() {
      this.detailList.push({
        amount: 0,
        selfWarehouseId: "",
        selfWarehouseName: "",
      });
    },
    delDetailItem() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords();
      if (selectRecords.length > 0) {
        selectRecords.forEach((selectItem) => {
          console.log(selectItem);
          for (let i = 0; i < this.detailList.length; i++) {
            const detailItem = this.detailList[i];
            if (selectItem._XID == detailItem._XID) {
              this.detailList.splice(i, 1);
            }
          }
        });
      } else {
        this.$message.error("尚未选中任何数据!");
      }
      // if (this.detailList.length > 0) {
      //   this.detailList.splice(this.detailList.length - 1, 1)
      // }
    },
    edit(record) {
      this.form.resetFields();
      this.visible = true;
      if (typeof record === "object") {
        this.buMaterialEntryDetailId = record.id;
        console.log(record);
        this.model.entryOrderId = record.entryOrderId;
        this.$nextTick(() => {
          this.form.setFieldsValue({
            materialTypeCode: record.materialTypeCode,
            materialTypeName: record.materialTypeName,
            confirmAmount: record.amount, // 入库数量
            selfWarehouseId: record.selfWarehouseId, // 存放位置
            productionDate: record.productionDate, // 生产日期
            expirDate: record.expirDate, // 失效日期
            expirDay: record.expirDay, // 有效期
            // materialAttr:'', // 物资属性
            confirmVO: [], // 入库明细
          });
          if (record.expirDate && record.productionDate) {
            this.handleExpirDate(record.expirDate);
          }
        });
      } else {
        this.buMaterialEntryDetailId = record;
      }
    },
    handleExpirDate(date) {
      let productionDate = this.form.getFieldValue("productionDate");
      if (!productionDate) {
        this.$message.warn("请选择生产日期!");
      } else {
        let s = this.moment(productionDate);
        let e = this.moment(date);
        let day = e.diff(s, "day") + 1;
        this.$nextTick(() => {
          this.form.setFieldsValue({ expirDay: day });
        });
        console.log(e.diff(s, "day"));
      }
    },
    disabledStartDate(startValue) {
      const endValue = this.form.getFieldValue("expirDate");
      if (!startValue || !endValue) {
        return false;
      }
      return startValue.valueOf() > endValue.valueOf();
    },
    disabledEndDate(endValue) {
      const startValue = this.form.getFieldValue("productionDate");
      if (!endValue || !startValue) {
        return false;
      }
      return startValue.valueOf() >= endValue.valueOf();
    },
    handleStartOpenChange(open) {
      if (!open) {
        this.endOpen = true;
      }
    },
    handleEndOpenChange(open) {
      this.endOpen = open;
    },
    // 确定
    handleOk() {
      const that = this;
      that.form.validateFields((err, values) => {
        if (!err) {
          if (!this.validatirEvent()) {
            return false;
          }
          let formData = Object.assign(that.model, values, {
            productionDate: !everythingIsEmpty(values.productionDate)
              ? moment(values.productionDate).format("YYYY-MM-DD")
              : "",
            expirDate: !everythingIsEmpty(values.expirDate)
              ? moment(values.expirDate).format("YYYY-MM-DD")
              : "",
          });
          formData["buMaterialEntryDetailId"] = this.buMaterialEntryDetailId;
          this.$confirm({
            content: "确认入库后不可修改，是否确认？",
            onOk: () => {
              that.confirmLoading = true;
              return entryPutStock(formData)
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
            },
            okText: "确认",
            cancelText: "取消",
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
      this.visible = false;
    },
  },
};
</script>
