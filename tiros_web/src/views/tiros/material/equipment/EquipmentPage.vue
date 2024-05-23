<template>
  <div class="bodyWrapper">
    <a-spin :spinning="spinningStatus" tip="导出中...">
      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="24">
            <a-col :md="5" :sm="24">
              <a-form-item label="工装名称">
                <a-input
                  placeholder="请输入名称或编码"
                  v-model="queryParam.searchText"
                  allowClear
                ></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="24">
              <a-form-item label="类别">
                <j-dict-select-tag
                  v-model="queryParam.toolType"
                  placeholder="请选择"
                  dictCode="bu_tools_type"
                />
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="24">
              <a-form-item label="状态">
                <j-dict-select-tag
                  v-model="queryParam.status"
                  placeholder="请选择"
                  dictCode="bu_tools_status"
                />
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="24">
              <a-form-item label="班组">
                <j-dict-select-tag
                  v-model="queryParam.groupId"
                  placeholder="请选择"
                  :dictCode="dictGroupStr"
                />
              </a-form-item>
            </a-col>
            <a-col v-if="isComponent" :md="4" :sm="24">
              <a-button @click="findList">查询</a-button>
            </a-col>
          </a-row>
          <a-row v-if="!isComponent">
            <span
              style="float: left; overflow: hidden"
              class="table-page-search-submitButtons"
            >
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button @click="handleAdd" type="primary">新增</a-button>
                <a-button @click="handleEdit" :disabled="!btnStatus.edit">编辑</a-button>
                <a-button @click="deleteRecord" :disabled="!btnStatus.del">删除</a-button>
  
                <a-button
                  @click="handleNextCheck(1)"
                  v-has="'material:nextcheck'"
                  :disabled="!btnStatus.del"
                  >设置上次自检</a-button
                >
                <a-button
                  @click="handleNextCheck(2)"
                  v-has="'material:nextcheck'"
                  :disabled="!btnStatus.del"
                  >设置下次送检</a-button
                >
                <a-button
                  @click="setToolDetail"
                  :disabled="!btnStatus.del"
                  >设置电动工具</a-button
                >
                <a-button
                  @click="handleSelfCheck"
                  v-has="'material:nextcheck'"
                  :disabled="!btnStatus.del"
                  >设置是否自检</a-button
                >
                <a-button
                  @click="handleDutyOfficer"
                  v-has="'material:nextcheck'"
                  :disabled="!btnStatus.del"
                  >设置责任人</a-button
                >
                <a-button @click="$refs.importModal.show()">导入</a-button>
                <a-button @click="exportTools">导出</a-button>
              </a-space>
            </span>
            <span style="float: right;" v-if="!isComponent">
              <p style="text-align: center">
            <a style="color: #ffa30f; font-weight: bold" @click="jumpneedcheck()"
            >有 {{ count }} 个即将送检装备，请点击查看</a
            >
          </p>
            </span>
          </a-row>
        </a-form>
      </div>
      <div :style="`height: calc(100vh - ${isComponent ? '316px' : '265px'})`">
        <vxe-table
          border
          height="100%"
          ref="listTable"
          :align="allAlign"
          :data="tableData"
          show-overflow="tooltip"
          :cell-class-name="cellClassName"
          :edit-config="{ trigger: 'manual', mode: 'row' }"
          :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          @checkbox-change="btnStatus.update()"
          @checkbox-all="btnStatus.update()"
        >
          <vxe-table-column
            type="checkbox"
            width="40"
          ></vxe-table-column>
          <vxe-table-column title="装备名称" width="150" align="left" header-align="center">
            <template v-slot="{ row }">
              <a @click.stop="jumpInfo(row)">{{ row.name }}</a>
            </template>
          </vxe-table-column>

          <vxe-table-column
            field="code"
            title="物料编码"
            width="130"
            align="center"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column
            field="assetCode"
            title="资产编号"
            width="170"
          ></vxe-table-column>
          <vxe-table-column
            field="toolType_dictText"
            title="架大修类别"
            width="100"
          ></vxe-table-column>
          <vxe-table-column
            field="model"
            title="物资描述"
            min-width="140"
            align="left"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column
            field="amount"
            title="数量"
            width="80"
            align="right"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column
            field="nextCheckTime"
            title="下次送检"
            width="120"
          ></vxe-table-column>
          <vxe-table-column field="isOverTime" title="是否逾期" width="100px">
            <template v-slot="{ row }">
              {{ getIsOverTimeStatus(row) }}
            </template>
          </vxe-table-column>
          <vxe-table-column field="isOverTimeDays" title="逾期天数" width="100">
            <template v-slot="{ row }">
              {{ getIsOverTimeDays(row) }}
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="userName"
            title="保管人姓名"
            width="150"
            align="left"
            header-align="center"
          ></vxe-table-column>

          <vxe-table-column
            field="isElectric_dictText"
            title="是否电动工具"
            width="120"
          ></vxe-table-column>
          <vxe-table-column
            field="isFixedAsset_dictText"
            title="是否固定资产"
            width="120"
          ></vxe-table-column>
          <vxe-table-column
            field="storageLocation"
            title="存放位置（库位）"
            width="150"
            align="left"
            header-align="center"
          ></vxe-table-column>

          <vxe-table-column
            field="groupName"
            title="领用班组"
            width="150"
            align="left"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column
            field="entryDate"
            title="入库日期"
            width="120"
          ></vxe-table-column>
          <vxe-table-column
            field="status_dictText"
            title="状态"
            width="80"
          ></vxe-table-column>
          <vxe-table-column
            field="isSelfCheck_dictText"
            title="是否自检?"
            width="90"
          ></vxe-table-column>
          <vxe-table-column
            field="lastSelfCheckTime"
            title="上次自检"
            width="120"
          ></vxe-table-column>

         <vxe-table-column
            field="warehouseName"
            title="库位"
            width="120"
            align="left"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column
            field="entraceDate"
            title="入场日期"
            width="100"
          ></vxe-table-column>
          

          
          <vxe-table-column
            field="sync_dictText"
            title="来自同步?"
            width="90"
          ></vxe-table-column>

          <vxe-table-column
            field="supplierName"
            title="生产厂家"
            width="150"
            align="left"
            header-align="center"
          ></vxe-table-column>

          <vxe-table-column
            field="lifetime"
            title="使用寿命"
            width="100"
            align="center"
            header-align="center"
          ></vxe-table-column>

          <!--        <vxe-table-column title="操作" width="15%" fixed="right">-->
          <!--          <template v-slot="{ row }">-->
          <!--            <a @click="handleEdit(row)">编辑</a>-->
          <!--            <a-divider type="vertical"/>-->
          <!--            <a @click="handleNextCheck(row)">设置下次送检时间</a>-->
          <!--          </template>-->
          <!--        </vxe-table-column>-->
        </vxe-table>
      </div>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
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
      <equipment-item-modal ref="modalForm" @ok="loadData()"></equipment-item-modal>
      <next-check-time-modal ref="nextmodalForm" @ok="loadData()"></next-check-time-modal>
      <self-check-modal ref="selfCheckModalForm" @ok="loadData()"></self-check-modal>
      <a-modal
        title="工装器具详情"
        :width="'90%'"
        centered
        :visible="visible"
        dialogClass="fullDialog no-footer"
        @cancel="visible = false"
        :forceRender="true"
      >
        <tools-manage-info ref="toolsManageInfo"></tools-manage-info>
      </a-modal>
      <user-list
        ref="userSelectModal"
        :dep-id="dispatchGroupId"
        :multiple="false"
        @ok="userSelect"
      ></user-list>
      <material-tools-import ref="importModal" url="/import/materialTools" @ok="loadData()"></material-tools-import>
    </a-spin>
    <a-modal v-model="showSetTool" title="设置电动工具信息" ok-text="确认" cancel-text="取消" :confirmLoading="confirmLoading" @ok="setToolDetailOk">
      <a-form-model :labelCol=" { span: 8 }"
        :wrapperCol=" { span: 16 }">
        <a-form-model-item label="是否电动工具">
          <a-switch v-model="ruleForm.isElectric" />
        </a-form-model-item>
        <a-form-model-item label="是否设置日期">
          <a-switch v-model="ruleForm.updateTime" />
        </a-form-model-item>
        <a-form-model-item v-if="ruleForm.updateTime" label="电动工具效期">
          <a-date-picker v-model="ruleForm.expirDate" />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import {
  getEquipmentList,
  delEquipment,
  getNeedCheckcount,
  setResponsible, 
  exportEquipment, 
  groupStockExport,
  toolSetElectric
} from '@/api/tirosMaterialApi'
import EquipmentItemModal from "../modules/EquipmentItemModal";
import NextCheckTimeModal from "../modules/NextCheckTimeModal";
import ToolsManageInfo from "@views/tiros/group/toolsmanage/ToolsManageInfo";
import SelfCheckModal from "@views/tiros/material/modules/SelfCheckModal";
import TableBtnUtil from "@views/tiros/util/TableBtnUtil";
import UserList from "@views/tiros/common/selectModules/UserList";
import MaterialToolsImport from '@views/tiros/material/modules/MaterialToolsImport'
import moment from 'moment'

