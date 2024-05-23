<template>
  <div class="OutinEntrance">
    <a-modal
      :title="'新增' + (orderType === 0 ? '出库单' : '入库单')"
      :width="'100%'"
      :visible="visible"
      :confirmLoading="confirmLoading"
      dialogClass="fullDialog"
      @cancel="handleCancel"
      @ok="saveOutInInfo"
      :destroyOnClose="true"
    >
      <!--  委外送修 -->
      <div v-if="orderType === 0">
        <div class="fieldset-wrapper">
          <h4 class="title">交接信息</h4>
          <a-form-model
            ref="outInfoForm"
            layout="inline"
            class="work-form"
            :model="outModelInfo"
            :label-col="{ span: 6 }"
            :wrapper-col="{ span: 18 }"
            :rules="outRules"
          >
            <a-row>
              <a-col :span="11">
                <a-form-model-item
                  label="交接单号"
                  :wrapper-col="{ span: 18 }"
                  prop="billNo"
                >
                  <a-input
                    placeholder="交接单号"
                    disabled
                    v-model="outModelInfo.billNo"
                  />
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="交接单名"
                  :wrapper-col="{ span: 18 }"
                  prop="outinName"
                >
                  <a-input placeholder="交接单名" v-model="outModelInfo.outinName" />
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item label="线路" :wrapper-col="{ span: 18 }" prop="lineId">
                  <j-dict-select-tag
                    :triggerChange="true"
                    ref="lineSelect"
                    v-model="outModelInfo.lineId"
                    dictCode="bu_mtr_line,line_name,line_id"
                    @change="onLineChange"
                  />
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="车号"
                  :wrapper-col="{ span: 18 }"
                  prop="trainNo"
                >
                  <j-dict-select-seach-tag
                    :triggerChange="true"
                    v-model="outModelInfo.trainNo"
                    :dictCode="dictTrainStr"
                    @select="onTrainSelect"
                  />
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  :key="oTransferDateMKey"
                  label="交接日期"
                  :wrapper-col="{ span: 18 }"
                  prop="transferDateM"
                >
                  <a-date-picker
                    style="width: 100%"
                    placeholder="交接日期"
                    v-model="outModelInfo.transferDateM"
                    @change="otransFerDateMkey"
                  ></a-date-picker>
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="工程师"
                  :wrapper-col="{ span: 18 }"
                  prop="engineerId"
                >
                  <div @click="showUserModal(2)">
                    <a-select
                      ref="userSelect2"
                      v-model="outModelInfo.engineerName"
                      placeholder="请选择人员"
                      :open="false"
                    >
                      <a-icon slot="suffixIcon" type="ellipsis" />
                    </a-select>
                  </div>
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="作业班组"
                  :wrapper-col="{ span: 18 }"
                  prop="sendGroupId"
                >
                  <j-dict-select-tag
                    :triggerChange="true"
                    v-model="outModelInfo.sendGroupId"
                    :dictCode="dictGroupStr"
                  />
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="移交人"
                  :wrapper-col="{ span: 18 }"
                  prop="transferUserId"
                >
                  <div @click="showUserModal(1)">
                    <a-select
                      ref="userSelect1"
                      v-model="outModelInfo.transferUserName"
                      placeholder="请选择人员"
                      :open="false"
                    >
                      <a-icon slot="suffixIcon" type="ellipsis" />
                    </a-select>
                  </div>
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="接收厂商"
                  :wrapper-col="{ span: 18 }"
                  prop="supplierId"
                >
                  <a-select
                    v-model="outModelInfo.supplierName"
                    placeholder="请选择厂商"
                    :open="false"
                    :showArrow="true"
                    @focus="openSupplierModal(1)"
                    ref="mySupplierSelectOut"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="接收人员"
                  :wrapper-col="{ span: 18 }"
                  prop="receiveUser"
                >
                  <a-input placeholder="接收人员" v-model="outModelInfo.receiveUser" />
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="所属合同"
                  :wrapper-col="{ span: 18 }"
                  prop="contractId"
                >
                  <j-dict-select-tag
                    :trigger-change="true"
                    v-model="outModelInfo.contractId"
                    dictCode="bu_contract_info,contract_name,id,status<>2"
                  />
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item label="备注" :wrapper-col="{ span: 18 }" prop="remark">
                  <a-input placeholder="备注" v-model="outModelInfo.remark" />
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-form-model>
        </div>
        <div class="fieldset-wrapper" style="height: calc(100% - 284px)">
          <h4 class="title">交接部件</h4>
          <div style="padding-bottom: 10px">
            <a-space>
              <a-button @click="onAddOutPart">添加</a-button>
              <a-button @click="onDeleteOutPart">删除</a-button>
            </a-space>
          </div>
          <vxe-table
            key="outTable"
            border
            ref="outTable"
            align="center"
            :data="outModelInfo.outinDetails"
            show-overflow="tooltip"
            :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          >
            <vxe-table-column type="checkbox" width="40"></vxe-table-column>
            <vxe-table-column field="trainNo" title="车号" width="100"></vxe-table-column>
            <vxe-table-column
              field="assetName"
              title="委外部件"
              header-align="center"
              align="left"
              min-width="140"
            ></vxe-table-column>
            <vxe-table-column
              field="assetNo"
              title="部件编号"
              min-width="140"
              align="left"
              header-align="center"
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
              title="是否故障"
              width="100"
            ></vxe-table-column>
            <vxe-table-column
              field="remark"
              title="备注"
              min-width="160"
              header-align="center"
              align="left"
            ></vxe-table-column>
          </vxe-table>
        </div>
      </div>
      <!-- 委外接收 -->
      <div v-else-if="orderType === 1">
        <div class="fieldset-wrapper">
          <h4 class="title">交接信息</h4>
          <a-form-model
            ref="inInfoForm"
            layout="inline"
            class="work-form"
            :model="inModelInfo"
            :label-col="{ span: 6 }"
            :wrapper-col="{ span: 18 }"
            :rules="inRules"
          >
            <a-row>
              <a-col :span="11">
                <a-form-model-item
                  label="交接单号"
                  :wrapperCol="{ span: 18 }"
                  prop="billNo"
                >
                  <a-input placeholder="交接单号" disabled v-model="inModelInfo.billNo" />
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="交接单名"
                  :wrapperCol="{ span: 18 }"
                  prop="outinName"
                >
                  <a-input placeholder="交接单名" v-model="inModelInfo.outinName" />
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item label="线路" :wrapperCol="{ span: 18 }" prop="lineId">
                  <j-dict-select-tag
                    :triggerChange="true"
                    v-model="inModelInfo.lineId"
                    dictCode="bu_mtr_line,line_name,line_id"
                    @change="onLineChange"
                  />
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item label="车号" :wrapperCol="{ span: 18 }" prop="trainNo">
                  <j-dict-select-seach-tag
                    :triggerChange="true"
                    v-model="inModelInfo.trainNo"
                    :dictCode="dictTrainStr"
                    @select="onTrainSelect"
                  />
                </a-form-model-item>
              </a-col>

              <a-col :span="11">
                <a-form-model-item
                  label="接收日期"
                  :wrapperCol="{ span: 18 }"
                  prop="transferDateM"
                >
                  <a-date-picker
                    style="width: 100%"
                    placeholder="交接日期"
                    v-model="inModelInfo.transferDateM"
                  ></a-date-picker>
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="工程师"
                  :wrapperCol="{ span: 18 }"
                  prop="engineerId"
                >
                  <div @click="showUserModal(2)">
                    <a-select
                      ref="userSelect2"
                      v-model="inModelInfo.engineerName"
                      placeholder="请选择人员"
                      :open="false"
                    >
                      <a-icon slot="suffixIcon" type="ellipsis" />
                    </a-select>
                  </div>
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="作业班组"
                  :wrapperCol="{ span: 18 }"
                  prop="sendGroupId"
                >
                  <j-dict-select-tag
                    :triggerChange="true"
                    v-model="inModelInfo.sendGroupId"
                    :dictCode="dictGroupStr"
                  />
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="接收人"
                  :wrapperCol="{ span: 18 }"
                  prop="receiveUser"
                >
                  <div @click="showUserModal(1)">
                    <a-select
                      ref="userSelect1"
                      v-model="inModelInfo.receiveUserName"
                      placeholder="请选择人员"
                      :open="false"
                    >
                      <a-icon slot="suffixIcon" type="ellipsis" />
                    </a-select>
                  </div>
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="移交厂商"
                  :wrapperCol="{ span: 18 }"
                  prop="supplierId"
                >
                  <!--                          <j-dict-select-tag
                          :triggerChange="true"
                          v-model="inModelInfo.supplierId"
                          dictCode="bu_base_supplier,name,id"
                        />-->
                  <a-select
                    v-model="inModelInfo.supplierName"
                    placeholder="请选择厂商"
                    :open="false"
                    :showArrow="true"
                    @focus="openSupplierModal(2)"
                    ref="mySupplierSelectIn"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="移交人员"
                  :wrapperCol="{ span: 18 }"
                  prop="transferUserId"
                >
                  <a-input
                    :maxLength="17"
                    placeholder="移交人员"
                    v-model="inModelInfo.transferUserId"
                  />
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item
                  label="所属合同"
                  :wrapperCol="{ span: 18 }"
                  prop="contractId"
                >
                  <j-dict-select-tag
                    :trigger-change="true"
                    v-model="inModelInfo.contractId"
                    dictCode="bu_contract_info,contract_name,id,status<>2"
                  />
                </a-form-model-item>
              </a-col>
              <a-col :span="11">
                <a-form-model-item label="备注" :wrapperCol="{ span: 18 }" prop="remark">
                  <a-input
                    :maxLength="201"
                    placeholder="备注"
                    v-model="inModelInfo.remark"
                  />
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-form-model>
        </div>
        <div class="fieldset-wrapper" style="height: calc(100% - 284px)">
          <h4 class="title">交接部件</h4>
          <div style="padding-bottom: 10px">
            <a-space>
              <a-button @click="onAddInPart">添加</a-button>
              <a-button @click="onDeleteInPart">删除</a-button>
            </a-space>
          </div>
          <vxe-table
            key="inTable"
            border
            ref="inTable"
            :align="'center'"
            :data="inModelInfo.outinDetails"
            :edit-config="{ trigger: 'manual', mode: 'row' }"
            show-overflow="tooltip"
            :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          >
            <vxe-table-column type="checkbox" width="40"></vxe-table-column>
            <vxe-table-column field="trainNo" title="车号" width="100"></vxe-table-column>
            <vxe-table-column
              field="assetName"
              title="委外部件"
              header-align="center"
              align="left"
              min-width="140"
            ></vxe-table-column>
            <vxe-table-column
              field="assetNo"
              title="部件编号"
              min-width="140"
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
              field="remark"
              title="备注"
              min-width="160"
              header-align="center"
              align="left"
            ></vxe-table-column>
          </vxe-table>
        </div>
      </div>
    </a-modal>
    <supplier-list
      ref="supplierModal"
      :multiple="false"
      @ok="supplierSelect"
    ></supplier-list>
    <out-part-modal
      ref="outPartModal"
      @ok="onOutPartOk"
      :lineId="outModelInfo.lineId"
      :trainNo="outModelInfo.trainNo"
      :exist-assets="outModelInfo.outinDetails"
    ></out-part-modal>
    <user-list ref="tUserModalForm" :multiple="false" @ok="onUserSelect"></user-list>
    <in-part-modal
      ref="inPartModal"
      @ok="onInPartOk"
      :lineId="inModelInfo.lineId"
      :trainNo="inModelInfo.trainNo"
      :exist-assets="outModelInfo.outinDetails"
    ></in-part-modal>
  </div>
