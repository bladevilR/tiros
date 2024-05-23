<template>
  <a-modal
    title="编辑"
    width="100%"
    height="auto"
    :visible="visible"
    :confirmLoading="confirmLoading"
    dialogClass="fullDialog"
    @ok="ok"
    @cancel="cancel"
    :destroyOnClose="true"
    :zIndex="50"
  >
    <div class="contentBox">
      <a-form-model
        ref="form"
        :model="formModel"
        :rules="formRules"
        style="margin-top: 10px"
      >
        <div class="info-wrapper info-top-wrapper">
          <h4>扩展信息</h4>
          <a-row :gutter="24">

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="简称"
                prop="shortName"
              >
                <a-input v-model="formModel.shortName" />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="车厢"
                prop="carriage"
              >
                <a-input v-model="formModel.carriage" />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="设备系统"
                prop="systemName"
              >
                <j-dict-select-tag
                  :triggerChange="true"
                  v-model="formModel.systemId"
                  :dictCode="dictSysIdStr1"
                  placeholder="请选择"
                />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="设备类型"
                prop="assetTypeName"
              >
                <a-select
                  allowClear
                  ref="tmyuserSelect1"
                  v-model="formModel.assetTypeName"
                  placeholder="请选择厂商"
                  :open="false"
                  @change="assetTypeNameChange"
                  @dropdownVisibleChange="
                    $refs.selectModal.showModal(), $refs.tmyuserSelect1.blur()
                  "
                >
                  <a-icon slot="suffixIcon" type="ellipsis" />
                </a-select>
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="设备分类"
                prop="assetCategory"
              >
                <a-input v-model="formModel.assetCategory" />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="厂商"
                prop="vendorName"
              >
                <a-select
                  allowClear
                  ref="tmyuserSelect2"
                  v-model="formModel.vendorName"
                  placeholder="请选择厂商"
                  :open="false"
                  @change="vendorNameChange"
                  @dropdownVisibleChange="
                    $refs.supplierModal.showModal(), $refs.tmyuserSelect2.blur()
                  "
                >
                  <a-icon slot="suffixIcon" type="ellipsis" />
                </a-select>
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="单位"
                prop="company"
              >
                <a-input v-model="formModel.company" />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="合同编号"
                prop="contractNo"
              >
                <a-input v-model="formModel.contractNo" />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="合同名称"
                prop="contractName"
              >
                <a-input v-model="formModel.contractName" />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="制造日期"
                prop="madeDate"
              >
                <a-date-picker
                  format="YYYY-MM-DD"
                  v-model="formModel.madeDate"
                  style="width: 100%"
                />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="购入日期"
                prop="buyDate"
              >
                <a-date-picker
                  format="YYYY-MM-DD"
                  v-model="formModel.buyDate"
                  style="width: 100%"
                />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="安装日期"
                prop="installDate"
              >
                <a-date-picker
                  format="YYYY-MM-DD"
                  v-model="formModel.installDate"
                  style="width: 100%"
                />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="验收日期"
                prop="verifyDate"
              >
                <a-date-picker
                  format="YYYY-MM-DD"
                  v-model="formModel.verifyDate"
                  style="width: 100%"
                />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="质保日期"
                prop="warrantedDate"
              >
                <a-date-picker
                  format="YYYY-MM-DD"
                  v-model="formModel.warrantedDate"
                  style="width: 100%"
                />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="运营日期"
                prop="operateDate"
              >
                <a-date-picker
                  format="YYYY-MM-DD"
                  v-model="formModel.operateDate"
                  style="width: 100%"
                />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="使用年限"
                prop="ageYear"
              >
                <a-input-number
                  style="width: 100%"
                  id="inputNumber"
                  v-model="formModel.ageYear"
                  :min="0"
                  :max="99"
                />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="品牌"
                prop="brand"
              >
                <a-input v-model="formModel.brand" />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="规格型号"
                prop="model"
              >
                <a-input v-model="formModel.model" />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="bom名称"
                prop="bomName"
              >
                <a-input v-model="formModel.bomName" />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="资产编码"
                prop="assetCode"
              >
                <a-input v-model="formModel.assetCode" />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="出厂编码"
                prop="manufNo"
              >
                <a-input v-model="formModel.manufNo" />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="标签编码"
                prop="tagCode"
              >
                <a-input v-model="formModel.tagCode" />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="归属部门"
                prop="belongDepart"
              >
                <j-select-depart
                  v-model="formModel.belongDepart"
                  :multi="true"
                ></j-select-depart>
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="责任班组"
                prop="dutyGroup"
              >
                <j-dict-select-tag
                  :triggerChange="true"
                  v-model="formModel.dutyGroup"
                  :dictCode="dictGroupStr"
                />
              </a-form-model-item>
            </a-col>

            <a-col :span="8">
              <a-form-model-item
                :labelCol="labelCol"
                :wrapperCol="wrapperCol"
                label="责任人"
                prop="dutyUserName"
              >
                <a-select
                  allowClear
                  ref="tmyuserSelect3"
                  v-model="formModel.dutyUserName"
                  placeholder="请选择人员"
                  :open="false"
                  @change="userChange"
                  @dropdownVisibleChange="
                    $refs.tUserModalForm.showModal(), $refs.tmyuserSelect3.blur()
                  "
                >
                  <a-icon slot="suffixIcon" type="ellipsis" />
                </a-select>
              </a-form-model-item>
            </a-col>

            <a-col :span="16">
              <a-form-model-item
                :labelCol="{ span: 3 }"
                :wrapperCol="{ span: 12 }"
                label="其他属性"
              >
                <a-row>
                  <a-col :span="24 / 4">
                    是否虚拟: <a-switch v-model="formModel.subjunctive" />
                  </a-col>
                  <a-col :span="24 / 4">
                    是否委外: <a-switch v-model="formModel.outsourceRepair" />
                  </a-col>
                  <a-col :span="24 / 4">
                    是否探伤: <a-switch v-model="formModel.flawDetection" />
                  </a-col>
                  <a-col :span="24 / 4">
                    是否送检: <a-switch v-model="formModel.sendCheck" />
                  </a-col>
                </a-row>
              </a-form-model-item>
            </a-col>
          </a-row>
        </div>
      </a-form-model>
    </div>
    <user-list
      ref="tUserModalForm"
      :dep-id="formModel.dutyGroup"
      :multiple="false"
      @ok="onUserSelect"
    ></user-list>
    <train-asset-type
      ref="selectModal"
      :multiple="false"
      :trainTypeId="trainTypeId"
      @ok="selectAssetType"
    ></train-asset-type>
    <supplier-list
      ref="supplierModal"
      :multiple="false"
      @ok="supplierSelect"
    ></supplier-list>
  </a-modal>
