<template>
<div>
  <a-form layout="inline">
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
    keep-source
    ref="personsTable"
    align="center"
    :data.sync="taskPersons"
    :checkbox-config="{trigger: 'row', highlight: true }"
    :edit-rules="validRules"
    :edit-config="{key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true}"
  >
    <vxe-table-column type="checkbox" width="40"></vxe-table-column>
    <vxe-table-column field="amount" title="所需人数" width="150" :edit-render="{name: 'input'}" align="right" header-align="center">
      <template v-slot:edit="{ row }">
        <a-input-number v-model="row.amount" :defaultValue="1" :min="1" :max="100" style="width: 100%" />
      </template>
    </vxe-table-column>
    <vxe-table-column field="requirePostion" title="岗位要求" width="300" :edit-render="{name: 'input'}" align="left" header-align="center">
      <template v-slot:edit="{row}">
        <a-input placeholder="请输入岗位需求" v-model="row.requirePostion" />
      </template>
    </vxe-table-column>
    <vxe-table-column field="requireTech" title="技能要求" width="300" :edit-render="{name: 'input'}" align="left" header-align="center">
      <template v-slot:edit="{row}">
        <a-input placeholder="请输入技能要求" v-model="row.requireTech" />
      </template>
    </vxe-table-column>
    <vxe-table-column field="requireCertificate" title="证书要求" width="300" :edit-render="{name: 'input'}" align="left" header-align="center">
      <template v-slot:edit="{row}">
        <a-input placeholder="请输入证书要求" v-model="row.requireCertificate" />
      </template>
    </vxe-table-column>
    <vxe-table-column field="remark" title="备注" :edit-render="{name: 'input'}" align="left" header-align="center">
      <template v-slot:edit="{row}">
        <a-input placeholder="请输入备注" v-model="row.remark" />
      </template>
    </vxe-table-column>
    <vxe-table-column title="操作" width="150">
      <template v-slot="{ row }">
        <template v-if="$refs.personsTable.isActiveByRow(row)">
          <a-space>
            <a-button type="dashed" size="small" @click.stop="saveRowEvent(row)">保存</a-button>
            <a-button type="dashed" size="small" @click.stop="cancelRowEvent(row)">取消</a-button>
          </a-space>
        </template>
        <template v-else>
          <a-button type="dashed" size="small" @click.stop="editRowEvent(row)">编辑</a-button>
          <!-- <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
        </template>
      </template>
    </vxe-table-column>
  </vxe-table>
</div>
</template>

<script>
import { randomUUID } from '@/utils/util'

export default {
  name: 'VxeTableDemo',
  data () {
    return {
      taskPersons: [],
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      validRules: {
        amount: [
          { required: true, message: '数量必须填写' }
        ],
        requireTech: [
          { required: true, message: '技术要求必须填写' }
        ]
      }
    }
  },
  methods: {
    handleAdd () {
      if (this.$refs.personsTable.getActiveRecord()) {
        return
      }
      this.curEditMode = 1
      let person = {
        id: randomUUID()
      }
      this.$refs.personsTable.insertAt(person, -1)
        .then(({ row }) => this.$refs.personsTable.setActiveCell(row, 'amount'))
    },
    handleDel () {
      let m = this.$refs.personsTable.getCheckboxRecords()
      if (m.length < 1) {
        this.$message.warn('请选择要删除的数据')
        return
      }
      this.$confirm({
        content: `确认删除选中的数据？`,
        onOk: () => {
          m.map((item1) => {
            this.taskPersons.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.taskPersons.splice(index2, 1)
              }
            })
          })
        }
      })
    },
    editRowEvent (row) {
      this.curEditMode = 2
      this.$refs.personsTable.setActiveRow(row)
    },
    delRowEvent (row) {
      this.taskPersons.map((item2, index2) => {
        if (row.id === item2.id) {
          this.taskPersons.splice(index2, 1)
        }
      })
    },
    saveRowEvent (row) {
      console.log('save row:', row)
      this.$refs.personsTable.validate(row, valid => {
        console.log('valid:', valid)
        if (!valid) {
          if(this.curEditMode === 1){
            this.taskPersons.push(row)
          }
          this.$refs.personsTable.clearActived()
          this.curEditMode = 0
        }
      })
    },
    cancelRowEvent (row) {
      this.$refs.personsTable.clearActived()
      if (this.curEditMode === 1) {
        // 新增,点击取消
        this.$refs.personsTable.remove(row)

      }
      this.curEditMode = 0
    }
  }
}
</script>

<style scoped>

</style>