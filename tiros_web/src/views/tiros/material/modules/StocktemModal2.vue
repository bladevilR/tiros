<template>
  <a-modal
    :title="title"
    :width="1000"
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
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资名称">
              <a-input
                disabled
                placeholder="请输入物资名称"
                v-decorator.trim="['materialTypeName']"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="入库批次">
              <a-input
                disabled
                placeholder="请输入入库批次"
                v-decorator.trim="['tradeNo']"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="入库日期">
              <a-date-picker
                style="width: 100%"
                v-decorator.trim="['entryDate', validatorRules.entryDate]"
                placeholder="请选择入库日期"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="出厂日期">
              <a-date-picker
                style="width: 100%"
                v-decorator.trim="['leaveFactory']"
                placeholder="请选择出厂日期"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="生产日期">
              <a-date-picker
                @change="dateChangeEvent($event, 1)"
                style="width: 100%"
                v-decorator.trim="['productionDate']"
                placeholder="请选择生产日期"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="有效期/天">
              <a-input-number
                @change="dateChangeEvent($event, 2)"
                style="width: 100%"
                :min="0"
                :precision="0"
                v-decorator.trim="['expirDay']"
                placeholder="请输入有效期"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="有效日期">
              <a-date-picker
                @change="dateChangeEvent($event, 3)"
                style="width: 100%"
                v-decorator.trim="['expirDate']"
                placeholder="请选择有效日期"
              />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="物资描述">
              <a-textarea
                disabled
                v-decorator.trim="['materialTypeSpec']"
                placeholder="请输入物资描述"
                :auto-size="{ minRows: 3, maxRows: 5 }"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import moment from "moment";
import "moment/locale/zh-cn";
import { editStockTradeAttr, getStockTradeAttr } from "@api/tirosMaterialApi";

export default {
  name: "StockItemModal2",
  data() {
    return {
      title: "操作",
      visible: false,
      materialTypeId: "",
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      labelCol2: {
        xs: { span: 24 },
        sm: { span: 3 },
      },
      wrapperCol2: {
        xs: { span: 24 },
        sm: { span: 20 },
      },
      validatorRules: {
        entryDate: { rules: [{ required: false, message: "请选择入库日期!" }] },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
    };
  },
  created() {},
  methods: {
    moment,
    dateChangeEvent(data, type) {
      const expirDate = this.form.getFieldValue("expirDate");
      const expirDay = this.form.getFieldValue("expirDay");
      const productionDate = this.form.getFieldValue("productionDate");
      if (type == 1 && data) {
        if (expirDate) {
          this.form.setFieldsValue({
            expirDay: moment(expirDate).diff(data, "day"),
          });
        } else if (expirDay) {
          this.form.setFieldsValue({
            expirDate: moment(data).add(expirDay, "days"),
          });
        }
      } else if (type == 2 && data && productionDate) {
        this.form.setFieldsValue({
          expirDate: moment(productionDate).add(data, "days"),
        });
      } else if (type == 3 && data && productionDate) {
        this.form.setFieldsValue({
          expirDay: moment(data).diff(moment(productionDate), "day"),
        });
      }
      console.log(data, type);
    },
    edit(materialTypeId, tradeNo) {
      this.form.resetFields();
      this.visible = true;
      this.materialTypeId = materialTypeId;
      let param = { materialTypeId: materialTypeId, tradeNo: tradeNo };
      getStockTradeAttr(param).then((res) => {
        if (res.success && res.result) {
          this.model = { ...res.result };
          const form = res.result;
          if (form.productionDate) {
            if (!form.expirDay && form.expirDate) {
              // 计算有效天数
              form.expirDay = moment(form.expirDate).diff(
                moment(form.productionDate),
                "day"
              );
            } else if (!form.expirDate && form.expirDay) {
              // 计算有有效日期
              form.expirDate = moment(form.productionDate)
                .add(form.expirDay, "days")
                .format("YYYY-MM-DD");
            }
          }
          this.form.setFieldsValue({
            entryDate: form.entryDate,
            expirDate: form.expirDate,
            expirDay: form.expirDay,
            leaveFactory: form.leaveFactory,
            materialTypeName: form.materialTypeName,
            materialTypeSpec: form.materialTypeSpec,
            productionDate: form.productionDate,
            tradeNo: form.tradeNo,
          });
        }
      });
    },
    // 确定
    handleOk() {
      const that = this;
      that.form.validateFields((err, values) => {
        if (!err) {
          let formData = Object.assign(that.model, values, {
            materialTypeId: this.materialTypeId,
            entryDate: moment(values.entryDate).format("YYYY-MM-DD"),
            expirDate: moment(values.expirDate).format("YYYY-MM-DD"),
            productionDate: moment(values.productionDate).format("YYYY-MM-DD"),
            leaveFactory: moment(values.leaveFactory).format("YYYY-MM-DD"),
          });
          editStockTradeAttr(formData)
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
          this.visible = false;
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