</template>

<script>
import moment from "moment";
import SupplierList from "@views/tiros/common/selectModules/SupplierList";
import JSelectDepart from "@/components/jeecgbiz/JSelectDepart";
import { getAssetInfoData, setAssetInfoSave } from "@/api/tirosApi";
import UserList from "@views/tiros/common/selectModules/UserList";
import TrainAssetType from '../../../common/selectModules/TrainAssetType'
export default {
  name: "maximoAssetEdit",
  components: { UserList, JSelectDepart, SupplierList, TrainAssetType },
  data() {
    return {
      visible: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      trainTypeId:'',
      dictSysIdStr1: "bu_train_asset_type,name,id,struct_type=1 and parent_id is null",
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" +
        this.$store.getters.userInfo.workshopId +
        "'|workshop_id",
      confirmLoading: false,
      formModel: {},
      formRules: {},
    };
  },
  methods: {
    onUserSelect(data) {
      if (data && data.length) {
        this.$set(this.formModel, "dutyUser", data[0].id);
        this.$set(this.formModel, "dutyUserName", data[0].realname);
      }
    },
    selectAssetType(data) {
      if (data && data.length) {
        this.$set(this.formModel, "assetTypeId", data[0].id);
        this.$set(this.formModel, "assetTypeName", data[0].name);
      }
    },
    supplierSelect(data) {
      if (data && data.length) {
        this.$set(this.formModel, "vendor", data[0].id);
        this.$set(this.formModel, "vendorName", data[0].name);
      }
    },
    vendorNameChange(value) {
      if (!value) {
        this.$set(this.formModel, "vendor", "");
        this.$set(this.formModel, "vendorName", undefined);
      }
    },
    assetTypeNameChange(value) {
      if (!value) {
        this.$set(this.formModel, "assetTypeId", "");
        this.$set(this.formModel, "assetTypeName", undefined);
      }
    },
    userChange(value) {
      if (!value) {
        this.$set(this.formModel, "dutyUser", "");
        this.$set(this.formModel, "dutyUserName", undefined);
      }
    },
    showModal(data) {
      console.log(data);
      this.visible = true;
      getAssetInfoData({ id: data.ext.assetId }).then((res) => {
        console.log(res);
        if (res.success && res.result && res.result.ext) {
          for (let i in res.result.ext) {
            // 遍历循环
            this.$set(this.formModel, i, res.result.ext[i]);
          }
          if (this.formModel.subjunctive) {
            this.$set(this.formModel, "subjunctive", true);
          } else {
            this.$set(this.formModel, "subjunctive", false);
          }

          if (this.formModel.outsourceRepair) {
            this.$set(this.formModel, "outsourceRepair", true);
          } else {
            this.$set(this.formModel, "outsourceRepair", false);
          }

          if (this.formModel.flawDetection) {
            this.$set(this.formModel, "flawDetection", true);
          } else {
            this.$set(this.formModel, "flawDetection", false);
          }

          if (this.formModel.sendCheck) {
            this.$set(this.formModel, "sendCheck", true);
          } else {
            this.$set(this.formModel, "sendCheck", false);
          }
        }
      });
    },
    ok() {
      let params = { ...this.formModel };

      params.subjunctive = params.subjunctive ? 1 : 0;
      params.outsourceRepair = params.outsourceRepair ? 1 : 0;
      params.flawDetection = params.flawDetection ? 1 : 0;
      params.sendCheck = params.sendCheck ? 1 : 0;

      params.buyDate = params.buyDate && moment(params.buyDate).format('YYYY-MM-DD');
      params.installDate = params.installDate && moment(params.installDate).format('YYYY-MM-DD');
      params.madeDate = params.madeDate && moment(params.madeDate).format('YYYY-MM-DD');
      params.operateDate = params.operateDate && moment(params.operateDate).format('YYYY-MM-DD');
      params.verifyDate = params.verifyDate && moment(params.verifyDate).format('YYYY-MM-DD');
      params.warrantedDate = params.warrantedDate && moment(params.warrantedDate).format('YYYY-MM-DD');

      console.log(params)
      this.confirmLoading = true;
      setAssetInfoSave(params)
        .then((res) => {
          if (res.success) {
            this.$message.success(res.message);
            this.$emit('ok');
            this.cancel();
          } else {
            this.$message.warning(res.message);
          }
          this.confirmLoading = false;
        })
        .catch((err) => {
          this.confirmLoading = false;
        });
    },
    cancel() {
      this.visible = false;
      Object.assign(this.$data, this.$options.data());
    },
  },
};
</script>

<style lang="less" scoped>
.contentBox {
  width: 100%;
  height: calc(100vh - 120px);
  overflow-y: auto;
  overflow-x: hidden;
  .info-wrapper {
    border: 1px solid #eee;
    position: relative;
    border-radius: 8px;
    padding: 10px;
    margin-bottom: 20px;
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
}
</style>
