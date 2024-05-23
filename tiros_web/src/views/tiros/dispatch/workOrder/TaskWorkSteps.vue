<template>
  <div>
    <!-- 表单添加 -->
    <div v-if="!readOnly" style="height: calc(100vh - 160px)">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="24" :sm="8">
            <a-form-item>
              <a-space>
                <a-button type="dashed" class="primary-color" @click="handleAdd"
                  >添加</a-button
                >
                <a-button v-if="tableData.length>0" type="dashed" class="primary-color" @click="previewHander"
                  >预览</a-button
                >
                <a-button type="dashed" @click="handleDel">删除</a-button>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <vxe-table
        border
        resizable
        show-overflow
        keep-source
        align="center"
        ref="xTable"
        max-height="auto"
        style="height: calc(100vh - 160px)"
        :data.sync="tableData"
        :checkbox-config="{ highlight: true, range: true }"
        :edit-rules="validRules"
        :edit-config="{
          trigger: 'manual',
          mode: 'row',
          autoClear: false,
          showStatus: true,
        }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column
          v-if="tasks && tasks.length>1"
          field="taskName"
          title="工单任务"
          :edit-render="{ name: 'input' }"
          align="left"
          header-align="center"
        >
          <template #default="{ row }">
            <span>{{ getTaskName(row.taskId) }}</span>
          </template>
          <template #edit="{ row }">
            <a-select v-model="row.taskId" style="width: 100%">
              <a-select-option
                v-for="(task, index) in tasks"
                :value="task.id"
                :key="index"
              >
                {{ task.taskName }}
              </a-select-option>
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="bookStepNo"
          title="步骤"
          :edit-render="{ name: 'input' }"
          width="120"
        >
          <template #edit="{ row }">
            <a-input disabled placeholder="请输入步骤序号" v-model="row.bookStepNo" />
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="bootStepTitle"
          title="步骤名称"
          align="left"
          :edit-render="{ name: 'input' }"
        >
          <template #default="{ row }">
            <a @click.stop="showSopDeatil(row.bookDetailId)"> {{ row.bootStepTitle }}</a>
          </template>
          <template v-slot:edit="{ row }">
            <a-select
              ref="trainAssetSelect"
              v-model="row.bootStepTitle"
              placeholder="请选择作业指导书"
              :open="false"
              style="width: 100%"
              @focus="openAssetSelectModal(row)"
            >
              <a-icon
                slot="suffixIcon"
                type="ellipsis"
                @click="openAssetSelectModal(row)"
              />
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="bookName"
          title="所属作业指导书"
          align="left"
          :edit-render="{ name: 'input' }"
        >
          <template #edit="{ row }">
            <a-input disabled placeholder="请输入步骤名称" v-model="row.bookName" />
          </template>
        </vxe-table-column>
        <vxe-table-column title="操作" width="160">
          <template #default="{ row }">
            <template v-if="$refs.xTable.isActiveByRow(row)">
              <a-space>
                <a-button type="dashed" size="small" @click="saveRowEvent(row)"
                  >保存</a-button
                >
                <a-button type="dashed" size="small" @click="cancelRowEvent(row)"
                  >取消</a-button
                >
              </a-space>
            </template>
            <template v-else>
              <a-button type="dashed" size="small" @click="editRowEvent(row)"
                >编辑</a-button
              >
            </template>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <!-- 查看表单 -->
    <div style="height: calc(100vh - 160px)" v-else>
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="24" :sm="8">
            <a-form-item>
              <a-space>
                <a-button type="dashed" class="primary-color" @click="previewHander"
                  >预览</a-button
                >
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <vxe-table
        border
        resizable
        show-overflow
        keep-source
        align="center"
        ref="xTable"
        max-height="auto"
        style="height: calc(100vh - 160px)"
        :data.sync="tableData"
        :checkbox-config="{ highlight: true, range: true }"
      >
        <vxe-table-column type="seq" width="40"></vxe-table-column>
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column
          field="taskName"
          title="工单任务"
          :edit-render="{ name: 'input' }"
        >
          <template #default="{ row }">
            <span>{{ getTaskName(row.taskId) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="bookStepNo"
          title="步骤"
          :edit-render="{ name: 'input' }"
          width="120"
        >
        </vxe-table-column>
        <vxe-table-column
          field="bootStepTitle"
          title="步骤名称"
          align="left"
          :edit-render="{ name: 'input' }"
        >
          <template #default="{ row }">
            <a @click.stop="showSopDeatil(row.bookDetailId)"> {{ row.bootStepTitle }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="bookName"
          title="所属作业指导书"
          :edit-render="{ name: 'input' }"
        >
        </vxe-table-column>
      </vxe-table>
    </div>
    <SopDetailList
      ref="modalForm"
      :multiple="false"
      @ok="onSelectBookStep"
    ></SopDetailList>
    <SopDetailList
      ref="modalFormMultiple"
      :multiple="true"
      @ok="onSelectBookStepMultiple"
    ></SopDetailList>
    <WorkSopView ref="workSopViewModal"></WorkSopView>
  </div>
</template>

<script>
import SopDetailList from "@views/tiros/common/selectModules/SopDetailList";
import WorkSopView from "@views/tiros/basic/modules/worksop/WorkSopView";
import { randomUUID } from "@/utils/util";

export default {
  props: {
    bookSteps: {
      type: Array,
      default: [],
    },
    businessKey: {
      type: String,
      default: null,
    },
    tasks: {
      type: Array,
      default: () => {
        return [];
      },
    },
    readOnly: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    bookSteps: {
      handler(newVal) {
        console.log(this.bookSteps, 222);
        this.tableData = newVal;
      },
      immediate: true,
      deep: true,
    },
  },
  components: { SopDetailList, WorkSopView },
  data() {
    return {
      isEdit: false,
      tableData: [],
      rowData: null,
      validRules: {
        bootStepTitle: [{ required: true, message: "请填写步骤名称", trigger: "manual" }],
        bookName: [{ required: true, message: "请选择作业指导书", trigger: "manual" }],
      },
    };
  },
  methods: {
    previewHander() {
      let arr =
        this.$refs.xTable.getCheckboxRecords().length <= 0
          ? this.tableData
          : this.$refs.xTable.getCheckboxRecords();
      if (arr.length <= 0) {
        this.$message.warning("没有可预览的作业指导书");
        return;
      }
      let ids = arr
        .map((item) => {
          return item.bookDetailId; //条件;
        })
        .join(",");
      this.showSopDeatil(ids);
    },
    getTaskName(id) {
      let taskName;
      for (let i = 0, len = this.tasks.length; i < len; i++) {
        if (this.tasks[i].id === id) {
          taskName = this.tasks[i].taskName;
        }
      }
      return taskName;
    },
    onSelectBookStep(data) {
      if (data.length <= 0) {
        return;
      }
      const item = data[0];
      console.log(item, 333);
      if (this.tableData.find((e) => e.bookDetailId === item.id)) {
        this.$message.error(`${item.stepTitle}步骤已经添加了`);
      } else {
        this.rowData.bookDetailId = item.id;
        this.rowData.bookName = item.bookName;
        this.rowData.bookStepNo = item.stepNum;
        this.rowData.bootStepContent = item.stepContent;
        // if (!this.rowData.bootStepTitle) {
        this.rowData.bootStepTitle = item.stepTitle;
        // }
        this.rowData.techBookId = item.bookId;
      }
    },
    showSopDeatil(ids) {
      this.$refs.workSopViewModal.showSopDetail(ids.split(","));
    },
    saveRowEvent(row) {
      const $table = this.$refs.xTable;
      $table.validate(row, (valid) => {
        if (!valid) {
          $table.clearActived();
          if (!this.isEdit) {
            this.tableData.push(row);
          }
          this.$emit("update:bookSteps", this.tableData);
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
    editRowEvent(row) {
      this.isEdit = true;
      this.$refs.xTable.setActiveCell(row, "bootStepTitle");
    },
    async cancelRowEvent(row) {
      const $table = this.$refs.xTable;
      await $table.clearActived();
      if (this.isEdit) {
        // 还原行数据
        $table.revertData(row);
      } else {
        $table.remove(row);
      }
    },
    async handleAdd() {
      if (this.$refs.xTable.getActiveRecord()) {
        this.$message.warning("请先保存数据");
        return;
      }
      this.isEdit = false;
      this.$refs.modalFormMultiple.showModal();
    },
    onSelectBookStepMultiple(data) {
      const $table = this.$refs.xTable;
      const tasks = this.tasks.length > 0 ? this.tasks[0] : false;
      data.forEach((item, index, arr) => {
        // 判断选择的表单是否已经存在
        let flag = 0;
        for (let i = 0, len = this.tableData.length; i < len; i++) {
          const f = this.tableData[i];
          if (f.bookDetailId == item.id) {
            flag = 1;
            break;
          }
        }
        console.log(flag)
        if (flag === 0) {
          const record = {
            id: randomUUID(),
            belongOrderTask: 1,
            workOrderId: this.businessKey,
            taskName: tasks ? tasks.taskName : "",
            taskId: tasks ? tasks.id : "",
            bookDetailId: item.id,
            bootStepContent: item.stepContent,
            bookStepNo: item.stepNum,
            bootStepTitle: item.stepTitle,
            bookName: item.bookName,
            techBookId: item.bookId,
          };

          // 添加时，不管是一条，还是多条，都直接添加，不需要点击保存
          this.$refs.xTable.insertAt(record, -1);
          this.tableData.push(record);

          /*if (data.length === 1) {
            this.$refs.xTable.insertAt(record, -1).then(({ row }) => {
              this.$refs.xTable.setActiveCell(row, "bootStepTitle");
            });
          } else {
            this.$refs.xTable.insertAt(record, -1);
            this.tableData.push(record);
          }*/
        }
      });
      console.log(this.tableData);
    },
    openAssetSelectModal(row) {
      this.rowData = row;
      this.$refs.modalForm.showModal();
      this.$refs.trainAssetSelect.blur();
    },
    handleDel() {
      let m = this.$refs.xTable.getCheckboxRecords();
      if (m.length > 0) {
        this.$confirm({
          content: `是否删除选中${m.length}条数据？`,
          okText: "确定",
          cancelText: "取消",
          onOk: () => {
            m.map((item1) => {
              this.tableData.map((item2, index2) => {
                if (item1.id === item2.id) {
                  this.tableData.splice(index2, 1)
                }
              })
            })
            this.$emit("update:bookSteps", this.tableData);
          },
        });
      } else {
        this.$message.error("尚未选中任何数据!");
      }
    },
  },
};
</script>

<style lang="less" scoped></style>
