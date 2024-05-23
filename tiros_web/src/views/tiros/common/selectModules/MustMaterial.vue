<template>
  <!-- 物料类型选择弹窗 -->
  <a-modal
    width="90%"
    title="必换件选择"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    centered
    :destroyOnClose="true"
    :bodyStyle="{ height: '80vh' }"
  >
    <div class="table-page-search-wrapper">
      <a-form
        layout="inline"
        @keyup.enter.native="findList"
        :label-col="formItemLayout.labelCol"
        :wrapper-col="formItemLayout.wrapperCol"
      >
        <a-row :gutter="24">
          <a-col :md="4" :sm="12">
            <a-form-item label="必换件">
              <a-input
                allowClear
                placeholder="必换件编码或者名称"
                v-model="willChangeQueryParam.searchText"
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="24">
            <a-form-item label="所属线路">
              <line-select-list v-model="willChangeQueryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="所属修程">
              <j-dict-select-tag
                v-model="willChangeQueryParam.repairProgramId"
                placeholder="请选择"
                dictCode="bu_repair_program,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="所属系统">
              <j-dict-select-tag
                v-model="willChangeQueryParam.sysId"
                placeholder="请选择"
                dictCode="bu_train_asset_type,name,id,struct_type=1 and parent_id is null"
              />
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="24">
            <a-form-item label="班组">
              <j-dict-select-tag
                v-model="willChangeQueryParam.groupId"
                placeholder="请选择班组"
                :dictCode="dictGroupStr"
              />
            </a-form-item>
          </a-col>

          <a-col :md="3" :sm="24">
            <a-form-item label="工位">
              <j-dict-select-tag
                v-model="willChangeQueryParam.workstationId"
                placeholder="请选择"
                :dictCode="dictCodeStrWork"
              />
            </a-form-item>
          </a-col>
          <a-col :md="2" :sm="8">
            <a-form-item>
              <a-button @click="findList">查询</a-button>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(80vh - 80px)">
      <vxe-table
        border
        style="height: calc(80vh - 130px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        highlight-current-row
        show-overflow="tooltip"
        :radio-config="!multiple ? { trigger: 'row' } : {}"
        :checkbox-config="
          multiple ? { trigger: 'row', highlight: true, range: true } : {}
        "
        :edit-config="{ trigger: 'click', mode: 'cell', showIcon: 'true' }"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
        <vxe-table-column field="code" title="必换件编码" width="150"></vxe-table-column>
        <vxe-table-column
          field="name"
          title="必换件名称"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="spec"
          title="必换件描述"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="groupName"
          title="所属班组"
          width="150"
        ></vxe-table-column>
        <vxe-table-column
          field="workstationName"
          title="所属工位"
          width="150"
        ></vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="150"></vxe-table-column>
        <vxe-table-column
          field="repairProgramName"
          title="修程"
          width="150"
        ></vxe-table-column>
        <vxe-table-column
          field="amount"
          title="所需数量"
          width="200"
          :edit-render="{
            name: '$input',
            immediate: true,
            props: { type: 'number', Min: 0 },
          }"
        ></vxe-table-column>
        <vxe-table-column
          field="amount"
          title="当前库存"
          width="150"
        ></vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync="willChangeQueryParam.pageNo"
        :page-size.sync="willChangeQueryParam.pageSize"
        :total="totalResult"
        :layouts="[
          'PrevJump',
          'PrevPage',
          'Number',
          'NextPage',
          'NextJump',
          'Sizes',
          'FullJump',
          'Total',
        ]"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
  </a-modal>
</template>

<script>
import { getWillChangeList } from "@/api/tirosMaterialApi";
import JTreeSelect from "@comp/jeecg/JTreeSelect";
import LineSelectList from "@views/tiros/common/selectModules/LineSelectList";

export default {
  name: "MaterialList",
  components: { JTreeSelect, LineSelectList },
  props: {
    repairProId: {
      default: "",
    },
    lineId: {
      default: "",
    },
    multiple: {
      type: Boolean,
      default: true,
    },
    disabled: {
      type: Boolean,
      default: false,
      required: false,
    },
    defaultCheckedKeys: {
      type: String,
      default: "",
    },
  },
  watch: {
    repairProId: {
      handler(val){
        this.willChangeQueryParam.repairProgramId = val;
      },
      immediate:true
    },
    lineId: {
      handler(val){
        this.willChangeQueryParam.lineId = val;
      },
      immediate:true
    },
    defaultCheckedKeys: {
      immediate: true,
      handler(id) {
        this.queryParam.warehouseId = id;
      },
    },
    //工班
    "willChangeQueryParam.groupId"(newVal, oldVal) {
      if (newVal) {
        this.dictCodeStrWork = `bu_mtr_workstation,name,id,id in (select workstation_id from bu_group_workstation where group_id='${newVal}')`;
      } else {
        this.dictCodeStrWork = "bu_mtr_workstation,name,id";
      }
    },
  },
  data() {
    /*const numValid = ({ cellValue, row }) => {
      return new Promise((resolve, reject) => {
        if (cellValue > row.amount) {
          reject(new Error('数量已超出当前库存量'))
        } else if (cellValue < 1) {
          reject(new Error('数量不能小于1'))
        } else {
          resolve()
        }
      })
    }*/
    return {
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" +
        this.$store.getters.userInfo.workshopId +
        "'|workshop_id",
      dictCodeStrWork: "bu_mtr_workstation,name,id",
      queryParam: {
        type: "1",
        category1: "",
        searchText: "",
        attr: null,
        warehouseId: "",
        pageNo: 1,
        pageSize: 10,
      },
      willChangeQueryParam: {
        searchText: "",
        lineId: "",
        repairProgramId: "",
        sysId: "",
        groupId: "",
        workstationId: "",
        pageNo: 1,
        pageSize: 10,
      },
      allAlign: "center",
      totalResult: 0,
      tableData: [],
      visible: false,
      confirmLoading: false,
      /*validRules: {
        num: [{ validator: numValid }],
      },*/
      formItemLayout: {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 18 },
        },
      },
    };
  },
  methods: {
    showModal() {
      this.visible = true;
      this.findList();
    },
    findList() {
      this.loading = true;
      let obj = "";
      obj = getWillChangeList(this.willChangeQueryParam);
      obj.then((res) => {
        this.totalResult = res.result.total;
        this.loading = false;
        this.tableData = res.result.records;
        this.tableData.forEach((item) => {
          this.$set(item, "amount", item.needAmount);
        });
      });
    },
    handlePageChange({ currentPage, pageSize }) {
      this.willChangeQueryParam.pageNo = currentPage;
      this.willChangeQueryParam.pageSize = pageSize;
      this.findList();
    },
    handleOk() {
      let selectRecords = [];
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords();
      } else {
        if (this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord());
        }
      }
      // let temps = []
      // selectRecords.forEach((item) => {
      //     item.category1 = 1
      //     item.category1_dictText = '必换件'
      //     item.id = item
      //     let obj = Object.assign({}, item)
      //     obj.id = obj.materialTypeId
      //     temps.push(obj)
      // })
      // selectRecords = temps
      // console.log(selectRecords);
      this.$emit("ok", selectRecords);
      this.visible = false;
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
<style scoped>
.limitTitle {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
  border-bottom: 1px solid #e1e1de;
}
.limitHeight {
  height: calc(80vh - 110px);
  overflow-y: auto;
}
</style>
