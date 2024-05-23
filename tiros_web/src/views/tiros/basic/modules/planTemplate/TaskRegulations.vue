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
      :align="allAlign"
      show-overflow="tooltip"
      :data="taskRegulations"
      :checkbox-config="{ trigger: 'row', highlight: true }"
    >
      <vxe-table-column type="checkbox" width="40"></vxe-table-column>
      <vxe-table-column
        field="title"
        title="名称"
        align="left"
        header-align="center"
        width="25%"
        tree-node
      ></vxe-table-column>
      <vxe-table-column field="type_dictText" title="类型" width="8%"></vxe-table-column>
      <vxe-table-column field="method_dictText" title="维保手段" width="8%"></vxe-table-column>
      <vxe-table-column field="qualityLevel" title="质保等级" width="8%"></vxe-table-column>
      <vxe-table-column field="outsource_dictText" title="是否委外" width="6%"></vxe-table-column>
      <vxe-table-column
        field="safeNotice"
        title="安全提示"
        align="left"
        header-align="center"
        width="25%"
      ></vxe-table-column>
      <vxe-table-column field="remark" title="备注" align="left" header-align="center"></vxe-table-column>
    </vxe-table>
    <regulation-list
      ref="modalForm"
      :multiple="true"
      :check-strictly='false'
      :regu-id="reguId"
      :plan-id='planId'
      :trainTypeId="trainTypeId"
      @ok="addRegulation"
    ></regulation-list>
  </div>
</template>

<script>
import RegulationList from '../../../common/selectModules/RegulationList'
import { randomUUID } from '@/utils/util'
import { getReguDetailOtherInfo } from '@/api/tirosApi'

export default {
  name: 'TaskRegulations',
  components: { RegulationList },
  props: {
    reguId: {
      type: String,
      default: '',
    },
    planId: {
      type: String,
      default: '',
    },
    trainTypeId: {
      type: String,
      default: '',
    },
    taskRegulations: {
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
      allAlign: 'center',
    }
  },
  computed: {
    plan() {
      return this.$store.getters.taskForm
    },
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
          m.map((deleteItem) => {
            this.taskRegulations.map((item, index2) => {
              if (deleteItem.id === item.id) {
                this.taskRegulations.splice(index2, 1)
              }
            })
            //删除后，要刷新任务：对应的物料、工器具、人员
            //将规程的物资，工器具，人员 设置到任务
            if (deleteItem.reguMaterials && deleteItem.reguMaterials.length > 0) {
              this.$emit('removeMaterials', deleteItem.reguMaterials)
            }
            if (deleteItem.reguPersons && deleteItem.reguPersons.length > 0) {
              this.$emit('removePersons', deleteItem.reguPersons)
            }
            if (deleteItem.reguTools && deleteItem.reguTools.length > 0) {
              this.$emit('removeTools', deleteItem.reguTools)
            }
            if (deleteItem.mustReplace === 1) {
              this.$emit('removeMustReplaces', deleteItem)
            }
          })
        },
      })
    },
    addRegulation(data) {
      // console.log(data)
      data.map((item) => {
        // 作业分类不添加，作业项目才添加
        if (item.type === 2){
          // 判断是否存在
          let tempIndex = this.taskRegulations.findIndex((m) => {
            return m.reguDetailId === item.id
          })
          // 不存在
          if (tempIndex < 0) {
            item.reguDetailId = item.id
            item.id = randomUUID()

            this.taskRegulations.push(item)
            // 获取第一条规程的系统
            /*let firstM = this.taskRegulations[0]
            getSysByPart({ id: firstM.assetTypeId }).then((res) => {
              if (res.success) {

              }
            })*/
            this.$emit('change')
            // 是否是必换
            if (item.mustReplace === 1) {
              this.$emit('addMustReplaces', item)
            }

            //将规程的物资，工器具，人员 设置到任务
            getReguDetailOtherInfo({ id: item.reguDetailId }).then((res) => {
              if (res.success) {
                const reguDetail = res.result
                if (reguDetail.reguMaterials && reguDetail.reguMaterials.length > 0) {
                  this.$emit('addMaterials', reguDetail.reguMaterials)
                }
                if (reguDetail.reguPersons && reguDetail.reguPersons.length > 0) {
                  this.$emit('addPersons', reguDetail.reguPersons)
                }
                if (reguDetail.reguTools && reguDetail.reguTools.length > 0) {
                  this.$emit('addTools', reguDetail.reguTools)
                }
                if (reguDetail.techBookDetails && reguDetail.techBookDetails.length > 0) {
                  this.$emit('addTools', reguDetail.techBookDetails)
                }
                if (reguDetail.techBookDetails && reguDetail.techBookDetails.length > 0) {
                  this.$emit('addBookDetails', reguDetail.techBookDetails)
                }
              }
            })
          }
        }
      })
    },
  },
}
</script>

<style>
</style>