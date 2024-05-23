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
      align="center"
      :data.sync="taskTools"
      :edit-rules="validRules"
      :keep-source="true"
      :checkbox-config="{ trigger: 'row', highlight: true }"
      :edit-config="{
        key: 'id',
        trigger: 'manual',
        mode: 'row',
        autoClear: false,
        showStatus: true,
      }"
    >
      <vxe-table-column type="checkbox" width="40"></vxe-table-column>
      <vxe-table-column type="seq" width="40"></vxe-table-column>
      <vxe-table-column field="code" title="编码" :edit-render="{ name: 'input' }">
        <template v-slot:edit="{ row }">
          <a-select
            ref="formSelect"
            v-model="row.code"
            placeholder="请选择工器具"
            :open="false"
            style="width: 100%"
            @focus="openToolSelectModal(row)"
          >
            <a-icon slot="suffixIcon" type="ellipsis" @click="openToolSelectModal(row)" />
          </a-select>
        </template>
      </vxe-table-column>
      <vxe-table-column
        field="name"
        title="工器具名称"
        :edit-render="{ name: 'input' }"
        align="left"
        header-align="center"
      >
        <template v-slot:edit="{ row }">
          {{ row.name }}
        </template>
      </vxe-table-column>
      <vxe-table-column
        field="category1_dictText"
        title="类别"
        :edit-render="{ name: 'input' }"
      >
        <template v-slot:edit="{ row }">
          {{ row.category1_dictText }}
        </template>
      </vxe-table-column>
      <vxe-table-column field="amount" title="所需数量" :edit-render="{ name: 'input' }">
        <template v-slot:edit="{ row }">
          <a-input-number
            v-model="row.amount"
            :defaultValue="1"
            :min="1"
            :max="999999"
            style="width: 100%"
          />
        </template>
      </vxe-table-column>
      <vxe-table-column field="unit" title="单位" :edit-render="{ name: 'input' }">
        <template v-slot:edit="{ row }">
          {{ row.unit }}
        </template>
      </vxe-table-column>
      <vxe-table-column field="remark" title="备注" :edit-render="{ name: 'input' }">
        <template v-slot:edit="{ row }">
          <a-input @change="remarkChangeValue(row)" :maxLength="201" placeholder="请输入备注" v-model="row.remark" />
        </template>
      </vxe-table-column>
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
    <ToolTypeSelect ref="modalForm" :multiple="false" @ok="onSelectTool"></ToolTypeSelect>
    <ToolTypeSelect ref="modalFormMultiple" :multiple="true" :batch="true" @ok="onSelectToolMultiple"></ToolTypeSelect>
  </div>
</template>

<script>
import ToolTypeSelect from "@views/tiros/common/selectModules/ToolTypeSelect";
import { randomUUID } from "@/utils/util";
export default {
  name: "TaskTools",
  components: { ToolTypeSelect },
  props: {
    taskTools: {
      type: Array,
      default: [],
    },
    readOnly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      validRules: {
        code: [{ required: true, message: "请选择工器具" }],
        amount: [{ required: true, message: "数量必须填写" }],
        remark:[
          {max:200,type:'string', message:'备注不能超过200个字符'},
        ]
      },
    };
  },
  methods: {
    remarkChangeValue({remark}){
      if(remark.length>200){
        this.$message.error('备注不能超过200个字符')
      }
    },
    handleAdd() {
      if (this.$refs.listTable.getActiveRecord()) {
        return;
      }
      this.curEditMode = 1
      this.$refs.modalFormMultiple.showModal()
      // let tool = {
      //   id: randomUUID(),
      // };
      // this.$refs.listTable.insertAt(tool, -1).then(({ row }) => {
      //   this.$refs.listTable.setActiveCell(row, "code");
      // });
    },
    handleDel() {
      let m = this.$refs.listTable.getCheckboxRecords();
      if (m.length < 1) {
        this.$message.warn("请选择要删除的数据");
        return;
      }
      this.$confirm({
        content: `确认删除选中的数据？`,
        onOk: () => {
          m.map((item1) => {
            this.taskTools.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.taskTools.splice(index2, 1);
              }
            });
          });
        },
      });
    },
    editRowEvent(row) {
      this.curEditMode = 2;
      this.$refs.listTable.setActiveRow(row);
    },
    delRowEvent(row) {
      this.taskTools.map((item2, index2) => {
        if (row.id === item2.id) {
          this.taskTools.splice(index2, 1);
        }
      });
    },
    saveRowEvent(row) {
      this.$refs.listTable.validate(row, (valid) => {
        if (!valid) {
          if (this.curEditMode === 1) {
            this.taskTools.push(row);
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
        this.$refs.listTable.revertData(row);
      }
      this.curEditMode = 0;
    },
    openToolSelectModal(row) {
      this.curRow = row;
      this.$refs.modalForm.showModal();
      this.$refs.formSelect.blur();
    },
    onSelectToolMultiple(data){
      data.forEach((element, index) => {
          if (this.taskTools.find(e => e.toolTypeId === element.id)) {
            this.$message.warn(`你选择的${element.name}已经在列表中存在`)
            return
          }else{
            let newRow ={
              id: randomUUID(),
              code: element.code,
              toolTypeId: element.toolTypeId || element.id,
              name: element.name,
              kind: element.kind,
              unit: element.unit,
              kind_dictText: element.kind_dictText,
              category1: element.category1,
              category1_dictText: element.category1_dictText,
              amount: element.num,
            }

            if(data.length == 1){
              this.$refs.listTable.insertAt(newRow, -1).then(({ row }) => {
                this.$refs.listTable.setActiveCell(row, 'amount')
              })
            }else{
              this.$refs.listTable.insertAt(newRow, -1)
              this.taskTools.push(newRow)
            }

          }
      })
    },
    onSelectTool(data) {
      if (data && data.length > 0) {
        const tool = data[0];
        let flag = 0;
        this.taskTools.map((f) => {
          if (f.toolTypeId === tool.id) {
            flag = 1;
            return false;
          }
        });

        if (flag === 1) {
          this.$message.error("你选择的工器具已经在列表中存在，请选择其他工器具");
          return;
        }
        this.curRow.code = tool.code;
        this.curRow.toolTypeId = tool.id;
        this.curRow.name = tool.name;
        this.curRow.kind = tool.kind;
        this.curRow.unit = tool.unit;
        this.curRow.kind_dictText = tool.kind_dictText;
        this.curRow.category1 = tool.category1;
        this.curRow.category1_dictText = tool.category1_dictText;
        this.curRow.amount = tool.num;
      } else {
        this.curRow.code = "";
        this.curRow.toolTypeId = "";
        this.curRow.name = "";
        this.curRow.kind = "";
        this.curRow.kind_dictText = "";
        this.curRow.category1 = "";
        this.curRow.category1_dictText = "";
        this.curRow.unit = "";
        this.curRow.amount = 0;
      }
    },
  },
};
</script>

<style></style>
