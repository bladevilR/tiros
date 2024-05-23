<template>
  <a-modal
    title="选择作业项目"
    width="80%"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="12">
          <a-col :md="7" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="名称" v-model="queryParam.title" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="24">
            <a-form-item label="规程">
              <a-select
                show-search
                allowClear
                placeholder="Select a person"
                option-filter-prop="children"
                v-model="queryParam.reguId"
                :filter-option="filterOption"
              >
                <a-select-option :value="undefined"> 请选择 </a-select-option>
                <a-select-option :value="item.id" v-for="(item, k) in treeData" :key="k">
                  {{ item.title }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="tableHeight" style="height: calc(100vh - 350px)">
      <vxe-table
        border
        resizable
        row-id="id"
        ref="listTableRadio"
        align="center"
        :data="tableData"
        show-overflow="tooltip"
        max-height="100%"
        style="auto"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        :radio-config="{ trigger: 'row', highlight: true, checkMethod: checkMethod }"
        :tree-config="{
          lazy: true,
          children: 'children',
          hasChild: 'hasChildren',
          loadMethod: loadChildrenMethod,
        }"
        :keep-source="true"
      >
        <vxe-table-column type="radio" width="40"></vxe-table-column>
        <vxe-table-column field="no" title="序号" width="60"></vxe-table-column>
        <vxe-table-column
          field="title"
          title="名称"
          tree-node
          align="left"
          header-align="center"
          min-width="140"
        ></vxe-table-column>
        <vxe-table-column
          field="type_dictText"
          title="类型"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="method_dictText"
          title="维保手段"
          width="100"
        ></vxe-table-column>
        <vxe-table-column
          field="qualityLevel_dictText"
          title="质保等级"
          width="80"
        ></vxe-table-column>
        <vxe-table-column
          field="outsource_dictText"
          title="是否委外"
          width="80"
        ></vxe-table-column>
        <vxe-table-column
          field="safeNotice"
          title="安全提示"
          min-width="140"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="remark"
          title="备注"
          min-width="120"
          align="left"
          header-align="center"
        ></vxe-table-column>
      </vxe-table>
    </div>
  </a-modal>
</template>

<script>
import { getReguDeteil, getReguList, importTechBook } from "@/api/tirosApi";
export default {
  data() {
    return {
      visible: false,
      selectIds: [],
      queryParam: {
        reguId: undefined,
        title: "",
      },
      tableData: [],
      treeData: [],
    };
  },
  methods: {
    show(id, ids) {
      if (id) this.queryParam.reguId = id;
      this.visible = true;
      this.selectIds = ids;
      console.log(ids);
      this.queryTreeData();
      this.findList();
    },
    queryTreeData() {
      getReguList().then((res) => {
        if (res.success) {
          let _result = res.result;
          Array.from(_result, (item, index) => {
            //将类数组对象或可迭代对象转化为数组。
            item.title = item.name && JSON.parse(JSON.stringify(item.name));
            if (item.version) {
              item.title += `.${item.version}`;
            }
          });
          this.treeData = _result;
          console.log(_result);
        }
      });
    },
    checkMethod({ row }) {
      return row.type == 2;
    },
    filterOption(input, option) {
      return (
        option.componentOptions.children[0].text
          .toLowerCase()
          .indexOf(input.toLowerCase()) >= 0
      );
    },
    findList() {
      this.loading = true;
      // if(this.queryParam.reguId){
      getReguDeteil(this.queryParam).then((res) => {
        if (res.success) {
          this.loading = false;
          this.tableData = res.result;
        }
      });
      // }
    },
    handleOk() {
      const data = this.$refs.listTableRadio.getRadioRecord();
      if(!data){
        this.$message.warning('请选择作业项目');
        return;
      }
      this.$confirm({
        content: "导入操作将覆盖原有的指导书数据，确认继续？",
        onOk: () => {
          const params = {
            targetReguItemId: data.id,
            toReguItemIds: this.selectIds,
          };
          return importTechBook(params).then((res) => {
            if (res.success) {
              this.$message.success(res.message);
              this.$emit("ok");
              this.handleCancel();
            } else {
              this.$message.warning(res.message);
            }
          })
        }
      });
    },
    handleCancel() {
      this.visible = false;
      this.selectIds = [];
      Object.assign(this.queryParam, this.$options.data().queryParam);
    },
    loadChildrenMethod({ row }) {
      console.log(row);
      let param = {
        reguId: this.queryParam.reguId,
        parentId: row.id,
      };
      return new Promise((resolve) => {
        getReguDeteil(param).then((res) => {
          if (res.success) {
            if (!res.result.length) {
              row.hasChildren = false;
            }
            resolve(res.result);
          } else {
            this.$message.error(`${res.msg}`);
          }
        });
      });
    },
  },
};
</script>

<style lang="scss" scoped></style>
