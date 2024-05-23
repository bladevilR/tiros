<template>
  <a-modal
    title="查看"
    :width="'100%'"
    height="auto"
    :visible="visible"
    dialogClass="fullDialog"
    :footer="null"
    @cancel="handleCancel"
    :destroyOnClose="true"
    :zIndex="50"
  >
    <div class="modalBox">
      <a-card id="trainAssetRightContent">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :md="5" :sm="24">
                <a-form-item label="名称">
                  <a-input
                    placeholder="名称或编码"
                    v-model="queryParam.searchText"
                  ></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="24">
                <a-form-item label="线路">
                  <j-dict-select-tag
                    :triggerChange="true"
                    v-model="queryParam.lineId"
                    dictCode="bu_mtr_line,line_name,line_id"
                    @change="changeLine"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="24">
                <a-form-item label="车辆">
                  <j-dict-select-seach-tag
                    :triggerChange="true"
                    v-model="queryParam.trainNo"
                    :dictCode="dictTrainStr"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="5" :sm="24">
                <a-form-item label="状态">
                  <j-dict-select-tag
                    v-model="queryParam.status"
                    dictCode="bu_effective"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="4" :sm="24">
                <span
                  style="float: left; overflow: hidden"
                  class="table-page-search-submitButtons"
                >
                  <a-space>
                    <a-button @click="searchQuery">查询</a-button>

                    <a-button
                      :disabled="records.length != 1"
                      @click="handleEdit(records[0])"
                      >编辑</a-button
                    >
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
            <vxe-table-column title="车号" field="trainNo" width="100"></vxe-table-column>
            <vxe-table-column
              align="left"
              header-align="center"
              field="name"
              title="设备名称"
              width="20%"
              tree-node
            ></vxe-table-column>
            <vxe-table-column
              field="code"
              title="设备编码"
              width="10%"
              align="left"
              header-align="center"
            ></vxe-table-column>
            <vxe-table-column title="车厢" width="10%" align="left" header-align="center">
              <template v-slot="{ row }">
                <div>{{ row.ext.carriage }}</div>
              </template>
            </vxe-table-column>
            <vxe-table-column
              field="locationName"
              title="位置"
              width="20%"
              align="left"
              header-align="center"
            ></vxe-table-column>
            <vxe-table-column title="系统" width="10%" align="left" header-align="center">
              <template v-slot="{ row }">
                <div>{{ row.ext.systemName }}</div>
              </template>
            </vxe-table-column>
            <vxe-table-column
              title="设备类型"
              width="10%"
              align="left"
              header-align="center"
            >
              <template v-slot="{ row }">
                <div>{{ row.ext.assetTypeName }}</div>
              </template>
            </vxe-table-column>
            <vxe-table-column
              title="资产编码"
              width="10%"
              align="left"
              header-align="center"
            >
              <template v-slot="{ row }">
                <div>{{ row.ext.assetCode }}</div>
              </template>
            </vxe-table-column>
            <vxe-table-column
              title="出厂编码"
              width="10%"
              align="left"
              header-align="center"
            >
              <template v-slot="{ row }">
                <div>{{ row.ext.manufNo }}</div>
              </template></vxe-table-column
            >
            <vxe-table-column field="status_dictText" title="设备标签" width="10%">
              <template v-slot="{ row }">
                <div>{{ row.ext.tagCode }}</div>
              </template>
            </vxe-table-column>
          </vxe-table>
        </div>
      </a-card>
    </div>
    <maximoAssetEdit ref="maximoAssetEdit" @ok="findList" />
  </a-modal>
</template>

<script>
import maximoAssetEdit from "./maximoAssetEdit.vue";
import { getTrainAssetListNew } from "@/api/tirosApi";
export default {
  name: "TrainAssetNew",
  components: { maximoAssetEdit },
  data() {
    return {
      visible: false,
      records: [],
      queryParam: {
        lineId: "",
        parentId: "",
        searchText: "",
        status: "",
        trainNo: "",
      },
      allAlign: "center",
      tableData: [],
      dictTrainStr: "",
    };
  },
  methods: {
    show(value) {
      console.log(value);
      this.visible = true;
      this.queryParam.trainNo = value.trainId;
      this.queryParam.lineId = value.lineId;
      this.findList();
    },
    changeLine(data, edit) {
      console.log(data);
      this.dictTrainStr = "";
      /* this.dictTrainSys = ''*/
      /*if (edit !== true) this.faultModel.sysId = ''*/
      if (data) {
        this.dictTrainStr =
          "bu_train_info,train_no,train_no,line_id=" + data + "|train_struct_id";
        /*this.dictTrainSys = 'bu_train_asset_type,name,id,struct_type=1 and parent_id is null and train_type_id=' +
              '(select train_type_id from bu_mtr_line where line_id=' + data + ' )'*/
      }
    },
    // 关闭
    handleCancel() {
      this.close();
    },
    close() {
      this.$emit("close");
      this.visible = false;
    },
    checkboxChange(e) {
      this.records = e.records;
    },
    findList() {
      getTrainAssetListNew(this.queryParam).then((res) => {
        if (res.success) {
          this.tableData = res.result;
          this.records = [];
        }
      });
    },
    loadChildrenMethod({ row }) {
      let param = {
        lineId: this.queryParam.lineId,
        trainNo: this.queryParam.trainNo,
        parentId: row.id,
        searchText: this.queryParam.searchText,
        status: this.queryParam.status,
      };
      return new Promise((resolve) => {
        getTrainAssetListNew(param).then((res) => {
          if (res.success) {
            resolve(res.result);
          } else {
            this.$message.error(`${res.msg}`);
          }
        });
      });
    },

    searchQuery() {
      this.findList();
    },
    handleEdit(record) {
      this.$refs.maximoAssetEdit.showModal(record)
    },
  },
};
</script>

<style scoped lang="less">
.modalBox {
  display: flex;
  justify-content: space-between;
  height: calc(100vh - 68px);
  width: 100%;
  #trainAssetRightContent {
    width: 100%;
    height: 100%;
    /deep/.ant-card {
      width: 100%;
    }
    .ant-card-body {
      padding: 10px;
      height: 100%;
      overflow-y: auto;
    }

    .tableHeight {
      // min-height: calc(100vh - 255px);
      height: calc(100vh - 174px);
      // overflow-y: auto;
    }
  }
}
</style>
