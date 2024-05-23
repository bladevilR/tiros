<template>
  <div>
    <a-form v-if="!readOnly" layout="inline">
      <a-row :gutter="24">
        <a-col :md="6" :sm="8">
          <a-form-item>
            <a-space>
              <a-button type="dashed" class="primary-color" @click="handleAdd"
                >添加</a-button
              >
              <a-button type="dashed" @click="handleDel">删除</a-button>
            </a-space>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <vxe-table
      border
      ref="listTable"
      :align="allAlign"
      :keep-source="true"
      :data.sync="mustReplaces"
      :edit-rules="validRules"
      :edit-config="{
        key: 'id',
        trigger: 'manual',
        mode: 'row',
        autoClear: false,
        showStatus: true,
      }"
      :checkbox-config="{ trigger: 'row', highlight: true }"
    >
      <vxe-table-column type="checkbox" width="40"></vxe-table-column>
      <vxe-table-column
        field="code"
        title="编码"
        width="150"
        align="left"
        :edit-render="{ name: 'input' }"
        header-align="center"
      >
        <template v-slot:edit="{ row }">
          <a-select
            ref="formSelect"
            v-model="row.code"
            placeholder="请选择必换件"
            :open="false"
            style="width: 100%"
            @focus="openMaterialSelectModal(row)"
          >
            <a-icon
              slot="suffixIcon"
              type="ellipsis"
              @click="openMaterialSelectModal(row)"
            />
          </a-select>
        </template>
      </vxe-table-column>
      <vxe-table-column
        field="name"
        title="名称"
        align="left"
        header-align="center"
      ></vxe-table-column>
      <vxe-table-column
        :edit-render="{ name: '$input',props:{type:'number'} }"
        field="amount"
        title="数量"
        width="150"
      ></vxe-table-column>
      <vxe-table-column field="unit" title="单位"></vxe-table-column>
      <!-- <vxe-table-column field="structType_dictText" title="类型"></vxe-table-column> -->
      <vxe-table-column
        field="spec"
        title="必换件描述"
        align="left"
        header-align="center"
      ></vxe-table-column>
      <vxe-table-column title="操作" width="150" v-if="!readOnly">
        <template v-slot="{ row }">
          <template v-if="$refs.listTable.isActiveByRow(row)">
            <a-space>
              <a-button type="dashed" size="small" @click.stop="saveRowEvent(row)"
                >保存</a-button
              >
              <a-button type="dashed" size="small" @click.stop="cancelRowEvent(row)"
                >取消</a-button
              >
            </a-space>
          </template>
          <template v-else>
            <a-button type="dashed" size="small" @click.stop="editRowEvent(row)"
              >编辑</a-button
            >
            <!-- <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
          </template>
        </template>
      </vxe-table-column>
    </vxe-table>
    <must-material
      ref="modalFormRadio"
      :lineId="lineId"
      :repairProId="repairProId"
      :multiple="false"
      @ok="addMustReplacesRadio"
    ></must-material>
    <must-material
      ref="modalForm"
      :lineId="lineId"
      :repairProId="repairProId"
      :multiple="true"
      @ok="addMustReplaces"
    ></must-material>
  </div>
</template>

<script>
import MustMaterial from "@views/tiros/common/selectModules/MustMaterial";
import { randomUUID } from "@/utils/util";
export default {
  name: "TaskMustReplace",
  components: { MustMaterial },
  props: {
    repairProId: {
      default: "",
    },
    lineId: {
      default: "",
    },
    mustReplaces: {
      type: Array,
      default: [],
    },
    // reguId: {
    //   type: String,
    //   default: ''
    // },
    readOnly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      allAlign: "center",
      validRules: {
        code: [{ required: true, message: "请选必换件" }],
        name: [{ required: true, message: "请选择必换件" }],
        amount: [{ required: true, message: "数量必须填写" }],
      },
    };
  },
  computed: {
    plan() {
      return this.$store.getters.planForm;
    },
  },
  methods: {
    saveRowEvent(row) {
      this.$refs.listTable.validate(row, (valid) => {
        if (!valid) {
          if (this.curEditMode === 1) {
            this.mustReplaces.push(row);
          }
          this.$refs.listTable.clearActived();
          this.curEditMode = 0;
        } else {
          for (const validKey in valid) {
            let vals = valid[validKey];
            vals.forEach((item) => {
              if (item.rule) {
                this.$message.error(item.rule.message);
              }
            });
          }
        }
      });
    },
    cancelRowEvent(row) {
      this.$refs.listTable.clearActived();
      if (this.curEditMode === 1) {
        // 新增,点击取消
        this.$refs.listTable.remove(row);
      } else if (this.curEditMode === 2) {
        // 还原行数据
        console.log('还原行数据')
        this.$refs.listTable.revertData(row);
      }
      this.curEditMode = 0;
    },
    editRowEvent(row) {
      this.curEditMode = 2;
      this.$refs.listTable.setActiveRow(row);
    },
    handleAdd() {
      this.curEditMode = 1;
      this.$refs.modalForm.showModal();
    },
    openMaterialSelectModal(row) {
      this.curRow = row;
      this.$refs.modalFormRadio.showModal();
      this.$refs.formSelect.blur();
    },
    handleDel() {
      let m = this.$refs.listTable.getCheckboxRecords();
      if (m.length < 1) {
        this.$message.warning("请选择要删除的数据");
        return;
      }
      this.$confirm({
        content: `确认删除选中的数据？`,
        onOk: () => {
          m.map((item1) => {
            this.mustReplaces.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.mustReplaces.splice(index2, 1);
              }
            });
          });
        },
      });
    },
    addMustReplacesRadio(data) {
      if (data && data.length) {
        const material = data[0]
        let flag = 0
        this.mustReplaces.map(f => {
          if (f.mustReplaceId === material.id) {
            flag=1
            return false;
          }
        })

        if (flag === 1) {
          this.$message.warning('你选择的物料已经在列表中存在，请选择其他物料')
          return
        }
        
        let newRow = JSON.parse(JSON.stringify(material))
        newRow.mustReplaceId = newRow.id;
        newRow.id = randomUUID();
        newRow.amount = newRow.needAmount;
        
        // this.curRow = newRow;
        Object.assign(this.curRow, newRow);
      }
    },
    addMustReplaces(data) {
      data.map((item) => {
        let tempIndex = this.mustReplaces.findIndex((m) => {
          return m.mustReplaceId === item.id;
        });
        if (tempIndex < 0) {
          let newRow = JSON.parse(JSON.stringify(item))
          newRow.mustReplaceId = newRow.id;
          newRow.id = randomUUID();
          newRow.amount = newRow.needAmount;

          if(data.length == 1){
            this.$refs.listTable.insertAt(newRow, -1).then(({ row }) => {
              this.$refs.listTable.setActiveCell(row, 'code')
            })
          }else{
            this.$refs.listTable.insertAt(newRow, -1)
            this.mustReplaces.push(newRow);
          }
        } else {
          this.$message.warning(`你选择的${item.name}已经在列表中存在，请选择其他物料`);
        }
      });
    },
  },
};
</script>

<style></style>
