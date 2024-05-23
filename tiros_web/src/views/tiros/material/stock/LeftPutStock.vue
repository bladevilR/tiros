<template>
  <a-card id="leftPutStock">
    <div class="typeTitle">
      <a-icon type="database" style="margin-right: 5px" />
      仓库选择
    </div>
    <div class="btn">
      <a-button @click="syncStock">同步物资库存数据</a-button>
    </div>
    <div style="width: 80%; margin: 0 auto">
      <a-tree
        v-if="treeData.length"
        :tree-data="treeData"
        :replaceFields="{ title: 'name', key: 'id' }"
        :defaultSelectedKeys="defaultSelectedKeys"
        @select="selectPid"
        :load-data="onLoadData"
      />
    </div>
  </a-card>
</template>
<script>
import { listWarehouseChildren } from "@api/tirosMaterialApi";
import { ACCESS_TOKEN } from "@/store/mutation-types";
import axios from "axios";
import Vue from "vue";
export default {
  name: "LeftPutStock",
  props: ["value"],
  data() {
    return {
      defaultSelectedKeys: [],
      treeData: [],
      queryParam: {
        parentId: "",
      },
      topId: "",
    };
  },
  mounted() {
    this.queryTreeData();
    this.queryTopId();
  },
  methods: {
    syncStock() {
      this.$confirm({
        // title: 'Do you want to delete these items?',
        content: "确定要同步物资库存数据吗？",
        onOk: () => {
          // 开启动画
          this.$emit("change", true);
          const token = Vue.ls.get(ACCESS_TOKEN);
          const url = window._CONFIG["syncMaximo"] + "/third/maximo/read/material-stock";
          axios({
            url: url,
            // headers: { "X-Access-Token": token },
            method: "GET",
          })
            .then((res) => {
              if (res.data.success) {
                this.$message.warn("同步成功，注意只同步了3级库位库存数量，4级库位需要自己调整哦！！！");
                // 刷新右侧列表
                this.$emit("ok", false);
              }
              // 关闭加载中动画
              this.$emit("change", false);
            })
            .catch((err) => {
              this.$emit("change", false);
              this.$notification.error({
                message: "服务器出错了",
                description: "Network Error",
                duration: 4,
              });
            });
        },
        onCancel() {},
      });
    },
    queryTopId() {
      listWarehouseChildren({}).then((res) => {
        if (res.success) {
          if (res.result.length > 0) {
            this.topId = res.result[0].id;
          }
        }
      });
    },
    queryTreeData() {
      listWarehouseChildren(this.queryParam).then((res) => {
        if (res.success) {
          this.treeData = res.result;
          this.defaultSelectedKeys[0] = res.result[0].id;
          this.$emit("input", this.defaultSelectedKeys[0]);
        }
      });
    },
    onLoadData(treeNode) {
      let param = {
        parentId: treeNode.dataRef.id,
      };
      return new Promise((resolve) => {
        listWarehouseChildren(param).then((res) => {
          if (res.success) {
            treeNode.dataRef.children = res.result;
            treeNode.dataRef.children.map((item) => {
              item.isLeaf = !item.hasChildren;
            });
            this.treeData = [...this.treeData];
          } else {
            this.$message.warning(res.message);
          }
          resolve();
        });
      });
    },
    selectPid(selectedKeys, e) {
      let selectedKey = selectedKeys[0];
      if (!selectedKey) {
        selectedKey = this.topId;
      }
      this.$emit("input", selectedKey);
    },
  },
};
</script>
<style lang="less">
#leftPutStock {
  .ant-card-body {
    padding: 0 0 24px 0;
    height: calc(100vh - 120px);
    overflow-y: auto;
  }

  .typeTitle {
    background: #eee;
    padding: 15px;
    text-align: center;
  }

  .btn {
    padding: 10px 0;
    text-align: center;
    width: 100%;
  }
}
</style>