export default {
  props:{
    isComponent:{
      type: Boolean,
      default: false,
    },
  },
  name: "EquipmentPage",
  components: {
    NextCheckTimeModal,
    EquipmentItemModal,
    ToolsManageInfo,
    SelfCheckModal,
    UserList,
    MaterialToolsImport
  },
  data() {
    return {
      ids:'',
      ruleForm: {
        ids: '',
        isElectric: true,
        updateTime: false,
        expirDate: '',
      },
      showSetTool: false,
      spinningStatus: false,
      confirmLoading: false,
      dispatchGroupId: "",
      visible: false,
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" +
        this.$store.getters.userInfo.workshopId +
        "'|workshop_id",
      queryParam: {
        searchText: "",
        code: "",
        groupId: "",
        status: "",
        toolType: "",
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: "center",
      tableData: [],
      btnStatus: new TableBtnUtil(this, "listTable"),
      count: "",
    };
  },
  created() {
    this.findList();
  },
  methods: {
    setToolDetail(){
      // debugger
      let selectRecords = this.$refs.listTable.getCheckboxRecords();
      if (selectRecords.length > 0) {
        const ids = selectRecords
          .map((item) => {
            return item.id; //条件;
          })
          .join(",");
          this.showSetTool = true;
          Object.assign(this.ruleForm, {
            ids: '',
            isElectric: true,
            updateTime: false,
            expirDate: '',
          });
          this.ruleForm.ids = ids
      }
      
    },
    setToolDetailOk(){
      console.log(this.ruleForm)
      this.confirmLoading = true;
      toolSetElectric({
        ...this.ruleForm,
        isElectric: Number(this.ruleForm.isElectric),
        updateTime: Number(this.ruleForm.updateTime),
        expirDate: this.ruleForm.updateTime? moment(this.ruleForm).format('YYYY-MM-DD') : null
      }).then((res)=>{
        this.confirmLoading = false;
        if(res.success){
          this.$message.success('设置成功！')
          this.showSetTool = false;
          this.findList();
        }else{
          this.$message.warning(res.message)
        }
      }).catch((err) => {
        this.confirmLoading = false;
      });
    },
    exportTools(){
      let param={
        searchText:this.queryParam.searchText,
        groupId:this.queryParam.groupId,
        status:this.queryParam.status,
        toolType:this.queryParam.toolType
      }
      this.spinningStatus = true;
      exportEquipment("工器具导出.xls",param).then((res)=>{
        this.spinningStatus = false;
      }).catch((err) => {
        this.spinningStatus = false;
      });
    },
    findList() {
      this.loading = true;
      getEquipmentList(this.queryParam).then((res) => {
        this.totalResult = res.result.total;
        this.loading = false;
        this.tableData = res.result.records;
        this.btnStatus.update();
      });
      getNeedCheckcount().then((res) => {
        this.count = res.result;
      });
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage;
      this.queryParam.pageSize = pageSize;
      this.findList();
    },
    loadData() {
      this.findList();
    },
    handleAdd() {
      this.$refs.modalForm.add();
      this.$refs.modalForm.title = "新增";
    },
    handleEdit() {
      // console.log(record)
      let record = this.$refs.listTable.getCheckboxRecords()[0];
      if(record.sync === 1){
        this.$message.warning('只能修改非同步的工装器具')
        return;
      }
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "编辑";
    },
    handleNextCheck(type) {
      let selectRecords = this.$refs.listTable.getCheckboxRecords();
      if (selectRecords.length > 0) {
        const ids = selectRecords
          .map((item) => {
            return item.id; //条件;
          })
          .join(",");
        let record = {
          type: type,
          ids: ids,
          nextCheckTime: selectRecords.length === 1 ? selectRecords[0].nextCheckTime : "",
          lastSelfCheckTime:
            selectRecords.length === 1 ? selectRecords[0].lastSelfCheckTime : "",
        };
        if (type === 1) {
          this.$refs.nextmodalForm.title = "设置上次自检时间";
          this.$refs.nextmodalForm.labelText = "上次自检时间";
        } else {
          this.$refs.nextmodalForm.title = "设置下次送检时间";
          this.$refs.nextmodalForm.labelText = "下次送检时间";
        }
        this.$refs.nextmodalForm.next(record);
      } else {
        this.$message.warning("尚未选中任何数据!");
      }
    },
    handleSelfCheck() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords();
      if (selectRecords.length > 0) {
        this.$refs.selfCheckModalForm.selfCheck(selectRecords);
        this.$refs.selfCheckModalForm.title = "设置自检";
      } else {
        this.$message.warning("尚未选中任何数据!");
      }
    },
    handleDutyOfficer() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords();
      if (selectRecords.length > 0) {
        this.ids = selectRecords
          .map((item) => {
            return item.id; //条件;
          })
          .join(",");
        this.$refs.userSelectModal.showModal();
      } else {
        this.$message.warning("尚未选中任何数据!");
      }
    },
    userSelect(data) {
      console.log(data);
      if(data.length>0){
        setResponsible(`ids=${this.ids}&userId=${data[0].id}`).then((res)=>{
          if(res.success){
            this.$message.success('设置成功');
            this.findList();
          }else{
            this.$message.warning(res.message);
          }
        })
      }else{
        this.$message.warning("未选择人员");
      }
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords();
      if (selectRecords.length > 0) {
        if(selectRecords.filter((item) => item.sync === 1).length){
          this.$message.warning('只能删除非同步的工装器具')
          return;
        }
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id;
              })
              .join(",");
            delEquipment("ids=" + idsStr).then((res) => {
              this.findList();
              this.$message.success(res.message);
            });
          },
        });
      } else {
        this.$message.warning("尚未选中任何数据!");
      }
    },
    jumpInfo(row) {
      // alert(row);
      this.visible = true;
      this.$refs.toolsManageInfo.show({ id: row.id });
    },
    jumpneedcheck() {
      this.$router.push({ path: `/tiros/material/equipment/needcheck` });
    },
    getIsOverTimeStatus(row) {
      if (row.nextCheckTime) {
        let dateNow = this.$moment(new Date()).format("YYYY-MM-DD");
        let diffDay = this.$moment(row.nextCheckTime).diff(dateNow, "day");
        return diffDay < 0 ? "是" : "否";
      } else {
        return "否";
      }
    },
    // 逾期数据单元格样式设定
    cellClassName({ row, rowIndex, column, columnIndex }) {
      let columnsArr = ["nextCheckTime", "isOverTime", "isOverTimeDays"];
      if (columnsArr.includes(column.property)) {
        if (row.nextCheckTime) {
          let dateNow = this.$moment(new Date()).format("YYYY-MM-DD");
          let diffDay = this.$moment(row.nextCheckTime).diff(dateNow, "day");

          if (column.property === "nextCheckTime") {
            if (diffDay <= 0) {
              return 'table-cell-bg-red'
            }
            if (diffDay > 0 && diffDay <= 15) {
              return 'table-cell-bg-yellow'
            }
          }

          if (column.property === "isOverTime") {
            if (diffDay < 0) {
              return "table-cell-bg-red";
            }
          }
          if (column.property === "isOverTimeDays") {
            if (diffDay < 0) {
              return "table-cell-bg-red";
            }
          }
        }
      }
    },
    // 计算逾期天数
    getIsOverTimeDays(row) {
      if (row.nextCheckTime) {
        let dateNow = this.$moment(new Date()).format("YYYY-MM-DD");
        let diffDay = this.$moment(row.nextCheckTime).diff(dateNow, "day");
        if (diffDay < 0) {
          return Math.abs(diffDay);
        }
      }
      return "";
    },
  },
};
</script>

<style scoped></style>
