<template>
  <a-modal
    title="交接单详情"
    :width="'85%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    dialogClass="fullDialog no-footer"
    @cancel="handleCancel"
    :destroyOnClose="true"
    :footer="null"
  >
    <div class="content">
      <div class="info-wrapper info-top-wrapper">
        <h4>交接信息</h4>
        <a-descriptions bordered>
          <a-descriptions-item label="交接单号">
            {{ outInDetail.billNo }}
          </a-descriptions-item>
          <a-descriptions-item label="线路">
            {{ outInDetail.lineName }}
          </a-descriptions-item>
          <a-descriptions-item label="车号">
            {{ outInDetail.trainNo }}
          </a-descriptions-item>
          <a-descriptions-item label="交接单名">
            {{ outInDetail.outinName }}
          </a-descriptions-item>
          <a-descriptions-item label="交接日期">
            {{ outInDetail.transferDate }}
          </a-descriptions-item>
          <a-descriptions-item label="作业班组">
            {{ outInDetail.sendGroupName }}
          </a-descriptions-item>
          <a-descriptions-item label="移交人">
            {{ outInDetail.transferUserName }}
          </a-descriptions-item>
          <a-descriptions-item label="接收人">
            {{ outInDetail.receiveUserName }}
          </a-descriptions-item>
          <a-descriptions-item label="工程师">
            {{ outInDetail.engineerName }}
          </a-descriptions-item>
          <a-descriptions-item :label="outInDetail.billType == 0 ? '接收厂商' : '移交厂商'">
            {{ outInDetail.supplierName }}
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </div>
    <div class="content">
      <div class="info-wrapper info-top-wrapper" style="margin-bottom:0">
        <h4>交接部件</h4>
        <div style="height: calc(100vh - 385px)">
          <vxe-table
            border
            ref="listTable"
            height="auto"
            :align="allAlign"
            :data="outInAsset"
            show-overflow="tooltip"
            :edit-config="{ trigger: 'manual', mode: 'row' }"
          >
            <vxe-table-column
              type="checkbox"
              width="40"
              align="center"
            ></vxe-table-column>
            <vxe-table-column
              field="lineName"
              title="线路"
              width="100"
            ></vxe-table-column>
            <vxe-table-column field="trainNo" title="车号" width="100"></vxe-table-column>
            <vxe-table-column
              field="assetName"
              header-align="center" 
              align="left"
              title="委外部件"
              min-width="120"
            ></vxe-table-column>
            <vxe-table-column
              field="assetNo"
              title="部件编号"
              min-width="120"
            ></vxe-table-column>
            <vxe-table-column
              field="facadeStatus_dictText"
              title="外观状态"
              width="100"
            ></vxe-table-column>
            <vxe-table-column
              field="mixtoolStatus_dictText"
              title="工装状态"
              width="100"
            ></vxe-table-column>
            <vxe-table-column
              field="fault_dictText"
              title="是否存在故障"
              width="110"
            ></vxe-table-column>
            <vxe-table-column
              field="remark"
              title="备注"
              header-align="center"
              align="left"
            ></vxe-table-column>
          </vxe-table>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script>
import moment from "moment";
import { getOutTaskInfo } from "@api/tirosGroupApi";

export default {
  name: "OutinDetail",
  data() {
    return {
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" +
        this.$store.getters.userInfo.workshopId +
        "'|workshop_id",
      outInDetail: {},
      outInAsset: [],
      totalResult: 0,
      allAlign: "center",
      billType: 0,
      form: this.$form.createForm(this),
      visible: false,
      confirmLoading: false,
      validatorRules: {
        engineerId: { rules: [{ required: true, message: "请选择工程师!" }] },
        trainNo: { rules: [{ required: true, message: "请选择车号!" }] },
        sendGroupId: { rules: [{ required: true, message: "请选择作业班组!" }] },
        receiveUser: { rules: [{ required: true, message: "请输入接车人!" }] },
        lineId: { rules: [{ required: true, message: "请选择所属线路!" }] },
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
    };
  },
  /* created() {
     if (this.$route.params.outInDetail) {
       this.outInDetail = this.$route.params.outInDetail
       this.outInAsset=  this.outInDetail.outinDetails
       this.billType= this.outInDetail.billType
       this.edit(this.outInDetail)
     }
   },*/

  methods: {
    show(value) {
      this.visible = true;
      this.outInDetail = value;
      this.outInAsset = value.outinDetails;
      this.billType = value.billType;
      this.edit(this.outInDetail);
    },
    showByTaskId(taskId) {
      getOutTaskInfo({ id: taskId }).then((res) => {
        if (res.success) {
          this.show(res.result);
        }
      });
    },
    edit(record) {
      // this.form.resetFields();
      this.model = Object.assign({}, record);
      // this.$nextTick(() => {
      //   this.form.setFieldsValue({
      //     billNo: this.model.billNo,
      //     lineId: this.model.lineId,
      //     trainNo: this.model.trainNo,
      //     transferDate: moment(this.model.transferDate || undefined),
      //     outinName: this.model.outinName,
      //     transferUserId: this.model.transferUserName,
      //     sendGroupId: this.model.sendGroupId,
      //     receiveUser: this.model.receiveUserName,
      //     engineerId: this.model.engineerName,
      //   });
      // });
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

<style scoped lang="less">
.content {
  .info-wrapper {
    border: 1px solid #eee;
    position: relative;
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 20px;
    margin-top: 10px;
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

  .ant-form-item {
    margin: 0;
  }
}
</style>
