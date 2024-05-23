<template>
  <div>
    <a-form v-if="!readOnly" layout="inline">
      <a-row :gutter="24">
        <a-col :md="6" :sm="8">
          <a-form-item>
            <a-space>
              <a-button type="dashed" class="primary-color" @click="handleAdd">添加</a-button>
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
      :data="taskBookSteps"
      :keep-source="true"
      :checkbox-config="{ trigger: 'row', highlight: true }"
      :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
    >
      <vxe-table-column type="checkbox" width="40"></vxe-table-column>
      <vxe-table-column width="60">
        <template v-slot="{ row }">
          <a @click.stop="showSopDeatil(row)"> 查看</a>
        </template>
      </vxe-table-column>
      <vxe-table-column field="bookStepNo" title="步骤" width="60px"></vxe-table-column>
      <vxe-table-column field="bootStepTitle" title="步骤标题" header-align="center" align="left"></vxe-table-column>
      <vxe-table-column field="bookName" title="所属作业指导书" max-width="200"></vxe-table-column>
    </vxe-table>
    <!-- <material-list ref="modalForm" :multiple="false" @ok="onSelectMaterial"></material-list> -->
    <SopDetailList ref="modalForm" :multiple="true" @ok="onSelectBookStep"></SopDetailList>
    <WorkSopView ref="workSopViewModal"></WorkSopView>
  </div>
</template>

<script>
import SopDetailList from '@views/tiros/common/selectModules/SopDetailList'
import WorkSopView from '@views/tiros/basic/modules/worksop/WorkSopView'
import { randomUUID } from '@/utils/util'
export default {
  name: 'TaskBookSteps',
  components: { SopDetailList, WorkSopView },
  props: {
    taskBookSteps: {
      type: Array,
      default: [],
    },
    taskId: {
      type: String,
      default: '',
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
        // code: [{ required: true, message: '请选择物料' }],
        // amount: [{ required: true, message: '数量必须填写' }],
      },
    }
  },
  methods: {
    handleAdd() {
      this.$refs.modalForm.showModal()
    },
    handleDel() {
      let m = this.$refs.listTable.getCheckboxRecords()
      if (m.length < 1) {
        this.$message.warn('请选择要删除的数据')
        return
      }
      this.$confirm({
        content: `确认删除选中的数据？`,
        onOk: () => {
          m.map((item1) => {
            this.taskBookSteps.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.taskBookSteps.splice(index2, 1)
              }
            })
          })
        },
      })
    },
    editRowEvent(row) {
      this.curEditMode = 2
      this.$refs.listTable.setActiveRow(row)
    },
    delRowEvent(row) {
      this.taskBookSteps.map((item2, index2) => {
        if (row.id === item2.id) {
          this.taskBookSteps.splice(index2, 1)
        }
      })
    },
    saveRowEvent(row) {
      this.$refs.listTable.validate(row, (valid) => {
        if (!valid) {
          if (this.curEditMode === 1) {
            this.taskBookSteps.push(row)
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
    openBookStepSelectModal(row) {
      this.curRow = row
      this.$refs.modalForm.showModal()
      this.$refs.formSelect.blur()
    },
    onSelectBookStep(data) {
      data.forEach((item) => {
        if (this.taskBookSteps.find((e) => e.bookDetailId === item.id)) {
          this.$message.error(`${item.stepTitle}步骤已经添加了`)
        } else {
          this.taskBookSteps.push({
            bookDetailId: item.id,
            bookStepNo: item.stepNum,
            bootStepContent: item.stepContent,
            bootStepTitle: item.stepTitle,
            bookName: item.bookName,
            id: null,
            taskId: this.taskId,
            techBookId: item.bookId,
          })
        }
      })
      this.$emit('update:taskBookSteps', this.taskBookSteps)
    },
    showSopDeatil(row) {
      this.$refs.workSopViewModal.showSopDetail([row.bookDetailId])
    },
  },
}
</script>

<style>
</style>