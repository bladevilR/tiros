<template>
  <a-card id="trainAssetRightContent">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="名称或编码" v-model="queryParam.title"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag v-model="queryParam.status" dictCode="bu_effective" />
            </a-form-item>
          </a-col>
          <!--          <a-col :md="5" :sm="24">-->
          <!--            <a-form-item label="上级">-->
          <!--              <a-tree-select-->
          <!--                tree-data-simple-mode-->
          <!--                allow-clear-->
          <!--                :tree-data="selectTreeNode"-->
          <!--                placeholder="请选择"-->
          <!--                :load-data="loadSelectNodeMethod"-->
          <!--                v-model="queryParam.id"-->
          <!--              />-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
          <a-col :md="10" :sm="24">
            <span
              style="float: left; overflow: hidden"
              class="table-page-search-submitButtons"
            >
              <a-space>
                <a-button @click="searchQuery">查询</a-button>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button :disabled="records.length != 1" @click="handleEdit(records[0])">编辑</a-button>
                <a-button :disabled="records.length < 1" @click="handleDel">删除</a-button>
                <a-button @click="importStructure" :loading="loading">导入创建</a-button>
                <!-- <a-button @click="handleBack">关闭</a-button> -->
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="tableHeight">
      <vxe-table
        border
        resizable
        row-id="id"
        ref="listTable"
        :align="allAlign"
        height="auto"
        :data="tableData"
        show-overflow="tooltip"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :checkbox-config="{ trigger: 'row', highlight: true, checkStrictly: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        :tree-config="{
          lazy: true,
          children: 'children',
          hasChild: 'hasChildren',
          loadMethod: loadChildrenMethod,
        }"
      >
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column title="车号" width="100">
          <template>
            <div>
              {{value.trainId}}
            </div>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="assetName"
          title="车厢"
          tree-node
          width="20%"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column field="placeDesc" title="位置" width="14%" align="left" header-align="center"></vxe-table-column>
        <vxe-table-column title="系统" width="14%" align="left" header-align="center"></vxe-table-column>
        <vxe-table-column field="assetNo" title="编码" width="14%" align="left" header-align="center"></vxe-table-column>
        <vxe-table-column
          field="status_dictText"
          title="状态"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="structType_dictText"
          title="类型"
          width="10%"
        ></vxe-table-column>
        <vxe-table-column
          field="assetTypeName"
          title="设备类型"
          width="13%"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="assetCode"
          title="资产编码"
          width="13%"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="manufNo"
          title="出厂编码"
          width="13%"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          title="设备标签"
          width="13%"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="placeId"
          title="所属位置"
          width="10%"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="placeDesc"
          title="位置描述"
          width="10%"
          align="left"
          header-align="center"
        ></vxe-table-column>
      </vxe-table>
    </div>

    <train-asset-type
      ref="modalForm"
      :trainTypeId="trainTypeId"
      :pid="selectPid"
      :multiple="true"
      @ok="importTarget"
    ></train-asset-type>
    <train-sys-item-modal
      ref="sysModalForm"
      :trainId="trainId"
      :trainTypeId="trainTypeId"
      :parent="selectParent"
      @ok="addTarget"
    ></train-sys-item-modal>
  </a-card>
</template>

<script>
import TrainAssetType from "../../../common/selectModules/TrainAssetType";
import TrainSysItemModal from "./TrainSysItemModal";
import { delTrainAsset, getTrainAssetList, importTrainAsset } from "@/api/tirosApi";

