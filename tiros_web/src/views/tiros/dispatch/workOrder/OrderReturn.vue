<template>
  <div>
    <a-form layout="inline" v-if="!readOnly">
      <a-row>
        <a-col>
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
    <div style="height: calc(100vh - 220px)">
      <vxe-table
        border
        ref="listTable"
        align="center"
        height="auto"
        :data.sync="tableData"
        :edit-rules="validRules"
        :keep-source="true"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{
          key: 'id',
          trigger: 'manual',
          mode: 'row',
          autoClear: false,
          showStatus: true,
        }"
        @blur="$refs.listTable.clearValidate()"
      >
        <vxe-table-column type="seq" width="40"></vxe-table-column>
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column
          field="code"
          title="物料编码"
          :edit-render="{ name: 'input' }"
        >
          <template v-slot:edit="{ row }">
            <a-select
              ref="formSelect"
              v-model="row.code"
              placeholder="请选择物料"
              :open="false"
              style="width: 100%"
              @dropdownVisibleChange="openMaterialSelectModal(row)"
            >
              <a-icon slot="suffixIcon" type="ellipsis" />
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="name"
          title="物料名称"
          :edit-render="{ name: 'input' }"
          align="left"
          header-align="center"
        >
          <template v-slot:edit="{ row }">
            {{ row.name }}
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="amount"
          title="退料数量"
          :edit-render="{ name: 'input' }"
        >
          <template v-slot:edit="{ row }">
            <a-input-number
              v-model="row.amount"
              :defaultValue="1"
              :min="1"
              :max="10000"
              style="width: 100%"
            />
          </template>
        </vxe-table-column>
        <vxe-table-column field="unit" title="单位" :edit-render="{ name: 'input' }">
          <template v-slot:edit="{ row }">
            {{ row.unit }}
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
            </template>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <ReturnMaterialModal
      ref="returnMaterialModal"
      @ok="onSelectReturnMaterial"
    ></ReturnMaterialModal>
  </div>
</template>

<script>
import ReturnMaterialModal from "@views/tiros/group/modules/ReturnMaterialModal";
import { randomUUID } from '@/utils/util'
export default {
  components: { ReturnMaterialModal },
  props: {
    taskMaterials: {
      type: Array,
      default: () => {
        return [];
      },
    },
    readOnly: {
      type: Boolean,
      default: false,
    },
    workOrderId: {
      type: String,
      default: null,
    },
    selectTask: {
      type: Object,
      default: null,
    },
    tasks: {
      type: Array,
      default: () => {
        return [];
      },
    },
  },
  watch: {
    taskMaterials: {
      handler(val) {
        const list = val;
        this.tableData = list.filter((item) => {
          console.log(item);
          return item.opType == 3; //条件;
        });
      },
      immediate: true,
      deep: true,
    },
  },
  data() {
    return {
      curEditMode:0,
      tableData: [],
      validRules: {
        taskName: [{ required: true, message: "请选工单任务", trigger: "manual" }],
        code: [{ required: true, message: "请选择物料", trigger: "manual" }],
        useCategory_dictText: [
          { required: true, message: "类别必须选择", trigger: "manual" },
        ],
        amount: [{ required: true, message: "数量必须填写", trigger: "manual" }],
        actAmount: [
          { required: this.opType === 2, message: "数量必须填写", trigger: "manual" },
        ],
      },
    };
  },
  methods: {
    handleAdd() {
      if (this.$refs.listTable.getActiveRecord()) {
        return
      }
      this.curEditMode = 1
      this.$refs.returnMaterialModal.showModal(this.workOrderId);
    },

    onSelectReturnMaterial(records) {
      records.forEach((element) => {
        console.log(element)
        if (this.tableData.find((e) => e.materialTypeId === element.materialTypeId)) {
          this.$message.warn(`${element.materialTypeName}物资已存在`);
          return;
        }

        let material = {
          id: randomUUID(),
          opType: 3,
          opType_dictText: "退还物资",
          orderId: this.workOrderId,
          code: element.materialTypeCode,
          materialTypeId: element.materialTypeId,
          name: element.materialTypeName,
          unit: element.materialTypeUnit,
          useCategory: element.useCategory,
          useCategory_dictText: element.useCategory_dictText,
          amount: element.num,
          actAmount: 0,
        };
        if (this.selectTask) {
          Object.assign(material, {
            orderTaskId: this.selectTask.id,
            taskName: this.selectTask.taskName,
          });
        } else {
          if (this.tasks.length) {
            Object.assign(material, {
              orderTaskId: this.tasks[0].id,
              taskName: this.tasks[0].taskName,
            });
          }
        }
        this.tableData.push(material)
        console.log(this.tableData[0], material);
      });
      this.initSubmitData();
    },
    handleDel() {
      let select = this.$refs.listTable.getCheckboxRecords();
      if (select.length <= 0) {
        this.$message.warning("请选择要删除的物料");
        return;
      }
      this.$confirm({
        content: `是否删除选中${select.length}条数据？`,
        okText: "确定",
        cancelText: "取消",
        onOk: () => {
          select.map((item) => {
            this.tableData.map((item1, index) => {
              if (item1.id === item.id) {
                this.tableData.splice(index, 1);
              }
            });
            
          });
          this.initSubmitData();
        },
      });
    },
    initSubmitData(){
      const oldList = this.taskMaterials.filter((item) => {
          return  item.opType != 3 //条件;
      });
      let newList = [...oldList,...this.tableData];
      console.log(newList)
      this.$emit('update:taskMaterials',newList)
    },
    saveRowEvent(row) {
      this.$refs.listTable.validate(row, (valid) => {
        if (!valid) {
          const list = this.taskMaterials.find(e => e.opType!=3 && (e.materialTypeId === row.materialTypeId));
          if (list && list.amount < row.amount) {
            this.$message.warn('退料数量不能大于领料数量')
            return
          }

          if (row.actAmount > row.amount) {
            this.$message.error('实际消耗不能大于所需数量')
            return
          }
          if (this.curEditMode === 1) {
            this.tableData.push(row)
          }
          this.$refs.listTable.clearActived()
          this.curEditMode = 0
        } else {
          for (const validKey in valid) {
            let vals = valid[validKey]
            vals.forEach((item) => {
              if (item.rule) {
                this.$message.error(item.rule.message)
              }
            })
          }
        }
      })
    },
    cancelRowEvent(row) {
      this.$refs.listTable.clearActived()
      if (this.curEditMode === 1) {
        // 新增,点击取消
        this.$refs.listTable.remove(row)
      } else if (this.curEditMode === 2) {
        // 还原行数据
        this.$refs.listTable.revertData(row)
      }
      this.curEditMode = 0
    },
    editRowEvent(row) {
      this.curEditMode = 2
      this.$refs.listTable.setActiveRow(row)
    },
  },
};
</script>

<style lang="less" scoped></style>
