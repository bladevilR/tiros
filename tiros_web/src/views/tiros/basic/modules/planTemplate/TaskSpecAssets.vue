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
      ref="specAssetsTable"
      align="center"
      :data="taskSpecAssets"
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
            @dropdownVisibleChange="openSpecAsset(row)"
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
          <a-time-picker v-model="startDate" format="HH:mm" @change="dateChange" style="width: 100%"/>
        </template>
      </vxe-table-column>
      <vxe-table-column field="endTime" title="结束时间"  :edit-render="{ name: 'input' }">
        <template v-slot:edit="{}">
          <!-- <a-date-picker format="YYYY-MM-DD HH" v-model="row.endTime" style="width: 100%" /> -->
          <a-time-picker v-model="endDate" format="HH:mm" @change="dateChange"  style="width: 100%"/>
        </template>
      </vxe-table-column>
      <vxe-table-column field="timeLen" title="需求时长" :edit-render="{ name: 'input' }">
        <template v-slot:edit="{ row }">
          <a-input-number v-model="row.timeLen" style="width: 100%" />
        </template>
      </vxe-table-column>
      <vxe-table-column field="remark" title="备注" :edit-render="{ name: 'input' }" align="left" header-align="center">
        <template v-slot:edit="{ row }">
          <a-input @change="remarkChangeValue(row)" :maxLength="201" placeholder="请输入备注" v-model="row.remark" />
        </template>
      </vxe-table-column>
      <vxe-table-column title="操作" width="150" v-if="!readOnly">
        <template v-slot="{ row }">
          <template v-if="$refs.specAssetsTable.isActiveByRow(row)">
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
    <SpecAssetList ref="specAssetModal" :multiple="false" @ok="onSelectSpecAsset"></SpecAssetList>
  </div>
</template>

<script>
import { randomUUID } from '@/utils/util'
import SpecAssetList from '@views/tiros/common/selectModules/SpecAssetList'

export default {
  name: 'TaskSpecAssets',
  components:{
    SpecAssetList
  },
  props: {
    taskSpecAssets: {
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
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'blur' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'blur' }],
        timeLen: [{ required: true, message: '请选择时长', trigger: 'blur' }],
        remark:[
          {max:200,type:'string', message:'备注不能超过200个字符'},
        ]
      },
      curRow: {},
      startDate: null,
      endDate: null,
    }
  },
  methods: {
    remarkChangeValue({ remark }){
      if(remark.length > 200){
        this.$message.error('备注不能超过200个字符')
      }
    },
    handleAdd() {
      if (this.$refs.specAssetsTable.getActiveRecord()) {
        return
      }
      this.startDate = null
      this.endDate = null
      this.curEditMode = 1
      let device = {
        id: randomUUID(),
      }
      this.$refs.specAssetsTable
        .insertAt(device, -1)
        .then(({ row }) => this.$refs.specAssetsTable.setActiveCell(row, 'assetCode'))
    },
    handleDel() {
      let m = this.$refs.specAssetsTable.getCheckboxRecords()
      if (m.length < 1) {
        this.$message.warn('请选择要删除的数据')
        return
      }
      this.$confirm({
        content: `确认删除选中的数据？`,
        onOk: () => {
          m.map((item1) => {
            this.taskSpecAssets.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.taskSpecAssets.splice(index2, 1)
              }
            })
          })
        },
      })
    },
    editRowEvent(row) {
      this.curRow = row
      this.curEditMode = 2
      this.startDate = this.$moment(row.startTime, 'HH:mm')
      this.endDate = this.$moment(row.endTime, 'HH:mm')
      this.$refs.specAssetsTable.setActiveRow(row)
    },
    delRowEvent(row) {
      this.taskSpecAssets.map((item2, index2) => {
        if (row.id === item2.id) {
          this.taskSpecAssets.splice(index2, 1)
        }
      })
    },
    saveRowEvent(row) {
      row.startTime = this.$moment(this.startDate).format('HH:mm')
      row.endTime = this.$moment(this.endDate).format('HH:mm')
      this.$refs.specAssetsTable.validate(row, (valid) => {
        if (!valid) {
          if (this.$moment(this.endDate).diff(this.$moment(this.startDate), 'minutes') < 0) {
            this.$message.warn('开始时间必须大于结束时间')
            return
          }
          if (this.curEditMode === 1) {
            this.taskSpecAssets.push(row)
          }
          this.$refs.specAssetsTable.clearActived()
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
      this.$refs.specAssetsTable.clearActived()
      row.startTime = null
      row.endTime = null
      if (this.curEditMode === 1) {
        // 新增,点击取消
        this.$refs.specAssetsTable.remove(row)
      } else if (this.curEditMode === 2) {
        // 还原行数据
        this.$refs.specAssetsTable.revertData(row)
      }
      this.curEditMode = 0
    },
    openSpecAsset(row) {
      this.curRow = row
      this.$refs.specAssetModal.showModal()
    },
    onSelectSpecAsset(records) {
      if (records.length) {
        this.curRow.assetCode = records[0].materialCode > 0 ? records[0].materialCode : records[0].assetCode
        this.curRow.assetName = records[0].name
        this.curRow.specAssetId = records[0].id
      } 
    },
    dateChange(){
      if (this.$moment(this.startDate) && this.$moment(this.endDate)) {
        if (this.$moment(this.endDate).diff(this.$moment(this.startDate), 'minutes') <= 0) {
          this.$message.warn('开始时间必须大于结束时间')
          return 
        }
        let m = Number(this.$moment(this.endDate).format('mm')) - Number(this.$moment(this.startDate).format('mm'))
        if (m > 0) {
          m = m / 60
        } else if(m < 0) {
           m = (m + 60) / 60
        } 
        if (m.toFixed(1) * 10 === 0) {
          this.curRow.timeLen = Number(`${Number(this.$moment(this.endDate).format('HH')) - Number(this.$moment(this.startDate).format('HH'))}`)
        }else {
          this.curRow.timeLen = Number(`${Number(this.$moment(this.endDate).format('HH')) - Number(this.$moment(this.startDate).format('HH'))}.${m.toFixed(1) * 10}`)
        }
        
      }
    }
  },
}
</script>

<style scoped>
</style>