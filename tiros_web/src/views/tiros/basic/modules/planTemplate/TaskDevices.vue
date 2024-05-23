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
      keep-source
      ref="devicesTable"
      align="center"
      :data="taskDevices"
      :checkbox-config="{ trigger: 'row', highlight: true }"
      :edit-rules="validRules"
      :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
    >
      <vxe-table-column type="checkbox" width="40"></vxe-table-column>
      <vxe-table-column
        field="assetCode"
        title="设备编码"
        :edit-render="{ name: 'input' }"
      >
        <template v-slot:edit="{ row }">
          <a-select
            v-model="row.assetCode"
            placeholder="请选择"
            :open="false"
            style="width: 100%"
            @dropdownVisibleChange="openMaterial(row)"
          >
            <a-icon slot="suffixIcon" type="ellipsis" />
          </a-select>
        </template>
      </vxe-table-column>
      <vxe-table-column
        field="assetName"
        title="设备名称"
        width="300"
      ></vxe-table-column>
      <vxe-table-column field="startTime" title="开始时间" :edit-render="{ name: 'input' }">
        <template v-slot:edit="{}">
          <!-- <a-date-picker v-model="row.startTime" format="YYYY-MM-DD HH"  style="width: 100%" /> -->
          <a-time-picker v-model="startDate" format="HH:mm" />
        </template>
      </vxe-table-column>
      <vxe-table-column field="endTime" title="结束时间" :edit-render="{ name: 'input' }">
        <template v-slot:edit="{}">
          <!-- <a-date-picker format="YYYY-MM-DD HH" v-model="row.endTime" style="width: 100%" /> -->
          <a-time-picker v-model="endDate" format="HH:mm" />
        </template>
      </vxe-table-column>
      <vxe-table-column field="timeLen" title="需求时长" :edit-render="{ name: 'input' }">
        <template v-slot:edit="{ row }">
          <a-input-number v-model="row.timeLen" :defaultValue="1" :min="1" :max="10000000" style="width: 100%" />
        </template>
      </vxe-table-column>
      <vxe-table-column field="remark" title="备注" :edit-render="{ name: 'input' }" align="left" header-align="center">
        <template v-slot:edit="{ row }">
          <a-input placeholder="请输入备注" v-model="row.remark" />
        </template>
      </vxe-table-column>
      <vxe-table-column title="操作" width="150" v-if="!readOnly">
        <template v-slot="{ row }">
          <template v-if="$refs.devicesTable.isActiveByRow(row)">
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
    <material-list ref="materialForm" :multiple="false" @ok="onSelectMaterial"></material-list>
  </div>
</template>

<script>
import { randomUUID } from '@/utils/util'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'

export default {
  name: 'TaskDevices',
  components:{
    MaterialList
  },
  props: {
    taskDevices: {
      type: Array,
      default: () => [],
    },
    readOnly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      validRules: {
        assetCode: [{ required: true, message: '请选择', trigger: 'blur' }],
      },
      curRow: {},
      startDate: null,
      endDate: null,
    }
  },
  methods: {
    handleAdd() {
      if (this.$refs.devicesTable.getActiveRecord()) {
        return
      }
      this.startDate = null
      this.endDate = null
      this.curEditMode = 1
      let device = {
        id: randomUUID(),
      }
      this.$refs.devicesTable
        .insertAt(device, -1)
        .then(({ row }) => this.$refs.devicesTable.setActiveCell(row, 'assetCode'))
    },
    handleDel() {
      let m = this.$refs.devicesTable.getCheckboxRecords()
      if (m.length < 1) {
        this.$message.warn('请选择要删除的数据')
        return
      }
      this.$confirm({
        content: `确认删除选中的数据？`,
        onOk: () => {
          m.map((item1) => {
            this.taskDevices.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.taskDevices.splice(index2, 1)
              }
            })
          })
        },
      })
    },
    editRowEvent(row) {
      this.curEditMode = 2
      this.startDate = this.$moment(row.startTime, 'HH:mm')
      this.endDate = this.$moment(row.endTime, 'HH:mm')
      this.$refs.devicesTable.setActiveRow(row)
    },
    delRowEvent(row) {
      this.taskDevices.map((item2, index2) => {
        if (row.id === item2.id) {
          this.taskDevices.splice(index2, 1)
        }
      })
    },
    saveRowEvent(row) {
      this.$refs.devicesTable.validate(row, (valid) => {
        if (!valid) {
          row.startTime = this.$moment(this.startDate).format('HH:mm')
          row.endTime = this.$moment(this.endDate).format('HH:mm')
          if (this.curEditMode === 1) {
            this.taskDevices.push(row)
          }
          this.$refs.devicesTable.clearActived()
          this.curEditMode = 0
          this.startDate = null
          this.endDate = null
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
      this.$refs.devicesTable.clearActived()
      row.startTime = null
      row.endTime = null
      if (this.curEditMode === 1) {
        // 新增,点击取消
        this.$refs.devicesTable.remove(row)
      } else if (this.curEditMode === 2) {
        // 还原行数据
        this.$refs.devicesTable.revertData(row)
      }
      this.curEditMode = 0
    },
    openMaterial(row) {
      this.curRow = row
      this.$refs.materialForm.showModal()
    },
    onSelectMaterial(records) {
      if (records.length) {
        this.curRow.assetCode = '010011080006'
        this.curRow.assetName = '测试'
        this.curRow.specAssetId = '3b98cd5515af4a54cd1322f19692c63d'
      } 
    },
  },
}
</script>

<style scoped>
</style>