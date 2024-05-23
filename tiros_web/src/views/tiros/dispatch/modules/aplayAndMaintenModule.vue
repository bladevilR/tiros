<template>
  <div>
    <a-modal
      :title="title"
      :visible="visible"
      :confirm-loading="confirmLoading"
      @ok="handleOk"
      @cancel="close"
    >
      <a-form :form="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 17 }">
        <a-form-item :label="`${type == 1 ?'运用' : '保养'}设备`">
          <a-input
            placeholder="请选择"
            v-decorator="[
              'deviceName',
              { rules: [{ required: true, message: '请选择特种设备' }] },
            ]"
            ref="diviceSelect"
            @focus="diviceFocus"
          >
            <a-icon type="ellipsis" slot="suffix" />
          </a-input>
        </a-form-item>
        <a-form-item label="开始日期">
          <a-date-picker
            allowClear
            style="width: 100%"
            v-decorator="[
              'startTime',
              { rules: [{ required: true, message: '请选择开始时间' }] },
            ]"
            show-time
            @change="dateChange($event, 1)"
          />
        </a-form-item>
        <a-form-item label="结束日期">
          <a-date-picker
            allowClear
            style="width: 100%"
            v-decorator="[
              'endTime',
              { rules: [{ required: true, message: '请选择结束时间' }] },
            ]"
            show-time
            @change="dateChange($event, 2)"
          />
        </a-form-item>
        <a-form-item label="备注说明">
          <a-textarea
            allowClear
            :maxLength="201"
            style="width: 100%"
            v-decorator="['remark',{ rules: [{ max: 200, message: '不能超过200个字符' }] }]"
            placeholder="请输入备注说明"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-modal>
    <specialDeviceSelect @ok="selectChange" :multiple="false" ref="specialDeviceSelect" />
  </div>
</template>

<script>
import {addSpecassetPlan,editSpecassetPlan} from "@/api/tirosDispatchApi";
import specialDeviceSelect from "@views/tiros/common/selectModules/specialDeviceSelect";
import moment from "moment";
export default {
  name: "aplayAndMaintenModule",
  props:{
    type:Number, // 1 = 运用设备   2 = 保养设备
  },
  components: { specialDeviceSelect },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      title: "",
      form: {},
      model: {},
    };
  },
  methods: {
    show(record) {
      this.form = this.$form.createForm(this);
      this.visible = true;
      if (typeof record === "number") {
        // 新增
        this.title = "新增";
        this.model.planType = record;
      } else {
        // 编辑
        this.title = "编辑";
        this.$nextTick(()=>{
          this.form.setFieldsValue({
            deviceName:record.name,
            startTime:record.startTime && moment(record.startTime),
            endTime:record.endTime && moment(record.endTime),
            remark:record.remark
          })
        })
        this.model.id = record.id;
        this.model.assetCode = record.assetCode;
        this.model.specAssetId = record.specAssetId;
      }
    },
    selectChange(data) {
      if (data.length > 0) {
        this.form.setFieldsValue({
          deviceName: data[0].name,
        });
        this.model.assetCode = data[0].assetCode;
        this.model.specAssetId = data[0].id;
      }
    },
    diviceFocus() {
      this.$refs.specialDeviceSelect.showModal();
      this.$refs.diviceSelect.blur();
    },
    dateChange(e, type) {
      if (!e) {
        return;
      }
      let start = type == 1 ? e : this.form.getFieldValue("startTime");
      if(start){
        start = moment(moment(start).format("YYYY-MM-DD HH:mm:ss"));
      }
      let end = type == 2 ? e : this.form.getFieldValue("endTime");
      if(end){
        end = moment(moment(end).format("YYYY-MM-DD HH:mm:ss"));
      }
      if (start && end) {
        if (start > end) {
          setTimeout(() => {
            if (type == 1) {
              this.form.setFieldsValue({
                startTime: undefined,
              });
            } else {
              this.form.setFieldsValue({
                endTime: undefined,
              });
            }
          }, 10);
          this.$message.warning("开始日期不能大于结束日期");
        }
      }
    },
    handleOk() {
      this.form.validateFields((err, values) => {
        if (!err) {
          let query = {
            ...values,
            ...this.model,
            startTime: values.startTime.format('YYYY-MM-DD HH:mm:ss'),
            endTime: values.endTime.format('YYYY-MM-DD HH:mm:ss'),
          }
          console.log(query);
          this.confirmLoading = true;
          // 新增或者编辑
          let request = this.model.id ? editSpecassetPlan : addSpecassetPlan ;
          request(query).then((res)=>{
            this.confirmLoading = false;
            if(res.success){
              this.$message.success('操作成功');
              this.$emit('ok');
              this.close();
            }else{
              this.$message.warning(res.message)
            }
          }).catch((err) => {
             this.confirmLoading = false;
          });
        }
      });
    },
    close() {
      this.visible = false;
      this.model = {};
      this.confirmLoading = false;
    },
  },
};
</script>

<style lang="scss" scoped></style>