export default {
  components: { TrainAssetType, TrainSysItemModal },
  name: "TrainInfo",
  props: ["value"],
  data() {
    return {
      records:[],
      trainId: "",
      trainTypeId: "",
      selectParent: {},
      queryParam: {
        code: "",
      },
      allAlign: "center",
      tableData: [],
      selectTreeNode: [],
      selectPid: "",
      loading: false,
    };
  },
  watch: {
    value: {
      immediate: true,
      handler(value) {
        if (value) {
          this.trainId = value.trainId;
          this.trainTypeId = value.trainTypeId;
          this.queryParam.code = value.trainId;
        }
        // this.findSelectList()
        this.findList();
      },
    },
  },
  created() {
    // this.findSelectList()
    // this.findList()
  },
  methods: {
    checkboxChange(e) {
      this.records = e.records;
    },
    findList() {
      getTrainAssetList(this.queryParam).then((res) => {
        if (res.success) {
          this.loading = false;
          this.tableData = res.result;
          this.records = [];
        }
      });
    },
    findSelectList() {
      let param = {
        code: this.trainId,
      };
      getTrainAssetList(param).then((res) => {
        if (res.success) {
          /*this.selectTreeNode = res.result.map((item) => {
            return this.genSelectTreeNode(item)
          })*/
        }
      });
    },

    loadChildrenMethod({ row }) {
      let param = {
        code: this.trainId,
        id: row.id,
        searchText: this.queryParam.searchText,
        status: this.queryParam.status,
      };
      return new Promise((resolve) => {
        getTrainAssetList(param).then((res) => {
          if (res.success) {
            resolve(res.result);
          } else {
            this.$message.error(`${res.msg}`);
          }
        });
      });
    },
    loadSelectNodeMethod(node) {
      let param = {
        code: this.trainId,
        id: node.dataRef.id,
      };
      return new Promise((resolve) => {
        getTrainAssetList(param).then((res) => {
          if (res.success) {
            let childrenNode = res.result.map((item) => {
              this.selectTreeNode = this.selectTreeNode.concat(
                this.genSelectTreeNode(item)
              );
            });
          } else {
            this.$message.error(res.message);
          }
        });
        resolve();
      });
    },

    genSelectTreeNode(node) {
      return {
        id: node.id,
        pId: node.parentId,
        value: node.id,
        title: node.assetName,
        isLeaf: node.hasChildren ? false : true,
      };
    },

    searchQuery() {
      this.findList();
    },

    handleAdd() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords();
      if (selectRecords.length > 0) {
        if (selectRecords.length == 1) {
          this.selectParent = {
            id: selectRecords[0].id,
            name: selectRecords[0].assetName,
          };
        } else {
          this.$message.error("只能选择一个上级节点或者不选!");
        }
      } else {
        this.selectParent = {};
      }
      setTimeout(() => {
        this.$refs.sysModalForm.add();
        this.$refs.sysModalForm.title = "新增";
      }, 0);
    },
    handleEdit(record) {
      this.$refs.sysModalForm.edit(record);
      this.$refs.sysModalForm.title = "编辑";
    },
    addTarget() {
      this.loadData();
    },
    handleDel() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords();
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `此操作将删除当前设备及其子级数据，是否继续？`,
          okText: "确定",
          cancelText: "取消",
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id;
              })
              .join(",");
            delTrainAsset({ ids: idsStr }).then((res) => {
              if (res.success) {
                this.$message.success(res.message);
                this.loadData();
              } else {
                this.$message.error(res.message);
              }
            });
          },
        });
      } else {
        this.$message.error("尚未选中任何数据!");
      }
    },
    importStructure() {
      if (!this.value || !this.value.trainId) {
        this.$message.error("请选择要的导入的车辆!");
        return;
      }
      let selectRecords = this.$refs.listTable.getCheckboxRecords();
      if (selectRecords.length === 0) {
        this.$refs.modalForm.showModal();
      } else if (selectRecords.length === 1) {
        this.selectPid = selectRecords[0].id;
        this.$refs.modalForm.showModal();
      } else {
        this.$message.error("只能在一个设备下进行导入，不要选择多个设备!");
      }
    },
    importTarget(data) {
      let idsStr = data
        .map(function (obj, index) {
          return obj.id;
        })
        .join(",");
      let param = {
        childId: idsStr,
        parentId: this.selectPid,
        trainNo: this.trainId,
      };
      this.loading = true;
      importTrainAsset(param)
        .then((res) => {
          if (res.success) {
            this.$message.success(res.message);
            this.loadData();
          } else {
            this.$message.warning(res.message);
          }
        })
        .finally(() => (this.loading = false));
    },
    loadData() {
      this.queryParam.id = "";
      // this.findSelectList()
      this.findList();
    },
    handleBack() {
      //this.$router.go(-1)
      this.$emit("close");
    },
  },
};
</script>

<style lang="less">
#trainAssetRightContent {
  height: 100%;
  .ant-card-body {
    padding: 10px;
    height: 100%;
    overflow-y: auto;
  }

  .tableHeight {
    // min-height: calc(100vh - 255px);
    height: calc(100% - 70px);
    // overflow-y: auto;
  }
}
</style>