</template>

<script>
import { everythingIsEmpty, randomUUID } from "@/utils/util";
import SupplierList from "@views/tiros/common/selectModules/SupplierList";
import OutPartModal from "@views/tiros/group/myWork/OutPartModal";
import UserList from "@views/tiros/common/selectModules/UserList";
import InPartModal from "@views/tiros/group/myWork/InPartModal";
import { saveOutInInfo } from "@/api/tirosGroupApi";
import { getSerialNumber } from "@/api/tirosApi.js";
import moment from "moment";
export default {
  components: { UserList, SupplierList, OutPartModal, InPartModal },
  data() {
    return {
      transferDateMKey: 0,
      orderType: 0, // 类型  出库/入库
      visible: false,
      confirmLoading: false,
      dictTrainStr: "",
      oTransferDateMKey: 0,
      userSelectType: 1,
      supplierSelectType: 1,
      curSelectTrainStructId: "",
      dictGroupStr: "",
      outModelInfo: {
        billType: 0,
        transferDateM: "",
        billNo: "",
        outinName: "",
        lineId: "",
        trainNo: "",
        engineerId: "",
        engineerName: "",
        sendGroupId: "",
        transferUserId: "",
        transferUserName: "",
        supplierId: "",
        supplierName: "",
        receiveUser: "",
        contractId: "",
        remark: "",
        outinDetails: [],
      },
      inModelInfo: {
        billType: 1,
        billNo: "",
        outinName: "",
        lineId: "",
        trainNo: "",
        transferDateM: "",
        engineerId: "",
        engineerName: "",
        sendGroupId: "",
        receiveUser: "",
        receiveUserName: "",
        supplierName: "",
        transferUserId: "",
        contractId: "",
        supplierId: "",
        remark: "",
        outinDetails: [],
      },
      outRules: {
        billNo: [{ required: false, message: "请填写交接单号!" }],
        outinName: [{ required: true, message: "请填写交接单名称!" }],
        lineId: [{ required: true, message: "请选择线路!" }],
        trainNo: [{ required: true, message: "请选择车辆!" }],
        transferUserId: [{ required: true, message: "请选择移交人!" }],
        receiveUser: [{ max: 32, message: "不能超过32个字符!" }],
        transferDateM: [{ required: true, message: "请选择交接日期!" }],
        engineerId: [{ required: true, message: "请选择工程师!" }],
        sendGroupId: [{ required: true, message: "请选择作业班组!" }],
        supplierId: [{ required: true, message: "请选择接收厂商!" }],
        contractId: [{ required: true, message: "请选择所属合同!" }],
        remark: [{ required: false, max: 200, message: "不能超过200个字符!" }],
      },
      inRules: {
        billNo: [{ required: false, message: "请填写交接单号!" }],
        outinName: [{ required: true, message: "请填写交接单名称!" }],
        lineId: [{ required: true, message: "请选择线路!" }],
        trainNo: [{ required: true, message: "请选择车辆!" }],
        receiveUser: [{ required: true, message: "请选择接收人!" }],
        transferUserId: [{ max: 32, message: "不能超过32个字符!" }],
        transferDateM: [{ required: true, message: "请选择交接日期!" }],
        engineerId: [{ required: true, message: "请选择工程师!" }],
        sendGroupId: [{ required: true, message: "请选择作业班组!" }],
        supplierId: [{ required: true, message: "请选择移交厂商!" }],
        contractId: [{ required: true, message: "请选择所属合同!" }],
        remark: [{ required: false, max: 200, message: "不能超过200个字符!" }],
      },
    };
  },
  methods: {
    saveOutInInfo() {
      if (this.orderType === 0) {
        this.saveOutInfo();
      }
      if (this.orderType === 1) {
        this.saveInInfo();
      }
    },
    saveOutInfo() {
      this.$refs.outInfoForm.validate((valid) => {
        if (valid) {
          if (
            !this.outModelInfo.outinDetails ||
            this.outModelInfo.outinDetails.length < 1
          ) {
            this.$message.warning("没有添加任何交接部件，请添加");
            return;
          }
          this.outModelInfo.transferDate = this.outModelInfo.transferDateM.format(
            "YYYY-MM-DD"
          );
          this.confirmLoading = true;
          saveOutInInfo(this.outModelInfo)
            .then((res) => {
              if (res.success) {
                this.$message.success("保存成功");
                this.$emit("ok");
                this.handleCancel();
              } else {
                this.$message.error("保存失败");
                console.error("保存委外送修信息失败:", res.message);
              }
            })
            .finally(() => {
              this.confirmLoading = false;
            });
        }
      });
    },
    saveInInfo() {
      this.$refs.inInfoForm.validate((valid) => {
        if (valid) {
          if (
            !this.inModelInfo.outinDetails ||
            this.inModelInfo.outinDetails.length < 1
          ) {
            this.$message.warning("没有添加任何交接部件，请添加");
            return;
          }

          this.inModelInfo.transferDate = this.inModelInfo.transferDateM.format(
            "YYYY-MM-DD"
          );
          this.confirmLoading = true;
          saveOutInInfo(this.inModelInfo)
            .then((res) => {
              if (res.success) {
                this.$message.success("保存成功");
                this.$emit("ok");
                this.handleCancel();
              } else {
                this.$message.error("保存失败");
                console.error("保存委外接受信息失败:", res.message);
              }
            })
            .finally(() => {
              this.confirmLoading = false;
            });
        }
      });
    },
    add(type) {
      this.edit(false, type);
    },
    edit(data, type) {
      Object.assign(this.$data, this.$options.data());
      this.dictGroupStr =
        "bu_mtr_workshop_group,group_name,id,workshop_id='" +
        this.$store.getters.userInfo.workshopId +
        "'|workshop_id";
      this.visible = true;
      console.log(data);
      if (data) {
        this.isEditor = true;
        this.orderType = data.billType;
        console.log(data.billType);
        if (data.billType === 0) {
          this.outModelInfo = Object.assign(this.outModelInfo, data);
          this.outModelInfo.transferDateM = moment(data.transferDateM);
        } else {
          this.inModelInfo = Object.assign(this.inModelInfo, data);
          this.inModelInfo.transferDateM = moment(data.transferDateM);
          console.log(this.inModelInfo);
        }
      } else {
        this.orderType = type;
        // getSerialNumber({ moduleCode: "OutTaskCode" }).then((res) => {
        //   if (res.success) {
        //     if (this.orderType == 0) {
        //       this.outModelInfo.billNo = res.result;
        //     } else {
        //       this.inModelInfo.billNo = res.result;
        //     }
        //   }
        // });
      }
    },
    // 关闭
    handleCancel() {
      this.close();
    },
    onDeleteOutPart() {
      let selectRecords = this.$refs.outTable.getCheckboxRecords();
      let ids = selectRecords.map((item) => item.id);
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}条数据？`,
          onOk: () => {
            for (let i = this.outModelInfo.outinDetails.length - 1; i >= 0; i--) {
              //
              let detail = this.outModelInfo.outinDetails[i];
              if (ids.indexOf(detail.id) > -1) {
                this.outModelInfo.outinDetails.splice(i, 1);
              }
            }
          },
        });
      } else {
        this.$message.error("尚未选中任何数据!");
      }
    },
    openSupplierModal(type) {
      if (type === 1) {
        this.supplierSelectType = 1;
        this.$refs.supplierModal.showModal();
        this.$refs.mySupplierSelectOut.blur();
      } else {
        this.supplierSelectType = 2;
        this.$refs.supplierModal.showModal();
        this.$refs.mySupplierSelectIn.blur();
      }
    },

    onUserSelect(data) {
      if (data.length) {
        if (this.userSelectType === 1) {
          if (this.orderType === 0) {
            // 送修
            this.outModelInfo.transferUserId = data[0].id;
            this.outModelInfo.transferUserName = data[0].realname;
            this.$refs.outInfoForm.validateField("transferUserId");
          } else if (this.orderType === 1) {
            // 接收
            this.inModelInfo.receiveUser = data[0].id;
            this.inModelInfo.receiveUserName = data[0].realname;
            this.$refs.inInfoForm.validateField("receiveUser");
          }
        } else if (this.userSelectType === 2) {
          if (this.orderType === 0) {
            // 送修
            this.outModelInfo.engineerId = data[0].id;
            this.outModelInfo.engineerName = data[0].realname;
            this.$refs.outInfoForm.validateField("engineerId");
          } else if (this.orderType === 1) {
            // 接收
            this.inModelInfo.engineerId = data[0].id;
            this.inModelInfo.engineerName = data[0].realname;
            this.$refs.inInfoForm.validateField("engineerId");
          }
        }
      }
      this.$forceUpdate();
    },
    supplierSelect(data) {
      if (!everythingIsEmpty(data)) {
        if (this.supplierSelectType === 1) {
          this.outModelInfo.supplierId = data[0].id;
          this.outModelInfo.supplierName = data[0].name;
          this.$refs.outInfoForm.validateField("supplierId");
        } else {
          this.inModelInfo.supplierId = data[0].id;
          this.inModelInfo.supplierName = data[0].name;
          this.$refs.inInfoForm.validateField("supplierId");
        }
      }
    },
    onOutPartOk(data) {
      this.outModelInfo.outinDetails.push(data);
    },
    onInPartOk(data) {
      this.inModelInfo.outinDetails.push(data);
    },
    showUserModal(type) {
      this.userSelectType = type;
      this.$refs.tUserModalForm.showModal();
      switch (type) {
        case 1:
          this.$refs.userSelect1.blur();
          break;
        case 2:
          this.$refs.userSelect2.blur();
          break;
      }
    },
    close() {
      this.$emit("close");
      this.visible = false;
    },
    onAddOutPart() {
      if (!this.outModelInfo.lineId || !this.outModelInfo.trainNo) {
        this.$message.warning("请先选择线路和车辆！");
        return;
      }
      if (!this.outModelInfo.outinDetails) {
        this.outModelInfo.outinDetails = [];
      }
      this.$refs.outPartModal.showModal(this.curSelectTrainStructId);
    },
    onDeleteInPart() {
      let selectRecords = this.$refs.inTable.getCheckboxRecords();
      let ids = selectRecords.map((item) => item.id);
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}条数据？`,
          onOk: () => {
            for (let i = this.inModelInfo.outinDetails.length - 1; i >= 0; i--) {
              //
              let detail = this.inModelInfo.outinDetails[i];
              if (ids.indexOf(detail.id) > -1) {
                this.inModelInfo.outinDetails.splice(i, 1);
              }
            }
          },
        });
      } else {
        this.$message.error("尚未选中任何数据!");
      }
    },
    onAddInPart() {
      if (!this.inModelInfo.lineId || !this.inModelInfo.trainNo) {
        this.$message.warning("请先选择线路和车辆！");
        return;
      }
      if (!this.inModelInfo.outinDetails) {
        this.inModelInfo.outinDetails = [];
      }
      this.$refs.inPartModal.showModal(this.curSelectTrainStructId);
    },
    otransFerDateMkey() {
      this.oTransferDateMKey++;
      this.$nextTick(() => {
        this.$refs.outInfoForm.validateField("transferDateM");
      });
    },
    onTrainSelect(val, data) {
      if (data) {
        console.log("onTrainSelect.data")
        console.log(data)
        this.curSelectTrainStructId = data.extFields[0].train_struct_id;
      }
    },
    onLineChange(value, option) {
      if (value) {
        this.dictTrainStr =
          "bu_train_info,train_no,train_no,line_id=" + value + "|train_struct_id";
      } else {
        this.dictTrainStr = "";
      }
    },
  },
};
</script>

<style lang="less" scoped>
/deep/ .ant-row.ant-form-item {
  width: 80%;
}
</style>
