<template>
  <div>
    <a-modal
      title="核实"
      :width="'70%'"
      :visible="visible"
      :confirmLoading="confirmLoading"
      @cancel="handleCancel"
      @ok="saveOutInInfo"
      :destroyOnClose="true"
    >
      <div class="fieldset-wrapper">
        <h4 class="title">物料详情</h4>
        <a-form-model
          layout="inline"
          :model="form"
          :label-col="labelCol"
          :wrapper-col="wrapperCol"
        >
          <a-row>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="线路">
                <a-input :value="form.lineName" disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="车辆">
                <a-input :value="form.trainNo" disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="列计划">
                <a-input :value="form.planName" disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="工单号">
                <a-input :value="form.orderCode" disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="工单名称">
                <a-input :value="form.orderName" disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="工单类型">
                <a-input :value="form.orderType_dictText" disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="物资编码">
                <a-input :value="form.materialTypeCode" disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="物资名称">
                <a-input :value="form.materialTypeName" disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="物资描述">
                <a-input :value="form.materialTypeSpec" disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="工班">
                <a-input :value="form.groupName" disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="额定数量">
                <a-input :value="form.amount" disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="实际消耗">
                <a-input :value="form.actAmount" disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="类别">
                <j-dict-select-tag
                  v-model="form.useCategory"
                  placeholder="请选择"
                  dictCode="bu_material_type"
                />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="系统">
                <j-dict-select-tag
                  v-model="form.systemId"
                  placeholder="请选择"
                  dictCode="bu_train_asset_type,name,id,struct_type=1 and parent_id is null"
                />
              </a-form-model-item>
            </a-col>
            <a-col :span="8">
              <a-form-model-item class="modelItemWarp" label="工位">
                <j-dict-select-tag
                  v-model="form.workstationId"
                  placeholder="请选择工位"
                  dictCode="bu_mtr_workstation,name,id"
                />
              </a-form-model-item>
            </a-col>
          </a-row>
        </a-form-model>
      </div>
      <div class="fieldset-wrapper">
        <h4 class="title">实际消耗</h4>
        <div style="padding-bottom: 10px">
          <a-space>
            <a-button @click="$refs.modalForm.showModal()">添加</a-button>
            <a-button @click="deleteEvent">删除</a-button>
          </a-space>
        </div>
        <div style="height: calc(100vh - 600px)">
          <vxe-table
            key="outTable"
            border
            height="auto"
            ref="outTable"
            align="center"
            :data="form.actList"
            @checkbox-all="selectChangeEvent"
            @checkbox-change="selectChangeEvent"
            show-overflow="tooltip"
            :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          >
            <vxe-table-column type="checkbox" width="40"></vxe-table-column>
            <vxe-table-column field="tradeNo" title="批次号"></vxe-table-column>
            <vxe-table-column field="price" title="价格"></vxe-table-column>
            <vxe-table-column field="actAmount" title="数量">
              <template v-slot="{ row }">
                <a-input-number v-model="row.actAmount" :min="0" :precision="0" />
              </template>
            </vxe-table-column>
          </vxe-table>
        </div>
      </div>
    </a-modal>
    <material-list
      ref="modalForm"
      :lineId="form.lineId"
      :multiple="false"
      :mode="[3]"
      :materialTypeCode="form.materialTypeCode"
      :group-id="form.groupId"
      @ok="onSelectMaterial"
    ></material-list>
  </div>
</template>

<script>
import { randomUUID } from "@/utils/util.js";
import MaterialList from "@views/tiros/common/selectModules/MaterialList";
import { planCostDetails, planCostDetailsSave } from "@api/tirosMaterialApi";
export default {
  name: "planMaterialModal",
  components: { MaterialList },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      form: {
        workstationId: null,
        useCategory: null,
        systemId: null,
        actList: [],
      },
      selecteList: [],
      labelCol: { span: 6 },
      wrapperCol: { span: 18 },
    };
  },
  methods: {
    selectChangeEvent({ records }) {
      this.selecteList = JSON.parse(JSON.stringify(records));
    },
    deleteEvent() {
      this.selecteList.forEach((item, index, arr) => {
        for (let i = 0, len = this.form.actList.length; i < len; i++) {
          if (item.id == this.form.actList[i].id) {
            this.form.actList.splice(i, 1);
            break;
          }
        }
      });
    },
    onSelectMaterial(data) {
      console.log(data);
      if (data.length) {
        const list = this.form.actList.filter((item) => {
          return item.tradeNo == data[0].tradeNo; //条件;
        });
        console.log(list);
        if (list.length) {
          return this.$message.warning("添加的批次已经存在列表中");
        }
        this.form.actList.push({
          id: randomUUID(),
          groupStockId: data[0].id,
          applyId: data[0].applyId,
          applyIdDetailId: data[0].applyIdDetailId,
          assignDetailId: data[0].assignDetailId,
          tradeNo: data[0].tradeNo,
          actAmount: "",
          price: data[0].price,
        });
      }
    },
    show(data) {
      console.log(data);
      planCostDetails({ orderMaterialId: data.id }).then((res) => {
        if (res.success && res.result) {
          this.form = { ...this.form, ...res.result };
          this.selecteList = [];
        }
      });
      this.visible = true;
    },
    saveOutInInfo() {
      console.log(23232);
      this.confirmLoading = true;
      planCostDetailsSave(this.form).then((res) => {
        if (res.success) {
          this.$emit("ok");
          this.$message.success(res.message)
          this.handleCancel();
        } else {
          this.$message.warning(res.message);
        }
        this.confirmLoading = false;
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

<style lang="less" scoped>
.modelItemWarp {
  width: 100%;
}
</style>
