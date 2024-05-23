<template>
  <div>
    <a-modal
      title="消耗核实"
      width="50%"
      :visible="visible"
      @ok="handleOk"
      @cancel="cancel"
    >
      <div style="height:500px">
        <vxe-table
          border
          resizable
          show-overflow
          keep-source
          ref="xTable1"
          height="auto"
          :data="tableData"
          :edit-rules="validRules"
          :edit-config="{ trigger: 'manual', mode: 'row', autoClear: false }"
        >
          <vxe-table-column type="seq" width="60"></vxe-table-column>
          <vxe-table-column field="tradeNo" title="批次" />
          <vxe-table-column
            field="actAmount"
            title="实际消耗"
            :edit-render="{ name: '$input', props: { type: 'number' } }"
          ></vxe-table-column>
          <vxe-table-column
            field="price"
            title="价格"
          ></vxe-table-column>
          <vxe-table-column title="操作" align="center" width="160">
            <template v-slot="{ row, rowIndex }">
              <template v-if="$refs.xTable1.isActiveByRow(row)">
                <a-space>
                  <a-button @click="saveRowEvent(row, rowIndex)">保存</a-button>
                  <a-button @click="cancelRowEvent(row)">取消</a-button>
                </a-space>
              </template>
              <template v-else>
                <a-button @click="editRowEvent(row)">编辑</a-button>
              </template>
            </template>
          </vxe-table-column>
        </vxe-table>
      </div>
    </a-modal>
  </div>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      isEdit: false,
      validRules: {
        actAmount: [{ required: true, message: "请输入消耗数量" }],
      },
      copy: {},
      tableData: [],
    };
  },
  methods: {
    show(row) {
      console.log(row);
      this.copy = JSON.parse(JSON.stringify(row));
      this.tableData = JSON.parse(JSON.stringify(row.actList));
      this.visible = true;
    },
    handleOk() {
      if (!this.isEdit) {
        this.$emit("ok", this.tableData);
        this.visible = false;
      } else {
        this.$message.warning("请先保存或者取消正在编辑的物资核实");
      }
      
    },
    cancel() {
      this.visible = false;
      this.isEdit = false;
      this.$emit("cancel", this.copy);
    },
    saveRowEvent(row, rowIndex) {
      console.log(rowIndex);
      this.$refs.xTable1.validate(row).then(() => {
        this.$refs.xTable1.clearActived();
        this.isEdit = false;
      });
      console.log(row);
    },
    cancelRowEvent(row) {
      const xTable1 = this.$refs.xTable1;
      xTable1.clearActived().then(() => {
        // 还原行数据
        xTable1.revertData(row);
        this.isEdit = false;
      });
    },
    editRowEvent(row) {
      if (!this.isEdit) {
        this.$refs.xTable1.setActiveRow(row);
        this.isEdit = true;
      } else {
        this.$message.warning("请先保存或者取消正在编辑的物资核实");
      }
    },
  },
};
</script>

<style lang="less" scoped></style>
