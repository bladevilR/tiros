<template>
  <a-modal
    centered
    :title="title"
    :width="600"
    :bodyStyle="{ height: '300px' }"
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
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="新的测量">
              <a-input-number
                style="width:100%"
                :max="999999999"
                placeholder="请输入"
                v-decorator.trim="['newValue', validatorRules.newValue]"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注描述">
              <a-textarea
                placeholder="请输入"
                :maxLength="201"
                v-decorator.trim="['remark', validatorRules.remark]"
                :auto-size="{ minRows: 3, maxRows: 10 }"
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
import { closeMeasurealert } from "../../../../api/tirosGroupApi";

export default {
  name: "WarningOffModal",
  data() {
    return {
      title: "操作",
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 },
      },
      isClose: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        newValue: { rules: [{ required: true, message: "请输入新的测量内容!" }] },
        remark: { rules: [{ max: 200, message: "不能超过200个字符" }] },
      },
    };
  },

  created() {},
  methods: {
    changeClose(checked) {
      this.isClose = checked;
    },
    edit(record) {
      if (record.id) {
        this.isClose = record["close"] && record["close"] == 1 ? true : false;
      } else {
        this.isClose = false;
      }
      // this.$form.resetFields()
      this.model = Object.assign({}, record);
      this.visible = true;
      this.$nextTick(() => {
        this.form.setFieldsValue({
          newValue: this.model.newValue,
          remark: this.model.remark,
        });
      });
    },

    // 确定
    handleOk() {
      const that = this;
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true;
          let formData = Object.assign(that.model, values);
          console.log(formData);
          let obj;
          obj = closeMeasurealert(formData);
          obj
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message);
                that.$emit("ok");
              } else {
                that.$message.warning(res.message);
              }
            })
            .finally(() => {
              that.confirmLoading = false;
              that.close();
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

<style scoped></style>
